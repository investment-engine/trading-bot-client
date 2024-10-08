package com.example.tradingbotclientapp.model.wallet;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BybitWalletBalanceCoin {
    private String coin;
    private BigDecimal equity;
    private BigDecimal usdValue;
    private BigDecimal walletBalance;
    private BigDecimal free;
    private BigDecimal locked;
    private BigDecimal spotHedgingQty;
    private BigDecimal borrowAmount;
    private BigDecimal availableToWithdraw;
    private BigDecimal accruedInterest;
    private BigDecimal totalOrderIM;
    private BigDecimal totalPositionIM;
    private BigDecimal totalPositionMM;
    private BigDecimal unrealisedPnl;
    private BigDecimal cumRealisedPnl;
    private BigDecimal bonus;
    private boolean marginCollateral;
    private boolean collateralSwitch;
    private BigDecimal availableToBorrow;

}