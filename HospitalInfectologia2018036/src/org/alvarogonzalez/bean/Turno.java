package org.alvarogonzalez.bean;

import java.util.Date;

public class Turno {
    private int codigoTurno;
    private Date fechaTurno;
    private Date fechaCita;
    private Double valorCita;
    private int codigoResponsableTurno;
    private int codigoMedicoEspecialidad;
    private int codigoPaciente;

    public Turno() {
    }

    public Turno(int codigoTurno, Date fechaTurno, Date fechaCita, Double valorCita, int codigoResponsableTurno, int codigoMedicoEspecialidad, int codigoPaciente) {
        this.codigoTurno = codigoTurno;
        this.fechaTurno = fechaTurno;
        this.fechaCita = fechaCita;
        this.valorCita = valorCita;
        this.codigoResponsableTurno = codigoResponsableTurno;
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
        this.codigoPaciente = codigoPaciente;
    }

    public int getCodigoTurno() {
        return codigoTurno;
    }

    public void setCodigoTurno(int codigoTurno) {
        this.codigoTurno = codigoTurno;
    }

    public Date getFechaTurno() {
        return fechaTurno;
    }

    public void setFechaTurno(Date fechaTurno) {
        this.fechaTurno = fechaTurno;
    }

    public Date getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(Date fechaCita) {
        this.fechaCita = fechaCita;
    }

    public Double getValorCita() {
        return valorCita;
    }

    public void setValorCita(Double valorCita) {
        this.valorCita = valorCita;
    }

    public int getCodigoResponsableTurno() {
        return codigoResponsableTurno;
    }

    public void setCodigoResponsableTurno(int codigoResponsableTurno) {
        this.codigoResponsableTurno = codigoResponsableTurno;
    }

    public int getCodigoMedicoEspecialidad() {
        return codigoMedicoEspecialidad;
    }

    public void setCodigoMedicoEspecialidad(int codigoMedicoEspecialidad) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
    }

    public int getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(int codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }
}
