package com.fleetmanager.fleet.dto.request;

import com.fleetmanager.fleet.enums.VehicleType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleCreateDTO {

	@NotBlank(message = "License plate is required")
	@Size(min = 3, max = 20, message = "License plate must be between 3 and 20 characters")
	@Pattern(regexp = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{1,4}$|^[A-Z0-9-]{5,12}$", message = "Invalid license plate format. Expected formats: XX00XX0000 or similar (country-specific)")
	private String licensePlate;

	@NotBlank(message = "Vehicle make is required")
	@Size(max = 50, message = "Make cannot exceed 50 characters")
	private String make;

	@NotBlank(message = "Vehicle model is required")
	@Size(max = 50, message = "Model cannot exceed 50 characters")
	private String model;

	@NotNull(message = "Manufacturing year is required")
	@Min(value = 1900, message = "Year must be 1900 or later")
	@Max(value = 2100, message = "Year must not be greater than 2100")
	private Integer year;

	@Size(min = 11, max = 17, message = "VIN must be between 11 and 17 characters")
	private String vin;

	@NotNull(message = "Vehicle type is required")
	private VehicleType type;
	@Size(max = 500, message = "Image URL is too long")
	@Pattern(regexp = "^(http|https)://.*$", message = "Image URL must be a valid HTTP/HTTPS URL")
	private String imageUrl;

	@PositiveOrZero(message = "Odometer reading cannot be negative")
	private Integer odometerReading;
}
