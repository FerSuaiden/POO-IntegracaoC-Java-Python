/*
INTEGRANTES DO GRUPO:
                    Felipe da Costa Coqueiro,   NºUSP: 11781361
                    Fernando Alee Suaiden,      NºUSP: 12680836
*/

#ifndef FUNCOES_AUX_H
    #define FUNCOES_AUX_H

    #include "funcionalidades.h"
    #include "definicoesTipos.h"
    #include <stdio.h>
    #include <stdlib.h>
    #include <stdbool.h>

    // Leitura de cabeçalho e registros
    void LeituraCabecalho(CABECALHO* cabecalho, FILE* arquivoBinario);
    void LeituraRegistro(DADOS* registro, FILE* arquivoBinario);
    void LeituraRegistroFixo(DADOS_FIXOS* registro, FILE* arquivoBinario);

    // Escrita de cabeçalho e registros
    void EscritaCabecalho(CABECALHO* cabecalho, FILE* arquivoBinario);
    void EscritaRegistro(DADOS* registro, FILE* arquivoBinario);
    void EscritaRegistroFixo(DADOS_FIXOS* registro, FILE* arquivoBinario);
    
    // Correspondecia de campos
    int todosCamposCorrespondem(DADOS registro, CAMPO_BUSCA camposBusca[], int numCamposBusca);
    int validarNomeCampo(const char *nomeCampo);
    int todosCamposCorrespondemFixo(DADOS_FIXOS registro, CAMPO_BUSCA camposBusca[], int numCamposBusca);
    
    // Escrita no index
    void EscritaCabecalho_Index(CABECALHO_INDEX* cabecalho_index, FILE* nomeArquivoBinarioDeIndices);
    void EscritaRegistro_Index(REGISTRO_INDEX* registro_index, FILE* nomeArquivoBinarioDeIndices);

    // Funções auxiliares de remoção
    void InicializaRegistroJogador(DADOS* registro);
    void AlocaMemoriaRegistro(DADOS* registro);
    int LeituraRemocao(FILE* arquivoBinario, DADOS* registro);
    int64_t RetornaUltimoRemovido(FILE* arquivoBinario);
    void LiberaMemoriaChar(char** nomeCampo, char** valorCampo, int numCamposBusca);
    void DesalocaMemoriaReg(DADOS* registro);
    void LeituraParametrosBusca(int numCamposBusca, char** nomeCampo, char** valorCampo);

    // Funções auxiliares de inserção
    void AtualizaCampos(DADOS* registro);
    LISTA* OrdenaRegistrosRem(FILE* arquivoBinario);
    void InserirOrdenado(LISTA** cabeca, LISTA* novoNo);
    int64_t MetodoBestFit(LISTA** removidos, int tamRegistro, FILE* arquivoBinario);
    int ReescreveRemovidos(FILE* arquivoBinario, LISTA* removidos);
    void DesalocaLista(LISTA* cabeca);
    void LeituraCampos(DADOS* registro);
    int AtualizaTamanhoStrings(DADOS* registro);
    int* ReutilizarRegistro(FILE* arquivoBinario, CABECALHO* cabecalho, DADOS* registro, int MetodoBestFitOffset, LISTA* removidos);
    void EscreveRegistroInsercao(FILE* arquivoBinario, DADOS* registro);
    void EscreveLixo(FILE* arquivoBinario, int tamRegistroRemovido, int tamRegistroInserido);
    void IniCabecalho(CABECALHO* cabecalho);

#endif