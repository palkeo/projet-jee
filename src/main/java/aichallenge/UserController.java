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

	if(user.getLogin().equals("") ||
	   user.getFirstName().equals("") ||
	   user.getLastName().equals("") ||
	   repo.findByLogin(user.getLogin()) != null)
	{
	    model.addAttribute("error", "inscription failed");
	    return "inscription";
	}

	repo.save(user);
	return "inscriptionSuccessful";
    }
}
