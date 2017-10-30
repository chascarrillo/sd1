package controladorSD;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author pc_user
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
