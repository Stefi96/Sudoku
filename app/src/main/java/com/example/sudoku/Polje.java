package com.example.sudoku;

public class Polje {

    private int x,y;
    private String broj;
    private boolean fixed;


    public Polje() {  }

    public Polje(int x, int y, String broj, boolean fixed) {
        this.broj = broj;
        this.x = x;
        this.y = y;
        this.fixed = fixed;
    }

    public String getBroj() {
        return broj;
    }

    public void setBroj(String broj) {
        this.broj = broj;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
}
