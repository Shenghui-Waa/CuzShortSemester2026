package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ChatMessageConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CartItemRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CategoryRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ChatMessageRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.FavoriteRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderInfoRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderItemRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductImageRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Announcement;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.CartItem;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ChatMessage;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Favorite;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderInfo;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderItem;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ProductImage;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Review;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.PasswordProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ToEntityUtilTest {

    private final PasswordProvider passwordProvider =
            new PasswordProvider(new BCryptPasswordEncoder());

    @Test
    void toUserEntity_appliesCreateDefaultsAndServerControlledFields() {
        UserRequest request = new UserRequest();
        request.setUsername("new-user");
        request.setPassword("plain-password");
        request.setNickname(null);
        request.setAvatar("https://example.test/avatar.jpg");
        request.setPhone("13800000000");
        request.setEmail("user@example.test");
        request.setSchool("Test University");
        request.setCampus("East Campus");

        User user = ToEntityUtil.toUserEntity(
                request,
                passwordProvider,
                UserConstant.Role.USER
        );

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isEqualTo("new-user");
        assertThat(user.getPassword()).isNotEqualTo("plain-password");
        assertThat(passwordProvider.matches(
                "plain-password",
                user.getPassword()
        )).isTrue();
        assertThat(user.getNickname()).isEqualTo("new-user");
        assertThat(user.getAvatar()).isEqualTo(
                "https://example.test/avatar.jpg"
        );
        assertThat(user.getPhone()).isEqualTo("13800000000");
        assertThat(user.getEmail()).isEqualTo("user@example.test");
        assertThat(user.getSchool()).isEqualTo("Test University");
        assertThat(user.getCampus()).isEqualTo("East Campus");
        assertThat(user.getRole()).isEqualTo(UserConstant.Role.USER);
        assertThat(user.getStatus()).isEqualTo(UserConstant.Status.ACTIVE);
        assertThat(user.getCreditScore())
                .isEqualTo(UserConstant.CREDIT_SCORE_DEFAULT);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateUserEntity_updatesEditableFieldsAndPreservesServerFields() {
        User existing = new User();
        existing.setId(10L);
        existing.setUsername("original-user");
        existing.setPassword("original-password");
        existing.setNickname("original-nickname");
        existing.setAvatar("https://example.test/original.jpg");
        existing.setPhone("10086");
        existing.setEmail("original@example.test");
        existing.setSchool("Original University");
        existing.setCampus("West Campus");
        existing.setRole(UserConstant.Role.ADMIN);
        existing.setStatus(UserConstant.Status.INACTIVE);
        existing.setCreditScore(42);
        existing.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        existing.setUpdatedAt(LocalDateTime.of(2020, 1, 2, 0, 0));

        UserRequest request = new UserRequest();
        request.setUsername("forged-user");
        request.setPassword("forged-password");
        request.setNickname("updated-nickname");
        request.setAvatar("https://example.test/forged.jpg");
        request.setPhone("13900000000");
        request.setEmail("updated@example.test");
        request.setSchool("Updated University");
        request.setCampus("North Campus");

        User updated = ToEntityUtil.updateUserEntity(existing, request);

        assertThat(updated).isSameAs(existing);
        assertThat(updated.getId()).isEqualTo(10L);
        assertThat(updated.getUsername()).isEqualTo("original-user");
        assertThat(updated.getPassword()).isEqualTo("original-password");
        assertThat(updated.getAvatar())
                .isEqualTo("https://example.test/original.jpg");
        assertThat(updated.getRole()).isEqualTo(UserConstant.Role.ADMIN);
        assertThat(updated.getStatus())
                .isEqualTo(UserConstant.Status.INACTIVE);
        assertThat(updated.getCreditScore()).isEqualTo(42);
        assertThat(updated.getNickname()).isEqualTo("updated-nickname");
        assertThat(updated.getPhone()).isEqualTo("13900000000");
        assertThat(updated.getEmail()).isEqualTo("updated@example.test");
        assertThat(updated.getSchool()).isEqualTo("Updated University");
        assertThat(updated.getCampus()).isEqualTo("North Campus");
        assertThat(updated.getUpdatedAt())
                .isAfter(LocalDateTime.of(2020, 1, 2, 0, 0));
    }

    @Test
    void toCategoryEntity_mapsEditableFields() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Books");
        request.setIcon("https://example.test/books.svg");
        request.setSortOrder(7);

        Category category = ToEntityUtil.toCategoryEntity(request);

        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isEqualTo("Books");
        assertThat(category.getIcon())
                .isEqualTo("https://example.test/books.svg");
        assertThat(category.getSortOrder()).isEqualTo(7);
        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    void toProductAndImageEntities_mapFieldsAndDefaultImageOrder() {
        ProductRequest request = new ProductRequest();
        request.setTitle("Calculus Textbook");
        request.setDescription("Clean copy");
        request.setPrice(new BigDecimal("29.90"));
        request.setOriginalPrice(new BigDecimal("59.80"));
        request.setState(2);
        request.setCampus("East Campus");
        request.setCategoryId(3L);

        Product product = ToEntityUtil.toProductEntity(request);

        assertThat(product.getId()).isNull();
        assertThat(product.getUserId()).isNull();
        assertThat(product.getCategoryId()).isEqualTo(3L);
        assertThat(product.getTitle()).isEqualTo("Calculus Textbook");
        assertThat(product.getDescription()).isEqualTo("Clean copy");
        assertThat(product.getPrice()).isEqualByComparingTo("29.90");
        assertThat(product.getOriginalPrice()).isEqualByComparingTo("59.80");
        assertThat(product.getState()).isEqualTo(2);
        assertThat(product.getCampus()).isEqualTo("East Campus");
        assertThat(product.getStatus()).isNull();
        assertThat(product.getViewCount()).isNull();
        assertThat(product.getIsDeleted()).isNull();

        ProductImageRequest defaultOrderRequest = new ProductImageRequest();
        defaultOrderRequest.setUrl("https://example.test/default.jpg");
        ProductImage defaultOrderImage = ToEntityUtil.toProductImageEntity(
                defaultOrderRequest,
                99L,
                4
        );

        assertThat(defaultOrderImage.getProductId()).isEqualTo(99L);
        assertThat(defaultOrderImage.getUrl())
                .isEqualTo("https://example.test/default.jpg");
        assertThat(defaultOrderImage.getSortOrder()).isEqualTo(4);

        ProductImageRequest explicitOrderRequest = new ProductImageRequest();
        explicitOrderRequest.setUrl("https://example.test/explicit.jpg");
        explicitOrderRequest.setSortOrder(2);
        ProductImage explicitOrderImage = ToEntityUtil.toProductImageEntity(
                explicitOrderRequest,
                100L,
                1
        );

        assertThat(explicitOrderImage.getProductId()).isEqualTo(100L);
        assertThat(explicitOrderImage.getSortOrder()).isEqualTo(2);
    }

    @Test
    void toOrderInfoEntity_appliesOrderDefaultsAndProductSnapshotAmount() {
        Product product = new Product();
        product.setId(8L);
        product.setUserId(18L);
        product.setPrice(new BigDecimal("68.50"));

        OrderInfoRequest request = new OrderInfoRequest();
        request.setProductId(product.getId());
        request.setRemark("Leave at the dormitory gate");

        OrderInfo order = ToEntityUtil.toOrderInfoEntity(
                28L,
                product,
                request
        );

        assertThat(order.getId()).isNull();
        assertThat(order.getOrderNo()).matches("[0-9a-f]{20}");
        assertThat(order.getBuyerId()).isEqualTo(28L);
        assertThat(order.getSellerId()).isEqualTo(18L);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("68.50");
        assertThat(order.getStatus()).isEqualTo(OrderInfoConstant.Status.WAIT_PAY);
        assertThat(order.getRefundStatus())
                .isEqualTo(OrderInfoConstant.RefundStatus.NONE);
        assertThat(order.getRefundedAt()).isNull();
        assertThat(order.getRemark())
                .isEqualTo("Leave at the dormitory gate");
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void toOrderItemEntity_mapsSnapshotFields() {
        OrderInfo order = new OrderInfo();
        order.setId(31L);

        OrderItemRequest request = new OrderItemRequest();
        request.setProductId(41L);
        request.setPrice(new BigDecimal("19.90"));
        request.setProductTitle("Desk Lamp");
        request.setProductImage("https://example.test/lamp.jpg");
        request.setProductState(3);

        OrderItem orderItem = ToEntityUtil.toOrderItemEntity(order, request);

        assertThat(orderItem.getId()).isNull();
        assertThat(orderItem.getOrderId()).isEqualTo(31L);
        assertThat(orderItem.getProductId()).isEqualTo(41L);
        assertThat(orderItem.getPrice()).isEqualByComparingTo("19.90");
        assertThat(orderItem.getProductTitle()).isEqualTo("Desk Lamp");
        assertThat(orderItem.getProductImage())
                .isEqualTo("https://example.test/lamp.jpg");
        assertThat(orderItem.getProductState()).isEqualTo(3);
        assertThat(orderItem.getCreatedAt()).isNotNull();
    }

    @Test
    void toCartItemEntity_usesAuthenticatedUserId() {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(51L);

        CartItem cartItem = ToEntityUtil.toCartItemEntity(61L, request);

        assertThat(cartItem.getId()).isNull();
        assertThat(cartItem.getUserId()).isEqualTo(61L);
        assertThat(cartItem.getProductId()).isEqualTo(51L);
        assertThat(cartItem.getCreatedAt()).isNotNull();
    }

    @Test
    void toFavoriteEntity_usesAuthenticatedUserId() {
        FavoriteRequest request = new FavoriteRequest();
        request.setProductId(71L);

        Favorite favorite = ToEntityUtil.toFavoriteEntity(81L, request);

        assertThat(favorite.getId()).isNull();
        assertThat(favorite.getUserId()).isEqualTo(81L);
        assertThat(favorite.getProductId()).isEqualTo(71L);
        assertThat(favorite.getCreatedAt()).isNotNull();
    }

    @Test
    void toChatMessageEntity_setsSenderAndReadDefaults() {
        ChatMessageRequest request = new ChatMessageRequest();
        request.setReceiverId(92L);
        request.setProductId(93L);
        request.setContent("Is this still available?");

        ChatMessage message = ToEntityUtil.toChatMessageEntity(91L, request);

        assertThat(message.getId()).isNull();
        assertThat(message.getSenderId()).isEqualTo(91L);
        assertThat(message.getReceiverId()).isEqualTo(92L);
        assertThat(message.getProductId()).isEqualTo(93L);
        assertThat(message.getContent()).isEqualTo("Is this still available?");
        assertThat(message.getIsRead())
                .isEqualTo(ChatMessageConstant.ReadStatus.NO);
        assertThat(message.getCreatedAt()).isNotNull();
    }

    @Test
    void toReviewEntity_usesAuthenticatedReviewerId() {
        ReviewRequest request = new ReviewRequest();
        request.setOrderId(101L);
        request.setTargetId(102L);
        request.setRating(5);
        request.setContent("Reliable seller");

        Review review = ToEntityUtil.toReviewEntity(103L, request);

        assertThat(review.getId()).isNull();
        assertThat(review.getOrderId()).isEqualTo(101L);
        assertThat(review.getReviewerId()).isEqualTo(103L);
        assertThat(review.getTargetId()).isEqualTo(102L);
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getContent()).isEqualTo("Reliable seller");
        assertThat(review.getCreatedAt()).isNotNull();
    }

    @Test
    void toAnnouncementEntity_mapsContentAndTimestamps() {
        AnnouncementRequest request = new AnnouncementRequest();
        request.setTitle("Maintenance");
        request.setContent("The service will be upgraded tonight.");

        Announcement announcement = ToEntityUtil.toAnnouncementEntity(request);

        assertThat(announcement.getId()).isNull();
        assertThat(announcement.getTitle()).isEqualTo("Maintenance");
        assertThat(announcement.getContent())
                .isEqualTo("The service will be upgraded tonight.");
        assertThat(announcement.getCreatedAt()).isNotNull();
        assertThat(announcement.getUpdatedAt()).isNotNull();
    }
}
