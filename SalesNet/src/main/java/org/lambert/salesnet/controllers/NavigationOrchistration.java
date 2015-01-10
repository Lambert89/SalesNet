package org.lambert.salesnet.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

	private static final Logger _LOGGER = LoggerFactory.getLogger("ERROR."
			+ NavigationOrchistration.class);

	// TODO

	@RequestMapping(value = "/admin/fileupload", method = RequestMethod.POST)
	public void fileupload(final @RequestParam MultipartFile[] files,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {

		final String realPath = request.getSession().getServletContext()
				.getRealPath("/upload");

		for (final MultipartFile file : files) {
			String originalFilename = file.getOriginalFilename();
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(
					realPath, originalFilename));
		}

		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(true);
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleException(final Exception exception,
			final HttpServletRequest request) {
		_LOGGER.error("Spring MVC processing error", exception);
		return exception.getMessage();
	}
}
