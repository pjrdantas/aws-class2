package com.aws.dto;

public record BrasilApiCepResponseDTO(String cep, String state, String city, String neighborhood, String street,
		Location location) {

	public record Location(Coordinates coordinates) {
	}

	public record Coordinates(String latitude, String longitude) {
	}
}
