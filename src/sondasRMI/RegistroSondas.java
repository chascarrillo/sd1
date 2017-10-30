package sondasRMI;

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
				System.out.println("generando sonda " + i);
				ObjetoRemSondas objRem = new ObjetoRemSondas();
				nombreSonda = "/Sonda" + i;
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
