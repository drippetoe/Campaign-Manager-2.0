/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.List;
import net.aixum.webservice.VoucherSummary;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class VouchersResponseModel extends BaseResponseModel {

    private List<VoucherSummary> vouchers;

    public VouchersResponseModel(List<VoucherSummary> vouchers) {
        this.vouchers = vouchers;
    }

    public VouchersResponseModel(List<VoucherSummary> vouchers, boolean success, String message) {
        super(success, message);
        this.vouchers = vouchers;
    }

    public List<VoucherSummary> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<VoucherSummary> vouchers) {
        this.vouchers = vouchers;
    }
}
