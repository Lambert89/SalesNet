package org.lambert.salesnet.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * <p>
 * <b> FollowUp. </b>
 * </p>
 */
@Embeddable
public class FollowUp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String comment;

    private FollowUp followUp;

    private String personId;

    /**
     * @return the personId
     */
    public String getPersonId() {
        return this.personId;
    }

    /**
     * @param personId
     *            the personId to set
     */
    public void setPersonId(final String personId) {
        this.personId = personId;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * @return the followUp
     */
    public FollowUp getFollowUp() {
        return this.followUp;
    }

    /**
     * @param followUp
     *            the followUp to set
     */
    public void setFollowUp(final FollowUp followUp) {
        this.followUp = followUp;
    }
}
