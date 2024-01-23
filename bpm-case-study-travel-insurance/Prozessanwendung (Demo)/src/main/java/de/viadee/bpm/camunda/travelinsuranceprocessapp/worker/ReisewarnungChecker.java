package your.package;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class ReisewarnungChecker {

    @JobWorker(type = "check-travel-warning")
    public void checkTravelWarning(final JobClient client, final ActivatedJob job) {
        String warnings = fetchTravelWarnings();

        if (!warnings.isEmpty()) {
            sendEmail("customer@example.com", warnings);
        }

        client.newCompleteCommand(job.getKey()).send().join();
    }

    private String fetchTravelWarnings() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://travelwarning.api.bund.dev/");
            String response = EntityUtils.toString(httpClient.execute(request).getEntity());

            return parseWarnings(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String parseWarnings(String jsonResponse) {
        return ""; 
    }

    private void sendEmail(String recipient, String content) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.example.com");

        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("your-email@example.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Travel Warning Alert");
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
