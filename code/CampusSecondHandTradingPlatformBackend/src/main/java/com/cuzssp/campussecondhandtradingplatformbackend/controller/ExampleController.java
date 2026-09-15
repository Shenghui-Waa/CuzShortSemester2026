package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/example")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    @PostMapping
    public Result<?> example(
    ) {
        return Result.success(exampleService.example());
    }

}
