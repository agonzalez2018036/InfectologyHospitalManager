package org.alvarogonzalez.bean;

public class ContactoUrgencia {
    private int codigoContactoUrgencia;
    private String nombres;
    private String apellidos;
    private String numeroContacto;
    private int codigoPaciente;

    public ContactoUrgencia(int codigoContactoUrgencia, String nombres, String apellidos, String numeroContacto, int codigoPaciente) {
        this.codigoContactoUrgencia = codigoContactoUrgencia;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.numeroContacto = numeroContacto;
        this.codigoPaciente = codigoPaciente;
    }

    public ContactoUrgencia() {
    }

    public int getCodigoContactoUrgencia() {
        return codigoContactoUrgencia;
    }

    public void setCodigoContactoUrgencia(int codigoContactoUrgencia) {
        this.codigoContactoUrgencia = codigoContactoUrgencia;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    public int getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(int codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }
    
        
}
