package com.aws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aws.dto.ErroResponseDTO;
import com.aws.exception.LocalidadeNaoEncontradaException;
import com.aws.exception.LocalidadeVaziaException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler({ LocalidadeVaziaException.class, HttpMessageNotReadableException.class })
	public ResponseEntity<ErroResponseDTO> tratarRequisicaoVazia(Exception exception) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status)
				.body(ErroResponseDTO.of(status, "A localidade deve ser informada."));
	}

	@ExceptionHandler(LocalidadeNaoEncontradaException.class)
	public ResponseEntity<ErroResponseDTO> tratarLocalidadeNaoEncontrada(LocalidadeNaoEncontradaException exception) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(status)
				.body(ErroResponseDTO.of(status, exception.getMessage()));
	}
}
