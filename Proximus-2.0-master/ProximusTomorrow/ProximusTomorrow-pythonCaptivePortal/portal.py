#!/usr/bin/env python

import sys
import subprocess
import re

###############################################
# properties retrieval
###############################################
def getProperty(prop):
    props = {}
    props['dnsmasqConf'] = '/tmp/foo'
    props['eth'] = 'eth0'
    props['ppp'] = 'ppp0'
    props['wifi'] = 'uap0'
    props['iptables'] = '/sbin/iptables'
    props['service'] = '/usr/sbin/service'
    return props[prop]

###############################################
# console manager
###############################################
class ConsoleManager():
        def execute(self, command):
                print command
                cmd = command.split(" ")
                subprocess.call(cmd)

        def evaluate(self, command):
                cmd = command.split(" ")
                process = subprocess.Popen(cmd, shell=False, stdout=subprocess.PIPE)
                data = process.communicate()

                results = data[0]
                return results


###############################################
# DNS manager
###############################################
class DNSManager():
        def turnOff(self):
                print "turn off dns resolution..."
                dnsmasqConf = getProperty("dnsmasqConf")
                dnsFile = open(dnsmasqConf, "w")
                dnsFile.write("# no dns management\n")
                dnsFile.close()

        def turnOn(self, defaultIP):
                print "turn on dns with default address resolution..."
                # todo - check for valid ip and throw exception if not
                dnsmasqConf = getProperty("dnsmasqConf")
                dnsFile = open(dnsmasqConf, "w")
                dnsFile.write("address=/#/%s\n" % defaultIP)
                dnsFile.close()

        def setDefault(self, defaultIP):
                print "set default address resolution..."
                # todo - check for valid ip and throw exception if not
                output = ""
                dnsmasqConf = getProperty("dnsmasqConf")
                dnsFile = open(dnsmasqConf, "r")
                fileData = dnsFile.readlines()
                for line in fileData:
                        if (line.find("address") == 0):
                                output += "address=/#/%s\n" % defaultIP
                        else:
                                output += line
                dnsFile.close()

                dnsFile = open(dnsmasqConf, "w")
                dnsFile.write(output)
                dnsFile.close()

        def addDomain(self, domain):
                print "adding domain entry..."
                dnsmasqConf = getProperty("dnsmasqConf")
                dnsFile = open(dnsmasqConf, "a")
                dnsFile.write("server=/%s/#\n" % domain)
                dnsFile.close()

        def removeDomain(self, domain):
                print "removing domain entry..."
                output = ""
                dnsmasqConf = getProperty("dnsmasqConf")
                dnsFile = open(dnsmasqConf, "r")
                fileData = dnsFile.readlines()
                for line in fileData:
                        if (line.find("server") == 0):
                                lineData = line.split("/")
                                if (lineData[1] != domain):
                                        output += line
                        else:
                                output += line
                dnsFile.close()

                dnsFile = open(dnsmasqConf, "w")
                dnsFile.write(output)
                dnsFile.close()

        def restartDNS(self):
                console = ConsoleManager()
                service = getProperty("service")
                console.execute("/etc/init.d/dnsmasq restart")


###############################################
# services
###############################################

# interface object for services
class Service():
        def match(self, command):
                pass

        def usage(self):
                return ""

        def run(self, args):
                pass
            

class DNSLookupAdd(Service):

        def match(self, command):
                value = False
                if (command[1] == "open" and command[2] == "dns"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "open dns <domain>         -> adds domain ip range to allowed"

        def run(self, args):        
                iptables = getProperty("iptables")
                dnsManager = DNSManager()        
                console = ConsoleManager()
                domain = args[3]
                addresses = set()
                command = "/home/proximus/bin/dns_ips.sh %s" % domain
                result = console.evaluate(command)
                alist = re.split('[\n,]', result)
                for address in alist:
                    if address not in addresses and len(address) != 0:
                        address = address.strip()
                        addresses.add(address)   
                        print "Adding: %s" % address                        
                                
                for ipr in addresses:
                    if(len(ipr) > 0):
                        command = '%s -t mangle -I internet 1 -d %s -j RETURN' % (iptables, ipr)
                        console.execute(command)
                        
                dnsManager.restartDNS()
                

class FacebookOpenStart(Service):
        def match(self, command):
                value = False
                if (command[1] == "open" and command[2] == "facebook"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "open facebook <IP>         -> configs and starts open portal"

        def run(self, args):
                iptables = getProperty("iptables")
            
                open = OpenStart()
                open.run(args)
                dnsManager = DNSManager()  
                console = ConsoleManager()
        
                
                # list of domains to make facebook work
                # this can be genericized by using a property file
                #domains = ['facebook.com', 'facebook.net', 'fbcdn.net', 'akamaihd.net']
                #domains = ['facebook.com', 'facebook.net', 'fbcdn.net', 'akamaihd.net', 'fb.net', 'connect.facebook.net', 'api.facebook.com', 'm.facebook.com', 'api-read.facebook.com', 'graph.facebook.com', 'static.ak.facebook.com', 'static.ak.fbcdn.net', 's-static.ak.fbcdn.net', 's-static.ak.facebook.com', 'profile.ak.fbcdn.net', 'pixel.facebook.com','s-static.ak.fbcdn.net']
                domains = ['m.facebook.com', 'facebook.com', 'facebook.net', 'fbcdn.net', 'akamaihd.net', 'fb.net', 'connect.facebook.net', 'api.facebook.com', 'm.facebook.com',
                            'api-read.facebook.com', 'graph.facebook.com', 'static.ak.facebook.com', 'static.ak.fbcdn.net', 's-static.ak.fbcdn.net',
                             's-static.ak.facebook.com', 'profile.ak.fbcdn.net', 'pixel.facebook.com', 's-static.ak.fbcdn.net']
                addresses = set()
                i = 1
                for domain in domains:
                        command = "/home/proximus/bin/dns_ips.sh %s" % domain
                        result = console.evaluate(command)
                        alist = re.split('[\n,]', result)
                        for address in alist:
                            if address not in addresses and len(address) != 0:
                                address = address.strip()
                                addresses.add(address)  
                                print "Adding %i: %s" % (i, address)
                                i += 1    
                for ipr in addresses:
                    if(len(ipr) > 0):
                        command = '%s -t mangle -I internet 1 -d %s -j RETURN' % (iptables, ipr)
                        console.execute(command)
                        
                dnsManager.restartDNS()
                

class OpenStart(Service):
        def match(self, command):
                value = False
                if (command[1] == "open" and command[2] == "start"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "open start <IP>         -> configs and starts open portal"

        def run(self, args):
                ethPort = getProperty("eth")
                pppPort = getProperty("ppp")
                wifiPort = getProperty("wifi")
                iptables = getProperty("iptables")

                console = ConsoleManager()

                
                ############################
                # ENABLE IPV4 FORWARD
                ############################
                #echo 1 > /proc/sys/net/ipv4/ip_forward
                console.execute("/bin/echo 1 > /proc/sys/net/ipv4/ip_forward")
                ##############
                # END IPV4 FORWARD
                ##############
                
                
                ############################
                # FLUSH ALL RULES/CHAINS
                ############################
                #iptables -F             # will delete all rules from filter table
                #iptables -F -t nat      # will delete all rules from nat table
                #iptables -F -t mangle   # will delete all rules from mangle table
                console.execute("%s -F" % iptables)
                console.execute("%s -F -t nat" % iptables)
                console.execute("%s -F -t mangle" % iptables)
                # END FLUSH
                ##############
                
                
                ############################
                # MANGLE
                ############################
                
                ############################
                # MANGLE : INTERNET
                ############################
                # CREATE CHAIN
                #iptables -t mangle -X internet
                #iptables -t mangle -N internet
                # (inserts go here) -j RETURN before it gets marked with 99
                #iptables -t mangle -A internet -i uap0 -j MARK --set-mark 99
                console.execute("%s -t mangle -X internet" % (iptables))
                console.execute("%s -t mangle -N internet" % (iptables))
                console.execute("%s -t mangle -A internet -i %s -j MARK --set-mark 99" % (iptables, wifiPort))
                
                
                # RUN MANGLE INTERNET WHEN A PACKET COMES IN
                #iptables -t mangle -A PREROUTING -j internet                
                console.execute("%s -t mangle -A PREROUTING -j internet" % iptables)
                ##############
                # END MANGLE
                ##############

                ############################
                # FILTER
                ############################
                
                #DO NOT DROP ALL MARKED 99(0x63) 
                #iptables -t filter -A INPUT -m mark --mark 99 -j DROP
                
                #console.execute("iptables -t mangle -A internet -i %s -j MARK --set-mark 99" % wifiPort)
                
                ############################
                # FILTER : AP
                ############################
                # ICMP
                console.execute("%s -A INPUT -i %s -p icmp -j ACCEPT" % (iptables, wifiPort))
                
                # HTTP
                #iptables -t filter -A INPUT -i uap0 -p tcp --dport 80 -j ACCEPT
                console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 80 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 80 -j ACCEPT" % (iptables , wifiPort))
                # HTTPS/SSL
                #iptables -t filter -A INPUT -i uap0 -p tcp --dport 443 -j ACCEPT
                #iptables -t filter -A INPUT -i uap0 -p tcp --sport 443 -j ACCEPT
                console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 443 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 443 -j ACCEPT" % (iptables , wifiPort)) 
                # DNS
                #iptables -t filter -A INPUT -i uap0 -p udp --dport 53 -j ACCEPT
                #iptables -t filter -A INPUT -i uap0 -p udp --dport 5353 -j ACCEPT
                console.execute("%s -t filter -A INPUT -i %s -p udp --dport 53 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p udp --sport 53 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p udp --dport 5353 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p udp --sport 5353 -j ACCEPT" % (iptables , wifiPort))
                # SSH/SCP
                #iptables -t filter -A INPUT -i uap0 -p tcp --dport 16222 -j ACCEPT
                #iptables -t filter -A INPUT -i uap0 -p tcp --dport 22 -j ACCEPT
                console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 16222 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 16222 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 22 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 22 -j ACCEPT" % (iptables , wifiPort))
                # DHCP
                #iptables -t filter -A INPUT -i uap0 -p udp --dport 67:68 --sport 67:68 -j ACCEPT  
                console.execute("%s -t filter -A INPUT -i %s -p udp --dport 67:68 -j ACCEPT" % (iptables , wifiPort))
                console.execute("%s -t filter -A INPUT -i %s -p udp --sport 67:68 -j ACCEPT" % (iptables , wifiPort))
                # DROP
                #iptables -t filter -A INPUT -i uap0 -j DROP
                console.execute("%s -t filter -A INPUT -i %s -j DROP" % (iptables , wifiPort))
                #iptables -t nat -A PREROUTING -m mark --mark 99 -p tcp --dport 80 -j DNAT --to-destination 192.168.45.3
                #console.execute("iptables -t nat -A PREROUTING -m mark --mark 99 -p tcp --dport 80 -j DNAT --to-destination %s" % args[3])
                ##############
                # END UAP0
                ##############

                interfaces = [ethPort,pppPort,"eth1"]
                for interface in interfaces:
                    ############################
                    # FILTER : OTHER
                    ############################
                    #iptables -t filter -A INPUT -i eth0 -m state --state RELATED,ESTABLISHED -j ACCEPT
                    console.execute("%s -t filter -A INPUT -i %s -m state --state RELATED,ESTABLISHED -j ACCEPT" % (iptables , interface))
                    # allow icmp to ethernet, wifi, and ppp
                    console.execute("%s -A INPUT -i %s -p icmp -j ACCEPT" % (iptables, interface))
                
                    # HTTP
                    #iptables -t filter -A INPUT -i eth0 -p tcp --dport 80 --sport 80 -j ACCEPT
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 80 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 80 -j ACCEPT" % (iptables , interface))
                    # HTTPS/SSL
                    #iptables -t filter -A INPUT -i eth0 -p tcp --dport 443 -j ACCEPT
                    #iptables -t filter -A INPUT -i eth0 -p tcp --dport 443 --sport 443 -j ACCEPT
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 443 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 443 -j ACCEPT" % (iptables , interface)) 
                    # DNS
                    #iptables -t filter -A INPUT -i eth0 -p udp --dport 53 -j ACCEPT
                    #iptables -t filter -A INPUT -i eth0 -p udp --dport 5353 -j ACCEPT
                    console.execute("%s -t filter -A INPUT -i %s -p udp --dport 53 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p udp --sport 53 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p udp --dport 5353 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p udp --sport 5353 -j ACCEPT" % (iptables , interface))
                    # SSH/SCP
                    #iptables -t filter -A INPUT -i eth0 -p tcp --dport 16222 -j ACCEPT
                    #iptables -t filter -A INPUT -i eth0 -p tcp --dport 22 -j ACCEPT
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 16222 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 16222 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --dport 22 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p tcp --sport 22 -j ACCEPT" % (iptables , interface))
                    # DHCP
                    #iptables -t filter -A INPUT -i eth0 -p udp --dport 67:68 --sport 67:68 -j ACCEPT
                    console.execute("%s -t filter -A INPUT -i %s -p udp --dport 67:68 -j ACCEPT" % (iptables , interface))
                    console.execute("%s -t filter -A INPUT -i %s -p udp --sport 67:68 -j ACCEPT" % (iptables , interface))
                    # DROP
                    #iptables -t filter -A INPUT -i eth0 -j DROP
                    #console.execute("iptables -t filter -A INPUT -i %s -j ACCEPT" % interface)
                    ##############
                    # END ETH0
                    ##############
                    ############################
                    # NAT
                    ############################    
                    #iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
                    console.execute("%s -t nat -A POSTROUTING -o %s -j MASQUERADE" % (iptables , interface))
                
                #console.execute("iptables -t nat -A POSTROUTING -o %s -j MASQUERADE" % ethPort)

                
                ############################
                # NAT
                ############################
                # ROUTE ALL HTTP/HTTPS
                #iptables -t nat -A PREROUTING -m mark --mark 99 -p tcp --dport 80 -j DNAT --to-destination 192.168.45.3
                console.execute("%s -t nat -A PREROUTING --match mark --mark 99 -p tcp --dport 80 -j DNAT --to-destination %s" % (iptables, args[3]))
                #console.execute("iptables -t nat -A PREROUTING --match mark --mark 99 -p tcp --dport 443 -j DNAT --to-destination %s" % args[3])
                #console.execute("%s -t nat -A POSTROUTING -o eth0 -j MASQUERADE" % iptables)
                


                


                dns = DNSManager()
                dns.turnOff()
                dns.restartDNS()
                
class OpenAddMAC(Service):
        def match(self, command):
                value = False
                if (command[1] == "open" and command[2] == "add"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "open add <ip addres>    -> add IP's MAC to pass through"

        def run(self, args):
                console = ConsoleManager()
                iptables = getProperty("iptables")
                data = console.evaluate("/usr/sbin/arp -an")
                data = data.split("\n")

                for item in data:
                        openp = item.find("(") + 1
                        closep = item.find(")")
                        ipAddress = item[openp:closep]

                        if (ipAddress.strip() == args[3]):
                                atLoc = item.find("at") + 3
                                macAddress = item[atLoc:(atLoc + 17)].upper()
                                command = '%s -t mangle -I internet 1 -m mac --mac-source %s -j RETURN' % (iptables, macAddress)
                                console.execute(command)


class OpenRemoveMAC(Service):
        def match(self, command):
                value = False
                if (command[1] == "open" and command[2] == "remove"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "open remove <ip addres> -> remove IP's MAC from pass through"

        def run(self, args):
                console = ConsoleManager()
                iptables = getProperty("iptables")
                data = console.evaluate("/usr/sbin/arp -an")
                data = data.split("\n")

                for item in data:
                        openp = item.find("(") + 1
                        closep = item.find(")")
                        ipAddress = item[openp:closep]

                        if (ipAddress.strip() == args[3]):
                                atLoc = item.find("at") + 3
                                macAddress = item[atLoc:(atLoc + 17)].upper()
                                command = "%s -t mangle -D internet 1 -m mac --mac-source %s" % (iptables, macAddress)
                                console.execute(command)

class ClosedStart(Service):
        def match(self, command):
                value = False
                if (command[1] == "closed" and command[2] == "start"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "closed start <IP>       -> configs and starts closed portal"

        def run(self, args):
                ethPort = getProperty("eth")
                pppPort = getProperty("ppp")
                wifiPort = getProperty("wifi")
                iptables = getProperty("iptables")
                print(args)

                console = ConsoleManager()

                console.execute("/bin/echo 1 > /proc/sys/net/ipv4/ip_forward")

                # flush ip tables and recreate
                console.execute("%s -F" % iptables)
                console.execute("%s -F -t nat" % iptables)
                console.execute("%s -F -t mangle" % iptables)

                # accept all on localhost
                console.execute("%s -A INPUT -i lo -j ACCEPT" % iptables)

                # handles all traffic that is coming back in from device requests
                console.execute("%s -A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT" % iptables)

                # allow icmp to ethernet, wifi, and ppp
                console.execute("%s -A INPUT -i %s -p icmp -j ACCEPT" % (iptables, ethPort))
                console.execute("%s -A INPUT -i %s -p icmp -j ACCEPT" % (iptables, pppPort))
                console.execute("%s -A INPUT -i %s -p icmp -j ACCEPT" % (iptables, wifiPort))

                # allow Web Server for wifi
                console.execute("%s -A INPUT -i %s -p tcp -m tcp --dport 80 -j ACCEPT" % (iptables, wifiPort))

                # allow DNS for wifi
                console.execute("%s -A INPUT -i %s -p udp --dport 53 -j ACCEPT" % (iptables, wifiPort))
                console.execute("%s -A INPUT -i %s -p tcp --dport 53 -j ACCEPT" % (iptables, wifiPort))
                console.execute("%s -A INPUT -i %s -p udp --dport 5353 -j ACCEPT" % (iptables, wifiPort))
                console.execute("%s -A INPUT -i %s -p tcp --dport 5353 -j ACCEPT" % (iptables, wifiPort))
                console.execute("%s -t nat -A PREROUTING -p udp -i %s --dport 53 -j DNAT --to %s" % (iptables, wifiPort, '192.168.45.3'))
                console.execute("%s -t nat -A PREROUTING -p tcp -i %s --dport 53 -j DNAT --to %s" % (iptables, wifiPort, '192.168.45.3'))


                # allow DHCP for wifi
                console.execute("%s -A INPUT -i %s -p udp --dport 67:68 --sport 67:68 -j ACCEPT" % (iptables, wifiPort))

                # allow ssh for ethernet and ppp
                console.execute("%s -A INPUT -i %s -p tcp -m tcp --dport 16222 -j ACCEPT" % (iptables, ethPort))
                console.execute("%s -A INPUT -i %s -p tcp -m tcp --dport 16222 -j ACCEPT" % (iptables, wifiPort))
                console.execute("%s -A INPUT -i %s -p tcp -m tcp --dport 16222 -j ACCEPT" % (iptables, pppPort))
                console.execute("%s -A INPUT -i %s -p tcp -m tcp --dport 22 -j ACCEPT" % (iptables, ethPort))
                console.execute("%s -A INPUT -i %s -p tcp -m tcp --dport 22 -j ACCEPT" % (iptables, wifiPort))
                console.execute("%s -A INPUT -i %s -p tcp -m tcp --dport 22 -j ACCEPT" % (iptables, pppPort))

                # logging to /var/log/syslog all dropped packets - useful for debugging
                console.execute('%s -I INPUT 5 -m limit --limit 5/min -j LOG --log-prefix "DROP:" --log-level 7' % iptables)

                # drop everything else
                console.execute("%s -A INPUT -j DROP" % iptables)
                
                console.execute("%s -t nat -A POSTROUTING -o %s -j MASQUERADE" % (iptables , ethPort))
                console.execute("%s -t nat -A POSTROUTING -o %s -j MASQUERADE" % (iptables , "eth1"))
                console.execute("%s -t nat -A POSTROUTING -o %s -j MASQUERADE" % (iptables , pppPort))


                # reset DNS
                ip = args[3]
                dns = DNSManager()
                dns.turnOn(ip)
                dns.restartDNS()

class ClosedDefault(Service):
        def match(self, command):
                value = False
                if (command[1] == "closed" and command[2] == "default"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "closed default <IP>     -> config default IP Address for DNS"

        def run(self, args):
                ip = args[3]
                dns = DNSManager()
                dns.setDefault(ip)
                dns.restartDNS()

class ClosedAddDomain(Service):
        def match(self, command):
                value = False
                if (command[1] == "closed" and command[2] == "add"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "closed add <domain>     -> Adds domain to pass through portal"

        def run(self, args):
                domain = args[3]
                dns = DNSManager()
                dns.addDomain(domain)
                dns.restartDNS()

class ClosedRemoveDomain(Service):
        def match(self, command):
                value = False
                if (command[1] == "closed" and command[2] == "remove"):
                        if (len(command) == 4):
                                value = True
                return value

        def usage(self):
                return "closed remove <domain>  -> Removes domain from pass through portal"

        def run(self, args):
                domain = args[3]
                dns = DNSManager()
                dns.removeDomain(domain)
                dns.restartDNS()


###############################################
# main
###############################################

services = [ OpenStart(), FacebookOpenStart(), OpenAddMAC(), OpenRemoveMAC(),
        ClosedStart(), ClosedDefault(), ClosedAddDomain(),
        ClosedRemoveDomain(), DNSLookupAdd() ]
flag = False
for service in services:
        if (len(sys.argv) > 1 and service.match(sys.argv)):
                service.run(sys.argv)
                flag = True
                break

if (not flag):
        print "portal.py [open|closed] <command> <option>"
        for service in services:
                print service.usage()

