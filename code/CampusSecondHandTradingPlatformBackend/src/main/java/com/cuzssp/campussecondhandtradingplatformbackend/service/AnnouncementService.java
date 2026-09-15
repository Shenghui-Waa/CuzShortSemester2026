package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Announcement;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.AnnouncementVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;

public interface AnnouncementService {

    PageResult<AnnouncementVO> getAllAnnouncement(Integer page, Integer pageSize);
    AnnouncementVO getAnnouncementInfo(Long id);
    // 管理员操作
    Announcement createAnnouncement(AnnouncementRequest announcementRequest);
    Announcement updateAnnouncement(Long id, AnnouncementRequest announcementRequest);
    Void removeAnnouncement(Long id);
}
