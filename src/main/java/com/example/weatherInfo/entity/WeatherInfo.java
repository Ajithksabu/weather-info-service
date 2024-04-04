package com.example.weatherInfo.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weather_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer pincode;

	private String place;

	private LocalDate date;

	private double temperature;

	private int humidity;

	private int pressure;

	private double windSpeed;

	private String description;

}