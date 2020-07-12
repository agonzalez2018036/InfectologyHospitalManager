package org.alvarogonzalez.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.MedicoEspecialidad;
import org.alvarogonzalez.bean.Paciente;
import org.alvarogonzalez.bean.ResponsableTurno;
import org.alvarogonzalez.bean.Turno;
import org.alvarogonzalez.db.Conexion;
import org.alvarogonzalez.report.GenerarReporte;
import org.alvarogonzalez.sistema.Principal;

public class TurnoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Turno> listaTurno;
    private ObservableList<ResponsableTurno> listaResponsableTurno;
    private ObservableList<Paciente> listaPaciente;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private DatePicker fecha1;
    private DatePicker fecha2;
    
    @FXML private GridPane grpFecha1;
    @FXML private GridPane grpFecha2;
    @FXML private TextField txtValores;
    @FXML private ComboBox cmbMedico;
    @FXML private ComboBox cmbResponsable;
    @FXML private ComboBox cmbPaciente;
    @FXML private TableView tblTurnos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colFechaTurno;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colValor;
    @FXML private TableColumn colMedico;
    @FXML private TableColumn colRT;
    @FXML private TableColumn colPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Label lbValor;
    
    public void nuevo(){
        switch (tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                txtValores.setEditable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validarValor = validarValor(txtValores.getText());
                if(cmbMedico.getSelectionModel().getSelectedItem() == null || cmbResponsable.getSelectionModel().getSelectedItem() == null || cmbPaciente.getSelectionModel().getSelectedItem() == null || 
                txtValores.getText().equals("") || fecha1.selectedDateProperty().get()== null || fecha2.selectedDateProperty().get() == null){
                   JOptionPane.showMessageDialog(null, "No ha llenado todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                } else if(txtValores.getText().length() > 7 || validarValor == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbValor.setTextFill(Color.AQUA);
                    txtValores.setText("");
                } else{
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    tblTurnos.setDisable(false);
                    cargarDatos();
                }
            break;
        }
    }
    
   public void guardar(){
        
        Turno registro = new Turno();
        registro.setFechaTurno(fecha1.getSelectedDate());
        registro.setFechaCita(fecha2.getSelectedDate());
        registro.setValorCita(Double.valueOf(txtValores.getText()));
        registro.setCodigoResponsableTurno(((ResponsableTurno)cmbResponsable.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
        registro.setCodigoPaciente(((Paciente)cmbPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)cmbMedico.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTurno(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(3, registro.getValorCita());
            procedimiento.setInt(4, registro.getCodigoResponsableTurno());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.setInt(6, registro.getCodigoMedicoEspecialidad());
            procedimiento.execute();
            listaTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cargarDatos(){
        tblTurnos.setItems(getTurno());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoTurno"));
        colFechaTurno.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaTurno"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaCita"));
        colValor.setCellValueFactory(new PropertyValueFactory<Turno, Double>("valorCita"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoPaciente"));
        colMedico.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoMedicoEspecialidad"));
        colRT.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoResponsableTurno"));
    }
    
    public ObservableList<Turno> getTurno(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTurnos}" );
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Turno(resultado.getInt("codigoTurno"),
                        resultado.getDate("fechaTurno"),
                        resultado.getDate("fechaCita"),
                        resultado.getDouble("valorCita"),
                        resultado.getInt("codigoResponsableTurno"),
                        resultado.getInt("codigoPaciente"),
                        resultado.getInt("codigoMedicoEspecialidad")));         
            }
        }catch(Exception e){
           e.printStackTrace();
        }return listaTurno = FXCollections.observableList(lista);
    }
    
    public ObservableList<ResponsableTurno> getResponsableTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsablesTurno}" );
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                        resultado.getString("nombreResponsable"),
                        resultado.getString("apellidoResponsable"),
                        resultado.getString("telefonoPersonal"),
                        resultado.getInt("codigoArea"),
                        resultado.getInt("codigoCargo")));         
           }
        }catch(Exception e){
           e.printStackTrace();
        }return listaResponsableTurno = FXCollections.observableList(lista);
    }

    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                resultado.getString("DPI"),
                                resultado.getString("apellidos"),
                                resultado.getString("nombres"),
                                resultado.getDate("fechaNacimiento"),
                                resultado.getInt("edad"),
                                resultado.getString("direccion"),
                                resultado.getString("ocupacion"),
                                resultado.getString("sexo")));                           
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPaciente=FXCollections.observableList(lista);
        
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
    
     public void eliminar() {
        switch (tipoDeOperacion) {
            case GUARDAR:
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                break;
            default:
               if (tblTurnos.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTurno(?)}");
                            procedimiento.setInt(1, ((Turno) tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno());
                            procedimiento.execute();
                            listaTurno.remove(tblTurnos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(respuesta == JOptionPane.NO_OPTION){
                        limpiarControles();
                        desactivarControles();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
        }
    }  
     
      public Turno buscarTurno(int codigoTurno){
        Turno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTurno(?)}");
            procedimiento.setInt(1, codigoTurno);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Turno ( registro.getInt("codigoTurno"),
                                        registro.getDate("fechaTurno"),
                                        registro.getDate("fechaCita"),
                                        registro.getDouble("valorCita"),
                                        registro.getInt("codgioMedicoEspecialidad"),
                                        registro.getInt("codigoResponsable"),
                                        registro.getInt("codigoPaciente"));
                                        
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ResponsableTurno buscarResponsable(int codigoResponsableTurno){
        ResponsableTurno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsableTurno);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ResponsableTurno ( registro.getInt("codigoResponsableTurno"),
                                        registro.getString("nombreResponsable"),
                                        registro.getString("apellidoResponsable"),
                                        registro.getString("telefonoPersonal"),
                                        registro.getInt("codigoArea"),
                                        registro.getInt("codigoCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente (registro.getInt("codigoPaciente"),
                                    registro.getString("DPI"),
                                    registro.getString("apellidos"),
                                    registro.getString("nombres"),
                                    registro.getDate("fechaNacimiento"),
                                    registro.getInt("edad"),
                                    registro.getString("direccion"),
                                    registro.getString("ocupacion"),
                                    registro.getString("sexo")); 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public MedicoEspecialidad buscarMedicoEspecialidad (int codigoMedicoEspecialidad){
        MedicoEspecialidad resultado = null;
                try{
                    PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedicoEspecialidad(?)}");
                    procedimiento.setInt(1, codigoMedicoEspecialidad);
                    ResultSet registro = procedimiento.executeQuery();
                    while (registro.next()){
                       resultado = new MedicoEspecialidad (registro.getInt("codigoMedicoEspecialidad") ,
                                                           registro.getInt("codigoMedico"),
                                                           registro.getInt("codigoEspecialidad"),
                                                           registro.getInt("codigoHorario"));
                   }
                }catch(Exception e){
                    e.printStackTrace();
                }
               return resultado;
    }
  
    public void seleccionarElemento() {
        if (tblTurnos.getSelectionModel().getSelectedItem() != null){       
            fecha1.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaTurno());
            fecha2.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaCita());
            txtValores.setText(String.valueOf(((Turno) tblTurnos.getSelectionModel().getSelectedItem()).getValorCita()));
            cmbMedico.getSelectionModel().select(buscarMedicoEspecialidad(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
            cmbResponsable.getSelectionModel().select(buscarResponsable(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno()));
            cmbPaciente.getSelectionModel().select(buscarPaciente(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));       
       }
    }  
    
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblTurnos.getSelectionModel().getSelectedItem() != null) {
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
 
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarTurno(?,?,?,?)}");
            Turno registro = (Turno) tblTurnos.getSelectionModel().getSelectedItem();
            registro.setFechaTurno(fecha1.getSelectedDate());
            registro.setFechaCita(fecha2.getSelectedDate());
            registro.setValorCita(Double.valueOf(txtValores.getText()));

            procedimiento.setInt(1, registro.getCodigoTurno());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(3, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(4, registro.getValorCita());
            procedimiento.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      
    public void generarReporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            case ACTUALIZAR:
                actualizar();
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico", null);
        GenerarReporte.mostrarReporte("ReporteTurno.jasper", "Reporte de turnos", parametros);
    }


    
    public boolean validarValor(String valor){
        try{
            double validar = Double.valueOf(valor);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void desactivarControles(){
        fecha1.setDisable(true);
        fecha2.setDisable(true);
        txtValores.setEditable(false);
        cmbMedico.setDisable(true);
        cmbResponsable.setDisable(true);
        cmbPaciente.setDisable(true);
    }
    
    public void activarControles(){
        fecha1.setDisable(false);
        fecha2.setDisable(false);
        txtValores.setEditable(true);
        cmbMedico.setDisable(false);
        cmbPaciente.setDisable(false);
        cmbResponsable.setDisable(false);
    }
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else{       
            txtValores.setText("");
            cmbMedico.getSelectionModel().select(null);
            cmbResponsable.getSelectionModel().select(null);
            cmbPaciente.getSelectionModel().select(null);
            fecha1.selectedDateProperty().set(null);
            fecha2.selectedDateProperty().set(null);
            tblTurnos.getSelectionModel().clearSelection();
        }
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha1 = new DatePicker(Locale.ENGLISH);
        fecha1.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha1.getCalendarView().todayButtonTextProperty().set("Today");
        fecha1.getCalendarView().setShowWeeks(false);
        fecha1.getStylesheets().add("/org/alvarogonzalez/resource/DatePicker.css");
        grpFecha1.add(fecha1, 0, 0);
        
        fecha2 = new DatePicker(Locale.ENGLISH);
        fecha2.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha2.getCalendarView().todayButtonTextProperty().set("Today");
        fecha2.getCalendarView().setShowWeeks(false);
        fecha2.getStylesheets().add("/org/alvarogonzalez/resource/DatePicker.css");
        grpFecha2.add(fecha2, 0 , 0);
        
        cmbMedico.setItems(getMedicoEspecialidad());
        cmbResponsable.setItems(getResponsableTurno());
        cmbPaciente.setItems(getPacientes());
    }    
}
