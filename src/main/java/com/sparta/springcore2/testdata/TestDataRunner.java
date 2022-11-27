package com.sparta.springcore2.testdata;

import com.sparta.springcore2.dto.ItemDto;
import com.sparta.springcore2.model.Folder;
import com.sparta.springcore2.model.Product;
import com.sparta.springcore2.model.User;
import com.sparta.springcore2.model.UserRoleEnum;
import com.sparta.springcore2.repository.FolderRepository;
import com.sparta.springcore2.repository.ProductRepository;
import com.sparta.springcore2.repository.UserRepository;
import com.sparta.springcore2.service.ItemSearchService;
import com.sparta.springcore2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sparta.springcore2.service.ProductService.MIN_MY_PRICE;

@Component
public class TestDataRunner implements ApplicationRunner {

    @Autowired
    UserService userService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ItemSearchService itemSearchService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
// 테스트 User 생성
        User testUser1 = new User("슈가", passwordEncoder.encode("1234"), "sugar@sparta.com", UserRoleEnum.USER);
        User testUser2 = new User("홍길동", passwordEncoder.encode("1234"), "hong@sparta.com", UserRoleEnum.USER);
        User testAdminUser = new User("관리자", passwordEncoder.encode("1234"), "admin@sparta.com", UserRoleEnum.ADMIN);
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testAdminUser = userRepository.save(testAdminUser);

// 테스트 User 의 관심상품 등록
// 검색어 당 관심상품 10개 등록
        createTestData(testUser1, "신발");
        createTestData(testUser1, "과자");
        createTestData(testUser1, "키보드");
        createTestData(testUser1, "휴지");
        createTestData(testUser1, "휴대폰");
        createTestData(testUser1, "앨범");
        createTestData(testUser1, "헤드폰");
        createTestData(testUser1, "이어폰");
        createTestData(testUser1, "노트북");
        createTestData(testUser1, "무선 이어폰");
        createTestData(testUser1, "모니터");
    }

    private void createTestData(User user, String searchWord) throws IOException {
// 네이버 쇼핑 API 통해 상품 검색
        List<ItemDto> itemDtoList = itemSearchService.getItems(searchWord);

        List<Product> productList = new ArrayList<>();

        for (ItemDto itemDto : itemDtoList) {
            Product product = new Product();
// 관심상품 저장 사용자
            product.setUserId(user.getId());
// 관심상품 정보
            product.setTitle(itemDto.getTitle());
            product.setLink(itemDto.getLink());
            product.setImage(itemDto.getImage());
            product.setLprice(itemDto.getLprice());

// 희망 최저가 랜덤값 생성
// 최저 (100원) ~ 최대 (상품의 현재 최저가 + 10000원)
            int myPrice = getRandomNumber(MIN_MY_PRICE, itemDto.getLprice() + 10000);
            product.setMyprice(myPrice);

            productList.add(product);
        }

        productRepository.saveAll(productList);

        Folder folder = new Folder(searchWord, user);
        folderRepository.save(folder);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
