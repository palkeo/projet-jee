package aichallenge;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PidginController
{
    @Autowired
    private PidginRepository repo;

    @Autowired
    private PidginInfo pidginInfo;

    @ModelAttribute("currentUser")
    public Pidgin getUser()
    {
        return pidginInfo.getCurrentUser();
    }

    @RequestMapping("/users/list")
    public String usersList(Model model)
    {
        model.addAttribute("users", repo.findAll());

        return "usersList";
    }

    @RequestMapping(value="/inscription", method=RequestMethod.GET)
    public String inscription(Model model)
    {
        model.addAttribute("user", new RegisteringUser());
        return "inscription";
    }

    @InitBinder("user")
    protected void initBinder(WebDataBinder binder)
    {
        binder.setValidator(new RegisteringUserValidator(repo));
    }

    @RequestMapping(value="/inscription", method=RequestMethod.POST)
    public String inscriptionResult(
        @ModelAttribute("user") @Valid RegisteringUser user,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes)
    {
        if(!result.hasErrors())
        {
            repo.save(user.toPidgin());

            ArrayList<String> successMessages = new ArrayList<String>();
            successMessages.add("L'inscription s'est déroulée avec succès. " +
                                "Vous pouvez dès à présent vous connecter a" +
                                "vec les identifiants que vous avez choisis.");
            redirectAttributes.addFlashAttribute("successMessages", successMessages);

            return "redirect:/";
        }
        else
        {
            model.addAttribute("user", user);
            return "inscription";
        }
    }

    @RequestMapping("/logout")
    public String logout(
        Model model,
        HttpSession session,
        RedirectAttributes redirectAttributes)
    {
        session.invalidate();

        System.out.println(model.asMap().size());

        ArrayList<String> successMessages = new ArrayList<String>();
        successMessages.add("Vous êtes maintenant déconnecté.");
        redirectAttributes.addFlashAttribute("successMessages", successMessages);

        return "redirect:/";
    }

}
