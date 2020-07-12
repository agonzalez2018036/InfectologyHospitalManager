package org.alvarogonzalez.controller;

//Librerias utilizadas
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
import org.alvarogonzalez.bean.Especialidad;
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.sistema.Principal;

public class EspecialidadController implements Initializable {
    private Principal escenarioPrincipal;
    private boolean validacion;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Especialidad> listaEspecialidad; 
    
    //Objetos FXML
    @FXML private TextField txtEspecialidades;
    @FXML private TableView tblEspecialidades;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colEspecialidad; 
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    @FXML private Label lbEspecialidad;
    
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
                    boolean validarArea = validacionDeTextField(txtEspecialidades.getText());
                        if(txtEspecialidades.getText().equals("")){
                            JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                        } else if(txtEspecialidades.getText().length() > 45 || validarArea == true){
                            JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                            lbEspecialidad.setTextFill(Color.AQUA);
                            txtEspecialidades.setText("");
                        } else{
                            guardar();
                            desactivarControles();
                            btnNuevo.setText("Nuevo");
                            btnEliminar.setText("Eliminar");
                            btnEditar.setDisable(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            limpiarControles();
                            valoresPredeterminados();
                            cargarDatos();
                        }
                    break;
        }
    }
    
    public void guardar(){
        Especialidad registro = new Especialidad();

        registro.setNombreEspecialidad(txtEspecialidades.getText());        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarEspecialidad(?)}");
            procedimiento.setString(1, registro.getNombreEspecialidad());
            procedimiento.execute();
            listaEspecialidad.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarDatos(){
        tblEspecialidades.setItems(getEspecialidad());
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, Integer>("codigoEspecialidad"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, String>("nombreEspecialidad"));
    }
    
    public ObservableList <Especialidad> getEspecialidad(){
        ArrayList <Especialidad> lista = new ArrayList<Especialidad>();
            try{
                PreparedStatement procedimiento =  (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades}");
                ResultSet resultado = procedimiento.executeQuery(); 
                    while(resultado.next()){
                        lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                        resultado.getString("nombreEspecialidad")));
                    }
            } catch(Exception e){
                e.printStackTrace();
            }
        return listaEspecialidad = FXCollections.observableArrayList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblEspecialidades.getSelectionModel().isEmpty()){
            
        } else if(tipoDeOperacion == operaciones.GUARDAR){
            
        } else{
            txtEspecialidades.setText(String.valueOf(((Especialidad) tblEspecialidades.getSelectionModel().getSelectedItem()).getNombreEspecialidad()));
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                valoresPredeterminados();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                            procedimiento.setInt(1, ((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
                            procedimiento.execute();
                            listaEspecialidad.remove(tblEspecialidades.getSelectionModel().getSelectedIndex());
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
    
    public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Especialidad( registro.getInt("codigoEspecialidad"),
                                        (registro.getString("nombreEspecialidad")));
                }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }

    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);                    
                    btnEditar.setText("Actualizar");
                    btnCancelar.setVisible(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    tblEspecialidades.setDisable(true);
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
            break;
            case ACTUALIZAR:
                boolean validarArea = validacionDeTextField(txtEspecialidades.getText());
                    if(txtEspecialidades.getText().equals("")){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                    } else if(txtEspecialidades.getText().length() > 45 || validarArea == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbEspecialidad.setTextFill(Color.AQUA);
                        txtEspecialidades.setText("");
                    } else{
                        actualizar();
                        btnEditar.setText("Editar");
                        btnCancelar.setVisible(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false); 
                        tblEspecialidades.setDisable(false);
                        limpiarControles();
                        desactivarControles();
                        valoresPredeterminados();
                        cargarDatos();
                    }
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarEspecialidad(?,?)}");
            Especialidad registro  = (Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem();
            registro.setNombreEspecialidad(txtEspecialidades.getText());
            procedimiento.setInt(1, registro.getCodigoEspecialidad());
            procedimiento.setString(2, registro.getNombreEspecialidad());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
   public void valoresPredeterminados(){
        lbEspecialidad.setTextFill(Color.BLACK);
    }
    
    public boolean validacionDeTextField(String texto){        
        for(int i = 0; i < texto.length(); i++){
            char caracter = texto.charAt(i);
            if(Character.isLetter(caracter) || Character.isSpaceChar(caracter)){
                validacion = false;           
            } else{
                validacion = true;
                i = texto.length();
            }
        }
        return validacion;
    }

    public void desactivarControles(){
        txtEspecialidades.setEditable(false);
    }
    
    public void activarControles(){
        txtEspecialidades.setEditable(true);
    }  
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){

        } else {
            txtEspecialidades.setText("");
            tblEspecialidades.getSelectionModel().clearSelection();
        }
    }
    
    public void cancelar(){
        tipoDeOperacion = operaciones.NINGUNO;  
        valoresPredeterminados();
        limpiarControles();
        desactivarControles();
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        btnEditar.setText("Editar");
        tblEspecialidades.setDisable(false);
        btnCancelar.setVisible(false);
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
    
    public void menuPrincipal(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaMedicoEspecialidad();
            }
        } else {
           this.escenarioPrincipal.ventanaMedicoEspecialidad();
        }        
    }  
}