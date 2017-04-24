/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba_javafx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 * Clase main
 * @author carlo
 */
public class Prueba_javafx extends Application {
private static final int MAX_DATA_POINTS_SPO2 = 500;
private static final int MAX_DATA_POINTS_ECG = 500;
private static final int MAX_DATA_POINTS_RESP = 500;
private static final int Y_MAX_SPO2 = 256;
private static final int Y_MAX_ECG = 3000;
private static final int Y_MAX_RESP = 3000;

    private Series series;
    private XYChart.Series<Number, Number> series1;
    private XYChart.Series<Number, Number> series2;
    private XYChart.Series<Number, Number> series3;
    private XYChart.Series<Number, Number> series4;
    private int xSeriesData_spo2 = 0;
    private int xSeriesData_ecg1 = 0;
    private int xSeriesData_ecg2 = 0;
    private int xSeriesData_resp = 0;
    private ConcurrentLinkedQueue<Number> dataECG1 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> dataECG2 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> dataSPO2 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> dataRESP = new ConcurrentLinkedQueue<Number>();
    private ExecutorService executor;
    private AddToQueue addToQueue;
    private Timeline timeline2;
    private NumberAxis xAxis;
    private NumberAxis xAxis_2;
    private NumberAxis xAxis_3;
    private NumberAxis xAxis_4;
    AdminDevice admin;
    //buffer para recibir comandos desde teclado (solo para prueba)
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
/**
 * Configura e Inicializa los chart de graficacion de ondas
 *
 */
    private void init(Stage primaryStage) {
        //configuracion Axis
        xAxis = new NumberAxis(0,MAX_DATA_POINTS_SPO2,MAX_DATA_POINTS_SPO2/10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        
        xAxis_2 = new NumberAxis(0,MAX_DATA_POINTS_ECG,MAX_DATA_POINTS_ECG/10);
        xAxis_2.setForceZeroInRange(false);
        xAxis_2.setAutoRanging(false);
        
        xAxis_3 = new NumberAxis(0,MAX_DATA_POINTS_ECG,MAX_DATA_POINTS_ECG/10);
        xAxis_3.setForceZeroInRange(false);
        xAxis_3.setAutoRanging(false);
        
        xAxis_4 = new NumberAxis(0,MAX_DATA_POINTS_RESP,MAX_DATA_POINTS_RESP/10);
        xAxis_4.setForceZeroInRange(false);
        xAxis_4.setAutoRanging(false);
        //numero de divisiones en eje Y
        NumberAxis yAxis = new NumberAxis(0,Y_MAX_SPO2,Y_MAX_SPO2/10);
        yAxis.setAutoRanging(false);
        
        NumberAxis yAxis_2 = new NumberAxis(0,Y_MAX_ECG,Y_MAX_ECG/10);
        yAxis.setAutoRanging(false);
        
        NumberAxis yAxis_3 = new NumberAxis(0,Y_MAX_ECG,Y_MAX_ECG/10);
        yAxis.setAutoRanging(false);
        
        NumberAxis yAxis_4 = new NumberAxis(0,Y_MAX_RESP,Y_MAX_RESP/10);
        yAxis.setAutoRanging(false);

        //-- Chart
        //Line chart Spo2
        final LineChart<Number, Number> lc1 = new LineChart<Number, Number>(xAxis, yAxis){
            // Override to remove symbols on each data point
            @Override protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {}
        };
        //Line chart ecg1
        final LineChart<Number, Number> lc2 = new LineChart<Number, Number>(xAxis_2, yAxis_2){
            // Override to remove symbols on each data point
            @Override protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {}
        };
        //Line chart ecg2
        final LineChart<Number, Number> lc3 = new LineChart<Number, Number>(xAxis_3, yAxis_3){
            // Override to remove symbols on each data point
            @Override protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {}
        };
        //Line chart RESP
        final LineChart<Number, Number> lc4 = new LineChart<Number, Number>(xAxis_4, yAxis_4){
            // Override to remove symbols on each data point
            @Override protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {}
        };
        lc1.setAnimated(false);
        lc1.setId("ondaSPO2");
        lc1.setTitle("Onda SPO2");
        
        lc2.setAnimated(false);
        lc2.setId("ondaECG1");
        lc2.setTitle("Onda ECG CH1");
        
        lc3.setAnimated(false);
        lc3.setId("ondaECG2");
        lc3.setTitle("Onda ECG CH2");
        
        lc4.setAnimated(false);
        lc4.setId("ondaRESP");
        lc4.setTitle("Onda RESP");

        //-- Chart Series
        series1= new XYChart.Series<>();
        series2= new XYChart.Series<>();
        series3= new XYChart.Series<>();
        series4= new XYChart.Series<>();
        //series = new AreaChart.Series<Number, Number>();
        series1.setName("Spo2");
        lc1.getData().add(series1);
        
        series2.setName("ECG1");
        lc2.getData().add(series2);
        
        series3.setName("ECG2");
        lc3.getData().add(series3);
        
        series4.setName("RESP");
        lc4.getData().add(series4);
        //Las 4 graficas se agregan en la misma ventana para ver su comportamiento. 
        //--Para integrarlo en el codigo final no se como seria; vos que manejas javafx sabras mejor
        FlowPane root = new FlowPane();
        root.getChildren().addAll(lc1,lc2,lc3,lc4);
        primaryStage.setScene(new Scene(root));
    }
/**
 * Se crea un objeto de la clase AdminDevice y se agrega al executor el hilo consumidor que extrae los datos
 * de las queue
 *
 */
    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
        
        admin=new AdminDevice();
        //-- Prepare Executor Services
        //Esto lo hago siguiendo el codigo ejemplo; se ejkecuta el hilo consumidorcon ExecutorService
        executor = Executors.newCachedThreadPool();
        addToQueue = new AddToQueue();
        executor.execute(addToQueue);
      
        //-- Prepare Timeline
        prepareTimeline();
    }

    public static void main(String[] args) {
        launch(args);
    }
/**
 * Hilo consumidor, donde se capturan los datos y se agregan a la queue respectiva
 * Se introduce un delay de 7 ms correspondiente al intervalo de cada dato.
 * En este mismo hilo se escuchan los comandos de conexion y medicion
 * Posteriormente se realizara este hilo en la clase ThreadConsumer
 */
    private class AddToQueue implements Runnable {
        public void run() {
            try {

                if (!admin.ecg1Signal.isEmpty()){
                     dataECG1.add(admin.ecg1Signal.readWave());
                    // System.out.println(ecg1); 
                 } else{
                     //monitor.getData();
                  }
                 if (!admin.ecg2Signal.isEmpty()){
                     dataECG2.add(admin.ecg2Signal.readWave());
                 } else{
                     //monitor.getData();
                 }
                 if (!admin.respSignal.isEmpty()){
                     dataRESP.add(admin.respSignal.readWave());
                 }  else{
                     //monitor.getData();
                 }
                  if (!admin.spo2Signal.isEmpty()){
                     dataSPO2.add(admin.spo2Signal.readWave()); 
                 }else{
                      //monitor.getData();
                  }
                       Thread.sleep(7);
                //System.out.println("Sistole: "+ admin.staticParameters.readPresSist()+", Diastole: "+admin.staticParameters.readPresDias()+", Media: "+admin.staticParameters.readPresMed()+", HR: "+admin.staticParameters.readSpo2Hr());
/**
 * Este es un ejemplo de como enviar los comandos al servidor. En lugar de escuchar el teclado, 
 * se debe implementar con botones o checkbox
 */
                    if(br.ready()){
                String s = br.readLine();
                System.out.println("Se oprimio: "+s);
                if(s.equals("a"))
                    admin.enviarComando("5leads",0);
                else if(s.equals("b"))
                    admin.enviarComando("3leads",0);
                else if(s.equals("c"))
                    admin.enviarComando("manualPressure",0);
                else if(s.equals("d"))
                    admin.enviarComando("continousPressure",2);
                else if(s.equals("e")){//comenzar toma de presion
                    admin.enviarComando("stopPressure",0);
                    admin.enviarComando("startPressure",0);}
                else if(s.equals("f"))
                    admin.enviarComando("stopPressure",0);
                else if(s.equals("t"))
                    admin.solicitarTanita("20","masculino","27","175","regular");//ID:17-65534, genero: masculino-femenino, edad , estatura: en cm, actividad: sedentario, regular o deportista.
                else if(s.equals("cb")){ //Para conectar por bluetooth
                    admin.dispositivoDesconectado();
                    admin.ConectarBluetooth();
                    }
                else if(s.equals("ct")){//para conectar cliente por TCP/IP
                    admin.dispositivoDesconectado();
                    admin.ConectarTcp();
                    }
                else if(s.equals("dt")){//para desconectar cliente por TCP/IP
                    admin.desconectarCliente();
                    }
            }
                executor.execute(this);
            } catch (InterruptedException ex) {
                Logger.getLogger(Prueba_javafx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Prueba_javafx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //-- Timeline gets called in the JavaFX Main thread
    
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override public void handle(long now) {
                addDataToSeries();
                addDataToSeries_2();
                addDataToSeries_3();
                addDataToSeries_4();
            }
        }.start();
    }
//Saca los datos de las queue y los agrega al chart spo2
//El for esta hasta 20 porque dio mejor performance
    private void addDataToSeries() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataSPO2.isEmpty()) break;
            series1.getData().add(new XYChart.Data<>(xSeriesData_spo2++, dataSPO2.remove()));

        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series1.getData().size() > MAX_DATA_POINTS_SPO2) {
            series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS_SPO2);
        }
        // update 
        xAxis.setLowerBound(xSeriesData_spo2-MAX_DATA_POINTS_SPO2);
        xAxis.setUpperBound(xSeriesData_spo2-1);
    }
    //Saca los datos de las queue y los agrega al chart ecg1
    private void addDataToSeries_2() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataECG1.isEmpty()) break;
            series2.getData().add(new XYChart.Data<>(xSeriesData_ecg1++, dataECG1.remove()));

        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series2.getData().size() > MAX_DATA_POINTS_ECG) {
            series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS_ECG);
        }
        // update 
        xAxis_2.setLowerBound(xSeriesData_ecg1-MAX_DATA_POINTS_ECG);
        xAxis_2.setUpperBound(xSeriesData_ecg1-1);
    }
    //Saca los datos de las queue y los agrega al chart ecg2
    private void addDataToSeries_3() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataECG2.isEmpty()) break;
            series3.getData().add(new XYChart.Data<>(xSeriesData_ecg2++, dataECG2.remove()));

        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series3.getData().size() > MAX_DATA_POINTS_ECG) {
            series3.getData().remove(0, series3.getData().size() - MAX_DATA_POINTS_ECG);
        }
        // update 
        xAxis_3.setLowerBound(xSeriesData_ecg2-MAX_DATA_POINTS_ECG);
        xAxis_3.setUpperBound(xSeriesData_ecg2-1);
    }
    //Saca los datos de las queue y los agrega al chart resp
    private void addDataToSeries_4() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataRESP.isEmpty()) break;
            series4.getData().add(new XYChart.Data<>(xSeriesData_resp++, dataRESP.remove()));

        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series4.getData().size() > MAX_DATA_POINTS_RESP) {
            series4.getData().remove(0, series4.getData().size() - MAX_DATA_POINTS_RESP);
        }
        // update 
        xAxis_4.setLowerBound(xSeriesData_resp-MAX_DATA_POINTS_RESP);
        xAxis_4.setUpperBound(xSeriesData_resp-1);
    }
}
