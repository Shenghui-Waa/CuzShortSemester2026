package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ProductQueryDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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

    private static final Set<String> ALLOWED_TRANSITIONS = Set.of(
            ProductConstant.STATUS_ON_SALE + "->" + ProductConstant.STATUS_DISABLE,
            ProductConstant.STATUS_DISABLE + "->" + ProductConstant.STATUS_NEED_CHECK
    );

    // 获取商品列表 同时标记当前用户喜欢的
    @Override
    public Result<PageResult<ProductVO>> getProductList(
            ProductQueryDTO query, Long currentUserId
    ) {
        PageHelper.startPage(query.getPage(), query.getPageSize());
        List<Product> page;
        page = productMapper.selectByKeywordOrCategoryOrCampusOrStatus(
                query.getKeyword(), query.getCategoryId(), query.getCampus(),
                ProductConstant.STATUS_ON_SALE);

        PageInfo<Product> pageInfo = new PageInfo<>(page);
        Set<Long> favoriteUserIds = (currentUserId != null)
                ? new HashSet<>(favoriteMapper.selectFavoriteProductIdsByUserId(currentUserId))
                : Collections.emptySet();

        // 批量预加载关联数据
        return batchPreloading(page, pageInfo, favoriteUserIds);
    }

    // 根据 用户 ID 获取用户发布的商品列表
    @Override
    public Result<PageResult<ProductVO>> getProductList(
            Long userId, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<Product> productList = productMapper.selectByUserIdWithLimit(userId, null, null);
        PageInfo<Product> productPageInfo = new PageInfo<>(productList);
        Set<Long> favoritedIds = new HashSet<>(
                favoriteMapper.selectFavoriteProductIdsByUserId(userId)
        );

        return batchPreloading(productList, productPageInfo, favoritedIds);
    }

    // 获取商品详情，同时标记当前用户的喜欢状态
    @Override
    public Result<ProductVO> getProductDetail(
            Long id, Long currentUserId
    ) {
        Product product = productMapper.selectById(id);
        if (product == null)
             throw new BusinessException(404, "Product not found");
        productMapper.addViewCount(id);
        Set<Long> favoritedProductsIds = Collections.emptySet();
        if (currentUserId != null)
            favoritedProductsIds = new HashSet<>(
                    favoriteMapper.selectFavoriteProductIdsByUserId(currentUserId)
            );
        product = productMapper.selectById(product.getId());
        return Result.success(toVO(product, favoritedProductsIds));
    }

    // 发布商品
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

    // 修改商品信息
    @Override
    public Result<ProductVO> updateProduct(
            Long userId, Long productId, Product product, List<String> images
    ) {
        Product existing = productMapper.selectById(productId);
        if (existing == null)
            throw new BusinessException(404, "Product not found");
        if (!Objects.equals(existing.getUserId(), userId))
            throw new BusinessException(403, "Permission denied");
        product.setId(productId);
        product.setUserId(existing.getUserId());
        product.setStatus(ProductConstant.STATUS_NEED_CHECK);
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

    // 修改商品状态
    @Override
    public Result<Void> updateProduct(
            Long userId, Long productId, Integer status
    ) {
        Product product = productMapper.selectById(productId);
        if (product == null)
            throw new BusinessException(404, "Product not found");
        if (!Objects.equals(product.getUserId(), userId))
            throw new BusinessException(403, "Permission denied");
        String transition = product.getStatus() + "->" + status;
        if (!ALLOWED_TRANSITIONS.contains(transition))
            throw new BusinessException(400, "Invalid status transition");
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        return Result.success();
    }

    // 删除商品
    @Override
    public Result<Void> removeProduct(
            Long userId, Long productId
    ) {
        Product product = productMapper.selectById(productId);
        if (product == null)
            throw new BusinessException(404, "Product not found");
        if (!Objects.equals(product.getUserId(), userId))
            throw new BusinessException(403, "Permission denied");
        productMapper.deleteById(product.getId());
        return Result.success();
    }


    // =====================================================================================
    // ===========================>>>>> 管 理 员 操 作 <<<<<==================================
    // =====================================================================================

    // 获取商品
    @Override
    public Result<PageResult<ProductVO>> getProductList(
            Integer page, Integer pageSize, String keyword, Integer status
    ) {
        PageHelper.startPage(page, pageSize);
        List<Product> products;
        products = productMapper.selectByKeywordOrCategoryOrCampusOrStatus(
                keyword, null, null, status);
        PageInfo<Product> pageInfo = new PageInfo<>(products);

        // 批量预加载关联数据
        Map<Long, User> userMap = buildUserMap(products);
        Map<Long, Category> categoryMap = buildCategoryMap(products);
        Map<Long, List<String>> imageMap = buildImageMap(products);

        List<ProductVO> productVOs = products.stream()
                .map(product -> {
                    ProductVO productVO = ToVOUtil.toProductVO(
                            product, userMap.get(product.getUserId()),
                            categoryMap.get(product.getCategoryId()));
                    productVO.setImages(imageMap.getOrDefault(product.getId(), Collections.emptyList()));
                    return productVO;
                }).collect(Collectors.toList());

        return Result.success(new PageResult<>(
                productVOs, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()
        ));
    }

    // 修改商品
    @Override
    public Result<Void> updateProduct(
            Long productId, Integer status
    ) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setStatus(status);
            product.setUpdatedAt(LocalDateTime.now());
            productMapper.updateById(product);
        }
        return Result.success();
    }


    // #===========>>>>> 内 部 私 有 工 具 方 法 <<<<<===========#

    // 单商品转 VO
    private ProductVO toVO(
            Product product, Set<Long> favIds
    ) {
        ProductVO productVO = ToVOUtil.toProductVO(
                product, userMapper.selectById(product.getUserId()),
                categoryMapper.selectById(product.getCategoryId())
        );
        productVO.setIsFavorited(favIds.contains(product.getId()));
        productVO.setImages(productImageMapper.selectByProductId(product.getId())
                .stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList()));
        return productVO;
    }

    // 批量商品转 VO
    private ProductVO toVO(
            Product product, Set<Long> favIds, Map<Long, User> userMap,
            Map<Long, Category> categoryMap, Map<Long, List<String>> imageMap
    ) {
        ProductVO productVO = ToVOUtil.toProductVO(
                product, userMap.get(product.getUserId()),
                categoryMap.get(product.getCategoryId())
        );
        productVO.setIsFavorited(favIds.contains(product.getId()));
        productVO.setImages(imageMap.getOrDefault(product.getId(), Collections.emptyList()));
        return productVO;
    }

    private Map<Long, User> buildUserMap(List<Product> products) {
        List<Long> userIds = products.stream()
                .map(Product::getUserId)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty())
            return Collections.emptyMap();
        return userMapper.selectByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    private Map<Long, Category> buildCategoryMap(List<Product> products) {
        List<Long> categoryIds = products.stream()
                .map(Product::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        if (categoryIds.isEmpty())
            return Collections.emptyMap();
        return categoryMapper.selectByIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
    }

    private Map<Long, List<String>> buildImageMap(List<Product> products) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        if (productIds.isEmpty())
            return Collections.emptyMap();
        return productImageMapper.selectByProductIds(productIds).stream()
                .collect(Collectors.groupingBy(
                        ProductImage::getProductId,
                        Collectors.mapping(ProductImage::getUrl, Collectors.toList())
                ));
    }

    // 批量预加载
    @NonNull
    private Result<PageResult<ProductVO>> batchPreloading(
            List<Product> productList, PageInfo<Product> productPageInfo, Set<Long> favoritedIds
    ) {
        Map<Long, User> userMap = buildUserMap(productList);
        Map<Long, Category> categoryMap = buildCategoryMap(productList);
        Map<Long, List<String>> imageMap = buildImageMap(productList);

        List<ProductVO> productVOs = productList.stream()
                .map(product -> toVO(
                        product, favoritedIds, userMap, categoryMap, imageMap))
                .collect(Collectors.toList());

        return Result.success(new PageResult<>(
                productVOs, productPageInfo.getTotal(),
                productPageInfo.getPageNum(), productPageInfo.getPageSize()
        ));
    }

}
