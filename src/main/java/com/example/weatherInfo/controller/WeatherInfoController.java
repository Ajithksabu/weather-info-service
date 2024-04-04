package com.example.weatherInfo.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weatherInfo.entity.WeatherInfo;
import com.example.weatherInfo.service.WeatherService;

@RestController
public class WeatherInfoController {

	private static final Logger logger = LoggerFactory.getLogger(WeatherInfoController.class);

	@Autowired
	WeatherService weatherService;

	@GetMapping("/weather")
	public ResponseEntity<WeatherInfo> getWeatherInfo(@RequestParam Integer pincode,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate for_date) {

		logger.info("Entry Inside WeatherInfoController getWeatherInfo method pincode {} and forDate {}", pincode,
				for_date);

		WeatherInfo weatherInfo = null;

		try {
			weatherInfo = weatherService.getWeatherInfo(pincode, for_date);
		}

		catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		logger.info("Exit from WeatherInfoController getWeatherInfo method");
		return ResponseEntity.ok(weatherInfo);
	}

}
