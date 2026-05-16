package com.example.market.web;

import com.example.market.service.ProductService;
import com.example.market.web.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> listOnSale(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(productService.listOnSale(categoryId, keyword, PageRequest.of(page, Math.min(size, 100))));
    }
}
