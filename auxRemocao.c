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

// Inicialização do registro
void InicializaRegistroJogador(DADOS* registro) {
    registro->removido = '0'; 
    registro->tamanhoRegistro = 0;
    registro->prox = -1;
    registro->id = -1;
    registro->idade = -1;
    registro->tamNomeJog = 0;
    registro->nomeJogador = NULL;
    registro->tamNacionalidade = 0;
    registro->nacionalidade = NULL;
    registro->tamNomeClube = 0;
    registro->nomeClube = NULL;
}

// Alocação da memória para a struct DADOS
void AlocaMemoriaRegistro(DADOS* registro) {
    registro->nomeClube = (char*)malloc(50*sizeof(char));
    registro->nacionalidade = (char*)malloc(50*sizeof(char));
    registro->nomeJogador = (char*)malloc(50*sizeof(char));
}

// Função que lê os registro no contexto da funcionalidade de remoção
int LeituraRemocao(FILE* arquivoBinario, DADOS* registro) {
    fread(&(registro->tamanhoRegistro), 4, 1, arquivoBinario);
    fread(&(registro->removido), 1, 1, arquivoBinario);

    // Se o registro foi removido, retorna false
    if (registro->removido == '1') {
        fseek(arquivoBinario, registro->tamanhoRegistro - 5, SEEK_CUR);
        return false;
    }

    fread(&(registro->prox), sizeof(int64_t), 1, arquivoBinario);
    fread(&(registro->id), sizeof(int), 1, arquivoBinario);
    fread(&(registro->idade), sizeof(int), 1, arquivoBinario);
    fread(&(registro->tamNomeJog), sizeof(int), 1, arquivoBinario);
    fread(registro->nomeJogador, registro->tamNomeJog, 1, arquivoBinario);
    (registro->nomeJogador)[registro->tamNomeJog] = '\0';
    fread(&(registro->tamNacionalidade), sizeof(int), 1, arquivoBinario);
    fread(registro->nacionalidade, registro->tamNacionalidade, 1, arquivoBinario);
    (registro->nacionalidade)[registro->tamNacionalidade] = '\0';
    fread(&(registro->tamNomeClube), sizeof(int), 1, arquivoBinario);
    fread(registro->nomeClube, registro->tamNomeClube, 1, arquivoBinario);
    (registro->nomeClube)[registro->tamNomeClube] = '\0';

    return 1;
}

// Função que retorna o byte OffSet do último registro removido
int64_t RetornaUltimoRemovido(FILE* arquivoBinario) {
    if (arquivoBinario == NULL) {
        printf("Erro: ponteiro do arquivo é NULL\n");
        return -1;
    }

    // Inicia como -1
    int64_t ultimoRemovido = -1;

    // Pula o status
    if (fseek(arquivoBinario, 1, SEEK_SET) != 0) {
        printf("Erro: fseek falhou ao pular o status\n");
        return -1;
    }

    // Armazena o topo
    int64_t topo;
    if (fread(&topo, sizeof(int64_t), 1, arquivoBinario) != 1) {
        printf("Erro: fread falhou ao ler o topo\n");
        return -1;
    }

    // Verifica se há registros removidos (topo == -1)
    if(topo == -1){
        return ultimoRemovido;
    } else {
        ultimoRemovido = topo;
    }

    // Vai para o topo
    if (fseek(arquivoBinario, topo, SEEK_SET) != 0) {
        printf("Erro: fseek falhou ao pular para o topo\n");
        return -1;
    }

    DADOS registro_dados;
    AlocaMemoriaRegistro(&registro_dados);

    int64_t posicaoNoArquivo = 0;

    // Continua até encontrar o último removido
    while(registro_dados.prox != -1){
        posicaoNoArquivo = ftell(arquivoBinario);

        fread(&registro_dados.removido, sizeof(char), 1, arquivoBinario);
        fread(&registro_dados.tamanhoRegistro, sizeof(int), 1, arquivoBinario);
        fread(&registro_dados.prox, sizeof(int64_t), 1, arquivoBinario);

        // pula 8 bytes
        fseek(arquivoBinario, 8, SEEK_CUR);
        fread(&registro_dados.tamNomeJog, sizeof(int), 1, arquivoBinario);

        if (registro_dados.tamNomeJog > 0) {
            fread(registro_dados.nomeJogador, sizeof(char), registro_dados.tamNomeJog, arquivoBinario);
            registro_dados.nomeJogador[registro_dados.tamNomeJog] = '\0';
        }

        fread(&registro_dados.tamNacionalidade, sizeof(int), 1, arquivoBinario);

        if (registro_dados.tamNacionalidade > 0){
            fread(registro_dados.nacionalidade, sizeof(char), registro_dados.tamNacionalidade, arquivoBinario);
            registro_dados.nacionalidade[registro_dados.tamNacionalidade] = '\0';
        }

        fread(&registro_dados.tamNomeClube, sizeof(int), 1, arquivoBinario);

        if (registro_dados.tamNomeClube > 0){
            fread(registro_dados.nomeClube, sizeof(char), registro_dados.tamNomeClube, arquivoBinario);
            registro_dados.nomeClube[registro_dados.tamNomeClube] = '\0';
        }

        topo = registro_dados.prox;
        fseek(arquivoBinario, registro_dados.prox, SEEK_SET);
    }

    // Libera a memória alocada para os campos variáveis
    free(registro_dados.nacionalidade);
    free(registro_dados.nomeClube);
    free(registro_dados.nomeJogador);

    return posicaoNoArquivo;
}

// Libera memória do nome do campo e valor do campo para uma busca
void LiberaMemoriaChar(char** nomeCampo, char** valorCampo, int numCamposBusca){
    for (int i = 0; i < numCamposBusca; i++) {
        free(nomeCampo[i]);
        free(valorCampo[i]);
    }
    free(nomeCampo);
    free(valorCampo);
}

// Dá free nos campos variáveis
void DesalocaMemoriaReg(DADOS* registro){
    free(registro->nomeJogador);
    free(registro->nacionalidade);
    free(registro->nomeClube);
}

// Recebe o nome do campo e seu respectivo valor em uma busca
void LeituraParametrosBusca(int numCamposBusca, char** nomeCampo, char** valorCampo){
    for (int i = 0; i < numCamposBusca; i++) {
        scanf("%s", nomeCampo[i]);

        if (strcmp(nomeCampo[i], "id") == 0 || strcmp(nomeCampo[i], "idade") == 0)
            
            scanf("%s", valorCampo[i]);
        else
            scan_quote_string(valorCampo[i]);
    }
}