package com.example.market.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProductImageResolver {

    public String resolve(Long productId, String dbImage) {
        if (StringUtils.hasText(dbImage) && dbImage.startsWith("http")) {
            return dbImage;
        }
        return switch (productId.intValue()) {
            case 1 -> "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=900&q=80";
            case 2 -> "https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=900&q=80";
            default -> "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80";
        };
    }
}
