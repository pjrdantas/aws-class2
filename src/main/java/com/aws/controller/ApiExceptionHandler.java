package com.aws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aws.dto.ErroResponseDTO;
import com.aws.exception.CepInvalidoException;
import com.aws.exception.CepNaoEncontradoException;
import com.aws.exception.CepVazioException;
import com.aws.exception.CoordenadasNaoEncontradasException;
import com.aws.exception.ServicoCepIndisponivelException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler({ CepVazioException.class, HttpMessageNotReadableException.class })
	public ResponseEntity<ErroResponseDTO> tratarRequisicaoVazia(Exception exception) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status)
				.body(ErroResponseDTO.of(status, "O CEP deve ser informado."));
	}

	@ExceptionHandler(CepInvalidoException.class)
	public ResponseEntity<ErroResponseDTO> tratarCepInvalido(CepInvalidoException exception) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status)
				.body(ErroResponseDTO.of(status, exception.getMessage()));
	}

	@ExceptionHandler({ CepNaoEncontradoException.class, CoordenadasNaoEncontradasException.class })
	public ResponseEntity<ErroResponseDTO> tratarCepNaoEncontrado(RuntimeException exception) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(status)
				.body(ErroResponseDTO.of(status, exception.getMessage()));
	}

	@ExceptionHandler(ServicoCepIndisponivelException.class)
	public ResponseEntity<ErroResponseDTO> tratarServicoCepIndisponivel(ServicoCepIndisponivelException exception) {
		HttpStatus status = HttpStatus.BAD_GATEWAY;
		return ResponseEntity.status(status)
				.body(ErroResponseDTO.of(status, exception.getMessage()));
	}
}
