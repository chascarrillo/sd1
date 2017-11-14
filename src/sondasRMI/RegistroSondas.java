package sondasRMI;

import java.io.File;
import java.rmi.Naming;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class RegistroSondas
{
	public static void main(String args[])
	{
		String nombreSonda;

		try
		{
			System.setSecurityManager(new SecurityManager());
			for (int i = 1; i <= 3; i++)
			{
				System.out.println("generando 3 sondas en el invernadero " + i);
				ObjetoRemSondas objRem = new ObjetoRemSondas();
				nombreSonda = "/Sonda" + i;
				for(int j = 1; j <= 3; j++)
				{
					File fich = new File("./Invernadero" + i + "Sonda" + j + ".txt");
				}
				Naming.rebind(nombreSonda, objRem);
				System.out.println("Servidor de Sondas preparado.");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage() + "Fallo..");
		}
	}
}
