package sumdu.edu.ua.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.service.RegistrationService;

@Controller
public class AuthController {
    private final RegistrationService registration;

    public AuthController(RegistrationService registration) {
        this.registration = registration;
    }

    @GetMapping("/login")
    String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    String register(@RequestParam("username") String username,
                    @RequestParam("password") String password, Model model) {
        try {
            registration.register(username, password);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException | ConflictException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "auth/register";
        }
    }

    @RequestMapping("/403")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String forbidden() {
        return "error/403";
    }
}
