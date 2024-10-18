package com.example.tradingbotclientapp.config;

import com.example.tradingbotclientapp.model.HuobiSymbol;
import jakarta.annotation.PostConstruct;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class TradingConfig {

    private final Map<HuobiSymbol, BigDecimal> pairs = new HashMap<>();

    private PropertiesConfiguration config;

    @Value("${bybit.balanceFile}")
    private String pathToBalanceFile;

    @PostConstruct
    public void init() throws IOException, ConfigurationException {
        config = new PropertiesConfiguration();

        Path path = Paths.get(pathToBalanceFile);

        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.createFile(path);
        }

        try (FileReader fileReader = new FileReader(path.toFile())) {
            config.read(fileReader);

            for (Iterator<String> it = config.getKeys(); it.hasNext(); ) {
                String key = it.next();
                HuobiSymbol symbol = HuobiSymbol.findByUsdtPair(key);
                if (symbol != null) {
                    BigDecimal amount = new BigDecimal(config.getString(key));
                    pairs.put(symbol, amount);
                }
            }
        }
    }

    public Map<HuobiSymbol, BigDecimal> getPairs() {
        return pairs;
    }

    public void updateAmount(HuobiSymbol pair, BigDecimal newAmount) throws IOException, ConfigurationException {
        pairs.put(pair, newAmount);
        config.setProperty(pair.getUsdtPair(), newAmount);

        try (FileWriter fileWriter = new FileWriter(pathToBalanceFile)) {
            config.write(fileWriter);
        }
    }
}
