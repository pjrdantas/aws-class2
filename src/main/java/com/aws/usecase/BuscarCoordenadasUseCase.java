package com.aws.usecase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.aws.dto.BrasilApiCepResponseDTO;
import com.aws.dto.CoordenadasResponseDTO;
import com.aws.dto.NominatimPlaceResponseDTO;
import com.aws.exception.CepInvalidoException;
import com.aws.exception.CepNaoEncontradoException;
import com.aws.exception.CepVazioException;
import com.aws.exception.CoordenadasNaoEncontradasException;
import com.aws.exception.ServicoCepIndisponivelException;

@Service
public class BuscarCoordenadasUseCase {

	private static final String NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search"
			+ "?q={query}&format=jsonv2&limit=1&countrycodes=br";

	private final RestTemplate restTemplate;

	public BuscarCoordenadasUseCase(RestTemplateBuilder restTemplateBuilder,
			@Value("${brasilapi.base-url:https://brasilapi.com.br}") String brasilApiBaseUrl) {
		this.restTemplate = restTemplateBuilder
				.rootUri(brasilApiBaseUrl)
				.defaultHeader(HttpHeaders.USER_AGENT, "aws-class2/1.0")
				.build();
	}

	public CoordenadasResponseDTO executar(String cep) {
		String cepNormalizado = normalizarCep(cep);

		try {
			BrasilApiCepResponseDTO response = restTemplate.getForObject(
					"/api/cep/v2/{cep}",
					BrasilApiCepResponseDTO.class,
					cepNormalizado);

			return buscarCoordenadas(response, cepNormalizado);
		} catch (HttpClientErrorException.NotFound exception) {
			throw new CepNaoEncontradoException(cepNormalizado);
		} catch (RestClientException exception) {
			throw new ServicoCepIndisponivelException(exception);
		}
	}

	private String normalizarCep(String cep) {
		if (cep == null || cep.isBlank()) {
			throw new CepVazioException();
		}

		String cepNormalizado = cep.replaceAll("\\D", "");

		if (!cepNormalizado.matches("\\d{8}")) {
			throw new CepInvalidoException(cep);
		}

		return cepNormalizado;
	}

	private CoordenadasResponseDTO buscarCoordenadas(BrasilApiCepResponseDTO response, String cep) {
		Coordenadas coordenadas = extrairCoordenadasBrasilApi(response);

		if (coordenadas == null) {
			coordenadas = buscarCoordenadasPorEndereco(response);
		}

		if (coordenadas == null) {
			throw new CoordenadasNaoEncontradasException(cep);
		}

		return new CoordenadasResponseDTO(
				formatarCep(cep),
				response.street(),
				response.neighborhood(),
				response.city(),
				response.state(),
				coordenadas.latitude(),
				coordenadas.longitude());
	}

	private Coordenadas extrairCoordenadasBrasilApi(BrasilApiCepResponseDTO response) {
		if (response == null || response.location() == null || response.location().coordinates() == null) {
			return null;
		}

		String latitudeResponse = response.location().coordinates().latitude();
		String longitudeResponse = response.location().coordinates().longitude();

		if (latitudeResponse == null || latitudeResponse.isBlank()
				|| longitudeResponse == null || longitudeResponse.isBlank()) {
			return null;
		}

		try {
			Double latitude = Double.valueOf(latitudeResponse);
			Double longitude = Double.valueOf(longitudeResponse);
			return new Coordenadas(latitude, longitude);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	private Coordenadas buscarCoordenadasPorEndereco(BrasilApiCepResponseDTO response) {
		if (response == null) {
			return null;
		}

		for (String consulta : montarConsultasEndereco(response)) {
			NominatimPlaceResponseDTO[] resultados = restTemplate.getForObject(
					NOMINATIM_SEARCH_URL,
					NominatimPlaceResponseDTO[].class,
					consulta);

			if (resultados != null && resultados.length > 0) {
				Coordenadas coordenadas = converterCoordenadas(resultados[0].lat(), resultados[0].lon());

				if (coordenadas != null) {
					return coordenadas;
				}
			}
		}

		return null;
	}

	private List<String> montarConsultasEndereco(BrasilApiCepResponseDTO response) {
		List<String> consultas = new ArrayList<>();

		adicionarConsulta(consultas, response.street(), response.city(), response.state(), "Brasil");

		if (response.street() != null && response.street().startsWith("Rua ")) {
			String nomeRua = response.street().substring(4);
			adicionarConsulta(consultas, "Rua das " + nomeRua, response.city(), response.state(), "Brasil");
			adicionarConsulta(consultas, "Rua dos " + nomeRua, response.city(), response.state(), "Brasil");
			adicionarConsulta(consultas, "Rua da " + nomeRua, response.city(), response.state(), "Brasil");
			adicionarConsulta(consultas, "Rua do " + nomeRua, response.city(), response.state(), "Brasil");
		}

		return consultas;
	}

	private void adicionarConsulta(List<String> consultas, String... partes) {
		List<String> partesValidas = new ArrayList<>();

		for (String parte : partes) {
			if (parte != null && !parte.isBlank()) {
				partesValidas.add(parte.trim());
			}
		}

		if (!partesValidas.isEmpty()) {
			consultas.add(String.join(", ", partesValidas));
		}
	}

	private Coordenadas converterCoordenadas(String latitudeResponse, String longitudeResponse) {
		if (latitudeResponse == null || latitudeResponse.isBlank()
				|| longitudeResponse == null || longitudeResponse.isBlank()) {
			return null;
		}

		try {
			return new Coordenadas(Double.valueOf(latitudeResponse), Double.valueOf(longitudeResponse));
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	private String formatarCep(String cep) {
		return cep.substring(0, 5) + "-" + cep.substring(5);
	}

	private record Coordenadas(Double latitude, Double longitude) {
	}
}
