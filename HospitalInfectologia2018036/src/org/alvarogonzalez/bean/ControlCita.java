package org.alvarogonzalez.bean;

import java.util.Date;

public class ControlCita {
    private int codigoControlCita;
    private Date fecha;
    private String horaInicio;
    private String horaFin;
    private int codigoMedico;
    private int codigoPaciente;

    public ControlCita() {
    }

    public ControlCita(int codigoControlCita, Date fecha, String horaInicio, String horaFin, int codigoMedico, int codigoPaciente) {
        this.codigoControlCita = codigoControlCita;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.codigoMedico = codigoMedico;
        this.codigoPaciente = codigoPaciente;
    }

    public int getCodigoControlCita() {
        return codigoControlCita;
    }

    public void setCodigoControlCita(int codigoControlCita) {
        this.codigoControlCita = codigoControlCita;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public int getCodigoMedico() {
        return codigoMedico;
    }

    public void setCodigoMedico(int codigoMedico) {
        this.codigoMedico = codigoMedico;
    }

    public int getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(int codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }
    
    public String toString(){
        return getCodigoControlCita() + " | " + getFecha();
    }
    
}
