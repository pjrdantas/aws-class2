package com.aws.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aws.dto.CepRequestDTO;
import com.aws.dto.CoordenadasResponseDTO;
import com.aws.usecase.BuscarCoordenadasUseCase;

@RestController
@RequestMapping("/ceps")
public class CepController {

	private final BuscarCoordenadasUseCase buscarCoordenadasUseCase;

	public CepController(BuscarCoordenadasUseCase buscarCoordenadasUseCase) {
		this.buscarCoordenadasUseCase = buscarCoordenadasUseCase;
	}

	@PostMapping("/coordenadas")
	public ResponseEntity<CoordenadasResponseDTO> buscarCoordenadas(@RequestBody CepRequestDTO request) {
		CoordenadasResponseDTO response = buscarCoordenadasUseCase.executar(request.cep());
		return ResponseEntity.ok(response);
	}
}
