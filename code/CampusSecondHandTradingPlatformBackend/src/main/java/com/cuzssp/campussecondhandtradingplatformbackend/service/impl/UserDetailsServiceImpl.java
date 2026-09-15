package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    // 通过用户名加载用户
    @Override
    @NullMarked
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found: " + username);

        String role = user.getRole() == UserConstant.Role.ADMIN ? "ROLE_ADMIN" : "ROLE_USER";
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                user.getStatus() == UserConstant.Status.ACTIVE, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
