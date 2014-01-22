package aichallenge;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pidgin
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String login;
    private String encryptedPassword;
    private String firstName;
    private String lastName;
    private boolean admin;

    protected Pidgin()
    {
        this.login = "";
        this.encryptedPassword = "";
        this.firstName = "";
        this.lastName = "";
        this.admin = false;
    }

    public Pidgin(String login, String password, String firstName, String lastName, boolean admin)
    {
        this.login = login;
        this.encryptedPassword = this.md5(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.admin = admin;
    }

    @Override
    public String toString()
    {
        return "Pidgin(" + this.login + ", " + this.encryptedPassword + ", " + this.firstName + ", " + this.lastName + ", " + (this.admin? "admin": "normal pidgin") + ")";
    }

    public long getId(){ return this.id; }
    public String getLogin(){ return this.login; }
    public String getEncryptedPassword(){ return this.encryptedPassword; }
    public String getFirstName(){ return this.firstName; }
    public String getLastName(){ return this.lastName; }
    public boolean isAdmin(){ return this.admin; }

    public void setLogin(String login){ this.login = login; }
    public void setEncryptedPassword(String password){ this.encryptedPassword = md5(password); }
    public void setFirstName(String firstName){ this.firstName = firstName; }
    public void setLastName(String lastName){ this.lastName = lastName; }
    public void setAdmin(boolean admin){ this.admin = admin; }

    /* Returns the md5 encryption of s */
    public static String md5(String s)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<array.length; ++i)
                sb.append(Integer.toHexString((array[i] & 0xff) | 0x100).substring(1, 3));

            return sb.toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println("User.md5: this exception should never be raised!");
            System.out.println(e.getMessage());
        }

        return null;
    }
}
