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
    boolean detenerProcesos= false;//Esta bandera controla que se cancelen los procesos si hay desconexión.   
    
    
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
                //int anterior= admin.staticParameters.readPresDias();
                while(admin.staticParameters.readPresDias()!=0)
                {
                    //No hace nada mientras se resetea
                }
                while(admin.staticParameters.readPresDias()== 0 && !detenerProcesos)
                {
                    //No haga nada mientras la presión no se haya actualizado
                    menu.publicarPresion("---/---");
                    //System.out.println(admin.staticParameters.readPresDias());
                    
                }
                if(!detenerProcesos)
                {
                    System.out.println("---------- Definitivo: " + admin.staticParameters.readPresDias()); 
                    menu.actualizarPresion(admin.staticParameters.readPresDias(), admin.staticParameters.readPresSist());                    
                    //menu.setTextoPresion("Tomar Presión");
                }else
                {
                    //Se deshabilitan los botones de la sección de afinamientos.
                    menu.enableTunning(false);
                }
                
                
                break;
                
            case 2:
                float anteriorPeso= admin.staticParameters.readWeight();
                while(admin.staticParameters.readWeight()== anteriorPeso && !detenerProcesos)
                {
                    //No haga nada mientras la presión no se haya actualizado
                    System.out.println(admin.staticParameters.readWeight());
                }
                 if(!detenerProcesos)
                {
                    System.out.println("---------- Definitivo: " + admin.staticParameters.readWeight()); 
                    menu.actualizarPeso();
                }else
                {
                    //Se deshabilitan los botones de la sección de afinamientos.
                    menu.enableTunning(false);
                }
                break;
        }
         
                
        
        
    }
    
    //Pone la bandera en falso si hubo desconexión.
    public void detenerProcesos()
    {
        detenerProcesos= true;
    }
    
    //Habilita la bandera para que los procesos puedan iniciar.
    public void iniciarProcesos()
    {
        detenerProcesos= false;
    }

    
}
