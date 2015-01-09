package org.lambert.salesnet.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * <p>
 * <b> Comment. </b>
 * </p>
 */
@Entity
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    private String mail;

    private String comment;

    private FollowUp followUp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /**
     * @return the mail
     */
    public String getMail() {
        return this.mail;
    }

    /**
     * @param mail
     *            the mail to set
     */
    public void setMail(final String mail) {
        this.mail = mail;
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
     * @return the id
     */
    public long getId() {
        return this.id;
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
