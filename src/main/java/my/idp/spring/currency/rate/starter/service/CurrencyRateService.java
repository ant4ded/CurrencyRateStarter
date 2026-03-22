package my.idp.spring.currency.rate.starter.service;

import my.idp.spring.currency.rate.starter.dto.CurrencyRateDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface CurrencyRateService {
	List<CurrencyRateDto> getCachedRates();

	default CurrencyRateDto getRate(String from, String to, LocalDate onDate) {
		if (Objects.equals(from, to)) {
			return new CurrencyRateDto(onDate.atStartOfDay(), from, to, BigDecimal.ONE);
		}
		List<CurrencyRateDto> found = getCachedRates().stream()
				.filter(dto -> dto.getFrom().equals(from) && dto.getTo().equals(to) && dto.getOnDate().toLocalDate().equals(onDate))
				.limit(1)
				.collect(Collectors.toList());
		if (found.size() != 1) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			throw new IllegalStateException("Cannot find rate for currency pair: " + from + ", " + to + " on date: " + onDate.format(formatter));
		} else {
			return found.get(0);
		}
	}
}
