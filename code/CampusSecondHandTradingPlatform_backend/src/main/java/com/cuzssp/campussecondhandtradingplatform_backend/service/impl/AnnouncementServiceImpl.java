package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Announcement;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.AnnouncementVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.AnnouncementMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AnnouncementService;
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
public class   AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public Result<PageResult<AnnouncementVO>> getAllAnnouncement(
            Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<Announcement> announcements = announcementMapper.selectAll();
        PageInfo<Announcement> announcementPageInfo = new PageInfo<>(announcements);
        List<AnnouncementVO> announcementVOs = announcements.stream()
                .map(ToVOUtil::toAnnouncementVO)
                .collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        announcementVOs,
                        announcementPageInfo.getTotal(),
                        announcementPageInfo.getPageNum(),
                        announcementPageInfo.getPageSize()
                )
        );
    }

    @Override
    public Result<AnnouncementVO> getAnnouncementInfo(
            Long id
    ) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("Announcement not found");
        }
        AnnouncementVO announcementVO = ToVOUtil.toAnnouncementVO(announcement);
        return Result.success(announcementVO);
    }

    /*
    管理员操作
     */
    @Override
    public Result<Announcement> createAnnouncement(
            AnnouncementRequest announcementRequest
    ) {
        Announcement announcement = ToEntityUtil.toAnnouncementEntity(announcementRequest);
        announcementMapper.insert(announcement);
        return Result.success(announcement);
    }

    @Override
    public Result<Announcement> updateAnnouncement(
            Long id, AnnouncementRequest announcementRequest
    ) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("Announcement not found");
        }
        announcement.setTitle(announcementRequest.getTitle());
        announcement.setContent(announcementRequest.getContent());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.updateById(announcement);

        return Result.success(announcement);
    }

    @Override
    public Result<Void> removeAnnouncement(
            Long id
    ) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("Announcement not found");
        }
        announcementMapper.deleteById(id);
        return Result.success();

    }

}
