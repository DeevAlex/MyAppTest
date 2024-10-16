package com.example.myapptest;

public class Dados {

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isLembrar() {
        return lembrar;
    }

    public void setLembrar(boolean lembrar) {
        this.lembrar = lembrar;
    }

    public Dados(String email, String senha, boolean lembrar) {
        this.email = email;
        this.senha = senha;
        this.lembrar = lembrar;
    }

    private String email, senha;
    private boolean lembrar;

}
