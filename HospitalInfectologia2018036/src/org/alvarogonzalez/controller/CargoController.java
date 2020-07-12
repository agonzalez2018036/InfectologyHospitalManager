package org.alvarogonzalez.controller;


//librerias utilizadas
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
import org.alvarogonzalez.bean.Cargo;
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.sistema.Principal;

public class CargoController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cargo> listaCargo;   
    
    @FXML private TextField txtCargo;
    @FXML private TableView tblCargos;
    @FXML private TableColumn colCodigoCargo;
    @FXML private TableColumn colCargo; 
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    @FXML private Label lbCargo;
    
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
                        if(txtCargo.getText().equals("")){
                            JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                        } else if(txtCargo.getText().length() > 45){
                            txtCargo.setText("");
                            lbCargo.setTextFill(Color.AQUA);
                            JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                        }else{
                            guardar();
                            desactivarControles();
                            valoresPredeterminados();
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
        Cargo registro = new Cargo();
        registro.setNombreCargo(txtCargo.getText());        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCargo(?)}");
            procedimiento.setString(1, registro.getNombreCargo());
            procedimiento.execute();
            listaCargo.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarDatos(){
        tblCargos.setItems(getCargos());
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<Cargo, Integer>("codigoCargo"));
        colCargo.setCellValueFactory(new PropertyValueFactory<Cargo, String>("nombreCargo"));
    }

    public ObservableList<Cargo> getCargos(){
        ArrayList<Cargo> lista = new ArrayList<Cargo>();
            try{
                PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarCargos()}");
                ResultSet resultado = procedimiento.executeQuery();
                    while(resultado.next()){
                        lista.add(new Cargo(resultado.getInt("codigoCargo"),
                        resultado.getString("nombreCargo"))); 
                    }
            } catch(Exception e){
                   e.printStackTrace();
            }
        return listaCargo = FXCollections.observableArrayList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblCargos.getSelectionModel().getSelectedItem() == null){
            
        } else if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        }else{
            txtCargo.setText(((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getNombreCargo());
        }
    }
   
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    valoresPredeterminados();
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    break;
            default:
                    if(tblCargos.getSelectionModel().getSelectedItem() != null){
                        int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCargo(?)}");
                                procedimiento.setInt(1, ((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
                                procedimiento.execute();
                                listaCargo.remove(tblCargos.getSelectionModel().getSelectedIndex());
                                limpiarControles();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        } else if(respuesta == JOptionPane.NO_OPTION){
                            limpiarControles();
                            desactivarControles();
                        }
                    } else{
                        JOptionPane.showMessageDialog(null, "Debe Seleccionar un elemento");
                    }
        }
    }
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargos(?)}");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Cargo( registro.getInt("codigoCargo"),
                                        (registro.getString("nombreCargo")));
                }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
        
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCargos.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblCargos.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnCancelar.setVisible(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
            break;
            case ACTUALIZAR:
                if(txtCargo.getText().equals("")){
                            JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                        } else if(txtCargo.getText().length() > 45){
                            txtCargo.setText("");
                            lbCargo.setTextFill(Color.AQUA);
                            JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                        }else{
                            actualizar();
                            btnEditar.setText("Editar");
                            btnCancelar.setVisible(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            btnNuevo.setDisable(false);
                            btnEliminar.setDisable(false);
                            tblCargos.setDisable(false);
                            cargarDatos();
                            break;
                        }
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarCargo(?,?)}");
            Cargo registro  = (Cargo)tblCargos.getSelectionModel().getSelectedItem();
            registro.setNombreCargo(txtCargo.getText());
            procedimiento.setInt(1, registro.getCodigoCargo());
            procedimiento.setString(2, registro.getNombreCargo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        limpiarControles();
        desactivarControles();
    }
    
    public void desactivarControles(){
        txtCargo.setEditable(false);
    }
    
    public void activarControles(){
        txtCargo.setEditable(true);
    }  
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else{
        txtCargo.setText("");
        tblCargos.getSelectionModel().clearSelection();
        }
    }
    
    public void valoresPredeterminados(){
        lbCargo.setTextFill(Color.BLACK);
    }
    
    public void cancelar(){
        desactivarControles();
        valoresPredeterminados();
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        tblCargos.setDisable(false);
        btnEditar.setText("Editar");
        btnCancelar.setVisible(false);
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