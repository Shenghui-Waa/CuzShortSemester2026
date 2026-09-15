package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

}
