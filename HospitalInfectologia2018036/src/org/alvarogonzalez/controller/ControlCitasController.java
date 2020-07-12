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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.ControlCita;
import org.alvarogonzalez.bean.Medico;
import org.alvarogonzalez.bean.Paciente;
import org.alvarogonzalez.db.Conexion;
import org.alvarogonzalez.report.GenerarReporte;
import org.alvarogonzalez.sistema.Principal;

public class ControlCitasController implements Initializable{
    private Principal escenarioPrincipal;
    private DatePicker fecha;
    private enum operaciones {NUEVO, CANCELAR, ELIMINAR, EDITAR, ACTUALIZAR, NINGUNO, GUARDAR}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;
    private ObservableList<Paciente> listaPaciente;
    private ObservableList<ControlCita> listaControlCita;
    
    @FXML private TextField txtInicio;
    @FXML private TextField txtSalida;
    @FXML private GridPane grpFecha;
    @FXML private ComboBox cmbMedico;
    @FXML private ComboBox cmbPaciente;
    @FXML private Button  btnNuevo;
    @FXML private Button  btnEliminar;
    @FXML private Button  btnEditar;
    @FXML private Button  btnReporte;
    @FXML private TableView tblCitas;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colFecha;
    @FXML private TableColumn colInicio;
    @FXML private TableColumn colFin;
    @FXML private TableColumn colMedico;
    @FXML private TableColumn colPaciente;
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean entrada = validarHora(txtInicio.getText());
                boolean salida = validarHora(txtSalida.getText());
                if(txtInicio.getText().length() == 0 || txtSalida.getText().length() == 0 || fecha.getSelectedDate() == null
                || cmbMedico.getSelectionModel().getSelectedItem() == null || cmbPaciente.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                } else if(entrada == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    txtInicio.setText("");
                } else if(salida == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    txtSalida.setText("");
                }else{
                    guardar();
                    cargarDatos();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    break;
                }
        }
    }
    
    public void guardar(){
        ControlCita registro = new ControlCita();
        registro.setHoraInicio(txtInicio.getText());
        registro.setHoraFin(txtSalida.getText());
        registro.setFecha(fecha.getSelectedDate());
        registro.setCodigoMedico(((Medico)cmbMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoPaciente(((Paciente)cmbPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarControlCita(?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFecha().getTime()));
            procedimiento.setString(2, registro.getHoraInicio());
            procedimiento.setString(3, registro.getHoraFin());
            procedimiento.setInt(4, registro.getCodigoMedico());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.execute();
            listaControlCita.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cargarDatos(){
       tblCitas.setItems(getControlCita());
       colCodigo.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoControlCita"));
       colFecha.setCellValueFactory(new PropertyValueFactory<ControlCita, Date>("fecha"));
       colInicio.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("horaInicio"));
       colFin.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("horaFin"));
       colMedico.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoMedico"));
       colPaciente.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoPaciente"));
    }
    
    public void seleccionarElemento(){
        if(tblCitas.getSelectionModel().getSelectedItem() == null || tipoDeOperacion == operaciones.GUARDAR){
            
        } else {
            txtInicio.setText(((ControlCita)tblCitas.getSelectionModel().getSelectedItem()).getHoraInicio());
            txtSalida.setText(((ControlCita)tblCitas.getSelectionModel().getSelectedItem()).getHoraFin());
            fecha.selectedDateProperty().set(((ControlCita)tblCitas.getSelectionModel().getSelectedItem()).getFecha());
            cmbMedico.getSelectionModel().select(buscarMedico(((ControlCita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoMedico()));
            cmbPaciente.getSelectionModel().select(buscarPaciente(((ControlCita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
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
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Paciente( registro.getInt("codigoPaciente"),
                                        (registro.getString("DPI")),
                                        (registro.getString("apellidos")),
                                        (registro.getString("nombres")),
                                        (registro.getDate("fechaNacimiento")),
                                        (registro.getInt("edad")),
                                        (registro.getString("direccion")),
                                        (registro.getString("ocupacion")),
                                        (registro.getString("sexo")));
                }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ControlCita buscarControlCita(int codigoControlCita){
        ControlCita resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarControlCita(?)}");
            procedimiento.setInt(1, codigoControlCita);
            ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new ControlCita( registro.getInt("codigoControlCita"),
                                        (registro.getDate("fecha")),
                                        (registro.getString("horaInicio")),
                                        (registro.getString("horaFin")),
                                        (registro.getInt("codigoMedico")),
                                        (registro.getInt("codigoPaciente")));
                }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
        } catch(Exception e){
            e.printStackTrace();
        }
        return listaMedico = FXCollections.observableList(lista);
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
        return listaPaciente = FXCollections.observableList(lista);
    }
    
    public ObservableList<ControlCita> getControlCita(){
        ArrayList<ControlCita> lista = new ArrayList<ControlCita>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarControlCitas}");
            ResultSet resultado = procedimiento.executeQuery(); 
            while(resultado.next()){
                lista.add(new ControlCita(resultado.getInt("codigoControlCita"),
                                          resultado.getDate("fecha"),
                                          resultado.getString("horaInicio"),
                                          resultado.getString("horaFin"),
                                          resultado.getInt("codigoMedico"),
                                          resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaControlCita = FXCollections.observableList(lista);
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
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
                if(tblCitas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Seguro que desea eliminar el registro?", "Eliminar Control-Citas", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarControlCita(?)}");
                            procedimiento.setInt(1, (((ControlCita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoControlCita()));
                            procedimiento.execute();
                            listaControlCita.remove(tblCitas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    } else{
                        limpiarControles();
                        desactivarControles();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento", "Error", JOptionPane.WARNING_MESSAGE);
                }
        }
    }
        
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCitas.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento", "Error", JOptionPane.WARNING_MESSAGE);
                } else{
                    activarControles();
                    cmbMedico.setDisable(true);
                    cmbPaciente.setDisable(true);
                    tblCitas.setDisable(true);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }
                break;        
            case ACTUALIZAR:
                boolean entrada = validarHora(txtInicio.getText());
                boolean salida = validarHora(txtSalida.getText());
                if(txtInicio.getText().length() == 0 || txtSalida.getText().length() == 0 || fecha.getSelectedDate() == null
                || cmbMedico.getSelectionModel().getSelectedItem() == null || cmbPaciente.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                } else if(entrada == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    txtInicio.setText("");
                } else if(salida == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    txtSalida.setText("");
                } else{
                    actualizar();
                    desactivarControles();
                    tblCitas.setDisable(false);
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();
                }
        }
    }
    
    public void actualizar(){
        ControlCita registro = (ControlCita)tblCitas.getSelectionModel().getSelectedItem();
        registro.setFecha(fecha.getSelectedDate());
        registro.setHoraInicio(txtInicio.getText());
        registro.setHoraFin(txtSalida.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarControlCita(?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoControlCita());
            procedimiento.setDate(2, new java.sql.Date(registro.getFecha().getTime()));
            procedimiento.setString(3, registro.getHoraInicio());
            procedimiento.setString(4, registro.getHoraFin());
            procedimiento.execute();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoControlCita", null);
        GenerarReporte.mostrarReporte("ReportePrincipal.jasper", "Reporte de Citas", parametros);
    }
    
    public boolean validarHora(String hora){
        if(hora.length() == 8 && hora.substring(2,3).equals(":") && hora.substring(5,6).equals(":")){
            int horas = Integer.valueOf(hora.substring(0,2));
            int minutos = Integer.valueOf(hora.substring(3,5));
            int segundos = Integer.valueOf(hora.substring(6,8));
            if(horas < 24 && minutos < 60 && segundos < 60){
            return true;
            }
        }
        return false;
    }
    
    public void activarControles(){
        txtInicio.setEditable(true);
        txtSalida.setEditable(true);
        cmbMedico.setDisable(false);
        cmbPaciente.setDisable(false);
        fecha.setDisable(false);
    }
    
    public void desactivarControles(){
        txtInicio.setEditable(false);
        txtSalida.setEditable(false);
        cmbMedico.setDisable(true);
        cmbPaciente.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else{
        txtInicio.setText("");
        txtSalida.setText("");
        cmbMedico.getSelectionModel().select(null);
        cmbPaciente.getSelectionModel().select(null);
        tblCitas.getSelectionModel().clearSelection();
        fecha.setSelectedDate(null);
        }
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaRecetas(){
        escenarioPrincipal.ventanaReceta();
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getStylesheets().add("/org/alvarogonzalez/resource/DatePicker.css");
        grpFecha.add(fecha, 0, 0);
        fecha.setVisible(true);
        fecha.setDisable(true);
        cmbMedico.setItems(getMedicos());
        cmbPaciente.setItems(getPacientes());
    }
    
}
