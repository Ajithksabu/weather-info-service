package com.example.weatherInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingApiResponse {

	private String zip;
	private transient String name;
	private double lat;
	private double lon;
	private transient String country;

}
