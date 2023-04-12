package ru.teachmeplz.currency_exchange.client;


import java.time.LocalDate;


public interface HttpCurrencyDateRateClient {
   String requestByDateRate(LocalDate date);
}
