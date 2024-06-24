/*
INTEGRANTES DO GRUPO:
                    Felipe da Costa Coqueiro,   NºUSP: 11781361
                    Fernando Alee Suaiden,      NºUSP: 12680836
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "funcionalidades.h"
#include "definicoesTipos.h"
#include "funcoes_fornecidas.h"

int main(void){
    int opcao, numBuscas, numRemocoes, numInsercoes;
    char nomeArquivoCSV[50];
    char nomeArquivoBinario[50];
    char nomeArquivoIndex[50];

    scanf("%d", &opcao); // Determina qual funcionalidade irá executar

    switch(opcao){
        case 1: // Funcionalidade 1 (cria arquivo binário)
            scanf("%s %s", nomeArquivoCSV, nomeArquivoBinario);
            criarArquivoBinario(nomeArquivoCSV, nomeArquivoBinario);
            binarioNaTela(nomeArquivoBinario);
            break;
        case 2: // Funcionalidade 2 (lista os registros)
            scanf("%s", nomeArquivoBinario);
            listarRegistros(nomeArquivoBinario);
            break;
        case 3: // Funcionalidade 3 (faz a busca por campos)
            scanf("%s %d", nomeArquivoBinario, &numBuscas);
            buscarRegistros(nomeArquivoBinario, numBuscas);
            break;
        case 4: // Funcionalidade 4 (cria arquivo de index)
            scanf("%s %s", nomeArquivoBinario, nomeArquivoIndex);
            if (criarIndex(nomeArquivoBinario, nomeArquivoIndex, opcao)) {
                binarioNaTela(nomeArquivoIndex);
            }
            break;
        case 5: { // Funcionalidade 5 (remoção lógica do arquivo de dados)
            scanf("%s %s %d", nomeArquivoBinario, nomeArquivoIndex, &numRemocoes);
            // abertura do arquivo
            FILE* arquivoBinario = fopen(nomeArquivoBinario, "rb+");
            FILE* arquivoIndex = fopen(nomeArquivoIndex, "rb+");

            if (arquivoBinario == NULL) {
                printf("Falha no processamento do arquivo.\n");
                return 0;
            }

            if(arquivoIndex == NULL) {
                // Caso o arquivo de índice não exista, cria um novo
                arquivoIndex = fopen(nomeArquivoIndex, "wb");
            }

            int numCampos;

            for(int i = 0; i < numRemocoes; i++) {
                scanf("%d", &numCampos);
                remover(arquivoBinario, arquivoIndex, numCampos);
            }

            criarIndex(nomeArquivoBinario, nomeArquivoIndex, opcao);

            // Fechar os arquivos
            fclose(arquivoBinario);
            fclose(arquivoIndex);

            binarioNaTela(nomeArquivoBinario);
            binarioNaTela(nomeArquivoIndex);

            break;
        }
        case 6: // Funcionalidade 6 (inserção de novos registros)
            scanf("%s %s %d", nomeArquivoBinario, nomeArquivoIndex, &numInsercoes);
            
            // Abertura do arquivo
            FILE* arquivoBinario = fopen(nomeArquivoBinario, "rb+");
            FILE* arquivoIndex = fopen(nomeArquivoIndex, "rb+");

            if (arquivoBinario == NULL) {
                printf("Falha no processamento do arquivo.\n");
                return 0;
            }

            if(arquivoIndex == NULL){
                // Caso o arquivo de índice não exista, cria um novo
                arquivoIndex = fopen(nomeArquivoIndex, "wb");
                criarIndex(nomeArquivoBinario, nomeArquivoIndex, opcao);

            }

            for(int i = 0; i < numInsercoes; i++) {
                inserir(arquivoBinario);
            }

            // Apagar o arquivo de índice
            fclose(arquivoIndex);
            arquivoIndex = fopen(nomeArquivoIndex, "wb");

            // Recria o arquivo binario de index
            criarIndex(nomeArquivoBinario, nomeArquivoIndex, opcao);

            // Fechar os arquivos
            fclose(arquivoBinario);
            fclose(arquivoIndex);

            binarioNaTela(nomeArquivoBinario);
            binarioNaTela(nomeArquivoIndex);

            break;
        default: 
            printf("Opção inválida.\n");
            return(1);
    }
    return(0);
}