package com.aws.exception;

public class LocalidadeVaziaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LocalidadeVaziaException() {
		super("A localidade deve ser informada.");
	}
}
