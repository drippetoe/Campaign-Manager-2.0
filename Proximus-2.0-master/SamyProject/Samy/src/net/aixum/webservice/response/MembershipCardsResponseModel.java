/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.List;
import net.aixum.webservice.MembershipCardSummary;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class MembershipCardsResponseModel extends BaseResponseModel {

    private List<MembershipCardSummary> membershipCards;

    public MembershipCardsResponseModel(List<MembershipCardSummary> membershipCards) {
        this.membershipCards = membershipCards;
    }

    public MembershipCardsResponseModel(List<MembershipCardSummary> membershipCards, boolean success, String message) {
        super(success, message);
        this.membershipCards = membershipCards;
    }

    public List<MembershipCardSummary> getMembershipCards() {
        return membershipCards;
    }

    public void setMembershipCards(List<MembershipCardSummary> membershipCards) {
        this.membershipCards = membershipCards;
    }
}
