# Rental Cars Spring API - Documentação Técnica

## Visão Geral
Este documento fornece informações técnicas abrangentes sobre a Rental Cars Spring API, uma aplicação Java Spring Boot projetada para gerenciar operações de aluguel de veículos. O sistema implementa uma API RESTful com padrão CQRS, princípios de design orientado a domínio e cobertura de testes abrangente.

## Visão Geral da Arquitetura

### Stack de Tecnologia
- **Framework**: Spring Boot 3.2.10
- **Linguagem**: Java 17
- **Banco de Dados**: H2 (em memória) com suporte PostgreSQL
- **ORM**: Spring Data JPA com Hibernate
- **Migração de Banco de Dados**: Liquibase
- **Segurança**: Spring Security com Servidor de Recursos OAuth2
- **Documentação**: OpenAPI 3.0 (Swagger)
- **Testes**: JUnit 5, Spring Boot Test, Spring Security Test
- **Ferramenta de Build**: Maven
- **Utilitários**: Lombok, Jackson

### Padrão de Arquitetura
- **CQRS (Command Query Responsibility Segregation)**: Modelos de leitura e escrita separados
- **Design Orientado a Domínio (DDD)**: Arquitetura limpa com design centrado em domínio
- **Padrão Repository**: Abstração de acesso a dados
- **Padrão Service Layer**: Encapsulamento de lógica de negócios

## Estrutura do Projeto

```
rental-cars-spring-api/
├── src/main/java/
│   └── com/challenge/rental_cars_spring_api/
│       ├── RentalCarsSpringApiApplication.java    # Classe principal da aplicação
│       ├── config/                                # Classes de configuração
│       │   └── SecurityConfig.java                 # Configuração Spring Security
│       ├── access/                                 # Controladores REST (Camada API)
│       │   ├── CarrosRestController.java          # Endpoints de gerenciamento de carros
│       │   └── AluguelRestController.java         # Endpoints de gerenciamento de aluguéis
│       ├── core/                                   # Camada de domínio
│       │   ├── domain/                            # Entidades de domínio
│       │   │   ├── Carro.java                     # Entidade Carro
│       │   │   ├── Cliente.java                   # Entidade Cliente
│       │   │   └── Aluguel.java                   # Entidade Aluguel
│       │   ├── queries/                           # Manipuladores de consulta (Leitura CQRS)
│       │   │   ├── ListarCarrosQuery.java
│       │   │   └── ListarAlugueisQuery.java
│       │   │   └── dtos/                        # Objetos de transferência de dados
│       │   └── usecases/                        # Lógica de negócios (Escrita CQRS)
│       │       └── ProcessarAluguelRtnService.java
│       └── infrastructure/                      # Camada de infraestrutura
│           └── repositories/                    # Repositórios JPA
├── src/main/resources/
│   ├── application.properties                  # Configuração da aplicação
│   ├── liquibase/                             # Scripts de migração de banco de dados
│   └── RentReport.rtn                         # Arquivo RTN de exemplo
└── src/test/                                  # Classes de teste
```

## Esquema do Banco de Dados

### Entidades e Relacionamentos

#### Carro (Carro)
- **Tabela**: `carro`
- **Chave Primária**: `id` (BIGINT, AUTO_INCREMENT)
- **Colunas**:
  - `modelo` (VARCHAR(50), NOT NULL) - Modelo do carro
  - `ano` (VARCHAR(4), NOT NULL) - Ano de fabricação
  - `qtd_passageiros` (BIGINT, NOT NULL) - Capacidade de passageiros
  - `km` (BIGINT, NOT NULL) - Quilometragem atual
  - `fabricante` (VARCHAR(50), NOT NULL) - Fabricante
  - `vlr_diaria` (DECIMAL(7,2), NOT NULL) - Taxa diária de aluguel

#### Cliente (Cliente)
- **Tabela**: `cliente`
- **Chave Primária**: `id` (BIGINT, AUTO_INCREMENT)
- **Colunas**:
  - `nome` (VARCHAR(100), NOT NULL) - Nome completo
  - `cpf` (VARCHAR(11), NOT NULL, UNIQUE) - CPF
  - `cnh` (VARCHAR(11), NOT NULL, UNIQUE) - CNH
  - `telefone` (VARCHAR(13), NOT NULL) - Número de telefone

#### Aluguel (Aluguel)
- **Tabela**: `aluguel`
- **Chave Primária**: `id` (BIGINT, AUTO_INCREMENT)
- **Chaves Estrangeiras**:
  - `carro_id` → `carro.id`
  - `cliente_id` → `cliente.id`
- **Colunas**:
  - `data_aluguel` (DATE, NOT NULL) - Data de início do aluguel
  - `data_devolucao` (DATE, NOT NULL) - Data de devolução
  - `valor` (DECIMAL(7,2), NOT NULL) - Valor total do aluguel
  - `pago` (BOOLEAN, NOT NULL) - Status de pagamento

## Endpoints da API

### URL Base
```
http://localhost:8080/api
```

### Endpoints de Gerenciamento de Carros

#### GET /api/carros
**Descrição**: Listar todos os carros da frota
**Resposta**: Lista de detalhes dos carros
```json
[
  {
    "id": 1,
    "modelo": "Gol",
    "ano": "2022",
    "qtdPassageiros": 5,
    "km": 15000,
    "fabricante": "Volkswagen",
    "vlrDiaria": 89.90
  }
]
```

### Endpoints de Gerenciamento de Aluguéis

#### POST /api/alugueis/processar
**Descrição**: Processar arquivo RTN e popular dados de aluguel
**Parâmetros**:
- `nomeArquivo` (parâmetro de consulta, opcional): Nome do arquivo RTN (padrão: "RentReport.rtn")
**Resposta**:
```json
{
  "sucesso": true,
  "registrosProcessados": 5,
  "registrosIgnorados": 0,
  "mensagem": "Processamento concluído com sucesso"
}
```

#### GET /api/alugueis/listar
**Descrição**: Listar todos os aluguéis com informações detalhadas
**Resposta**:
```json
{
  "alugueis": [
    {
      "dataAluguel": "2024-01-15",
      "modeloCarro": "Gol",
      "kmCarro": 15000,
      "nomeCliente": "João Silva",
      "telefoneCliente": "+55(11)98765-4321",
      "dataDevolucao": "2024-01-20",
      "valor": 449.50,
      "pago": "SIM"
    }
  ],
  "valorTotalNaoPago": 1247.30
}
```

## Formato do Arquivo RTN

### Estrutura
- **Tipo de Arquivo**: Arquivo de texto de largura fixa
- **Comprimento da Linha**: 20 caracteres por linha
- **Codificação**: ASCII/UTF-8

### Posições dos Campos
| Posição | Campo | Tipo | Descrição |
|----------|-------|------|-------------|
| 1-2 | carroId | Integer | ID do carro (chave estrangeira para tabela carro) |
| 3-4 | clienteId | Integer | ID do cliente (chave estrangeira para tabela cliente) |
| 5-12 | dataAluguel | Date (YYYYMMDD) | Data de início do aluguel |
| 13-20 | dataDevolucao | Date (YYYYMMDD) | Data de devolução |

### Exemplo
```
01022024011520240120
```
- ID do Carro: 01
- ID do Cliente: 02
- Data de Aluguel: 2024-01-15
- Data de Devolução: 2024-01-20

## Configuração

### Propriedades da Aplicação
```properties
# Configuração do Servidor
spring.application.name=rental-cars-spring-api
spring.mvc.servlet.path=/api

# Configuração do Banco de Dados
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Configuração do Liquibase
spring.liquibase.change-log=classpath:liquibase/changelog.xml
```

### Dependências Maven
Principais dependências incluem:
- Spring Boot Starter Web (API REST)
- Spring Boot Starter Data JPA (Acesso ao banco de dados)
- Spring Boot Starter Security (Autenticação/Autorização)
- H2 Database (Banco de dados em memória)
- Liquibase (Migrações de banco de dados)
- SpringDoc OpenAPI (Documentação da API)
- Lombok (Geração de código)

## Testes

### Estrutura de Testes
- **Testes Unitários**: Testes de entidades de domínio
- **Testes de Integração**: Testes de endpoints da API com banco de dados H2
- **Configuração de Teste**: Configuração de teste separada para testes de integração

### Classes de Teste
- `CarrosIntegrationTest`: Testa funcionalidade de listagem de carros
- `AlugueisListarIntegrationTest`: Testa funcionalidade de listagem de aluguéis
- `AlugueisProcessarIntegrationTest`: Testa processamento de arquivo RTN
- `H2ConfigIntegrationTest`: Testes de configuração do banco de dados

### Executando Testes
```bash
mvn test
```

## Configuração de Desenvolvimento

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, ou VS Code)

### Build e Execução
```bash
# Clone o repositório
git clone [repository-url]
cd rental-cars-spring-api

# Build o projeto
mvn clean install

# Execute a aplicação
mvn spring-boot:run
```

### Acessando a API
- **URL Base**: http://localhost:8080/api
- **Interface Swagger**: http://localhost:8080/api/swagger-ui.html
- **Console H2**: http://localhost:8080/api/h2-console

## Tratamento de Erros

### Tratamento Global de Exceções
- **Erros de Validação**: 400 Bad Request com mensagens de erro detalhadas
- **Recurso Não Encontrado**: 404 Not Found
- **Erros Internos do Servidor**: 500 Internal Server Error com detalhes do erro

### Registro de Logs
- **Framework**: SLF4J com Logback
- **Níveis**: INFO, WARN, ERROR
- **Configuração**: Configuração do Logback nas propriedades da aplicação

## Segurança

### Autenticação
- **Tipo**: Servidor de Recursos OAuth2
- **Formato do Token**: JWT (JSON Web Tokens)
- **Endpoints**: Todos os endpoints protegidos por padrão

### Autorização
- **Baseado em Funções**: Configurável via Spring Security
- **Configuração CORS**: Configurado para integração frontend

## Considerações de Performance

### Otimização do Banco de Dados
- **Índices**: Chaves primárias e chaves estrangeiras indexadas automaticamente
- **Pool de Conexões**: HikariCP para gerenciamento de conexões
- **Otimização de Consultas**: Otimização de consultas JPA

### Cache
- **Cache Nível 1**: Cache de primeiro nível do Hibernate habilitado
- **Cache de Consulta**: Configurável para dados frequentemente acessados

## Implantação

### Implantação em Contêiner
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/rental-cars-spring-api-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Variáveis de Ambiente
```bash
# Configuração do Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rental_cars
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password

# Configuração do Servidor
SERVER_PORT=8080
```

## Monitoramento e Observabilidade

### Verificações de Saúde
- **Endpoint**: GET /api/actuator/health
- **Saúde do Banco de Dados**: Verificação de conectividade do banco de dados
- **Espaço em Disco**: Monitoramento de espaço em disco disponível

### Métricas
- **Framework**: Spring Boot Actuator
- **Endpoint**: GET /api/actuator/metrics
- **Métricas Customizadas**: Métricas de negócios para operações de aluguel

## Solução de Problemas

### Problemas Comuns
1. **Conexão com Banco de Dados**: Verificar acessibilidade do console H2
2. **Conflitos de Porta**: Garantir que a porta 8080 esteja disponível
3. **Problemas de Memória**: Aumentar tamanho do heap JVM se necessário
4. **Processamento de Arquivo**: Verificar formato e codificação do arquivo RTN

### Modo Debug
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## Contribuindo

### Estilo de Código
- **Estilo de Código Java**: Seguir convenções Java padrão
- **Convenções de Nomenclatura**: CamelCase para variáveis e métodos
- **Documentação**: Javadoc para APIs públicas

### Processo de Pull Request
1. Faça fork do repositório
2. Crie um branch de feature
3. Implemente mudanças com testes
4. Garanta que todos os testes passem
5. Envie pull request com descrição detalhada

## Licença
Este projeto é software proprietário desenvolvido para fins de avaliação técnica.
