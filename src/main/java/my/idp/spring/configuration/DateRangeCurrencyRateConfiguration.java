package my.idp.spring.configuration;

import my.idp.spring.service.CurrencyRateService;
import my.idp.spring.service.DateRangeCurrencyRateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(prefix = "rate.starter", name = "mode", havingValue = "date-range")
public class DateRangeCurrencyRateConfiguration {
	@Bean
	public CurrencyRateService currencyRateService(RestTemplate restTemplate, CurrencyRateStarterProperties properties) {
		return new DateRangeCurrencyRateService(restTemplate, properties);
	}
}
