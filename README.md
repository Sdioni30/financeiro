# 📊 API Financeira Corporativa e Pessoal

API desenvolvida em Java.
Gerencia finanças de pequenos negócios.
Gerencia finanças pessoais separadamente.
Sistema escalável.

## 🚀 Tecnologias Utilizadas
- **Java**
- **Spring Boot**
- **MySQL** (Produção)
- **H2 Database** (Testes Isolados)
- **Apache POI** (Relatórios Excel)
- **Swagger / OpenAPI** (Documentação)

## ⚙️ Arquitetura e Boas Práticas
- Padrão **Command** implementado.
- Princípio de Responsabilidade Única.
- Injeção de dependência por construtor.
- Testes automatizados configurados.

## 🎯 Principais Funcionalidades
- Cadastro unificado de transações.
- Separação por categoria (PESSOAL ou PROFISSIONAL).
- Exportação de relatórios em `.xlsx`.

## 🛠️ Como Executar o Projeto

1. Clone este repositório:
   `git clone https://github.com/Sdioni30/financeiro.git`

2. Configure o banco de dados:
   Crie um banco MySQL chamado `db_financeiro`.

3. Configure a variável de ambiente:
   Defina `DB_PASSWORD` com sua senha.

4. Rode o projeto:
   Execute a classe `FinanceiroApplication`.

5. Acesse a documentação Swagger:
   Abra `http://localhost:8080/swagger-ui.html`.

---
*Desenvolvido por Dioni.*