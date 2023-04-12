package ru.teachmeplz.currency_exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.teachmeplz.currency_exchange.service.CbrService;

import java.math.BigDecimal;
import java.util.Locale;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    private final CbrService cbrService;

    @Autowired
    public CurrencyController(CbrService cbrService) {
        this.cbrService = cbrService;
    }

    @GetMapping("/rate/{code}")
    public BigDecimal currencyRate(@PathVariable("code") String code) {
        return cbrService.requestByCurrencyCode(code.toUpperCase(Locale.ENGLISH));
    }
}
