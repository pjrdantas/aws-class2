package com.aws.exception;

public class CepInvalidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CepInvalidoException(String cep) {
		super("CEP invalido: " + cep);
	}
}
