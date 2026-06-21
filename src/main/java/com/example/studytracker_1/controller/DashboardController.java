package com.example.studytracker_1.controller;

import com.example.studytracker_1.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    @GetMapping
    public String showDashboard(Model model, Principal principal) {
        String username = principal.getName();
        System.out.println("=== Dashboard for user: " + username + " ===");

        Map<String, Object> stats = dashboardService.getStatsForUser(username);
        System.out.println("Stats data: " + stats);

        model.addAttribute("stats", stats);
        return "dashboard";
    }
}