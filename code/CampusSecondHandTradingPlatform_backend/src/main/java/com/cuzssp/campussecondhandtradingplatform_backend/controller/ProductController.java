package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ProductQueryDTO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.SecurityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SecurityUtil securityUtil;

    /**
     * 获取商品列表
     */
    @GetMapping
    public Result<?> getProductList(
            ProductQueryDTO query,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return productService.getProductList(query, currentUserId);
    }

    /**
     * 获取某一商品详情 基于 id
     */
    @GetMapping("/{id}")
    public Result<?> getProductDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return productService.getProductDetail(id, currentUserId);
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
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return productService.createProduct(currentUserId, product, images);
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
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return productService.updateProduct(currentUserId, id, product, images);
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
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return productService.updateProduct(currentUserId, id, status);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}/del")
    public Result<?> removeProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return productService.removeProduct(currentUserId, id);
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
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return productService.getProductList(currentUserId, page, pageSize);
    }

}
