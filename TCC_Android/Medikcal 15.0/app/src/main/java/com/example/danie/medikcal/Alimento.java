package com.example.danie.medikcal;

public class Alimento {


    private String nomeAlimento;
    private String minusculaNome;
    private float valorKcal;
    private float valorProt;
    private float valorCarb;
    private float valorGord;
    private float valorFibras;


    public Alimento() {
    }

    public String getNomeAlimento() {
        return nomeAlimento;
    }

    public void setNomeAlimento(String nomeAlimento) {
        this.nomeAlimento = nomeAlimento;
    }

    public String getMinusculaNome() {
        return minusculaNome;
    }

    public void setMinusculaNome(String minusculaNome) {
        this.minusculaNome = minusculaNome;
    }

    public float getValorKcal() {
        return valorKcal;
    }

    public void setValorKcal(float valorKcal) {
        this.valorKcal = valorKcal;
    }

    public float getValorProt() {
        return valorProt;
    }

    public void setValorProt(float valorProt) {
        this.valorProt = valorProt;
    }

    public float getValorCarb() {
        return valorCarb;
    }

    public void setValorCarb(float valorCarb) {
        this.valorCarb = valorCarb;
    }

    public float getValorGord() {
        return valorGord;
    }

    public void setValorGord(float valorGord) {
        this.valorGord = valorGord;
    }

    public float getValorFibras() {
        return valorFibras;
    }

    public void setValorFibras(float valorFibras) {
        this.valorFibras = valorFibras;
    }
}
