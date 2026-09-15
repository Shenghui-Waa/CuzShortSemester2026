package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductQueryRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ProductVO;
import java.util.List;

public interface ProductService {
    PageResult<ProductVO> getProductList(ProductQueryRequest query, Long currentUserId);
    PageResult<ProductVO> getProductList(Long userId, Integer page, Integer pageSize);
    ProductVO getProductDetail(Long id, Long currentUserId);
    ProductVO createProduct(Long userId, Product product, List<String> images);
    ProductVO updateProduct(Long userId, Long productId, Product product, List<String> images);
    Void updateProduct(Long userId, Long productId, Integer status);
    Void removeProduct(Long userId, Long productId);
    // 管理员操作
    PageResult<ProductVO> getProductList(
            Integer page,
            Integer pageSize,
            String keyword,
            Integer status
    );
    Void updateProduct(Long productId, Integer status);
}
