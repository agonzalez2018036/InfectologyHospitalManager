package org.alvarogonzalez.controller;

//Librerias Utilizadas
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.alvarogonzalez.bean.Paciente;
import org.alvarogonzalez.sistema.Principal;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.alvarogonzalez.report.GenerarReporte;

public class PacienteController implements Initializable {
    private Principal escenarioPrincipal;
    private DatePicker fecha;
    private boolean validacion;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente;
    
    //Objetos FXML
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos; 
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    @FXML private TextField txtDPI;
    @FXML private TableView tblPacientes;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colDPI;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colEdad;
    @FXML private TableColumn colSexo; 
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    @FXML private GridPane grpFecha;
    @FXML private Label lbNombres;
    @FXML private Label lbApellidos;
    @FXML private Label lbDireccion;
    @FXML private Label lbOcupacion;
    @FXML private Label lbNacimiento;
    @FXML private Label lbDPI;
    @FXML private Label lbSexo;
    @FXML private ComboBox cmbSexo;
    
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
                    boolean validarNombre = validacionDeTextField(txtNombres.getText());
                    boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                    boolean validarOcupacion = validacionDeTextField(txtOcupacion.getText());
                    boolean validarDPI = validacionNumerica(txtDPI.getText());
                        if(txtNombres.getText().equals("") || txtApellidos.getText().equals("")|| txtDireccion.getText().equals("") || txtOcupacion.getText().equals("")
                        || txtDPI.getText().equals("") || cmbSexo.getSelectionModel().getSelectedItem() == null || fecha.selectedDateProperty().get() == null){
                            JOptionPane.showMessageDialog(null, "No ha llenado todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                        } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                            JOptionPane.showMessageDialog(null, "Ha llenado un campo", "Error", JOptionPane.WARNING_MESSAGE);
                            lbNombres.setTextFill(Color.AQUA);
                            txtNombres.setText("");
                        } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                            JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                            lbApellidos.setTextFill(Color.AQUA);
                            txtApellidos.setText("");
                        } else if(txtDireccion.getText().length() > 150){
                            JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                            lbDireccion.setTextFill(Color.AQUA);
                            txtDireccion.setText("");
                        } else if(txtOcupacion.getText().length() > 50 || validarOcupacion == true){
                            JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                            lbOcupacion.setTextFill(Color.AQUA);
                            txtOcupacion.setText("");
                        } else if(txtDPI.getText().length() != 13 || validarDPI == false){
                            JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                            lbDPI.setTextFill(Color.AQUA);
                            txtDPI.setText("");
                        } else{
                            guardar();
                            desactivarControles();
                            btnNuevo.setText("Nuevo");
                            btnEliminar.setText("Eliminar");
                            btnEditar.setDisable(false);
                            btnReporte.setDisable(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            limpiarControles();
                            valoresPredeterminados();
                            cargarDatos();
                        }
                    break;
        }
    }
    
    public void guardar(){
        Paciente registro = new Paciente();
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setDireccion(txtDireccion.getText());
        registro.setOcupacion(txtOcupacion.getText());
        registro.setFechaNacimiento(fecha.getSelectedDate());
        registro.setDPI(txtDPI.getText());
        registro.setSexo((String)cmbSexo.getSelectionModel().getSelectedItem());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarPaciente(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombres());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getDireccion());
            procedimiento.setString(4, registro.getOcupacion());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDPI());
            procedimiento.setString(7, registro.getSexo());
            procedimiento.execute();
            listaPaciente.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cargarDatos(){
        tblPacientes.setItems(getPaciente());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente, String>("DPI"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente, String>("Nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente, String>("Apellidos"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("ocupacion"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("edad"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
    }
    
    public ObservableList<Paciente> getPaciente(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
            try{
                PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarPacientes}");
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
            } catch(Exception e){
                e.printStackTrace();
            }
        return listaPaciente = FXCollections.observableList(lista);
    }

    public void seleccionarElemento(){
        if(tblPacientes.getSelectionModel().isEmpty()){

        } else if(tipoDeOperacion == operaciones.GUARDAR){
            
        } else{
            txtNombres.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidos());
            txtDireccion.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDireccion());
            txtOcupacion.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getOcupacion());
            fecha.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            txtDPI.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDPI());
            cmbSexo.getSelectionModel().select(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
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
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;                   
                    limpiarControles();
                    break;
            default:
                    if(!tblPacientes.getSelectionModel().isEmpty()){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                                procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                                procedimiento.execute();
                                listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
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
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblPacientes.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;                
            case ACTUALIZAR:
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                boolean validarOcupacion = validacionDeTextField(txtOcupacion.getText());
                boolean validarDPI = validacionNumerica(txtDPI.getText());
                if(txtNombres.getText().equals("") || txtApellidos.getText().equals("")|| txtDireccion.getText().equals("") || txtOcupacion.getText().equals("")
                || txtDPI.getText().equals("") || cmbSexo.getSelectionModel().getSelectedItem() == null || fecha.selectedDateProperty().get() == null){
                    JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbNombres.setTextFill(Color.AQUA);
                    txtNombres.setText("");
                } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbApellidos.setTextFill(Color.AQUA);
                    txtApellidos.setText("");
                } else if(txtDireccion.getText().length() > 150){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbDireccion.setTextFill(Color.AQUA);
                    txtDireccion.setText("");
                } else if(txtOcupacion.getText().length() > 50 || validarOcupacion == true){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbOcupacion.setTextFill(Color.AQUA);
                    txtOcupacion.setText("");
                } else if(txtDPI.getText().length() > 13 || validarDPI == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbDPI.setTextFill(Color.AQUA);
                    txtDPI.setText("");
                } else {
                    actualizar();
                    desactivarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tblPacientes.setDisable(false);
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarPaciente(?,?,?,?,?,?,?,?)}");
            Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setDPI(txtDPI.getText());            
            registro.setApellidos(txtApellidos.getText());
            registro.setNombres(txtNombres.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());          
            registro.setSexo((String)cmbSexo.getSelectionModel().getSelectedItem());
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getDPI());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNombres());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccion());
            procedimiento.setString(7, registro.getOcupacion());
            procedimiento.setString(8, registro.getSexo());
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
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                boolean validarOcupacion = validacionDeTextField(txtOcupacion.getText());
                boolean validarDPI = validacionNumerica(txtDPI.getText());
                if(txtNombres.getText().equals("") || txtApellidos.getText().equals("")|| txtDireccion.getText().equals("") || txtOcupacion.getText().equals("")
                || txtDPI.getText().equals("") || cmbSexo.getSelectionModel().getSelectedItem() == null || fecha.selectedDateProperty().get() == null){
                    JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbNombres.setTextFill(Color.AQUA);
                    txtNombres.setText("");
                } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbApellidos.setTextFill(Color.AQUA);
                    txtApellidos.setText("");
                } else if(txtDireccion.getText().length() > 150){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbDireccion.setTextFill(Color.AQUA);
                    txtDireccion.setText("");
                } else if(txtOcupacion.getText().length() > 50 || validarOcupacion == true){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbOcupacion.setTextFill(Color.AQUA);
                    txtOcupacion.setText("");
                } else if(txtDPI.getText().length() > 13 || validarDPI == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    lbDPI.setTextFill(Color.AQUA);
                    txtDPI.setText("");
                } else {
                    actualizar();
                    desactivarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tblPacientes.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    valoresPredeterminados();
                    limpiarControles();
                    cargarDatos();
                }
                break;            
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico", null);
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
    }

    public void valoresPredeterminados(){
        lbDireccion.setTextFill(Color.BLACK);
        lbNombres.setTextFill(Color.BLACK);
        lbApellidos.setTextFill(Color.BLACK);
        lbOcupacion.setTextFill(Color.BLACK);
        lbNacimiento.setTextFill(Color.BLACK);
        lbDPI.setTextFill(Color.BLACK); 
        lbSexo.setTextFill(Color.BLACK);
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
        long numero = 0;
        try{
            numero = Long.parseLong(numeros);
            return true; 
        } catch(Exception e){
            return false;
        }
    }
        
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false); 
        txtDireccion.setEditable(false);
        txtOcupacion.setEditable(false);
        txtDPI.setEditable(false);
        cmbSexo.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true); 
        txtDireccion.setEditable(true);
        txtOcupacion.setEditable(true);
        txtDPI.setEditable(true);
        cmbSexo.setDisable(false);
        fecha.setDisable(false);
    }  
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        } else{
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDireccion.setText("");
        txtOcupacion.setText("");
        txtDPI.setText("");
        cmbSexo.getSelectionModel().select(null);
        tblPacientes.getSelectionModel().clearSelection();
        fecha.selectedDateProperty().set(null);
        }
    }
    
        public void cancelar(){
        if(btnReporte.getText().equals("Cancelar")){
        desactivarControles();
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        btnEditar.setText("Editar");
        btnReporte.setText("Reporte");
        tblPacientes.setDisable(false);
        tipoDeOperacion = operaciones.NINGUNO;         
        limpiarControles();
        }
    }  
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/alvarogonzalez/resource/DatePicker.css");
        grpFecha.add(fecha, 0, 0);
        fecha.setDisable(true);
        cmbSexo.getItems().add("femenino");
        cmbSexo.getItems().add("masculino");
        cmbSexo.getItems().add("otro");
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaContactoUrgencia(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaContactoUrgencia();
            }
        } else {
            this.escenarioPrincipal.ventanaContactoUrgencia();
        }   
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