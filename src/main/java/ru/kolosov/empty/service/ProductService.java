package ru.kolosov.empty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolosov.empty.dto.ProductDTO;
import ru.kolosov.empty.dto.request.RequestProducts;
import ru.kolosov.empty.dto.response.ResponseProducts;
import ru.kolosov.empty.model.Product;
import ru.kolosov.empty.repository.ProductRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void save(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .quantity(productDTO.getQuantity())
                .build();
        if (productRepository.existsByName(productDTO.getName())) {
            throw new IllegalArgumentException("Продукт с таким именем уже существует");
        }
        productRepository.save(product);
        log.info("Товар {} сохранен в количестве {}", productDTO.getName(), productDTO.getQuantity());
    }

    @Transactional
    public void update(ProductDTO productDTO) {
        Long newQuantity = checkQuantity(productDTO);
        Product product = productRepository.getProductByName(productDTO.getName()).get();
        product.setQuantity(newQuantity);
        productRepository.save(product);
        log.info("Товар {} обновлен, новое количество единиц {}", productDTO.getName(), newQuantity);
    }

    public ResponseProducts getQuantity(RequestProducts requestProducts) {
        ResponseProducts responseProducts = new ResponseProducts();
        List<String> names = new ArrayList<>();
        for (ProductDTO dto : requestProducts.getProductDTOList()) {
            names.add(dto.getName());
        }
        List<Product> optionalProducts = productRepository.findProductsByName(names);
        if (optionalProducts.isEmpty()) {
            log.warn("Ничего нет по заявке");
            throw new NoSuchElementException();
        }
        Map<String, Long> productsWithQuantity = new HashMap<>();

        for (Product product : optionalProducts) {
            productsWithQuantity.put(product.getName(),
                    productsWithQuantity.getOrDefault(product.getName(), 0L) + product.getQuantity());
        }
        for (ProductDTO productDTO : requestProducts.getProductDTOList()) {
            if (productsWithQuantity.containsKey(productDTO.getName()) &&
                    productsWithQuantity.get(productDTO.getName()) >= productDTO.getQuantity()) {
                responseProducts.getProductDTOList().add(new ProductDTO().builder()
                        .name(productDTO.getName())
                        .quantity(productDTO.getQuantity())
                        .build());
            } else responseProducts.getProductDTOList().add(new ProductDTO().builder()
                    .name(productDTO.getName())
                    .quantity(productsWithQuantity.getOrDefault(productDTO.getName(), 0L))
                    .build());
        }
        log.info("Проверили заказ по заявке, вернули доступное количество товаров и единиц: {}", responseProducts);
        return responseProducts;
    }

    private Long checkQuantity(ProductDTO dto) {
        Optional<Product> optionalProduct = productRepository.getProductByName(dto.getName());
        if (optionalProduct.isEmpty()) {
            log.warn("Нет такого продукта на складе");
            throw new NoSuchElementException();
        }
        Long newQuantity = optionalProduct.get().getQuantity() - dto.getQuantity();
        if (newQuantity < 0) {
            log.warn("Нет такого количества единиц товара на складе");
            throw new NoSuchElementException();
        }
        return newQuantity;
    }
}
