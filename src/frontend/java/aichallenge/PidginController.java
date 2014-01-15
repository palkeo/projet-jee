package aichallenge;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.WebDataBinder;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

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
	model.addAttribute("user", new RegistringUser());
	return "inscription";
    }

    @InitBinder("user")
    protected void initBinder(WebDataBinder binder)
    {
        binder.setValidator(new RegistringUserValidator());
    }

    @RequestMapping(value="/inscription", method=RequestMethod.POST)
    public String inscriptionResult(@ModelAttribute("user") @Valid RegistringUser user, BindingResult result, Model model)
    {
        ArrayList<String> errorMessages = new ArrayList<String>();

        if(result.hasErrors()) {
            errorMessages.add(result.getAllErrors().toString());
        }
	if(repo.findByLogin(user.getLogin()) != null)
	{
            errorMessages.add("Ce pseudo est déjà utilisé.");
        }

        if(errorMessages.isEmpty()) {
            repo.save(user.toPidgin());

            ArrayList<String> successMessages = new ArrayList<String>();
            successMessages.add("L'inscription s'est déroulée avec succès. " +
                                "Vous pouvez dès à présent vous connecter a" +
                                "vec les identifiants que vous avez choisis.");
            model.addAttribute("successMessages", successMessages);
            return "home";
        }
        else {
            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("user", user);
            return "inscription";
        }
    }
}

