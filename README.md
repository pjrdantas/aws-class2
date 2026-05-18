# aws-class2

API Spring Boot para buscar coordenadas de um CEP.

A API consulta a BrasilAPI CEP v2. Quando a BrasilAPI retorna o endereco sem latitude/longitude, a aplicacao usa o endereco retornado como fallback para buscar as coordenadas no OpenStreetMap/Nominatim.

## Como executar

```bash
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

A API sobe em:

```text
http://localhost:8082
```

## Endpoint

```http
POST /ceps/coordenadas
Content-Type: application/json
```

## Exemplo com curl

Linux, macOS ou Git Bash:

```bash
curl -X POST http://localhost:8082/ceps/coordenadas \
  -H "Content-Type: application/json" \
  -d '{"cep":"01001-000"}'
```

Windows PowerShell:

```powershell
curl.exe -X POST http://localhost:8082/ceps/coordenadas `
  -H "Content-Type: application/json" `
  -d "{\"cep\":\"01001-000\"}"
```

## Como usar no Bruno

1. Abra o Bruno e crie uma nova request.
2. Selecione o metodo `POST`.
3. Informe a URL:

```text
http://localhost:8082/ceps/coordenadas
```

4. Em `Headers`, adicione:

```text
Content-Type: application/json
```

5. Em `Body`, selecione `JSON` e informe:

```json
{
  "cep": "01001-000"
}
```

Tambem funciona com CEPs em que a BrasilAPI retorna o endereco, mas nao retorna coordenadas diretamente:

```json
{
  "cep": "11691-024"
}
```

6. Clique em `Send`.

## Resposta de sucesso

HTTP Status: `200 OK`

```json
{
  "cep": "01001-000",
  "rua": "Praça da Sé",
  "bairro": "Sé",
  "cidade": "São Paulo",
  "estado": "SP",
  "latitude": -23.561684,
  "longitude": -46.655981
}
```

## Erro quando o CEP estiver vazio

HTTP Status: `400 Bad Request`

Entrada:

```json
{
  "cep": ""
}
```

Resposta:

```json
{
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "O CEP deve ser informado."
}
```

## Erro quando o CEP for invalido

HTTP Status: `400 Bad Request`

Entrada:

```json
{
  "cep": "123"
}
```

Resposta:

```json
{
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "CEP invalido: 123"
}
```

## Erro quando o CEP nao for encontrado

HTTP Status: `404 Not Found`

Entrada:

```json
{
  "cep": "00000000"
}
```

Resposta:

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "CEP nao encontrado: 00000000"
}
```
