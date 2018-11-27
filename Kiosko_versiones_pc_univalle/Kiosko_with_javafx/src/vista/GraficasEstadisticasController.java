/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import BD.Medicion;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author Miguel Askar
 */
public class GraficasEstadisticasController implements Initializable {

    @FXML
    private LineChart<String, Double> chartSistolica;
    @FXML
    private LineChart<String, Double> chartDiastolica;
    @FXML
    private LineChart<String, Double> chartMedia;
    @FXML
    private LineChart<String, Double> chartPulso;
    @FXML
    private LineChart<String, Double> chartECG;
    @FXML
    private LineChart<String, Double> chartSPO2;
    @FXML
    private LineChart<String, Double> chartHeart;
    @FXML
    private LineChart<String, Double> chartRESP;
    
    private int idPersonalizado;
    private List<Medicion> mediciones;
    private Kiosko programaPrincipal;
    
    private Stage thisStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setIdPersonalizado(int id)
    {
        idPersonalizado= id;
    }
    
    public void obtenerMediciones()
    {
       EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();        
       Query queryMedicionFindAll = em.createNativeQuery("SELECT * from medicion m WHERE idPersonalizada= '"+ idPersonalizado +"'", Medicion.class);
       mediciones = queryMedicionFindAll.getResultList();
    }
    
    public void dibujarCharts()
    {
        dibujarSistolica();
        dibujarDiastolica();
        dibujarMedia();
        dibujarPulso();
        dibujarECG();
        dibujarSPO2();
        dibujarHearRate();
        dibujarResp();        
    }
    
    private void dibujarSistolica()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartSistolica.getData().add(series);
        double ejeX= 0;
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getPresionSistolica().substring(1, mediciones.get(i).getPresionSistolica().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            for(int j=0; j<datosNumericos.size(); j++) //Se agrega al chart cada dato del vector numérico obtenido.
            {               
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    private void dibujarDiastolica()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartDiastolica.getData().add(series);
        int ejeX= 0;
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getPresionDiastolica().substring(1, mediciones.get(i).getPresionDiastolica().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            for(int j=0; j<datosNumericos.size(); j++) //Se agrega al chart cada dato del vector numérico obtenido.
            {               
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    private void dibujarMedia()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartMedia.getData().add(series);
        int ejeX= 0;
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getMed().substring(1, mediciones.get(i).getMed().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            for(int j=0; j<datosNumericos.size(); j++) //Se agrega al chart cada dato del vector numérico obtenido.
            {               
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    private void dibujarPulso()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartPulso.getData().add(series);
        int ejeX= 0;
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getPulso().substring(1, mediciones.get(i).getPulso().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            for(int j=0; j<datosNumericos.size(); j++) //Se agrega al chart cada dato del vector numérico obtenido.
            {               
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    private void dibujarECG()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartECG.getData().add(series);
        int ejeX= 0;
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getEcg().substring(1, mediciones.get(i).getEcg().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            for(int j=0; j<datosNumericos.size(); j++) //Se agrega al chart cada dato del vector numérico obtenido.
            {               
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    private void dibujarSPO2()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartSPO2.getData().add(series);
        int ejeX= 0;        
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getSpo2().substring(1, mediciones.get(i).getSpo2().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            int tamanio= (datosNumericos.size()-1)/4;
            for(int j=0; j<datosNumericos.size(); j+=tamanio) //Se agrega al chart cada dato del vector numérico obtenido.
            {
                System.err.println(datosNumericos.get(j));
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    private void dibujarHearRate()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartHeart.getData().add(series);
        int ejeX= 0;
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getHr().substring(1, mediciones.get(i).getHr().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            for(int j=0; j<datosNumericos.size(); j++) //Se agrega al chart cada dato del vector numérico obtenido.
            {               
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    private void dibujarResp()
    {
        XYChart.Series<String, Double> series= new XYChart.Series<>();
        chartRESP.getData().add(series);
        int ejeX= 0;
        for(int i=0; i<mediciones.size(); i++) //Se recorren todas las mediciones del paciente para dicha medición personalizada.
        {
            List<Double> datosNumericos= new ArrayList<Double>();
            String datos= mediciones.get(i).getResp().substring(1, mediciones.get(i).getResp().length()-1);
            StringTokenizer tokens= new StringTokenizer(datos, ", ");
            while(tokens.hasMoreTokens()) //Se convierten los datos almacenados por cada medicion en un arreglo de enteros.
            {
                datosNumericos.add(Double.parseDouble(tokens.nextToken()));
            }
            for(int j=0; j<datosNumericos.size(); j++) //Se agrega al chart cada dato del vector numérico obtenido.
            {               
                series.getData().add(new XYChart.Data<>(String.valueOf(ejeX), datosNumericos.get(j)));
                ejeX++; //Se aumenta una posición en el eje X (tiempo).
            }
        }
    }
    
    public void setProgramaPrincipal(Kiosko programaPrincipal) 
    {
        this.programaPrincipal = programaPrincipal;        
    }
    
    public void setStage(Stage stage)
    {
        this.thisStage= stage;
    }
    
}
