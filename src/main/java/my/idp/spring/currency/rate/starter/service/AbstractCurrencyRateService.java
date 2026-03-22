package my.idp.spring.currency.rate.starter.service;

import my.idp.spring.currency.rate.starter.configuration.CurrencyRateStarterProperties;
import my.idp.spring.currency.rate.starter.dto.CurrencyRateDto;
import my.idp.spring.currency.rate.starter.dto.IncomingCurrencyRateDto;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public abstract class AbstractCurrencyRateService implements CurrencyRateService {
	protected final RestTemplate restTemplate;
	protected final String url;
	protected final List<String> currencies;
	protected final List<CurrencyRateDto> cache = new LinkedList<>();
	protected final boolean enableCrossRate;
	protected final int crossRateScale;

	protected AbstractCurrencyRateService(RestTemplate restTemplate, CurrencyRateStarterProperties properties) {
		this.restTemplate = restTemplate;
		this.url = properties.getUrl();
		this.currencies = properties.getCurrencies();
		this.enableCrossRate = properties.isEnableCrossRate();
		this.crossRateScale = properties.getCrossRateScale();
	}

	@PostConstruct
	public void postConstruct() {
		fetchAndProcessData();
		if (enableCrossRate) {
			List<CurrencyRateDto> crossRates = generateCrossRate(crossRateScale, cache);
			cache.addAll(crossRates);
		}
	}

	protected abstract void fetchAndProcessData();

	protected void addRateToCache(IncomingCurrencyRateDto incomingDto) {
		if (currencies.isEmpty() || currencies.contains(incomingDto.getToCurrency())) {
			BigDecimal rate = removeIncomingScale(incomingDto);
			CurrencyRateDto rateDto = CurrencyRateDto.builder()
					.onDate(incomingDto.getDate())
					.from(incomingDto.getFromCurrency())
					.to(incomingDto.getToCurrency())
					.rate(rate)
					.build();
			cache.add(rateDto);
		}
	}

	@Override
	public List<CurrencyRateDto> getCachedRates() {
		return cache;
	}

	public int getDigitCount(int number) {
		if (number == 0) {
			return 1;
		}
		int count = 0;
		number = Math.abs(number);
		while (number > 0) {
			number /= 10;
			count++;
		}
		return count;
	}

	public BigDecimal removeIncomingScale(IncomingCurrencyRateDto incomingDto) {
		BigDecimal rate = incomingDto.getRate().stripTrailingZeros();
		int currentScale = rate.scale();
		int zeroCount = getDigitCount(incomingDto.getScale()) - 1;
		int scale = currentScale + zeroCount;
		rate = rate.divide(BigDecimal.valueOf(incomingDto.getScale()), scale, RoundingMode.HALF_UP);
		return rate;
	}

	public List<CurrencyRateDto> generateCrossRate(int crossRateScale, List<CurrencyRateDto> cache) {
		List<CurrencyRateDto> crossRates = new LinkedList<>();
		Map<LocalDateTime, Map<String, BigDecimal>> bynToCurrencyMapByDate = new HashMap<>();
		for (CurrencyRateDto rate : cache) {
			if ("BYN".equals(rate.getFrom())) {
				bynToCurrencyMapByDate
						.computeIfAbsent(rate.getOnDate(), k -> new HashMap<>())
						.put(rate.getTo(), rate.getRate());
			}
		}

		for (Map.Entry<LocalDateTime, Map<String, BigDecimal>> dateEntry : bynToCurrencyMapByDate.entrySet()) {
			LocalDateTime date = dateEntry.getKey();
			Map<String, BigDecimal> bynToCurrencyMap = dateEntry.getValue();

			if (bynToCurrencyMap.size() > 1) {
				List<String> currencyList = new ArrayList<>(bynToCurrencyMap.keySet());
				for (int i = 0; i < currencyList.size(); i++) {
					String a = currencyList.get(i);
					for (int j = i + 1; j < currencyList.size(); j++) {
						String b = currencyList.get(j);

						boolean hasAB = false;
						boolean hasBA = false;
						for (CurrencyRateDto rate : cache) {
							if (a.equals(rate.getFrom()) && b.equals(rate.getTo()) && date.equals(rate.getOnDate())) {
								hasAB = true;
							}
							if (b.equals(rate.getFrom()) && a.equals(rate.getTo()) && date.equals(rate.getOnDate())) {
								hasBA = true;
							}
						}

						if (!hasAB) {
							CurrencyRateDto crossRateABDto = buildCrossRate(crossRateScale, a, b, date, bynToCurrencyMap.get(a), bynToCurrencyMap.get(b));
							crossRates.add(crossRateABDto);
						}
						if (!hasBA) {
							CurrencyRateDto crossRateABDto = buildCrossRate(crossRateScale, b, a, date, bynToCurrencyMap.get(b), bynToCurrencyMap.get(a));
							crossRates.add(crossRateABDto);
						}
					}
				}
			}
		}
		return crossRates;
	}

	private static CurrencyRateDto buildCrossRate(int crossRateScale, String a, String b, LocalDateTime date, BigDecimal rateA, BigDecimal rateB) {
		BigDecimal crossRateAB = rateB.divide(rateA, crossRateScale, RoundingMode.HALF_UP);
		return CurrencyRateDto.builder()
				.onDate(date)
				.from(a)
				.to(b)
				.rate(crossRateAB)
				.build();
	}
}
