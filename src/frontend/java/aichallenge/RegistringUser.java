package aichallenge;

import javax.validation.constraints.NotNull;

public class RegistringUser {
    @NotNull private String login;
    @NotNull private String password;
    @NotNull private String confirmation;
    @NotNull private String firstName;
    @NotNull private String lastName;

    protected RegistringUser() {}

    public RegistringUser(String login, String password, String confirmation, String firstName, String lastName) {
	this.login = login;
	this.password = password;
	this.confirmation = confirmation;
	this.firstName = firstName;
	this.lastName = lastName;
    }

    public String getLogin()        { return this.login; }
    public String getPassword()     { return this.password; }
    public String getConfirmation() { return this.confirmation; }
    public String getFirstName()    { return this.firstName; }
    public String getLastName()     { return this.lastName; }

    public void setLogin(String login)           { this.login = login; }
    public void setPassword(String password)     { this.password = password; }
    public void setConfirmation(String password) { this.confirmation = confirmation; }
    public void setFirstName(String firstName)   { this.firstName = firstName; }
    public void setLastName(String lastName)     { this.lastName = lastName; }

    public Pidgin toPidgin() {
        return new Pidgin(login, password, firstName, lastName, false);
    }
}
