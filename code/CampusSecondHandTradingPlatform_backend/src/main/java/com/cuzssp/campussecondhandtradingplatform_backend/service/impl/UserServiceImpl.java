package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.Base64Provider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.UserService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserMapper userMapper;
    @Autowired private Base64Provider base64Provider;

    @Override
    public Result<UserVO> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null)
            throw new BusinessException("User not found");
        return Result.success(toVO(user));
    }

    /**
     * 修改个人信息
     * @param userId
     * @param request
     * @return
     */
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
        userMapper.updateById(user);
        return Result.success(toVO(userMapper.selectById(userId)));
    }

    /**
     * 修改密码
     * @param userId
     * @param request
     * @return
     */
    @Override
    public Result<Void> changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException("User not found");
        if (!base64Provider.matches(request.getOldPassword(), user.getPassword()))
            throw new BusinessException("Old password is incorrect");
        user.setPassword(base64Provider.encode(request.getNewPassword()));
        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 修改头像
     * @param userId
     * @param imageURL
     * @return
     */
    @Override
    public Result<Void> updateAvatar(Long userId, String imageURL) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new BusinessException("User not found");
        user.setAvatar(imageURL);
        userMapper.updateById(user);
        return Result.success();
    }

    private UserVO toVO(User user) {
        return ToVOUtil.toUserVO(user);
    }
}
