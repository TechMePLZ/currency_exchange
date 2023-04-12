package ru.teachmeplz.currency_exchange.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.teachmeplz.currency_exchange.config.CurrencyClientCfg;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class HttpCurrencyDateRateClientImpl implements HttpCurrencyDateRateClient{
    /**
     * https://cbr.ru/scripts/XML_daily.asp?date_req=02/03/2023
     * 02/03/2023 паттерн
     */
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private  final CurrencyClientCfg clientCfg;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    @Autowired
    public HttpCurrencyDateRateClientImpl(CurrencyClientCfg clientCfg) {
        this.clientCfg = clientCfg;
    }

    /**
     * Создаем URL запроса, используя HTTP реквест
     * @param date дата
     * @return
     */
    @Override
    public String requestByDateRate(LocalDate date) {
        String baseUrl = clientCfg.getUrl();   ;
        HttpClient client = HttpClient.newHttpClient();
        String url = BuildUriRequest(baseUrl, date);
        /**
         * создаем запрос. С помощью метода uri() можно задать URI (или URL), к которому будет отправлен http-запрос.
         * Отправляем запрос на внешний ресурс.
         * В зависимости от того, какой тип BodyHandlers передали в метод send(), такой тип результата он и вернет.
         *
         */
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> send =client.send(request,HttpResponse.BodyHandlers.ofString());
            return send.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Создаем Uri для запроса используя, UriComponentsBuilder
     * @param baseUrl
     * @param date
     * @return
     */
    private String BuildUriRequest(String baseUrl, LocalDate date) {
    return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("date_req", DATE_TIME_FORMATTER.format(date))
            .build()
            .toUriString();
    }
}
