package org.alvarogonzalez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.ControlCita;
import org.alvarogonzalez.bean.Receta;
import org.alvarogonzalez.db.Conexion;
import org.alvarogonzalez.sistema.Principal;

public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {GUARDAR, ELIMINAR, EDITAR, CANCELAR, ACTUALIZAR, NINGUNO}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Receta> listaReceta;
    private ObservableList<ControlCita> listaControlCita;
    
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox cmbCodigoCC;
    @FXML private TableView tblRecetas;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colCC;
    @FXML private Button  btnNuevo;
    @FXML private Button  btnEliminar;
    @FXML private Button  btnEditar;
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtDescripcion.getText().length() == 0 || cmbCodigoCC.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                } else{
                    guardar();
                    cargarDatos();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    desactivarControles();
                }
        }
    }
    
    public void guardar(){
        Receta registro = new Receta();
        registro.setDescripcionReceta(txtDescripcion.getText());
        registro.setCodigoControlCita(((ControlCita)cmbCodigoCC.getSelectionModel().getSelectedItem()).getCodigoControlCita());  
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarReceta(?,?)}");
            procedimiento.setString(1, registro.getDescripcionReceta());
            procedimiento.setInt(2, registro.getCodigoControlCita());
            procedimiento.execute();
            listaReceta.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   
    public void cargarDatos(){
       tblRecetas.setItems(getRecetas());
       colCodigo.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoReceta"));
       colDescripcion.setCellValueFactory(new PropertyValueFactory<Receta, String>("descripcionReceta"));
       colCC.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoControlCita"));
    }
    
    public void seleccionarElemento(){
        if(tblRecetas.getSelectionModel().getSelectedItem() == null || tipoDeOperacion == operaciones.GUARDAR){
            
        } else {
            txtDescripcion.setText(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getDescripcionReceta());
            cmbCodigoCC.getSelectionModel().select(buscarControlCita(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoControlCita()));
        }
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
    
    public ObservableList<Receta> getRecetas(){
        ArrayList<Receta> lista = new ArrayList<Receta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarRecetas}");
            ResultSet resultado = procedimiento.executeQuery(); 
            while(resultado.next()){
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                                     resultado.getString("descripcionReceta"),
                                     resultado.getInt("codigoControlCita")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaReceta = FXCollections.observableList(lista);
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
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                desactivarControles();
                break;
            case ACTUALIZAR:
                btnNuevo.setDisable(false);
                btnEliminar.setText("Eliminar");
                btnEditar.setText("Editar");
                tblRecetas.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                desactivarControles();
                break;                         
            default:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Seguro que desea eliminar el registro?", "Eliminar Control-Citas", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarReceta(?)}");
                            procedimiento.setInt(1, (((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta()));
                            procedimiento.execute();
                            listaReceta.remove(tblRecetas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    } else{
                        limpiarControles();
                        desactivarControles();
                    }
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento", "Error", JOptionPane.WARNING_MESSAGE);
                }
            break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblRecetas.getSelectionModel().getSelectedItem() == null){
                    
                } else {
                    activarControles();
                    cmbCodigoCC.setDisable(true);
                    tblRecetas.setDisable(true);
                    btnNuevo.setDisable(true);
                    btnEliminar.setText("Cancelar");
                    btnEditar.setText("Actualizar");
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }
                break;        
            case ACTUALIZAR:
                if(txtDescripcion.getText().length() == 0 || cmbCodigoCC.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                } else{
                    actualizar();
                    desactivarControles();
                    cargarDatos();
                    btnNuevo.setDisable(false);
                    btnEliminar.setText("Eliminar");
                    btnEditar.setText("Editar");
                    tblRecetas.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                }
        }
    }
    
    public void actualizar(){
        Receta registro = (Receta)tblRecetas.getSelectionModel().getSelectedItem();
        registro.setDescripcionReceta(txtDescripcion.getText());       
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarReceta(?,?)}");
            procedimiento.setInt(1, registro.getCodigoReceta());
            procedimiento.setString(2, registro.getDescripcionReceta());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else{
        txtDescripcion.setText("");
        cmbCodigoCC.getSelectionModel().select(null);
        tblRecetas.getSelectionModel().clearSelection();
        }
    }
    
    public void activarControles(){
        txtDescripcion.setEditable(true);
        cmbCodigoCC.setDisable(false);
    }
    
    public void desactivarControles(){
        txtDescripcion.setEditable(false);
        cmbCodigoCC.setDisable(true);
    }
    
    public void ventanaControlCitas(){
        escenarioPrincipal.ventanaControlCitas();
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
        cmbCodigoCC.setItems(getControlCita());
    }
    
}
