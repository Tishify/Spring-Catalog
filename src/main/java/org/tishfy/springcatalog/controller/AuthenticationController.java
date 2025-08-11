package org.tishfy.springcatalog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.tishfy.springcatalog.dto.AuthenticationRequest;
import org.tishfy.springcatalog.dto.AuthenticationResponse;
import org.tishfy.springcatalog.service.AuthenticationService;
import org.tishfy.springcatalog.security.JwtUtil;

@Controller
@RequestMapping("/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public String getAuthenticationPage(Model model,
                                        @CookieValue(value = "JWT", required = false) String jwt,
                                        HttpServletRequest request) {

        log.debug("GET /authentication called from: {}", request.getRemoteAddr());

        if (jwt != null && !jwt.trim().isEmpty() && jwtUtil.validateToken(jwt)) {
            log.debug("Valid JWT found, redirecting to /admin");
            return "redirect:/admin";
        }

        if (!model.containsAttribute("authRequest")) {
            model.addAttribute("authRequest", new AuthenticationRequest());
        }

        log.debug("Returning authentication page");
        return "authentication";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authenticateUserForm(@Valid @ModelAttribute("authRequest") AuthenticationRequest authRequest,
                                       BindingResult result,
                                       Model model,
                                       HttpServletResponse response) {

        log.debug("POST /authentication called with email: {}", authRequest.getEmail());

        if (result.hasErrors()) {
            log.debug("Validation errors: {}", result.getAllErrors());
            return "authentication";
        }

        try {
            AuthenticationResponse authResponse = authenticationService.authenticate(authRequest);

            if (!authResponse.isSuccess()) {
                log.debug("Authentication failed: {}", authResponse.getMessage());
                model.addAttribute("error", authResponse.getMessage());
                return "authentication";
            }

            Cookie cookie = new Cookie("JWT", authResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setSecure(false); // на продакшене - true с HTTPS
            cookie.setMaxAge(60 * 60); // 1 час
            response.addCookie(cookie);

            log.debug("Authentication successful for user: {}", authRequest.getEmail());
            return "redirect:/admin";

        } catch (Exception e) {
            log.error("Authentication error", e);
            model.addAttribute("error", "Authentication failed. Please try again.");
            return "authentication";
        }
    }

    // REST API для JSON (если нужно отдельно)
    @PostMapping(path = "/api/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AuthenticationResponse authenticateApi(@RequestBody @Valid AuthenticationRequest request) {
        log.debug("API authentication called for: {}", request.getEmail());
        return authenticationService.authenticate(request);
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        log.debug("Logout called");

        Cookie cookie = new Cookie("JWT", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();

        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully.");
        return "redirect:/authentication";
    }
}