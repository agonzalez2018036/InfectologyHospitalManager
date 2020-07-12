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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import org.alvarogonzalez.bean.ContactoUrgencia;
import org.alvarogonzalez.bean.Paciente;
import org.alvarogonzalez.db.Conexion;
import static org.alvarogonzalez.db.Conexion.getInstancia;
import org.alvarogonzalez.sistema.Principal;

public class ContactoUrgenciaController implements Initializable {
    private Principal escenarioPrincipal;
    private boolean validacion;
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ContactoUrgencia> listaContactoUrgencia;   
    private ObservableList<Paciente> listaPaciente;

    //Objetos FXML
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumeroContacto;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblContactos;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumContacto;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colCodigoUrgencia;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Label lbNombres;
    @FXML private Label lbApellidos;
    @FXML private Label lbNumero;    
    
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
                boolean telPersonal = validacionNumerica(txtNumeroContacto.getText());
                boolean validarNombre = validacionDeTextField(txtNombres.getText());
                boolean validarApellido = validacionDeTextField(txtApellidos.getText()); 
                    if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || 
                    txtNumeroContacto.getText().equals("") || cmbCodigoPaciente.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "No ha llenado todos los campos", "Error", JOptionPane.WARNING_MESSAGE);   
                    } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombres.setTextFill(Color.AQUA);
                        txtNombres.setText("");
                    } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");
                    } else if(txtNumeroContacto.getText().length() != 8 || txtNumeroContacto.getText().length() < 8 || telPersonal == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNumero.setTextFill(Color.AQUA);
                        txtNumeroContacto.setText("");
                    } else{
                        guardar();
                        desactivarControles();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        btnReporte.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        limpiarControles();
                        cargarDatos();                    
                    }
                break;
        }
    }
    
    public void guardar(){
        valoresPredeterminados();
        ContactoUrgencia registro = new ContactoUrgencia();       
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNumeroContacto(txtNumeroContacto.getText());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContactoUrgencia(?,?,?,?)}");
                procedimiento.setString(1, registro.getNombres());
                procedimiento.setString(2, registro.getApellidos());
                procedimiento.setString(3, registro.getNumeroContacto());
                procedimiento.setInt(4, registro.getCodigoPaciente());
                procedimiento.executeQuery();
                listaContactoUrgencia.add(registro);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "El registro ya existe", "Error", JOptionPane.WARNING_MESSAGE);
            }
    }
    
    public void cargarDatos(){
        tblContactos.setItems(getContactoUrgencia());
        colCodigoUrgencia.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoContactoUrgencia"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory <ContactoUrgencia,String>("apellidos"));
        colNumContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia,String>("numeroContacto"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoPaciente"));
    }
    
    public ObservableList<ContactoUrgencia> getContactoUrgencia(){
        ArrayList<ContactoUrgencia> lista = new ArrayList<ContactoUrgencia>();
            try{
                PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarContactosUrgencia}");
                ResultSet resultado = procedimiento.executeQuery();
                    while(resultado.next()){
                        lista.add(new ContactoUrgencia(resultado.getInt("codigoContactoUrgencia"),
                                resultado.getString("nombres"),
                                resultado.getString("apellidos"),
                                resultado.getString("numeroContacto"),
                                resultado.getInt("codigoPaciente")));
                    }
            } catch(Exception e){
                e.printStackTrace();
            }
        return listaContactoUrgencia = FXCollections.observableList(lista);
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
            if(tblContactos.getSelectionModel().isEmpty()){
                             
            } else if(tipoDeOperacion == operaciones.GUARDAR){
                
            } else{
                txtNombres.setText(((ContactoUrgencia) tblContactos.getSelectionModel().getSelectedItem()).getNombres());
                txtApellidos.setText(((ContactoUrgencia) tblContactos.getSelectionModel().getSelectedItem()).getApellidos());
                txtNumeroContacto.setText(((ContactoUrgencia) tblContactos.getSelectionModel().getSelectedItem()).getNumeroContacto());
                cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
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
                if(tblContactos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Telefono",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContactoUrgencia(?)}");
                            procedimiento.setInt(1, ((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
                            procedimiento.execute();
                            listaContactoUrgencia.remove(tblContactos.getSelectionModel().getSelectedIndex());
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
                if(tblContactos.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblContactos.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    cmbCodigoPaciente.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
            break;
            case ACTUALIZAR:
                    boolean telPersonal = validacionNumerica(txtNumeroContacto.getText());
                    boolean validarNombre = validacionDeTextField(txtNombres.getText());
                    boolean validarApellido = validacionDeTextField(txtApellidos.getText());                  
                    if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || 
                    txtNumeroContacto.getText().equals("") || cmbCodigoPaciente.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo", "Error", JOptionPane.WARNING_MESSAGE);   
                    } else if(txtNombres.getText().length() > 100 || validarNombre == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNombres.setTextFill(Color.AQUA);
                        txtNombres.setText("");
                    } else if(txtApellidos.getText().length() > 100 || validarApellido == true){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbApellidos.setTextFill(Color.AQUA);
                        txtApellidos.setText("");
                    } else if(txtNumeroContacto.getText().length() != 10 || txtNumeroContacto.getText().length() < 8 || telPersonal == false){
                        JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente", "Error", JOptionPane.WARNING_MESSAGE);
                        lbNumero.setTextFill(Color.AQUA);
                        txtNumeroContacto.setText("");
                    }else{
                        actualizar();
                        btnEditar.setText("Editar");
                        btnReporte.setText("Reporte");
                        tipoDeOperacion = operaciones.NINGUNO;
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false); 
                        tblContactos.setDisable(false);
                        limpiarControles();
                        desactivarControles();
                        cargarDatos();
                        valoresPredeterminados();
                    }
                break;
        }
    }
           
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarContactoUrgencia(?,?,?,?)}");
            ContactoUrgencia registro  = (ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem();
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumeroContacto.getText());
            procedimiento.setInt(1, registro.getCodigoContactoUrgencia());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNumeroContacto());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void valoresPredeterminados(){
        lbNombres.setTextFill(Color.BLACK);
        lbApellidos.setTextFill(Color.BLACK);
        lbNumero.setTextFill(Color.BLACK);
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
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumeroContacto.setEditable(false);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumeroContacto.setEditable(true);
        cmbCodigoPaciente.setDisable(false);
    }  
    
    public void limpiarControles(){
        if(tipoDeOperacion == operaciones.GUARDAR || tipoDeOperacion == operaciones.ACTUALIZAR){
            
        }else{
        txtNombres.setText("");
        txtApellidos.setText("");
        txtNumeroContacto.setText("");
        cmbCodigoPaciente.getSelectionModel().select(null); 
        tblContactos.getSelectionModel().clearSelection();
        }
    }
    
    public void cancelar(){
        if(btnReporte.getText().equals("Cancelar")){
        desactivarControles();
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        tblContactos.setDisable(false);
        btnEditar.setText("Editar");
        btnReporte.setText("Reporte");
        tipoDeOperacion = operaciones.NINGUNO;         
        limpiarControles();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPaciente());
    } 

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaPacientes(){
        if(tipoDeOperacion == operaciones.GUARDAR){
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea cancelar su operacion?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(respuesta == JOptionPane.YES_OPTION){
                this.escenarioPrincipal.ventanaPacientes();
            }
        } else {
            this.escenarioPrincipal.ventanaPacientes();
        }   
    }  
}