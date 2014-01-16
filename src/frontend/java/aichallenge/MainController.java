package aichallenge;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;

@Controller
public class MainController
{
    @Autowired
    private PidginRepository repo;
    @Autowired
    private PidginInfo pidginInfo;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String home(Model model)
    {
        return "home";
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public String home(
        @RequestParam("username") String login,
        @RequestParam("password") String password,
        Model model)
    {
        ArrayList<String> errorMessages = new ArrayList<String>();
        ArrayList<String> successMessages = new ArrayList<String>();

        Pidgin user = repo.findByLogin(login);
        if(user == null)
            errorMessages.add("Cet utilisateur n'existe pas.");
        else if(!Pidgin.md5(password).equals(user.getEncryptedPassword()))
            errorMessages.add("Le mot de passe que vous avez entré est incorrect.");
        else
        {
            pidginInfo.setCurrentUser(user);
            successMessages.add("Vous êtes maintenant connecté en tant que " + login + ".");
        }

        model.addAttribute("errorMessages", errorMessages);
        model.addAttribute("successMessages", successMessages);

        return "home";
    }
}
