/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class VoucherDetailsResponseModel extends BaseResponseModel {

    private int id;
    private int revision;
    private boolean needsLocation;
    private boolean locked;
    List<Integer> categoryIds;
    private String name;
    private String description;
    private Date start;
    private Date end;
    private int timeLimit;
    private String type;
    private String url;
    private String subline;
    private boolean share;
    private String voucherCode;
    private String voucherScanHtml;
    private String voucherHtml;

    public VoucherDetailsResponseModel(int id, int revision, boolean needsLocation, boolean locked, List<Integer> categoryIds, String name, String description, Date start, Date end, int timeLimit, String type, String url, String subline, boolean share, String voucherCode, String voucherScanHtml, String voucherHtml) {
        this.id = id;
        this.revision = revision;
        this.needsLocation = needsLocation;
        this.locked = locked;
        this.categoryIds = categoryIds;
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.timeLimit = timeLimit;
        this.type = type;
        this.url = url;
        this.subline = subline;
        this.share = share;
        this.voucherCode = voucherCode;
        this.voucherScanHtml = voucherScanHtml;
        this.voucherHtml = voucherHtml;
    }

    public VoucherDetailsResponseModel(int id, int revision, boolean needsLocation, boolean locked, List<Integer> categoryIds, String name, String description, Date start, Date end, int timeLimit, String type, String url, String subline, boolean share, String voucherCode, String voucherScanHtml, String voucherHtml, boolean success, String message) {
        super(success, message);
        this.id = id;
        this.revision = revision;
        this.needsLocation = needsLocation;
        this.locked = locked;
        this.categoryIds = categoryIds;
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.timeLimit = timeLimit;
        this.type = type;
        this.url = url;
        this.subline = subline;
        this.share = share;
        this.voucherCode = voucherCode;
        this.voucherScanHtml = voucherScanHtml;
        this.voucherHtml = voucherHtml;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public boolean isNeedsLocation() {
        return needsLocation;
    }

    public void setNeedsLocation(boolean needsLocation) {
        this.needsLocation = needsLocation;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubline() {
        return subline;
    }

    public void setSubline(String subline) {
        this.subline = subline;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getVoucherScanHtml() {
        return voucherScanHtml;
    }

    public void setVoucherScanHtml(String voucherScanHtml) {
        this.voucherScanHtml = voucherScanHtml;
    }

    public String getVoucherHtml() {
        return voucherHtml;
    }

    public void setVoucherHtml(String voucherHtml) {
        this.voucherHtml = voucherHtml;
    }
}
