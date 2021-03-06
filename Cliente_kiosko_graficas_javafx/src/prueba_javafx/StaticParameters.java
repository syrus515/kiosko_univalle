package prueba_javafx;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Permite obtener en formato int y float los parametros biomedicos despues de recibirlos en formato string,
 * @author Carlos Andres
 */
public class StaticParameters implements ReadStaticData{
    
    private int hr;
    private int respRate;
    private int spo2Oxi;
    private int spo2Hr;
    private int presRate=0;
    private int presDias;
    private int presMed;
    private int presSist;
    private int temp;
    private float weight;
    private float waterPercent;
    private float bodyFat;
    private float muscleMass;
    
    public void getHr(String param){
        hr=Integer.parseInt(param);
    }
    public void getResp(String param){
        respRate=Integer.parseInt(param);
    }
    public void getSpo2Oxi(String param){
        spo2Oxi=Integer.parseInt(param);
    }
    public void getSpo2Hr(String param){
        spo2Hr=Integer.parseInt(param);
    }
    public void getPresR(String param){
        presRate=Integer.parseInt(param);
    }
    public void getPresDias(String param){
        presDias=Integer.parseInt(param);
    }
    public void getPresMed(String param){
        presMed=Integer.parseInt(param);
    }
    public void getPresSist(String param){
        presSist=Integer.parseInt(param);
    }
    public void getTemp(String param){
        temp=Integer.parseInt(param);
    }
    public void getWeight(String param){
        weight=Float.parseFloat(param);
    }
    public void getWaterPercent(String param){
        waterPercent=Float.parseFloat(param);
    }
    public void getBodyFat(String param){
        bodyFat=Float.parseFloat(param);
    }
    public void getMuscleMass(String param){
        muscleMass=Float.parseFloat(param);
    }
    @Override
    public int readHr(){
        return hr;
    }
    @Override
    public int readResp(){
        return respRate;
    }
    @Override
    public int readSpo2Oxi(){
        return spo2Oxi;
    }
    @Override
    public int readSpo2Hr(){
        return spo2Hr;
    }
    @Override
    public int readPresR(){
        return presRate;
    }
    @Override
    public int readPresDias(){
        return presDias;
    }
    @Override
    public int readPresMed(){
        return presMed;
    }
    @Override
    public int readPresSist(){
        return presSist;
    }
    @Override
    public int readTemp(){
        return temp;
    }
    @Override
    public float readWeight(){
        return weight;
    }
    @Override
    public float readWaterPercent(){
        return waterPercent;
    }
    @Override
    public float readBodyFat(){
        return bodyFat;
    }
    @Override
    public float readMuscleMass(){
        return muscleMass;
    }
}
