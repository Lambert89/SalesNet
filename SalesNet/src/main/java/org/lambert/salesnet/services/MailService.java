package org.lambert.salesnet.services;

import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class MailService {

	private JavaMailSender mailSender;

	private VelocityEngine velocityEngine;

	private MessageSource mailMessageSource;

	public void sendSubscribeConfirmation(final String mailDestination,
			final Map<String, Object> model) throws MessagingException {
		final MimeMessage message = this.mailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(message, false,
				"UTF-8");
		helper.setTo(mailDestination);
		helper.setFrom(this.mailMessageSource.getMessage("mail.from", null,
				Locale.getDefault()));

		String text = VelocityEngineUtils.mergeTemplateIntoString(
				this.getVelocityEngine(), "content/subscribeConfirmation.vm",
				"UTF-8", model);
		helper.setText(text, true);
		helper.setSubject(this.mailMessageSource.getMessage(
				"mail.subscribeCofirmationSubject", null, Locale.getDefault()));

		this.mailSender.send(message);
	}

	public void sendUnsubscribedNotification(final String mailDestination,
			final Map<String, Object> model) throws MessagingException {
		final MimeMessage message = this.mailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(message, false,
				"UTF-8");
		helper.setTo(mailDestination);
		helper.setFrom(this.mailMessageSource.getMessage("mail.from", null,
				Locale.getDefault()));

		String text = VelocityEngineUtils.mergeTemplateIntoString(
				this.getVelocityEngine(),
				"content/unsubscribedNotification.vm", "UTF-8", model);
		helper.setText(text, true);
		helper.setSubject(this.mailMessageSource.getMessage(
				"mail.unsubscribedNotificationSubject", null,
				Locale.getDefault()));

		this.mailSender.send(message);
	}

	public void sendContentUpdateNotification(final String mailDestination,
			final Map<String, Object> model) throws MessagingException {
		final MimeMessage message = this.mailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(message, false,
				"UTF-8");
		helper.setTo(mailDestination);
		helper.setFrom(this.mailMessageSource.getMessage("mail.from", null,
				Locale.getDefault()));

		String text = VelocityEngineUtils.mergeTemplateIntoString(
				this.getVelocityEngine(),
				"content/contentUpdateConfirmation.vm", "UTF-8", model);
		helper.setText(text, true);
		helper.setSubject(this.mailMessageSource.getMessage(
				"mail.contentUpdateSubject", null, Locale.getDefault()));

		this.mailSender.send(message);
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public MessageSource getMailMessageSource() {
		return mailMessageSource;
	}

	public void setMailMessageSource(MessageSource mailMessageSource) {
		this.mailMessageSource = mailMessageSource;
	}
}
