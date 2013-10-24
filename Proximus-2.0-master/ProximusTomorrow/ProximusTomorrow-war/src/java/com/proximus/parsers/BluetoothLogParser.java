package com.proximus.parsers;

import com.proximus.data.BluetoothDwell;
import com.proximus.data.BluetoothSend;
import com.proximus.data.Campaign;
import com.proximus.data.Device;
import com.proximus.manager.BluetoothReportManagerLocal;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
public class BluetoothLogParser {

    private static final Logger logger = Logger.getLogger(BluetoothLogParser.class.getName());
    private static final long MAX_TIME_MISSING_MS = 60 * 1000;
    
    public static final String BLUEGIGA_LOG = "bluegiga";
    public static final String Dreamplug_LOG = "dreamplug";

    static final String bgSentEntryPattern = "^([\\d\\/.]+) \\S+: Sent (\\S+) to (\\S+): (\\S+)";
    static final Pattern bgSentEntry = Pattern.compile(bgSentEntryPattern);
    static final String bgDeviceFoundPattern = "^([\\d\\/.]+) \\S+: Device (\\S+):\\s+(\\S+) \\(RSSI ([\\d-]+)[\\),].*";
    static final Pattern bgDeviceFound = Pattern.compile(bgDeviceFoundPattern);
    static final String bgHashPattern = "^([\\d\\/.]+) \\S+: Hash:(.*)";
    static final Pattern bgHashEntry = Pattern.compile(bgHashPattern);
    static final String dreamplugSentEntryPattern = "^([^,]*),([^,]*),([^,]*)[,]*([^,]*)[,]*([^,]*)[,]*([^,]*)[,]*([^,]*)";
    static final Pattern dreamplugSentEntry = Pattern.compile(dreamplugSentEntryPattern);
    static final String dreamplugDwellEntryPattern = "^([^,]*),([^,]*),([^,]*)[,]*([^,]*)";
    static final Pattern dreamplugDwellEntry = Pattern.compile(dreamplugDwellEntryPattern);

    public boolean parse(File f, DeviceManagerLocal deviceMgr, CampaignManagerLocal campMgr, BluetoothReportManagerLocal btReportMgr) {
        // this file could be an ObexSender-created log or a Dreamplug-created log
        // example bluetoothTransfer_platform_F0AD4E00CB4F_6_2012-02-23.15-16-08.txt

        String filename = f.getName();
        if (filename.isEmpty() || !filename.startsWith("bluetooth")) {
            return false;
        }

        String[] split = filename.split("_");
        if (split.length != 5) {
            logger.warn("File " + filename + " doesn't have a valid filename");
            return false;
        }
        String platform = split[1];
        String macAddr = split[2];
        String campId = split[3];

        if (campId.equalsIgnoreCase("-1") || campId.equalsIgnoreCase("NoActive")) {
            logger.warn("File " + filename + " is not associated with a campaign");
            return false;
        }

        Device d = deviceMgr.getDeviceByMacAddress(macAddr);
        Campaign camp = campMgr.find(Long.parseLong(campId));

        if (platform.equalsIgnoreCase(BLUEGIGA_LOG)) {
            return parseBluegigaTransfer(f, d, camp, btReportMgr);
        } else if (platform.equalsIgnoreCase(Dreamplug_LOG)) {
            return parseDreamplug(f, d, camp, btReportMgr);
        }
        return false;
    }

    public static boolean parseDreamplug(File f, Device d, Campaign c, BluetoothReportManagerLocal btMgr) {

        if (f.getName().contains("Transfer")) {
            return parseDreamplugTransfer(f, d, c, btMgr);
        } else if (f.getName().contains("Dwell")) {
            return parseDreamplugDwell(f, d, c, btMgr);
        }

        return true;
    }

    public static boolean parseDreamplugTransfer(File f, Device d, Campaign c, BluetoothReportManagerLocal btMgr) {
        // CANCELLED,1330453817633,0CDDEFDDDF62,Nokia N900,offer-which-wich.jpg
        // ACTION,time,MAC,Friendly Name,filename
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(f));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() < 8) {
                    continue;
                }
                try {
                    Matcher matcher = dreamplugSentEntry.matcher(line);
                    if (matcher.matches()) {

                        // ACTION,time,MAC,Friendly Name,filename
                        String code = matcher.group(1);
                        String eventDateMs = matcher.group(2);
                        String macAddress = matcher.group(3).replace(":", "").toUpperCase();
                        String friendlyName = matcher.group(4);
                        String fileSent = matcher.group(5);
                        String transferTime = matcher.group(6);
                        String signalStrength = matcher.group(7);

                        BluetoothSend btd = new BluetoothSend();
                        btd.setDevice(d);
                        btd.setCompany(d.getCompany());
                        btd.setCampaign(c);
                        btd.setEventDate(new Date(Long.parseLong(eventDateMs)));
                        btd.setFile(fileSent);
                        btd.setFriendlyName(friendlyName);
                        btd.setMacAddress(macAddress);
                        btd.setSendStatus(translateDreamplugCode(code));
                        if (transferTime != null && transferTime.length() > 0) {
                            btd.setTransferTimeMS(Long.parseLong(transferTime));
                        }
                        if (signalStrength != null && signalStrength.length() > 0) {
                            btd.setSignalStrength(Integer.parseInt(signalStrength));
                        }

                        if (btd.getSendStatus() == BluetoothSend.STATUS_SENT
                                || btd.getSendStatus() == BluetoothSend.STATUS_ACCEPTED) {
                            btd.setAcceptStatus(1);
                        } else {
                            btd.setAcceptStatus(0);
                        }

                        btMgr.createBluetoothSend(btd);
                        continue;
                    }
                } catch (Exception err) {
                    logger.fatal(err);
                }

                logger.debug("Not Processed: " + line);
            }

        } catch (Exception ex) {
            logger.fatal(ex);
        }
        return true;
    }

    public static boolean parseDreamplugDwell(File f, Device d, Campaign c, BluetoothReportManagerLocal btMgr) {
        //  00225F961DCA,MWATERS,1330455395488,1330455405905
        //  MAC,FRIENDLY_NAME,STARTMS,ENDMS
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(f));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() < 8) {
                    continue;
                }
                Matcher matcher = dreamplugDwellEntry.matcher(line);
                if (matcher.matches()) {
                    // MAC,FRIENDLY_NAME,STARTMS,ENDMS
                    String macAddress = matcher.group(1).replace(":", "").toUpperCase();
                    String friendlyName = matcher.group(2);
                    String startTimeMs = matcher.group(3);
                    String endTimeMs = matcher.group(4);

                    Date startTime = new Date(Long.parseLong(startTimeMs));
                    Date endTime = new Date(Long.parseLong(endTimeMs));
                    long dwellTime = endTime.getTime() - startTime.getTime();

                    BluetoothDwell btd = new BluetoothDwell();
                    btd.setDevice(d);
                    btd.setCompany(d.getCompany());
                    btd.setCampaign(c);
                    btd.setMacAddress(macAddress);
                    btd.setFriendlyName(friendlyName);
                    btd.setEventDate(startTime);
                    btd.setDwellTimeMS(dwellTime);

                    btMgr.createBluetoothDwell(btd);
                    continue;
                }

                logger.debug("Not processed: " + line);
            }

        } catch (Exception ex) {
            logger.fatal(ex);
        }

        return true;
    }

    private static void addDwellEntry(HashMap<String, BluetoothDwell> userTracker, String macAddress, Date entryDate, Device d, Campaign c) {
        BluetoothDwell de;
        if (userTracker.containsKey(macAddress)) {
            de = userTracker.get(macAddress);
        } else {
            de = new BluetoothDwell();
        }

        de.setDevice(d);
        de.setCampaign(c);
        de.setCompany(d.getCompany());
        de.setMacAddress(macAddress);
        de.addSeen(entryDate);
        userTracker.put(macAddress, de);
    }

    public static boolean parseBluegigaTransfer(File f, Device d, Campaign c, BluetoothReportManagerLocal btMgr) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd/HHmmss");

        // keep a list of all MAC addresses and every time they were seen
        HashMap<String, BluetoothDwell> phoneTracker = new HashMap<String, BluetoothDwell>();

        // keep a hash of friendly names when we see them, so we can log them on send/dwell
        HashMap<String, String> friendlyNames = new HashMap<String, String>();

        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(f));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() < 27) {
                    continue;
                }

                Matcher matcher = bgSentEntry.matcher(line);
                if (matcher.matches()) {

                    String dateStr = matcher.group(1);
                    Date entryDate = dateFormat.parse(dateStr);

                    String fileSent = matcher.group(2);
                    String macAddress = matcher.group(3).replace(":", "").toUpperCase();
                    String status = matcher.group(4);

                    BluetoothSend btd = new BluetoothSend();
                    btd.setDevice(d);
                    btd.setCompany(d.getCompany());
                    btd.setCampaign(c);
                    btd.setEventDate(entryDate);
                    btd.setFile(fileSent);
                    btd.setMacAddress(macAddress);
                    btd.setSendStatus(translateBluegigaCode(status));

                    if (friendlyNames.containsKey(macAddress)) {
                        btd.setFriendlyName(friendlyNames.get(macAddress));
                    }

                    if (btd.getSendStatus() == BluetoothSend.STATUS_SENT
                            || btd.getSendStatus() == BluetoothSend.STATUS_SENT_MORE) {
                        btd.setAcceptStatus(1);
                    } else {
                        btd.setAcceptStatus(0);
                    }

                    btMgr.createBluetoothSend(btd);

                    addDwellEntry(phoneTracker, macAddress, entryDate, d, c);

                    continue;
                }
                matcher = bgDeviceFound.matcher(line);
                if (matcher.matches()) {
                    String dateStr = matcher.group(1);
                    Date entryDate = dateFormat.parse(dateStr);

                    String action = matcher.group(2);
                    String macAddress = matcher.group(3).replace(":", "").toUpperCase();
                    // Integer signalStr = Integer.parseInt(matcher.group(4));

                    addDwellEntry(phoneTracker, macAddress, entryDate, d, c);

                    continue;
                }

                matcher = bgHashEntry.matcher(line);
                if (matcher.matches()) {
                    String dateStr = matcher.group(1);
                    Date entryDate = dateFormat.parse(dateStr);
                    String[] body = matcher.group(2).split("@");
                    String friendlyName = body[body.length - 1];
                    friendlyName = friendlyName.substring(1, friendlyName.length() - 1); // remove the quotes around it
                    String macAddress = body[body.length - 5].replace(":", "").toUpperCase();

                    if (!friendlyNames.containsKey(macAddress)) {
                        friendlyNames.put(macAddress, friendlyName);

                    }

                    addDwellEntry(phoneTracker, macAddress, entryDate, d, c);

                    continue;
                }

                logger.debug("Did not parse line: " + line);
            }

        } catch (Exception ex) {
            logger.fatal(ex);
        }

        for (BluetoothDwell entry : phoneTracker.values()) {
            List<BluetoothDwell> dwellEntries = getDwellEntriesForPhone(entry.getMacAddress(), entry.getSeen());

            for (BluetoothDwell bluetoothDwell : dwellEntries) {
                bluetoothDwell.setMacAddress(entry.getMacAddress());
                bluetoothDwell.setCampaign(entry.getCampaign());
                bluetoothDwell.setCompany(entry.getCompany());
                bluetoothDwell.setDevice(entry.getDevice());
                bluetoothDwell.setFriendlyName(friendlyNames.get(entry.getMacAddress()));
                btMgr.createBluetoothDwell(bluetoothDwell);
            }
        }

        return true;
    }

    /**
     * Calculate dwell entries based on a raw list of times seen
     * @param macAddress mac address of the phone
     * @param timesSeen a list of Dates that the phone was seen
     * @return list of dwell entries for this phone
     */
    public static List<BluetoothDwell> getDwellEntriesForPhone(String macAddress, List<Date> timesSeen) {
        List<BluetoothDwell> results = new ArrayList<BluetoothDwell>();

        Date firstEntry = null;
        Date previousEntry = null;

        for (Date currentEntry : timesSeen) {
            if (firstEntry == null) {
                firstEntry = currentEntry;
                previousEntry = firstEntry;
            } else {
                long timeSinceLastEntry = currentEntry.getTime() - previousEntry.getTime();

                if (timeSinceLastEntry > MAX_TIME_MISSING_MS) {
                    long dwellTimeMilliseconds = previousEntry.getTime() - firstEntry.getTime();
                    logger.debug("DWELL TIME for " + macAddress + ": " + BluetoothDwell.convertTime(dwellTimeMilliseconds));
                    BluetoothDwell entry = new BluetoothDwell();
                    entry.setMacAddress(macAddress);
                    entry.setDwellTimeMS(dwellTimeMilliseconds);
                    entry.setEventDate(firstEntry);
                    results.add(entry);

                    // start a new one
                    firstEntry = currentEntry;
                }

                // set this entry to previous so that we can compare
                previousEntry = currentEntry;
            }
        }
        long dwellTimeMilliseconds = previousEntry.getTime() - firstEntry.getTime();
        if (dwellTimeMilliseconds > 0) {
            logger.debug("LAST DWELL TIME for: " + macAddress + BluetoothDwell.convertTime(dwellTimeMilliseconds));
            BluetoothDwell entry = new BluetoothDwell();
            entry.setMacAddress(macAddress);
            entry.setDwellTimeMS(dwellTimeMilliseconds);
            entry.setEventDate(firstEntry);
            results.add(entry);
        }

        return results;
    }

    public static Integer translateDreamplugCode(String code) {
        if (code.equalsIgnoreCase("ACCEPTED")) {
            return BluetoothSend.STATUS_ACCEPTED;
        }
        if (code.equalsIgnoreCase("DELIVERED")) {
            return BluetoothSend.STATUS_SENT;
        }
        if (code.equalsIgnoreCase("CANCELLED")) {
            return BluetoothSend.STATUS_REJECTED;
        }
        if (code.equalsIgnoreCase("REJECTED")) {
            return BluetoothSend.STATUS_REJECTED;
        }
        if (code.equalsIgnoreCase("NO-OBEX")) {
            return BluetoothSend.STATUS_UNSUPPORTED;
        }

        return BluetoothSend.STATUS_UNKNOWN;
    }

    public static Integer translateBluegigaCode(String code) {
        if (code.equalsIgnoreCase("OK/0")) {
            return BluetoothSend.STATUS_SENT;
        }
        if (code.equalsIgnoreCase("MORE/0")) {
            return BluetoothSend.STATUS_SENT;
        }
        if (code.equalsIgnoreCase("RETRY/3")) {
            return BluetoothSend.STATUS_IGNORED;
        }
        if (code.equalsIgnoreCase("RETRY/8")) {
            return BluetoothSend.STATUS_IGNORED_RETRY;
        }
        if (code.equalsIgnoreCase("FAIL/4")) {
            return BluetoothSend.STATUS_REJECTED;
        }
        if (code.equalsIgnoreCase("FAIL/7")) {
            return BluetoothSend.STATUS_UNSUPPORTED;
        }
        return BluetoothSend.STATUS_UNKNOWN;
    }

    public static void main(String[] args) {
        String line = "20120104/141919 1118100080: Sent offer-which-wich.jpg to 00:1c:d4:8c:33:16: RETRY/8";

        Matcher matcher = bgSentEntry.matcher(line);

        if (matcher.matches()) {
            System.out.println("Matched");
        }
    }
}
