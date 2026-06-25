# Relatório de Auditoria de Configuração

**Projeto:** Lo-car (Sistema de Locação de Veículos)

**Autores:** Carlos Henrique Alves de Morais · José Borges da Cruz · Vinícius Pereira Espíndola

**Data de emissão:** 18/06/2026

---

## Sumário

1. [Identificação da Baseline](#1-identificação-da-baseline)
2. [Itens de Configuração Identificados (Inspeção Manual)](#2-itens-de-configuração-identificados-inspeção-manual)
3. [Resultado do Escaneamento Automatizado (SBOM)](#3-resultado-do-escaneamento-automatizado-sbom)
   - [3.1 Metadados da ferramenta](#31-metadados-da-ferramenta)
   - [3.2 Licenças identificadas](#32-licenças-identificadas)
4. [Auditoria Física](#4-auditoria-física)
5. [Hashes de Integridade (SHA-256)](#5-hashes-de-integridade-sha-256)
6. [Conclusão e Observações](#6-conclusão-e-observações)

---

## 1. Identificação da Baseline

| Atributo | Valor |
|---|---|
| Repositório | lo-car |
| Versão/Tag auditada | `v1.0.0` |
| Forma de obtenção | `git checkout v1.0.0` |
| Ferramenta de inventário | Syft v1.45.1 (Anchore) |
| Formato de saída | CycloneDX 1.6 (JSON) |
| Serial number do BOM | `urn:uuid:cb0ff3e5-a65c-474a-af77-5aa39f2a75bc` |
| Timestamp de geração | 2026-06-18T13:09:00-03:00 |

A baseline `v1.0.0` foi escolhida como versão candidata à release. O projeto não utiliza ferramenta de gerência de build/dependências declarativa (não há `pom.xml` nem `build.gradle`); a estrutura do repositório indica gerenciamento via IntelliJ IDEA (`lo-car.iml`), com bibliotecas externas mantidas como arquivos `.jar` versionados manualmente no diretório `lib/`.

---

## 2. Itens de Configuração Identificados (Inspeção Manual)

Antes da execução da ferramenta automatizada, foi realizada inspeção visual do diretório `lib/`, que funciona como o "arquivo de configuração de dependências" informal do projeto. Foram identificados **15 Itens de Configuração (ICs)** abstratos, correspondentes a bibliotecas de terceiros:

| Biblioteca | Versão |
|---|---|
| apiguardian-api | 1.1.2 |
| byte-buddy | 1.12.19 |
| byte-buddy-agent | 1.12.19 |
| flatlaf | 3.5.1 |
| hamcrest-core | 1.3 |
| junit | 4.13.1 |
| junit-jupiter | 5.8.1 |
| junit-jupiter-api | 5.8.1 |
| junit-jupiter-engine | 5.8.1 |
| junit-jupiter-params | 5.8.1 |
| junit-platform-commons | 1.8.1 |
| junit-platform-engine | 1.8.1 |
| mockito-core | 4.10.0 |
| objenesis | 3.3 |
| opentest4j | 1.2.0 |

> **Observação relevante:** por não existir um gerenciador de build, não há um grafo de dependências transitivas declarado. A relação real entre os pacotes (por exemplo, `byte-buddy-agent` e `objenesis` sendo dependências transitivas do `mockito-core`) existe apenas implicitamente, sem rastreabilidade formal — risco identificado para a Gerência de Configuração do projeto.

---

## 3. Resultado do Escaneamento Automatizado (SBOM)

A ferramenta **Syft v1.45.1** foi executada na raiz do projeto. O inventário gerado (`sbom.json`) confirmou os 15 componentes identificados manualmente, sem divergências de nome ou versão. A ferramenta catalogou cada item em duas camadas complementares:

- **Componentes do tipo `library`:** identidade lógica do pacote (nome, versão, licença, identificadores PURL/CPE), totalizando 15 entradas.
- **Componentes do tipo `file`:** evidência física do artefato no disco, contendo caminho absoluto e hashes criptográficos (SHA-1 e SHA-256), também totalizando 15 entradas — uma para cada `.jar`.

Não foram encontrados artefatos adicionais fora de `lib/` (ex.: bibliotecas embutidas em `doc/`, como jQuery), indicando que o cataloger do Syft restringiu corretamente o escopo a dependências de runtime/teste reais.

### 3.1 Metadados da ferramenta

| Campo | Valor |
|---|---|
| Nome | syft |
| Autor | anchore |
| Versão | 1.45.1 |
| Formato do BOM | CycloneDX 1.6 |

### 3.2 Licenças identificadas

| Licença | Bibliotecas |
|---|---|
| Apache-2.0 | apiguardian-api, byte-buddy, byte-buddy-agent, flatlaf, junit-jupiter-params, objenesis, opentest4j* |
| BSD-3-Clause | hamcrest-core |
| EPL-1.0 | junit |
| EPL-2.0 | junit-jupiter, junit-jupiter-api, junit-jupiter-engine, junit-jupiter-params, junit-platform-commons, junit-platform-engine |
| MIT | mockito-core |

> \* `opentest4j` declara a licença via campo de texto livre (`"name"`) em vez do identificador SPDX padronizado (`"id"`) usado pelas demais bibliotecas — pequena inconsistência de metadados originada no próprio artefato, não na ferramenta de scan.

---

## 4. Auditoria Física

A auditoria física tem como objetivo verificar se os Itens de Configuração declarados correspondem, de fato, a artefatos físicos íntegros e presentes na baseline.

**Procedimento:** comparação entre os 15 ICs identificados na inspeção manual (Seção 2) e os 15 componentes do tipo `file` retornados pelo SBOM, validando presença e integridade via hash SHA-256.

| Verificação | Resultado |
|---|---|
| Quantidade de ICs declarados (manual) | 15 |
| Quantidade de artefatos físicos encontrados (Syft) | 15 |
| Itens declarados sem artefato físico correspondente | 0 |
| Artefatos físicos sem IC declarado correspondente | 0 |
| Hashes SHA-256 gerados com sucesso | 15/15 |

**Resultado da auditoria física: CONFORME.**

Todos os Itens de Configuração identificados manualmente possuem um artefato físico correspondente, íntegro e hasheado. Não foram encontradas divergências entre a baseline declarada e a baseline física (nenhum JAR ausente, nenhum JAR "fantasma" não documentado). A tabela completa de hashes está na Seção 5.

---

## 5. Hashes de Integridade (SHA-256)

| Arquivo | SHA-256 |
|---|---|
| `apiguardian-api-1.1.2.jar` | `b509448ac506d607319f182537f0b35d71007582ec741832a1f111e5b5b70b38` |
| `byte-buddy-1.12.19.jar` | `030704139e46f32c38d27060edee9e0676b0a0fff8a8be53461515154ba8a7be` |
| `byte-buddy-agent-1.12.19.jar` | `3a70240de7cdcde04e7c504c2327d7035b9c25ae0206881e3bf4e6798a273ed8` |
| `flatlaf-3.5.1.jar` | `62a044190aedfb4168ed04eb3fe480a167a875b1a847f2655aae708847dac659` |
| `hamcrest-core-1.3.jar` | `66fdef91e9739348df7a096aa384a5685f4e875584cce89386a7a47251c4d8e9` |
| `junit-4.13.1.jar` | `c30719db974d6452793fe191b3638a5777005485bae145924044530ffa5f6122` |
| `junit-jupiter-5.8.1.jar` | `8f1049ee24b34a10b60cd810048099f781c2658cde2181e831496ab30a982985` |
| `junit-jupiter-api-5.8.1.jar` | `ce3374a7efba605e2d2b69a3fef90134032bab3ecc3ed8579a4871b1c2c4729c` |
| `junit-jupiter-engine-5.8.1.jar` | `4689bc902255a19fe98277683ba3231c094d107c54c8d35f2b6f9c97d226418e` |
| `junit-jupiter-params-5.8.1.jar` | `389b8d13a8d8872fcbd4f0eba7b2c46afc628419f9a1b2a3a9f93241a06a7218` |
| `junit-platform-commons-1.8.1.jar` | `fa4fa68c8bd54dd0cb49c3fcbe9b2e42f4da6bedbe7e7ccf2a05f1a1e609b593` |
| `junit-platform-engine-1.8.1.jar` | `702868ed7e86b9b4672ede0f1e185e905baca9afab57746a7c650be3c7bca047` |
| `mockito-core-4.10.0.jar` | `bc149a7d74a65827faad56fdcd37f2f53f436ee39c29f088276fa1b384bf837a` |
| `objenesis-3.3.jar` | `02dfd0b0439a5591e35b708ed2f5474eb0948f53abf74637e959b8e4ef69bfeb` |
| `opentest4j-1.2.0.jar` | `58812de60898d976fb81ef3b62da05c6604c18fd4a249f5044282479fc286af2` |

---

## 6. Conclusão e Observações

A baseline `v1.0.0` do projeto **lo-car** foi auditada com sucesso. A auditoria física não identificou divergências entre os ICs declarados e os artefatos físicos presentes no repositório.

**Pontos de atenção para a Gerência de Configuração do projeto:**

1. **Ausência de gerenciador de dependências declarativo.** O projeto depende de JARs versionados manualmente em `lib/`, sem lockfile ou árvore de dependências resolvida. Isso aumenta o risco de drift de configuração (ex.: alguém substituir um JAR por uma versão diferente sem que isso seja rastreado em nenhum manifesto).

2. **Ausência de rastreamento de dependências transitivas.** Bibliotecas como `byte-buddy-agent` e `objenesis` são, na prática, dependências transitivas do `mockito-core`, mas constam no inventário como itens de primeiro nível, sem relação documentada entre eles.

3. **Inconsistência menor de metadados de licença** no artefato `opentest4j` (formato de texto livre em vez de identificador SPDX), originada no próprio JAR publicado, não na ferramenta de scan.

> **Recomendação:** como evolução de processo, a migração para uma ferramenta de build com gerência de dependências declarativa (Maven ou Gradle) permitiria geração de SBOMs com grafo de dependências completo e versionamento automatizado.
