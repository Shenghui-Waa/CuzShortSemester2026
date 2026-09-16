package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ProductVO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductDetailAccessIntegrationTest extends IntegrationTestSupport {

    @Test
    void publicAndOtherUserViewsIncrementOnSaleProductViewCount() {
        User seller = insertUser("detail-view-seller");
        User viewer = insertUser("detail-view-user");
        Category category = insertCategory("detail-view-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.ON_SALE,
                "detail view product"
        );

        ProductVO anonymousView = productService.getProductDetail(product.getId(), null);
        ProductVO userView = productService.getProductDetail(product.getId(), viewer.getId());

        assertThat(anonymousView.getViewCount()).isEqualTo(1);
        assertThat(userView.getViewCount()).isEqualTo(2);
    }

    @Test
    void ownerAndAdminViewsDoNotIncrementViewCount() {
        User seller = insertUser("detail-owner-seller");
        Category category = insertCategory("detail-owner-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.NEED_CHECK,
                "detail owner product"
        );

        ProductVO ownerView = productService.getProductDetail(product.getId(), seller.getId());
        ProductVO adminView = productService.getProductDetailForAdmin(product.getId());

        assertThat(ownerView.getViewCount()).isZero();
        assertThat(adminView.getViewCount()).isZero();
        assertThat(productMapper.selectById(product.getId()).getViewCount()).isZero();
    }

    @Test
    void nonOwnerCannotReadNonOnSaleProductAndViewCountIsUnchanged() {
        User seller = insertUser("detail-hidden-seller");
        User viewer = insertUser("detail-hidden-user");
        Category category = insertCategory("detail-hidden-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.NEED_CHECK,
                "detail hidden product"
        );

        assertThatThrownBy(() -> productService.getProductDetail(
                product.getId(),
                viewer.getId()
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", Result.Code.NOT_FOUND);

        assertThat(productMapper.selectById(product.getId()).getViewCount()).isZero();
    }
}
