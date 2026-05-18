package com.aws.exception;

public class ServicoCepIndisponivelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServicoCepIndisponivelException(Throwable cause) {
		super("Servico de CEP indisponivel no momento.", cause);
	}
}
