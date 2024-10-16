package com.example.myapptest;

public class Usuario {

    private int id;
    private String email;
    private String senha;

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    private String cargo;

    public boolean isLembrarSenha() {
        return lembrarSenha;
    }

    public void setLembrarSenha(boolean lembrarSenha) {
        this.lembrarSenha = lembrarSenha;
    }

    private boolean lembrarSenha;

    public Usuario(int id, String email, String senha, boolean lembrarSenha, String cargo) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.lembrarSenha = lembrarSenha;
        this.cargo = cargo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }

}
