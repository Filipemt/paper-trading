Documento de Design: Simulador de Carteira de Ações
Versão: 1.0
Data: 07/09/2025

# 1. Visão Geral do Projeto
O objetivo é desenvolver um sistema de "Paper Trading" como um projeto de portfólio para demonstrar competências em desenvolvimento backend com Java e Spring. A aplicação permitirá que usuários cadastrados gerenciem uma carteira de ações virtual, executando ordens de compra e venda baseadas em cotações de mercado (com dados reais ou simulados).

**Objetivos de Aprendizado:**

- Modelagem de domínio complexo.
- Implementação de regras de negócio não-triviais.
- Segurança com autenticação/autorização baseada em tokens (JWT).
- Criação de uma API REST bem documentada (Swagger).
- Boas práticas de persistência de dados com Spring Data JPA.
- Implementação de testes unitários e de integração.
- Deploy da aplicação em um ambiente de nuvem.

# 2. Modelagem do Domínio (Entidades e Relacionamentos)
Esta seção detalha as entidades centrais da aplicação.

## 2.1. User
Representa um usuário do sistema.

- **id (Long):** Chave primária (PK).
- **name (String):** Nome completo.
- **email (String):** E-mail de login. (Restrição: Único).
- **password (String):** Senha criptografada (Hashed).
- **cpf (String):** CPF do usuário. (Deve ser armazenado criptografado).
- **role (Enum: USER, ADMIN):** Nível de acesso.
- **createdAt (LocalDateTime):** Timestamp de criação.
- **updatedAt (LocalDateTime):** Timestamp da última atualização.

## 2.2. Asset
Representa um ativo financeiro que pode ser negociado.

- **id (Long):** PK.
- **ticker (String):** Código de negociação do ativo (ex: "PETR4"). (Restrição: Único).
- **companyName (String):** Nome da empresa/fundo.
- **type (Enum: STOCK, FII):** Tipo do ativo.

## 2.3. Portfolio (Carteira)
O container que agrega as posições e o saldo de um usuário.

- **id (Long):** PK.
- **user (User):** Dono da carteira. (Relacionamento: @OneToOne).
- **balance (BigDecimal):** Saldo em dinheiro disponível para negociações.

## 2.4. Position (Posição)
Representa a custódia consolidada de um Asset dentro de um Portfolio.

- **id (Long):** PK.
- **portfolio (Portfolio):** Carteira à qual a posição pertence. (Relacionamento: @ManyToOne).
- **asset (Asset):** O ativo em custódia. (Relacionamento: @ManyToOne).
- **quantity (Integer):** Quantidade de unidades do ativo.
- **averagePrice (BigDecimal):** Preço médio de compra do ativo para esta posição.

## 2.5. Order (Ordem)
Representa uma intenção de compra ou venda.

- **id (Long):** PK.
- **portfolio (Portfolio):** Carteira que emitiu a ordem. (Relacionamento: @ManyToOne).
- **asset (Asset):** Ativo a ser negociado. (Relacionamento: @ManyToOne).
- **quantity (Integer):** Quantidade a ser negociada.
- **price (BigDecimal):** Preço definido para ordens do tipo LIMIT.
- **type (Enum: BUY, SELL).**
- **orderType (Enum: MARKET, LIMIT).**
- **status (Enum: PENDING, EXECUTED, CANCELLED).**
- **createdAt (LocalDateTime):** Timestamp de criação da ordem.

## 2.6. Transaction (Transação)
Representa o registro histórico de uma Order que foi executada com sucesso.

- **id (Long):** PK.
- **portfolio (Portfolio):** Carteira envolvida. (Relacionamento: @ManyToOne).
- **asset (Asset):** Ativo transacionado. (Relacionamento: @ManyToOne).
- **order (Order):** Ordem que originou a transação. (Relacionamento: @ManyToOne).
- **quantity (Integer):** Quantidade efetivamente transacionada.
- **price (BigDecimal):** Preço unitário no momento da execução.
- **type (Enum: BUY, SELL).**
- **timestamp (LocalDateTime):** Momento exato da execução.

# 3. Arquitetura de Software
**Padrão Arquitetural:** Arquitetura Hexagonal (Ports & Adapters).

**Justificativa:** Promover o desacoplamento entre a lógica de negócio e as tecnologias de infraestrutura (API, banco de dados, clientes HTTP), facilitando a testabilidade e a manutenção.

**Estrutura de Pacotes (Proposta):**
- `com.seuprojeto.domain`: Contém as entidades e a lógica de negócio pura.
- `com.seuprojeto.application`: Orquestra os casos de uso (services) e define as "Ports" (interfaces).
- `com.seuprojeto.infrastructure`: Implementa os "Adapters" (controllers REST, repositórios JPA, clientes de API externa, etc.).

# 4. Stack de Tecnologia
- **Linguagem/Framework:** Java 17+ / Spring Boot 3+
- **Banco de Dados:**
  - Desenvolvimento/Testes: H2 (em memória).
  - Produção: PostgreSQL.
- **Persistência:** Spring Data JPA / Hibernate.
- **Segurança:** Spring Security (autenticação via JWT).
- **Documentação da API:** Springdoc OpenAPI (Swagger).
- **Build Tool:** Maven ou Gradle.

# 5. Considerações de Segurança
- **Dados Pessoais (PII):** Campos como cpf devem ser obrigatoriamente armazenados de forma criptografada no banco de dados.
  - *Implementação Proposta:* Utilizar um JPA @AttributeConverter para criptografia/decriptografia transparente.
- **Senhas:** Devem ser armazenadas utilizando um algoritmo de hashing forte (ex: BCrypt).
- **Comunicação:** A API deve ser exposta exclusivamente via HTTPS em produção (TLS).
