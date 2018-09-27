/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import cliente.AdminDevice;

/**
 *
 * @author Miguel Askar
 */
public class AlterarInterfaz extends Thread 
{    
    int opcion;
    AdminDevice admin;
    MenuController menu;
    
    public AlterarInterfaz(AdminDevice admin, MenuController menu)
    {        
        this.admin= admin;
        this.menu= menu;
    }
    
    public void setOpcion(int n)
    {
        opcion= n;
    }
    
    @Override
    public void run() 
    {
        //super.run(); //To change body of generated methods, choose Tools | Templates.
        switch(opcion)
        {
            case 1:
                int anterior= admin.staticParameters.readPresDias();
                while(admin.staticParameters.readPresDias()== anterior)
                {
                    //No haga nada mientras la presión no se haya actualizado
                    System.out.println(admin.staticParameters.readPresDias());
                }
                System.out.println("---------- Definitivo: " + admin.staticParameters.readPresDias()); 
                menu.actualizarPresion();
                
                break;
                
            case 2:
                float anteriorPeso= admin.staticParameters.readWeight();
                while(admin.staticParameters.readWeight()== anteriorPeso)
                {
                    //No haga nada mientras la presión no se haya actualizado
                    System.out.println(admin.staticParameters.readWeight());
                }
                System.out.println("---------- Definitivo: " + admin.staticParameters.readWeight()); 
                menu.actualizarPeso();
                
                break;
        }
         
                
        
        
    }

    
}
