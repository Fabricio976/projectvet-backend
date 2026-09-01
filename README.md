API - Sistema de Cadastro de Animais para Hospital Veterinário
Esta API foi desenvolvida para gerenciar o cadastro de animais em um hospital veterinário/Petshop, incluindo funcionalidades de autenticação, 
autorização e recuperação de senha. O sistema diferencia clientes (tutores de animais) de funcionários (usuários internos), garantindo controle de acesso adequado a cada perfil.

Funcionalidades

Autenticação e Autorização com JWT
  - Login com geração de token JWT
  - Validação automática de token nas rotas protegidas
  - Diferenciação entre clientes e funcionários com base em permissões

Recuperação de Senha
  - Envio de código de verificação por e-mail
  - Geração e validação de token de recuperação de senha
  - Atualização da senha do usuário

CRUD de Animais
  - Cadastro de animais vinculados a clientes
  - Listagem com filtros (ex: por cliente, espécie)
  - Edição e exclusão de dados dos animais
  - Cada animal possui informações como nome, idade, espécie, raça e data de registro
  - Upload de imagem do animal

Tecnologias Utilizadas
  - Back-end: Java com Spring Boot
  - Segurança: Spring Security com JWT
  - Persistência: Spring Data JPA
  - Banco de Dados: PostgreSQL
  - Envio de E-mails: JavaMailSender
  - Validação: Bean Validation (javax.validation)
  - Padrão de projeto: MVC

Testes Automatizados
  - Testes de endpoints REST usando Rest Assured
  - Cobertura de cenários de sucesso, falhas de autenticação, autorização e validações

Pré-requisitos
  - Java 17+
  - Maven
  - PostgreSQL
  - Conta de e-mail SMTP configurada (para envio de código de recuperação)
  - Testes de API: Rest Assured + JUnit

Futuras melhorias
  - Integração com módulo de agendamento de consultas
  - Integração de um e-commerce (produtos para os animais)
