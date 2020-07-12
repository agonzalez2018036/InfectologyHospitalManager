package org.alvarogonzalez.controller;

//Librerias utilizadas
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.alvarogonzalez.sistema.Principal;

// Inicio del método MenuPrincipalController
public class MenuPrincipalController implements Initializable {
    private Principal escenarioPrincipal;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {     
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    //Método que levanta la ventana de Medicos
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }

    //Método que levanta la ventana de Telefonos Médico
    public void ventanaTelefonoMedico(){
        escenarioPrincipal.ventanaTelefonoMedico();
    }
    
    //Método que levanta la ventana de Pacientes
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }

    //Método que levanta la ventana de Contacto Urgencia
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
    
    //Método que levanta la ventana de Especialidades
    public void ventanaEspecialidades(){
        escenarioPrincipal.ventanaEspecialidades();
    }

    
    //Método que levanta la ventana de Programador
    public void ventanaProgramador(){
        escenarioPrincipal.ventanaProgramador();
    }
    
    public void ventanaResponsableTurno(){
        escenarioPrincipal.ventanaResponsableTurno();
    }
    
    public void ventanaHorario(){
        escenarioPrincipal.ventanaHorario();
    }
    
    public void ventanaMedicoEspecialidad(){
        escenarioPrincipal.ventanaMedicoEspecialidad();
    }
    
    public void ventanaTurno(){
        escenarioPrincipal.ventanaTurno();
    }
    
    public void ventanaControlCitas(){
        escenarioPrincipal.ventanaControlCitas();
    }
}
