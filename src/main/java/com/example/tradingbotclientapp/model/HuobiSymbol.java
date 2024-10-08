package com.example.tradingbotclientapp.model;

import lombok.Getter;

public enum HuobiSymbol {
    BTC("btcusdt", "BTCUSDT", 6),
    ETH("ethusdt", "ETHUSDT", 4),
    TON("tonusdt", "TONUSDT", 2),
    CORE("coreusdt", "COREUSDT", 4),
    SHIB("shibusdt", "SHIBUSDT", 0),
    FLOKI("flokiusdt", "FLOKIUSDT", 0),
    BONK("bonkusdt", "BONKUSDT", 0),
    WIF("wifusdt", "WIFUSDT", 0),
    PEPE("pepeusdt", "PEPEUSDT", 0),
    USDT("usdt", "usdt", 2);

    @Getter
    private String label;
    @Getter
    private String description;
    @Getter
    private int scale;

    HuobiSymbol(String label, String description, int scale) {
        this.label = label;
        this.description = description;
        this.scale = scale;
    }

    public static HuobiSymbol valueOfLabel(String label) {
        for (HuobiSymbol symbol : values()) {
            if (symbol.getLabel().equals(label)) {
                return symbol;
            }
        }
        throw new IllegalArgumentException("No enum constant with label: " + label);
    }
}
