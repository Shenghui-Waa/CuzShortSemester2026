package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ProductQueryDTO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.ProductVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import java.util.List;

public interface ProductService {
    Result<PageResult<ProductVO>> getProductList(ProductQueryDTO query, Long currentUserId);
    Result<PageResult<ProductVO>> getProductList(Long userId, Integer page, Integer pageSize);
    Result<ProductVO> getProductDetail(Long id, Long currentUserId);
    Result<ProductVO> createProduct(Long userId, Product product, List<String> images);
    Result<ProductVO> updateProduct(Long userId, Long productId, Product product, List<String> images);
    Result<Void> updateProduct(Long userId, Long productId, Integer status);
    Result<Void> removeProduct(Long userId, Long productId);
    // 管理员操作
    Result<PageResult<ProductVO>> getProductList(Integer page, Integer pageSize, String keyword, Integer status);
    Result<Void> updateProduct(Long productId, Integer status);
}
