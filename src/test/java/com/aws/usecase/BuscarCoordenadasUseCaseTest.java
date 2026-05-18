package com.aws.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import com.aws.dto.CoordenadasResponseDTO;
import com.aws.exception.CepInvalidoException;
import com.aws.exception.CepNaoEncontradoException;

@RestClientTest(BuscarCoordenadasUseCase.class)
@TestPropertySource(properties = "brasilapi.base-url=https://brasilapi.com.br")
class BuscarCoordenadasUseCaseTest {

	@Autowired
	private BuscarCoordenadasUseCase buscarCoordenadasUseCase;

	@Autowired
	private MockRestServiceServer server;

	@Test
	void deveBuscarCoordenadasPorCepNaBrasilApi() {
		server.expect(requestTo("/api/cep/v2/01001000"))
				.andRespond(withSuccess("""
						{
						  "cep": "01001000",
						  "state": "SP",
						  "city": "São Paulo",
						  "neighborhood": "Sé",
						  "street": "Praça da Sé",
						  "location": {
						    "type": "Point",
						    "coordinates": {
						      "longitude": "-46.633308",
						      "latitude": "-23.55052"
						    }
						  }
						}
						""", MediaType.APPLICATION_JSON));

		CoordenadasResponseDTO response = buscarCoordenadasUseCase.executar("01001-000");

		assertThat(response.cep()).isEqualTo("01001-000");
		assertThat(response.rua()).isEqualTo("Praça da Sé");
		assertThat(response.bairro()).isEqualTo("Sé");
		assertThat(response.cidade()).isEqualTo("São Paulo");
		assertThat(response.estado()).isEqualTo("SP");
		assertThat(response.latitude()).isEqualTo(-23.55052);
		assertThat(response.longitude()).isEqualTo(-46.633308);
	}

	@Test
	void deveLancarErroQuandoCepForInvalido() {
		assertThatThrownBy(() -> buscarCoordenadasUseCase.executar("123"))
				.isInstanceOf(CepInvalidoException.class)
				.hasMessage("CEP invalido: 123");
	}

	@Test
	void deveLancarErroQuandoCepNaoForEncontrado() {
		server.expect(requestTo("/api/cep/v2/00000000"))
				.andRespond(withResourceNotFound());

		assertThatThrownBy(() -> buscarCoordenadasUseCase.executar("00000000"))
				.isInstanceOf(CepNaoEncontradoException.class)
				.hasMessage("CEP nao encontrado: 00000000");
	}

	@Test
	void deveBuscarCoordenadasPorEnderecoQuandoBrasilApiNaoRetornarCoordenadas() {
		server.expect(requestTo("/api/cep/v2/11691024"))
				.andRespond(withSuccess("""
						{
						  "cep": "11691024",
						  "state": "SP",
						  "city": "Ubatuba",
						  "neighborhood": "Mato Dentro",
						  "street": "Rua Magnólias",
						  "location": {
						    "type": "Point",
						    "coordinates": {}
						  }
						}
						""", MediaType.APPLICATION_JSON));

		server.expect(requestToUriTemplate(
				"https://nominatim.openstreetmap.org/search?q={query}&format=jsonv2&limit=1&countrycodes=br",
				"Rua Magnólias, Ubatuba, SP, Brasil"))
				.andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

		server.expect(requestToUriTemplate(
				"https://nominatim.openstreetmap.org/search?q={query}&format=jsonv2&limit=1&countrycodes=br",
				"Rua das Magnólias, Ubatuba, SP, Brasil"))
				.andRespond(withSuccess("""
						[
						  {
						    "lat": "-23.4406838",
						    "lon": "-45.0911815"
						  }
						]
						""", MediaType.APPLICATION_JSON));

		CoordenadasResponseDTO response = buscarCoordenadasUseCase.executar("11691-024");

		assertThat(response.cep()).isEqualTo("11691-024");
		assertThat(response.rua()).isEqualTo("Rua Magnólias");
		assertThat(response.bairro()).isEqualTo("Mato Dentro");
		assertThat(response.cidade()).isEqualTo("Ubatuba");
		assertThat(response.estado()).isEqualTo("SP");
		assertThat(response.latitude()).isEqualTo(-23.4406838);
		assertThat(response.longitude()).isEqualTo(-45.0911815);
	}
}
