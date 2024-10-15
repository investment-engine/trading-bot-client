package com.example.tradingbotclientapp.client;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.domain.trade.PositionIdx;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.TimeInForce;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.example.tradingbotclientapp.config.BybitConfig;
import com.example.tradingbotclientapp.model.HuobiSymbol;
import com.example.tradingbotclientapp.model.wallet.BybitWalletBalance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.bybit.api.client.domain.account.AccountType.UNIFIED;

@Component
@Slf4j
public class BybitClient {

    private final ObjectMapper objectMapper;
    private final BybitApiTradeRestClient clientTrade;
    private final BybitApiAccountRestClient accountClient;

    public BybitClient(BybitConfig bybitConfig) {
        objectMapper = new ObjectMapper();
        this.clientTrade = BybitApiClientFactory.newInstance(bybitConfig.getApiToken(),
                        bybitConfig.getSecretKey(), bybitConfig.getBaseUrl(), true)
                .newTradeRestClient();

        this.accountClient = BybitApiClientFactory.newInstance(bybitConfig.getApiToken(),
                        bybitConfig.getSecretKey(), bybitConfig.getBaseUrl())
                .newAccountRestClient();
        log.info("Bybit client created");
    }

    public BybitWalletBalance getWalletInfo() throws JsonProcessingException {
        var data = AccountDataRequest.builder().accountType(UNIFIED).build();
        return objectMapper.readValue(objectMapper.writeValueAsString(accountClient.getWalletBalance(data)), BybitWalletBalance.class);
    }

    public void closePosition(HuobiSymbol symbol, BigDecimal qty) {
        log.info("Trying CLOSE position for PAIR={}, amount={}", symbol.getUsdtPair(), qty);
        var newOrderRequest = TradeOrderRequest.builder().category(CategoryType.SPOT).symbol(symbol.getUsdtPair())
                .side(Side.SELL).orderType(TradeOrderType.MARKET).qty(qty.setScale(symbol.getScale()).toString()).timeInForce(TimeInForce.GOOD_TILL_CANCEL)
                .positionIdx(PositionIdx.ONE_WAY_MODE).build();
        var resp = clientTrade.createOrder(newOrderRequest);
        log.info("Response: {}", resp);
    }

    public void openPosition(HuobiSymbol symbol, BigDecimal qty) {
        log.info("Trying open position for PAIR={}, amount={}", symbol.getUsdtPair(), qty);
        var newOrderRequest = TradeOrderRequest.builder().category(CategoryType.SPOT).symbol(symbol.getUsdtPair())
                .side(Side.BUY).orderType(TradeOrderType.MARKET).marketUnit("quoteCoin").qty(qty.toString()).timeInForce(TimeInForce.GOOD_TILL_CANCEL)
                .positionIdx(PositionIdx.ONE_WAY_MODE).build();
        var resp = clientTrade.createOrder(newOrderRequest);
        log.info("Response: {}", resp);
    }
}