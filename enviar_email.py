import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders
from dotenv import load_dotenv
import os       

load_dotenv()


def enviar_emails(remetente, destinatario, assunto, corpo, anexo):
    # Configurações do servidor SMTP do Gmail
    smtp_server = "smtp.gmail.com"
    smtp_port = 587
    
    # Crie o objeto da mensagem
    msg = MIMEMultipart()
    msg['From'] = remetente
    msg['To'] = destinatario
    msg['Subject'] = assunto
    
    # Anexe o corpo do e-mail
    msg.attach(MIMEText(corpo, 'plain'))
    
    # Anexe o arquivo Excel
    try:
        with open(anexo, "rb") as attachment:
            part = MIMEBase("application", "octet-stream")
            part.set_payload(attachment.read())
        
        encoders.encode_base64(part)
        part.add_header("Content-Disposition", f"attachment; filename= {anexo}")
        msg.attach(part)
    except FileNotFoundError:
        print(f"Erro: O arquivo {anexo} não foi encontrado.\n")
        return
    
    # Conecte-se e envie o e-mail
    try:
        server = smtplib.SMTP(smtp_server, smtp_port)
        server.starttls()
        server.login(remetente, os.getenv("SENHA"))  # Use sua senha de app
        server.send_message(msg)
        server.quit()
        print("\n✅ E-mail enviado com sucesso!\n")
    except Exception as e:
        print(f"\n❌ Erro ao enviar o e-mail: {e}\n")