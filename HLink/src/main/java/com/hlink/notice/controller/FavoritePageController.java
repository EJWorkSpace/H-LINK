package com.hlink.notice.controller;

import com.hlink.notice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FavoritePageController {
    private final FavoriteService favoriteService;

    @GetMapping("/favorites")
    public String favoritesPage(Model model) {
        model.addAttribute("pageTitle", "즐겨찾기");
        model.addAttribute("notices", favoriteService.favoriteNotices());
        model.addAttribute("activeTab", "favorites");
        return "favorites";
    }
}