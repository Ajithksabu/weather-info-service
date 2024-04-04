package com.example.weatherInfo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pincode_location")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PincodeLocation {

	@Id
	@Column(name = "pincode", nullable = false)
	private Integer pincode;

	private double latitude;

	private double longitude;

}
