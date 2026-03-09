package my.idp.spring.currency.rate.starter.service;

import my.idp.spring.currency.rate.starter.dto.CurrencyRateDto;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateRangeCurrencyRateService implements CurrencyRateService, SmartInitializingSingleton {
	private final RestTemplate restTemplate;
	private final String url;
	private final List<String> currencies;
	private final LocalDate dateRangeFrom;
	private final LocalDate dateRangeTo;

	private final List<CurrencyRateDto> cache = new LinkedList<>();

	public DateRangeCurrencyRateService(RestTemplate restTemplate, String url, List<String> currencies, LocalDate dateRangeFrom, LocalDate dateRangeTo) {
		this.restTemplate = restTemplate;
		this.url = url;
		this.currencies = currencies;
		this.dateRangeFrom = dateRangeFrom;
		this.dateRangeTo = dateRangeTo;
	}

	@Override
	public List<CurrencyRateDto> getCachedRates() {
		return cache;
	}

	@Override
	public void afterSingletonsInstantiated() {
		long days = ChronoUnit.DAYS.between(dateRangeFrom, dateRangeTo);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<LocalDate> dates = Stream.iterate(dateRangeFrom, d -> d.plusDays(1)).limit(days + 1).collect(Collectors.toList());
		for (LocalDate date : dates) {
			String formatedDate = date.format(formatter);
			StringBuilder parametrizedUrl = new StringBuilder(url).append('?').append("ondate=").append(formatedDate).append("periodicity=0");
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
}
