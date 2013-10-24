/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.List;
import net.aixum.webservice.BranchSummary;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class BranchesResponseModel extends BaseResponseModel {

    List<BranchSummary> branches;

    public BranchesResponseModel() {
        super();
    }

    public BranchesResponseModel(List<BranchSummary> branches, boolean success, String message) {
        super(success, message);
        this.branches = branches;
    }

    public List<BranchSummary> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchSummary> branches) {
        this.branches = branches;
    }
}
