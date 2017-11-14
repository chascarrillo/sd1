package sondasRMI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class ObjetoRemSondas
extends UnicastRemoteObject
implements InterfRemSondas, Serializable
{
	private static final long serialVersionUID = 1L;

	public ObjetoRemSondas()
	throws RemoteException
	{
		super();
	}

	public void EscribirFichero(File Ffichero, String SCadena)
	{
		try
		{
			if (!Ffichero.exists())
			{
				Ffichero.createNewFile();
			}
			/* en "FileOutputStream(Ffichero,true)", true es por si
			 * existe el fichero seguir añadiendo texto sin borrar nada
			 */
			BufferedWriter Fescribe
			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Ffichero, true), "utf-8"));

			Fescribe.write(SCadena + "\n");

			Fescribe.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void BorrarFichero(File Ffichero)
	{
		try
		{
			if (Ffichero.exists())
			{
				Ffichero.delete();
				System.out.println("Fichero Borrado con Exito");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void ModificarFichero(File FficheroAntiguo, String nuevodato, int numsonda)
	{
		String SnombFichNuev = "./Sonda" + numsonda + ".txt";
		File FficheroNuevo = new File(SnombFichNuev);
		try
		{
			if(!FficheroNuevo.renameTo(FficheroAntiguo))
			{
				Exception e = new Exception("fallo al cambiar el nombre del fichero");
				throw e;
			}
			if (FficheroAntiguo.exists())
			{
				BufferedReader Flee = new BufferedReader(new FileReader(FficheroAntiguo));
				String Slinea;

				while ((Slinea = Flee.readLine()) != null)
				{
					if (Slinea.substring(0, 3).equals("Led"))
					{
						EscribirFichero(FficheroNuevo, "Led=" + nuevodato);
					}
					else
					{
						EscribirFichero(FficheroNuevo, Slinea);
					}
				}
				BorrarFichero(FficheroAntiguo);
				Flee.close();
			}
			else
			{
				System.out.println("Fichero No Existe");
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	private String leeFichero(int numsonda, int peticion)
	{
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		String dato = "";

		// peticion 1 = humedad, 2 = temperatura
		try
		{
			archivo = new File("./sensor" + numsonda + ".txt");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			String linea;
			while ((linea = br.readLine()) != null)
			{
				switch (peticion)
				{
					case 1:
						if (linea.substring(0, 7).equals("humedad"))
						{
							dato = linea.substring(8);
						}
						break;
					case 2:
						if (linea.substring(0, 11).equals("temperatura"))
						{
							dato = linea.substring(12);
						}
						break;
					default:
						break;
				}
				System.out.println(linea);
			}
			fr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return dato;
	}

	public String[] getHumedad(int numsonda)
	{
		int hum = Integer.parseInt(leeFichero(numsonda, 1));
		if (hum > 100 || hum < 0)
		{
			hum = -1;
		}

		String[] devuelta = new String[4];
		devuelta[0] = String.valueOf(numsonda);
		devuelta[1] = String.valueOf(hum);
		devuelta[2] = getFecha();

		return devuelta;
	}

	public String getFecha()
	{
		SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss EEEE d MMM, yyyy", new Locale("es", "ES"));
		return dt.format((new Date()).getTime());
	}

	public Integer getTemperatura(int numsonda)
	{
		int temp = Integer.parseInt(leeFichero(numsonda, 2));
		if (temp > 45 || temp < -10)
		{
			return -1;
		}
		else
		{
			return temp;
		}
	}

	public Integer setHumedad(int humedad, int numsonda)
	{
		File fich = new File("./Sonda" + numsonda + ".txt");
		ModificarFichero(fich, Integer.toString(humedad), numsonda);
		return 0;
	}
}
