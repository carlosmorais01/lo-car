# Version Description Document (VDD)

**Projeto:** Lo-car (Sistema de Locação de Veículos)

**Autores:** Carlos Henrique Alves de Morais · José Borges da Cruz · Vinícius Pereira Espíndola

**Data de emissão:** 18/06/2026

---

## Sumário

1. [Identificação da Baseline](#1-identificação-da-baseline)
2. [Inventário de Modificações](#2-inventário-de-modificações)
   - [2.1 Contribuidores](#21-contribuidores)
   - [2.2 Matriz de Rastreabilidade](#22-matriz-de-rastreabilidade)
   - [2.3 Solicitações de mudança via Pull Requests](#23-solicitações-de-mudança-via-pull-requests)
3. [Composição do Software / SBOM](#3-composição-do-software--sbom)
   - [3.1 Resumo de componentes](#31-resumo-de-componentes)
4. [Problemas Conhecidos e Limitações](#4-problemas-conhecidos-e-limitações)
5. [Hashes Criptográficos dos Artefatos da Baseline](#5-hashes-criptográficos-dos-artefatos-da-baseline)

---

## 1. Identificação da Baseline

| Atributo | Valor |
|---|---|
| Identificador único | `lo-car-v1.0.0` |
| Tag Git | `v1.0.0` |
| Commit HEAD da baseline | `a952f16` |
| Data do último commit | 2026-05-27 |
| Repositório | lo-car |
| Ramo de origem | main (integração via Pull Requests) |
| Serial number do SBOM | `urn:uuid:cb0ff3e5-a65c-474a-af77-5aa39f2a75bc` |
| Ferramenta de inventário | Syft v1.45.1 – CycloneDX 1.6 |

### Descrição da baseline

A baseline `lo-car-v1.0.0` representa a primeira versão candidata à release do sistema de locação de veículos **lo-car**, desenvolvido como Trabalho 1 da disciplina de Gerência de Configuração de Software (2026/1). O sistema é uma aplicação desktop Java com interface gráfica Swing/FlatLaf, persistência via serialização binária (`.dat`) e suporte a dois perfis de usuário: Cliente e Funcionário.

O projeto não adota um gerenciador de build declarativo; as dependências são gerenciadas manualmente via arquivos `.jar` versionados no diretório `lib/` e configuradas no módulo IntelliJ IDEA (`lo-car.iml`).

---

## 2. Inventário de Modificações

### 2.1 Contribuidores

| Autor | Commits |
|---|---|
| carlosmorais01 (Carlos Henrique Alves Morais) | ~80 |
| Jose-borges47 | ~35 |
| Vinícius Espíndola | ~22 |

### 2.2 Matriz de Rastreabilidade

A tabela abaixo consolida os commits agrupados por funcionalidade/área de mudança, evidenciando as solicitações de mudança (SMs) atendidas nesta versão. Por não haver sistema formal de issue tracking referenciado nas mensagens de commit, as SMs são identificadas por categoria funcional inferida do histórico.

| ID SM | Categoria | Commits representativos | Descrição da mudança |
|---|---|---|---|
| SM-01 | Estrutura inicial do projeto | `c829862`, `cd9f124`, `49d36c1`, `7186b98` | Criação do repositório, entidades e enums iniciais (`Veiculo`, `Cliente`, `Funcionario`, `Locacao`, `Endereco`, `Pessoa`), README inicial. |
| SM-02 | Reestruturação de pacotes | `003455c`, `07e28e1`, `b78b45d` | Reorganização do namespace para `br.com.locar`, ajuste de importações de enums e consolidação do código por pacote. |
| SM-03 | Autenticação e tela de login | `617260b`, `c67127c`, `251e806`, `6644d3b` | Implementação de `AuthController`, `PasswordHasher` e `LoginScreen` com suporte a login de Cliente e Funcionário, redirecionamento pós-login. |
| SM-04 | Interface gráfica principal | `f4ae3b0`, `dc1285d`, `fa9517d` | Adição de `MainScreen` com carrossel de veículos, suporte a múltiplos tipos de usuário e exibição condicional de botões. |
| SM-05 | Listagem e filtro de veículos | `368242b`, `3316b4b`, `a707984`, `b0617a1`, `16b3ebc` | Implementação de `VehicleListScreen` com filtros por cor, busca case-insensitive, suporte a múltiplos usuários e adição de opção `TODAS` no enum `Cor`. |
| SM-06 | Cadastro de veículos | `b437afb`, `a0a56f3` | Adição de `VehicleRegistrationScreen` com validações, seleção de tipo de veículo, upload de imagem e persistência associada à placa. |
| SM-07 | Detalhes do veículo e locação | `c622e6d`, `a698de1`, `25ff56b`, `06059c0` | Implementação de `VehicleDetailScreen`, cálculo de valor total via `calcularValorTotal`, QR Code estático para simulação de pagamento PIX, e métodos de devolução (`encontrarLocacaoAtiva`, `registrarDevolucao`). |
| SM-08 | Perfil de usuário | `7ba1075`, `37d887e`, `82ebeca`, `74011e7` | Adição de `UserProfileScreen`, suporte a foto de perfil (`caminhoFoto`), seleção e pré-visualização de imagem no registro. |
| SM-09 | Tela de cadastro de clientes | `07cb695`, `f7286e6`, `099ea80` | Implementação de `RegisterScreen` com campos de registro, validações de CPF e integração com `AuthController`. |
| SM-10 | Componentes visuais (carrossel/cards) | `1e6f6c9`, `ea5314f`, `1c6f6c9` | Adição de `CarCardPanel` e `CarrosselPanel` com navegação, imagens e informações de veículos. |
| SM-11 | Refatoração de controladores | `5f9c2ec`, `6325569`, `b9935e9`, `d2f31fd`, `a67c845` | Refatoração de `LocacaoController`, `VeiculoController` e `AuthController`: melhoria de validações, assinaturas de métodos, lógica de persistência e substituição de mensagens de erro por exceções específicas. |
| SM-12 | Serialização de entidades | `11b6074`, `efdee81`, `71f1d65` | Marcação de `Endereco`, `Pessoa`, `Funcionario` e `Cliente` como `Serializable`, adição de `serialVersionUID`. |
| SM-13 | Geração de dados de exemplo (dump) | `211340c`, `922acee`, `3936e0e`, `6942ecd` | Implementação e refatoração de `DumpGenerator` para geração de dumps de clientes, funcionários, veículos e locações; remoção de lógica redundante. |
| SM-14 | Documentação JavaDoc | `c27f2e5`, `ca5a033`, `f542ab6` e demais commits de 2025-06-22 | Adição de documentação detalhada via JavaDoc em todos os controladores, telas, utilitários e componentes visuais. Geração de Javadoc HTML em `doc/`. |
| SM-15 | Infraestrutura de testes | `d08ec71`, `6ee0902`, `ddecfb5`, `3316b4b`, `6780476` | Adição de testes unitários para entidades (`Caminhao`, `Carro`, `Moto`, `Cliente`, `Funcionario`, `Locacao`, `Veiculo`, `Endereco`, `Pessoa`) e controladores (`AuthController`, `LocacaoController`, `VeiculoController`); inclusão de dependências de teste (`byte-buddy`, `mockito-core`, `objenesis`) na `lib/`. |
| SM-16 | Correções de bugs | `15b1c7a`, `39f9e86`, `4d18ea7`, `7fea47a`, `6c6a18b`, `6d3a157` | Correção de referência de variável em `RegisterScreen` (`backButtonm` → `backButton`), retorno de null em exceção de autenticação, caminho do arquivo de clientes em `AuthController`, instanciação de `Font` em `VehicleListScreen`. |
| SM-17 | Assets e recursos visuais | `47ebad6`, `714aa28`, `9ca6f90`, `f8bd0fc`, `0d01e48` | Adição de imagens de veículos, ícones de interface, fotos de perfil de exemplo e fontes Roboto Slab com documentação e licença. |
| SM-18 | Integração final e merge | `9a5767e` (PR #24), `790a49a` (PR #11) | Merges de PRs consolidando funcionalidades desenvolvidas em branches paralelas. |

### 2.3 Solicitações de mudança via Pull Requests

| PR | Commit de merge | Descrição |
|---|---|---|
| PR #11 | `790a49a` | Integração da branch `carlos` com `homol` – consolidação da interface gráfica principal |
| PR #24 | `9a5767e` | Merge final consolidando correções e ajustes de serialização |

> **Nota:** O projeto adotou fluxo de integração contínua via PRs direto na `main`, sem branch de release dedicada. A tag `v1.0.0` foi aplicada retroativamente ao commit `a952f16` para identificação formal da baseline.

---

## 3. Composição do Software / SBOM

O inventário completo de componentes de terceiros desta baseline foi gerado pela ferramenta **Syft v1.45.1** no formato **CycloneDX 1.6**, arquivo `sbom.json` (Serial: `urn:uuid:cb0ff3e5-a65c-474a-af77-5aa39f2a75bc`).

### 3.1 Resumo de componentes

| # | Componente | Versão | Licença | Uso |
|---|---|---|---|---|
| 1 | apiguardian-api | 1.1.2 | Apache-2.0 | Suporte à API JUnit |
| 2 | byte-buddy | 1.12.19 | Apache-2.0 | Geração de bytecode (Mockito) |
| 3 | byte-buddy-agent | 1.12.19 | Apache-2.0 | Agente de instrumentação (Mockito) |
| 4 | flatlaf | 3.5.1 | Apache-2.0 | Tema visual da interface gráfica |
| 5 | hamcrest-core | 1.3 | BSD-3-Clause | Asserções em testes |
| 6 | junit | 4.13.1 | EPL-1.0 | Framework de testes (legado) |
| 7 | junit-jupiter | 5.8.1 | EPL-2.0 | Agregador JUnit 5 |
| 8 | junit-jupiter-api | 5.8.1 | EPL-2.0 | API de testes JUnit 5 |
| 9 | junit-jupiter-engine | 5.8.1 | EPL-2.0 | Engine de execução JUnit 5 |
| 10 | junit-jupiter-params | 5.8.1 | Apache-2.0 / EPL-2.0 | Testes parametrizados JUnit 5 |
| 11 | junit-platform-commons | 1.8.1 | EPL-2.0 | Utilitários comuns JUnit Platform |
| 12 | junit-platform-engine | 1.8.1 | EPL-2.0 | Engine da plataforma JUnit |
| 13 | mockito-core | 4.10.0 | MIT | Framework de mocking para testes |
| 14 | objenesis | 3.3 | Apache-2.0 | Instanciação de objetos (Mockito) |
| 15 | opentest4j | 1.2.0 | Apache-2.0 | Protocolo de falha em testes |

**Total: 15 componentes** | Todas as dependências de escopo: teste + UI runtime

O SBOM completo com hashes SHA-1 e SHA-256 de cada artefato está disponível em `sbom.json`, presente no repositório remoto do projeto.

---

## 4. Problemas Conhecidos e Limitações

| # | Descrição | Impacto | Justificativa para permanência na v1.0.0 |
|---|---|---|---|
| L-01 | Ausência de gerenciador de dependências declarativo. O projeto gerencia dependências como JARs versionados manualmente em `lib/`, sem `pom.xml` ou `build.gradle`. Não há lockfile nem grafo de dependências transitivas resolvido. | Substituição manual de um JAR não é rastreada automaticamente. | Decisão de arquitetura do grupo para a disciplina de POO; a adoção de Maven/Gradle não era requisito do Trabalho 1. |
| L-02 | JUnit 4 e JUnit 5 coexistindo. A `lib/` contém `junit-4.13.1.jar` e os módulos do JUnit Jupiter 5.8.1 simultaneamente. | Risco de comportamento inconsistente dependendo do runner utilizado; `hamcrest-core-1.3` é dependência do JUnit 4 e pode conflitar com versões internas do JUnit 5. | Os testes foram escritos majoritariamente em JUnit 5; o JAR do JUnit 4 foi mantido por compatibilidade retroativa com testes legados já existentes. |
| L-03 | Persistência via serialização binária sem versionamento de esquema. Os dados são armazenados em arquivos `.dat` via `ObjectOutputStream`. | Alterações em atributos de entidades (`serialVersionUID` ausente em algumas classes ou alterado) podem tornar arquivos `.dat` existentes ilegíveis. | Escopo do projeto é acadêmico/local; não há ambiente de produção com dados persistidos entre versões. |
| L-04 | Ausência de rastreamento de dependências transitivas. Componentes como `byte-buddy-agent` e `objenesis` são transitivas do `mockito-core`, mas constam no inventário como itens de primeiro nível sem relação documentada. | O SBOM não reflete o grafo real de dependências, apenas a lista plana de JARs presentes. | Consequência direta da ausência de gerenciador de build (L-01); não há como inferir a hierarquia sem um arquivo de manifesto. |
| L-05 | Metadado de licença não padronizado em `opentest4j`. O artefato declara a licença via campo de texto livre em vez do identificador SPDX. | Ferramentas de compliance de licença podem não reconhecer automaticamente a licença desse componente. | Inconsistência originada no próprio artefato publicado pelo autor; fora do controle do projeto. |

---

## 5. Hashes Criptográficos dos Artefatos da Baseline

| Arquivo | SHA-256 |
|---|---|
| `sbom.json` | `61dc218e67f07c6e0a8d88a76b5e9657b2446712f3299e0aff2c81c2f932f1e4` |
| `Version Description Document (VDD).pdf` | `9c89118c961eec0bc533ec0b88fdc36a5800a3f9b0bdf1d1e85d0c5d8c046f7a` |
| `Relatório de Auditoria de Configuração.pdf` | `1c91af001d901e70e32636ce75e968c1b389447dae1773c093da2dd16ae886dd` |
