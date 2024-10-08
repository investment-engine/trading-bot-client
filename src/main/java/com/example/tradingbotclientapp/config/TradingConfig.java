package com.example.tradingbotclientapp.config;

import jakarta.annotation.PostConstruct;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "trading")
public class TradingConfig {
    private Map<String, BigDecimal> pairs = new HashMap<>();
    private PropertiesConfiguration config;

    @PostConstruct
    public void init() {
        config = new PropertiesConfiguration();
        try {
            config.read(new FileReader("application.properties"));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, BigDecimal> getPairs() {
        return pairs;
    }

    public void setPairs(Map<String, BigDecimal> pairs) {
        this.pairs = pairs;
    }

    public void updateAmount(String pair, BigDecimal newAmount) {
        pairs.put(pair, newAmount);
        config.setProperty("trading.pairs." + pair, newAmount);
        try {
            config.write(new FileWriter("application.properties"));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }
}
