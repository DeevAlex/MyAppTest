package com.example.myapptest;

import java.util.Date;

public class Produto {

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

    public boolean isStatusProduto() {
        return statusProduto;
    }

    public void setStatusProduto(boolean statusProduto) {
        this.statusProduto = statusProduto;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    int id;
    String nome;
    int quantidade;  // Alterar para int
    boolean statusProduto;
    Date dataEntrada;

    public Produto(int id, String nome, int quantidade, boolean statusProduto) {  // Altere o tipo do parâmetro para int
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.statusProduto = statusProduto;
    }

}
