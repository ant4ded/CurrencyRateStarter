package my.idp.spring.currency.rate.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfiguration {
	@Bean
	public String helloString() {
		return "Hello world";
	}
}
