package aichallenge;

import java.lang.IllegalStateException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Autowired
    private AIRepository aiRepo;

    @ModelAttribute("currentUser")
    public Pidgin getUser()
    {
        return pidginInfo.getCurrentUser();
    }

    // TODO: does not work
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String invalidSize(MaxUploadSizeExceededException e)
    {
        return "redirect:/ai/upload";
    }

    @RequestMapping(value="/ai/upload", method=RequestMethod.GET)
    public String getUpload(
        Model model,
        RedirectAttributes redirectAttributes)
    {
        if(getUser() == null) {
            ArrayList<String> errorMessages = new ArrayList<String>();
            errorMessages.add("Vous devez être connecté pour uploader un fichier.\nSi vous n'êtes pas inscrit, vous pouvez le faire ci-dessous.");
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);

            return "redirect:/inscription";
        }
        else {
            model.addAttribute("maxUploadSize", Application.maxUploadSize);
            model.addAttribute("games", gameRepo.findAll());
            return "upload";
        }
    }

    @RequestMapping(value="/ai/upload", method=RequestMethod.POST)
    public String postUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("game") long gameId,
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        Model model,
        RedirectAttributes redirectAttributes)
    {
        // TODO: maybe check whether the archive is correct to repport to the user
        String targetDirectory = "./archives";
        String targetFile = getUser().getLogin() + "-" + file.getOriginalFilename();
        String target = targetDirectory + "/" + targetFile;

        try {
            file.transferTo(new File(target));
        }
        catch(IOException | IllegalStateException e) {
            e.printStackTrace();

            ArrayList<String> errorMessages = new ArrayList<String>();
            errorMessages.add("Une erreur est survenue. Peut-être qu'en réessayant vous aurez plus de chance. Ou pas.");
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);

            return "redirect:/ai/upload";
        }

        AI ai = new AI(name, targetFile, description, pidginInfo.getCurrentUser(), gameRepo.findById(gameId));
        ai = aiRepo.save(ai);

        ArrayList<String> successMessages = new ArrayList<String>();
        successMessages.add("Votre IA a été uploadée correctement. Vous pouvez désormais voir ses propriétés <a href=\"/ai/" + ai.getId() + "\">ici</a>.");
        redirectAttributes.addFlashAttribute("successMessages", successMessages);

        return "redirect:/";
    }
}
