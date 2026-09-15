package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ProductImage;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CartItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.FavoriteMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ProductVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductQueryRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final CartItemMapper cartItemMapper;

    private static final Set<String> ALLOWED_TRANSITIONS = Set.of(
            ProductConstant.Status.ON_SALE + "->" + ProductConstant.Status.DISABLE,
            ProductConstant.Status.DISABLE + "->" + ProductConstant.Status.NEED_CHECK
    );

    // 获取商品列表 同时标记当前用户喜欢的
    @Override
    public PageResult<ProductVO> getProductList(
            ProductQueryRequest query, Long currentUserId
    ) {
        validatePagination(query.getPage(), query.getPageSize());
        PageHelper.startPage(query.getPage(), query.getPageSize());
        try {
            List<Product> page;
            page = productMapper.selectByKeywordOrCategoryOrCampusOrStatus(
                    query.getKeyword(), query.getCategoryId(), query.getCampus(),
                    ProductConstant.Status.ON_SALE);

            PageInfo<Product> pageInfo = new PageInfo<>(page);
            Set<Long> favoriteUserIds = (currentUserId != null)
                    ? new HashSet<>(favoriteIds(currentUserId))
                    : Collections.emptySet();

            // 批量预加载关联数据
            return batchPreloading(page, pageInfo, favoriteUserIds);
        } finally {
            PageHelper.clearPage();
        }
    }

    // 根据 用户 ID 获取用户发布的商品列表
    @Override
    public PageResult<ProductVO> getProductList(
            Long userId, Integer page, Integer pageSize
    ) {
        validatePagination(page, pageSize);
        PageHelper.startPage(page, pageSize);
        try {
            List<Product> productList = productMapper.selectByUserIdWithLimit(userId);
            PageInfo<Product> productPageInfo = new PageInfo<>(productList);
            Set<Long> favoritedIds = new HashSet<>(
                    favoriteIds(userId)
            );

            return batchPreloading(productList, productPageInfo, favoritedIds);
        } finally {
            PageHelper.clearPage();
        }
    }

    // 获取商品详情，同时标记当前用户的喜欢状态
    @Override
    public ProductVO getProductDetail(
            Long id, Long currentUserId
    ) {
        Product product = productMapper.selectById(id);
        if (product == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Product not found");

        productMapper.addViewCount(id);
        Set<Long> favoritedProductsIds = Collections.emptySet();
        if (currentUserId != null)
            favoritedProductsIds = new HashSet<>(favoriteIds(currentUserId));

        product = productMapper.selectById(product.getId());
        return toVO(product, favoritedProductsIds);
    }

    // 发布商品
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO createProduct(
            Long userId, Product product, List<String> images
    ) {
        validateProduct(product);
        product.setId(null);
        product.setUserId(userId);
        product.setStatus(ProductConstant.Status.NEED_CHECK);
        product.setViewCount(ProductConstant.VIEW_COUNT_DEFAULT);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setProductId(product.getId());
                productImage.setUrl(images.get(i));
                productImage.setSortOrder(i + 1);
                productImageMapper.insert(productImage);
            }
        }
        return toVO(productMapper.selectById(product.getId()), Collections.emptySet());
    }

    // 修改商品信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO updateProduct(
            Long userId, Long productId, Product product, List<String> images
    ) {
        Product existing = productMapper.selectById(productId);
        if (existing == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Product not found");

        if (!Objects.equals(existing.getUserId(), userId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        validateProduct(product);
        if (Objects.equals(existing.getStatus(), ProductConstant.Status.SOLD_OUT))
            throw new BusinessException("Sold product cannot be edited");

        product.setCreatedAt(existing.getCreatedAt());
        product.setId(productId);
        product.setUserId(existing.getUserId());
        product.setStatus(ProductConstant.Status.NEED_CHECK);
        product.setViewCount(existing.getViewCount());
        product.setUpdatedAt(LocalDateTime.now());
        int updated = productMapper.updateDetailsIfStatusMatches(product, existing.getStatus());
        if (updated != 1)
            throw new BusinessException("Product status has changed");

        if (images != null) {
            productImageMapper.deleteByProductId(productId);
            for (int i = 0; i < images.size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setProductId(productId);
                productImage.setUrl(images.get(i));
                productImage.setSortOrder(i + 1);
                productImageMapper.insert(productImage);
            }
        }
        return toVO(productMapper.selectById(productId), Collections.emptySet());
    }

    // 修改商品状态
    @Override
    public Void updateProduct(
            Long userId, Long productId, Integer status
    ) {
        Product product = productMapper.selectById(productId);
        if (product == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Product not found");

        if (!Objects.equals(product.getUserId(), userId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        String transition = product.getStatus() + "->" + status;
        if (!ALLOWED_TRANSITIONS.contains(transition))
            throw new BusinessException("Invalid status transition");

        updateStatus(product, status);
        return null;
    }

    // 删除商品
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void removeProduct(
            Long userId, Long productId
    ) {
        Product product = productMapper.selectById(productId);
        if (product == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Product not found");

        if (!Objects.equals(product.getUserId(), userId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        if (Objects.equals(product.getStatus(), ProductConstant.Status.SOLD_OUT))
            throw new BusinessException("Sold product cannot be removed");

        int locked = productMapper.lockForRemoval(
                productId, userId, product.getStatus(), LocalDateTime.now());
        if (locked != 1)
            throw new BusinessException("Product status has changed");

        productImageMapper.deleteByProductId(productId);
        favoriteMapper.deleteByProductId(productId);
        cartItemMapper.deleteByProductId(productId);
        productMapper.deleteById(product.getId());
        return null;
    }


    // =====================================================================================
    // ===========================>>>>> 管 理 员 操 作 <<<<<==================================
    // =====================================================================================

    // 获取商品
    @Override
    public PageResult<ProductVO> getProductList(
            Integer page, Integer pageSize, String keyword, Integer status
    ) {
        validatePagination(page, pageSize);
        PageHelper.startPage(page, pageSize);
        try {
            List<Product> products;
            products = productMapper.selectByKeywordOrCategoryOrCampusOrStatus(
                    keyword, null, null, status);
            PageInfo<Product> pageInfo = new PageInfo<>(products);

            // 批量预加载关联数据
            Map<Long, User> userMap = buildUserMap(products);
            Map<Long, Category> categoryMap = buildCategoryMap(products);
            Map<Long, List<ProductImage>> imageMap = buildImageMap(products);

            List<ProductVO> productVOs = products
                    .stream()
                    .map(product -> {
                        ProductVO productVO = ToVOUtil.toProductVO(
                                product, userMap.get(product.getUserId()),
                                categoryMap.get(product.getCategoryId()),
                                imageMap.getOrDefault(product.getId(), Collections.emptyList()));
                        return productVO;
                    })
                    .collect(Collectors.toList());

            return new PageResult<>(
                    productVOs, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()
            );
        } finally {
            PageHelper.clearPage();
        }
    }

    // 修改商品
    @Override
    public Void updateProduct(
            Long productId, Integer status
    ) {
        Product product = productMapper.selectById(productId);
        if (status == null
                || status < ProductConstant.Status.NEED_CHECK
                || status > ProductConstant.Status.DISABLE)
            throw new BusinessException("Invalid product status");

        if (product == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Product not found");

        if (Objects.equals(product.getStatus(), ProductConstant.Status.SOLD_OUT)
                && !Objects.equals(status, ProductConstant.Status.SOLD_OUT))
            throw new BusinessException("Sold product can only be restored by cancelling its order");

        updateStatus(product, status);
        return null;
    }


    // #===========>>>>> 内 部 私 有 工 具 方 法 <<<<<===========#

    // 单商品转 VO
    private ProductVO toVO(
            Product product, Set<Long> favIds
    ) {
        ProductVO productVO = ToVOUtil.toProductVO(
                product, userMapper.selectById(product.getUserId()),
                categoryMapper.selectById(product.getCategoryId()),
                productImageMapper.selectByProductId(product.getId())
        );
        productVO.setIsFavorite(favIds.contains(product.getId()));
        return productVO;
    }

    // 批量商品转 VO
    private ProductVO toVO(
            Product product, Set<Long> favIds, Map<Long, User> userMap,
            Map<Long, Category> categoryMap, Map<Long, List<ProductImage>> imageMap
    ) {
        ProductVO productVO = ToVOUtil.toProductVO(
                product, userMap.get(product.getUserId()),
                categoryMap.get(product.getCategoryId()),
                imageMap.getOrDefault(product.getId(), Collections.emptyList())
        );
        productVO.setIsFavorite(favIds.contains(product.getId()));
        return productVO;
    }

    private Map<Long, User> buildUserMap(List<Product> products) {
        List<Long> userIds = products
                .stream()
                .map(Product::getUserId)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectByIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    private Map<Long, Category> buildCategoryMap(List<Product> products) {
        List<Long> categoryIds = products
                .stream()
                .map(Product::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return categoryMapper.selectByIds(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
    }

    private Map<Long, List<ProductImage>> buildImageMap(List<Product> products) {
        List<Long> productIds = products
                .stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return productImageMapper.selectByProductIds(productIds)
                .stream()
                .collect(Collectors.groupingBy(
                        ProductImage::getProductId
                ));
    }

    // 批量预加载
    @NonNull
    private PageResult<ProductVO> batchPreloading(
            List<Product> productList, PageInfo<Product> productPageInfo, Set<Long> favoritedIds
    ) {
        Map<Long, User> userMap = buildUserMap(productList);
        Map<Long, Category> categoryMap = buildCategoryMap(productList);
        Map<Long, List<ProductImage>> imageMap = buildImageMap(productList);

        List<ProductVO> productVOs = productList
                .stream()
                .map(product -> toVO(
                        product, favoritedIds, userMap, categoryMap, imageMap))
                .collect(Collectors.toList());

        return new PageResult<>(
                productVOs, productPageInfo.getTotal(),
                productPageInfo.getPageNum(), productPageInfo.getPageSize()
        );
    }

    private List<Long> favoriteIds(Long userId) {
        return favoriteMapper.selectFavoriteProductIdsByUserId(userId);
    }

    private void validateProduct(Product product) {
        if (product == null || product.getTitle() == null || product.getTitle().isBlank()
                || product.getPrice() == null || product.getPrice().signum() < 0
                || product.getCategoryId() == null || product.getState() == null
                || product.getState() < ProductConstant.State.S_NEW
                || product.getState() > ProductConstant.State.B_NEW)
            throw new BusinessException("Invalid product details");

        if (categoryMapper.selectById(product.getCategoryId()) == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Category not found");

    }

    private void validatePagination(Integer page, Integer pageSize) {
        if (page == null || page < 1
                || pageSize == null || pageSize < 1 || pageSize > 100)
            throw new BusinessException("Invalid pagination");

    }

    private void updateStatus(Product product, Integer status) {
        int updated = productMapper.updateStatusIfMatches(
                product.getId(), product.getStatus(), status, LocalDateTime.now());
        if (updated != 1)
            throw new BusinessException("Product status has changed");

    }

}
