package my.idp.spring.currency.rate.starter.service;

import my.idp.spring.currency.rate.starter.dto.CurrencyRateDto;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

public class CurrentDayCurrencyRateService implements CurrencyRateService, SmartInitializingSingleton {
	private final RestTemplate restTemplate;
	private final String url;
	private final List<String> currencies;

	private final List<CurrencyRateDto> cache = new LinkedList<>();

	public CurrentDayCurrencyRateService(RestTemplate restTemplate, String url, List<String> currencies) {
		this.restTemplate = restTemplate;
		this.url = url;
		this.currencies = currencies;
	}
	@Override
	public List<CurrencyRateDto> getCachedRates() {
		return cache;
	}

	@Override
	public void afterSingletonsInstantiated() {
		StringBuilder parametrizedUrl = new StringBuilder(url).append('?').append("periodicity=0");
		ResponseEntity<CurrencyRateDto[]> response = restTemplate.getForEntity(parametrizedUrl.toString(), CurrencyRateDto[].class);
		if (response.getBody() != null) {
			for (CurrencyRateDto dto : response.getBody()) {
				if (currencies.contains(dto.getToCurrency())) {
					cache.add(dto);
				}
			}
		} else {
			throw new IllegalStateException("Received null body for url: " + parametrizedUrl);
		}
	}
}
