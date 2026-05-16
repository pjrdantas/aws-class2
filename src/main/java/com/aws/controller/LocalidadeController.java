package com.aws.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aws.dto.CoordenadasResponseDTO;
import com.aws.dto.LocalidadeRequestDTO;
import com.aws.usecase.BuscarCoordenadasUseCase;

@RestController
@RequestMapping("/localidades")
public class LocalidadeController {

	private final BuscarCoordenadasUseCase buscarCoordenadasUseCase;

	public LocalidadeController(BuscarCoordenadasUseCase buscarCoordenadasUseCase) {
		this.buscarCoordenadasUseCase = buscarCoordenadasUseCase;
	}

	@PostMapping("/coordenadas")
	public ResponseEntity<CoordenadasResponseDTO> buscarCoordenadas(@RequestBody LocalidadeRequestDTO request) {
		CoordenadasResponseDTO response = buscarCoordenadasUseCase.executar(request.localidade());
		return ResponseEntity.ok(response);
	}
}
