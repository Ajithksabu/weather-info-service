package com.example.weatherInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sys {
	private int type;
	private int id;
	private String country;
	private long sunrise;
	private long sunset;
}
