package com.example.tradingbotclientapp;

import com.example.tradingbotclientapp.client.BybitClient;
import com.example.tradingbotclientapp.config.TradingConfig;
import com.example.tradingbotclientapp.model.HuobiSymbol;
import com.example.tradingbotclientapp.model.TradeLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final BybitClient bybitClient;
    private final RestTemplate restTemplate;
    private final TradingConfig tradingConfig;

    @Value("${api.url}")
    private String apiUrl;

    @Scheduled(cron = "0 0 0/4 * * ?", zone = "UTC") // Опрос каждые 4 часа
    public void checkAndTrade() throws IOException, ConfigurationException {
        List<TradeLog> tradeLogs = Optional.ofNullable(restTemplate.getForObject(apiUrl, TradeLog[].class))
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);

        Map<HuobiSymbol, TradeLog> tradeLogMap = tradeLogs.stream()
                .filter(tradeLog -> HuobiSymbol.findByUsdtPair(tradeLog.getPair()) != null)
                .collect(Collectors.toMap(tradeLog1 -> HuobiSymbol.findByUsdtPair(tradeLog1.getPair()), Function.identity()));

        for (Map.Entry<HuobiSymbol, BigDecimal> entry : tradingConfig.getPairs().entrySet()) {
            HuobiSymbol pair = entry.getKey();

            if (pair == null) {
                continue;
            }

            BigDecimal amount = entry.getValue();

            TradeLog tradeLog = tradeLogMap.get(pair);
            if (tradeLog == null) {
                if (isTradeOpen(pair)) {
                    BigDecimal obtainedAmount = closeTrade(pair);
                    if (BigDecimal.ZERO.compareTo(obtainedAmount) <= 0) {
                        tradingConfig.updateAmount(pair, obtainedAmount);
                    }
                }
            } else {
                LocalDateTime openTimestamp = tradeLog.getOpenTimestamp().toLocalDateTime();
                LocalDateTime currentTimestamp = LocalDateTime.now(ZoneOffset.UTC);
                Duration duration = Duration.between(openTimestamp, currentTimestamp);
                long durationH = duration.toHours();
                if (durationH <= 1 && !isTradeOpen(pair)) {
                    executeTrade(pair, amount);
                }
            }
        }
    }

    private boolean isTradeOpen(HuobiSymbol symbol) throws JsonProcessingException {
        var walletBalance = bybitClient.getWalletInfo();
        var coin = walletBalance.getCoinEquity(symbol);
        if (coin.isPresent()) {
            BigDecimal qty = coin.get().getUsdValue().setScale(symbol.getScale(), RoundingMode.DOWN);
            return qty.compareTo(new BigDecimal("10.0")) > 0;
        }
        return false;
    }

    private void executeTrade(HuobiSymbol symbol, BigDecimal amount) {
        bybitClient.openPosition(symbol, amount);
    }

    private BigDecimal closeTrade(HuobiSymbol symbol) throws JsonProcessingException {
        var walletBalance = bybitClient.getWalletInfo();
        var coin = walletBalance.getCoinEquity(symbol);

        var usdtBefore = getWalletBalanceUSDT();

        log.info("USDT before close {} is {}", symbol, usdtBefore);

        if (coin.isPresent()) {
            BigDecimal qty = coin.get().getUsdValue().setScale(symbol.getScale(), RoundingMode.DOWN);
            if (qty.compareTo(new BigDecimal("10.0")) > 0) {
                BigDecimal count = coin.get().getEquity().setScale(symbol.getScale(), RoundingMode.DOWN);
                bybitClient.closePosition(symbol, count);

                BigDecimal usdtAfter = getWalletBalanceUSDT();

                log.info("USDT after close {} is {}", symbol, usdtAfter);
                return usdtAfter.subtract(usdtBefore);
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getWalletBalanceUSDT() throws JsonProcessingException {
        var walletBalanceBeforeClose = bybitClient.getWalletInfo();
        var usdt = walletBalanceBeforeClose.getCoinEquity(HuobiSymbol.USDT);
        return usdt.get().getUsdValue().setScale(HuobiSymbol.USDT.getScale(), RoundingMode.DOWN);
    }
}
