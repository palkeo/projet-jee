package aichallenge;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String login;
    private String encryptedPassword;
    private String firstName;
    private String lastName;
    private boolean admin;

    protected User(){}
    public User(String login, String password, String firstName, String lastName, boolean admin)
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
	return "User(" + this.login + ", " + this.encryptedPassword + ", " + this.firstName + ", " + this.lastName + ", " + (this.admin? "admin": "normal user") + ")";
    }

    public String getLogin(){ return this.login; }
    public String getEncryptedPassword(){ return this.encryptedPassword; }
    public String getFirstName(){ return this.firstName; }
    public String getLastName(){ return this.lastName; }
    public boolean isAdmin(){ return this.admin; }

    /* Returns the md5 encryption of s */
    private String md5(String s)
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
