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

        // 批量预加载关联数据，避免N+1查询
        Map<Long, User> userMap = buildUserMap(page);
        Map<Long, Category> categoryMap = buildCategoryMap(page);
        Map<Long, List<String>> imageMap = buildImageMap(page);

        List<ProductVO> productVOs = page.stream()
                .map(product -> toVO(product, favoriteUserIds, userMap, categoryMap, imageMap))
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
     */
    @Override
    public Result<ProductVO> getProductDetail(
            Long id, Long currentUserId
    ) {
        Product product = productMapper.selectById(id);
        if (product == null)
            return Result.error(404, "Product not found");
        // 原子更新浏览量
        productMapper.incrementViewCount(id);
        product.setViewCount(product.getViewCount() != null ? product.getViewCount() + 1 : 1);
        Set<Long> favoritedProductsIds = Collections.emptySet();
        if (currentUserId != null)
            favoritedProductsIds = new HashSet<>(
                    favoriteMapper.selectFavoritedProductIdsByUserId(currentUserId)
            );
        return Result.success(toVO(product, favoritedProductsIds));
    }

    /**
     * 发布商品
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
     */
    @Override
    public Result<ProductVO> updateProduct(
            Long userId, Long productId, Product product, List<String> images
    ) {
        Product existing = productMapper.selectById(productId);
        if (existing == null)
            return Result.error(404, "Product not found");
        if (!Objects.equals(existing.getUserId(), userId))
            return Result.error(403, "Permission denied");
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

    /**
     * 修改商品状态
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
        if (!Objects.equals(product.getUserId(), userId))
            return Result.error(403, "Permission denied");
        productMapper.deleteById(product.getId());
        return Result.success();
    }

    /**
     * 获取我发布的商品列表
     */
    @Override
    public Result<PageResult<ProductVO>> getProductsByUser(
            Long userId, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<Product> productList = productMapper.selectByUserId(userId);
        PageInfo<Product> productPageInfo = new PageInfo<>(productList);
        Set<Long> favoritedIds = new HashSet<>(
                favoriteMapper.selectFavoritedProductIdsByUserId(userId)
        );

        // 批量预加载关联数据
        Map<Long, User> userMap = buildUserMap(productList);
        Map<Long, Category> categoryMap = buildCategoryMap(productList);
        Map<Long, List<String>> imageMap = buildImageMap(productList);

        List<ProductVO> productVOs = productList.stream()
                .map(product -> toVO(product, favoritedIds, userMap, categoryMap, imageMap))
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

    /**
     * 单商品转VO（逐条查询，用于单商品场景）
     */
    private ProductVO toVO(Product product, Set<Long> favIds) {
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

    /**
     * 批量商品转VO（使用预加载数据，避免N+1查询）
     */
    private ProductVO toVO(
            Product product, Set<Long> favIds,
            Map<Long, User> userMap,
            Map<Long, Category> categoryMap,
            Map<Long, List<String>> imageMap
    ) {
        ProductVO productVO = ToVOUtil.toProductVO(
                product,
                userMap.get(product.getUserId()),
                categoryMap.get(product.getCategoryId())
        );
        productVO.setIsFavorited(favIds.contains(product.getId()));
        productVO.setImages(imageMap.getOrDefault(product.getId(), Collections.emptyList()));
        return productVO;
    }

    private Map<Long, User> buildUserMap(List<Product> products) {
        List<Long> userIds = products.stream().map(Product::getUserId).distinct().collect(Collectors.toList());
        if (userIds.isEmpty()) return Collections.emptyMap();
        return userMapper.selectByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
    }

    private Map<Long, Category> buildCategoryMap(List<Product> products) {
        List<Long> categoryIds = products.stream().map(Product::getCategoryId).distinct().collect(Collectors.toList());
        if (categoryIds.isEmpty()) return Collections.emptyMap();
        return categoryMapper.selectByIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));
    }

    private Map<Long, List<String>> buildImageMap(List<Product> products) {
        List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
        if (productIds.isEmpty()) return Collections.emptyMap();
        return productImageMapper.selectByProductIds(productIds).stream()
                .collect(Collectors.groupingBy(
                        ProductImage::getProductId,
                        Collectors.mapping(ProductImage::getUrl, Collectors.toList())));
    }

    /*
    管理员操作
     */
    @Override
    public Result<PageResult<ProductVO>> getProductList(
            Integer page,
            Integer pageSize,
            String keyword,
            Integer status
    ) {
        PageHelper.startPage(page, pageSize);
        List<Product> all;
        if (keyword != null && !keyword.isEmpty())
            all = productMapper.searchByKeyword(keyword, ProductConstant.STATUS_ON_SALE);
        else
            all = productMapper.selectAll();
        if (status != null)
            all = all.stream()
                    .filter(product -> product.getStatus().equals(status))
                    .collect(Collectors.toList());
        PageInfo<Product> pageInfo = new PageInfo<>(all);

        // 批量预加载关联数据
        Map<Long, User> userMap = buildUserMap(all);
        Map<Long, Category> categoryMap = buildCategoryMap(all);
        Map<Long, List<String>> imageMap = buildImageMap(all);

        List<ProductVO> productVOs = all.stream()
                .map(product -> {
                    ProductVO productVO = ToVOUtil.toProductVO(
                            product,
                            userMap.get(product.getUserId()),
                            categoryMap.get(product.getCategoryId()));
                    productVO.setImages(
                            imageMap.getOrDefault(product.getId(), Collections.emptyList()));
                    return productVO;
                }).collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        productVOs,
                        pageInfo.getTotal(),
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize()
                )
        );
    }

    @Override
    public Result<Void> updateProductStatus(
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

}
