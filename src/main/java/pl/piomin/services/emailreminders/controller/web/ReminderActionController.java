package pl.piomin.services.emailreminders.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.piomin.services.emailreminders.exception.InvalidTokenException;
import pl.piomin.services.emailreminders.service.ActionTokenService;

@Controller
public class ReminderActionController {

    private final ActionTokenService actionTokenService;

    public ReminderActionController(ActionTokenService actionTokenService) {
        this.actionTokenService = actionTokenService;
    }

    @GetMapping("/actions/{token}")
    public String executeAction(@PathVariable String token, Model model) {
        try {
            ActionTokenService.ActionResult result = actionTokenService.executeAction(token);

            model.addAttribute("success", true);
            model.addAttribute("eventTitle", result.eventTitle());
            model.addAttribute("message", result.message());
            model.addAttribute("actionType", result.actionType());
            model.addAttribute("pageTitle", "Action Completed");
        } catch (InvalidTokenException e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "This link is invalid or has expired. Please check your email for a new link.");
            model.addAttribute("pageTitle", "Invalid Link");
        }

        return "action-result";
    }
}
