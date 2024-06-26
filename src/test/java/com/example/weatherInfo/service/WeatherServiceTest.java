package com.example.weatherInfo.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.example.weatherInfo.dto.Coord;
import com.example.weatherInfo.dto.GeocodingApiResponse;
import com.example.weatherInfo.dto.Weather;
import com.example.weatherInfo.dto.WeatherApiResponse;
import com.example.weatherInfo.entity.PincodeLocation;
import com.example.weatherInfo.entity.WeatherInfo;
import com.example.weatherInfo.repository.PincodeLocationRepository;
import com.example.weatherInfo.repository.WeatherInfoRepository;

@SpringBootTest
class WeatherServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private PincodeLocationRepository pincodeLocationRepository;

	@Mock
	private WeatherInfoRepository weatherInfoRepository;

	@InjectMocks
	private WeatherService weatherService;

	@Test
	void testGetPincodeLocation() throws Exception {
		// create mock response
		GeocodingApiResponse mockResponse = new GeocodingApiResponse("686574", "Palackattumala", 9.633, 76.679, "IN");
		ResponseEntity<GeocodingApiResponse> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
		Mockito.when(restTemplate.getForEntity(anyString(), eq(GeocodingApiResponse.class)))
				.thenReturn(mockResponseEntity);
		PincodeLocation actualPincodeLocation = weatherService.getPincodeLocation(686574);
		// verify results
		Assertions.assertEquals(mockResponse.getZip(), Integer.toString(actualPincodeLocation.getPincode()));
		Assertions.assertEquals(mockResponse.getLat(), actualPincodeLocation.getLatitude());
		Assertions.assertEquals(mockResponse.getLon(), actualPincodeLocation.getLongitude());
	}

	@Test
	void testGetWeatherApiResponse() throws Exception {
		WeatherApiResponse mockResponse = new WeatherApiResponse();
		mockResponse.setCoord(new Coord(9.633, 76.679));
		List<Weather> temp = new ArrayList<>();
		temp.add(new Weather(804, "Clouds", "overcast clouds", "04d"));
		mockResponse.setWeather(temp);
		Mockito.when(restTemplate.getForEntity(anyString(), eq(WeatherApiResponse.class)))
				.thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
		WeatherApiResponse actualResponse = weatherService.getWeatherApiResponse(9.633, 76.679,
				LocalDate.parse("2024-04-03"));
		Assertions.assertEquals(mockResponse.getCoord().getLat(), actualResponse.getCoord().getLat());
		Assertions.assertEquals(mockResponse.getCoord().getLon(), actualResponse.getCoord().getLon());
		Assertions.assertEquals(mockResponse.getWeather().get(0).getMain(),
				actualResponse.getWeather().get(0).getMain());
	}

	@Test
	void testGetWeatherInfo() throws Exception {

		PincodeLocation pincodeLocationmock = new PincodeLocation(686574, 9.633, 76.679);

		WeatherInfo weatherInfomock = new WeatherInfo();
		weatherInfomock.setPincode(686574);
		weatherInfomock.setDate(LocalDate.parse("2024-04-03"));
		weatherInfomock.setPlace("Palackattumala");
		weatherInfomock.setDescription("overcast clouds");

		WeatherApiResponse mockResponse = new WeatherApiResponse();
		mockResponse.setCoord(new Coord(9.633, 76.679));
		List<Weather> temp = new ArrayList<>();
		temp.add(new Weather(804, "Clouds", "overcast clouds", "04d"));
		mockResponse.setWeather(temp);

		when(pincodeLocationRepository.findById(686574)).thenReturn(Optional.of(pincodeLocationmock));
		when(weatherInfoRepository.findByPincodeAndDate(686574, LocalDate.parse("2024-04-03")))
				.thenReturn(Optional.of(weatherInfomock));
		when(weatherInfoRepository.save(weatherInfomock)).thenReturn(weatherInfomock);
		when(restTemplate.getForEntity(anyString(), eq(WeatherApiResponse.class)))
				.thenReturn(ResponseEntity.ok(mockResponse));

		WeatherInfo weatherInfo = weatherService.getWeatherInfo(686574, LocalDate.parse("2024-04-03"));
		assertNotNull(weatherInfo);
		Assertions.assertEquals(weatherInfomock.getPincode(), weatherInfo.getPincode());
		Assertions.assertEquals(weatherInfomock.getDate(), weatherInfo.getDate());
		Assertions.assertEquals(weatherInfomock.getPlace(), weatherInfo.getPlace());
		Mockito.verify(weatherInfoRepository).findByPincodeAndDate(686574, LocalDate.parse("2024-04-03"));
		Mockito.verifyNoMoreInteractions(weatherInfoRepository);
	}
}
