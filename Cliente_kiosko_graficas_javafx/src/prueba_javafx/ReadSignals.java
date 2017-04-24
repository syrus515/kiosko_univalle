package prueba_javafx;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Carlos Andres
 */
public interface ReadSignals {
/**
 * Convierte el string recibido que contiene los datos de onda y los guarda en un array,
 * para despues agregarlo a una queue.
 *
 * @param  wave   String correspondiente a los datos de ondas, separados por punto y coma (;).
 */ 
    public void getWave(String wave);
    /**
 * Extrae un array de datos de onda de la queue
 *
 * @return  Array que contiene datos de ondas en formato entero.
 */ 
    public int readWave();    
 /**
 * Permite saber si la queue que contiene los array de datos de onda esta vacia.
 *
 * @return  if true, la queue esta vacia. if false, la queue tiene elementos
 */ 
    public boolean isEmpty();
}
