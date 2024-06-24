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

// Função que insere um novo registro no arquivo
void inserir(FILE* nomeArquivoBinario){
    
    if(nomeArquivoBinario == NULL){
        printf("Erro: ponteiro do arquivo é NULL\n");
        return;
    }
    
    CABECALHO cabecalho;
    
    // Inicializa o cabeçalho
    IniCabecalho(&cabecalho);
    
    // Leitura do cabeçalho
    LeituraCabecalho(&cabecalho, nomeArquivoBinario);

    DADOS registro_dados;

    // Aloca memória para o registro
    AlocaMemoriaRegistro(&registro_dados);

    // Lê os campos relacionados ao registro
    LeituraCampos(&registro_dados);

    // Atualiza o tamanho das strings
    AtualizaTamanhoStrings(&registro_dados);

    // Atualiza os outros campos
    AtualizaCampos(&registro_dados);

    // Armazenar o offset registro removido
    int64_t MetodoBestFitOffset = -1;
    LISTA* removidos = NULL;

    // Leitura do topo
    int64_t topo;
    fseek(nomeArquivoBinario, 1, SEEK_SET);
    fread(&topo, sizeof(int64_t), 1, nomeArquivoBinario);

    // Se tem registro removido
    if(topo != -1){

        // Ordena os registros removidos
        removidos = OrdenaRegistrosRem(nomeArquivoBinario);

        if(removidos != NULL){
            // Encontra o byteoffset do melhor registro removido para reutilizar
            MetodoBestFitOffset = MetodoBestFit(&removidos, registro_dados.tamanhoRegistro, nomeArquivoBinario);
        }
    }

    // Adiciona o registro
    int* tamanhoRegistro = ReutilizarRegistro(nomeArquivoBinario, &cabecalho, &registro_dados, MetodoBestFitOffset, removidos);

    // Define os novos valores para o registro não removido
    registro_dados.prox = -1;
    registro_dados.removido = '0';

    // Aumenta o número de registros
    cabecalho.nroRegArq++;

    // Escreve os dados inseridos no arquivo
    EscreveRegistroInsercao(nomeArquivoBinario, &registro_dados);

    // Escreve o lixo '$' de acordo com o tamanho do registro
    EscreveLixo(nomeArquivoBinario, tamanhoRegistro[0], tamanhoRegistro[1]);

    // Libera a memória alocada
    free(tamanhoRegistro);

    // Atualiza a lista de removidos no arquivo
    ReescreveRemovidos(nomeArquivoBinario, removidos);

    // Libera a memória alocada
    DesalocaMemoriaReg(&registro_dados);
    DesalocaLista(removidos);

    // Atualiza e escreve o cabeçalho no arquivo
    EscritaCabecalho(&cabecalho, nomeArquivoBinario);

    return;
}