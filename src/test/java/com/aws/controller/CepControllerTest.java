package com.aws.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.aws.dto.CoordenadasResponseDTO;
import com.aws.exception.CepInvalidoException;
import com.aws.exception.CepNaoEncontradoException;
import com.aws.exception.CepVazioException;
import com.aws.usecase.BuscarCoordenadasUseCase;

@WebMvcTest(CepController.class)
class CepControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BuscarCoordenadasUseCase buscarCoordenadasUseCase;

	@Test
	void deveRetornarCoordenadasQuandoCepForEncontrado() throws Exception {
		when(buscarCoordenadasUseCase.executar("01001-000"))
				.thenReturn(new CoordenadasResponseDTO(
						"01001-000",
						"Praça da Sé",
						"Sé",
						"São Paulo",
						"SP",
						-23.561684,
						-46.655981));

		mockMvc.perform(post("/ceps/coordenadas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cep\":\"01001-000\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cep").value("01001-000"))
				.andExpect(jsonPath("$.rua").value("Praça da Sé"))
				.andExpect(jsonPath("$.bairro").value("Sé"))
				.andExpect(jsonPath("$.cidade").value("São Paulo"))
				.andExpect(jsonPath("$.estado").value("SP"))
				.andExpect(jsonPath("$.latitude").value(-23.561684))
				.andExpect(jsonPath("$.longitude").value(-46.655981));
	}

	@Test
	void deveRetornarBadRequestQuandoCepEstiverVazio() throws Exception {
		when(buscarCoordenadasUseCase.executar(""))
				.thenThrow(new CepVazioException());

		mockMvc.perform(post("/ceps/coordenadas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cep\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.mensagem").value("O CEP deve ser informado."));
	}

	@Test
	void deveRetornarBadRequestQuandoCepForInvalido() throws Exception {
		when(buscarCoordenadasUseCase.executar("123"))
				.thenThrow(new CepInvalidoException("123"));

		mockMvc.perform(post("/ceps/coordenadas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cep\":\"123\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.mensagem").value("CEP invalido: 123"));
	}

	@Test
	void deveRetornarNotFoundQuandoCepNaoForEncontrado() throws Exception {
		when(buscarCoordenadasUseCase.executar(anyString()))
				.thenThrow(new CepNaoEncontradoException("00000000"));

		mockMvc.perform(post("/ceps/coordenadas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cep\":\"00000000\"}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.mensagem").value("CEP nao encontrado: 00000000"));
	}
}
