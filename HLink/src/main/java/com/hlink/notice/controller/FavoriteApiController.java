package com.hlink.notice.controller;

import com.hlink.notice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteApiController {

    private final FavoriteService favoriteService;


    @PostMapping("/toggle")
    public ResponseEntity<Boolean> toggle(@RequestParam Long noticeId) {
        boolean result = favoriteService.toggle(noticeId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ids")
    public Set<Long> idSet() {
        return favoriteService.idSet();
    }
}
