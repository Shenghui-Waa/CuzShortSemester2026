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

    @Autowired
    private ProductService productService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Result<?> getProductList(ProductQueryDTO query,
                                     @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = getCurrentUserId(token);
        return productService.getProductList(query, currentUserId);
    }

    @GetMapping("/{id}")
    public Result<?> getProductDetail(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = getCurrentUserId(token);
        return productService.getProductDetail(id, currentUserId);
    }

    @PostMapping
    public Result<?> createProduct(@RequestHeader("Authorization") String token,
                                    @RequestBody Product product,
                                    @RequestParam(required = false) List<String> images) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return productService.createProduct(userId, product, images);
    }

    @PutMapping("/{id}")
    public Result<?> updateProduct(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id,
                                    @RequestBody Product product,
                                    @RequestParam(required = false) List<String> images) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return productService.updateProduct(userId, id, product, images);
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@RequestHeader("Authorization") String token,
                                   @PathVariable Long id,
                                   @RequestParam Integer status) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return productService.updateProductStatus(userId, id, status);
    }

    @GetMapping("/my")
    public Result<?> getMyProducts(@RequestHeader("Authorization") String token,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return productService.getMyProducts(userId, page, pageSize);
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        try {
            return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
