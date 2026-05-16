package com.aws.usecase;

import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aws.dto.CoordenadasResponseDTO;
import com.aws.exception.LocalidadeNaoEncontradaException;
import com.aws.exception.LocalidadeVaziaException;

@Service
public class BuscarCoordenadasUseCase {

	private static final Map<String, CoordenadasResponseDTO> COORDENADAS_POR_LOCALIDADE = Map.of(
			"ubatuba", new CoordenadasResponseDTO(-23.561684, -46.655981));

	public CoordenadasResponseDTO executar(String localidade) {
		if (localidade == null || localidade.isBlank()) {
			throw new LocalidadeVaziaException();
		}

		String chave = localidade.trim().toLowerCase(Locale.ROOT);
		CoordenadasResponseDTO coordenadas = COORDENADAS_POR_LOCALIDADE.get(chave);

		if (coordenadas == null) {
			throw new LocalidadeNaoEncontradaException(localidade);
		}

		return coordenadas;
	}
}
