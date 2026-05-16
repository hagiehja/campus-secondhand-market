package com.example.market.service;

import com.example.market.web.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductService {

    Page<ProductResponse> listOnSale(Long categoryId, String keyword, PageRequest pageRequest);
}
