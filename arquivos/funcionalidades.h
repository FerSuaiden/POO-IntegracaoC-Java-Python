/*
INTEGRANTES DO GRUPO:
                    Felipe da Costa Coqueiro,   NºUSP: 11781361
                    Fernando Alee Suaiden,      NºUSP: 12680836
*/

#ifndef FUNCIONALIDADES_H

    #define FUNCIONALIDADES_H
    
    #include "definicoesTipos.h"
    #include <stdbool.h>

    // Funcionalidade 1
    void criarArquivoBinario(char* nomeArquivoCSV, char* nomeArquivoBinario);

    // Funcionalidade 2
    void listarRegistros(const char* nomeArquivoBinario);

    // Funcionalidade 3
    void buscarRegistros(const char *arquivoEntrada, int numBuscas);

    // Funcionalidade 4
    int criarIndex(char* nomeArquivoBinario, char* nomeArquivoBinDeIndices, int opcao);

    // Funcionalidade 5
    void remover(FILE* nomeArquivoBinario, FILE* nomeArquivoBinDeIndices, int numBuscas);
    
    // Funcionalidade 6
    void inserir(FILE* nomeArquivoBinario);

#endif