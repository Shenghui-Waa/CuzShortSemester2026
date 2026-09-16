package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.CartItem;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Favorite;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.UtcTime;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ProductVO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceIntegrationTest extends IntegrationTestSupport {

    @Test
    void createAndUpdateProduct_replacesAndClearsImages() {
        User seller = insertUser("product-image-seller");
        Category category = insertCategory("image-category");
        ProductRequest createRequest = productRequest(
                "image product",
                List.of(
                        "https://example.test/one.jpg",
                        "https://example.test/two.jpg"
                )
        );
        createRequest.setCategoryId(category.getId());
        ProductVO created = productService.createProduct(
                seller.getId(),
                createRequest
        );

        assertThat(created.getId()).isPositive();
        assertThat(created.getStatus()).isEqualTo(ProductConstant.Status.NEED_CHECK);
        assertThat(created.getImages()).containsExactly(
                "https://example.test/one.jpg",
                "https://example.test/two.jpg"
        );

        ProductVO updated = productService.updateProduct(
                seller.getId(),
                created.getId(),
                productRequest("image product updated", List.of())
        );

        assertThat(updated.getImages()).isEmpty();
        assertThat(productImageMapper.selectByProductId(created.getId())).isEmpty();
    }

    @Test
    void removeProduct_softDeletesAndCleansCartAndFavorite() {
        User seller = insertUser("remove-product-seller");
        User buyer = insertUser("remove-product-buyer");
        Category category = insertCategory("remove-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.ON_SALE,
                "remove product",
                "https://example.test/remove.jpg"
        );

        CartItem cartItem = new CartItem();
        cartItem.setUserId(buyer.getId());
        cartItem.setProductId(product.getId());
        cartItem.setCreatedAt(UtcTime.now());
        cartItemMapper.insert(cartItem);

        Favorite favorite = new Favorite();
        favorite.setUserId(buyer.getId());
        favorite.setProductId(product.getId());
        favorite.setCreatedAt(UtcTime.now());
        favoriteMapper.insert(favorite);

        productService.removeProduct(seller.getId(), product.getId());

        Map<String, Object> deletedProduct = jdbcTemplate.queryForMap(
                "SELECT status, is_deleted, deleted_at FROM product WHERE id = ?",
                product.getId()
        );
        assertThat(((Number) deletedProduct.get("is_deleted")).intValue()).isEqualTo(1);
        assertThat(deletedProduct.get("deleted_at")).isNotNull();
        assertThat(productMapper.selectById(product.getId())).isNull();
        assertThat(cartItemMapper.countByUserIdAndProductId(
                buyer.getId(),
                product.getId()
        )).isZero();
        assertThat(favoriteMapper.countByUserIdAndProductId(
                buyer.getId(),
                product.getId()
        )).isZero();
        assertThat(productImageMapper.selectByProductId(product.getId())).hasSize(1);

        assertThatThrownBy(() -> productService.getProductDetail(product.getId(), null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", Result.Code.NOT_FOUND);
    }

    @Test
    void adminCanReviewProductButCannotMarkItSoldOut() {
        User seller = insertUser("admin-status-seller");
        Category category = insertCategory("admin-status-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.NEED_CHECK,
                "admin status product"
        );

        productService.updateProduct(product.getId(), ProductConstant.Status.ON_SALE);

        assertThat(productMapper.selectById(product.getId()).getStatus())
                .isEqualTo(ProductConstant.Status.ON_SALE);

        assertThatThrownBy(() -> productService.updateProduct(
                product.getId(),
                ProductConstant.Status.SOLD_OUT
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", Result.Code.BAD_REQUEST);

        assertThat(productMapper.selectById(product.getId()).getStatus())
                .isEqualTo(ProductConstant.Status.ON_SALE);
    }
}
