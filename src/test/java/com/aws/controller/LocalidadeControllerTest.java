package com.aws.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LocalidadeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void deveRetornarCoordenadasQuandoLocalidadeForUbatuba() throws Exception {
		mockMvc.perform(post("/localidades/coordenadas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"localidade\":\"Ubatuba\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.latitude").value(-23.561684))
				.andExpect(jsonPath("$.longitude").value(-46.655981));
	}

	@Test
	void deveRetornarBadRequestQuandoLocalidadeEstiverVazia() throws Exception {
		mockMvc.perform(post("/localidades/coordenadas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"localidade\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.mensagem").value("A localidade deve ser informada."));
	}

	@Test
	void deveRetornarNotFoundQuandoLocalidadeNaoForEncontrada() throws Exception {
		mockMvc.perform(post("/localidades/coordenadas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"localidade\":\"Santos\"}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.mensagem").value("Localidade nao encontrada: Santos"));
	}
}
