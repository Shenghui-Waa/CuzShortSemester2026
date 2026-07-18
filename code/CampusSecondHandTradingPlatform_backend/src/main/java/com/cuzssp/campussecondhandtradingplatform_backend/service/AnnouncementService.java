package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Announcement;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.AnnouncementVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;

public interface AnnouncementService {

    Result<PageResult<AnnouncementVO>> getAllAnnouncement(Integer page, Integer pageSize);
    Result<Announcement> createAnnouncement(AnnouncementRequest announcementRequest);
    Result<Announcement> updateAnnouncement(Long id, AnnouncementRequest announcementRequest);
    Result<Void> removeAnnouncement(Long id);
    Result<AnnouncementVO> getAnnouncementInfo(Long id);
}
