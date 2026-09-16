package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.CartItem;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.UtcTime;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.OrderItemVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.OrderVO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Test
    void createOrderSnapshotsProductAndClearsCart() {
        User seller = insertUser("order-snapshot-seller");
        User buyer = insertUser("order-snapshot-buyer");
        User otherBuyer = insertUser("order-snapshot-other");
        Category category = insertCategory("order-snapshot-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.ON_SALE,
                "snapshot product",
                new BigDecimal("88.00"),
                ProductConstant.State.A_NEW,
                "East Campus",
                "https://example.test/snapshot.jpg"
        );
        insertCartItem(buyer.getId(), product.getId());
        insertCartItem(otherBuyer.getId(), product.getId());

        OrderVO order = orderService.createOrder(
                buyer.getId(),
                createOrderRequest(product.getId())
        );

        assertThat(productMapper.selectById(product.getId()).getStatus())
                .isEqualTo(ProductConstant.Status.SOLD_OUT);
        assertThat(cartItemMapper.countByUserIdAndProductId(
                buyer.getId(),
                product.getId()
        )).isZero();
        assertThat(cartItemMapper.countByUserIdAndProductId(
                otherBuyer.getId(),
                product.getId()
        )).isZero();
        assertThat(order.getRefundStatus()).isEqualTo(OrderInfoConstant.RefundStatus.NONE);
        assertThat(order.getItems()).hasSize(1);

        OrderItemVO item = order.getItems().get(0);
        assertThat(item.getProductId()).isEqualTo(product.getId());
        assertThat(item.getProductTitle()).isEqualTo("snapshot product");
        assertThat(item.getProductImage()).isEqualTo("https://example.test/snapshot.jpg");
        assertThat(item.getProductState()).isEqualTo(ProductConstant.State.A_NEW);
        assertThat(item.getPrice()).isEqualByComparingTo(new BigDecimal("88.00"));

        jdbcTemplate.update(
                "UPDATE product SET title = ?, state = ? WHERE id = ?",
                "changed product",
                ProductConstant.State.B_NEW,
                product.getId()
        );
        jdbcTemplate.update(
                "DELETE FROM product_image WHERE product_id = ?",
                product.getId()
        );

        OrderVO reloaded = orderService.getOrderDetail(buyer.getId(), order.getId());
        assertThat(reloaded.getItems()).hasSize(1);
        assertThat(reloaded.getItems().get(0).getProductTitle())
                .isEqualTo("snapshot product");
        assertThat(reloaded.getItems().get(0).getProductImage())
                .isEqualTo("https://example.test/snapshot.jpg");
        assertThat(reloaded.getItems().get(0).getProductState())
                .isEqualTo(ProductConstant.State.A_NEW);
    }

    @Test
    void cancelWaitPayOrderRestoresProductWithoutRefund() {
        OrderScenario scenario = createOrderScenario(
                "wait-pay-seller",
                "wait-pay-buyer",
                "wait-pay-product"
        );

        orderService.cancelOrder(scenario.buyer().getId(), scenario.order().getId());

        OrderVO cancelled = orderService.getOrderDetail(
                scenario.buyer().getId(),
                scenario.order().getId()
        );
        assertThat(cancelled.getStatus()).isEqualTo(OrderInfoConstant.Status.CANCELLED);
        assertThat(cancelled.getRefundStatus()).isEqualTo(OrderInfoConstant.RefundStatus.NONE);
        assertThat(cancelled.getRefundedAt()).isNull();
        assertThat(productMapper.selectById(scenario.product().getId()).getStatus())
                .isEqualTo(ProductConstant.Status.ON_SALE);
    }

    @Test
    void cancelWaitDeliverOrderRefundsAndRestoresProductOnSale() {
        OrderScenario scenario = createOrderScenario(
                "wait-deliver-seller",
                "wait-deliver-buyer",
                "wait-deliver-product"
        );
        orderService.payOrder(scenario.buyer().getId(), scenario.order().getId());

        orderService.cancelOrder(scenario.buyer().getId(), scenario.order().getId());

        OrderVO cancelled = orderService.getOrderDetail(
                scenario.buyer().getId(),
                scenario.order().getId()
        );
        assertThat(cancelled.getStatus()).isEqualTo(OrderInfoConstant.Status.CANCELLED);
        assertThat(cancelled.getRefundStatus())
                .isEqualTo(OrderInfoConstant.RefundStatus.REFUNDED);
        assertThat(cancelled.getRefundedAt()).isNotNull();
        assertThat(productMapper.selectById(scenario.product().getId()).getStatus())
                .isEqualTo(ProductConstant.Status.ON_SALE);
    }

    @Test
    void cancelWaitReceiveOrderRefundsAndDisablesProduct() {
        OrderScenario scenario = createOrderScenario(
                "wait-receive-seller",
                "wait-receive-buyer",
                "wait-receive-product"
        );
        orderService.payOrder(scenario.buyer().getId(), scenario.order().getId());
        orderService.shipOrder(scenario.seller().getId(), scenario.order().getId());

        orderService.cancelOrder(scenario.buyer().getId(), scenario.order().getId());

        OrderVO cancelled = orderService.getOrderDetail(
                scenario.buyer().getId(),
                scenario.order().getId()
        );
        assertThat(cancelled.getStatus()).isEqualTo(OrderInfoConstant.Status.CANCELLED);
        assertThat(cancelled.getRefundStatus())
                .isEqualTo(OrderInfoConstant.RefundStatus.REFUNDED);
        assertThat(cancelled.getRefundedAt()).isNotNull();
        assertThat(productMapper.selectById(scenario.product().getId()).getStatus())
                .isEqualTo(ProductConstant.Status.DISABLE);
    }

    private OrderScenario createOrderScenario(
            String sellerName, String buyerName, String productTitle
    ) {
        User seller = insertUser(sellerName);
        User buyer = insertUser(buyerName);
        Category category = insertCategory(productTitle + "-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.ON_SALE,
                productTitle
        );
        OrderVO order = orderService.createOrder(
                buyer.getId(),
                createOrderRequest(product.getId())
        );
        return new OrderScenario(seller, buyer, product, order);
    }

    private void insertCartItem(Long userId, Long productId) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setCreatedAt(UtcTime.now());
        cartItemMapper.insert(cartItem);
    }

    private record OrderScenario(
            User seller, User buyer, Product product, OrderVO order
    ) {
    }
}
