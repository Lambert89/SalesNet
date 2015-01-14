package org.lambert.salesnet.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * <p>
 * <b> User. </b>
 * </p>
 */
@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String mail;

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
}
