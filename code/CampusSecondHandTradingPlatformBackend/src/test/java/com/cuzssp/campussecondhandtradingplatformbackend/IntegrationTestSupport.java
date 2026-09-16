package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderInfoRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductImageRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ProductImage;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.UtcTime;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CartItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.FavoriteMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderInfoMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(locations = "classpath:remake-test.properties")
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected CategoryMapper categoryMapper;

    @Autowired
    protected ProductMapper productMapper;

    @Autowired
    protected ProductImageMapper productImageMapper;

    @Autowired
    protected CartItemMapper cartItemMapper;

    @Autowired
    protected FavoriteMapper favoriteMapper;

    @Autowired
    protected OrderInfoMapper orderInfoMapper;

    @Autowired
    protected OrderItemMapper orderItemMapper;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected OrderService orderService;

    protected User insertUser(String username) {
        return insertUser(username, UserConstant.Role.USER);
    }

    protected User insertUser(String username, int role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("test-password");
        user.setNickname(username);
        user.setRole(role);
        user.setStatus(UserConstant.Status.ACTIVE);
        user.setCreditScore(UserConstant.CREDIT_SCORE_DEFAULT);
        user.setCreatedAt(UtcTime.now());
        user.setUpdatedAt(UtcTime.now());
        userMapper.insert(user);
        return user;
    }

    protected Category insertCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setIcon("");
        category.setSortOrder(1);
        category.setCreatedAt(UtcTime.now());
        categoryMapper.insert(category);
        return category;
    }

    protected Product createProduct(
            Long sellerId, Long categoryId, int status, String title
    ) {
        return createProduct(
                sellerId, categoryId, status, title,
                new BigDecimal("99.00"), 2, "East Campus", null
        );
    }

    protected Product createProduct(
            Long sellerId, Long categoryId, int status, String title,
            String imageUrl
    ) {
        return createProduct(
                sellerId, categoryId, status, title,
                new BigDecimal("99.00"), 2, "East Campus", imageUrl
        );
    }

    protected Product createProduct(
            Long sellerId, Long categoryId, int status, String title,
            BigDecimal price, int productState, String campus, String imageUrl
    ) {
        Product product = new Product();
        product.setUserId(sellerId);
        product.setCategoryId(categoryId);
        product.setTitle(title);
        product.setDescription(title + " description");
        product.setPrice(price);
        product.setOriginalPrice(price.add(new BigDecimal("10.00")));
        product.setState(productState);
        product.setCampus(campus);
        product.setStatus(status);
        product.setViewCount(ProductConstant.VIEW_COUNT_DEFAULT);
        product.setIsDeleted(ProductConstant.DEFAULT_DELETED);
        product.setDeletedAt(null);
        product.setCreatedAt(UtcTime.now());
        product.setUpdatedAt(UtcTime.now());
        productMapper.insert(product);

        if (imageUrl != null) {
            insertProductImage(product.getId(), imageUrl, 1);
        }
        return product;
    }

    protected ProductImage insertProductImage(Long productId, String url, int sortOrder) {
        ProductImage image = new ProductImage();
        image.setProductId(productId);
        image.setUrl(url);
        image.setSortOrder(sortOrder);
        productImageMapper.insert(image);
        return image;
    }

    protected ProductRequest productRequest(String title, List<String> imageUrls) {
        ProductRequest request = new ProductRequest();
        request.setTitle(title);
        request.setDescription(title + " description");
        request.setPrice(new BigDecimal("39.90"));
        request.setOriginalPrice(new BigDecimal("59.90"));
        request.setState(ProductConstant.State.A_NEW);
        request.setCampus("East Campus");
        request.setCategoryId(1L);

        List<ProductImageRequest> images = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            ProductImageRequest image = new ProductImageRequest();
            image.setUrl(imageUrl);
            images.add(image);
        }
        request.setImages(images);
        return request;
    }

    protected OrderInfoRequest createOrderRequest(Long productId) {
        OrderInfoRequest request = new OrderInfoRequest();
        request.setProductId(productId);
        request.setRemark("test order");
        return request;
    }
}
