package org.alvarogonzalez.controller;

//Librerias utilizadas
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
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.report.GenerarReporte;
import org.alvarogonzalez.sistema.Principal;


public class MedicoController implements Initializable {
    private Principal escenarioPrincipal;  
    private boolean validacion;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;

    
    //Objetos FXML
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtLicenciaMedica;
    @FXML private TextField txtHoraEntrada;
    @FXML private TextField txtHoraSalida;
    @FXML private TextField txtTurnoMaximo;
    @FXML private TableView tblMedicos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colLicenciaMedica;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colEntrada;
    @FXML private TableColumn colSalida;
    @FXML private TableColumn colTurno;
    @FXML private TableColumn colSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    @FXML private Label lbLicencia;
    @FXML private Label lbNombre;
    @FXML private Label lbApellidos;
    @FXML private Label lbHoraEntrada;
    @FXML private Label lbHoraSalida;
    @FXML private Label lbTurnoMaximo;
    @FXML private Label lbSexo;
    @FXML private ComboBox cmbSexo;
    
    //Activa todos los textField, limpia los controles, guarda los datos tanto en el modelo de datos como en la base de datos
    //mete los datos ingresados un la TableView
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
                boolean validar = validacionNumerica(txtLicenciaMedica.getText());
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                boolean validarEntrada = validarHora(txtHoraEntrada.getText());
                boolean validarSalida = validarHora(txtHoraSalida.getText());
                    if(txtLicenciaMedica.getText().equals("") || txtNombres.getText().equals("")|| txtApellidos.getText().equals("") || txtHoraEntrada.getText().equals("")
                    || txtHoraSalida.getText().equals("") || cmbSexo.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                    } else if(txtLicenciaMedica.getText().length() != 8 || validar == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbLicencia.setTextFill(Color.AQUA);
                        txtLicenciaMedica.setText("");                                
                    } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombre.setTextFill(Color.AQUA);
                        txtNombres.setText("");                            
                    } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);                               
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");
                    } else if(txtHoraEntrada.getText().length() != 8 || validarEntrada == false){
                        lbHoraEntrada.setTextFill(Color.AQUA);
                        txtHoraEntrada.setText("");
                        JOptionPane.showMessageDialog(null, "Use el siguiente formato: HH:MM:SS", "Error", JOptionPane.WARNING_MESSAGE);
                    } else if(txtHoraSalida.getText().length() != 8 || validarSalida == false){
                        lbHoraSalida.setTextFill(Color.AQUA);
                        txtHoraSalida.setText("");
                        JOptionPane.showMessageDialog(null, "Use el siguiente formato: HH:MM:SS", "Error", JOptionPane.WARNING_MESSAGE);
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
    
    //guarda los datos almacenados en los textfield en el modelo de datos
    //pasa los datos del modelo de datos a la base de datos
    public void guardar(){
        Medico registro = new Medico();
        registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setHoraEntrada(txtHoraEntrada.getText());
        registro.setHoraSalida(txtHoraSalida.getText());
        registro.setSexo((String)cmbSexo.getSelectionModel().getSelectedItem());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedico(?,?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getLicenciaMedica());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getHoraEntrada());
            procedimiento.setString(5, registro.getHoraSalida());
            procedimiento.setString(6, registro.getSexo());
            procedimiento.execute();
            listaMedico.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //con la ayuda de la propiedad setCellValueFactory, mete los datos en las columnas correspondientes de la TableView
    public void cargarDatos(){
        tblMedicos.setItems(getMedicos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("codigoMedico"));
        colLicenciaMedica.setCellValueFactory(new PropertyValueFactory<Medico, String>("licenciaMedica"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Medico,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Medico,String>("Apellidos"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaSalida"));
        colTurno.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("turnoMaximo"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Medico, String>("sexo"));
    }
    
    //Este método es el encargado de traer los datos de la base de datos por medio del procedimiento ListarMedicos y los almacena en el modelo de datos
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
    
    
    //Metodo que introduce los datos del registro seleccionado en los textfield
    public void seleccionarElemento(){
        if(tblMedicos.getSelectionModel().isEmpty()){
                                
        } else if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){

        }else{
            txtLicenciaMedica.setText(String.valueOf(((Medico) tblMedicos.getSelectionModel().getSelectedItem()).getLicenciaMedica()));
            txtNombres.setText(((Medico) tblMedicos.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((Medico) tblMedicos.getSelectionModel().getSelectedItem()).getApellidos());
            cmbSexo.getSelectionModel().select(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getSexo());
            txtHoraEntrada.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraEntrada());
            txtHoraSalida.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraSalida());
            txtTurnoMaximo.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getTurnoMaximo()));               
        }
    }
    
    //Elimina los datos de la TableView, del modelo de datos y de la base de datos
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                    valoresPredeterminados();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblMedicos.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    break;
            default:
                    if(!tblMedicos.getSelectionModel().isEmpty()){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                                procedimiento.setInt(1, ((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
                                procedimiento.execute();
                                listaMedico.remove(tblMedicos.getSelectionModel().getSelectedIndex());
                                limpiarControles();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }else if(respuesta == JOptionPane.NO_OPTION){
                            desactivarControles();
                            limpiarControles();
                        }
                    } else{
                        JOptionPane.showMessageDialog(null, "Debe Seleccionar un elemento");
                    }
        }
    }
    
    //busca un dato dentro de la base de datos
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
    
    //Cambia los valores de la base de datos y de la TableView
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblMedicos.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                boolean validar = validacionNumerica(txtLicenciaMedica.getText());
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                boolean validarEntrada = validarHora(txtHoraEntrada.getText());
                boolean validarSalida = validarHora(txtHoraSalida.getText());
                    if(txtLicenciaMedica.getText().equals("") || txtNombres.getText().equals("")|| txtApellidos.getText().equals("") || txtHoraEntrada.getText().equals("")
                    || txtHoraSalida.getText().equals("") || cmbSexo.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                    } else if(txtLicenciaMedica.getText().length() > 13 || validar == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbLicencia.setTextFill(Color.AQUA);
                        txtLicenciaMedica.setText("");                                
                    } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombre.setTextFill(Color.AQUA);
                        txtNombres.setText("");                            
                    } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);                               
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");
                    } else if(txtHoraEntrada.getText().length() != 8 || validarEntrada == false){
                        JOptionPane.showMessageDialog(null, "Use el siguiente formato: HH:MM:SS", "Error", JOptionPane.WARNING_MESSAGE);
                        lbHoraEntrada.setTextFill(Color.AQUA);
                        txtHoraEntrada.setText("");                 
                    } else if(txtHoraSalida.getText().length() != 8  || validarSalida == false){
                        JOptionPane.showMessageDialog(null, "Use el siguiente formato: HH:MM:SS", "Error", JOptionPane.WARNING_MESSAGE);
                        lbHoraSalida.setTextFill(Color.AQUA);
                        txtHoraSalida.setText("");
                    } else{
                        actualizar();
                        desactivarControles();
                        btnEditar.setText("Editar");
                        btnReporte.setText("Reporte");
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        tblMedicos.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;                                   
                        valoresPredeterminados();
                        limpiarControles();
                        cargarDatos();
                    }
            break;
        }
    }
    
    //Actualiza los datos de la base de datos y del modelo de datos.
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarMedico(?,?,?,?,?,?,?)}");           
            Medico registro  = (Medico)tblMedicos.getSelectionModel().getSelectedItem();
            registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setHoraEntrada(txtHoraEntrada.getText());
            registro.setHoraSalida(txtHoraSalida.getText());
            registro.setSexo((String)cmbSexo.getSelectionModel().getSelectedItem());       
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getLicenciaMedica());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setString(4, registro.getApellidos());
            procedimiento.setString(5, registro.getHoraEntrada());
            procedimiento.setString(6, registro.getHoraSalida());
            procedimiento.setString(7, registro.getSexo());
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
                boolean validar = validacionNumerica(txtLicenciaMedica.getText());
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText());
                boolean validarEntrada = validacionDeTextField(txtHoraEntrada.getText());
                boolean validarSalida = validacionDeTextField(txtHoraSalida.getText());
                    if(txtLicenciaMedica.getText().equals("") || txtNombres.getText().equals("")|| txtApellidos.getText().equals("") || txtHoraEntrada.getText().equals("")
                    || txtHoraSalida.getText().equals("") || cmbSexo.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos");
                    } else if(txtLicenciaMedica.getText().length() > 13 || validar == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbLicencia.setTextFill(Color.AQUA);
                        txtLicenciaMedica.setText("");                                
                    } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombre.setTextFill(Color.AQUA);
                        txtNombres.setText("");                            
                    } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);                               
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");
                    } else if(txtHoraEntrada.getText().length() > 8  || validarEntrada == true){
                        lbHoraEntrada.setTextFill(Color.AQUA);
                        txtHoraEntrada.setText("");
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    } else if(txtHoraSalida.getText().length() > 8 || validarSalida == true){
                        lbHoraSalida.setTextFill(Color.AQUA);
                        txtHoraSalida.setText("");
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                    }                 
                    else{
                        btnEditar.setText("Editar");
                        btnReporte.setText("Reporte");
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        tblMedicos.setDisable(false);
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
        GenerarReporte.mostrarReporte("ReporteMedicos.jasper", "Reporte de Médicos", parametros);
    }
    
    //regresa el color blanco a los textfield
    public void valoresPredeterminados(){
        lbLicencia.setTextFill(Color.BLACK);
        lbNombre.setTextFill(Color.BLACK);
        lbApellidos.setTextFill(Color.BLACK);
        lbHoraEntrada.setTextFill(Color.BLACK);
        lbHoraSalida.setTextFill(Color.BLACK);
        lbTurnoMaximo.setTextFill(Color.BLACK); 
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
        int numero = 0;
        try{
            numero = Integer.parseInt(numeros);
            return true; 
        } catch(Exception e){
            return false;
        }
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
    
    // Metodo que desactiva los textfield
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtLicenciaMedica.setEditable(false);
        cmbSexo.setDisable(true);
        txtHoraEntrada.setEditable(false);
        txtHoraSalida.setEditable(false);
        txtTurnoMaximo.setEditable(false);
    }
    
    // Metodo que activa los texfield
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtLicenciaMedica.setEditable(true);
        cmbSexo.setDisable(false);
        txtHoraEntrada.setEditable(true);
        txtHoraSalida.setEditable(true);
        txtTurnoMaximo.setEditable(false);
    }  
    
    // Limpia todos los textfield
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
        } else{
        txtNombres.setText("");
        txtApellidos.setText("");
        txtLicenciaMedica.setText("");
        cmbSexo.getSelectionModel().select(null);
        txtHoraEntrada.setText("");
        txtHoraSalida.setText("");
        txtTurnoMaximo.setText("");
        tblMedicos.getSelectionModel().clearSelection();
        }
    }    
    
    // Metodo que reestablece todo al momento inicial
    public void cancelar(){
        if(btnReporte.getText().equals("Cancelar")){
        desactivarControles();
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        btnEditar.setText("Editar");
        btnReporte.setText("Reporte");
        tblMedicos.setDisable(false);
        tipoDeOperacion = operaciones.NINGUNO;         
        limpiarControles();
        }
    }      
           
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
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
    
    public void ventanaTelefonoMedico(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaTelefonoMedico();
            }
        } else {
            this.escenarioPrincipal.ventanaTelefonoMedico();
        }        
    }
}
