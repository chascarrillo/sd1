package sd1;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class MyHTTPServer
{
	private static Inet4Address address;
	private static boolean online = false;
	private static MyHTTPServer instancia;
	private static int puerto;
	private static short conexSimultaneas;
	

	private MyHTTPServer(int puerto, short conexSimultaneas)
	{
		try
		{
			setAddress((Inet4Address) Inet4Address.getLocalHost());
			MyHTTPServer.puerto = puerto;
			MyHTTPServer.setConexSimultaneas(conexSimultaneas);
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}

	public static MyHTTPServer MyHTTPServerBuilder(int puerto, short conexSimultaneas)
	{
		if(!online)
		{
			instancia = new MyHTTPServer(puerto, conexSimultaneas);
			online = true;
			return instancia;
		}
		else
		{
			return instancia;
		}
	}

	public static int getPuerto()
	{
		return puerto;
	}

	public static void main(String[] args)
	{
		MyHTTPServer servidor = MyHTTPServer.MyHTTPServerBuilder(Integer.valueOf(args[0]), Short.valueOf(args[1]));
	}

	public static Inet4Address getAddress()
	{
		return address;
	}

	public static void setAddress(Inet4Address address)
	{
		MyHTTPServer.address = address;
	}

	public static short getConexSimultaneas()
	{
		return conexSimultaneas;
	}

	public static void setConexSimultaneas(short conexSimultaneas)
	{
		MyHTTPServer.conexSimultaneas = conexSimultaneas;
	}
}
