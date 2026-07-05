package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ProductQueryDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductImageMapper productImageMapper;
    @Autowired private FavoriteMapper favoriteMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private CategoryMapper categoryMapper;

    @Override
    public Result<PageResult<ProductVO>> getProductList(ProductQueryDTO query, Long currentUserId) {
        PageHelper.startPage(query.getPage(), query.getPageSize());
        List<Product> page;
        if (query.getKeyword() != null && !query.getKeyword().isEmpty())
            page = productMapper.search(query.getKeyword());
        else if (query.getCategoryId() != null && query.getCampus() != null)
            page = productMapper.selectByCategoryAndCampus(query.getCategoryId(), query.getCampus());
        else if (query.getCategoryId() != null)
            page = productMapper.selectByCategoryId(query.getCategoryId());
        else if (query.getCampus() != null)
            page = productMapper.selectByCampus(query.getCampus());
        else
            page = productMapper.selectAllActive();
        PageInfo<Product> pgInfo = new PageInfo<>(page);
        Set<Long> favIds = (currentUserId != null)
                ? new HashSet<>(favoriteMapper.selectProductIdsByUserId(currentUserId))
                : Collections.emptySet();
        List<ProductVO> vos = page.stream().map(p -> toVO(p, favIds)).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }

    @Override
    public Result<ProductVO> getProductDetail(Long id, Long currentUserId) {
        Product product = productMapper.selectById(id);
        if (product == null) return Result.error(404, "Product not found");
        product.setViewCount(product.getViewCount() != null ? product.getViewCount() + 1 : 1);
        productMapper.updateById(product);
        Set<Long> favIds = Collections.emptySet();
        if (currentUserId != null) favIds = new HashSet<>(favoriteMapper.selectProductIdsByUserId(currentUserId));
        return Result.success(toVO(productMapper.selectById(id), favIds));
    }

    @Override
    public Result<ProductVO> createProduct(Long userId, Product product, List<String> images) {
        product.setUserId(userId); product.setStatus(0); product.setViewCount(0);
        productMapper.insert(product);
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                ProductImage img = new ProductImage();
                img.setProductId(product.getId()); img.setUrl(images.get(i)); img.setSortOrder(i);
                productImageMapper.insert(img);
            }
        }
        return Result.success(toVO(productMapper.selectById(product.getId()), Collections.emptySet()));
    }

    @Override
    public Result<ProductVO> updateProduct(Long userId, Long productId, Product product, List<String> images) {
        Product existing = productMapper.selectById(productId);
        if (existing == null) return Result.error(404, "Product not found");
        product.setId(productId); product.setUserId(existing.getUserId());
        product.setStatus(existing.getStatus()); product.setViewCount(existing.getViewCount());
        productMapper.updateById(product);
        if (images != null) {
            productImageMapper.deleteByProductId(productId);
            for (int i = 0; i < images.size(); i++) {
                ProductImage img = new ProductImage();
                img.setProductId(productId); img.setUrl(images.get(i)); img.setSortOrder(i);
                productImageMapper.insert(img);
            }
        }
        return Result.success(toVO(productMapper.selectById(productId), Collections.emptySet()));
    }

    @Override
    public Result<Void> updateProductStatus(Long userId, Long productId, Integer status) {
        Product product = productMapper.selectById(productId);
        if (product == null) return Result.error(404, "Product not found");
        product.setStatus(status);
        productMapper.updateById(product);
        return Result.success();
    }

    @Override
    public Result<PageResult<ProductVO>> getMyProducts(Long userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Product> pg = productMapper.selectByUserId(userId);
        PageInfo<Product> pgInfo = new PageInfo<>(pg);
        Set<Long> favIds = new HashSet<>(favoriteMapper.selectProductIdsByUserId(userId));
        List<ProductVO> vos = pg.stream().map(p -> toVO(p, favIds)).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }

    private ProductVO toVO(Product product, Set<Long> favIds) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId()); vo.setUserId(product.getUserId()); vo.setCategoryId(product.getCategoryId());
        vo.setTitle(product.getTitle()); vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice()); vo.setOriginalPrice(product.getOriginalPrice());
        vo.setCondition(product.getCondition()); vo.setCampus(product.getCampus());
        vo.setStatus(product.getStatus()); vo.setViewCount(product.getViewCount());
        vo.setCreatedAt(product.getCreatedAt()); vo.setIsFavorited(favIds.contains(product.getId()));
        User seller = userMapper.selectById(product.getUserId());
        if (seller != null) { vo.setSellerName(seller.getNickname()); vo.setSellerAvatar(seller.getAvatar()); }
        Category cat = categoryMapper.selectById(product.getCategoryId());
        if (cat != null) vo.setCategoryName(cat.getName());
        vo.setImages(productImageMapper.selectByProductId(product.getId()).stream()
                .map(ProductImage::getUrl).collect(Collectors.toList()));
        return vo;
    }
}
