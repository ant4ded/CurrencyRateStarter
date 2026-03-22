package my.idp.spring.currency.rate.starter.service;

import my.idp.spring.currency.rate.starter.configuration.CurrencyRateStarterProperties;
import my.idp.spring.currency.rate.starter.dto.IncomingCurrencyRateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateRangeCurrencyRateService extends AbstractCurrencyRateService implements CurrencyRateService {
	private final LocalDate dateRangeFrom;
	private final LocalDate dateRangeTo;

	public DateRangeCurrencyRateService(RestTemplate restTemplate, CurrencyRateStarterProperties properties) {
		super(restTemplate, properties);
		this.dateRangeFrom = properties.getDateFrom();
		this.dateRangeTo = properties.getDateTo();
	}

	@Override
	protected void fetchAndProcessData() {
		long days = ChronoUnit.DAYS.between(dateRangeFrom, dateRangeTo);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<LocalDate> dates = Stream.iterate(dateRangeFrom, d -> d.plusDays(1))
				.limit(days + 1)
				.collect(Collectors.toList());

		for (LocalDate date : dates) {
			String formatedDate = date.format(formatter);
			StringBuilder parametrizedUrl = new StringBuilder(url).append('?').append("ondate=").append(formatedDate).append("periodicity=0");
			ResponseEntity<IncomingCurrencyRateDto[]> response = restTemplate.getForEntity(parametrizedUrl.toString(), IncomingCurrencyRateDto[].class);
			if (response.getBody() != null) {
				for (IncomingCurrencyRateDto dto : response.getBody()) {
					addRateToCache(dto);
				}
			} else {
				throw new IllegalStateException("Received null body for url: " + parametrizedUrl);
			}
		}
	}
}
