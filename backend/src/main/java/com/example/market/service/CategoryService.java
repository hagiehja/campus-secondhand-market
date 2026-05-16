package com.example.market.service;

import com.example.market.web.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {

    List<CategoryResponse> listCategories();
}
