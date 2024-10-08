package com.example.tradingbotclientapp.model.wallet;

import lombok.Data;

import java.util.List;

@Data
public class BybitWalletResult {
    private List<BybitWalletBalanceWallet> list;
}
