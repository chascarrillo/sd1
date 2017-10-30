package sondasRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

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

	public Integer getVolumen(int numsonda)
	throws java.rmi.RemoteException;

	public Integer getLuz(int numsonda)
	throws java.rmi.RemoteException;

	public String getFecha(int numsonda)
	throws java.rmi.RemoteException;

	public String getUltimaFecha(int numsonda)
	throws java.rmi.RemoteException;

	public Integer setLuz(int luz, int numsonda)
	throws java.rmi.RemoteException;
}
