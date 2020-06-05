package peter;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

public class Mailhandler {
    private Properties mailingproperites;
    private Session session;
    private String mailServerUser;
    private String mailserverPassword;
    private String mailserver;

    public Mailhandler(String server, String userName, String password) {
        mailserver = server;
        mailServerUser = userName;
        mailserverPassword = password;

        /**
         * Sets the properties for the mail-transport session.
         * Enables TLS
         * Sets port to g-mail smtp-port to 587
         * sets smtp-host for the receiving server
         * sets transport protocol to SMTP
         * enables smtp authentication
         */

        mailingproperites = System.getProperties();
        mailingproperites.put("mail.smtp.starttls.enable", "true");
        mailingproperites.put("mail.smtp.port", 587);
        mailingproperites.put("mail.smtp.host", server);
        mailingproperites.put("mail.transport.protocol", "smtp");
        mailingproperites.put("mail.smtp.auth", "true");

        /**
         * creates an password Athenticator for the SMTP Session
         */
       Authenticator auth = new SMTPAuthenticator();

        /**
         * Makes session for the transmisson using the properties set earlier
         * and applies authentication to the session
         */
        session = Session.getDefaultInstance(mailingproperites, auth);

    }

    /**
     * SMTP Authentication class to enable authentication for gmail mailserver.
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator{
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(mailServerUser, mailserverPassword);
        }
    }

    /**
     * Send the email to the mailserver
     * @param to - Emailrecipient
     * @param from - Sender of the email
     * @param subject - subject of the email
     * @param body - the body of the email being sent
     * @return - returns the result text of sending the email which will be passed to the player
     */
    public String send(String to, String from, String subject, String body)  {
        String resultText = "";

        String[] adresses = to.split(";");

        /**
         * create the message
         */
        MimeMessage message = new MimeMessage(session);
        try{
            for (String address : adresses)
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
            message.addFrom(InternetAddress.parse(from));
            message.setSubject(subject);
            message.setText(body);
        }catch (AddressException e){
            e.printStackTrace();
            resultText = resultText + "Invalid email address!\n";
            return resultText;
        } catch (MessagingException e) {
            e.printStackTrace();
            resultText = resultText + "Invalid email message text!\n";
            return resultText;
        }
        try{
            Transport transport = session.getTransport();
            transport.send(message);
            resultText = resultText + "Invitations was sent successfully!";
        }catch (MessagingException e){
            resultText = resultText + "Message Not sent successfully!!!";
        }
        return resultText;
    }

}

