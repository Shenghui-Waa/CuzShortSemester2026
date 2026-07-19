package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 获取公告列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 公告列表
     */
    @GetMapping
    public Result<?> getAnnouncement(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return announcementService.getAllAnnouncement(page, pageSize);
    }

    /**
     * 查看公告详情
     * @param id 公告 ID
     * @return 公告详情
     */
    @GetMapping("/{id}")
    public Result<?> getAnnouncementInfo(
            @PathVariable Long id
    ) {
        return announcementService.getAnnouncementInfo(id);
    }


}
