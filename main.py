import pandas as pd
import os
from enviar_email import enviar_emails  # sua função modularizada


registros_entradas_salao = []
registros_saidas_salao = []
registros_saidas_casa = []
registros_agendamentos = []


def menu_salao():
    while True:
        print("\n📍 MENU SALÃO")
        print("1 - Registrar entrada")
        print("2 - Registrar despesa")
        print("0 - Voltar")
        opcao = input("Escolha uma opção: ")

        if opcao == "1":
            descricao = input("Descrição da entrada: ")
            valor = float(input("Valor da entrada: "))
            registros_entradas_salao.append({"Nome": descricao, "Valor": valor})
        elif opcao == "2":
            descricao = input("Descrição da despesa: ")
            valor = float(input("Valor da despesa: "))
            registros_saidas_salao.append({"Nome": descricao, "Valor": valor})
        elif opcao == "0":
            break
        else:
            print("⚠️ Opção inválida!")

def menu_casa():
    while True:
        print("\n🏠 MENU CASA")
        print("1 - Registrar despesa")
        print("0 - Voltar")
        opcao = input("Escolha uma opção: ")

        if opcao == "1":
            descricao = input("Descrição da despesa: ")
            valor = float(input("Valor da despesa: "))
            registros_saidas_casa.append({"Nome": descricao, "Valor": valor})
        elif opcao == "0":
            break
        else:
            print("⚠️ Opção inválida!")

def agendar_horarios():
    while True:
        data = input("Digite a data (DD/MM) ou digite 0 para voltar ao menu principal: ")
        if data == "0":
            break
        horario = input("Digite o horário (HH:MM): ")
        nome_cliente = input("Nome do cliente: ")
        procedimentos = input("Procedimentos")
        registros_agendamentos.append({
            "Data": data,
            "Horário": horario,
            "Cliente": nome_cliente,
            "Procedimentos": procedimentos
        })

        print(f"✅ Compromisso agendado para {data} às {horario}: {nome_cliente}")


def exportar_agendamentos(writer):
    if not registros_agendamentos:
        print("⚠️ Nenhum agendamento para exportar.")
        return

    print("Exportando agendamentos para planilha 'Agendamentos'...")
    df_agendamentos = pd.DataFrame(registros_agendamentos)
    df_agendamentos.to_excel(writer, sheet_name="Agendamentos", index=False)


def exportar_para_excel(local_nome, entradas_list, saidas_list, writer):
    print(f"Exportando dados para a planilha '{local_nome}'...")

    df_entradas = pd.DataFrame(entradas_list)
    df_saidas = pd.DataFrame(saidas_list)

    start_row = 0

    if not df_entradas.empty:
        df_entradas.to_excel(writer, sheet_name=local_nome, index=False)
        total_entradas = df_entradas['Valor'].sum()
        ws = writer.sheets[local_nome]
        ws.cell(row=len(df_entradas) + 2, column=1, value='TOTAL ENTRADAS')
        ws.cell(row=len(df_entradas) + 2, column=2, value=total_entradas)
        start_row = len(df_entradas) + 3

    if not df_saidas.empty:
        df_saidas.to_excel(writer, sheet_name=local_nome, startrow=start_row, index=False)
        total_saidas = df_saidas['Valor'].sum()
        ws = writer.sheets[local_nome]
        ws.cell(row=start_row + len(df_saidas) + 2, column=1, value='TOTAL SAÍDAS')
        ws.cell(row=start_row + len(df_saidas) + 2, column=2, value=total_saidas)

    if not df_entradas.empty or not df_saidas.empty:
        total_entradas = df_entradas['Valor'].sum() if not df_entradas.empty else 0
        total_saidas = df_saidas['Valor'].sum() if not df_saidas.empty else 0
        saldo_final = total_entradas - total_saidas
        ws.cell(row=start_row + len(df_saidas) + 4, column=1, value='SALDO FINAL')
        ws.cell(row=start_row + len(df_saidas) + 4, column=2, value=saldo_final)

# ===================== PROGRAMA PRINCIPAL =====================
while True:
    print("\n===== MENU PRINCIPAL =====")
    print("1 - Menu Salão")
    print("2 - Menu Casa")
    print("3 - Agendar horários")
    print("4 - Exportar Excel e enviar e-mail")
    print("0 - Sair")

    escolha = input("Escolha uma opção: ")

    if escolha == "1":
        menu_salao()
    elif escolha == "2":
        menu_casa()
    elif escolha == "3":
        agendar_horarios()
    elif escolha == "4":
        nome_arquivo_excel = "controle_financeiro.xlsx"
        try:
            with pd.ExcelWriter(nome_arquivo_excel, engine="openpyxl") as writer:
                exportar_para_excel("Salão", registros_entradas_salao, registros_saidas_salao, writer)
                exportar_para_excel("Casa", [], registros_saidas_casa, writer)
                exportar_agendamentos(writer)

            print(f"\n✅ Dados salvos em '{nome_arquivo_excel}'")

            pergunta_email = input("Deseja enviar o arquivo Excel por e-mail? (S/N): \n").strip().upper()
            if pergunta_email == "S":
                remetente = os.getenv("EMAIL")
                destinatario = os.getenv("EMAIL_DESTINATARIO")
                senha = os.getenv("SENHA")
                assunto = "Relatório de Controle Financeiro"
                corpo = "Olá! Segue em anexo o relatório financeiro gerado pelo sistema Python."

                if not remetente or not destinatario or not senha:
                    print("⚠️Erro no .env")
                else:
                    enviar_emails(remetente, destinatario, assunto, corpo, nome_arquivo_excel, senha)

        except Exception as e:
            print(f"\n❌ Ocorreu um erro ao salvar o arquivo Excel: {e}")
    elif escolha == "0":
        print("Saindo...")
        break
    else:
        print("⚠️ Opção inválida!")

    
