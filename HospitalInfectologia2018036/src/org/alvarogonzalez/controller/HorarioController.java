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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.Horario;
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.sistema.Principal;

public class HorarioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Horario> listaHorario;
    
    @FXML private TextField txtEntrada;
    @FXML private TextField txtSalida;
    @FXML private TableView tblHorarios;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colEntrada;
    @FXML private TableColumn colSalida;
    @FXML private TableColumn colLunes;
    @FXML private TableColumn colMartes;
    @FXML private TableColumn colMiercoles;
    @FXML private TableColumn colJueves;
    @FXML private TableColumn colViernes;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    @FXML private CheckBox chkLunes;
    @FXML private CheckBox chkMartes;
    @FXML private CheckBox chkMiercoles;
    @FXML private CheckBox chkJueves;
    @FXML private CheckBox chkViernes;
    @FXML private Label lbEntrada;
    @FXML private Label lbSalida;
  
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnCancelar.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validarEntrada = validarHora(txtEntrada.getText());
                boolean validarSalida = validarHora(txtSalida.getText());
                if(txtEntrada.getText().equals("") || txtSalida.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                } else if(txtEntrada.getText().length() != 8 || validarEntrada  == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbEntrada.setTextFill(Color.AQUA);
                    txtEntrada.setText("");
                } else if(txtSalida.getText().length() != 8 || validarSalida  == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbSalida.setTextFill(Color.AQUA);
                    txtSalida.setText("");
                } else{
                    guardar();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnCancelar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    valoresPredeterminados();
                    limpiarControles();
                    cargarDatos();
                    break;
                }    
        }
    }
    
    public void guardar(){
        Horario registro = new Horario();
        registro.setHoraEntrada(txtEntrada.getText());
        registro.setHoraSalida(txtSalida.getText());
        registro.setLunes(chkLunes.isSelected());
        registro.setMartes(chkMartes.isSelected());
        registro.setMiercoles(chkMiercoles.isSelected());
        registro.setJueves(chkJueves.isSelected());
        registro.setViernes(chkViernes.isSelected());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getHoraEntrada());
            procedimiento.setString(2, registro.getHoraSalida());
            procedimiento.setBoolean(3, registro.isLunes());
            procedimiento.setBoolean(4, registro.isMartes());
            procedimiento.setBoolean(5, registro.isMiercoles());
            procedimiento.setBoolean(6, registro.isJueves());
            procedimiento.setBoolean(7, registro.isViernes());
            procedimiento.execute();
            listaHorario.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cargarDatos(){
        tblHorarios.setItems(getHorario());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Horario, Integer>("codigoHorario"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<Horario, String>("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<Horario, String>("horaSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("viernes"));
    }
    
    public ObservableList<Horario> getHorario(){
        ArrayList<Horario> lista = new ArrayList<Horario>();
        try{
            PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarHorarios}");
            ResultSet resultado = procedimiento.executeQuery();
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
        return listaHorario = FXCollections.observableArrayList(lista);
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                    valoresPredeterminados();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnCancelar.setDisable(false);
                    tblHorarios.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    break;
            default:
                    if(!tblHorarios.getSelectionModel().isEmpty()){
                        int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarHorario(?)}");
                                procedimiento.setInt(1, ((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getCodigoHorario());
                                procedimiento.execute();
                                listaHorario.remove(tblHorarios.getSelectionModel().getSelectedIndex());
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
    
    public void seleccionarElemento(){
            if(tblHorarios.getSelectionModel().isEmpty()){
                                
            } else if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){

            }else{
                txtEntrada.setText(String.valueOf(((Horario) tblHorarios.getSelectionModel().getSelectedItem()).getHoraEntrada()));
                txtSalida.setText(((Horario) tblHorarios.getSelectionModel().getSelectedItem()).getHoraSalida());
                chkLunes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isLunes());
                chkMartes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMartes());
                chkMiercoles.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMiercoles());
                chkJueves.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isJueves());
                chkViernes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isViernes());
            }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblHorarios.setDisable(true);
                    btnEditar.setText("Actualizar");
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                boolean validarEntrada = validarHora(txtEntrada.getText());
                boolean validarSalida = validarHora(txtSalida.getText());
                if(txtEntrada.getText().equals("") || txtSalida.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                } else if(txtEntrada.getText().length() != 8 || validarEntrada  == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbEntrada.setTextFill(Color.AQUA);
                    txtEntrada.setText("");
                } else if(txtSalida.getText().length() != 8 || validarSalida  == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbSalida.setTextFill(Color.AQUA);
                    txtSalida.setText("");
                } else{
                    btnEditar.setText("Editar");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tblHorarios.setDisable(false);
                    actualizar();                                   
                    tipoDeOperacion = operaciones.NINGUNO;
                    valoresPredeterminados();
                    limpiarControles();
                    cargarDatos();
            break;
                }
        }
    }
    
    //Actualiza los datos de la base de datos y del modelo de datos.
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarHorario(?,?,?,?,?,?,?,?)}");           
            Horario registro  = (Horario)tblHorarios.getSelectionModel().getSelectedItem();
            registro.setHoraEntrada(txtEntrada.getText());
            registro.setHoraSalida(txtSalida.getText());
            registro.setLunes(chkLunes.isSelected());
            registro.setMartes(chkMartes.isSelected());
            registro.setMiercoles(chkMiercoles.isSelected());
            registro.setJueves(chkJueves.isSelected());
            registro.setViernes(chkViernes.isSelected());
            
   
            procedimiento.setInt(1, registro.getCodigoHorario());
            procedimiento.setString(2, registro.getHoraEntrada());
            procedimiento.setString(3, registro.getHoraSalida());
            procedimiento.setBoolean(4, registro.isLunes());
            procedimiento.setBoolean(5, registro.isMartes());
            procedimiento.setBoolean(6, registro.isMiercoles());
            procedimiento.setBoolean(7, registro.isJueves());
            procedimiento.setBoolean(8, registro.isViernes());
            procedimiento.execute();           
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void valoresPredeterminados(){
        lbEntrada.setTextFill(Color.BLACK);
        lbSalida.setTextFill(Color.BLACK);
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

    public void desactivarControles(){
        txtEntrada.setEditable(false);
        txtSalida.setEditable(false);
        chkLunes.setDisable(true);
        chkMartes.setDisable(true);
        chkMiercoles.setDisable(true);
        chkJueves.setDisable(true);
        chkViernes.setDisable(true); 
    }
    
    public void activarControles(){
        txtEntrada.setEditable(true);
        txtSalida.setEditable(true);
        chkLunes.setDisable(false);
        chkMartes.setDisable(false);
        chkMiercoles.setDisable(false);
        chkJueves.setDisable(false);
        chkViernes.setDisable(false); 
    }
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
        } else{
        txtEntrada.setText("");
        txtSalida.setText("");
        chkLunes.setSelected(false);
        chkMartes.setSelected(false);
        chkMiercoles.setSelected(false);
        chkJueves.setSelected(false);
        chkViernes.setSelected(false);
        }
    }
    
    public void cancelar(){
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        btnEditar.setText("Editar");
        tblHorarios.setDisable(false);
        tblHorarios.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
        valoresPredeterminados();
        desactivarControles();
        limpiarControles();
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaMedicoEspecialidad(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaMedicoEspecialidad();
            }
        } else {
           this.escenarioPrincipal.ventanaMedicoEspecialidad();
        }        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
}
