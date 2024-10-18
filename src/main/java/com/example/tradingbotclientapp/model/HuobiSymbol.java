package com.example.tradingbotclientapp.model;

import lombok.Getter;

@Getter
public enum HuobiSymbol {
    BTC("BTCUSDT", 6),
    ETH("ETHUSDT", 4),
    TON("TONUSDT", 2),
    CORE("COREUSDT", 2),
    SHIB("SHIBUSDT", 0),
    FLOKI("FLOKIUSDT", 0),
    BONK("BONKUSDT", 0),
    WIF("WIFUSDT", 0),
    PEPE("PEPEUSDT", 0),
    USDT("usdt", 2);

    private final String usdtPair;
    private final int scale;

    HuobiSymbol(String usdtPair, int scale) {
        this.usdtPair = usdtPair;
        this.scale = scale;
    }

    public static HuobiSymbol findByUsdtPair(String label) {
        for (HuobiSymbol symbol : values()) {
            if (symbol.getUsdtPair().equalsIgnoreCase(label)) {
                return symbol;
            }
        }
        return null;
    }
}
