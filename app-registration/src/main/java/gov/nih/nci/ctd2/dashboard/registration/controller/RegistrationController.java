package gov.nih.nci.ctd2.dashboard.registration.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    final static Logger logger = Logger.getLogger(RegistrationController.class.getName());

    @Autowired
    private String db_url;

    @Autowired
    private String db_username;

    @Autowired
    private String db_password;

    @Autowired
    private String emailer_username;
    @Autowired
    private String emailer_password;
    @Autowired
    private String reviewer_email;

    @Autowired
    private String grecaptcha_secret;

    /* retrieve PUBLISHED registration data */
    @RequestMapping(value = "data", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getData() {
        final String PUBLISHED = "published"; // registration status
        logger.log(Level.INFO, "request received to retrieve registration data");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (!load_db_driver()) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        String json = "{}";
        String sqlSelectAllPersons = "SELECT * FROM registration";

        try (Connection conn = DriverManager.getConnection(db_url, db_username, db_password);
                PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons)) {
            ResultSet rs = ps.executeQuery();

            StringBuffer sb = new StringBuffer("[");
            while (rs.next()) {
                String status = rs.getString("status");
                if (!PUBLISHED.equals(status)) {
                    continue;
                }
                if (sb.length()>1) { // not the first record
                    sb.append(",");
                }
                String app_code = rs.getString("app_code");
                String title = rs.getString("title");
                String url = rs.getString("url");
                String description = rs.getString("description");
                String developers = rs.getString("developers");
                String email = rs.getString("email");
                String institution = rs.getString("institution");
                String lab = rs.getString("lab");
                Blob image = rs.getBlob("image");

                JSONObject jo = new JSONObject();
                jo.put("app_code", app_code);
                jo.put("title", title);
                jo.put("url", url);
                jo.put("description", description);
                jo.put("developers", developers);
                jo.put("email", email);
                jo.put("institution", institution);
                jo.put("lab", lab);
                if (image != null) {
                    String imageURI = "/registration/registration/image/" + app_code;
                    jo.put("image", imageURI);
                }
                sb.append(jo.toString());
            }
            sb.append("]");
            json = sb.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "{app_code}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getRegistration(@PathVariable String app_code) {
        logger.log(Level.INFO, "request received for registration app_code " + app_code);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (!load_db_driver()) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        String json = "{}";
        String sqlSelectAllPersons = "SELECT * FROM registration WHERE app_code=?";

        try (Connection conn = DriverManager.getConnection(db_url, db_username, db_password);
                PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons)) {
            ps.setString(1, app_code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String url = rs.getString("url");
                String description = rs.getString("description");
                String developers = rs.getString("developers");
                String email = rs.getString("email");
                String institution = rs.getString("institution");
                String lab = rs.getString("lab");
                Blob image = rs.getBlob("image");

                JSONObject jo = new JSONObject();
                jo.put("app_code", app_code);
                jo.put("title", title);
                jo.put("url", url);
                jo.put("description", description);
                jo.put("developers", developers);
                jo.put("email", email);
                jo.put("institution", institution);
                jo.put("lab", lab);
                if (image != null) {
                    String encodedImage = Base64.getEncoder().encodeToString(image.getBytes(1l, (int) image.length()));
                    jo.put("image", encodedImage);
                }
                json = jo.toString();
            } else {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "image/{app_code}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable String app_code) {
        logger.log(Level.INFO, "retrieve image for registration app_code " + app_code);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-download");
        headers.add("Content-Disposition", "filename=" + app_code + ".jpg");
        if (!load_db_driver()) {
            return new ResponseEntity<byte[]>(headers, HttpStatus.NOT_FOUND);
        }

        byte[] bytes = null;
        try (Connection conn = DriverManager.getConnection(db_url, db_username, db_password);
                PreparedStatement ps = conn.prepareStatement("SELECT image FROM registration WHERE app_code=?")) {
            ps.setString(1, app_code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Blob image = rs.getBlob("image");
                if (image == null)
                    return new ResponseEntity<byte[]>(headers, HttpStatus.NOT_FOUND);
                bytes = image.getBytes(1l, (int) image.length());
            } else {
                return new ResponseEntity<byte[]>(headers, HttpStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(headers, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "register", method = { RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> register(@RequestParam("title") String title,
            @RequestParam("url") String url, @RequestParam("description") String description,
            @RequestParam("developers") String developers, @RequestParam("email") String email,
            @RequestParam("institution") String institution, @RequestParam("lab") String lab,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (!load_db_driver()) {
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // safeguard of size limit although they should have been check
        title = sizeLimit(title, TITLE_LIMIT);
        url = sizeLimit(url, URL_LIMIT);
        description = sizeLimit(description, DESCRIPTION_LIMIT);
        developers = sizeLimit(developers, DEVELOPERS_LIMIT);
        email = sizeLimit(email, EMAIL_LIMIT);
        institution = sizeLimit(institution, INSTITUTION_LIMIT);
        lab = sizeLimit(lab, LAB_LIMIT);

        String app_code = unique_code();

        String sql = "INSERT INTO registration VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(db_url, db_username, db_password);
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, app_code);
            ps.setString(2, title);
            ps.setString(3, url);
            ps.setString(4, description);
            ps.setString(5, developers);
            ps.setString(6, email);
            ps.setString(7, institution);
            ps.setString(8, lab);
            ps.setString(10, "pending");

            if (image != null) {
                String filename = image.getOriginalFilename();
                logger.log(Level.INFO, "image=" + image + "[]");
                logger.log(Level.INFO, filename);

                ps.setBytes(9, image.getBytes());
            } else {
                logger.log(Level.INFO, "no image uploaded");
                ps.setBytes(9, null);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // email the registration applicant and the admin
        String host = "smtp.gmail.com";
        String port = "465";

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailer_username, emailer_password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            // message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Code for your CTD2 Dashboard application");

            String msg = "Dear Developer, thank you for registering your application with the CTD2 Dashboard. "
                    + "We are in the process of reviewing your application; we will notify you when it has been published. "
                    + "This is your unique application code: <p><b>" + app_code
                    + "</b></p>You can use it at any time to edit the application information you submitted, "
                    + "by revisiting the application registration <a href='https://ctd2-dashboard.nci.nih.gov/dashboard/'>web page</a>."
                    + "<p>Sincerely,<p>The CTD2 Dashboard team";

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            Transport.send(message);

            message = new MimeMessage(session);
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(reviewer_email));
            message.setSubject("New CTD2 Dashboard application registration");
            message.setText("=== new registration details ===\napplication title: " + title + "\nURL: " + url
                    + "\napplication description: " + description + "\ndeveloper name: " + developers
                    + "\ncontact email: " + email + "\ninstitution: " + institution + "\nlab name: " + lab + "\nimage: "
                    + image);
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("{\"app_code\":\"" + app_code + "\"}", headers, HttpStatus.OK);
    }

    static private boolean load_db_driver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, "Error: unable to load driver class!");
            return false;
        }
        return true;
    }

    static private String unique_code() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        return sb.append(simpleDateFormat.format(new Date())).toString();
    }

    @RequestMapping(value = "update", method = { RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> update(@RequestParam("app_code") String app_code,
            @RequestParam("title") String title,
            @RequestParam("url") String url, @RequestParam("description") String description,
            @RequestParam("developers") String developers, @RequestParam("email") String email,
            @RequestParam("institution") String institution, @RequestParam("lab") String lab,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (!load_db_driver()) {
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // safeguard of size limit although they should have been check
        title = sizeLimit(title, TITLE_LIMIT);
        url = sizeLimit(url, URL_LIMIT);
        description = sizeLimit(description, DESCRIPTION_LIMIT);
        developers = sizeLimit(developers, DEVELOPERS_LIMIT);
        email = sizeLimit(email, EMAIL_LIMIT);
        institution = sizeLimit(institution, INSTITUTION_LIMIT);
        lab = sizeLimit(lab, LAB_LIMIT);

        String sql = "UPDATE registration SET title=?, url=?, description=?, developers=?, email=?, institution=?, lab=?, image=? WHERE app_code=?";

        if (image == null) {
            sql = "UPDATE registration SET title=?, url=?, description=?, developers=?, email=?, institution=?, lab=? WHERE app_code=?";
        }

        try (Connection conn = DriverManager.getConnection(db_url, db_username, db_password);
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, url);
            ps.setString(3, description);
            ps.setString(4, developers);
            ps.setString(5, email);
            ps.setString(6, institution);
            ps.setString(7, lab);

            if (image != null) {
                String filename = image.getOriginalFilename();
                logger.log(Level.INFO, "image=" + image + "[]");
                logger.log(Level.INFO, filename);

                byte[] bytes = image.getBytes();
                ps.setBytes(8, bytes);
                logger.log(Level.INFO, "image size " + bytes.length);
                ps.setString(9, app_code);
            } else {
                logger.log(Level.INFO, "no image uploaded");
                ps.setString(8, app_code);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("{\"app_code\":\"" + app_code + "\"}", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "recaptcha", method = { RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> recaptcha(@RequestParam("recaptcha_response") String recaptcha_response) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        HttpClient httpClient = HttpClient.newBuilder().build();
        String x = "";
        try {
            URI uri = URI.create("https://www.google.com/recaptcha/api/siteverify");
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .headers("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers
                            .ofString("secret=" + grecaptcha_secret + "&response=" + recaptcha_response))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jo = new JSONObject(response.body());
            logger.log(Level.FINE, jo.toString());
            logger.log(Level.FINE, jo.get("success").toString());
            x = response.body(); // simply pass along to the client for now. but we could do more with it if
                                 // needed
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(x, headers, HttpStatus.OK);
    }

    private static String sizeLimit(String s, int limit) {
        if (s.length() <= limit)
            return s;
        else
            return s.substring(0, limit);
    }

    /* size limit. should match DB schema */
    private static int TITLE_LIMIT = 30;
    private static int URL_LIMIT = 100;
    private static int DESCRIPTION_LIMIT = 500;
    private static int DEVELOPERS_LIMIT = 100;
    private static int EMAIL_LIMIT = 50;
    private static int INSTITUTION_LIMIT = 50;
    private static int LAB_LIMIT = 100;
}
