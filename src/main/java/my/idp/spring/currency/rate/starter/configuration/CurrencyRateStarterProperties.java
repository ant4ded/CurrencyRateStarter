package my.idp.spring.currency.rate.starter.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "rate.starter")
public class CurrencyRateStarterProperties {
	private String mode;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private String url;
	private int crossRateScale = 32;
	private boolean enableCrossRate;
	private List<String> currencies;
}
