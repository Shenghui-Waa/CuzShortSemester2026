package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ExampleMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {

    private final ExampleMapper exampleMapper;

    @Override
    public Void example() {
        doSomething();
        exampleMapper.doSomething();
        doSomething();
        return null;
    }

    private void doSomething() {
        // example do something
    }

}
