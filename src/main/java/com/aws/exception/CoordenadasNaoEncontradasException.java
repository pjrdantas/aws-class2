package com.aws.exception;

public class CoordenadasNaoEncontradasException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CoordenadasNaoEncontradasException(String cep) {
		super("Coordenadas nao encontradas para o CEP: " + cep);
	}
}
