package com.aws.exception;

public class CepVazioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CepVazioException() {
		super("O CEP deve ser informado.");
	}
}
