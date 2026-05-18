package com.aws.dto;

public record CoordenadasResponseDTO(String cep, String rua, String bairro, String cidade, String estado,
		Double latitude, Double longitude) {
}
