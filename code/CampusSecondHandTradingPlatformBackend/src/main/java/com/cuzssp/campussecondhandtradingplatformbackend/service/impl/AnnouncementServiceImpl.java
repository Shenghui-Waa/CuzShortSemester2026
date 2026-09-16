package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Announcement;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.AnnouncementVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.AnnouncementMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.AnnouncementService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    // 获取全部公告
    @Override
    public PageResult<AnnouncementVO> getAllAnnouncement(
            Integer page, Integer pageSize
    ) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        PageHelper.startPage(currentPage, currentSize);
        try {
            List<Announcement> announcements = announcementMapper.selectAll();
            PageInfo<Announcement> announcementPageInfo = new PageInfo<>(announcements);
            List<AnnouncementVO> announcementVOs = announcements.stream()
                    .map(ToVOUtil::toAnnouncementVO)
                    .collect(Collectors.toList());
            return new PageResult<>(
                            announcementVOs,
                            announcementPageInfo.getTotal(),
                            announcementPageInfo.getPageNum(),
                            announcementPageInfo.getPageSize()
            );
        } finally {
            PageHelper.clearPage();
        }
    }

    // 获取公告详情
    @Override
    public AnnouncementVO getAnnouncementInfo(
            Long id
    ) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Announcement not found");

        AnnouncementVO announcementVO = ToVOUtil.toAnnouncementVO(announcement);
        return announcementVO;
    }

    // 管理员操作

    // 创建公告
    @Override
    public Announcement createAnnouncement(
            AnnouncementRequest announcementRequest
    ) {
        Announcement announcement = ToEntityUtil.toAnnouncementEntity(announcementRequest);
        announcementMapper.insert(announcement);
        return announcement;
    }

    // 修改公告
    @Override
    public Announcement updateAnnouncement(
            Long id, AnnouncementRequest announcementRequest
    ) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Announcement not found");

        Announcement updated = ToEntityUtil.toAnnouncementEntity(announcementRequest);
        updated.setId(id);
        updated.setCreatedAt(announcement.getCreatedAt());
        announcementMapper.updateById(updated);
        return updated;
    }

    // 删除公告
    @Override
    public Void removeAnnouncement(
            Long id
    ) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Announcement not found");

        announcementMapper.deleteById(id);
        return null;
    }
}
