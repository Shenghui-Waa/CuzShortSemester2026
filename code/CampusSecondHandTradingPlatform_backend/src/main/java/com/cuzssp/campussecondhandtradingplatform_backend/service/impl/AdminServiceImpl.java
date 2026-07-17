package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.Base64Provider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserMapper userMapper;

    private final Base64Provider base64Provider;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;

    private final CategoryMapper categoryMapper;

    /**
     * 获取仪表盘
     * @return
     */
    @Override
    public Result<DashboardVO> getDashboard() {
        return Result.success(ToVOUtil.toDashboardVO(userMapper, productMapper, orderMapper));
    }

    @Override
    public Result<UserVO> addAdmin(RegisterRequest request) {
        if (userMapper.countByUsername(request.getUsername()) > 0)
            throw new BusinessException("Admin name already exists");
        User user = ToEntityUtil.toUserEntity(request, base64Provider);
        user.setRole(UserConstant.ROLE_ADMIN);
        userMapper.insert(user);
        return Result.success(ToVOUtil.toUserVO(user));
    }

    /**
     * 获取用户列表
     * @param page
     * @param pageSize
     * @param keyword
     * @return
     */
    @Override
    public Result<PageResult<UserVO>> getUserList(
            Integer page,
            Integer pageSize,
            String keyword
    ) {
        PageHelper.startPage(page, pageSize);
        List<User> users = (keyword != null && !keyword.isEmpty())
                ? userMapper.selectAll().stream()
                .filter(user -> user.getUsername().contains(keyword)
                        || user.getNickname().contains(keyword))
                .collect(Collectors.toList())
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

    /**
     * 修改用户状态
     * @param userId
     * @param status
     * @return
     */
    @Override
    public Result<Void> updateUserStatus(
            Long userId,
            Integer status
    ) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(status);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
        return Result.success();
    }

    /**
     * 获取商品列表
     * @param page
     * @param pageSize
     * @param keyword
     * @param status
     * @return
     */
    @Override
    public Result<PageResult<ProductVO>> getProductList(
            Integer page,
            Integer pageSize,
            String keyword,
            Integer status
    ) {
        PageHelper.startPage(page, pageSize);
        List<Product> all;
        if (keyword != null && !keyword.isEmpty())
            all = productMapper.searchByKeyword(keyword, ProductConstant.STATUS_ON_SALE);
        else
            all = productMapper.selectAll();
        if (status != null)
            all = all.stream()
                    .filter(product -> product.getStatus().equals(status))
                    .collect(Collectors.toList());
        PageInfo<Product> pageInfo = new PageInfo<>(all);
        List<ProductVO> productVOs = all.stream()
                .map(product -> {
                    ProductVO productVO = ToVOUtil.toProductVO(
                            product,
                            userMapper.selectById(product.getUserId()),
                            categoryMapper.selectById(product.getCategoryId()));
                    productVO.setImages(
                            productImageMapper.selectByProductId(product.getId()).stream()
                                    .map(ProductImage::getUrl)
                                    .collect(Collectors.toList()));
                    return productVO;
                }).collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        productVOs,
                        pageInfo.getTotal(),
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize()
                )
        );
    }


    /**
     * 修改商品状态
     * @param productId
     * @param status
     * @return
     */
    @Override
    public Result<Void> updateProductStatus(Long productId, Integer status) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setStatus(status);
            product.setUpdatedAt(LocalDateTime.now());
            productMapper.updateById(product);
        }
        return Result.success();
    }

    /**
     * 获取订单列表
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public Result<PageResult<OrderVO>> getOrders(
            Integer page,
            Integer pageSize,
            @Nullable Integer status
    ) {
        PageHelper.startPage(page, pageSize);
        List<OrderInfo> all = orderMapper.selectAll();
        if (status != null)
            all = all.stream()
                    .filter(order -> order.getStatus().equals(status))
                    .collect(Collectors.toList());
        PageInfo<OrderInfo> orderPageInfo = new PageInfo<>(all);
        List<OrderVO> orderVOs = all.stream()
                .map(order -> {
                    OrderVO orderVO = ToVOUtil.toOrderVO(
                            order,
                            userMapper.selectById(order.getBuyerId()),
                            userMapper.selectById(order.getSellerId())
                    );
                    List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
                    orderVO.setItems(items.stream()
                            .map(orderItem -> ToVOUtil.toOrderItemVO(
                                    orderItem,
                                    productMapper.selectById(orderItem.getProductId()),
                                    productImageMapper
                            )).collect(Collectors.toList()));

                    return orderVO;
                }).collect(Collectors.toList());

        return Result.success(
                new PageResult<>(
                        orderVOs,
                        orderPageInfo.getTotal(),
                        orderPageInfo.getPageNum(),
                        orderPageInfo.getPageSize()
                )
        );
    }


}
