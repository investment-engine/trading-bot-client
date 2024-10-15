package com.example.tradingbotclientapp.model.wallet;

import com.example.tradingbotclientapp.model.HuobiSymbol;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;

@Data
public class BybitWalletBalance {

    private int retCode;
    private String retMsg;
    private BybitWalletResult result;
    private Object retExtInfo;
    private long time;

    public Optional<BybitWalletBalanceCoin> getCoinEquity(HuobiSymbol symbol) {
        return Optional.ofNullable(result.getList())
                .flatMap(wallets -> wallets.stream().findFirst())
                .flatMap(wallet -> Optional.ofNullable(wallet.getCoin())
                        .flatMap(coins -> coins.stream()
                                .filter(balanceCoin -> Objects.equals(balanceCoin.getCoin(), symbol.toString()))
                                .findFirst()
                        )
                );
    }


}
