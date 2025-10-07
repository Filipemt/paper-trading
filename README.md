# Especificação Técnica: Simulador de Carteira de Ações v3.0 (Atualizado)
---

## 1. Visão Geral
Construir uma API RESTful para um sistema de *Paper Trading*, permitindo que usuários se cadastrem, autentiquem, gerenciem uma carteira virtual e simulem operações de mercado.  
O foco da **v1.0** é a funcionalidade principal do usuário.

---

## 2. Modelo de Dados (@Entity)

A camada de domínio é composta pelas seguintes entidades, implementadas com JPA.

| Entidade   | Atributos Chave                             | Relacionamentos Principais                                                                 | Notas                                                                 |
|------------|---------------------------------------------|--------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| **User**   | id, name, email, password, cpf, timestamps  | `@OneToOne(mappedBy="user")` com **Portfolio**                                             | Contém a identidade do usuário. `email` e `cpf` são únicos. Implementa `UserDetails`. |
| **Portfolio** | id, balance                              | `@OneToOne` com **User**, `@OneToMany` com **Position**, **Order**, **Transaction**        | O "container" dos ativos e do saldo de um usuário.                   |
| **Asset**  | id, ticker, companyName, type               | Nenhum (relações unidirecionais a partir de outras entidades)                              | Catálogo de ativos negociáveis. `ticker` é único.                    |
| **Position** | id, quantity, averagePrice                | `@ManyToOne` com **Portfolio**, `@ManyToOne` com **Asset**                                | Registro consolidado da posse de um **Asset**.                       |
| **Order**  | id, quantity, price, type, marketOrderType, status, timestamps | `@ManyToOne` com **Portfolio**, `@ManyToOne` com **Asset**, `@OneToOne(mappedBy="order")` com **Transaction** | Representa uma intenção de compra ou venda. |
| **Transaction** | id, quantity, price, timestamp         | `@ManyToOne` com **Portfolio**, `@ManyToOne` com **Asset**, `@OneToOne` com **Order**      | Registro histórico e imutável de uma ordem executada.                |

---

## 3. Arquitetura da Camada de Aplicação

Adotamos o padrão de **Casos de Uso (Use Cases)** para definir os contratos da lógica de negócio, com dois princípios:

1. **Interfaces Granulares**: Uma interface `UseCase` para cada ação de negócio, especialmente para **Comandos** (operações que alteram dados).
2. **Implementações Coesas**: Uma única classe `Service` por conceito de negócio (**Auth, Asset, Portfolio, Order**), que implementa as várias interfaces `UseCase` relacionadas.

---

## 4. Casos de Uso e Regras de Negócio (UC)

### UC-01: Registro de Novo Usuário (`RegisterUserUseCase`)
**Descrição:** Um novo usuário deve poder se registrar.

**Regras de Negócio:**
- **[RN-01]** `email` e `cpf` devem ser únicos. Se duplicado, lançar exceção (`UserAlreadyExistsException`, `CpfAlreadyExistsException`).
- **[RN-02]** A senha deve ser armazenada com hash `BCrypt`.
- **[RN-03]** Um `Portfolio` com saldo inicial de **100.000,00** deve ser criado em cascata.
- **[RN-04]** Após o registro, um token **JWT** deve ser gerado e retornado, autenticando o usuário automaticamente.

---

### UC-02: Autenticação de Usuário (`AuthenticateUserUseCase`)
**Descrição:** Um usuário registrado deve poder fazer login.

**Regras de Negócio:**
- **[RN-05]** As credenciais devem ser validadas pelo `AuthenticationManager` do **Spring Security**.
- **[RN-06]** Em caso de falha, retornar **401 Unauthorized** com mensagem genérica.
- **[RN-07]** Em caso de sucesso, gerar e retornar novo token **JWT**.

---

### UC-03: Listar Ativos Disponíveis (`ListAssetsUseCase`)
**Descrição:** Um usuário autenticado deve poder ver a lista de ativos disponíveis para negociação.

**Regras de Negócio:**
- **[RN-08]** Buscar os **Assets** do banco de dados de forma **paginada**.
- **[RN-09]** Para cada Asset, o preço atualizado deve ser buscado via `PriceProviderPort`.
- **[RN-10]** A resposta deve ser um `Page<AssetResponseDTO>` combinando os dados.

---

### UC-04: Buscar Ativo por Ticker (`FindAssetByTickerUseCase`)
**Descrição:** Um usuário autenticado deve poder ver os detalhes de um ativo específico.

**Regras de Negócio:**
- **[RN-11]** Se o ticker não for encontrado, lançar `AssetNotFoundException`.
- **[RN-12]** A resposta deve ser um `AssetResponseDTO` enriquecido com o preço atual.

---

### UC-05: Consultar Carteira (`ViewPortfolioUseCase`)
**Descrição:** Um usuário autenticado deve poder visualizar o estado completo de sua carteira.

**Regras de Negócio:**
- **[RN-13]** Buscar o **Portfolio** do usuário autenticado via `SecurityContextHolder`.
- **[RN-14]** Para cada **Position**, buscar o preço atual via `PriceProviderPort`.
- **[RN-15]** Calcular o valor total de cada posição, o valor total em ativos e o patrimônio total.
- **[RN-16]** Retornar `PortfolioResponseDTO` contendo todos os cálculos e lista de `PositionResponseDTO`.

**Nota:** Os UCs de **Order** e **Transaction** serão detalhados no **BLOCO 4**.

---

### UC-06: Criação de Ordem de Compra/Venda (`CreateOrderUseCase`)

**Descrição:**  
Um usuário autenticado deve poder submeter uma ordem de compra ou venda de um ativo.

### Fluxo Principal (Lógica do Serviço):
1. Receber dados da ordem via DTO (`ticker`, `quantity`, `type`, `orderType`, `price` se for LIMIT).
2. Validar os dados de entrada (`@Valid` no DTO).
3. Buscar o **User** autenticado do `SecurityContextHolder`.
4. Buscar o **Asset** pelo `ticker`. Se não existir, lançar `AssetNotFoundException`.
5. Buscar o **Portfolio** do usuário. Se não existir, lançar `PortfolioNotFoundException`.
6. **Se a ordem for de COMPRA (BUY):**  
   a. Obter o preço atual do ativo via `PriceProviderPort`.  
   b. Calcular o custo total (`preço * quantidade`).  
   c. Validar se o `balance` do Portfolio é suficiente. Se não, lançar `InsufficientFundsException`.
7. **Se a ordem for de VENDA (SELL):**  
   a. Buscar a **Position** do usuário para aquele **Asset**.  
   b. Validar se a `quantity` na Position é suficiente. Se não, lançar `InsufficientPositionException`.
8. Criar a entidade **Order** com status `PENDING` (para LIMIT) ou `EXECUTED` (para MARKET).
9. Salvar a **Order**.
10. **Se a ordem for EXECUTED:**  
    a. Criar e salvar a **Transaction** correspondente, ligada à Order.  
    b. Chamar o **PortfolioService** para executar a lógica de atualização da carteira (debitar/creditar `balance` e atualizar a **Position**).

### Regras de Negócio e Validações Adicionais:
- **[RN-17]** A `quantity` da ordem deve ser um inteiro positivo.
- **[RN-18]** O custo de uma ordem de compra não pode ser maior que o `balance` do Portfolio.
- **[RN-19]** A quantidade de uma ordem de venda não pode ser maior que a `quantity` da Position correspondente.
- **[RN-20]** A atualização do `balance` e da `Position` após uma transação deve ser matematicamente precisa (ex: recálculo do preço médio na compra).
- **[RN-21]** Toda a operação de execução de uma ordem de mercado deve ser atômica (`@Transactional`).

---

### UC-07: Cancelamento de Ordem (`CancelOrderUseCase`)

**Descrição:**  
Um usuário autenticado deve poder cancelar uma ordem que ainda não foi executada.

### Fluxo Principal (Lógica do Serviço):
1. Receber o `orderId` e o **User** autenticado.
2. Buscar a **Order** pelo id e pelo **Portfolio** do usuário (garantia de segurança).
    - Se não encontrar, lançar `OrderNotFoundException`.
3. Verificar se o `status` da ordem é `PENDING`.
4. Se for, alterar o `status` para `CANCELLED`.
5. Salvar a **Order** atualizada.

### Regras de Negócio e Validações Adicionais:
- **[RN-22]** Apenas ordens com `status = PENDING` podem ser canceladas.
    - Se o status for outro, lançar `OrderCannotBeCancelledException`.

---

### UC-08: Listar Ordens do Usuário (`ListOrdersUseCase`)

**Descrição:**  
Um usuário autenticado deve poder ver seu histórico de ordens.

### Regras de Negócio:
- **[RN-23]** O serviço deve buscar todas as **Orders** associadas ao **Portfolio** do usuário autenticado.
- **[RN-24]** A resposta deve ser **paginada** e permitir filtros (ex: por `status`, `type`, período).

---

### UC-09: Listar Transações do Usuário (`ListTransactionsUseCase`)

**Descrição:**  
Um usuário autenticado deve poder ver seu extrato de transações executadas.

### Regras de Negócio:
- **[RN-25]** O serviço deve buscar todas as **Transactions** associadas ao **Portfolio** do usuário autenticado.
- **[RN-26]** A resposta deve ser **paginada** e ordenada da mais recente para a mais antiga.

---
