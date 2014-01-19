package aichallenge;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadController
{
    @Autowired
    private PidginRepository repo;

    @Autowired
    private PidginInfo pidginInfo;

    @Autowired
    private GameRepository gameRepo;

    @ModelAttribute("currentUser")
    public Pidgin getUser()
    {
        return pidginInfo.getCurrentUser();
    }

    /*TODO
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String invalidSize(MaxUploadSizeExceededException e)
    {
        return "redirect:/games/upload";
    }
    */

    @RequestMapping(value="/games/upload", method=RequestMethod.GET)
    public String getUpload(
        Model model,
        RedirectAttributes redirectAttributes)
    {
        if(getUser() == null) {
            ArrayList<String> errorMessages = new ArrayList<String>();
            errorMessages.add("Vous devez être connecté pour uploader un fichier.\nSi vous n'êtes pas inscrit, vous pouvez le faire ci-dessous.");
            redirectAttributes.addFlashAttribute("error_messages", errorMessages);

            return "redirect:/inscription";
        }
        else {
            model.addAttribute("games", gameRepo.findAll());
            return "upload";
        }
    }

    @RequestMapping(value="/games/upload", method=RequestMethod.POST)
    public String postUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("game") String game,
        Model model,
        RedirectAttributes redirectAttributes)
    {
        return "redirect:/home";
    }
}

