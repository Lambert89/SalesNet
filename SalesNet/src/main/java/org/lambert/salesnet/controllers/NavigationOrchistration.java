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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.lambert.salesnet.beans.DocumentTracker;
import org.lambert.salesnet.beans.NewDocument;
import org.lambert.salesnet.beans.User;
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

    private static final Logger _LOGGER = LoggerFactory.getLogger("ERROR." + NavigationOrchistration.class);

    // TODO

    @RequestMapping(value = "/admin/fileupload", method = RequestMethod.POST)
    public void fileupload(final @RequestParam MultipartFile[] files, final HttpServletRequest request,
        final HttpServletResponse response) throws Exception {

        final String realPath = request.getSession().getServletContext().getRealPath("/upload");

        System.out.println("realPath: " + realPath);

        final List<NewDocument> newDocs = new ArrayList<NewDocument>();

        this.persistenceService.updateObjects(DocumentTracker.class, 1L, new PersistenceService.UpdateOperation<DocumentTracker>() {

            @Override
            public void update(final DocumentTracker docTracker) throws Exception {
                for (final MultipartFile file : files) {
                    String originalFilename = file.getOriginalFilename();
					System.out.println("originalFilename: " + originalFilename);
                    FileUtils.copyInputStreamToFile(file.getInputStream(), new File(realPath, originalFilename));
                    docTracker.addDocument(originalFilename, realPath + "/" + originalFilename);
                    final NewDocument newDoc = new NewDocument();
                    newDoc.setName(originalFilename);
                    newDoc.setUrl(NavigationOrchistration.this.baseUrl + "/api/download/" + originalFilename);
                    newDocs.add(newDoc);

                    System.out.println("after upload -> getAllDocumentNames: " + docTracker.getAllDocumentNames() + " with id: "
                        + docTracker.getId());
                }
            }

        });
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(true);

        this.persistenceService.saveObjects(newDocs.toArray());
    }

    @RequestMapping(value = "/documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Map<String, Object> getAllDocuments() throws Exception {
        final DocumentTracker docTracker = this.persistenceService.findObject(DocumentTracker.class, 1L);
        final List<String> docNames = docTracker.getAllDocumentNames();
        final Map<String, Object> result = new HashMap<String, Object>(2);
        result.put("documents", docNames);
        result.put("baseUrl", this.baseUrl);
        return result;
    }

    @RequestMapping(value = "/download/{documentName}", method = RequestMethod.GET)
    public void downloadDocument(@PathVariable final String documentName, final HttpServletResponse response) throws Exception {

        System.out.println("Testing downloadDocument DocumentName: " + documentName);

        final DocumentTracker docTracker = this.persistenceService.findObject(DocumentTracker.class, 1L);

        System.out.println("Testing downloadDocument docTraker: " + docTracker + " with id: " + docTracker.getId());

        System.out.println("during download -> getAllDocumentNames: " + docTracker.getAllDocumentNames());
        final String filename = documentName.endsWith(".pdf") ? documentName : documentName.concat(".pdf");

        final String path = docTracker.getDocumentLocation(filename);
        System.out.println("file path: " + path);
        final File file = new File(path);

        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            final OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            NavigationOrchistration._LOGGER.error("Error during downloading file {" + filename + "}.", ex);
            throw ex;
        }
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public @ResponseBody
    String subscribe(final String mailAddress) throws Throwable {
        String result = mailAddress;
        final User user = new User();
        user.setMail(mailAddress);
        try {
            this.persistenceService.saveObjects(new Object[] {mailAddress});
            final Map<String, Object> model = new HashMap<String, Object>();
            model.put("webUrl", this.baseUrl);
            this.mailService.sendSubscribeConfirmation(mailAddress, model);
        } catch (Throwable e) {
            result = "";
            throw e;
        }
        return result;
    }

    @RequestMapping(value = "/confirmUpdateNotification", method = RequestMethod.GET)
    public @ResponseBody
    String confirmUpdateNotification() throws MessagingException {
        final List<NewDocument> newDocs = this.persistenceService.queryObjects(NewDocument.class);

        if (newDocs.size() > 0) {
            final Map<String, String> newDocMap = new HashMap<String, String>();
            final long[] primaryKeys = new long[newDocs.size()];
            int i = 0;
            for (final NewDocument newDoc : newDocs) {
                newDocMap.put(newDoc.getName(), newDoc.getUrl());
                primaryKeys[i] = newDoc.getId();
                i++;
            }
            final Set<Map.Entry<String, String>> newEntries = newDocMap.entrySet();
            final Map<String, Object> templateParams = new HashMap<String, Object>();
            templateParams.put("newsList", newEntries);
            templateParams.put("webUrl", this.baseUrl);

            // TODO: to impl the real. stubbed for now.
            this.mailService.sendContentUpdateNotification("jason.lambert.89@gmail.com", templateParams);
            this.persistenceService.deleteObjects(NewDocument.class, primaryKeys);
        }

        return "success";
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    String handleException(final Exception exception, final HttpServletRequest request) {
        NavigationOrchistration._LOGGER.error("Spring MVC processing error", exception);
        exception.printStackTrace();
        return exception.getMessage();
    }
}
