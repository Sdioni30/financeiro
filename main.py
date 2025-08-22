import pandas as pd
from enviar_email import enviar_emails
from dotenv import load_dotenv
import os

# Carrega as variáveis de ambiente do arquivo .env
load_dotenv()

# Define o menu de opções
menu_de_opcoes = {1: "Salão", 2: "Casa"}
# Listas para armazenar os registros
registros_entradas_salao = []
registros_saidas_salao = []
registros_entradas_casa = []
registros_saidas_casa = []

def registrar(local_do_registro, tipo_de_registro):
    """
    Registra uma entrada ou saída de acordo com o local e tipo.
    """
    if tipo_de_registro == 'entrada':
        print("Você escolheu registrar uma entrada.\n")
    elif tipo_de_registro == 'saida':
        print(f"Você escolheu registrar uma {tipo_de_registro}.\n")
    else:
        print("Tipo de registro inválido.\n")
        return

    nome = input(f'Digite o nome do item de {tipo_de_registro}: ')
    # Tratamento de erro para garantir que o valor seja um número
    try:
        valor_do_item = float(input('Digite o valor do item: \n'))
    except ValueError:
        print("Valor inválido. Por favor, digite um número.\n")
        return

    # O dicionário é criado com a chave 'Valor', não 'Valor entrada' ou 'Valor saida'
    registro = {'Nome': nome, 'Valor': valor_do_item}

    print(registro)

    if local_do_registro == 1:
        if tipo_de_registro == 'entrada':
            registros_entradas_salao.append(registro)
        else:
            registros_saidas_salao.append(registro)
    elif local_do_registro == 2:
        if tipo_de_registro == 'entrada':
            registros_entradas_casa.append(registro)
        else:
            registros_saidas_casa.append(registro)

    print(f"Registro de {tipo_de_registro} realizado com sucesso!\n")


def exportar_para_excel(local_nome, entradas_list, saidas_list, writer):
    """
    Exporta os dados de um local específico para uma planilha Excel.
    """
    print(f"Exportando dados para a planilha '{local_nome}'...")
    
    # Criando DataFrames a partir das listas
    df_entradas = pd.DataFrame(entradas_list)
    df_saidas = pd.DataFrame(saidas_list)
    
    # Define a linha inicial para o próximo DataFrame
    start_row = 0
    
    # Se houver dados de entrada, escreve no Excel e calcula o total
    if not df_entradas.empty:
        df_entradas.to_excel(writer, sheet_name=local_nome, index=False)
        total_entradas = df_entradas['Valor'].sum()
        writer.sheets[local_nome].cell(row=len(df_entradas) + 2, column=1, value='TOTAL ENTRADAS')
        writer.sheets[local_nome].cell(row=len(df_entradas) + 2, column=2, value=total_entradas)
        # Atualiza a linha de início para as saídas
        start_row = len(df_entradas) + 3

    # Se houver dados de saída, escreve no Excel
    if not df_saidas.empty:
        df_saidas.to_excel(writer, sheet_name=local_nome, startrow=start_row, index=False)
        total_saidas = df_saidas['Valor'].sum()
        writer.sheets[local_nome].cell(row=start_row + len(df_saidas) + 2, column=1, value='TOTAL SAÍDAS')
        writer.sheets[local_nome].cell(row=start_row + len(df_saidas) + 2, column=2, value=total_saidas)

    '''# Adiciona a linha de saldo final, se houver dados
    if not df_entradas.empty or not df_saidas.empty:
        total_entradas = df_entradas['Valor'].sum() if not df_entradas.empty else 0
        total_saidas = df_saidas['Valor'].sum() if not df_saidas.empty else 0
        saldo_final = total_entradas - total_saidas
        writer.sheets[local_nome].cell(row=start_row + len(df_saidas) + 4, column=1, value='SALDO FINAL')
        writer.sheets[local_nome].cell(row=start_row + len(df_saidas) + 4, column=2, value=saldo_final)'''


while True:
    pergunta = input('Digite 1 para Salão, 2 para Casa, ou 0 para sair: \n').strip()

    if pergunta == '0':
        break

    if pergunta not in ['1', '2']:
        print("Opção inválida. Tente novamente.\n")
        continue

    pergunta = int(pergunta)
    print(f"Você escolheu: {menu_de_opcoes[pergunta]}")

    tipo = input('Digite "E" para entrada ou "D" para despesa: \n').strip().upper()

    if tipo == 'E':
        registrar(pergunta, 'entrada')
    elif tipo == 'D':
        registrar(pergunta, 'saida')
    else:
        print("Opção inválida! Digite E ou D.\n")
        continue

    pergunta_continuar = input("Digite 'S' para continuar, ou 'N' para parar: \n").strip().upper()
    if pergunta_continuar == 'N':
        break

# Exportação para o Excel
nome_arquivo_excel = "controle_financeiro.xlsx"
try:
    with pd.ExcelWriter(nome_arquivo_excel, engine='openpyxl') as writer:
        # Salão
        exportar_para_excel("Salão", registros_entradas_salao, registros_saidas_salao, writer)
        
        # Casa
        exportar_para_excel("Casa", registros_entradas_casa, registros_saidas_casa, writer)
    
    print(f"\n✅ Dados salvos em '{nome_arquivo_excel}'")
    
    # Adiciona a pergunta para enviar o e-mail
    pergunta_email = input("Deseja enviar o arquivo Excel por e-mail? (S/N): \n").strip().upper()

    if pergunta_email == 'S':
        remetente = os.getenv("EMAIL")  
        destinatario = input("Para qual e-mail você deseja enviar o arquivo? \n")
        assunto = "Relatório de Controle Financeiro"
        corpo = "Olá! Segue em anexo o relatório financeiro gerado pelo sistema Python."
        enviar_emails(remetente, destinatario, assunto, corpo, nome_arquivo_excel)

except IndexError:
    # Captura o erro caso não haja dados para escrever nas planilhas
    print("\n⚠️ Não foi possível salvar o arquivo Excel pois nenhuma entrada ou saída foi registrada.")
except Exception as e:
    print(f"\n❌ Ocorreu um erro ao salvar o arquivo Excel: {e}")