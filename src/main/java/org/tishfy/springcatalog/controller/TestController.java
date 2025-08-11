package org.tishfy.springcatalog.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestController {

    @GetMapping("/")
    public String home() {
        return "redirect:/authentication";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        model.addAttribute("message", "Welcome to Admin Dashboard!");
        return "admin-dashboard";
    }

    @GetMapping("/admin/test")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> adminTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Admin endpoint working!");
        response.put("timestamp", new Date());
        return response;
    }

    @GetMapping("/health")
    @ResponseBody
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", new Date().toString());
        return status;
    }
}