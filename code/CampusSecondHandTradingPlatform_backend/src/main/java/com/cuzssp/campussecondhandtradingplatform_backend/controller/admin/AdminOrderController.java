package com.cuzssp.campussecondhandtradingplatform_backend.controller.admin;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductImageMapper productImageMapper;
    @Autowired private UserMapper userMapper;

    @GetMapping
    public Result<?> getOrders(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) Integer status) {
        PageHelper.startPage(page, pageSize);
        List<OrderInfo> all = orderMapper.selectAll();
        if (status != null) all = all.stream().filter(o -> o.getStatus().equals(status)).collect(Collectors.toList());
        PageInfo<OrderInfo> pgInfo = new PageInfo<>(all);
        List<OrderVO> vos = all.stream().map(order -> {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId()); vo.setOrderNo(order.getOrderNo());
            vo.setBuyerId(order.getBuyerId()); vo.setSellerId(order.getSellerId());
            vo.setTotalAmount(order.getTotalAmount()); vo.setStatus(order.getStatus());
            vo.setCreatedAt(order.getCreatedAt());
            User buyer = userMapper.selectById(order.getBuyerId());
            if (buyer != null) vo.setBuyerName(buyer.getNickname());
            User seller = userMapper.selectById(order.getSellerId());
            if (seller != null) vo.setSellerName(seller.getNickname());
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            vo.setItems(items.stream().map(item -> {
                OrderItemVO ivo = new OrderItemVO();
                ivo.setId(item.getId()); ivo.setProductId(item.getProductId()); ivo.setPrice(item.getPrice());
                Product p = productMapper.selectById(item.getProductId());
                if (p != null) {
                    ivo.setProductTitle(p.getTitle());
                    List<ProductImage> imgs = productImageMapper.selectByProductId(p.getId());
                    ivo.setProductImage(imgs.isEmpty() ? null : imgs.get(0).getUrl());
                }
                return ivo;
            }).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }
}
