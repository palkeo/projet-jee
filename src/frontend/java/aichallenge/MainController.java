package aichallenge;

import java.util.ArrayList;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.PageRequest;

@Controller
public class MainController
{
    @Autowired
    private PidginRepository pidginRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private PidginInfo pidginInfo;

    @ModelAttribute("currentUser")
    public Pidgin getUser()
    {
        return pidginInfo.getCurrentUser();
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String home(Model model)
    {
        model.addAttribute("games", gameRepo.findAll());
        model.addAttribute("matchs", matchRepo.findAll(new PageRequest(0, 10, new Sort(Direction.DESC, "id"))));
        return "home";
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public String home(
        @RequestParam("username") String login,
        @RequestParam("password") String password,
        Model model,
        RedirectAttributes redirectAttributes)
    {
        ArrayList<String> errorMessages = new ArrayList<String>();
        ArrayList<String> successMessages = new ArrayList<String>();

        Pidgin user = pidginRepo.findByLogin(login);
        if(user == null)
        {
            //todo:messages:errorMessages.add(messageSource.getMessage("unknown.user", new Object[]{login}, null));
            errorMessages.add("Utilisateur inconnu " + login + ".");
        }
        else if(!Pidgin.md5(password).equals(user.getEncryptedPassword()))
        {
            //todo:messages:errorMessages.add(messageSource.getMessage("incorrect.password", null, null));
            errorMessages.add("Mot de passe incorrect.");
        }
        else
        {
            pidginInfo.setCurrentUser(user);
            //todo:messages:successMessages.add(messageSource.getMessage("connected", null, null));
            successMessages.add("Vous êtes maintenant connectés.");
        }

        redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
        redirectAttributes.addFlashAttribute("successMessages", successMessages);

        return "redirect:/";
    }
}
