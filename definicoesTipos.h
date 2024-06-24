/*
INTEGRANTES DO GRUPO:
                    Felipe da Costa Coqueiro,   NºUSP: 11781361
                    Fernando Alee Suaiden,      NºUSP: 12680836
*/

#ifndef FUNCOES_H

    #define FUNCOES_H

    #define MAX_CAMPO 50
    #define TAM_INICIAL_BYTEOFFSET 25

    #include <stdint.h>
    #include <stdio.h>
    
    // Registro de cabeçalho
    typedef struct {
        char status;
        int64_t topo;
        int64_t proxByteOffset;
        int nroRegArq;
        int nroRegRem;
    } CABECALHO;

    // Registro de dados
    typedef struct {
        char removido;
        int tamanhoRegistro;
        int64_t prox;
        int id;
        int idade;
        int tamNomeJog;
        char* nomeJogador;
        int tamNacionalidade;
        char* nacionalidade;
        int tamNomeClube;
        char* nomeClube;
    } DADOS;

    // Registro de dados de campos fixos
    typedef struct {
        char removido;
        int tamanhoRegistro;
        int64_t prox;
        int id;
        int idade;
        int tamNomeJog;
        char nomeJogador[100];
        int tamNacionalidade;
        char nacionalidade[100];
        int tamNomeClube;
        char nomeClube[100];
    } DADOS_FIXOS;

    // Armazena informações sobre os campos de busca
    typedef struct {
        char nomeCampo[MAX_CAMPO];
        int valorInt;
        char valorString[MAX_CAMPO];
    } CAMPO_BUSCA;

    // Registro de index do cabeçalho
    typedef struct {
        char status;
    } CABECALHO_INDEX;

    // Registro de index do registro
    typedef struct {
        int id;
        int64_t byteOffset;
    } REGISTRO_INDEX;

    // Estrutura para armazenar as listas
    typedef struct LISTA {
        struct LISTA *prox;
        int tamRegistro;
        int64_t byteOffset;
    } LISTA;

#endif
