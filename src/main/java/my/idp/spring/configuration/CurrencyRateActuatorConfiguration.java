package my.idp.spring.configuration;

import my.idp.spring.actuator.CurrencyRateEndpoint;
import my.idp.spring.service.CurrencyRateService;
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
