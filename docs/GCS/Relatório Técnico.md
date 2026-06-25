# Relatório Técnico – Gerência de Configuração de Software (Parte 2)

**Projeto:** Lo-car (Sistema de Locação de Veículos)

**Autores:** Carlos Henrique Alves de Morais · José Borges da Cruz · Vinícius Pereira Espíndola

**Data de emissão:** 25/06/2026

---

## 1. Introdução

Este relatório acompanha os três artefatos entregues na Parte 2 da disciplina de Gerência de Configuração de Software (2026/1): o **SBOM**, o **VDD** e o **Script de Build**. O projeto auditado é o **LoCar!** — sistema desktop Java para locação de veículos — cuja Baseline `v1.0.0` foi formalmente identificada no Plano de Gerência de Configuração entregue na Parte 1.

O objetivo deste documento é resumir os pontos principais de cada artefato e apresentar a análise de determinismo, versionamento SemVer e reprodutibilidade do processo de build.

---

## 2. SBOM (Software Bill of Materials)

**Arquivo:** `sbom.json`
**Ferramenta:** Syft v1.45.1 (Anchore)
**Formato:** CycloneDX 1.6 (JSON)
**Serial:** `urn:uuid:cb0ff3e5-a65c-474a-af77-5aa39f2a75bc`

### Pontos principais

O SBOM foi gerado na raiz do repositório a partir da tag `v1.0.0`, identificando **15 componentes de terceiros** presentes no diretório `lib/`. Cada componente é catalogado em duas camadas:

- **`library`** — identidade lógica: nome, versão, licença, PURL e CPE.
- **`file`** — evidência física: caminho absoluto do `.jar`, hash SHA-1 e SHA-256.

| Licença | Componentes |
|---|---|
| Apache-2.0 | apiguardian-api, byte-buddy, byte-buddy-agent, flatlaf, junit-jupiter-params, objenesis, opentest4j |
| EPL-2.0 | junit-jupiter, junit-jupiter-api, junit-jupiter-engine, junit-platform-commons, junit-platform-engine |
| MIT | mockito-core |
| BSD-3-Clause | hamcrest-core |
| EPL-1.0 | junit |

### Limitação identificada

O projeto não possui gerenciador de build declarativo (sem `pom.xml` ou `build.gradle`), portanto o SBOM não reflete o **grafo de dependências transitivas** — apenas a lista plana de JARs presentes em `lib/`. Componentes como `byte-buddy-agent` e `objenesis` são, na prática, transitivos do `mockito-core`, mas aparecem como itens de primeiro nível.

---

## 3. VDD (Version Description Document)

**Arquivo:** `docs/GCS/Version Description Document (VDD).md`

### Pontos principais

O VDD documenta formalmente a Baseline `lo-car-v1.0.0`, cujo commit HEAD é `a952f16` (2026-05-27).

**Inventário de modificações:** foram identificadas **18 Solicitações de Mudança (SMs)** agrupadas por categoria funcional a partir do histórico Git, cobrindo desde a estrutura inicial do projeto (SM-01) até a integração final por Pull Requests (SM-18).

**Contribuidores:**

| Autor | Commits |
|---|---|
| carlosmorais01 | ~80 |
| Jose-borges47 | ~35 |
| Vinícius Espíndola | ~22 |

**Integração via PRs:** 28 Pull Requests formais foram registrados, consolidando branches paralelas na `main`.

**Limitações documentadas:** 5 limitações conhecidas foram registradas no VDD (L-01 a L-05), destacando a ausência de gerenciador de build, coexistência de JUnit 4 e 5, e persistência via serialização binária sem versionamento de esquema.

---

## 4. Script de Build

**Arquivo:** `build.sh`
**Artefato gerado:** `lo-car-1.0.0.jar`
**SHA-256:** `d23742642dec85b2b955e54a2528f7f1f2c6afd5b29ad5eec348073b823f92a1`

### Pontos principais

O `build.sh` é um shell script Bash que executa o ciclo completo de build em 5 etapas:

1. **Limpeza** — remove artefatos de builds anteriores.
2. **Coleta de fontes** — lista os 37 arquivos `.java` de `src/main/java/` com `find | sort`.
3. **Compilação** — invoca `javac --release 17` com classpath ordenado.
4. **Empacotamento de recursos** — copia imagens e fontes; extrai `flatlaf-3.5.1.jar` (única dependência de runtime) no diretório de classes.
5. **Geração do JAR** — empacota um **fat JAR** autocontido com `jar --date`, e gera o arquivo `.sha256` de integridade.

O artefato gerado é executado diretamente com:

```bash
java -jar lo-car-1.0.0.jar
```

**Separação runtime vs. teste:** os 14 JARs de teste (JUnit, Mockito e suas transitivas) são utilizados apenas na compilação e não são incluídos no artefato de produção, mantendo o JAR enxuto.

---

## 5. Análise de Determinismo do Build

Um build é **determinístico** quando, dado o mesmo código-fonte, produz sempre o mesmo artefato binário. O `build.sh` garante isso por meio de quatro decisões técnicas:

| Mecanismo | Problema eliminado |
|---|---|
| `find ... \| sort` na lista de fontes | A ordem de listagem do filesystem varia entre execuções e sistemas operacionais; `sort` a torna estável |
| `javac --release 17` | Fixa a versão-alvo do bytecode independente da JDK instalada (sistema usa JDK 21, mas o `.class` é emitido em nível 17) |
| `SOURCE_DATE_EPOCH` derivado do último commit Git | Provê um timestamp fixo e rastreável, eliminando a variação por hora de execução |
| `jar --date` (JDK 15+) | Aplica o `SOURCE_DATE_EPOCH` nos metadados de cada entrada do arquivo ZIP/JAR, tornando o binário byte-a-byte idêntico entre execuções |

O classpath passado ao `javac` também é ordenado (`find lib/ | sort`), eliminando variação pela ordem de descoberta dos JARs em `lib/`.

---

## 6. Versionamento SemVer

O projeto adota a nomenclatura **Semantic Versioning (SemVer)** no formato `MAJOR.MINOR.PATCH`, conforme definido no PGC da Parte 1:

| Versão | Significado                                                          |
|---|----------------------------------------------------------------------|
| `v1.0.0` | Sistema original com locação de veículos terrestres (baseline atual) |
| `v2.0.0` | Módulo Aquático (planejado)                                          |
| `v3.0.0` | Módulo Aéreo + integração final (planejado)                          |

A versão `1.0.0` está gravada como tag Git no repositório (`git tag v1.0.0`) apontando para o commit `a952f165c025f28142f26b796474996193e67abb`. O mesmo identificador consta no MANIFEST.MF do JAR (`Implementation-Version: 1.0.0`), no SBOM (`sbom.json`) e no VDD, garantindo rastreabilidade cruzada entre os artefatos.

A mudança de `MAJOR` é reservada para expansões arquiteturais que quebram compatibilidade com dados serializados existentes (como a adição dos módulos Aquático e Aéreo). Correções de bugs incrementam `PATCH`; novas funcionalidades retrocompatíveis incrementam `MINOR`.

---

## 7. Análise de Reprodutibilidade

**Reprodutibilidade** é a capacidade de um terceiro, em um ambiente diferente, gerar o mesmo artefato a partir do mesmo código-fonte.

### Evidência experimental

O `build.sh` foi executado duas vezes consecutivas em sequência, sem alteração de código ou ambiente. O SHA-256 do artefato gerado foi idêntico nas duas execuções:

```
d23742642dec85b2b955e54a2528f7f1f2c6afd5b29ad5eec348073b823f92a1  lo-car-1.0.0.jar
```

### Requisitos para reprodução

Para que um terceiro reproduza o artefato, são necessários:

1. **JDK 15 ou superior** — para suporte ao flag `jar --date`; recomendado JDK 17+.
2. **Git** — para clonar o repositório e derivar o `SOURCE_DATE_EPOCH` do commit.
3. **Bash** — para execução do `build.sh`.
4. **`unzip`** — para extração do `flatlaf` no fat JAR.

```bash
git clone <url-do-repositorio>
cd lo-car
git checkout v1.0.0
bash build.sh
sha256sum lo-car-1.0.0.jar   # deve retornar d23742642dec...
```

### Limitação residual

O campo `Build-Jdk` no MANIFEST.MF varia com a versão da JDK instalada (ex.: `javac 21.0.11` vs. `javac 17.0.x`), o que altera o conteúdo do MANIFEST e, portanto, o SHA-256 final em ambientes com JDK diferente. Para builds **bit-a-bit idênticos entre JDKs distintas**, seria necessário fixar também a versão do compilador ou remover esse campo do MANIFEST.
