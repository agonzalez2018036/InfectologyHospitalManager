package org.alvarogonzalez.sistema;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.alvarogonzalez.controller.AreaController;
import org.alvarogonzalez.controller.CargoController;
import org.alvarogonzalez.controller.ContactoUrgenciaController;
import org.alvarogonzalez.controller.ControlCitasController;
import org.alvarogonzalez.controller.EspecialidadController;
import org.alvarogonzalez.controller.HorarioController;
import org.alvarogonzalez.controller.MedicoController;
import org.alvarogonzalez.controller.MedicoEspecialidadController;
import org.alvarogonzalez.controller.MenuPrincipalController;
import org.alvarogonzalez.controller.PacienteController;
import org.alvarogonzalez.controller.ProgramadorController;
import org.alvarogonzalez.controller.RecetaController;
import org.alvarogonzalez.controller.ResponsableTurnoController;
import org.alvarogonzalez.controller.TelefonoMedicoController;
import org.alvarogonzalez.controller.TurnoController;


public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/alvarogonzalez/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("Infectología");
        menuPrincipal();
        escenarioPrincipal.show();
        escenarioPrincipal.getIcons().add(new Image("/org/alvarogonzalez/images/icono.png"));
        escenarioPrincipal.setResizable(false); 
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 605, 405);
            menuPrincipal.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
     
    public void ventanaMedicos(){
        try{
            MedicoController medicoController = (MedicoController) cambiarEscena("MedicoView.fxml", 730, 630);
            medicoController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController programadorController = (ProgramadorController) cambiarEscena("ProgramadorView.fxml", 605, 405);
            programadorController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPacientes(){
        try{
            PacienteController pacienteController = (PacienteController) cambiarEscena("PacienteView.fxml", 810, 680);
            pacienteController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
            EspecialidadController especialidadController = (EspecialidadController) cambiarEscena("EspecialidadesView.fxml", 605, 405);
            especialidadController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTelefonoMedico(){
        try{
            TelefonoMedicoController telefonoMedicoController = (TelefonoMedicoController) cambiarEscena("TelefonoMedicoView.fxml", 605, 420);
            telefonoMedicoController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCargo(){
        try{
            CargoController cargoController = (CargoController) cambiarEscena("CargoView.fxml", 605, 405);
            cargoController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
        
    public void ventanaArea(){
        try{
            AreaController areaController = (AreaController) cambiarEscena("AreaView.fxml", 605, 405);
            areaController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaResponsableTurno(){
        try{
            ResponsableTurnoController responsableTurnoController = (ResponsableTurnoController)cambiarEscena("ResponsableTurnoView.fxml",700,550);
            responsableTurnoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaHorario(){
        try{
            HorarioController horarioController = (HorarioController)cambiarEscena("HorarioView.fxml", 600, 470);
            horarioController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaContactoUrgencia(){
        try{
            ContactoUrgenciaController contactoUrgenciaController = (ContactoUrgenciaController) cambiarEscena("ContactoUrgenciaView.fxml", 605, 420);
            contactoUrgenciaController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicoEspecialidad(){
        try{
            MedicoEspecialidadController medicoEspecialidadController = (MedicoEspecialidadController) cambiarEscena("MedicoEspecialidadView.fxml", 611, 420);
            medicoEspecialidadController.setEscenarioPrincipal(this);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTurno(){
        try{
            TurnoController turnoController = (TurnoController) cambiarEscena("TurnoView.fxml", 750, 530);
            turnoController.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaControlCitas(){
        try{
            ControlCitasController controlCitasController = (ControlCitasController)cambiarEscena("ControlCitasView.fxml", 700, 450);
            controlCitasController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaReceta(){
        try{
            RecetaController recetaController = (RecetaController)cambiarEscena("RecetaView.fxml", 600, 430);
            recetaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA + fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA + fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
                
        return resultado;
    }

    public static void main(String[] args) {
        launch(args);
    }
   
}
