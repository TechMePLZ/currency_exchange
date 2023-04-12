package ru.teachmeplz.currency_exchange.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.teachmeplz.currency_exchange.client.HttpCurrencyDateRateClient;
import ru.teachmeplz.currency_exchange.schema.ValCurs;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CbrService {
    private final HttpCurrencyDateRateClient httpCurrencyDateRateClient;
    /**
     * Используем в качестве кэша guava cache. ключом будет локал дата,
     * а значением Мапа <Код валюты, Котировка>
     */
    private final Cache<LocalDate, Map<String, BigDecimal>> cache;

    /**
     * настраоиваем КЭш с помощью АПИ Guava
     */
    @Autowired
    public CbrService(HttpCurrencyDateRateClient httpCurrencyDateRateClient) {
        this.httpCurrencyDateRateClient = httpCurrencyDateRateClient;
        this.cache = CacheBuilder.newBuilder().build();
    }

    /**
     * @param code - код валюты из XML ЦБ РФ
     * @return Котировку валюты. Вначале чекаем кэш.
     */
    public BigDecimal requestByCurrencyCode(String code) {

        try {
            return cache.get(LocalDate.now(), this::callAllByCurrentDate).get(code);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получаем XML обращаясь по интерфейсу метода запроса с текущей датой
     * Делаем анмаршал XML.
     * извлекаем из объекта  ValCurs и кладем в мапу
     * @return
     */
    private Map<String, BigDecimal> callAllByCurrentDate() {
        String xml = httpCurrencyDateRateClient.requestByDateRate(LocalDate.now());
        ValCurs valCurs = unmarshaller(xml);

        return valCurs.getValute().stream().collect(Collectors.toMap(ValCurs.Valute::getCharCode, item -> parseWithLocale(item.getValue())));
    }

    private BigDecimal parseWithLocale(String value) {
        double c = Double.parseDouble(value);
        return BigDecimal.valueOf(c);
    }

    /**
     * @param xml
     * @return демаршализованый POJO, используя JAXBContext и StringReader
     */
    private ValCurs unmarshaller(String xml) {
        StringReader reader = new StringReader(xml);
        try {
            JAXBContext context = JAXBContext.newInstance(ValCurs.class);
            return (ValCurs) context.createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
