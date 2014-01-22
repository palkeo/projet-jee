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

    @RequestMapping("/games/{gameId}")
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

}
