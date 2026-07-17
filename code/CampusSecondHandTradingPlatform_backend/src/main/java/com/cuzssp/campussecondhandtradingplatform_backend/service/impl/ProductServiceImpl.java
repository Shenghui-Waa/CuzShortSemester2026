package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ProductQueryDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 获取商品列表 同时标记当前用户喜欢的
     * @param query
     * @param currentUserId
     * @return
     */
    @Override
    public Result<PageResult<ProductVO>> getProductList(
            ProductQueryDTO query, Long currentUserId
    ) {
        PageHelper.startPage(query.getPage(), query.getPageSize());
        List<Product> page;
        if (query.getKeyword() != null && !query.getKeyword().isEmpty())
            page = productMapper.searchByKeyword(
                    query.getKeyword(),
                    ProductConstant.STATUS_ON_SALE
            );
        else if (
                query.getCategoryId() != null && query.getCampus() != null
                && !query.getCampus().isEmpty()
        )
            page = productMapper.selectByCategoryAndCampus(
                    query.getCategoryId(),
                    query.getCampus(),
                    ProductConstant.STATUS_ON_SALE);
        else if (query.getCategoryId() != null)
            page = productMapper.selectByCategoryId(
                    query.getCategoryId(),
                    ProductConstant.STATUS_ON_SALE
            );
        else if (query.getCampus() != null && !query.getCampus().isEmpty())
            page = productMapper.selectByCampus(
                    query.getCampus(),
                    ProductConstant.STATUS_ON_SALE
            );
        else
            page = productMapper.selectAllActive();
        PageInfo<Product> pageInfo = new PageInfo<>(page);
        Set<Long> favoriteUserIds = (currentUserId != null)
                ? new HashSet<>(favoriteMapper.selectFavoritedProductIdsByUserId(currentUserId))
                : Collections.emptySet();
        List<ProductVO> productVOs = page.stream()
                .map(product -> toVO(product, favoriteUserIds))
                .collect(Collectors.toList());
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
     * 获取商品详情，同时标记当前用户的喜欢状态
     * @param id
     * @param currentUserId
     * @return
     */
    @Override
    public Result<ProductVO> getProductDetail(
            Long id, Long currentUserId
    ) {
        Product product = productMapper.selectById(id);
        if (product == null)
            return Result.error(404, "Product not found");
        product.setViewCount(product.getViewCount() != null ? product.getViewCount() + 1 : 1);
        productMapper.updateById(product);
        Set<Long> favoritedProductsIds = Collections.emptySet();
        if (currentUserId != null)
            favoritedProductsIds = new HashSet<>(
                    favoriteMapper.selectFavoritedProductIdsByUserId(currentUserId)
            );
        return Result.success(toVO(productMapper.selectById(id), favoritedProductsIds));
    }

    /**
     * 发布商品
     * @param userId
     * @param product
     * @param images
     * @return
     */
    @Override
    public Result<ProductVO> createProduct(
            Long userId, Product product, List<String> images
    ) {
        product.setUserId(userId);
        product.setStatus(ProductConstant.STATUS_NEED_CHECK);
        product.setViewCount(ProductConstant.VIEW_COUNT_DEFAULT);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setProductId(product.getId());
                productImage.setUrl(images.get(i));
                productImage.setSortOrder(i);
                productImageMapper.insertImage(productImage);
            }
        }
        return Result.success(
                toVO(productMapper.selectById(product.getId()), Collections.emptySet())
        );
    }

    /**
     * 修改商品
     * @param userId
     * @param productId
     * @param product
     * @param images
     * @return
     */
    @Override
    public Result<ProductVO> updateProduct(
            Long userId, Long productId, Product product, List<String> images
    ) {
        Product existing = productMapper.selectById(productId);
        if (existing == null)
            return Result.error(404, "Product not found");
        product.setId(productId);
        product.setUserId(existing.getUserId());
        product.setStatus(ProductConstant.STATUS_NEED_CHECK); // 修改后须审核
        product.setViewCount(existing.getViewCount());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        if (images != null) {
            productImageMapper.deleteByProductId(productId);
            for (int i = 0; i < images.size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setProductId(productId);
                productImage.setUrl(images.get(i));
                productImage.setSortOrder(i);
                productImageMapper.insertImage(productImage);
            }
        }
        return Result.success(
                toVO(productMapper.selectById(productId), Collections.emptySet())
        );
    }

    /**
     * 修改商品状态
     * @param userId
     * @param productId
     * @param status
     * @return
     */
    @Override
    public Result<Void> updateProductStatus(
            Long userId, Long productId, Integer status
    ) {
        Product product = productMapper.selectById(productId);
        if (product == null)
            return Result.error(404, "Product not found");
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        return Result.success();
    }

    @Override
    public Result<Void> deleteProduct(
            Long userId, Long productId
    ) {
        Product product = productMapper.selectById(productId);
        if (product == null)
            return Result.error(404, "Product not found");
        productMapper.deleteById(product.getId());
        return Result.success();
    }

    /**
     * 获取我发布的商品列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Result<PageResult<ProductVO>> getMyProducts(
            Long userId, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<Product> productList = productMapper.selectByUserId(userId);
        PageInfo<Product> productPageInfo = new PageInfo<>(productList);
        Set<Long> favoritedIds = new HashSet<>(
                favoriteMapper.selectFavoritedProductIdsByUserId(userId)
        );
        List<ProductVO> productVOs = productList.stream()
                .map(product -> toVO(product, favoritedIds))
                .collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        productVOs,
                        productPageInfo.getTotal(),
                        productPageInfo.getPageNum(),
                        productPageInfo.getPageSize()
                )
        );
    }

    private ProductVO toVO(
            Product product, Set<Long> favIds
    ) {
        ProductVO productVO = ToVOUtil.toProductVO(
                product,
                userMapper.selectById(product.getUserId()),
                categoryMapper.selectById(product.getCategoryId())
        );
        productVO.setIsFavorited(favIds.contains(product.getId()));
        productVO.setImages(productImageMapper.selectByProductId(product.getId())
                .stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList()));
        return productVO;
    }
}
