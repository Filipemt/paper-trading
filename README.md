# 🧾 Paper Trading API - Simulador de Investimentos

## 📖 Sumário
- [1. Visão Geral](#1-visão-geral)
- [2. Arquitetura Hexagonal e Design](#2-arquitetura-hexagonal-e-design)
- [3. Stack Tecnológica](#3-stack-tecnológica)
- [4. Funcionalidades (v1.0)](#4-funcionalidades-v10)
- [5. Como Executar Localmente](#5-como-executar-localmente)
- [6. Documentação da API (Swagger)](#6-documentação-da-api-swagger)
- [7. Roadmap (v2.0)](#7-roadmap-v20)

---

## 1. Visão Geral

Este projeto é uma **API RESTful completa** para uma plataforma de **Paper Trading (simulação de investimentos)**, desenvolvida com **Java e Spring Boot**.

O objetivo foi ir além de um CRUD básico, focando em uma **arquitetura robusta e limpa**, capaz de lidar com **regras de negócio complexas**, **autenticação**, **transações** e **integração com serviços externos**.

A API permite que usuários:
- Se cadastrem e gerenciem um portfólio virtual (com saldo inicial)
- Simulem **compra e venda de ativos** (Ações e FIIs) com base em cotações do mercado atual
- Consultem o **histórico de operações**

> Este repositório contém exclusivamente o **backend** da aplicação.

---

## 2. Arquitetura Hexagonal e Design

A arquitetura é baseada nos princípios da **Arquitetura Hexagonal (Portas e Adaptadores)**.  
Isso garante um **núcleo de negócio limpo, desacoplado e altamente testável**, isolado de detalhes de infraestrutura.

### 🧩 Estrutura de Pacotes

- **domain:**  
  Contém as entidades (`@Entity`), exceções de negócio e interfaces (Ports) que definem os contratos da aplicação  
  *(ex: `PortfolioRepositoryPort`, `PriceProviderPort`)*

- **application:**  
  Contém os **UseCases** (interfaces) e **Services** (implementações) que orquestram a lógica de negócio, dependendo apenas das Ports do domínio.

- **infrastructure:**  
  Contém os **Adapters** que implementam as Ports.  
  Aqui estão os **Controllers**, a **configuração de segurança (Spring Security)**, os **clientes externos (OpenFeign)**, os **repositórios (Spring Data JPA)** e o **Flyway**.

---

## 3. Stack Tecnológica

| Categoria | Tecnologia | Justificativa |
|------------|-------------|---------------|
| **Framework Base** | Spring Boot 3 / Java 17 | Ecossistema robusto, injeção de dependência e auto-configuração. |
| **Segurança** | Spring Security 6 / JWT | Padrão de mercado para autenticação e autorização de APIs stateless. |
| **Persistência** | Spring Data JPA / Hibernate | ORM padrão para abstração e produtividade no acesso a dados. |
| **Banco de Dados** | PostgreSQL (Produção) / H2 (Dev) | Banco robusto + memória para testes. |
| **Migrações** | Flyway | Versionamento confiável do schema do banco. |
| **Cliente HTTP** | Spring Cloud OpenFeign | Clientes REST declarativos e limpos. |
| **Caching** | Spring Cache / Caffeine | Otimização de performance em cotações externas. |
| **Validação** | Spring Validation / Custom Validators | Garantia da integridade dos dados de entrada (ex: `@CPF`). |
| **Documentação** | Springdoc (OpenAPI / Swagger) | Documentação da API interativa e auto-gerada. |
| **Utilitários** | Lombok | Redução de código boilerplate. |

---

## 4. Funcionalidades 

### 🔐 Autenticação e Usuário (`/auth`)
- **POST `/auth/register`** → Registro de novo usuário com criação de portfólio e saldo inicial.
- **POST `/auth/login`** → Autenticação e geração de token JWT.

### 📈 Mercado e Ativos (`/assets`)
- **GET `/assets`** → Lista todos os ativos (Ações e FIIs), com paginação e preços atualizados (via API externa + cache).
- **GET `/assets/{ticker}`** → Detalha um ativo específico com seu preço atual.

### 💼 Gestão de Carteira (`/portfolios`)
- **GET `/portfolios/me`** → Retorna o portfólio do usuário autenticado: saldo, valor total de ativos, patrimônio e posições com valores de mercado atualizados.

### 💸 Operações de Negociação (`/orders`)
- **POST `/orders`** → Criação de ordens de compra (BUY) e venda (SELL).
    - **MARKET:** Executadas imediatamente, atualizam balance e posições.
    - **LIMIT:** Validadas e salvas com status `PENDING`.
- **GET `/orders`** → Lista histórico de ordens com filtros e paginação.
- **DELETE `/orders/{id}`** → Cancela ordem `LIMIT` pendente.

### 📜 Histórico (`/transactions`)
- **GET `/transactions`** → Extrato de transações executadas, paginado e ordenado do mais recente para o mais antigo.

---

## 5. Como Executar Localmente

### ⚙️ Pré-requisitos
- **Java 17 (JDK)**
- **Apache Maven 3.8+**
- **Git**
- *(Opcional)* **PostgreSQL 14+** (para perfil de produção)
- *(Opcional)* **Chave de API da [Brapi.dev](https://brapi.dev)** (para o `PriceProvider` real)

---

### 📥 Clonar o Repositório
```bash
git clone https://github.com/seu-usuario/paper-trading.git
cd paper-trading
