package prueba_javafx;

/*
 * Esta clase no esta en uso de momento.
 * @author Carlos Andres
 */


import java.io.*;
import java.util.ArrayList;
import java.util.Queue;


public class Monitor {
  private char contenido;
  private boolean disponible=false;
  public Monitor() {
  }

  public synchronized void getData(){
    while(!disponible){
        try{
            wait();
        }catch(InterruptedException ex){}
    }
    disponible=false;
    notify();
    //return contenido;
  }

  public synchronized void putData(){
     while(disponible){
        try{
            wait();
        }catch(InterruptedException ex){}
    }
    //contenido=valor;
    disponible=true;
    notify();
  }
}
//public class Monitor{
//
// private int maxSize;
// private Queue<ArrayList<int[]>> queue;
// private ArrayList<int[]> WAVES = new ArrayList<int[]>();
// private String[] waveArray;
// int[] int_ecg1=new int[250];
// int[] int_ecg2=new int[250];
// int[] int_spo2=new int[250];
// int[] int_resp=new int[250];
//  private boolean available = false;
//
//public Monitor(Queue<ArrayList<int[]>> queue, int maxSize){ 
//    this.queue = queue; 
//    this.maxSize = maxSize; 
//}
//
//
//  
//   public ArrayList<int[]> getWAVES() {
//      synchronized (queue) {
//      while (queue.isEmpty()) {
//          try {
//              queue.wait();
//          } catch (InterruptedException e) { }
//      }
//      ArrayList<int[]> waves=queue.remove();
//      queue.notifyAll();
//      return waves;
//      }
//  } 
//
//   public void put(int[] indRef, String ECG1_WAVE, String ECG2_WAVE, String SPO2_WAVE, String RESP_WAVE) {
//       synchronized (queue) {
//      while (queue.size()==maxSize) {
//          try {
//              queue.wait();
//          } catch (InterruptedException e) { }
//      }
// 
//        waveArray=ECG1_WAVE.split(";");
//        for(int i=0;i<waveArray.length;i++){
//           int_ecg1[i]=Integer.parseInt(waveArray[i]);
//        }
//        waveArray=ECG2_WAVE.split(";");
//        for(int i=0;i<waveArray.length;i++){
//           int_ecg2[i]=Integer.parseInt(waveArray[i]);
//        }
//        waveArray=SPO2_WAVE.split(";");
//        for(int i=0;i<waveArray.length;i++){
//           int_spo2[i]=Integer.parseInt(waveArray[i]);
//        }
//        waveArray=RESP_WAVE.split(";");
//        for(int i=0;i<waveArray.length;i++){
//           int_resp[i]=Integer.parseInt(waveArray[i]);
//        }
//      WAVES.add(0, indRef);
//      WAVES.add(1, int_ecg1);
//      WAVES.add(2, int_ecg2);
//      WAVES.add(3, int_spo2);
//      WAVES.add(4, int_resp);
//      queue.add(WAVES);
//      queue.notifyAll();
//      }
//  }
//}