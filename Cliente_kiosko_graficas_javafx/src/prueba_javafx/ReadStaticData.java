package prueba_javafx;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Esta interface permite leer los parametros biomedicos. 
 * @author Carlos Andres
 */
public interface ReadStaticData {
    
    public int readHr();
    public int readResp();
    public int readSpo2Oxi();
    public int readSpo2Hr();
    public int readPresR();
    public int readPresDias();
    public int readPresMed();
    public int readPresSist();
    public int readTemp();
    public float readWeight();
    public float readWaterPercent();
    public float readBodyFat();
    public float readMuscleMass();
    
}
