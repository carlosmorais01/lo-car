#!/usr/bin/env bash
# =============================================================================
# build.sh — Script de build determinístico do projeto LoCar!
# Versão do artefato (SemVer): 1.0.0
# =============================================================================
set -euo pipefail

# ── Configuração ──────────────────────────────────────────────────────────────
VERSION="1.0.0"
ARTIFACT="lo-car-${VERSION}.jar"
MAIN_CLASS="br.com.locar.Main"
JAVA_RELEASE="17"

SRC_DIR="src/main/java"
LIB_DIR="lib"
BUILD_DIR="build"
CLASSES_DIR="${BUILD_DIR}/classes"
SOURCES_LIST="${BUILD_DIR}/sources.txt"

# Dependências de runtime que serão embutidas no fat JAR.
# JARs de teste (JUnit, Mockito, etc.) são excluídos do artefato de produção.
RUNTIME_LIBS=(
  "${LIB_DIR}/flatlaf-3.5.1.jar"
)

# SOURCE_DATE_EPOCH: timestamp fixo do último commit Git.
# Garante que dois builds do mesmo commit produzam o mesmo JAR byte-a-byte.
# Pode ser sobrescrito via variável de ambiente: SOURCE_DATE_EPOCH=<unix_ts> ./build.sh
SOURCE_DATE_EPOCH="${SOURCE_DATE_EPOCH:-$(git log -1 --format=%ct)}"
BUILD_DATE=$(date -u -d "@${SOURCE_DATE_EPOCH}" '+%Y-%m-%dT%H:%M:%S+00:00')

echo "========================================"
echo " LoCar! Build Script"
echo " Versão artefato   : ${VERSION}"
echo " SOURCE_DATE_EPOCH : ${SOURCE_DATE_EPOCH}"
echo " Build date (UTC)  : ${BUILD_DATE}"
echo "========================================"
echo ""

# ── 1. Limpeza ────────────────────────────────────────────────────────────────
echo "[1/5] Limpando build anterior..."
rm -rf "${BUILD_DIR}" "${ARTIFACT}" "${ARTIFACT}.sha256"
mkdir -p "${CLASSES_DIR}"

# ── 2. Coleta de fontes (ordenada para determinismo) ─────────────────────────
echo "[2/5] Coletando fontes Java..."
find "${SRC_DIR}" -name "*.java" | sort > "${SOURCES_LIST}"
echo "      $(wc -l < "${SOURCES_LIST}") arquivo(s) .java encontrado(s)."

# ── 3. Compilação ─────────────────────────────────────────────────────────────
echo "[3/5] Compilando com --release ${JAVA_RELEASE}..."
# Classpath ordenado: elimina variação por ordem de listagem de diretório.
CLASSPATH=$(find "${LIB_DIR}" -name "*.jar" | sort | tr '\n' ':')

javac \
  --release "${JAVA_RELEASE}" \
  -encoding UTF-8 \
  -cp "${CLASSPATH}" \
  -d "${CLASSES_DIR}" \
  @"${SOURCES_LIST}"

echo "      Compilação concluída."

# ── 4. Cópia de recursos e dependências de runtime ───────────────────────────
echo "[4/5] Copiando recursos (imagens, fontes) e dependências de runtime..."

# Recursos não-Java embutidos no JAR
for ext in png jpg jpeg ttf gif ico; do
  find "${SRC_DIR}" -name "*.${ext}" | sort | while read -r resource; do
    dest="${CLASSES_DIR}/${resource#${SRC_DIR}/}"
    mkdir -p "$(dirname "${dest}")"
    cp "${resource}" "${dest}"
  done
done

# Extrai cada dependência de runtime dentro do diretório de classes (fat JAR).
# META-INF/MANIFEST.MF é excluído para não sobrescrever o nosso.
for lib in "${RUNTIME_LIBS[@]}"; do
  echo "      Extraindo: $(basename "${lib}")"
  unzip -q -o "${lib}" -d "${CLASSES_DIR}" -x "META-INF/MANIFEST.MF" "META-INF/LICENSE*" "META-INF/NOTICE*"
done
echo "      Recursos e dependências copiados."

# ── 5. Empacotamento JAR ──────────────────────────────────────────────────────
echo "[5/5] Empacotando ${ARTIFACT} (fat JAR)..."

# Gera o MANIFEST com metadados de versão
MANIFEST_FILE="${BUILD_DIR}/MANIFEST.MF"
cat > "${MANIFEST_FILE}" <<EOF
Manifest-Version: 1.0
Main-Class: ${MAIN_CLASS}
Implementation-Title: LoCar!
Implementation-Version: ${VERSION}
Build-Jdk: $(javac -version 2>&1)
EOF

# --date fixa os timestamps de todas as entradas do ZIP/JAR (requer JDK 15+).
# Com SOURCE_DATE_EPOCH fixo, dois builds do mesmo commit geram bytes idênticos.
jar --create \
    --file="${ARTIFACT}" \
    --manifest="${MANIFEST_FILE}" \
    --date="${BUILD_DATE}" \
    -C "${CLASSES_DIR}" .

# ── Verificação de integridade ────────────────────────────────────────────────
sha256sum "${ARTIFACT}" > "${ARTIFACT}.sha256"
CHECKSUM=$(awk '{print $1}' "${ARTIFACT}.sha256")

echo ""
echo "========================================"
echo " Build finalizado com sucesso!"
echo " Artefato  : ${ARTIFACT}"
echo " SHA-256   : ${CHECKSUM}"
echo "========================================"
echo ""
echo "Para executar a aplicação:"
echo "  java -jar ${ARTIFACT}"
