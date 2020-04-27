package peter;

//import com.mysql.cj.Session;
import javax.mail.Session;


import java.util.Properties;

public class Mailhandler {
    private Properties mailingproperites;
    private Session session;
    private String mailServerUser;
    private String mailserverPassword;
    private String mailserver;

    public Mailhandler(String server, String userName, String password){
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
        //mailingproperites.put("mail.smtp.")

    }
}
