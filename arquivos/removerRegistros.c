/*
INTEGRANTES DO GRUPO:
                    Felipe da Costa Coqueiro,   NºUSP: 11781361
                    Fernando Alee Suaiden,      NºUSP: 12680836
*/

#include "funcionalidades.h"
#include "definicoesTipos.h"
#include "funcoesAuxiliares.h"
#include "funcoes_fornecidas.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

// Função para remover os registros especificados
void remover(FILE* nomeArquivoBinario, FILE* nomeArquivoIndices, int numeroDeBuscas) {
    
    // Alocação de memória para o valor do campo
    char** valorCampo = (char**)malloc(numeroDeBuscas*sizeof(char*));
    for (int i = 0; i < numeroDeBuscas; i++) {
        valorCampo[i] = (char*)malloc(40*sizeof(char));
    }
    
    // Alocação de memória para campo
    char** campo = (char**)malloc(numeroDeBuscas*sizeof(char*));
    for (int i = 0; i < numeroDeBuscas; i++) {
        campo[i] = (char*)malloc(14 * sizeof(char));
    }

    // Lê os campos de busca
    LeituraParametrosBusca(numeroDeBuscas, campo, valorCampo);

    // Inicializa registro de dados
    DADOS registro_dados;
    InicializaRegistroJogador(&registro_dados);

    // Aloca memória para variáveis de tamanho variável
    AlocaMemoriaRegistro(&registro_dados);

    int64_t prox;

    // Pula o cabeçalho
    fseek(nomeArquivoBinario, TAM_INICIAL_BYTEOFFSET, SEEK_SET);

    while (fread(&prox, sizeof(int64_t), 1, nomeArquivoBinario) == 1) {
        // Volta para ler o registro inteiro
        fseek(nomeArquivoBinario, -sizeof(int64_t), SEEK_CUR);

        int64_t byteOffset = ftell(nomeArquivoBinario);

        if (!LeituraRemocao(nomeArquivoBinario, &registro_dados)) {
            continue;
        }

        // Contador para a quantidade de campos da busca
        int counterCampo = 0;

        // Verificar os critérios de busca
        for (int j = 0; j < numeroDeBuscas; j++) {
            if (strcmp(campo[j], "id") == 0 && registro_dados.id == atoi(valorCampo[j])) {
                counterCampo++;

                // Vai no arquivo de índice e procura o byteoffset por id
                fseek(nomeArquivoIndices, 0, SEEK_SET);
                int idRegistro;
                int64_t byteOffsetIndice;
                while (fread(&idRegistro, sizeof(int), 1, nomeArquivoIndices) == 1) {
                    fread(&byteOffsetIndice, sizeof(int64_t), 1, nomeArquivoIndices);
                    if (idRegistro == registro_dados.id) {

                        // ByteOffSet do último registro removido
                        int byteOffSetUltimoRegistroRemovido = RetornaUltimoRemovido(nomeArquivoBinario);

                        //  Tem registros removidos
                        if (byteOffSetUltimoRegistroRemovido != -1) {

                            // Pula para o prox do registro
                            fseek(nomeArquivoBinario, byteOffSetUltimoRegistroRemovido + 5, SEEK_SET);
                            fwrite(&byteOffsetIndice, sizeof(int64_t), 1, nomeArquivoBinario);

                            fseek(nomeArquivoBinario, byteOffsetIndice, SEEK_SET);
                            char removido = '1';
                            fwrite(&removido, sizeof(char), 1, nomeArquivoBinario);

                        // Não tem registros removidos
                        } else {
                            // fseek para o byteoffset do registro que será removido
                            fseek(nomeArquivoBinario, byteOffsetIndice, SEEK_SET);

                            // Atualiza o removido
                            char removido = '1';
                            fwrite(&removido, sizeof(char), 1, nomeArquivoBinario);

                            // Volta pro começo do registro pulando status e lê o topo
                            fseek(nomeArquivoBinario, 1, SEEK_SET);
                            int64_t topo;
                            fread(&topo, sizeof(int64_t), 1, nomeArquivoBinario);

                            // Volta para o registro que quer remover mais 5 bytes
                            fseek(nomeArquivoBinario, byteOffsetIndice + 5, SEEK_SET);

                            // Escreve o topo no campo prox
                            fwrite(&topo, sizeof(int64_t), 1, nomeArquivoBinario);

                            // Volta pro começo do registro
                            fseek(nomeArquivoBinario, byteOffsetIndice, SEEK_SET);

                            // Escreve o byteoffset no topo
                            fseek(nomeArquivoBinario, 1, SEEK_SET);
                            fwrite(&byteOffsetIndice, sizeof(int64_t), 1, nomeArquivoBinario);
                        }
                        break;
                    }
                }

            } else if (strcmp(campo[j], "idade") == 0) {
                if (registro_dados.idade == atoi(valorCampo[j])) {
                    counterCampo++;
                }
            } else if (strcmp(campo[j], "nomeJogador") == 0) {
                if (registro_dados.tamNomeJog > 0 && strcmp(registro_dados.nomeJogador, valorCampo[j]) == 0) {
                    counterCampo++;
                }
            } else if (strcmp(campo[j], "nacionalidade") == 0) {
                if (registro_dados.tamNacionalidade > 0 && strcmp(registro_dados.nacionalidade, valorCampo[j]) == 0) {
                    counterCampo++;
                }
            } else if (strcmp(campo[j], "nomeClube") == 0) {
                if (registro_dados.tamNomeClube > 0 && strcmp(registro_dados.nomeClube, valorCampo[j]) == 0) {
                    counterCampo++;
                }
            }
        }

        // Se todos os critérios forem atendidos
        if (counterCampo == numeroDeBuscas) {

            // Salvamos o byteoffset atual 
            int64_t byteOffsetAtual = ftell(nomeArquivoBinario);

            int byteOffSetUltimoRegistroRemovido = RetornaUltimoRemovido(nomeArquivoBinario);

            if (byteOffSetUltimoRegistroRemovido != -1) {
                // pulamos para o prox do registro
                fseek(nomeArquivoBinario, byteOffSetUltimoRegistroRemovido + 5, SEEK_SET);
                fwrite(&byteOffset, sizeof(int64_t), 1, nomeArquivoBinario);

                fseek(nomeArquivoBinario, byteOffset, SEEK_SET);
                char removido = '1';
                fwrite(&removido, sizeof(char), 1, nomeArquivoBinario);
            } else {
                // Vai pro byteoffset do registro que quer remover
                fseek(nomeArquivoBinario, byteOffset, SEEK_SET);

                // Escreve o campo removido
                char removido = '1';
                fwrite(&removido, sizeof(char), 1, nomeArquivoBinario);

                // Volta pro começo do registro pulando status e lê o topo
                fseek(nomeArquivoBinario, 1, SEEK_SET);
                int64_t topo;
                fread(&topo, sizeof(int64_t), 1, nomeArquivoBinario);

                // Volta para o registro que quer remover mais 5 bytes
                fseek(nomeArquivoBinario, byteOffset + 5, SEEK_SET);

                // Escreve o topo no campo prox
                fwrite(&topo, sizeof(int64_t), 1, nomeArquivoBinario);

                // Volta pro começo do registro
                fseek(nomeArquivoBinario, byteOffset, SEEK_SET);

                // Escreve o byteoffset no topo
                fseek(nomeArquivoBinario, 1, SEEK_SET);
                fwrite(&byteOffset, sizeof(int64_t), 1, nomeArquivoBinario);
            }

            RetornaUltimoRemovido(nomeArquivoBinario);

            // Volta para o byteoffset que estava antes
            fseek(nomeArquivoBinario, byteOffsetAtual, SEEK_SET);

        }
    }
    // Libera memória alocada
    LiberaMemoriaChar(campo, valorCampo, numeroDeBuscas);
    DesalocaMemoriaReg(&registro_dados);
}
