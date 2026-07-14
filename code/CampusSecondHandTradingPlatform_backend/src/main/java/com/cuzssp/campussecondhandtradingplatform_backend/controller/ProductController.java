package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ProductQueryDTO;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    /**
     * 获取商品列表
     * @param query
     * @param token
     * @param priceBase 按价格升降序，为空默认不参考价格排序
     * @return
     */
    @GetMapping("/{priceBase}")
    public Result<?> getProductList(
            ProductQueryDTO query,
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer priceBase
    ) {
        Long currentUserId = getCurrentUserId(token);
        return productService.getProductList(query, currentUserId, priceBase);
    }

    /**
     * 获取某一商品详情 基于 id
     * @param id
     * @param token
     * @return
     */
    @GetMapping("/{id}")
    public Result<?> getProductDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        Long currentUserId = getCurrentUserId(token);
        return productService.getProductDetail(id, currentUserId);
    }

    /**
     * 发布商品
     * @param token
     * @param product
     * @param images
     * @return
     */
    @PostMapping
    public Result<?> createProduct(
            @RequestHeader("Authorization") String token,
            @RequestBody Product product,
            @RequestParam(required = false) List<String> images
    ) {
        Long currentUserId = getCurrentUserId(token);
        return productService.createProduct(currentUserId, product, images);
    }

    /**
     * 修改商品
     * TODO: 前端未做，无法验证是否可用，仅作逻辑确认
     * @param token
     * @param id
     * @param product
     * @param images
     * @return
     */
    @PutMapping("/{id}")
    public Result<?> updateProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Product product,
            @RequestParam(required = false) List<String> images
    ) {
        Long currentUserId = getCurrentUserId(token);
        return productService.updateProduct(currentUserId, id, product, images);
    }

    /**
     * 修改商品状态
     * @param token
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        Long currentUserId = getCurrentUserId(token);
        return productService.updateProductStatus(currentUserId, id, status);
    }

    /**
     * 获取我发布的商品列表
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/my")
    public Result<?> getMyProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Long currentUserId = getCurrentUserId(token);
        return productService.getMyProducts(currentUserId, page, pageSize);
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty())
            return null;
        try {
            return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
