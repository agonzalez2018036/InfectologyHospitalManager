package org.alvarogonzalez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.Especialidad;
import org.alvarogonzalez.bean.Horario;
import org.alvarogonzalez.bean.Medico;
import org.alvarogonzalez.bean.MedicoEspecialidad;
import org.alvarogonzalez.db.Conexion;
import org.alvarogonzalez.report.GenerarReporte;
import org.alvarogonzalez.sistema.Principal;


public class MedicoEspecialidadController implements Initializable{
    private Principal escenarioPrincipal;  
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<Medico> listaMedico;
    private ObservableList<Horario> listaHorario;
    private ObservableList<Especialidad> listaEspecialidad;

    @FXML private ComboBox cmbCodigoMedico;
    @FXML private ComboBox cmbCodigoEspecialidad;
    @FXML private ComboBox cmbCodigoHorario;
    @FXML private TableView tblMedicoEspecialidad;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colCodigoHorario;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch (tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(cmbCodigoMedico.getSelectionModel().getSelectedItem() == null || cmbCodigoHorario.getSelectionModel().getSelectedItem() == null || cmbCodigoEspecialidad.getSelectionModel().getSelectedItem() == null){
                     JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                } else {
                    guardar();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    desactivarControles();
                    limpiarControles();
                    cargarDatos();
                    break;
                }
        }
    }
    
     public void guardar(){
        MedicoEspecialidad registro = new MedicoEspecialidad();
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        registro.setCodigoHorario(((Horario)cmbCodigoHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedicoEspecialidad(?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getCodigoEspecialidad());
            procedimiento.setInt(3, registro.getCodigoHorario());
            procedimiento.execute();
            listaMedicoEspecialidad.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarDatos(){
        tblMedicoEspecialidad.setItems(getMedicoEspecialidad());
        colCodigo.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoMedicoEspecialidad"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoMedico"));
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoEspecialidad"));
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoHorario"));
    }
   
    
    public ObservableList<MedicoEspecialidad> getMedicoEspecialidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicoEspecialidad}" );
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new MedicoEspecialidad(resultado.getInt("codigoMedicoEspecialidad"),
                        resultado.getInt("codigoMedico"),
                        resultado.getInt("codigoEspecialidad"),
                        resultado.getInt("codigoHorario")));         
            }
        }catch(Exception e){
            e.printStackTrace();
        }return listaMedicoEspecialidad = FXCollections.observableList(lista);
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                                resultado.getInt("licenciaMedica"),
                                resultado.getString("nombres"),
                                resultado.getString("apellidos"),
                                resultado.getString("horaEntrada"),
                                resultado.getString("horaSalida"),
                                resultado.getInt("turnoMaximo"),
                                resultado.getString("sexo")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return   listaMedico=FXCollections.observableList(lista);
    }
      
     public ObservableList<Horario> getHorarios(){
        ArrayList<Horario> lista = new ArrayList<Horario>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall(("{call sp_ListarHorarios}"));
            ResultSet resultado =procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Horario(resultado.getInt("codigoHorario"),
                        resultado.getString("horaEntrada"),
                        resultado.getString("horaSalida"),
                        resultado.getBoolean("lunes"),
                        resultado.getBoolean("martes"),
                        resultado.getBoolean("miercoles"),
                        resultado.getBoolean("jueves"),
                        resultado.getBoolean("viernes")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaHorario = FXCollections.observableList(lista);
    }
    
     
     public ObservableList<Especialidad> getEspecialidades(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                                resultado.getString("nombreEspecialidad")));
            }       
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEspecialidad = FXCollections.observableList(lista);
    }
     
    public void seleccionarElemento() {
        if(tblMedicoEspecialidad.getSelectionModel().isEmpty()){
                             
        } else if(tipoDeOperacion == operaciones.GUARDAR){
                
        } else {
            cmbCodigoMedico.getSelectionModel().select(buscarMedico(((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedico()));
            cmbCodigoEspecialidad.getSelectionModel().select(buscarEspecialidad(((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
            cmbCodigoHorario.getSelectionModel().select(buscarHorario(((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoHorario()));
        }
    }    
 
    public void eliminar() {
        switch (tipoDeOperacion) {
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnReporte.setDisable(false);
                tblMedicoEspecialidad.getSelectionModel().select (null);
                tipoDeOperacion = operaciones.NINGUNO;
                tblMedicoEspecialidad.setDisable(false);
                break;
            default:
                if (tblMedicoEspecialidad.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar ContactoUrgencia",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedicoEspecialidad(?)}");
                            procedimiento.setInt(1, ((MedicoEspecialidad) tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedico());
                            procedimiento.execute();
                            listaMedicoEspecialidad.remove(tblMedicoEspecialidad.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cmbCodigoMedico.setDisable(true);
                            cmbCodigoEspecialidad.setDisable(true);
                            cmbCodigoHorario.setDisable(true);
                            tblMedicoEspecialidad.getSelectionModel().select (null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(respuesta == JOptionPane.NO_OPTION){
                            desactivarControles();
                            limpiarControles();
                    } else {
                        limpiarControles();
                        desactivarControles();
                        cmbCodigoMedico.setDisable(true);
                        cmbCodigoEspecialidad.setDisable(true);
                        cmbCodigoHorario.setDisable(true);
                        tblMedicoEspecialidad.getSelectionModel().select (null);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
        }
    }
    
    public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
            procedimiento.setInt(1, codigoMedico);
            ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Medico( registro.getInt("codigoMedico"),
                                        (registro.getInt("licenciaMedica")),
                                        (registro.getString("nombres")),
                                        (registro.getString("apellidos")),
                                        (registro.getString("horaEntrada")),
                                        (registro.getString("horaSalida")),
                                        (registro.getInt("turnoMaximo")),
                                        (registro.getString("sexo")));
                }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    
    public Horario buscarHorario(int codigoHorario){
        Horario resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarHorario(?)}");
            procedimiento.setInt(1, codigoHorario);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Horario( registro.getInt("codigoHorario"),
                                        registro.getString("horaEntrada"),
                                        registro.getString("horaEntrada"),
                                        registro.getBoolean("lunes"),
                                        registro.getBoolean("martes"),
                                        registro.getBoolean("miercoles"),
                                        registro.getBoolean("jueves"),
                                        registro.getBoolean("viernes"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    
     public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Especialidad (registro.getInt("codigoEspecialidad"),
                                    registro.getString("nombreEspecialidad"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void generarReporte(){
                imprimirReporte();
                tipoDeOperacion = operaciones.NINGUNO;
    }

    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico", null);
        GenerarReporte.mostrarReporte("ReporteMedicoEspecialidad.jasper", "Reporte de Médico Especialidad", parametros);
    }
     
    public void desactivarControles(){
        cmbCodigoMedico.setDisable(true);
        cmbCodigoEspecialidad.setDisable(true);
        cmbCodigoHorario.setDisable(true);
    }
    
    public void activarControles(){
        cmbCodigoMedico.setDisable(false);
        cmbCodigoEspecialidad.setDisable(false);
        cmbCodigoHorario.setDisable(false);
    }
    
    public void limpiarControles(){
        cmbCodigoMedico.getSelectionModel().select(null);
        cmbCodigoEspecialidad.getSelectionModel().select(null);
        cmbCodigoHorario.getSelectionModel().select(null);
        tblMedicoEspecialidad.getSelectionModel().clearSelection();
    }
    
    public void cancelar(){
        btnNuevo.setText("Nuevo");
        btnEliminar.setText("Eliminar");
        btnReporte.setDisable(false);
        tblMedicoEspecialidad.setDisable(false);
        tipoDeOperacion = operaciones.NINGUNO;
        limpiarControles();
        desactivarControles();
    }

    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.menuPrincipal();
            }
        } else {
           this.escenarioPrincipal.menuPrincipal();
        }        
    }

    public void ventanaHorarios(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaHorario();
            }
        } else {
           this.escenarioPrincipal.ventanaHorario();
        }        
    }
 
    public void ventanaEspecialidades(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaEspecialidades();
            }
        } else {
           this.escenarioPrincipal.ventanaEspecialidades();
        }        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
       cmbCodigoMedico.setItems(getMedicos());
       cmbCodigoEspecialidad.setItems(getEspecialidades());
       cmbCodigoHorario.setItems(getHorarios());
    }
    
}
