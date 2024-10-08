package com.example.tradingbotclientapp;

import com.example.tradingbotclientapp.client.BybitClient;
import com.example.tradingbotclientapp.config.TradingConfig;
import com.example.tradingbotclientapp.model.HuobiSymbol;
import com.example.tradingbotclientapp.model.TradeLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.math.RoundingMode;

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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Scheduled(cron = "0 0 0/4 * * ?") // Опрос каждые 4 часа
    public void checkAndTrade() throws JsonProcessingException {
        List<TradeLog> tradeLogs = Arrays.asList(Optional.ofNullable(restTemplate.getForObject(apiUrl, TradeLog[].class)).orElseGet(() -> new TradeLog[0]));
        Map<String, TradeLog> tradeLogMap = tradeLogs.stream()
                .collect(Collectors.toMap(TradeLog::getPair, Function.identity()));


        for (Map.Entry<String, BigDecimal> entry : tradingConfig.getPairs().entrySet()) {
            String pair = entry.getKey();
            BigDecimal amount = entry.getValue();

            TradeLog tradeLog = tradeLogMap.get(pair);

            if (tradeLog == null) {
                if (isTradeOpen(HuobiSymbol.valueOfLabel(pair))) {
                    BigDecimal obtainedAmount = closeTrade(HuobiSymbol.valueOfLabel(pair));
                    if (BigDecimal.ZERO.compareTo(obtainedAmount) <= 0) {
                        tradingConfig.updateAmount(pair, obtainedAmount);
                    }
                }
            } else {
                LocalDateTime openTimestamp = LocalDateTime.parse(tradeLog.getOpenTimestamp().toString(), formatter);
                LocalDateTime currentTimestamp = LocalDateTime.now(ZoneOffset.UTC);
                Duration duration = Duration.between(openTimestamp, currentTimestamp);
                long durationH = duration.toHours();
                if (durationH <= 1 && !isTradeOpen(HuobiSymbol.valueOfLabel(pair))) {
                        executeTrade(HuobiSymbol.valueOfLabel(pair), amount);

                }
            }
        }
    }

    private boolean isTradeOpen(HuobiSymbol symbol) throws JsonProcessingException {
        var walletBalance = bybitClient.getWalletInfo(symbol);
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
        var walletBalance = bybitClient.getWalletInfo(symbol);
        var coin = walletBalance.getCoinEquity(symbol);

        var usdtBefore = getWalletBalanceUSDT();

        log.info("USDT before close " + symbol + " is " + usdtBefore.toString());

        if (coin.isPresent()) {
            BigDecimal qty = coin.get().getUsdValue().setScale(symbol.getScale(), RoundingMode.DOWN);
            if (qty.compareTo(new BigDecimal("10.0")) > 0) {
                BigDecimal count = coin.get().getEquity().setScale(symbol.getScale(), RoundingMode.DOWN);
                bybitClient.closePosition(symbol, count);

                BigDecimal usdtAfter = getWalletBalanceUSDT();

                log.info("USDT after close " + symbol + " is " + usdtAfter.toString());
                return usdtAfter.subtract(usdtBefore);
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getWalletBalanceUSDT() throws JsonProcessingException {
        var walletBalancBeforeClose =  bybitClient.getWalletInfo(HuobiSymbol.USDT);
        var usdt = walletBalancBeforeClose.getCoinEquity(HuobiSymbol.USDT);
        return usdt.get().getUsdValue().setScale(HuobiSymbol.USDT.getScale(), RoundingMode.DOWN);
    }
}
