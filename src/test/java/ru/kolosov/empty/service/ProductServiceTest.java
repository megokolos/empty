package ru.kolosov.empty.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.kolosov.empty.dto.ProductDTO;
import ru.kolosov.empty.dto.request.RequestProducts;
import ru.kolosov.empty.dto.response.ResponseProducts;
import ru.kolosov.empty.model.Product;
import ru.kolosov.empty.repository.ProductRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void save_ShouldPersistProduct() {
        ProductDTO dto = new ProductDTO("apple", 10L);
        productService.save(dto);

        Optional<Product> product = productRepository.getProductByName("apple");
        assertTrue(product.isPresent());
        assertEquals(10L, product.get().getQuantity());
    }

    @Test
    void update_ShouldUpdateExitingProduct() {
        ProductDTO dto = new ProductDTO("apple", 10L);
        productService.save(dto);
        dto.setQuantity(1L);
        productService.update(dto);
        Optional<Product> product = productRepository.getProductByName("apple");
        assertTrue(product.isPresent());
        assertEquals(9L, product.get().getQuantity());
    }

    @Test
    void update_NoProduct() {
        ProductDTO dto = new ProductDTO("apple", 10L);
        productService.save(dto);
        assertThrows(NoSuchElementException.class, () -> productService.update(new ProductDTO("cannon", 1L)));
    }

    @Test
    void update_NotEnoughProduct() {
        ProductDTO dto = new ProductDTO("apple", 10L);
        productService.save(dto);
        assertThrows(NoSuchElementException.class, () -> productService.update(new ProductDTO("apple", 100L)));
    }

    @Test
    public void getQuantity_CorrectNumbers() {
        ProductDTO dtoApple = new ProductDTO("apple", 10L);
        productService.save(dtoApple);

        ProductDTO dtoOrange = new ProductDTO("orange", 5L);
        productService.save(dtoOrange);

        RequestProducts requestProducts = new RequestProducts();
        ProductDTO dtoAppleRequest = new ProductDTO("apple", 10L);
        ProductDTO dtoOrangeRequest = new ProductDTO("orange", 15L);
        requestProducts.getProductDTOList().add(dtoOrangeRequest);
        requestProducts.getProductDTOList().add(dtoAppleRequest);

        ResponseProducts response = productService.getQuantity(requestProducts);

        assertEquals(2, response.getProductDTOList().size());
        assertEquals(10, response.getProductDTOList().stream()
                .filter(elem -> elem.getName().equals("apple"))
                .map(ProductDTO::getQuantity)
                .findFirst().get());
        assertEquals(5, response.getProductDTOList().stream()
                .filter(elem -> elem.getName().equals("orange"))
                .map(ProductDTO::getQuantity)
                .findFirst().get());
    }

    @Test
    public void getQuantity_WithNothing_ExceptionThrow() {
        RequestProducts requestProducts = new RequestProducts();
        assertThrows(NoSuchElementException.class,() -> productService.getQuantity(requestProducts));
    }
}
