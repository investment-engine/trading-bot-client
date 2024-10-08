package com.example.tradingbotclientapp.model.wallet;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BybitWalletBalanceWallet {
    private String accountType;
    private String accountLTV;
    private String accountIMRate;
    private String accountMMRate;
    private BigDecimal totalEquity;
    private BigDecimal totalWalletBalance;
    private BigDecimal totalMarginBalance;
    private BigDecimal totalAvailableBalance;
    private BigDecimal totalPerpUPL;
    private BigDecimal totalInitialMargin;
    private BigDecimal totalMaintenanceMargin;
    private List<BybitWalletBalanceCoin> coin;
}