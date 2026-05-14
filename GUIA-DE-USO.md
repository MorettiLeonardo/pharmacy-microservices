# Guia de Uso - pharmacy-microservices

## Como rodar

1. Garanta que o Docker Desktop esteja em execução.
2. No terminal:

```bash
cd C:\pharmacy-microservices
docker compose up --build
```

3. Portas dos serviços:
- Gateway: `8080`
- Sales Service: `8081`
- Inventory Service: `8082`
- Expiration Service: `8083`
- Kafka: `9092`
- Zookeeper: `2181`

## Arquitetura e fluxo

- `sales-service` cria venda e publica evento Kafka `sale-created`.
- `inventory-service` consome `sale-created` e reduz estoque.
- `inventory-service` publica `product-created` quando um produto novo é cadastrado.
- `expiration-service` consome `product-created` e cadastra automaticamente o monitoramento de validade.
- `expiration-service` executa scheduler a cada 10s para marcar vencidos como `EXPIRED`.
- `gateway` centraliza o acesso HTTP.

Fluxo de venda:
1. Cliente chama `POST /sales` no Gateway.
2. Sales persiste venda.
3. Sales publica evento `sale-created` no Kafka.
4. Inventory consome evento e atualiza estoque.

## Rotas (via Gateway)

### Sales
- `POST /sales`
- `GET /sales`
- `GET /sales/{id}`

### Inventory
- `POST /products`
- `GET /products`
- `GET /products/{id}`
- `PUT /products/{id}/stock`

### Expiration
- `GET /api/expirations/products`
- `GET /api/expirations/products/expired`
- `POST /api/expirations/products`

## Payloads de exemplo

### Criar produto
**POST** `http://localhost:8080/products`

```json
{
  "name": "Dipirona 500mg",
  "stock": 100,
  "expirationDate": "2026-12-31",
  "controlled": false
}
```

### Atualizar estoque
**PUT** `http://localhost:8080/products/1/stock`

```json
{
  "stock": 80
}
```

### Criar venda
**POST** `http://localhost:8080/sales`

```json
{
  "productId": 1,
  "quantity": 2
}
```

### Criar registro de validade (opcional/manual)
**POST** `http://localhost:8080/api/expirations/products`

```json
{
  "productCode": "MED-001",
  "productName": "Amoxicilina 500mg",
  "expirationDate": "2025-01-10"
}
```

## Respostas esperadas (resumo)

### Sales
- `POST /sales` retorna `201 Created` com:

```json
{
  "id": 1,
  "productId": 1,
  "quantity": 2,
  "createdAt": "2026-05-13T17:00:00"
}
```

- `GET /sales/{id}` inexistente retorna `404`.

### Inventory
- `POST /products` retorna `201 Created` com o produto criado.
- `GET /products` retorna `200 OK` com a lista de produtos.
- `GET /products/{id}` retorna `200 OK` com o produto; se não existir, retorna `404`.
- `PUT /products/{id}/stock` retorna `200 OK` com o produto atualizado.
- Erros da API seguem o padrão abaixo e principais cenários:
  - `400 Bad Request`: payload inválido (campos obrigatórios, estoque negativo, etc.).
  - `404 Not Found`: produto não encontrado.
  - `409 Conflict`: estoque insuficiente (fluxo de baixa via evento Kafka).

```json
{
  "error": "Product not found with id=999"
}
```

### Expiration
- `GET /api/expirations/products`: lista geral.
- `GET /api/expirations/products/expired`: apenas expirados.
- Scheduler (`fixedRate=10000`) marca vencidos e gera logs (inclusive quando não encontra vencidos).

## Evento Kafka

Tópico: `sale-created`

Payload:

```json
{
  "saleId": 1,
  "productId": 1,
  "quantity": 2
}
```

Tópico: `product-created`

Payload:

```json
{
  "productId": 1,
  "name": "Dipirona 500mg",
  "expirationDate": "2026-12-31"
}
```

## Observações

- Banco por serviço: H2 em memória (dados não persistem após restart).
- Sem autenticação/autorização.
- Sem pagamentos.
- Sem integrações externas.
- Observabilidade baseada em logs dos serviços.
