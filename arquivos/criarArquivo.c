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

// Função para criar o arquivo binário
void criarArquivoBinario(char* nomeArquivoCSV, char* nomeArquivoBinario){

    // Abertura dos arquivos
    FILE* arquivoCSV = fopen(nomeArquivoCSV, "r");
    if (arquivoCSV == NULL){
        printf("Falha no processamento do arquivo.\n");
        return;
    }

    // Abre para escrita
    FILE* arquivoBinario = fopen(nomeArquivoBinario, "wb");
    if (arquivoBinario == NULL){
        printf("Falha no processamento do arquivo.\n");
        fclose(arquivoCSV);
        return;
    }

    // Inicialização do cabeçalho
    CABECALHO cabecalho;

    cabecalho.status = '0';
    cabecalho.topo = -1;
    cabecalho.proxByteOffset = 25; // Tamanho do cabeçalho
    cabecalho.nroRegArq = 0;
    cabecalho.nroRegRem = 0;

    // Escrita de cada campo do cabeçalho no arquivo binário (sequencialmente)
    EscritaCabecalho(&cabecalho, arquivoBinario);

    // Variáveis para armazenar dados do registro
    DADOS_FIXOS registro;

    // Pula a primeira linha do CSV
    char linha[45];
    fgets(linha, 45, arquivoCSV);

    // Só para o loop quando encontrar o fim do arquivo
    while(!feof(arquivoCSV)){
        char string_aux[40];
        char c;
        int i;

        registro.tamNomeJog = 0;
        registro.tamNacionalidade = 0;
        registro.tamNomeClube = 0;

        // Id
        int linha_vazia = 0;
        i=0;
        while((c = getc(arquivoCSV)) != ','){
            if(c == EOF){ 
                linha_vazia = 1;
                break;
            }
            string_aux[i] = c;
            i++;
        }
        if (linha_vazia) break;
        string_aux[i] = '\0';
        registro.id = atoi(string_aux);
        if (registro.id == 0) registro.id = -1;

        // Idade
        i=0;
        while((c = getc(arquivoCSV)) != ','){
            string_aux[i] = c;
            i++;
        }
        string_aux[i] = '\0';
        registro.idade = atoi(string_aux);
        if (registro.idade == 0) registro.idade = -1;


        // Nome jogador
        i=0;
        while((c = getc(arquivoCSV)) != ','){
            registro.nomeJogador[i] = c;
            registro.tamNomeJog++;
            i++;
        }

        // Nacionalidade
        i=0;
        while((c = getc(arquivoCSV)) != ','){
            registro.nacionalidade[i] = c;
            registro.tamNacionalidade++;
            i++;
        }        

        // Nome do clube
        i=0;
        while(((c = getc(arquivoCSV)) != '\n' && c != EOF) && (c != '\r' && c != EOF)){
            registro.nomeClube[i] = c;
            registro.nomeClube[i] = c;
            registro.tamNomeClube++;
            i++;
        }

        // Cálculo e atribuição do tamanho do registro
        registro.tamanhoRegistro = 33 + registro.tamNomeJog + registro.tamNacionalidade + registro.tamNomeClube;
        
        // Definir registro como não removido logicamente
        registro.removido = '0';

        // Não há próximo registro que foi removido
        registro.prox = -1;

        // Escrita de cada campo do registro no arquivo binário (sequencialmente)
        EscritaRegistroFixo(&registro, arquivoBinario);

        // Atualização do offset do próximo byte
        cabecalho.proxByteOffset += registro.tamanhoRegistro;

        // Incrementação do número de registros
        cabecalho.nroRegArq++;

    }
    // Deu tudo certo
    cabecalho.status = '1';

    // Reescrita do cabeçalho com as informações corretas
    fseek(arquivoBinario, 0, SEEK_SET);
    
    // escrita do cabeçalho no arquivo binário
    EscritaCabecalho(&cabecalho, arquivoBinario);

    // Fechamento dos arquivos
    fclose(arquivoCSV);
    fclose(arquivoBinario);

    return;
}