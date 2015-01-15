package org.lambert.salesnet.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.lambert.salesnet.beans.Comment;
import org.lambert.salesnet.beans.DocumentTracker;
import org.lambert.salesnet.beans.NewDocument;
import org.lambert.salesnet.beans.User;
import org.lambert.salesnet.exceptions.ValidationException;
import org.lambert.salesnet.services.MailService;
import org.lambert.salesnet.services.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * <b> Entry point of salesNet. </b>
 * </p>
 */
@Controller
public class NavigationOrchistration {

	@Value("${config.baseUrl}")
	private String baseUrl;

	@Resource
	private PersistenceService persistenceService;

	@Resource
	private MailService mailService;

	private static final Logger _LOGGER = LoggerFactory.getLogger("ERROR."
			+ NavigationOrchistration.class);

	@RequestMapping(value = "/admin/getUserDetails", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getUserDetails() {
		final List<User> users = this.persistenceService
				.queryObjects(User.class);
		final List<Comment> comments = this.persistenceService
				.queryObjects(Comment.class);
		final Map<String, Object> result = new HashMap<String, Object>(
				users.size());
		for (final User user : users) {
			final Map<String, Object> detail = new HashMap<String, Object>(3);
			final String mailAddress = user.getMail();
			detail.put("user", user);
			for (final Comment comment : comments) {
				final String mailID = comment.getMail();
				System.out.println("=========================");
				System.out.println("mailID: " + mailID);
				System.out.println("mailAddress: " + mailAddress);
				System.out.println("comment: " + comment.getComment());
				System.out.println("Date: " + comment.getDate());
				if (StringUtils.equals(mailAddress, mailID)) {
					detail.put("comment", comment);
					break;
				}
			}
			result.put(mailAddress, detail);
		}
		return result;
	}

	@RequestMapping(value = "/admin/fileupload", method = RequestMethod.POST)
	public void fileupload(final @RequestParam MultipartFile[] files,
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {

		final String realPath = request.getSession().getServletContext()
				.getRealPath("/upload");

		System.out.println("realPath: " + realPath);

		final List<NewDocument> newDocs = new ArrayList<NewDocument>();

		this.persistenceService.updateObjects(DocumentTracker.class, 1L,
				new PersistenceService.UpdateOperation<DocumentTracker>() {

					@Override
					public void update(final DocumentTracker docTracker)
							throws Exception {
						for (final MultipartFile file : files) {
							String originalFilename = file
									.getOriginalFilename();
							System.out.println("originalFilename: "
									+ originalFilename);
							FileUtils.copyInputStreamToFile(file
									.getInputStream(), new File(realPath,
									originalFilename));
							docTracker.addDocument(originalFilename, realPath
									+ "/" + originalFilename);
							final NewDocument newDoc = new NewDocument();
							newDoc.setName(originalFilename);
							newDoc.setUrl(NavigationOrchistration.this.baseUrl
									+ "/api/download/" + originalFilename);
							newDocs.add(newDoc);

							System.out
									.println("after upload -> getAllDocumentNames: "
											+ docTracker.getAllDocumentNames()
											+ " with id: " + docTracker.getId());
						}
					}

				});
		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(true);

		this.persistenceService.saveObjects(newDocs.toArray());
	}

	@RequestMapping(value = "/documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAllDocuments() throws Exception {
		final DocumentTracker docTracker = this.persistenceService.findObject(
				DocumentTracker.class, 1L);
		final List<String> docNames = docTracker.getAllDocumentNames();
		final Map<String, Object> result = new HashMap<String, Object>(2);
		result.put("documents", docNames);
		result.put("baseUrl", this.baseUrl);
		return result;
	}

	@RequestMapping(value = "/download/{documentName}", method = RequestMethod.GET)
	public void downloadDocument(@PathVariable final String documentName,
			final HttpServletResponse response) throws Exception {

		System.out.println("Testing downloadDocument DocumentName: "
				+ documentName);

		final DocumentTracker docTracker = this.persistenceService.findObject(
				DocumentTracker.class, 1L);

		System.out.println("Testing downloadDocument docTraker: " + docTracker
				+ " with id: " + docTracker.getId());

		System.out.println("during download -> getAllDocumentNames: "
				+ docTracker.getAllDocumentNames());
		final String filename = documentName.endsWith(".pdf") ? documentName
				: documentName.concat(".pdf");

		final String path = docTracker.getDocumentLocation(filename);
		System.out.println("file path: " + path);
		final File file = new File(path);

		try {
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			final OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			NavigationOrchistration._LOGGER.error(
					"Error during downloading file {" + filename + "}.", ex);
			throw ex;
		}
	}

	@RequestMapping(value = "/unsubscribe/{mailAddress}", method = RequestMethod.GET)
	public String unsubscribe(@PathVariable final String mailAddress) {
		System.out.println("Unsubscribe through mail!");
		return "redirect:/index.html#mailAddress";
	}

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
	public @ResponseBody String unsubscribe(
			@RequestBody final Map<String, String> body) throws Exception {
		final String mailAddress = body.get("unsubscribeEmail");
		if (StringUtils.isBlank(mailAddress)) {
			throw new ValidationException("unsubscribeEmail");
		}
		final User user = this.persistenceService.findObject(User.class,
				mailAddress);
		if (user != null) {
			final String username = user.getName();
			this.persistenceService.updateObjects(User.class, mailAddress,
					new PersistenceService.UpdateOperation<User>() {
						@Override
						public void update(User object) throws Exception {
							object.setIsActive(Boolean.FALSE);
						}
					});
			final Map<String, Object> model = new HashMap<String, Object>();
			model.put("userName", username);
			model.put("webUrl", NavigationOrchistration.this.baseUrl);
			try {
				this.mailService.sendUnsubscribedNotification(mailAddress,
						model);
			} catch (MessagingException e) {
				_LOGGER.error("Fail to send unsubscribed notification to {"
						+ mailAddress + "}.", e);
			}
		}

		return "true";
	}

	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public @ResponseBody String subscribe(
			@RequestBody final Map<String, String> body) throws Exception {
		String result = "true";
		final String mailAddress = body.get("mailAddress");
		final String userName = body.get("userName");
		final String phoneNumber = body.get("phoneNumber");
		final String contactMeIndicator = body.get("contactMeIndicator");
		final String otherThingsToSay = body.get("otherThingsToSay");
		System.out.println("mailAddress: " + mailAddress);
		System.out.println("userName: " + userName);
		System.out.println("phoneNumber: " + phoneNumber);
		System.out.println("contactMeIndicator: " + contactMeIndicator);
		System.out.println("otherThingsToSay: " + otherThingsToSay);
		if (StringUtils.isBlank(userName)) {
			throw new ValidationException("userName");
		}
		if (StringUtils.isBlank(mailAddress)) {
			throw new ValidationException("mailAddress");
		}
		if ((StringUtils.isBlank(contactMeIndicator) || "false"
				.equalsIgnoreCase(contactMeIndicator))
				&& StringUtils.isNotBlank(phoneNumber)) {
			throw new ValidationException("phoneNumber&contactMeIndicator");
		}
		if (StringUtils.isNotBlank(contactMeIndicator)
				&& "true".equalsIgnoreCase(contactMeIndicator)
				&& StringUtils.isBlank(phoneNumber)) {
			throw new ValidationException("phoneNumber&contactMeIndicator");
		}
		final User user = new User();
		user.setMail(mailAddress);
		user.setName(userName);
		if (StringUtils.isNotBlank(contactMeIndicator)
				&& "true".equalsIgnoreCase(contactMeIndicator)
				&& StringUtils.isNotBlank(phoneNumber)) {
			user.setContactMeIndicator(true);
			user.setPhoneNumber(phoneNumber);
		}
		try {
			final User exist = this.persistenceService.findObject(User.class,
					mailAddress);
			if (exist == null) {
				System.out.println("new User! " + mailAddress);
				this.persistenceService.saveObjects(new Object[] { user });
			}
			if (StringUtils.isNotBlank(otherThingsToSay)) {
				final Comment existComment = this.persistenceService
						.findObject(Comment.class, mailAddress);
				if (existComment == null) {
					final Comment comment = new Comment();
					comment.setMail(mailAddress);
					comment.setDate(new Date());
					comment.setComment(otherThingsToSay);
					this.persistenceService
							.saveObjects(new Object[] { comment });
				} else {
					this.persistenceService.updateObjects(Comment.class,
							mailAddress,
							new PersistenceService.UpdateOperation<Comment>() {

								@Override
								public void update(Comment comment)
										throws Exception {
									comment.setComment(otherThingsToSay);
									comment.setDate(new Date());
								}
							});
				}
			}
			final Map<String, Object> model = new HashMap<String, Object>();
			model.put("webUrl", this.baseUrl);
			model.put("userName", userName);
			this.mailService.sendSubscribeConfirmation(mailAddress, model);
		} catch (MessagingException e) {
			result = "false";
			throw e;
		} catch (Exception e) {
			result = "false";
			throw e;
		}
		return result;
	}

	@RequestMapping(value = "/admin/confirmUpdateNotification", method = RequestMethod.GET)
	public @ResponseBody String confirmUpdateNotification() {
		final List<NewDocument> newDocs = this.persistenceService
				.queryObjects(NewDocument.class);

		if (newDocs.size() > 0) {
			final Map<String, String> newDocMap = new HashMap<String, String>();
			final Object[] primaryKeys = new Long[newDocs.size()];
			int i = 0;
			for (final NewDocument newDoc : newDocs) {
				newDocMap.put(newDoc.getName(), newDoc.getUrl());
				primaryKeys[i] = newDoc.getId();
				i++;
			}
			final Set<Map.Entry<String, String>> newEntries = newDocMap
					.entrySet();

			final List<User> users = this.persistenceService
					.queryObjects(User.class);
			for (final User user : users) {
				final Boolean isActive = user.getIsActive();
				if (isActive) {
					final String mailDestination = user.getMail();
					final Map<String, Object> templateParams = new HashMap<String, Object>();
					templateParams.put("newsList", newEntries);
					templateParams.put("webUrl", this.baseUrl);
					templateParams.put("userName", user.getName());
					templateParams.put("unsubscribeUrl",
							NavigationOrchistration.this.baseUrl
									+ "/api/unsubscribe/" + mailDestination);
					try {
						this.mailService.sendContentUpdateNotification(
								mailDestination, templateParams);
					} catch (MessagingException e) {
						_LOGGER.error("Fail to send mail to {"
								+ mailDestination + "}.", e);
					}
				}
			}
			this.persistenceService.deleteObjects(NewDocument.class,
					primaryKeys);
		}
		return "true";
	}

	@ExceptionHandler(value = Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> handleException(
			final Exception exception, final HttpServletRequest request) {
		NavigationOrchistration._LOGGER.error("Spring MVC processing error",
				exception);
		exception.printStackTrace();
		final Map<String, Object> result = new HashMap<String, Object>(1);
		result.put("error", exception.getMessage());
		return result;
	}

	@ExceptionHandler(value = ValidationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody Map<String, Object> handleValidationError(
			final ValidationException exception,
			final HttpServletRequest request) {
		NavigationOrchistration._LOGGER.error("Validation Error", exception);
		exception.printStackTrace();
		final String[] errorFields = exception.getErrorFieldName().split("&");
		final Map<String, Object> validationError = new HashMap<String, Object>();
		validationError.put("fields", errorFields);
		return validationError;
	}
}
