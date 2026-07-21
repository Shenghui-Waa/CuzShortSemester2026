package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.PasswordProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.UserService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordProvider passwordProvider;
    private final ProductMapper productMapper;

    private static final int BATCH_SIZE = 50;

    @Override
    public Result<UserVO> getUserById(
            Long id
    ) {
        User user = userMapper.selectById(id);
        if (user == null)
            throw new BusinessException("User not found");
        return Result.success(ToVOUtil.toUserVO(user));
    }

    @Override
    public Result<UserVO> updateProfile(
            Long userId, UpdateProfileRequest request
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException("User not found");
        if (request.getNickname() != null)
            user.setNickname(request.getNickname());
        if (request.getPhone() != null)
            user.setPhone(request.getPhone());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getSchool() != null)
            user.setSchool(request.getSchool());
        if (request.getCampus() != null)
            user.setCampus(request.getCampus());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success(ToVOUtil.toUserVO(userMapper.selectById(userId)));
    }

    @Override
    public Result<Void> changePassword(
            Long userId, ChangePasswordRequest request
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException("User not found");
        if (!passwordProvider.matches(request.getOldPassword(), user.getPassword()))
            throw new BusinessException("Old password is incorrect");
        user.setPassword(passwordProvider.encode(request.getNewPassword()));
        userMapper.updateById(user);
        return Result.success();
    }

    @Override
    public Result<Void> updateAvatar(
            Long userId, String imageURL
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException("User not found");
        user.setAvatar(imageURL);
        userMapper.updateById(user);
        return Result.success();
    }


    /*
    管理员操作
     */
    @Override
    public Result<UserVO> addAdmin(
            RegisterRequest request
    ) {
        if (userMapper.countByUsername(request.getUsername()) > 0)
            throw new BusinessException("Admin name already exists");
        User user = ToEntityUtil.toUserEntity(request, passwordProvider);
        user.setRole(UserConstant.ROLE_ADMIN);
        userMapper.insert(user);
        return Result.success(ToVOUtil.toUserVO(user));
    }

    @Override
    public Result<PageResult<UserVO>> getUserList(
            Integer page, Integer pageSize, String keyword
    ) {
        PageHelper.startPage(page, pageSize);
        List<User> users = (keyword != null && !keyword.isEmpty())
                ? userMapper.selectByKeyword(keyword)
                : userMapper.selectAll();
        PageInfo<User> pageInfo = new PageInfo<>(users);
        List<UserVO> userVOs = users.stream()
                .map(ToVOUtil::toUserVO)
                .collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        userVOs,
                        pageInfo.getTotal(),
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize()
                )
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateUserStatus(
            Long userId, Integer targetStatus
    ) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException(404, "User not found");
        user.setStatus(targetStatus);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        if (targetStatus == UserConstant.STATUS_DISABLE)
            updateUserProductsStatus(userId, ProductConstant.STATUS_DISABLE);
        else
            updateUserProductsStatus(userId, ProductConstant.STATUS_NEED_CHECK);

        return Result.success();
    }

    private void updateUserProductsStatus(Long userId, Integer targetStatus) {
        int offset = 0;
        List<Product> batch;
        do {
            batch = productMapper.selectByUserIdWithLimit(userId, offset, BATCH_SIZE);
            for (Product product : batch) {
                if (!Objects.equals(product.getStatus(), ProductConstant.STATUS_SOLD_OUT)) {
                    product.setStatus(targetStatus);
                    product.setUpdatedAt(LocalDateTime.now());
                    productMapper.updateById(product);
                }
            }
            offset += BATCH_SIZE;
        } while (batch.size() == BATCH_SIZE);
    }

}
