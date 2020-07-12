package org.alvarogonzalez.report;

import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.view.JasperViewer;
import java.util.Map;
import java.io.InputStream;
import org.alvarogonzalez.db.Conexion;

public class GenerarReporte {
    
    public static void mostrarReporte(String nombreReporte, String titulo, Map parametro){
        InputStream reporte = GenerarReporte.class.getResourceAsStream(nombreReporte);
        try{
            JasperReport reporteMaestro = (JasperReport)JRLoader.loadObject(reporte);
            JasperPrint reporteImpreso = JasperFillManager.fillReport(reporteMaestro, parametro, Conexion.getInstancia().getConexion());
            JasperViewer visor = new JasperViewer(reporteImpreso, false);
            visor.setTitle(titulo);
            visor.setVisible(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
