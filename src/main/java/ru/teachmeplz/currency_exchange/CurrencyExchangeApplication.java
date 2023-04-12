package ru.teachmeplz.currency_exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.teachmeplz.currency_exchange.config.CurrencyClientCfg;

@SpringBootApplication()
@EnableConfigurationProperties(CurrencyClientCfg.class)
public class CurrencyExchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyExchangeApplication.class, args);
    }

}
