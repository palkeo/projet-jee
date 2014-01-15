package aichallenge;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class PidginInfo
{
    private Pidgin currentUser;

    public PidginInfo(){}

    @ModelAttribute("currentUser")
    public Pidgin getCurrentUser(){ return currentUser; }
    public void setCurrentUser(Pidgin user){ this.currentUser = user; }
}
