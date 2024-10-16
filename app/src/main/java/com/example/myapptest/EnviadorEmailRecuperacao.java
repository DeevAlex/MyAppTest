package com.example.myapptest;

import java.security.SecureRandom;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

public class EnviadorEmailRecuperacao {

    private String userEmail;
    private String password;
    private Context context;

    public EnviadorEmailRecuperacao(String userEmail, String password, Context context) {
        this.userEmail = userEmail;
        this.password = password;

        this.context = context;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void sendRecoveryEmail(String toEmail, String recoveryCode) {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            final String emailEnviador = context.getString(R.string.emailEnviador);
            final String senhaEnviador = context.getString(R.string.senhaEnviador);
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailEnviador, senhaEnviador);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("classicrankup@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("AMAZEN: Email de Recuperação de Senha");

            String body = String.format(
                    "<html>" +
                            "    <body style='height: 100%%; margin: 0; padding: 0;'>" +
                            "        <div style='margin: 20px auto; width: 500px; font-family: Arial, sans-serif;'>" +
                            "            <h1 style='text-align: center;'>Recuperação de Conta</h1>" +
                            "            <p>Olá, %s</p>" +
                            "            <p>Seu código de recuperação de conta é: <strong style='font-size: 18px;'>%s</strong></p>" +
                            "            <p>Use este código para recuperar o acesso à sua conta.</p>" +
                            "            <p>Se você não solicitou este código, por favor ignore este e-mail.</p>" +
                            "            <br>" +
                            "            <p>Atenciosamente,</p>" +
                            "            <p>Equipe de Suporte</p>" +
                            "        </div>" +
                            "    </body>" +
                            "</html>",
                    toEmail, recoveryCode
            );

            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void enviaEmail(String destinatario, String codigoGerado) {
        sendRecoveryEmail(destinatario, codigoGerado);
    }

    public String generateRecoveryCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }

}


