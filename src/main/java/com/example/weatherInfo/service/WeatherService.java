package com.example.weatherInfo.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.weatherInfo.dto.GeocodingApiResponse;
import com.example.weatherInfo.dto.WeatherApiResponse;
import com.example.weatherInfo.entity.PincodeLocation;
import com.example.weatherInfo.entity.WeatherInfo;
import com.example.weatherInfo.repository.PincodeLocationRepository;
import com.example.weatherInfo.repository.WeatherInfoRepository;

@Service
public class WeatherService {

	private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

	@Value("${open.weather.api.key}")
	private String OPEN_WEATHER_API_KEY;

	@Value("${open.weather.api.pincodeLocation.url}")
	private String OPEN_WEATHER_PINCODE_LOCATION_URL;

	@Value("${open.weather.api.url}")
	private String OPEN_WEATHER_API_URL;

	@Autowired
	private WeatherInfoRepository weatherInfoRepository;

	@Autowired
	private PincodeLocationRepository pincodeLocationRepository;

	public WeatherInfo getWeatherInfo(Integer pincode, LocalDate date) throws Exception {

		logger.info("Entry Inside WeatherService getWeatherInfo method");

		Optional<WeatherInfo> optionalWeatherInfo = weatherInfoRepository.findByPincodeAndDate(pincode, date);

		if (optionalWeatherInfo.isPresent())
			return optionalWeatherInfo.get();

		double latitude;
		double longitude;

		Optional<PincodeLocation> optionalPincodeLocation = null;
		try {
			optionalPincodeLocation = pincodeLocationRepository.findById(pincode);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
		}

		// Check if the latitude and longitude are already saved in the database
		if (optionalPincodeLocation.isPresent()) {
			latitude = optionalPincodeLocation.get().getLatitude();
			longitude = optionalPincodeLocation.get().getLongitude();
		} else {
			PincodeLocation pincodeLocation = getPincodeLocation(pincode);
			logger.info("Pincode location info:" + pincodeLocation);
			latitude = pincodeLocation.getLatitude();
			longitude = pincodeLocation.getLongitude();
			pincodeLocationRepository.save(pincodeLocation);
		}

		WeatherApiResponse weatherApiResponse = getWeatherApiResponse(latitude, longitude, date);

		WeatherInfo weatherInfo = new WeatherInfo();
		if (weatherApiResponse != null) {
			logger.info("Weather API response:" + weatherApiResponse);
			saveWeatherApiResponse(weatherApiResponse, pincode, date, weatherInfo);
		}

		logger.info("Exit from WeatherService getWeatherInfo method");
		return weatherInfo;
	}

	public void saveWeatherApiResponse(WeatherApiResponse weatherApiResponse, Integer pincode, LocalDate date,
			WeatherInfo weatherInfo) {

		weatherInfo.setPincode(pincode);
		weatherInfo.setDate(date);
		weatherInfo.setTemperature(weatherApiResponse.getMain().getTemp());
		weatherInfo.setHumidity(weatherApiResponse.getMain().getHumidity());
		weatherInfo.setPressure(weatherApiResponse.getMain().getPressure());
		weatherInfo.setWindSpeed(weatherApiResponse.getWind().getSpeed());
		weatherInfo.setDescription(weatherApiResponse.getWeather().get(0).getDescription());
		weatherInfo.setPlace(weatherApiResponse.getName());
		weatherInfoRepository.save(weatherInfo);

		logger.info(weatherInfo.toString());

	}

	public PincodeLocation getPincodeLocation(Integer pincode) throws Exception {

		logger.info("Entry Inside WeatherService getPincodeLocation method");

		String url = OPEN_WEATHER_PINCODE_LOCATION_URL + pincode + ",in&appid=" + OPEN_WEATHER_API_KEY;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<GeocodingApiResponse> response = restTemplate.getForEntity(url, GeocodingApiResponse.class);
		double latitude = 0, longitude = 0;
		if (response.getStatusCode().is2xxSuccessful()) {
			latitude = response.getBody().getLat();
			longitude = response.getBody().getLon();
		} else {
			throw new Exception(response.getStatusCode().toString());
		}

		return new PincodeLocation(pincode, latitude, longitude);
	}

	public WeatherApiResponse getWeatherApiResponse(double latitude, double longitude, LocalDate date)
			throws Exception {

		logger.info("Entry Inside WeatherService getWeatherApiResponse method");

		long unixTimestamp = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
		String url = OPEN_WEATHER_API_URL + latitude + "&lon=" + longitude + "&appid=" + OPEN_WEATHER_API_KEY + "&dt="
				+ unixTimestamp;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url, WeatherApiResponse.class);
		if (response.getStatusCode().isError()) {
			throw new Exception();
		}
		return response.getBody();
	}

}
