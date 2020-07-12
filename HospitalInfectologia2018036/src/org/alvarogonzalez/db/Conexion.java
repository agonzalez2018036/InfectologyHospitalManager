package org.alvarogonzalez.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.jdbc.Driver;

public class Conexion {
    private Connection conexion;
    private Statement sentencia;
    private static Conexion instancia;
public Conexion(){
    try{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBHospitalInfectologia2018036?useSSL=false", "root", "admin");
    }catch(ClassNotFoundException e){
        e.printStackTrace();
    }catch(InstantiationException e){
        e.printStackTrace();
    }catch(IllegalAccessException e){
        e.printStackTrace();
    }catch (SQLException e){
        e.printStackTrace();
    }
}

    public static Conexion getInstancia(){
        if(instancia == null){
            instancia = new Conexion();
        }
        return instancia;
    }
    
    public Connection getConexion(){
        return conexion;
    }
    public void setConexion(Connection conexion){
        this.conexion = conexion;
    }
}

