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
#include <limits.h>

// Função que lê o cabeçalho
void LeituraCabecalho(CABECALHO* cabecalho, FILE* arquivoBinario) {
    fseek(arquivoBinario, 0, SEEK_SET);
    fread(&cabecalho->status,         sizeof(cabecalho->status),         1, arquivoBinario);
    fread(&cabecalho->topo,           sizeof(cabecalho->topo),           1, arquivoBinario);    
    fread(&cabecalho->proxByteOffset, sizeof(cabecalho->proxByteOffset), 1, arquivoBinario);  
    fread(&cabecalho->nroRegArq,      sizeof(cabecalho->nroRegArq),      1, arquivoBinario);  
    fread(&cabecalho->nroRegRem,      sizeof(cabecalho->nroRegRem),      1, arquivoBinario);
}

// Função que lê o registro
void LeituraRegistro(DADOS* registro, FILE* arquivoBinario){
    fread(&registro->removido,        sizeof(registro->removido),         1,                          arquivoBinario);
    fread(&registro->tamanhoRegistro, sizeof(registro->tamanhoRegistro),  1,                          arquivoBinario);
    fread(&registro->prox,            sizeof(registro->prox),             1,                          arquivoBinario);   
    fread(&registro->id,              sizeof(registro->id),               1,                          arquivoBinario);   
    fread(&registro->idade,           sizeof(registro->idade),            1,                          arquivoBinario);
    fread(&registro->tamNomeJog,      sizeof(registro->tamNomeJog),       1,                          arquivoBinario);
    fread(&registro->nomeJogador,     sizeof(char),                      registro->tamNomeJog,        arquivoBinario);
    registro->nomeJogador[registro->tamNomeJog] = '\0';     
    fread(&registro->tamNacionalidade,sizeof(registro->tamNacionalidade), 1,                          arquivoBinario);
    fread(&registro->nacionalidade,   sizeof(char),                      registro->tamNacionalidade,  arquivoBinario);
    registro->nacionalidade[registro->tamNacionalidade] = '\0';
    fread(&registro->tamNomeClube,    sizeof(registro->tamNomeClube),     1,                          arquivoBinario);
    fread(&registro->nomeClube,       sizeof(char),                      registro->tamNomeClube,      arquivoBinario);
    registro->nomeClube[registro->tamNomeClube] = '\0';
}

// Função que lê o registro 
void LeituraRegistroFixo(DADOS_FIXOS* registro, FILE* arquivoBinario){
    fread(&registro->removido,        sizeof(registro->removido),         1,                          arquivoBinario);
    fread(&registro->tamanhoRegistro, sizeof(registro->tamanhoRegistro),  1,                          arquivoBinario);  
    fread(&registro->prox,            sizeof(registro->prox),             1,                          arquivoBinario);   
    fread(&registro->id,              sizeof(registro->id),               1,                          arquivoBinario);   
    fread(&registro->idade,           sizeof(registro->idade),            1,                          arquivoBinario);
    fread(&registro->tamNomeJog,      sizeof(registro->tamNomeJog),       1,                          arquivoBinario);
    registro->nomeJogador[registro->tamNomeJog] ='\0';     

    fread(&registro->nomeJogador,     sizeof(char),                      registro->tamNomeJog,        arquivoBinario);
    
    fread(&registro->tamNacionalidade,sizeof(registro->tamNacionalidade), 1,                          arquivoBinario);
    registro->nacionalidade[registro->tamNacionalidade] ='\0';

    fread(&registro->nacionalidade,   sizeof(char),                      registro->tamNacionalidade,  arquivoBinario);
    
    fread(&registro->tamNomeClube,    sizeof(registro->tamNomeClube),     1,                          arquivoBinario);
    registro->nomeClube[registro->tamNomeClube] ='\0';

    fread(&registro->nomeClube,       sizeof(char),                      registro->tamNomeClube,      arquivoBinario);
}