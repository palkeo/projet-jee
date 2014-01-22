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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GameController
{
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private AIRepository aiRepo;
    @Autowired
    MatchRepository matchRepo;

    @Autowired
    private PidginInfo pidginInfo;

    @ModelAttribute("currentUser")
    public Pidgin getUser()
    {
        return pidginInfo.getCurrentUser();
    }

    @RequestMapping("/games/list")
    public String gamesList(Model model)
    {
        model.addAttribute("games", gameRepo.findAll());
        return "gamesList";
    }

    @RequestMapping(value="/games/{gameId}", method=RequestMethod.GET)
    public String gameDisplay(@PathVariable Long gameId, Model model)
    {
        model.addAttribute("game", gameRepo.findById(gameId));

        if(pidginInfo.getCurrentUser() != null)
        {
            long userId = pidginInfo.getCurrentUser().getId();
            model.addAttribute("allAI", aiRepo.findByGameId(gameId));
            model.addAttribute("userAI", aiRepo.findByGameIdAndPidginId(gameId, userId));
        }

        return "game";
    }

    @RequestMapping(value="/games/{gameId}", method=RequestMethod.POST)
    public String gameDisplay(
        @PathVariable Long gameId,
        Model model,
        @RequestParam("ai1") long ai1Id,
        @RequestParam("ai2") long ai2Id)
    {
        Game game = gameRepo.findById(gameId);
        AI ai1 = aiRepo.findById(ai1Id);
        AI ai2 = aiRepo.findById(ai2Id);;

        model.addAttribute("game", game);

        ArrayList<String> successMessages = new ArrayList<String>();

        Match m = new Match(game, ai1, ai2, new java.util.Date());
        m = matchRepo.save(m);

        successMessages.add("Votre demande de match a été correctement enregistrée. Elle sera traitée dès qu'un de nos esclaves sera libre. Vous pouvez voir le statut de votre match <a href=\"/matches/" + m.getId() + "\">ici</a>.");

        model.addAttribute("successMessages", successMessages);

        if(pidginInfo.getCurrentUser() != null)
        {
            long userId = pidginInfo.getCurrentUser().getId();
            model.addAttribute("allAI", aiRepo.findByGameId(gameId));
            model.addAttribute("userAI", aiRepo.findByGameIdAndPidginId(gameId, userId));
        }

        return "game";
    }
}
