package com.example.myapptest;

public class Item {
    private int id;
    private String nome;
    private int quantidade;
    private int centroId;

    public Item(int id, String nome, int quantidade, int centroId) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.centroId = centroId;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getCentroId() {
        return centroId;
    }

    public void setCentroId(int centroId) {
        this.centroId = centroId;
    }

    @Override
    public String toString() {
        return nome + " - Quantidade: " + quantidade;
    }

}
