package my.idp.spring.currency.rate.starter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class IncomingCurrencyRateDto {
	@JsonProperty("Cur_ID")
	private int id;
	@JsonProperty("Date")
	private LocalDateTime date;
	@JsonProperty("Cur_Abbreviation")
	private String toCurrency;
	@JsonProperty("Cur_Scale")
	private int scale;
	@JsonProperty("Cur_Name")
	private String name;
	@JsonProperty("Cur_OfficialRate")
	private BigDecimal rate;
	private String fromCurrency = "BYN";
}
