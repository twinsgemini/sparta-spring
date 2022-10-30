package com.sparta.springcore2.intefration;

import com.sparta.springcore2.dto.ProductMypriceRequestDto;
import com.sparta.springcore2.dto.ProductRequestDto;
import com.sparta.springcore2.dto.SignupRequestDto;
import com.sparta.springcore2.model.Product;
import com.sparta.springcore2.model.User;
import com.sparta.springcore2.model.UserRoleEnum;
import com.sparta.springcore2.service.ProductService;
import com.sparta.springcore2.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserProductIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    Long userId = null;
    User registeredUser = null;
    Product createdProduct = null;
    int updatedMyPrice = -1;

    @Test
    @Order(1)
    @DisplayName("회원 가입 전 관심상품 등록 (실패)")
    void test1() {
        // given
        String title = "Apple <b>에어팟</b> 2세대 유선충전 모델 (MV7N2KH/A)";
        String imageUrl = "https://shopping-phinf.pstatic.net/main_1862208/18622086330.20200831140839.jpg";
        String linkUrl = "https://search.shopping.naver.com/gate.nhn?id=18622086330";
        int lPrice = 10000;
        ProductRequestDto requestDto = new ProductRequestDto(title, imageUrl, linkUrl, lPrice);

        // 회원 가입 전
        userId = null;

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(requestDto, userId);
        });

        // then
        assertEquals(
                "회원 Id 가 유효하지 않습니다.",
                exception.getMessage()
        );

    }

    @Test
    @Order(2)
    @DisplayName("회원 가입")
    void test2() {
        // given
        String userName = "홍길동";
        String password = "1234";
        String email = "testuser99@testemail99.com";
        boolean admin = false;

        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername(userName);
        requestDto.setPassword(password);
        requestDto.setEmail(email);
        requestDto.setAdmin(admin);

        // when
        User user = userService.registerUser(requestDto);

        // then
        assertNotNull(user.getId());
        assertEquals(userName, user.getUsername());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
        assertEquals(email, user.getEmail());
        assertEquals(UserRoleEnum.USER, user.getRole());

        userId = user.getId();
    }

    @Test
    @Order(3)
    @DisplayName("가입된 회원으로 관심상품 등록")
    void test3() {
        // given
        String title = "Apple <b>에어팟</b> 2세대 유선충전 모델 (MV7N2KH/A)";
        String imageUrl = "https://shopping-phinf.pstatic.net/main_1862208/18622086330.20200831140839.jpg";
        String linkUrl = "https://search.shopping.naver.com/gate.nhn?id=18622086330";
        int lPrice = 10000;
        ProductRequestDto requestDto = new ProductRequestDto(title, imageUrl, linkUrl, lPrice);

        // when
        Product product = productService.createProduct(requestDto, userId);

        // then
        assertNotNull(product.getId());
        assertEquals(userId, product.getUserId());
        assertEquals(title, product.getTitle());
        assertEquals(imageUrl, product.getImage());
        assertEquals(linkUrl, product.getLink());
        assertEquals(lPrice, product.getLprice());
        assertEquals(0, product.getMyprice());

        createdProduct = product;
    }

    @Test
    @Order(4)
    @DisplayName("관심상품 업데이트")
    void test4() {
        // given
        Long productId = createdProduct.getId();
        int myPrice = 20000;
        ProductMypriceRequestDto requestDto = new ProductMypriceRequestDto(myPrice);

        // when
        Product product = productService.updateProduct(productId, requestDto);

        // then
        assertNotNull(product.getId());
        assertEquals(userId, product.getUserId());
        assertEquals(createdProduct.getTitle(), product.getTitle());
        assertEquals(createdProduct.getImage(), product.getImage());
        assertEquals(createdProduct.getLink(), product.getLink());
        assertEquals(createdProduct.getLprice(), product.getLprice());
        assertEquals(myPrice, product.getMyprice());

        updatedMyPrice = myPrice;
    }

    @Test
    @Order(5)
    @DisplayName("관심상품 조회")
    void test5() {
        // given
        Long createProductId = createdProduct.getId();

        // when
        List<Product> productList = productService.getProducts(userId);
        Product foundProdoct = productList.stream().filter(product -> product.getId().equals(createProductId))
                .findFirst()
                .orElse(null);

        // then
        assertNotNull(foundProdoct);
        assertEquals(createdProduct.getId(), foundProdoct.getId());
        assertEquals(createdProduct.getTitle(), foundProdoct.getTitle());
        assertEquals(createdProduct.getImage(), foundProdoct.getImage());
        assertEquals(createdProduct.getLink(), foundProdoct.getLink());
        assertEquals(createdProduct.getLprice(), foundProdoct.getLprice());
        assertEquals(updatedMyPrice, foundProdoct.getMyprice());
        assertEquals(userId, foundProdoct.getUserId());
    }
}
