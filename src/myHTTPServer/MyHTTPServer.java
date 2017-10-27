package myHTTPServer;

import java.net.*;
//import myhttpserver.MyHTTPServer_Thread;
import java.lang.Exception;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class MyHTTPServer
{
	public static final String path = "/src/myHTTPServer";
	public static int cont = 0;

	private int puerto;
	private Inet4Address IP;
	private Inet4Address IP_controller;
	private int puerto_controller;
	private int maxConexPermitidas;

	private final int ERROR = 0;
	private final int WARNING = 1;
	private final int DEBUG = 2;

	public void depura(String mensaje)
	{
		depura(mensaje, DEBUG);
	}

	public void depura(String mensaje, int gravedad)
	{
		System.err.println("Mensaje: " + mensaje);
	}

	public MyHTTPServer(String[] args)
	{
		try
		{
			compruebaParametros(args);
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		MyHTTPServer server = new MyHTTPServer(args);
		server.run();
	}

	public void compruebaParametros(String[] args)
	throws UnknownHostException
	{
	if (args[0] != null)
	{
		// this.IP = (Inet4Address) InetAddress.getLocalHost();
		this.IP = (Inet4Address) InetAddress.getByAddress(InetAddress.getLocalHost().getAddress());
		this.puerto = Integer.parseInt(args[0]);

		if (args[1] != null)
		{
			this.maxConexPermitidas = Integer.parseInt(args[1]);
		} else
		{
			depura("Error en servidor, parametros invalidos.");
		}

		if (args[2] != null)
		{
			this.IP_controller = (Inet4Address) InetAddress.getByName(args[2]);
		} else
		{
			depura("Error en servidor, parametros invalidos.");
		}

		if (args[3] != null)
		{
			this.puerto_controller = Integer.parseInt(args[3]);
		} else
		{
			depura("Error en servidor, parametros invalidos.");
		}
	} else
	{
		depura("Error en servidor, parametros invalidos.");
	}
	}

	boolean run()
	{
		depura("Server running...");

		try
		{
			ServerSocket sk = new ServerSocket(puerto);

			depura("Escuchando el puerto " + this.puerto + " en la IP " + this.IP.getHostAddress());

			do
			{
				if (cont < maxConexPermitidas)
				{
					Socket recibido = sk.accept();
					Thread hilo = new MyHTTPServer_Thread(recibido, IP, puerto_controller);
					hilo.start();
				} else
				{
					Exception ex = new Exception("El servidor no puede atender la peticion");
					throw ex;
				}
			} while (true);
		} catch (Exception e)
		{
			depura("Server error \n" + e.toString());
		}
		return true;
	}
}
