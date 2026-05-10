package my.idp.spring.configuration;

import my.idp.spring.service.CurrencyRateService;
import my.idp.spring.service.CurrentDayCurrencyRateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(prefix = "rate.starter", name = "mode", havingValue = "current-day", matchIfMissing = true)
public class CurrentDayCurrencyRateConfiguration {
	@Bean
	public CurrencyRateService currencyRateService(RestTemplate restTemplate, CurrencyRateStarterProperties properties) {
		return new CurrentDayCurrencyRateService(restTemplate, properties);
	}
}
