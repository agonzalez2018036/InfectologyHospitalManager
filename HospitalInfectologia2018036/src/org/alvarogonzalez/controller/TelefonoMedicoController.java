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
import javafx.scene.DepthTest;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.Medico;
import org.alvarogonzalez.bean.TelefonosMedico;
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.sistema.Principal;

public class TelefonoMedicoController implements Initializable {
    private Principal escenarioPrincipal;
    private boolean validacion;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<TelefonosMedico> listaTelefonosMedico;   
    private ObservableList<Medico> listaMedico;
    
    //Objetos FXML
    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtTelefonoTrabajo;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TableView tblTelefonosMedico;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colTelPersonal;
    @FXML private TableColumn colTelTrabajo;
    @FXML private TableColumn colCodMedico;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    @FXML private Label lbTelPersonal;
    @FXML private Label lbTelTrabajo;
    
    
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
                boolean telPersonal = validacionNumerica(txtTelefonoPersonal.getText());
                boolean telTrabajo = validacionNumerica(txtTelefonoTrabajo.getText());
                    if(txtTelefonoPersonal.getText().equals("") || txtTelefonoTrabajo.getText().equals("") || cmbCodigoMedico.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");   
                    } else if(txtTelefonoPersonal.getText().length() != 8 || telPersonal == false){
                        lbTelPersonal.setTextFill(Color.AQUA);
                        txtTelefonoPersonal.setText("");
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    } else if(txtTelefonoTrabajo.getText().length() != 8 || telTrabajo == false){
                        lbTelTrabajo.setTextFill(Color.AQUA);
                        txtTelefonoTrabajo.setText("");
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");                       
                    } else{
                        guardar(); 
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        btnReporte.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;  
                        valoresPredeterminados();
                        desactivarControles();
                        limpiarControles();
                        cargarDatos();                        
                    }
                break;
        }
    }
    
    public void guardar(){
        TelefonosMedico registro = new TelefonosMedico();
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico(?,?,?)}");
                procedimiento.setString(1, registro.getTelefonoPersonal());
                procedimiento.setString(2, registro.getTelefonoTrabajo());
                procedimiento.setInt(3, registro.getCodigoMedico());
                procedimiento.executeQuery();
                listaTelefonosMedico.add(registro);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "El registro ya existe", "Error", JOptionPane.WARNING_MESSAGE);
            }
    }
    
    public void cargarDatos(){
        tblTelefonosMedico.setItems(getTelefonoMedico());
        colCodigo.setCellValueFactory(new PropertyValueFactory<TelefonosMedico, Integer>("codigoTelefonoMedico"));
        colTelPersonal.setCellValueFactory(new PropertyValueFactory <TelefonosMedico,String>("telefonoPersonal"));
        colTelTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonosMedico,String>("telefonoTrabajo"));
        colCodMedico.setCellValueFactory(new PropertyValueFactory<TelefonosMedico, Integer>("codigoMedico"));
    }
        
    public ObservableList<TelefonosMedico> getTelefonoMedico(){
        ArrayList<TelefonosMedico> lista = new ArrayList<TelefonosMedico>();
            try{
                PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarTelefonosMedico}");
                ResultSet resultado = procedimiento.executeQuery();
                    while(resultado.next()){
                        lista.add(new TelefonosMedico(resultado.getInt("codigoTelefonoMedico"),
                                resultado.getString("telefonoPersonal"),
                                resultado.getString("telefonoTrabajo"),
                                resultado.getInt("codigoMedico")));
                    }
            } catch(Exception e){
            }
        return listaTelefonosMedico = FXCollections.observableList(lista);
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
            try{
                PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarMedicos}");
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
            
            return listaMedico = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblTelefonosMedico.getSelectionModel().isEmpty()){
                             
        } else if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
                
        }else{
            txtTelefonoPersonal.setText(((TelefonosMedico) tblTelefonosMedico.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            txtTelefonoTrabajo.setText(((TelefonosMedico) tblTelefonosMedico.getSelectionModel().getSelectedItem()).getTelefonoTrabajo());          
            cmbCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                valoresPredeterminados();
                desactivarControles();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    break;
            default:
                if(tblTelefonosMedico.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Telefono",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTelefonoMedico(?)}");
                            procedimiento.setInt(1, ((TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());
                            procedimiento.execute();
                            listaTelefonosMedico.remove(tblTelefonosMedico.getSelectionModel().getSelectedIndex());
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

    public TelefonosMedico buscarTelefono(int codigoTelefonoMedico){
        TelefonosMedico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTelefonoMedico(?)}");
            procedimiento.setInt(1, codigoTelefonoMedico);
            ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new TelefonosMedico(registro.getInt("codigoTelefonoMedico"),
                                                    registro.getString("telefonoPersonal"),
                                                    registro.getString("telefonoTrabajo"),
                                                    registro.getInt("codigoMedico"));
                }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
       
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTelefonosMedico.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);                    
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    cmbCodigoMedico.setDisable(true);
                    tblTelefonosMedico.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                boolean telPersonal = validacionNumerica(txtTelefonoPersonal.getText());
                boolean telTrabajo = validacionNumerica(txtTelefonoTrabajo.getText());
                    if(txtTelefonoPersonal.getText().equals("") || txtTelefonoTrabajo.getText().equals("") || cmbCodigoMedico.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");   
                    } else if(txtTelefonoPersonal.getText().length() != 8 || telPersonal == false){
                        lbTelPersonal.setTextFill(Color.AQUA);
                        txtTelefonoPersonal.setText("");
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    } else if(txtTelefonoTrabajo.getText().length() != 8 || telTrabajo == false){
                        lbTelTrabajo.setTextFill(Color.AQUA);
                        txtTelefonoTrabajo.setText("");
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");                       
                    } else{
                            actualizar();
                            btnEditar.setText("Editar");
                            btnReporte.setText("Reporte");
                            btnNuevo.setDisable(false);
                            btnEliminar.setDisable(false);
                            tblTelefonosMedico.setDisable(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            valoresPredeterminados();                        
                            limpiarControles();
                            cargarDatos();
                        }   
                break;
        }
    }
       
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarTelefonoMedico(?,?,?)}");
            TelefonosMedico registro  = (TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem();
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
            procedimiento.setInt(1, registro.getCodigoTelefonoMedico());
            procedimiento.setString(2, registro.getTelefonoPersonal());
            procedimiento.setString(3, registro.getTelefonoTrabajo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        desactivarControles();
    }
    
    public void valoresPredeterminados(){
        lbTelPersonal.setTextFill(Color.BLACK);
        lbTelTrabajo.setTextFill(Color.BLACK);
    
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
 
    public void desactivarControles(){
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoTrabajo.setEditable(false);
        cmbCodigoMedico.setDisable(true);
    }
    
    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoTrabajo.setEditable(true);
        cmbCodigoMedico.setDisable(false);
    }  
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else{
        txtTelefonoPersonal.setText("");
        txtTelefonoTrabajo.setText("");
        cmbCodigoMedico.getSelectionModel().select(null);
        tblTelefonosMedico.getSelectionModel().clearSelection();
        }
    }
    
    public void cancelar(){
        desactivarControles();
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        btnEditar.setText("Editar");
        btnReporte.setText("reporte");
        tblTelefonosMedico.setDisable(false);
        tipoDeOperacion = operaciones.NINGUNO;
        limpiarControles();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
    } 

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void vantanaMedico(){
        if(tipoDeOperacion == operaciones.GUARDAR){
        int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaMedicos();
            }
        } else {
            this.escenarioPrincipal.ventanaMedicos();
        }
    }  
}