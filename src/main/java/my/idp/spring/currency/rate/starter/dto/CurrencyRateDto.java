package my.idp.spring.currency.rate.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class CurrencyRateDto {
	private LocalDateTime onDate;
	private String from;
	private String to;
	private BigDecimal rate;
}
