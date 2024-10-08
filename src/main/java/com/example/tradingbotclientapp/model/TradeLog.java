package com.example.tradingbotclientapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeLog {
    private Long id;

    private Timestamp openTimestamp;

    private Timestamp closeTimestamp;

    private String pair;

    private BigDecimal openPrice;

    private BigDecimal closePrice;

    private boolean open;

    private BigDecimal profitPercentage;

}