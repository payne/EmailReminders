package pl.piomin.services.emailreminders.controller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.piomin.services.emailreminders.dto.request.MagicLinkRequest;
import pl.piomin.services.emailreminders.dto.request.RegisterRequest;
import pl.piomin.services.emailreminders.exception.DuplicateResourceException;
import pl.piomin.services.emailreminders.exception.InvalidTokenException;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.security.UserPrincipal;
import pl.piomin.services.emailreminders.service.AuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class WebAuthController {

    private final AuthService authService;

    public WebAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, RedirectAttributes redirectAttributes) {
        MagicLinkRequest request = new MagicLinkRequest(email);
        authService.sendMagicLink(request);
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/check-email";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("pageTitle", "Register");
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String email,
                                  @RequestParam(required = false) String displayName,
                                  RedirectAttributes redirectAttributes) {
        try {
            RegisterRequest request = new RegisterRequest(email, displayName);
            authService.registerUser(request);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/check-email";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An account with this email already exists");
            return "redirect:/register";
        }
    }

    @GetMapping("/check-email")
    public String showCheckEmailPage(Model model) {
        model.addAttribute("pageTitle", "Check Your Email");
        return "auth/check-email";
    }

    @GetMapping("/auth/verify")
    public String verifyMagicLink(@RequestParam String token,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = authService.getUserFromMagicLink(token);

            // Create authentication and set in security context
            UserPrincipal principal = new UserPrincipal(user);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Store in session
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            redirectAttributes.addFlashAttribute("successMessage", "Welcome back!");
            return "redirect:/dashboard";
        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired link. Please request a new one.");
            return "redirect:/login";
        }
    }
}
