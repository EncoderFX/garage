# garage
O repositorio pode ser baixado em https://github.com/EncoderFX/garage
use o comando git clone https://github.com/EncoderFX/garage.git para baixar o repositorio

# Setup:
Use o comando "docker compose up" para executar o arquivo compose.yaml na raiz do repositorio garage
O arquivo compose.yaml executa a instalação do postgres e do adminer para acesso a base de dados

# JPA e Hibernate:
A configuração do hibernate create-drop ira executar a criação e deleção das tabelas sempre que o projeto for iniciado
    hibernate:
        ddl-auto: create-drop

# Database:
Na pasta resouces/json existe um arquivo data.json que contem as informações da garagem, elas serão carregadas e inseridas na base de dados sempre que o projeto for iniciado, retorna para o default.

Para facilitar o entendimento da base de dados foi eportado um arquivo database.txt que esta na raiz do projeto, esse arquivo contem toda a estrutura das tabelas e relacionamentos da base de dados criada

# Tabelas:
    garage: Contem informações do setores
    spot: Contem informações das vagas
    lease: Contem relacionamentos e informações em tempo real das locações das vagas
    event: Contem todos os eventos gerados durante o uso da garagem, entrada, parkeamento, saida e pagamentos
    payment: Contem informações dos pagamentos realizados

# Collection para testes garage_Insomnia_2025-06-08:
O arquivo garage_Insomnia_2025-06-08 esta na raiz do projeto, pode ser importado para o insominia e contem todos os endpoints para eecução dos testes na api/webhook

# GarageApplication.java:
Inicia o projeto spring boot

# Simulator.java:
Inicia um simulador que ira inserir uma massa se testes com entradas na garage e entradas nas vagas