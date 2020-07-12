package org.alvarogonzalez.bean;

// Inicio de la clase Médico
public class Medico {
    private int codigoMedico;
    private int licenciaMedica;
    private String nombres;
    private String apellidos;
    private String horaEntrada;
    private String horaSalida;
    private int turnoMaximo;
    private String sexo;
    
    public Medico(){
    }

    public Medico(int codigoMedico, int licenciaMedica, String nombres, String apellidos, String horaEntrada, String horaSalida, int turnoMaximo, String sexo) {
        this.codigoMedico = codigoMedico;
        this.licenciaMedica = licenciaMedica;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.turnoMaximo = turnoMaximo;
        this.sexo = sexo;
    }

    public int getCodigoMedico() {
        return codigoMedico;
    }

    public void setCodigoMedico(int codigoMedico) {
        this.codigoMedico = codigoMedico;
    }

    public int getLicenciaMedica() {
        return licenciaMedica;
    }

    public void setLicenciaMedica(int licenciaMedica) {
        this.licenciaMedica = licenciaMedica;
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

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getTurnoMaximo() {
        return turnoMaximo;
    }

    public void setTurnoMaximo(int turnoMaximo) {
        this.turnoMaximo = turnoMaximo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }   
        
    public String toString(){
        return getCodigoMedico() + " | " + getNombres() + ", " + getApellidos();
    }
    
}
