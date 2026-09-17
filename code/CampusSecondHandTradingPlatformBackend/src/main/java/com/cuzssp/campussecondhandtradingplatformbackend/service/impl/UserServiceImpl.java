package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ResetPasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.PasswordProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.UtcTime;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.UserService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.UserVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordProvider passwordProvider;
    private final ProductMapper productMapper;

    // 获取用户信息
    @Override
    public UserVO getUserById(
            Long id
    ) {
        User user = userMapper.selectById(id);
        if (user == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "用户不存在");

        return ToVOUtil.toUserVO(user);
    }

    // 修改个人信息
    @Override
    public UserVO updateProfile(
            Long userId, UserRequest request
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "用户不存在");

        userMapper.updateProfileFields(ToEntityUtil.updateUserEntity(user, request));
        return ToVOUtil.toUserVO(userMapper.selectById(userId));
    }

    // 修改密码
    @Override
    public Void changePassword(
            Long userId, ChangePasswordRequest request
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "用户不存在");

        if (!passwordProvider.matches(request.getOldPassword(), user.getPassword()))
            throw new BusinessException("旧密码错误");

        if (request.getNewPassword() == null || request.getNewPassword().isBlank())
            throw new BusinessException("需要新密码");

        user.setPassword(passwordProvider.encode(request.getNewPassword()));
        user.setUpdatedAt(UtcTime.now());
        userMapper.updatePassword(user.getId(), user.getPassword(), user.getUpdatedAt());
        return null;
    }

    // 修改头像
    @Override
    public Void updateAvatar(
            Long userId, String imageURL
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "用户不存在");

        userMapper.updateAvatar(userId, imageURL, UtcTime.now());
        return null;
    }

    // 管理员操作

    // 获取用户列表
    @Override
    public PageResult<UserVO> getUserList(
            Integer page, Integer pageSize, String keyword
    ) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        PageHelper.startPage(currentPage, currentSize);
        try {
            List<User> users = (keyword != null && !keyword.isEmpty())
                    ? userMapper.selectByKeyword("%" + keyword + "%")
                    : userMapper.selectAll();
            PageInfo<User> pageInfo = new PageInfo<>(users);
            List<UserVO> userVOs = users.stream()
                    .map(ToVOUtil::toUserVO)
                    .collect(Collectors.toList());
            return new PageResult<>(
                            userVOs,
                            pageInfo.getTotal(),
                            pageInfo.getPageNum(),
                            pageInfo.getPageSize()
            );
        } finally {
            PageHelper.clearPage();
        }
    }

    // 添加管理员
    @Override
    public UserVO addAdmin(
            UserRequest request
    ) {
        if (userMapper.countByUsername(request.getUsername()) > 0)
            throw new BusinessException(Result.Code.FORBIDDEN, "该管理员用户名已存在");

        User user = ToEntityUtil.toUserEntity(
                request, passwordProvider, UserConstant.Role.ADMIN);
        userMapper.insert(user);
        return ToVOUtil.toUserVO(user);
    }

    // 修改用户状态
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateUserStatus(
            Long userId, Integer targetStatus
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException(Result.Code.FORBIDDEN, "用户不存在");

        if (!Objects.equals(targetStatus, UserConstant.Status.ACTIVE)
                && !Objects.equals(targetStatus, UserConstant.Status.INACTIVE))
            throw new BusinessException("无效的用户状态");

        user.setStatus(targetStatus);
        user.setUpdatedAt(UtcTime.now());
        userMapper.updateStatus(user.getId(), user.getStatus(), user.getUpdatedAt());

        if (targetStatus == UserConstant.Status.INACTIVE)
            updateUserProductsStatus(userId, ProductConstant.Status.DISABLE);
        else
            updateUserProductsStatus(userId, ProductConstant.Status.NEED_CHECK);

        return null;
    }

    // 删除用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void deleteUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "用户不存在");

        updateUserProductsStatus(user.getId(), ProductConstant.Status.DISABLE);
        userMapper.deleteById(user.getId());
        log.info("成功删除 ID={} 的用户", id);
        return null;
    }

    @Override
    public Void resetPassword(Long id, ResetPasswordRequest request) {
        User user = userMapper.selectById(id);
        if (user == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "用户不存在");

        if (request.getNewPassword() == null || request.getNewPassword().isBlank())
            throw new BusinessException("需要新密码");

        user.setPassword(passwordProvider.encode(request.getNewPassword()));
        user.setUpdatedAt(UtcTime.now());
        userMapper.updatePassword(user.getId(), user.getPassword(), user.getUpdatedAt());
        return null;
    }

    // 修改用户的商品状态
    private void updateUserProductsStatus(Long userId, Integer targetStatus) {
        productMapper.updateUserProductsStatus(userId, targetStatus, UtcTime.now());
    }
}
