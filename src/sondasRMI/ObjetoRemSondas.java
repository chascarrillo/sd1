package sondasRMI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Random;

import controladorSD.InterfRemSondas;

/**
 *
 * @author aao16
 */
public class ObjetoRemSondas extends UnicastRemoteObject
implements InterfRemSondas, Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     *
     * @throws RemoteException
     */
    public ObjetoRemSondas () throws RemoteException{
        super();
        System.out.println("Creamos objeto");
    }
    
    public void EcribirFichero(File Ffichero,String SCadena){
        try {
                //Si no Existe el fichero lo crea
                 if(!Ffichero.exists()){
                     Ffichero.createNewFile();
                 }
                /*Abre un Flujo de escritura,sobre el fichero con codificacion utf-8. 
                 *Además  en el pedazo de sentencia "FileOutputStream(Ffichero,true)",
                 *true es por si existe el fichero seguir añadiendo texto y no borrar lo que tenia*/
                BufferedWriter Fescribe=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Ffichero,true), "utf-8"));
                /*Escribe en el fichero la cadena que recibe la función. 
                 *el string "\r\n" significa salto de linea*/
                Fescribe.write(SCadena + "\r\n");
                //Cierra el flujo de escritura
                Fescribe.close();
             } catch (Exception ex) {
                //Captura un posible error le imprime en pantalla 
                System.out.println(ex.getMessage());
             } 
    }
    
    public void BorrarFichero(File Ffichero){
     try {
         /*Si existe el fichero*/
         if(Ffichero.exists()){
           /*Borra el fichero*/  
           Ffichero.delete(); 
           System.out.println("Fichero Borrado con Exito");
         }
     } catch (Exception ex) {
         /*Captura un posible error y le imprime en pantalla*/ 
          System.out.println(ex.getMessage());
     }
} 
    
    public void ModificarFichero(File FficheroAntiguo,String nuevodato, int numsonda){        
        
        String SnombFichNuev="./sensor"+numsonda+".txt";
        /*Crea un objeto File para el fichero nuevo*/
        File FficheroNuevo=new File(SnombFichNuev);
        try {
            /*Si existe el fichero inical*/
            if(FficheroAntiguo.exists()){
                /*Abro un flujo de lectura*/
                BufferedReader Flee= new BufferedReader(new FileReader(FficheroAntiguo));
                String Slinea;
                /*Recorro el fichero de texto linea a linea*/
                while((Slinea=Flee.readLine())!=null) { 
                    /*Si la lia obtenida es igual al la bucada
                     *para modificar*/
                    if(Slinea.substring(0, 3).equals("Led")){
                        EcribirFichero(FficheroNuevo,"Led="+nuevodato);
                    }
                    else{
                        EcribirFichero(FficheroNuevo,Slinea);
                    }
                }
                /*Obtengo el nombre del fichero inicial*/
                String SnomAntiguo=FficheroAntiguo.getName();
                /*Borro el fichero inicial*/
                BorrarFichero(FficheroAntiguo);
                /*renombro el nuevo fichero con el nombre del 
                *fichero inicial*/
                FficheroNuevo.renameTo(FficheroAntiguo);
                /*Cierro el flujo de lectura*/
                Flee.close();
            }else{
                System.out.println("Fichero No Existe");
            }
        } catch (Exception ex) {
            /*Captura un posible error y le imprime en pantalla*/ 
             System.out.println(ex.getMessage());
        }
    }
    
    
    private String leeFichero(int numsonda, int peticion){
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        String dato = "";

        try {
            archivo = new File ("./sensor"+numsonda+".txt");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            while((linea=br.readLine())!=null){
                switch(peticion){
                    case 1: if(linea.substring(0,7).equals("Volumen")){
                                dato = linea.substring(8);
                                System.out.println("Leemos "+dato);
                            }
                            break;
                    case 2: if(linea.substring(0,11).equals("UltimaFecha")){
                                dato = linea.substring(12);
                            }
                    System.out.println("Leemos "+dato);
                            break;
                    case 3: if(linea.substring(0,3).equals("Led")){
                                dato = linea.substring(4);
                            }
                    System.out.println("Leemos "+dato);
                            break;
                    default: break;
                }
                System.out.println(linea);
                System.out.println("Dato: "+dato);
            }
            fr.close();     
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return dato;
    }
    
    public Integer getVolumen(int numsonda){
        int vol = Integer.parseInt(leeFichero(numsonda,1));
        if(vol > 100 || vol < 0){
            vol = -1;
            return vol;
        }
        else{
            return vol;
        }
    }
    
    public Integer getLuz(int numsonda){
        return Integer.parseInt(leeFichero(numsonda, 3));
    }
    
    public String getFecha(int numsonda){
        Date ahora = new Date();
        ahora.getTime();
        return ahora.toString();
    }
    
    public String getUltimaFecha(int numsonda){
       return leeFichero(numsonda, 2);
    }
    
    public Integer setLuz(int luz, int numsonda){
        File fich = new File("./sensor"+numsonda+".txt");
        ModificarFichero(fich,Integer.toString(luz),numsonda);
        return 0;
    }
}

