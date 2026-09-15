package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.mapper.AnnouncementMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class   AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

}
