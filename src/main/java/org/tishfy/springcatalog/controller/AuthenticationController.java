package org.tishfy.springcatalog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public String getAuthenticationPage(Model model, @CookieValue(value = "JWT", required = false) String jwt) {
        if (jwt != null && jwtUtil.validateToken(jwt)) {
            return "redirect:/admin";
        }
        model.addAttribute("authRequest", new AuthenticationRequest());
        return "authentication";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authenticateUserForm(@Valid @ModelAttribute("authRequest") AuthenticationRequest authRequest,
                                       BindingResult result,
                                       Model model,
                                       HttpServletResponse response,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "authentication";
        }

        AuthenticationResponse authResponse = authenticationService.authenticate(authRequest);
        if (!authResponse.isSuccess()) {
            model.addAttribute("error", authResponse.getMessage());
            return "authentication";
        }

        Cookie cookie = new Cookie("JWT", authResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // secure = true в prod при https
        cookie.setSecure(false);
        cookie.setMaxAge(60 * 60); // 1 час
        response.addCookie(cookie);

        return "redirect:/admin";
    }

    // REST login (возвращает token JSON) — для AJAX / мобильных клиентов
    @PostMapping(path = "/api/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AuthenticationResponse authenticateApi(@RequestBody @Valid AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        // Удаляем cookie
        Cookie cookie = new Cookie("JWT", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully.");
        return "redirect:/authentication";
    }
}