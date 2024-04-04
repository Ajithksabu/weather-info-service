package com.example.weatherInfo.controller;

import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.weatherInfo.entity.WeatherInfo;
import com.example.weatherInfo.service.WeatherService;

@SpringBootTest
class WeatherInfoControllerTest {
	
	private static final Logger logger = LoggerFactory.getLogger(WeatherInfoControllerTest.class);

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherInfoController weatherInfoController;
    
    private WeatherInfo expectedWeatherInfo ;

    @BeforeEach
    public void setUp(){
        expectedWeatherInfo = new WeatherInfo();
        expectedWeatherInfo.setPincode(677888);
        expectedWeatherInfo.setPlace("Pala");
        LocalDate for_date = LocalDate.parse("2024-04-04");
        expectedWeatherInfo.setDate(for_date);
        expectedWeatherInfo.setTemperature(20.0);
        expectedWeatherInfo.setHumidity(50);
        expectedWeatherInfo.setPressure(1000);
        expectedWeatherInfo.setWindSpeed(3.46);
        expectedWeatherInfo.setDescription("clear");
    }
    
    @Test
    void testGetWeatherInfo() throws Exception {
    	logger.info("inside testGetWeatherInfo method");
        Mockito.when(weatherService.getWeatherInfo(677888,LocalDate.parse("2024-04-04"))).thenReturn(expectedWeatherInfo);
        ResponseEntity<WeatherInfo> actualWeatherInfo = weatherInfoController.getWeatherInfo(12345,LocalDate.parse("2024-04-04"));
        // verify results
        Assertions.assertEquals(HttpStatus.OK, actualWeatherInfo.getStatusCode());
        Assertions.assertEquals(expectedWeatherInfo.getTemperature(), actualWeatherInfo.getBody().getTemperature());
        Assertions.assertEquals(expectedWeatherInfo.getHumidity(), actualWeatherInfo.getBody().getHumidity());
        verify(weatherService, Mockito.times(1)).getWeatherInfo(677888,LocalDate.parse("2024-04-04") );
        
    }
}
