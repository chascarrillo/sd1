package sondasRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public interface InterfRemSondas
extends Remote
{
	public static void main(String[] args)
	throws RemoteException
	{}

	public String[] getHumedad(int numsonda)
	throws java.rmi.RemoteException;

	public Integer getTemperatura(int numsonda)
	throws java.rmi.RemoteException;

	public String getFecha()
	throws java.rmi.RemoteException;

	public Integer setHumedad(int humedad, int numsonda)
	throws java.rmi.RemoteException;
}
