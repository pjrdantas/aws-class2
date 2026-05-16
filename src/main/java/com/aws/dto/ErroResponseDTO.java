package com.aws.dto;

import org.springframework.http.HttpStatus;

public record ErroResponseDTO(int status, String erro, String mensagem) {

	public static ErroResponseDTO of(HttpStatus status, String mensagem) {
		return new ErroResponseDTO(status.value(), status.getReasonPhrase(), mensagem);
	}
}
