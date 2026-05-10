package my.idp.spring.actuator;

import my.idp.spring.dto.CurrencyRateDto;
import my.idp.spring.service.CurrencyRateService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.List;

@Endpoint(id = "currencyRates")
public class CurrencyRateEndpoint {
	private CurrencyRateService service;

	public CurrencyRateEndpoint(CurrencyRateService service) {
		this.service = service;
	}

	@ReadOperation
	public List<CurrencyRateDto> getCurrencyRates() {
		return service.getCachedRates();
	}
}
