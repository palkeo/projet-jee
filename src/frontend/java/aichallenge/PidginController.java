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
public class PidginController
{
    @Autowired
    private PidginRepository repo;

    @RequestMapping("/users/list")
    public String usersList(Model model)
    {
	model.addAttribute("users", repo.findAll());

	return "usersList";
    }

    @RequestMapping(value="/inscription", method=RequestMethod.GET)
    public String inscription(Model model)
    {
	model.addAttribute("user", new Pidgin());
	return "inscription";
    }

    @RequestMapping(value="/inscription", method=RequestMethod.POST)
    public String inscriptionResult(@ModelAttribute Pidgin user, Model model)
    {
	boolean inscriptionSuccessful = true;
        ArrayList<String> errorMessages = new ArrayList<String>();

	if(user.getLogin().equals(""))
	{
            errorMessages.add("Le pseudo ne doit pas être vide.");
	    inscriptionSuccessful = false;
	}
	if(user.getFirstName().equals(""))
	{
            errorMessages.add("Le prénom ne doit pas être vide.");
	    inscriptionSuccessful = false;
	}
	if(user.getLastName().equals(""))
	{
            errorMessages.add("Le nom ne doit pas être vide.");
	    inscriptionSuccessful = false;
	}
	if(repo.findByLogin(user.getLogin()) != null)
	{
            errorMessages.add("Ce pseudo est déjà utilisé.");
	    inscriptionSuccessful = false;
	}

	if(!inscriptionSuccessful)
        {
            model.addAttribute("user", user);
	    model.addAttribute("errorMessages", errorMessages);
	    return "inscription";
        }

	repo.save(user);
        ArrayList<String> successMessages = new ArrayList<String>();
        successMessages.add("L'inscription s'est déroulée avec succès. " +
                            "Vous pouvez dès à présent vous connecter a" +
                            "vec les identifiants que vous avez choisis.");
        model.addAttribute("successMessages", successMessages);
	return "home";
    }
}
