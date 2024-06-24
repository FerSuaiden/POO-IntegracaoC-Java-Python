/*
INTEGRANTES DO GRUPO:
                    Felipe da Costa Coqueiro,   NºUSP: 11781361
                    Fernando Alee Suaiden,      NºUSP: 12680836
*/

#include "funcoesAuxiliares.h"
#include "funcoes_fornecidas.h"
#include "funcionalidades.h"
#include "definicoesTipos.h"
#include <stdbool.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>

// Função que insere o nó na lista de forma ordenada 
void InserirOrdenado(LISTA** cabeca, LISTA* novoNo) {
  
    LISTA* atual;

    // Caso para o cabeçalho da lista
    if (*cabeca == NULL || (*cabeca)->tamRegistro > novoNo->tamRegistro) {
        novoNo->prox = *cabeca;
        *cabeca = novoNo;
    } else {
        // Localiza o nó anterior ao ponto de inserção
        LISTA* anterior = NULL;
        atual = *cabeca;

        while (atual != NULL && atual->tamRegistro <= novoNo->tamRegistro) {
            anterior = atual;
            atual = atual->prox;
        }

        novoNo->prox = anterior->prox;
        anterior->prox = novoNo;
    }
}

// Função que reescreve os registros removidos no arquivo de dados
int ReescreveRemovidos(FILE* arquivoBinario, LISTA* removidos) {
    if (removidos == NULL) {
        fseek(arquivoBinario, 1, SEEK_SET);
        int64_t topo = -1;
        fwrite(&topo, sizeof(int64_t), 1, arquivoBinario);
        return true;
    }

    // Atualiza o topo da lista de removidos
    fseek(arquivoBinario, 1, SEEK_SET);
    fwrite(&removidos->byteOffset, sizeof(int64_t), 1, arquivoBinario);

    LISTA* atual = removidos;
    while (atual != NULL) {
        fseek(arquivoBinario, atual->byteOffset, SEEK_SET);

        char removido = '1';
        fwrite(&removido, sizeof(char), 1, arquivoBinario);
        fwrite(&atual->tamRegistro, sizeof(int), 1, arquivoBinario);

        if (atual->prox != NULL) {
            fwrite(&atual->prox->byteOffset, sizeof(int64_t), 1, arquivoBinario);
        } else {
            int64_t fimLista = -1;
            fwrite(&fimLista, sizeof(int64_t), 1, arquivoBinario);
        }

        atual = atual->prox;
    }
    return 1;
}

// liberação da memória da lista encadeada
void DesalocaLista(LISTA* cabeca) {
    LISTA* atual = cabeca;
    LISTA* proximo;

    while (atual != NULL) {
        proximo = atual->prox;

        free(atual);
        atual = proximo;
    }
}

// Função que retorna o melhor byteoffset para inserção (dentre os removidos) de acordo com a estratégia bestFit
int64_t MetodoBestFit(LISTA** removidos, int tamRegistro, FILE* arquivoBinario) {
    LISTA* anterior = NULL;
    LISTA* atual = *removidos;

    // Passa pela lista inteira
    while (atual != NULL) {
        if (atual->tamRegistro >= tamRegistro) {
            int64_t offset = atual->byteOffset;

            if (anterior == NULL) {
                *removidos = atual->prox;

            } else {
                anterior->prox = atual->prox;
            }

            free(atual);
            return offset;
        }
        anterior = atual;
        atual = atual->prox;
    }

    // Não há offset que o registro caiba
    return -1;
}

// Função que retorna uma lista dos registros removidos em ordem crescente 
LISTA* OrdenaRegistrosRem(FILE* arquivoBinario){

    // Pula o status
    fseek(arquivoBinario, 1, SEEK_SET);

    // Guarda a posição atual do ponteiro
    int64_t posicaoAtual = ftell(arquivoBinario);

    int64_t topo;
    LISTA* listaOrdenada = NULL;

    // Leitura do topo da lista de removidos
    fread(&topo, sizeof(int64_t), 1, arquivoBinario);

    if(topo == -1){
        fseek(arquivoBinario, posicaoAtual, SEEK_SET);
        return(listaOrdenada);
    }

    fseek(arquivoBinario, topo, SEEK_SET);
    DADOS registro_dados;

    // Aloca memória para o registro
    AlocaMemoriaRegistro(&registro_dados);

    while(topo != -1){
        if (fread(&registro_dados.removido, sizeof(char), 1, arquivoBinario) != 1 || fread(&registro_dados.tamanhoRegistro, sizeof(int), 1, arquivoBinario) != 1 ||
            fread(&registro_dados.prox, sizeof(int64_t), 1, arquivoBinario) != 1) {

            printf("Erro: falha em ler o próximo registro\n");

            break;
        }

        LISTA* no = (LISTA*)malloc(sizeof(LISTA));
        no->byteOffset = topo;
        no->tamRegistro = registro_dados.tamanhoRegistro;
        no->prox = NULL;

        // Insere ordenado
        InserirOrdenado(&listaOrdenada, no);

        topo = registro_dados.prox;

        if(topo != -1){
            // Restaura a posição original do ponteiro
            fseek(arquivoBinario, topo, SEEK_SET);
        }
    }

    free(registro_dados.nacionalidade);
    free(registro_dados.nomeClube);
    free(registro_dados.nomeJogador);
    
    // Restaura a posição original do ponteiro
    fseek(arquivoBinario, posicaoAtual, SEEK_SET);

    return(listaOrdenada);
}

// Leitura dos campos relacionados ao registro
void LeituraCampos(DADOS* registro){

    if(registro == NULL){
        return;
    }

    // Leitura id
    scanf("%d", &registro->id);

    char idade[15];
    // Leitura idade
    scanf("%s", idade);
    
    if(strcmp(idade, "NULO") == 0){
        registro->idade = -1;
    } else {
        registro->idade = atoi(idade); 
    }

    scan_quote_string(registro->nomeJogador);
    scan_quote_string(registro->nacionalidade);
    scan_quote_string(registro->nomeClube);

    return;
}

// atualiza o tamanho das strings do registro
int AtualizaTamanhoStrings(DADOS* registro) {
    if (registro == NULL){
        return 0;
    }
    registro->tamNomeJog = strlen(registro->nomeJogador);
    registro->tamNacionalidade = strlen(registro->nacionalidade);
    registro->tamNomeClube = strlen(registro->nomeClube);

    return 1;
}

// Função que retorna o tamanho do registro removido e tamanho do registro a ser inserido
int* ReutilizarRegistro(FILE* arquivoBinario, CABECALHO* cabecalho, DADOS* registro, int BestFit, LISTA* removidos) {
    
    // Tamanho do registro removido
    int tamRegRemovido;

    // Registro removido que pode ser usado
    if(BestFit != -1){

        // Decrementa num registro removidos do cabeçalho
        cabecalho->nroRegRem--;

        // Pega o tamanho do registro
        fseek(arquivoBinario, BestFit, SEEK_SET);

        fread(&registro->removido, sizeof(char), 1, arquivoBinario);

        fread(&tamRegRemovido, sizeof(int), 1, arquivoBinario);

        // Reutiliza o registro removido
        fseek(arquivoBinario, BestFit, SEEK_SET);
    } else {
        // Atualiza proxByteOffset
        cabecalho->proxByteOffset += registro->tamanhoRegistro;

        // Atualiza o tamanho do registro removido
        tamRegRemovido = registro->tamanhoRegistro;

        // Vai para final do arquivo
        fseek(arquivoBinario, 0, SEEK_END);
    }

    if(removidos && BestFit == cabecalho->topo)
        // Atualiza o topo
        cabecalho->topo = removidos->byteOffset;

    int tamRegistroInserido = registro->tamanhoRegistro;
    registro->tamanhoRegistro = tamRegRemovido;

    // Retorna um vetor com tamanho do registro a ser inserido e tamanho do registro removido
    int* tamanhoReg = (int*)malloc(2*sizeof(int));
    tamanhoReg[0] = tamRegRemovido;
    tamanhoReg[1] = tamRegistroInserido;

    return(tamanhoReg);
}

// Funcao escreve o cabecalho no arquivo binario
void EscreveRegistroInsercao(FILE* arquivoBinario, DADOS* registro) {
    fwrite(&registro->removido, sizeof(registro->removido), 1, arquivoBinario);
    fwrite(&registro->tamanhoRegistro, sizeof(registro->tamanhoRegistro), 1, arquivoBinario);
    fwrite(&registro->prox, sizeof(registro->prox), 1, arquivoBinario);
    fwrite(&registro->id, sizeof(registro->id), 1, arquivoBinario);
    fwrite(&registro->idade, sizeof(registro->idade), 1, arquivoBinario);
    fwrite(&registro->tamNomeJog, sizeof(registro->tamNomeJog), 1, arquivoBinario);
    fwrite(registro->nomeJogador, sizeof(char), registro->tamNomeJog, arquivoBinario);
    fwrite(&registro->tamNacionalidade, sizeof(registro->tamNacionalidade), 1, arquivoBinario);
    fwrite(registro->nacionalidade, sizeof(char), registro->tamNacionalidade, arquivoBinario);
    fwrite(&registro->tamNomeClube, sizeof(registro->tamNomeClube), 1, arquivoBinario);
    fwrite(registro->nomeClube, sizeof(char), registro->tamNomeClube, arquivoBinario);
}

// Função que escreve o lixo no final do registro
void EscreveLixo(FILE* arquivoBinario, int tamRegistroRemovido, int tamRegistroInserido) {
    if (tamRegistroRemovido == tamRegistroInserido) return;

    int diferencaBytes = tamRegistroRemovido - tamRegistroInserido;

    for(int i = 0; i < diferencaBytes; i++){
        char cifrao = '$';
        fwrite(&cifrao, sizeof(char), 1, arquivoBinario);
    }
}

// Função que inicializa o cabecalho
void IniCabecalho(CABECALHO* cabecalho) {
    cabecalho->status = '0';
    cabecalho->topo = -1;
    cabecalho->proxByteOffset = 25;
    cabecalho->nroRegArq = 0;
    cabecalho->nroRegRem = 0;
}