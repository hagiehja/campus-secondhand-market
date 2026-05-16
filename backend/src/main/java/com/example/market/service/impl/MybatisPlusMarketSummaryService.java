package com.example.market.service.impl;

import com.example.market.persistence.mapper.MarketSummaryMapper;
import com.example.market.service.MarketSummaryService;
import com.example.market.web.dto.MarketSummaryResponse;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class MybatisPlusMarketSummaryService implements MarketSummaryService {

    private final MarketSummaryMapper marketSummaryMapper;

    public MybatisPlusMarketSummaryService(MarketSummaryMapper marketSummaryMapper) {
        this.marketSummaryMapper = marketSummaryMapper;
    }

    @Override
    public MarketSummaryResponse getSummary() {
        return new MarketSummaryResponse(
            valueOrZero(marketSummaryMapper.countPendingChats()),
            valueOrZero(marketSummaryMapper.countActiveOrders()),
            valueOrZero(marketSummaryMapper.countFinishedOrders()),
            valueOrZero(marketSummaryMapper.countTodayNewProducts()),
            amountOrZero(marketSummaryMapper.sumOnSaleProductPrice())
        );
    }

    private Integer valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal amountOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
