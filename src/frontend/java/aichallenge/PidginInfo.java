package aichallenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class PidginInfo
{
    private Pidgin currentUser;

    @Autowired
    private AIRepository aiRepo;

    public PidginInfo(){}

    @ModelAttribute("currentUser")
    public Pidgin getCurrentUser(){ return currentUser; }
    public void setCurrentUser(Pidgin user){ this.currentUser = user; }
    public int getNbAI(long id) { return aiRepo.findByPidginId(id).size(); }
}
