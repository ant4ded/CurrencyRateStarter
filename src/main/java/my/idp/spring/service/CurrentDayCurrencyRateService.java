package my.idp.spring.service;

import my.idp.spring.configuration.CurrencyRateStarterProperties;
import my.idp.spring.dto.IncomingCurrencyRateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class CurrentDayCurrencyRateService extends AbstractCurrencyRateService implements CurrencyRateService {
	public CurrentDayCurrencyRateService(RestTemplate restTemplate, CurrencyRateStarterProperties properties) {
		super(restTemplate, properties);
	}

	@Override
	protected void fetchAndProcessData() {
		StringBuilder parametrizedUrl = new StringBuilder(url).append('?').append("periodicity=0");
		ResponseEntity<IncomingCurrencyRateDto[]> response = restTemplate.getForEntity(parametrizedUrl.toString(), IncomingCurrencyRateDto[].class);
		if (response.getBody() != null) {
			for (IncomingCurrencyRateDto incomingDto : response.getBody()) {
				addRateToCache(incomingDto);
			}
		} else {
			throw new IllegalStateException("Received null body for url: " + parametrizedUrl);
		}
	}
}
