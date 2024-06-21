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

// Função para buscar registros em um arquivo de dados
void buscarRegistros(const char *arquivoEntrada, int numBuscas) {
   
    FILE* entrada = fopen(arquivoEntrada, "rb");
    if(entrada == NULL){
        printf("Falha no processamento do arquivo.\n");
        return;
    }

    CABECALHO cabecalho;

    // Lê o cabeçalho campo a campo
    LeituraCabecalho(&cabecalho, entrada);

    // Verifica a consistência do arquivo
    if(cabecalho.status == '0'){
        printf("Arquivo inconsistente.\n");
        fclose(entrada);
        return;
    }

    // variáveis para armazenar os campos de busca
    CAMPO_BUSCA camposBusca[MAX_CAMPO];
    int numCamposBusca;

    DADOS_FIXOS registro;
    int encontrado;

    // Loop para cada busca
    for (int busca = 0; busca < numBuscas; busca++) {
        printf("Busca %d\n\n", busca + 1);
        encontrado = 0;

        // Leitura do número de campos 
        scanf("%d", &numCamposBusca);
        getchar(); // limpa o buffer do '\n'

        // valida do número de campos
        if (numCamposBusca > MAX_CAMPO) {
            printf("Número máximo de campos excedido.\n");
            continue;
        }

        // Loop para cada campo
        for (int i = 0; i < numCamposBusca; i++) {
            // leitura do nome do campo
            scanf("%s", camposBusca[i].nomeCampo);
            getchar(); 

            // Validação do nome do campo
            if (!validarNomeCampo(camposBusca[i].nomeCampo)) { 
                printf("Nome de campo inválido.\n");
                continue;
            }

            // Lê o valor do campo
            if (strcmp(camposBusca[i].nomeCampo, "id") == 0 || strcmp(camposBusca[i].nomeCampo, "idade") == 0) {
                // Campo de inteiro
                scanf("%d", &camposBusca[i].valorInt); // lê do inteiro
            } else {
                // Campo de string
                scan_quote_string(camposBusca[i].valorString); // lê da string
            }
        }

        // Voltar ao início do arquivo para iniciar a busca
        fseek(entrada, 25, SEEK_SET);

        // Busca os registros correspondentes
        for (int i = 0; i < cabecalho.nroRegArq + cabecalho.nroRegRem; i++) {
            
            // Leitura do registro
            LeituraRegistroFixo(&registro, entrada);

            if (registro.removido == '0') {
                if (todosCamposCorrespondemFixo(registro, camposBusca, numCamposBusca)) { // Verifica se um registro corresponde a todos os campos
                    encontrado = 1;
                    printf("Nome do Jogador: %s\n", registro.nomeJogador[0] ? registro.nomeJogador : "SEM DADO");
                    printf("Nacionalidade do Jogador: %s\n", registro.nacionalidade[0] ? registro.nacionalidade : "SEM DADO");
                    printf("Clube do Jogador: %s\n", registro.nomeClube[0] ? registro.nomeClube : "SEM DADO");
                    printf("\n");
                }
            }
        }

        // Mensagem caso nenhum registro seja encontrado
        if (!encontrado) {
            printf("Registro inexistente.\n\n");
        }
    }

    // Fechamento do arquivo
    fclose(entrada);
}