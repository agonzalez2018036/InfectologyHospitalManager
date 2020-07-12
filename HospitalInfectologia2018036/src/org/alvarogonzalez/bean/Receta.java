package org.alvarogonzalez.bean;

public class Receta {
    private int codigoReceta;
    private String descripcionReceta;
    private int codigoControlCita;

    public Receta() {
    }

    public Receta(int codigoReceta, String descripcionReceta, int codigoControlCita) {
        this.codigoReceta = codigoReceta;
        this.descripcionReceta = descripcionReceta;
        this.codigoControlCita = codigoControlCita;
    }

    public int getCodigoReceta() {
        return codigoReceta;
    }

    public void setCodigoReceta(int codigoReceta) {
        this.codigoReceta = codigoReceta;
    }

    public String getDescripcionReceta() {
        return descripcionReceta;
    }

    public void setDescripcionReceta(String descripcionReceta) {
        this.descripcionReceta = descripcionReceta;
    }

    public int getCodigoControlCita() {
        return codigoControlCita;
    }

    public void setCodigoControlCita(int codigoControlCita) {
        this.codigoControlCita = codigoControlCita;
    }
    
}
