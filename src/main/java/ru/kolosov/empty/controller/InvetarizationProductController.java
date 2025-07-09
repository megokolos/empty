package ru.kolosov.empty.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kolosov.empty.dto.ProductDTO;
import ru.kolosov.empty.dto.request.RequestProducts;
import ru.kolosov.empty.dto.response.ResponseProducts;
import ru.kolosov.empty.service.ProductService;

@RestController
@RequestMapping("/invetarization")
@RequiredArgsConstructor
public class InvetarizationProductController {

    private final ProductService productService;

    @PostMapping("/save")
    public void save(@Valid @RequestBody ProductDTO productDTO) {
        productService.save(productDTO);
    }

    @PutMapping("/update")
    public void update(@Valid @RequestBody ProductDTO productDTO) {
        productService.update(productDTO);
    }

    @PostMapping("/check")
    public ResponseProducts getQuantity(@Validated @RequestBody RequestProducts requestProducts) {
        return productService.getQuantity(requestProducts);
    }
}
