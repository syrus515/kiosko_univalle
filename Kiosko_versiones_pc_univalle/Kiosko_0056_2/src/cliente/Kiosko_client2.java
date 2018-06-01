package cliente;




import java.net.*;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Kiosko_client2 implements Runnable{

//    Ecg1Signal ecg1Signal;
//    Ecg2Signal ecg2Signal;
//    Spo2Signal spo2Signal;
//    RespSignal respSignal;
//    StaticParameters staticParameters;
//    private ConcurrentLinkedQueue<Integer> queueECG1 = new ConcurrentLinkedQueue<Integer>();
//    private ConcurrentLinkedQueue<Integer> queueECG2 = new ConcurrentLinkedQueue<Integer>();
//    private ConcurrentLinkedQueue<Integer> queueSpo2 = new ConcurrentLinkedQueue<Integer>();
//    private ConcurrentLinkedQueue<Integer> queueResp = new ConcurrentLinkedQueue<Integer>();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    AdminDevice admin;
    
    public static void main(String args[]) {
        new Kiosko_client2();
    }
  
    
    public Kiosko_client2() {
        admin= new AdminDevice(null);
        Thread t1=new Thread(this);
        t1.start();
    }
     
    @Override
    public void run(){
        boolean flag=false;
        double ecg1 = 0;
        double ecg2 = 0;
        int spo2 = 0;
        double resp = 0;
        byte [] buffer_in=null;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        try {
            while(true){
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
                else if(s.equals("ct")){//para conectar por TCP/IP
                    admin.dispositivoDesconectado();
                    admin.ConectarTcp();
                    }
                else if(s.equals("dt")){//para conectar por TCP/IP
                    admin.desconectarCliente();
                    }
                Thread.sleep(150);
            }
            }
        } catch (IOException ex) {
            Logger.getLogger(Kiosko_client2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Kiosko_client2.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
//    public void ConectarBluetooth(){
//        try {
//            new BluetoothClient(ecg1Signal,ecg2Signal,spo2Signal,respSignal,staticParameters,commands,monitor,connectionState);
//            connectionState.reset();
//        } catch (IOException ex) {
//            Logger.getLogger(Kiosko_client2.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    public void ConectarTcp(){
//        try {
//            new ThreadCliente(ecg1Signal,ecg2Signal,spo2Signal,respSignal,staticParameters,commands,monitor,connectionState);
//            connectionState.reset();
//        } catch (IOException ex) {
//            Logger.getLogger(Kiosko_client2.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**

     */
//         private static void errorFatal(Exception excepcion, String mensajeError) {
//        excepcion.printStackTrace();
//        JOptionPane.showMessageDialog(null, "Error fatal."+ System.getProperty("line.separator") +
//                mensajeError, "InformaciÃ³n para el usuario", JOptionPane.WARNING_MESSAGE);
//    }
}

 