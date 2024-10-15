package com.example.tradingbotclientapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BybitConfig {

    @Value("${bybit.token}")
    private String apiToken;

    @Value("${bybit.secretKey}")
    private String secretKey;

    @Value("${bybit.api.url}")
    private String baseUrl;

}
