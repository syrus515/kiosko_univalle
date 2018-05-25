package cliente;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos Andres
 */
public class Ecg2Signal implements ReadSignals{
    
    String waveString;
    String[] waveArray;
    int[] wave=null;
    int[] intWave=new int[50];

    private ConcurrentLinkedQueue<Integer> queueWaves;

    public Ecg2Signal(ConcurrentLinkedQueue<Integer> dataQ){
        queueWaves=dataQ;
    }
    
@Override
    public void getWave(String waveString){
        this.waveString=waveString;
        waveArray=waveString.split(";");
        for(int i=0;i<waveArray.length;i++){
           intWave[i]=Integer.parseInt(waveArray[i]);
           queueWaves.add(intWave[i]);
           
        }
        

    }
    @Override
    public int readWave(){
        return queueWaves.remove().intValue();
    }
    public boolean isEmpty(){
       return queueWaves.isEmpty();
    }
}

