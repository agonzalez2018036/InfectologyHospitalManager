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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.Area;
import org.alvarogonzalez.bean.Cargo;
import org.alvarogonzalez.bean.ResponsableTurno;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.report.GenerarReporte;
import org.alvarogonzalez.sistema.Principal;

public class ResponsableTurnoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ResponsableTurno> listaTurno;
    private ObservableList<Cargo> listaCargo;
    private ObservableList<Area> listaArea;
    private boolean validacion;
    
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TableView tblTurnos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colTelefono;
    @FXML private TableColumn colArea;
    @FXML private TableColumn colCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ComboBox cmbCargo;
    @FXML private ComboBox cmbArea;
    @FXML private Label lbNombres;
    @FXML private Label lbApellidos;
    @FXML private Label lbTelefono;    
    
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
                boolean validar = validacionNumerica(txtTelefono.getText());
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                    if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || txtTelefono.getText().equals("") || cmbArea.getSelectionModel().getSelectedItem() == null
                    || cmbCargo.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                    } else if(txtTelefono.getText().length() != 8 || txtTelefono.getText().length() > 10 || validar == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbTelefono.setTextFill(Color.AQUA);
                        txtTelefono.setText("");
                    } else if(txtNombres.getText().length() > 75 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombres.setTextFill(Color.AQUA);
                        txtNombres.setText("");                       
                    } else if(txtApellidos.getText().length() > 75 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");                       
                    }
                    else{
                        guardar();
                        valoresPredeterminados();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        btnReporte.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        limpiarControles();
                        cargarDatos();
                        desactivarControles();
                        break;
                    }
        }
    }
    
    public void guardar(){
        ResponsableTurno registro = new ResponsableTurno();
        registro.setNombreResponsable(txtNombres.getText());
        registro.setApellidoResponsable(txtApellidos.getText());
        registro.setTelefonoPersonal(txtTelefono.getText());
        registro.setCodigoArea(((Area)cmbArea.getSelectionModel().getSelectedItem()).getCodigoArea());
        registro.setCodigoCargo(((Cargo)cmbCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
        
        try{
            PreparedStatement procedimiento = (PreparedStatement)getInstancia().getConexion().prepareCall("{call sp_AgregarResponsableTurno(?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreResponsable());
            procedimiento.setString(2, registro.getApellidoResponsable());
            procedimiento.setString(3, registro.getTelefonoPersonal());
            procedimiento.setInt(4, registro.getCodigoArea());
            procedimiento.setInt(5, registro.getCodigoCargo());
            procedimiento.executeQuery();
            listaTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cargarDatos(){
        tblTurnos.setItems(getTurnos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoResponsableTurno"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("nombreResponsable"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("apellidoResponsable"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("telefonoPersonal"));
        colArea.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoArea"));
        colCargo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoCargo"));
    }
    
    public ObservableList<ResponsableTurno> getTurnos(){
       ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
       try{
           PreparedStatement procedimiento = (PreparedStatement)getInstancia().getConexion().prepareCall("{call sp_ListarResponsablesTurno()}");
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
       }
       return listaTurno = FXCollections.observableArrayList(lista);
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
                    valoresPredeterminados();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblTurnos.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    break;
            default:
                    if(!tblTurnos.getSelectionModel().isEmpty()){
                        int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement procedimiento = getInstancia().getConexion().prepareCall("{call sp_EliminarResponsableTurno(?)}");
                                procedimiento.setInt(1, ((ResponsableTurno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
                                procedimiento.execute();
                                listaTurno.remove(tblTurnos.getSelectionModel().getSelectedIndex());
                                limpiarControles();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }else if(respuesta == JOptionPane.NO_OPTION){
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
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblTurnos.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    cmbArea.setDisable(true);
                    cmbCargo.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                boolean validar = validacionNumerica(txtTelefono.getText());
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                    if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || txtTelefono.getText().equals("") || cmbArea.getSelectionModel().getSelectedItem() == null
                    || cmbCargo.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                    } else if(txtTelefono.getText().length() != 8 || txtTelefono.getText().length() > 10 || validar == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbTelefono.setTextFill(Color.AQUA);
                        txtTelefono.setText("");
                    } else if(txtNombres.getText().length() > 75 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombres.setTextFill(Color.AQUA);
                        txtNombres.setText("");                       
                    } else if(txtApellidos.getText().length() > 75 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");                       
                    }
                    else{
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tblTurnos.setDisable(false);
                    cmbArea.setDisable(true);
                    cmbCargo.setDisable(true);
                    actualizar();     
                    valoresPredeterminados();
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();
                }
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = getInstancia().getConexion().prepareCall("{call sp_ActualizarResponsableTurno(?,?,?,?)}");           
            ResponsableTurno registro  = (ResponsableTurno)tblTurnos.getSelectionModel().getSelectedItem();
            registro.setNombreResponsable(txtNombres.getText());
            registro.setApellidoResponsable(txtApellidos.getText());
            registro.setTelefonoPersonal(txtTelefono.getText());      
            
            procedimiento.setInt(1, registro.getCodigoResponsableTurno());
            procedimiento.setString(2, registro.getNombreResponsable());
            procedimiento.setString(3, registro.getApellidoResponsable());
            procedimiento.setString(4, registro.getTelefonoPersonal());
            procedimiento.execute();           
        }catch(Exception e){
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
                boolean validar = validacionNumerica(txtTelefono.getText());
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                    if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || txtTelefono.getText().equals("") || cmbArea.getSelectionModel().getSelectedItem() == null
                    || cmbCargo.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                    } else if(txtTelefono.getText().length() < 8 || txtTelefono.getText().length() > 10 || validar == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbTelefono.setTextFill(Color.AQUA);
                        txtTelefono.setText("");
                    } else if(txtNombres.getText().length() > 75 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombres.setTextFill(Color.AQUA);
                        txtNombres.setText("");                       
                    } else if(txtApellidos.getText().length() > 75 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");                       
                    }
                    else{
                        btnEditar.setText("Editar");
                        btnReporte.setText("Reporte");
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        tblTurnos.setDisable(false);
                        cmbArea.setDisable(true);
                        cmbCargo.setDisable(true);
                        actualizar();     
                        valoresPredeterminados();
                        tipoDeOperacion = operaciones.NINGUNO;
                        limpiarControles();
                        cargarDatos();
                    }
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico", null);
        GenerarReporte.mostrarReporte("ReporteResponsableTurno.jasper", "Reporte de Responsable turno", parametros);
    }
    
    public Area buscarArea(int codigoArea){
        Area resultado = null;
        try{
            PreparedStatement procedimiento = getInstancia().getConexion().prepareCall("{call sp_BuscarArea(?)}");
            procedimiento.setInt(1, codigoArea);
            ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Area( registro.getInt("codigoArea"),
                                        (registro.getString("nombreArea")));
                }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado = null;
        try{
            PreparedStatement procedimiento = getInstancia().getConexion().prepareCall("{call sp_BuscarCargos(?)}");
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
    
    public void seleccionarElemento(){
            if(tblTurnos.getSelectionModel().isEmpty()){
                             
            } else if(tipoDeOperacion == operaciones.GUARDAR){
                
            }else{
            txtNombres.setText(((ResponsableTurno) tblTurnos.getSelectionModel().getSelectedItem()).getNombreResponsable());
            txtApellidos.setText(((ResponsableTurno) tblTurnos.getSelectionModel().getSelectedItem()).getApellidoResponsable());
            txtTelefono.setText(((ResponsableTurno) tblTurnos.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            cmbArea.getSelectionModel().select(buscarArea(((ResponsableTurno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoArea()));
            cmbCargo.getSelectionModel().select(buscarCargo(((ResponsableTurno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoCargo()));
            }
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtTelefono.setEditable(false);
        cmbArea.setDisable(true);
        cmbCargo.setDisable(true);
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtTelefono.setEditable(true);
        cmbArea.setDisable(false);
        cmbCargo.setDisable(false);
    }  
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else{
            txtNombres.setText("");
            txtApellidos.setText("");
            txtTelefono.setText("");
            cmbArea.getSelectionModel().select(null);
            cmbCargo.getSelectionModel().select(null);
            tblTurnos.getSelectionModel().clearSelection();
        }
    }
    
    public void valoresPredeterminados(){
        lbNombres.setTextFill(Color.BLACK);
        lbApellidos.setTextFill(Color.BLACK);
        lbTelefono.setTextFill(Color.BLACK);
    }
    
    public void cancelar(){
        btnNuevo.setText("Nuevo");
        btnEliminar.setText("Eliminar");
        btnEditar.setDisable(false);
        btnReporte.setDisable(false);
        tblTurnos.setDisable(false);
        tipoDeOperacion = operaciones.NINGUNO;
        limpiarControles();
        desactivarControles();
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
    
    public boolean validacionNumerica(String numeros){
        int numero = 0;
        try{
            numero = Integer.parseInt(numeros);
            return true; 
        } catch(Exception e){
            return false;
        }
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }   
    
    public void ventanaCargo(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaCargo();
            }
        } else {
                this.escenarioPrincipal.ventanaCargo();
        }  
    }
    
    public void ventanaArea(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                escenarioPrincipal.ventanaArea();
            }
        } else {
                escenarioPrincipal.ventanaArea();
        }  
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbArea.setItems(getAreas());
        cmbCargo.setItems(getCargos());
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
}
