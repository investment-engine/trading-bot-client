package com.example.tradingbotclientapp.model.wallet;

import com.example.tradingbotclientapp.model.HuobiSymbol;
import lombok.Data;

import java.util.Optional;

@Data
public class BybitWalletBalance {
    private int retCode;
    private String retMsg;
    private BybitWalletResult result;
    private Object retExtInfo;
    private long time;

    public Optional<BybitWalletBalanceCoin> getCoinEquity(HuobiSymbol symbol) {
        return result
                .getList()
                .get(0)
                .getCoin()
                .stream()
                .filter(i -> i.getCoin().equals(symbol.toString()))
                .findFirst();
    }


}
