# Diretórios
SRC_DIR = ../src
CSV_DIR = ../csv
ARQ_DIR = .

# Todos os arquivos de origem
C_SOURCES = $(wildcard $(ARQ_DIR)/*.c)
H_FILES = $(wildcard $(ARQ_DIR)/*.h)

# Saída da biblioteca compartilhada
LIB_OUTPUT = $(ARQ_DIR)/libfuncionalidades.so

# Flags do compilador
CFLAGS = -g -Wall -fPIC

# Alvo para compilar o programa C
all: programaTrab lib

# Compilar o executável principal
programaTrab: $(C_SOURCES) $(H_FILES)
	gcc $(CFLAGS) -o "$(ARQ_DIR)/programaTrab" $(C_SOURCES)

# Compilar a biblioteca compartilhada
lib: $(C_SOURCES) $(H_FILES)
	gcc $(CFLAGS) -shared -o "$(LIB_OUTPUT)" $(C_SOURCES)

# Executar o executável principal
run: all
	"$(ARQ_DIR)/programaTrab"

# Limpar os arquivos compilados
clean:
	rm -f "$(ARQ_DIR)/*.exe" "$(ARQ_DIR)/programaTrab" "$(LIB_OUTPUT)"
