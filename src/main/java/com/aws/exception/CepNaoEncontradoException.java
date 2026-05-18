package com.aws.exception;

public class CepNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CepNaoEncontradoException(String cep) {
		super("CEP nao encontrado: " + cep);
	}
}
