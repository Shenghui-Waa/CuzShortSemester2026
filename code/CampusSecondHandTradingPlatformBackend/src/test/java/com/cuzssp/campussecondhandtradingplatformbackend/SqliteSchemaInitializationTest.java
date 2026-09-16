package com.cuzssp.campussecondhandtradingplatformbackend;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SqliteSchemaInitializationTest extends IntegrationTestSupport {

    private static final Set<String> EXPECTED_TABLES = Set.of(
            "announcement",
            "cart_item",
            "category",
            "chat_message",
            "favorite",
            "order_info",
            "order_item",
            "product",
            "product_image",
            "review",
            "revoked_token",
            "user"
    );

    @Test
    void createsAllTablesAndEnablesForeignKeys() {
        Set<String> tables = new HashSet<>(
                jdbcTemplate.queryForList(
                        "SELECT name FROM sqlite_master "
                                + "WHERE type = 'table' AND name NOT LIKE 'sqlite_%'",
                        String.class
                )
        );
        assertThat(tables).isEqualTo(EXPECTED_TABLES);

        Integer foreignKeysEnabled = jdbcTemplate.queryForObject(
                "PRAGMA foreign_keys",
                Integer.class
        );
        assertThat(foreignKeysEnabled).isEqualTo(1);
    }

    @Test
    void supportsAutoIncrementIdsAndBasicInsertsForEveryTable() {
        long userId = insertAndGetId(
                "INSERT INTO user (username, password, role, status, credit_score) "
                        + "VALUES (?, ?, 0, 1, 100)",
                "schema-user",
                "password"
        );
        long firstCategoryId = insertAndGetId(
                "INSERT INTO category (name, sort_order) VALUES (?, ?)",
                "schema-category-1",
                10
        );
        long secondCategoryId = insertAndGetId(
                "INSERT INTO category (name, sort_order) VALUES (?, ?)",
                "schema-category-2",
                11
        );
        long productId = insertAndGetId(
                "INSERT INTO product "
                        + "(user_id, category_id, title, price, state, campus, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                userId,
                firstCategoryId,
                "schema-product",
                new BigDecimal("10.00"),
                1,
                "East Campus",
                0
        );
        long productImageId = insertAndGetId(
                "INSERT INTO product_image (product_id, url, sort_order) "
                        + "VALUES (?, ?, ?)",
                productId,
                "https://example.test/schema.jpg",
                1
        );
        long orderId = insertAndGetId(
                "INSERT INTO order_info "
                        + "(order_no, buyer_id, seller_id, total_amount, status) "
                        + "VALUES (?, ?, ?, ?, ?)",
                "schema-order",
                userId,
                userId,
                new BigDecimal("10.00"),
                0
        );
        long orderItemId = insertAndGetId(
                "INSERT INTO order_item "
                        + "(order_id, product_id, price, product_title, product_state) "
                        + "VALUES (?, ?, ?, ?, ?)",
                orderId,
                productId,
                new BigDecimal("10.00"),
                "schema-product",
                1
        );
        long cartItemId = insertAndGetId(
                "INSERT INTO cart_item (user_id, product_id) VALUES (?, ?)",
                userId,
                productId
        );
        long favoriteId = insertAndGetId(
                "INSERT INTO favorite (user_id, product_id) VALUES (?, ?)",
                userId,
                productId
        );
        long chatMessageId = insertAndGetId(
                "INSERT INTO chat_message "
                        + "(sender_id, receiver_id, product_id, content, is_read) "
                        + "VALUES (?, ?, ?, ?, ?)",
                userId,
                userId,
                productId,
                "schema message",
                0
        );
        long reviewId = insertAndGetId(
                "INSERT INTO review "
                        + "(order_id, reviewer_id, target_id, rating, content) "
                        + "VALUES (?, ?, ?, ?, ?)",
                orderId,
                userId,
                userId,
                5,
                "schema review"
        );
        long announcementId = insertAndGetId(
                "INSERT INTO announcement (title, content) VALUES (?, ?)",
                "schema announcement",
                "schema content"
        );
        int revokedTokenCount = jdbcTemplate.update(
                "INSERT INTO revoked_token (jti, expires_at) VALUES (?, ?)",
                "schema-jti",
                new Date(System.currentTimeMillis() + 60_000)
        );

        assertThat(secondCategoryId).isGreaterThan(firstCategoryId);
        assertThat(userId).isPositive();
        assertThat(productId).isPositive();
        assertThat(productImageId).isPositive();
        assertThat(orderId).isPositive();
        assertThat(orderItemId).isPositive();
        assertThat(cartItemId).isPositive();
        assertThat(favoriteId).isPositive();
        assertThat(chatMessageId).isPositive();
        assertThat(reviewId).isPositive();
        assertThat(announcementId).isPositive();
        assertThat(revokedTokenCount).isEqualTo(1);
    }

    private long insertAndGetId(String sql, Object... parameters) {
        jdbcTemplate.update(sql, parameters);
        Long id = jdbcTemplate.queryForObject(
                "SELECT last_insert_rowid()",
                Long.class
        );
        assertThat(id).isNotNull();
        return id;
    }
}
