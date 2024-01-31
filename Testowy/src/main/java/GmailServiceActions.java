import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GmailServiceActions {
    private final String APPLICATION_NAME = " Test ";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private String TOKENS_DIRECTORY_PATH;
    private List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM);
    private String CREDENTIALS_FILE_PATH;

    public GmailServiceActions() {
        TOKENS_DIRECTORY_PATH = System.getProperty("user.dir") +
                File.separator + "src" +
                File.separator + "main" +
                File.separator + "resources" +
                File.separator + "credential";
        CREDENTIALS_FILE_PATH = System.getProperty("user.dir") +
                File.separator + "src" +
                File.separator + "main" +
                File.separator + "resources" +
                File.separator + "credential" +
                File.separator + "credentials.json";
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String EmailsBody() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            ListMessagesResponse response = service.users().messages()
                    .list("me")
                    .setQ("from:" + "automat@nazwa.pl")
                    .execute();

            String messageid = response.getMessages().get(0).getId();
            Message message = service.users().messages().get("me", messageid).execute();

            String emailBody = "";
            if (message.getPayload().getMimeType().equals("multipart/signed")) {
                List<MessagePart> parts = message.getPayload().getParts();
                for (MessagePart part : parts) {
                    if (part.getMimeType().equals("multipart/alternative")) {
                        List<MessagePart> innerParts = part.getParts();
                        for (MessagePart innerPart : innerParts) {
                            if (innerPart.getMimeType().equals("text/plain") || innerPart.getMimeType().equals("text/html")) {
                                emailBody = StringUtils.newStringUtf8(Base64.decodeBase64(innerPart.getBody().getData()));
                                break;
                            }
                        }
                    }
                }
            }
            String otp = extractVerificationCode(emailBody);
            return otp;
        } catch (Exception e) {
            System.out.println("Exception log: " + e);
        }
        return null;
    }

    public String extractVerificationCode(String messageBody) {
        String regex = "\\b\\d{6}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageBody);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}