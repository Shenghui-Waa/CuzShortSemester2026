package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductQueryRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final TokenProvider tokenProvider;

    /**
     * 获取商品列表
     */
    @GetMapping
    public Result<?> getProductList(
            ProductQueryRequest query,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        Long currentUserId = token == null ? null : tokenProvider.getUserId(token);
        return Result.success(productService.getProductList(query, currentUserId));
    }

    /**
     * 获取某一商品详情 基于 id
     */
    @GetMapping("/{id}")
    public Result<?> getProductDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        Long currentUserId = token == null ? null : tokenProvider.getUserId(token);
        return Result.success(productService.getProductDetail(id, currentUserId));
    }

    /**
     * 发布商品
     */
    @PostMapping
    public Result<?> createProduct(
            @RequestHeader("Authorization") String token,
            @RequestBody Product product,
            @RequestParam(required = false) List<String> images
    ) {
        Long currentUserId = requireCurrentUserId();
        return Result.success(productService.createProduct(currentUserId, product, images));
    }

    /**
     * 修改商品信息
     */
    @PutMapping("/{id}")
    public Result<?> updateProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Product product,
            @RequestParam(required = false) List<String> images
    ) {
        Long currentUserId = requireCurrentUserId();
        return Result.success(productService.updateProduct(currentUserId, id, product, images));
    }

    /**
     * 修改商品状态
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        Long currentUserId = requireCurrentUserId();
        return Result.success(productService.updateProduct(currentUserId, id, status));
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}/del")
    public Result<?> removeProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = requireCurrentUserId();
        return Result.success(productService.removeProduct(currentUserId, id));
    }

    /**
     * 获取我发布的商品列表
     */
    @GetMapping("/my")
    public Result<?> getMyProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Long currentUserId = requireCurrentUserId();
        return Result.success(productService.getProductList(currentUserId, page, pageSize));
    }

    private Long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            throw new BusinessException(Result.Code.UNAUTHORIZED, "Authentication required");
        }
        return user.getId();
    }

}
