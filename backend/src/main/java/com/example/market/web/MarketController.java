package com.example.market.web;

import com.example.market.service.MarketSummaryService;
import com.example.market.web.dto.MarketSummaryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketSummaryService marketSummaryService;

    public MarketController(MarketSummaryService marketSummaryService) {
        this.marketSummaryService = marketSummaryService;
    }

    @GetMapping("/summary")
    public ApiResponse<MarketSummaryResponse> getSummary() {
        return ApiResponse.ok(marketSummaryService.getSummary());
    }
}
