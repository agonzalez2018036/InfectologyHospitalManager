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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.Area;
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.sistema.Principal;

public class AreaController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Area> listaArea;  
    
    @FXML private TextField txtArea;
    @FXML private TableView tblAreas;
    @FXML private TableColumn colArea; 
    @FXML private TableColumn colCodigoArea;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    
public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                        activarControles();
                        limpiarControles();
                        btnNuevo.setText("Guardar");
                        btnEliminar.setText("Cancelar");
                        btnEditar.setDisable(true);
                        tipoDeOperacion = operaciones.GUARDAR;
                        break;
            case GUARDAR:
                        if(txtArea.getText().equals("")){
                            JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                        }
                        else{
                            guardar();
                            desactivarControles();
                            btnNuevo.setText("Nuevo");
                            btnEliminar.setText("Eliminar");
                            btnEditar.setDisable(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            limpiarControles();
                            cargarDatos();
                            break;
                        }
        }
    }

    public void guardar(){
        Area registro = new Area();

        registro.setNombreArea(txtArea.getText());        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarArea(?)}");
            procedimiento.setString(1, registro.getNombreArea());
            procedimiento.execute();
            listaArea.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarDatos(){
        tblAreas.setItems(getAreas());
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<Area, Integer>("codigoArea"));
        colArea.setCellValueFactory(new PropertyValueFactory<Area, String>("nombreArea"));
    }
    
    public ObservableList<Area> getAreas(){
        ArrayList<Area> lista = new ArrayList<Area>();
        
            try{
                PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarAreas}");
                ResultSet resultado = procedimiento.executeQuery();
                    while(resultado.next()){
                        lista.add(new Area(resultado.getInt("codigoArea"),
                        resultado.getString("nombreArea")));
                    }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaArea = FXCollections.observableArrayList(lista);
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    break;
            default:
                    if(tblAreas.getSelectionModel().getSelectedItem() != null){
                        int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarArea(?)}");
                                procedimiento.setInt(1, ((Area)tblAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
                                procedimiento.execute();
                                listaArea.remove(tblAreas.getSelectionModel().getSelectedIndex());
                                limpiarControles();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        } else if(respuesta == JOptionPane.NO_OPTION){
                            desactivarControles();
                            limpiarControles();
                        }
                    } else{
                        JOptionPane.showMessageDialog(null, "Debe Seleccionar un elemento");
                    }
        }
    }

    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblAreas.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);                    
                    btnEditar.setText("Actualizar");
                    btnCancelar.setVisible(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    tblAreas.setDisable(true);
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
            break;
            case ACTUALIZAR:
                        if(txtArea.getText().equals("")){
                            JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                        }
                        else{
                        actualizar();
                        btnEditar.setText("Editar");
                        btnCancelar.setVisible(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false); 
                        tblAreas.setDisable(false);
                        limpiarControles();
                        desactivarControles();
                        cargarDatos();
                        }
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarArea(?,?)}");
            Area registro  = (Area)tblAreas.getSelectionModel().getSelectedItem();
            registro.setNombreArea(txtArea.getText());
            procedimiento.setInt(1, registro.getCodigoArea());
            procedimiento.setString(2, registro.getNombreArea());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void seleccionarElemento(){
        if(tblAreas.getSelectionModel().isEmpty()){
            
        } else if(tipoDeOperacion == operaciones.GUARDAR){
            
        } else {
            txtArea.setText(((Area)tblAreas.getSelectionModel().getSelectedItem()).getNombreArea());
        }
    }
    
    public void desactivarControles(){
        txtArea.setEditable(false);
    }
    
    public void activarControles(){
        txtArea.setEditable(true);
    }  
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else {
        txtArea.setText("");
        tblAreas.getSelectionModel().clearSelection();
        }
    }
    
    public void cancelar(){
        desactivarControles();
        btnCancelar.setVisible(false);
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        tblAreas.setDisable(false);
        btnEditar.setText("Editar");
        tipoDeOperacion = operaciones.NINGUNO;         
        limpiarControles();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    } 

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void responsableTurnoController(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaResponsableTurno();
            }
        } else {
            this.escenarioPrincipal.ventanaResponsableTurno();
        }
    }  
}