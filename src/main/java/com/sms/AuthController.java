package com.sms;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AdminRepository admins;
    private final PasswordEncoder encoder;

    public AuthController(AdminRepository admins, PasswordEncoder encoder) {
        this.admins = admins;
        this.encoder = encoder;
    }

    public record LoginRequest(String username, String password) { }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest req, HttpSession session) {
        if (req.username() == null || req.password() == null) {
            return unauthorized();
        }
        return admins.findByUsername(req.username())
                .filter(a -> encoder.matches(req.password(), a.getPasswordHash()))
                .map(a -> {
                    session.setAttribute("admin", a.getUsername());   // remember the login in the session
                    return ResponseEntity.ok(Map.of("username", a.getUsername()));
                })
                .orElseGet(AuthController::unauthorized);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession session) {
        session.invalidate();
    }

    // The page calls this on load to find out whether it is still signed in
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(HttpSession session) {
        Object user = session.getAttribute("admin");
        if (user == null) {
            return unauthorized();
        }
        return ResponseEntity.ok(Map.of("username", user.toString()));
    }

    private static ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Wrong username or password. Try admin / admin123."));
    }
}
