package my.idp.spring.currency.rate.starter.configuration;

import my.idp.spring.currency.rate.starter.actuator.CurrencyRateEndpoint;
import my.idp.spring.currency.rate.starter.service.CurrencyRateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
@ConditionalOnClass(Endpoint.class)
public class CurrencyRateActuatorConfiguration {
	@Bean
	public CurrencyRateEndpoint currencyRateEndpoint(CurrencyRateService currencyRateService) {
		return new CurrencyRateEndpoint(currencyRateService);
	}
}
