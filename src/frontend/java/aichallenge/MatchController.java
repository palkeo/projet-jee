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
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MatchController
{
    @Autowired
    private MatchRepository repo;
    @Autowired
    private PidginInfo pidginInfo;

    @ModelAttribute("currentUser")
    public Pidgin getUser()
    {
        return pidginInfo.getCurrentUser();
    }

    @RequestMapping("/matchs/list")
    public String matchsList(Model model)
    {
        model.addAttribute("matchs", repo.findAll());

        return "matchsList";
    }

    @RequestMapping("/matchs/{id}")
    public String matchDisplay(@PathVariable Long id, Model model)
    {
        Match match = repo.findById(id);
        model.addAttribute("match", match);

        // Warning : Quick n'dirty. Feel free to improve. Or not :D
        String javascript = "var turns = [";
        for(Turn i : match.getTurns())
        {
            javascript = javascript + i.getState() + ",";
        }
        javascript = javascript + "];";

        model.addAttribute("javascript", javascript);

        return "match";
    }

}
