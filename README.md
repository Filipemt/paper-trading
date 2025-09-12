# Paper Trading API

API RESTful para uma plataforma de simulação de investimentos (Paper Trading), construída com Java, Spring Boot e princípios de Arquitetura Limpa.

---

## 1. Visão Geral do Projeto
O objetivo deste projeto é desenvolver um sistema de "Paper Trading" robusto e escalável.  
A aplicação permitirá que usuários se cadastrem, gerenciem uma carteira de investimentos virtual com um saldo inicial, e simulem a compra e venda de ativos financeiros (ações, FIIs) com base em cotações de mercado.

Este projeto serve como um estudo de caso prático na aplicação de padrões de arquitetura de software modernos em um ambiente backend.

---

## 2. Arquitetura
A aplicação é construída seguindo os princípios da **Arquitetura Hexagonal (Portas e Adaptadores)** para garantir um núcleo de negócio desacoplado, testável e independente de tecnologias externas (web, banco de dados).

### Princípio Central
A regra fundamental é a **Regra da Dependência**, que estabelece que todas as dependências do código-fonte devem apontar para "dentro", em direção ao núcleo do negócio.
- A camada de **infraestrutura** depende da camada de **aplicação**.
- A camada de **aplicação** depende da camada de **domínio**.
- O **domínio** não conhece ninguém.

### Estrutura de Pacotes
```text
com.filipecode.papertrading
├── PaperTradingApplication.java  # Raiz do Component Scan
│
├── domain                        # O NÚCLEO: Regras de negócio puras
│   ├── model                     # Entidades e Objetos de Valor (ex: User, Order)
│   ├── repository                # PORTAS de saída (Interfaces de persistência)
│   ├── service                   # PORTAS de saída (Serviços externos, ex: PriceProviderPort)
│   └── exception                 # Exceções de negócio customizadas
│
├── application                   # O CÉREBRO: Orquestração dos casos de uso
│   ├── usecase                   # PORTAS de entrada (Interfaces, ex: RegisterUserUseCase)
│   └── service                   # Implementações dos casos de uso (ex: UserService)
│
└── infrastructure                # A CASCA: Detalhes de tecnologia (Spring, JPA, Web, etc.)
    ├── web                       # ADAPTADORES de entrada (Controllers, DTOs, Exception Handlers)
    ├── persistence               # ADAPTADORES de saída (JPA Repositories)
    ├── client                    # ADAPTADORES de saída (APIs externas)
    ├── config                    # Configurações do Spring (@Configuration, Beans)
    └── security                  # Implementações de segurança (JWT, Filters)
```
---

## 3. Stack de Tecnologias
- **Linguagem & Framework**: Java 17+, Spring Boot 3+
- **Segurança**: Spring Security
- **Persistência**: Spring Data JPA, Hibernate
- **Banco de Dados**:
    - Desenvolvimento: H2 (modo arquivo)
    - Produção: PostgreSQL
- **Migrações**: Flyway
- **Testes**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven

---

## 4. Como Executar o Projeto Localmente

### Pré-requisitos
- Java (JDK) 17 ou superior
- Apache Maven 3.8+

### Passos
1. Clone o repositório.
2. Abra um terminal na raiz do projeto.
3. Execute o build com o Maven:
   ```bash
   mvn clean install
4. Rode a aplicação
   ```bash
    java -jar target/paper-trading-api-0.0.1-SNAPSHOT.jar
5. A API estará disponível em: http://localhost:8080

### Banco de Dados de Desenvolvimento (H2
Console: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./target/papertradingdb

User Name: sa

Password: (vazio)

---

## 5. Modelo de Domínio
O núcleo da aplicação é definido por **6 entidades principais**:

- **User**: Representa o cliente da plataforma.
- **Portfolio**: A carteira de investimentos do usuário (saldo e posições).
- **Asset**: O catálogo de ativos negociáveis (ex: PETR4).
- **Position**: Registro da posse de um ativo por um usuário (ex: "100 unidades de PETR4").
- **Order**: Intenção de compra ou venda de um ativo.
- **Transaction**: Registro histórico de uma ordem executada.

---

## 6. Contrato da API (v1.0)

### Registro de Usuário
| Método | Endpoint                | Descrição                             |
|--------|-------------------------|---------------------------------------|
| POST   | `/api/v1/auth/register` | Registra um novo usuário na plataforma |

#### Corpo da Requisição (`RegisterUserRequestDTO`)
```json
{
  "name": "Nome Completo",
  "email": "email@valido.com",
  "password": "senhaComPeloMenos8Caracteres",
  "cpf": "123.456.789-00"
}
```
#### Resposta de Sucesso (201 Created - AuthResponseDTO)
```json
{
"userId": 1,
"name": "Nome Completo",
"token": "token-simulado-para-ambiente-dev"
}
```

---

## 7. Fluxo Principal: Registro de Usuário
O fluxo de registro demonstra a arquitetura em ação:

1. **AuthController** recebe o DTO e o valida (incluindo a validação customizada `@CPF`).
2. Chama o **RegisterUserUseCase**.
3. O **UserService** orquestra a lógica:
    - Verifica via `UserRepositoryPort` se o e-mail/CPF já existem.
    - Criptografa a senha com `PasswordEncoder`.
    - Cria as entidades `User` e `Portfolio`.
    - Salva o `User` (o `Portfolio` é salvo em cascata).
    - Chama o `TokenProviderPort` para gerar um token.
    - Retorna o `AuthResponseDTO`.
4. O **GlobalExceptionHandler** captura exceções de negócio (ex: `UserAlreadyExistsException`) ou de validação, traduzindo em respostas HTTP **4xx padronizadas**.

---

## 8. Estratégia de Testes
A abordagem de testes é feita em múltiplas camadas:

### 🔹 Testes de Unidade
- Foco na lógica de negócio dentro dos Services.
- Executados em isolamento total com **JUnit 5 + Mockito**.

### 🔹 Testes de Integração Web
- Validação dos Controllers e contrato da API.
- Utiliza `@WebMvcTest`, simulando requisições HTTP reais e validando as respostas.  



