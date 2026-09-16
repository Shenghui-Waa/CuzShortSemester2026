package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class RequestBindingControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void cartAndFavoriteUseJsonRequestBody() throws Exception {
        User seller = insertUser("binding-cart-seller");
        User buyer = insertUser("binding-cart-buyer");
        Category category = insertCategory("binding-cart-category");
        Product product = createProduct(
                seller.getId(),
                category.getId(),
                ProductConstant.Status.ON_SALE,
                "binding cart product"
        );
        String token = tokenProvider.generateToken(buyer);
        String body = """
                {
                  "productId": %d
                }
                """.formatted(product.getId());

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/favorite")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        assertThat(cartItemMapper.countByUserIdAndProductId(
                buyer.getId(),
                product.getId()
        )).isEqualTo(1);
        assertThat(favoriteMapper.countByUserIdAndProductId(
                buyer.getId(),
                product.getId()
        )).isEqualTo(1);
    }

    @Test
    void cartAndFavoriteRejectMissingProductId() throws Exception {
        User buyer = insertUser("binding-invalid-buyer");
        String token = tokenProvider.generateToken(buyer);

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/favorite")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void categoryPostAndPutUseCategoryRequest() throws Exception {
        User admin = insertUser("binding-admin", UserConstant.Role.ADMIN);
        String token = tokenProvider.generateToken(admin);

        mockMvc.perform(post("/api/admin/category")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Binding Category",
                                  "icon": "https://example.test/category.svg",
                                  "sortOrder": 20
                                }
                                """))
                .andExpect(status().isOk());

        Long categoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM category WHERE name = ?",
                Long.class,
                "Binding Category"
        );
        assertThat(categoryId).isPositive();

        mockMvc.perform(put("/api/admin/category/{id}", categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Binding Category Updated",
                                  "icon": "https://example.test/updated.svg",
                                  "sortOrder": 21
                                }
                                """))
                .andExpect(status().isOk());

        Map<String, Object> category = jdbcTemplate.queryForMap(
                "SELECT name, icon, sort_order FROM category WHERE id = ?",
                categoryId
        );
        assertThat(category.get("name")).isEqualTo("Binding Category Updated");
        assertThat(category.get("icon"))
                .isEqualTo("https://example.test/updated.svg");
        assertThat(((Number) category.get("sort_order")).intValue())
                .isEqualTo(21);
    }

    @Test
    void registerRequiresCreateGroupFields() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "missing-create-fields"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerIgnoresClientControlledFields() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 999999,
                                  "username": "binding-register-user",
                                  "password": "binding-password",
                                  "nickname": "Binding Register",
                                  "role": 1,
                                  "status": 0,
                                  "creditScore": 0
                                }
                                """))
                .andExpect(status().isOk());

        Map<String, Object> user = jdbcTemplate.queryForMap(
                "SELECT id, username, password, role, status, credit_score "
                        + "FROM user WHERE username = ?",
                "binding-register-user"
        );
        assertThat(((Number) user.get("id")).longValue())
                .isPositive()
                .isNotEqualTo(999999L);
        assertThat(user.get("password")).isNotEqualTo("binding-password");
        assertThat(((Number) user.get("role")).intValue())
                .isEqualTo(UserConstant.Role.USER);
        assertThat(((Number) user.get("status")).intValue())
                .isEqualTo(UserConstant.Status.ACTIVE);
        assertThat(((Number) user.get("credit_score")).intValue())
                .isEqualTo(UserConstant.CREDIT_SCORE_DEFAULT);
    }

    @Test
    void updateProfileUsesUpdateGroupAndPreservesServerFields() throws Exception {
        User user = insertUser("binding-profile-user");
        String originalPassword = user.getPassword();
        String token = tokenProvider.generateToken(user);

        mockMvc.perform(put("/api/user/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 999999,
                                  "username": "forged-username",
                                  "password": "forged-password",
                                  "nickname": "Updated Profile",
                                  "phone": "13900000000",
                                  "email": "profile@example.test",
                                  "school": "Updated University",
                                  "campus": "North Campus",
                                  "avatar": "https://example.test/forged.jpg",
                                  "role": 1,
                                  "status": 0,
                                  "creditScore": 0
                                }
                                """))
                .andExpect(status().isOk());

        Map<String, Object> updated = jdbcTemplate.queryForMap(
                "SELECT username, password, nickname, avatar, phone, email, "
                        + "school, campus, role, status, credit_score "
                        + "FROM user WHERE id = ?",
                user.getId()
        );
        assertThat(updated.get("username")).isEqualTo("binding-profile-user");
        assertThat(updated.get("password")).isEqualTo(originalPassword);
        assertThat(updated.get("nickname")).isEqualTo("Updated Profile");
        assertThat(updated.get("avatar")).isEqualTo(user.getAvatar());
        assertThat(updated.get("phone")).isEqualTo("13900000000");
        assertThat(updated.get("email")).isEqualTo("profile@example.test");
        assertThat(updated.get("school")).isEqualTo("Updated University");
        assertThat(updated.get("campus")).isEqualTo("North Campus");
        assertThat(((Number) updated.get("role")).intValue())
                .isEqualTo(UserConstant.Role.USER);
        assertThat(((Number) updated.get("status")).intValue())
                .isEqualTo(UserConstant.Status.ACTIVE);
        assertThat(((Number) updated.get("credit_score")).intValue())
                .isEqualTo(UserConstant.CREDIT_SCORE_DEFAULT);
    }

    @Test
    void createProductIgnoresClientControlledServerFields() throws Exception {
        User seller = insertUser("binding-product-seller");
        Category category = insertCategory("binding-product-category");
        String token = tokenProvider.generateToken(seller);
        String body = """
                {
                  "id": 999999,
                  "userId": 888888,
                  "categoryId": %d,
                  "title": "Binding Product",
                  "description": "Request body test",
                  "price": 39.90,
                  "originalPrice": 59.90,
                  "state": 2,
                  "campus": "East Campus",
                  "status": 1,
                  "viewCount": 99,
                  "isDeleted": 1,
                  "deletedAt": "2020-01-01T00:00:00",
                  "images": []
                }
                """.formatted(category.getId());

        mockMvc.perform(post("/api/product")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        Map<String, Object> product = jdbcTemplate.queryForMap(
                "SELECT user_id, status, view_count, is_deleted, deleted_at "
                        + "FROM product WHERE title = ?",
                "Binding Product"
        );
        assertThat(((Number) product.get("user_id")).longValue())
                .isEqualTo(seller.getId());
        assertThat(((Number) product.get("status")).intValue())
                .isEqualTo(ProductConstant.Status.NEED_CHECK);
        assertThat(((Number) product.get("view_count")).intValue())
                .isZero();
        assertThat(((Number) product.get("is_deleted")).intValue())
                .isZero();
        assertThat(product.get("deleted_at")).isNull();
    }
}
