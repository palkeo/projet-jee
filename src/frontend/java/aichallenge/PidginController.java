package aichallenge;

import java.util.ArrayList;
import java.util.HashMap;
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
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private AIRepository aiRepo;

    @ModelAttribute("pidginInfo")
    public PidginInfo getPidginInfo(){ return pidginInfo; }

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
        model.addAttribute("user", new RegisteringPidgin());
        return "inscription";
    }

    @InitBinder("user")
    protected void initBinder(WebDataBinder binder)
    {
        binder.setValidator(new RegisteringPidginValidator(repo));
    }

    @RequestMapping(value="/inscription", method=RequestMethod.POST)
    public String inscriptionResult(
        @ModelAttribute("user") @Valid RegisteringPidgin user,
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

    @RequestMapping(value="/user/ais", method=RequestMethod.GET)
    public String inscriptionResult(
        Model model,
        RedirectAttributes redirectAttributes)
    {
        Pidgin user = getUser();

        ArrayList<String> errorMessages = new ArrayList<String>();

        if(user != null) {
            HashMap<Game, List<AI>> perGame = new HashMap<Game, List<AI>>();

            for(Game game : gameRepo.findAll()) {
                List<AI> ais = aiRepo.findByGameIdAndPidginId(game.getId(), user.getId());

                if(!ais.isEmpty()) {
                    perGame.put(game, ais);
                }
            }

            if(perGame.isEmpty()) {
                errorMessages.add("Vous n'avez aucune AI.");
            }

            model.addAttribute("perGame", perGame);
            model.addAttribute("errorMessages", errorMessages);

            return "userAIList";
        }
        else {
            errorMessages.add("Vous devez être connectés pour accéder à cette page.");
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/";
        }
    }

    // FIXME: There is a CSRF here. Fix only if you have time, it's not important at all for such a project...
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
