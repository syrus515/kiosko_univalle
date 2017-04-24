package prueba_javafx;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Por el momento esta clase no se esta utilizando
 * @author Carlos Andres
 */
public class ThreadConsumer implements Runnable{
    Ecg1Signal ecg1Signal;
    Ecg2Signal ecg2Signal;
    Spo2Signal spo2Signal;
    RespSignal respSignal;
    StaticParameters staticParameters;
    Monitor monitor;
    ConnectionState connectionState;
    private volatile boolean running = true;
    
    
    public ThreadConsumer(Ecg1Signal ecg1Signal, Ecg2Signal ecg2Signal, Spo2Signal spo2Signal, RespSignal respSignal, 
            StaticParameters staticParameters, Monitor monitor, ConnectionState connectionState){
        this.ecg1Signal=ecg1Signal;
        this.ecg2Signal=ecg2Signal;
        this.spo2Signal=spo2Signal;
        this.respSignal=respSignal;
        this.staticParameters=staticParameters;
        this.monitor=monitor;
        this.connectionState=connectionState;
    }

    @Override
    public synchronized void run() {
        boolean flag=false;
         double ecg1 = 0;
         double ecg2 = 0;
         int spo2 = 0;
         double resp = 0;
         byte [] buffer_in=null;
        
         while(running){       
                 if (!ecg1Signal.isEmpty()){
                     ecg1=ecg1Signal.readWave();
                    // System.out.println(ecg1); 
                 } else{
                     monitor.getData();
                      
                  }
                 if (!ecg2Signal.isEmpty()){
                     ecg2=ecg2Signal.readWave();
                 } else{
                     monitor.getData();
                 }
                 if (!respSignal.isEmpty()){
                     resp=respSignal.readWave();
                 }  else{
                     monitor.getData();
                 }
                  if (!spo2Signal.isEmpty()){
                     spo2=spo2Signal.readWave(); 
                     
                 }else{
                      monitor.getData();
        
                      
                  }
                  
//         
//                System.out.println(" ParAmetros-> " + staticParameters.readHr() + ", "
//                        + staticParameters.readResp() + ", "
//                        + staticParameters.readSpo2Oxi() + ", "
//                        + staticParameters.readSpo2Hr() + ", "
//                        + staticParameters.readPresR() + ", "
//                        + staticParameters.readPresDias() + ", "
//                        + staticParameters.readPresMed() + ", "
//                        + staticParameters.readPresSist());
                  
                  //System.out.println("Sistole: "+ staticParameters.readPresSist()+", Diastole: "+staticParameters.readPresDias()+", Media: "+staticParameters.readPresMed()+", HR: "+staticParameters.readSpo2Hr());
                  System.out.println(spo2); 
                  
        }
    }
    public void stopThread(){
        running = false;
    }
}
