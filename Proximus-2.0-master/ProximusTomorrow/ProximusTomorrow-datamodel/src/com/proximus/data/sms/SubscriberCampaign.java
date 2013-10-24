
package com.proximus.data.sms;


import java.io.Serializable;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
/**
 *
 * @author Angela Mercer
 */
@Entity
@Index(members = {"subscriber", "smscampaign"}, unique = "true")
@Table(name = "subscriber_campaign")
public class SubscriberCampaign implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    @ManyToOne
    @Index
    private SMSCampaign smscampaign;
    @ManyToOne
    @Index
    private Subscriber subscriber;
    @Column(name = "sms_opt_in")
    private Boolean smsOptIn;
    @Column(name = "tracking_opt_in")
    private Boolean trackingOptIn;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Boolean getSmsOptIn()
    {
        return smsOptIn;
    }

    public void setSmsOptIn(Boolean smsOptIn)
    {
        this.smsOptIn = smsOptIn;
    }

    public SMSCampaign getSmscampaign()
    {
        return smscampaign;
    }

    public void setSmscampaign(SMSCampaign smscampaign)
    {
        this.smscampaign = smscampaign;
    }

    public Subscriber getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber)
    {
        this.subscriber = subscriber;
    }

    public Boolean getTrackingOptIn()
    {
        return trackingOptIn;
    }

    public void setTrackingOptIn(Boolean trackingOptIn)
    {
        this.trackingOptIn = trackingOptIn;
    }
    
    @Override
    public String toString() {
        return getSubscriber() + " | " + getSmscampaign() + " | " + getSmsOptIn() 
                + " | " + getTrackingOptIn();
    }
    
    
}
