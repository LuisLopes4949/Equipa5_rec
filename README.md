# Equipa5_rec
Projeto de Laboratório de Programação (Recurso 2025/2026)

# API Rest - Gestor de Despesas Pessoais

Este projeto é o **Backend (Servidor)** da aplicação de gestão de despesas. Foi desenvolvido em **Java (Spring Boot)** e serve como ponto central de lógica e acesso à base de dados para os clientes (Consola e JavaFX).

## Tecnologias Usadas
* **Java 21**
* **Spring Boot 3** (Web, Data JPA)
* **MySQL Database** (Persistência de dados)
* **Maven** (Gestão de dependências)

---

## Funcionalidades Principais
1.  **Gestão de Utilizadores:** Registo de conta e Login.
2.  **Gestão de Movimentos:**
    * Registo de **Despesas** (Gastos) e **Receitas** (Ganhos).
    * Suporte a **Métodos de Pagamento** (Dinheiro, MBWay, Cartão, etc.).
    * Edição e Remoção.
3.  **Categorias Personalizadas:** Cada utilizador tem as suas próprias categorias.
4.  **Filtros Avançados:** Por Ano, por Categoria e por Intervalo de Valores.
5.  **Segurança:**
    * Validação de e-mail único.
    * Proteção contra uso de categorias de outros utilizadores.

---

## Configuração da Base de Dados

Antes de correr a aplicação, certifique-se que tem o MySQL a correr e crie a base de dados:

```sql
CREATE DATABASE despesasPessoais;
