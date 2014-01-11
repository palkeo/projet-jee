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

@Controller
public class UserController
{
    @Autowired
    private UserRepository repo;

    @RequestMapping("/users/list")
    public String usersList(Model model)
    {
	model.addAttribute("users", repo.findAll());

	return "usersList";
    }

    @RequestMapping(value="/inscription", method=RequestMethod.GET)
    public String inscription(Model model)
    {
	model.addAttribute("user", new User());
	return "inscription";
    }

    @RequestMapping(value="/inscription", method=RequestMethod.POST)
    public String inscriptionResult(@ModelAttribute User user, Model model)
    {
	model.addAttribute("user", user);

	boolean inscriptionSuccessful = true;

	if(user.getLogin().equals(""))
	{
	    model.addAttribute("emptyLogin", true);
	    inscriptionSuccessful = false;
	}
	if(user.getFirstName().equals(""))
	{
	    model.addAttribute("emptyFirstName", true);
	    inscriptionSuccessful = false;
	}
	if(user.getLastName().equals(""))
	{
	    model.addAttribute("emptyLastName", true);
	    inscriptionSuccessful = false;
	}
	if(repo.findByLogin(user.getLogin()) != null)
	{
	    model.addAttribute("loginAlreadyUsed", true);
	    inscriptionSuccessful = false;
	}

	if(!inscriptionSuccessful)
	    return "inscription";

	repo.save(user);
	return "inscriptionSuccessful";
    }
}
