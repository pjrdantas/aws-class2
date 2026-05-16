package com.aws.exception;

public class LocalidadeNaoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LocalidadeNaoEncontradaException(String localidade) {
		super("Localidade nao encontrada: " + localidade);
	}
}
