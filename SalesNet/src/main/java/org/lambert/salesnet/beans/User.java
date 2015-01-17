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

	private String name;

	private String phoneNumber;

	private Boolean contactMeIndicator;

	private Boolean isActive = Boolean.TRUE;

	private String languageCode;

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

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
	 * @return the contactMeIndicator
	 */
	public Boolean getContactMeIndicator() {
		return this.contactMeIndicator;
	}

	/**
	 * @param contactMeIndicator
	 *            the contactMeIndicator to set
	 */
	public void setContactMeIndicator(final Boolean contactMeIndicator) {
		this.contactMeIndicator = contactMeIndicator;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
}
