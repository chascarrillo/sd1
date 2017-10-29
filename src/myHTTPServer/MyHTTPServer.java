package myHTTPServer;

import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Exception;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class MyHTTPServer
{
	public static final String path = System.getProperty("user.dir");
	public static Map<Thread, Socket> hilos = null;
	public static int cont = 0;

	private int puerto;
	private Inet4Address IP;
	private Inet4Address IP_controller;
	private int puerto_controller;
	private int maxConexPermitidas;

	private static final int ERROR = 0;
	private static final int WARNING = 1;
	private static final int DEBUG = 2;

	public void depura(String mensaje)
	{
		depura(mensaje, DEBUG);
	}

	public void depura(String mensaje, int gravedad)
	{
		System.out.println(mensaje);
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
		hilos = new ConcurrentHashMap<Thread, Socket>(server.maxConexPermitidas);
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
		depura(path);
		depura("Servidor en marcha...");

		try
		{
			ServerSocket sk = new ServerSocket(puerto);

			depura("Escuchando en " + this.IP.getHostAddress() + ":" + this.puerto);
			System.out.print("\n");
			int peticionesAtendidas = 0;

			do
			{
				if (cont < maxConexPermitidas)
				{
					Socket recibido = sk.accept();
					peticionesAtendidas++;
					depura("Peticiones atendidas: " + peticionesAtendidas);
					Thread hilo = new MyHTTPServer_Thread(recibido, IP, puerto_controller);
					cont++; // cont-- se ejecuta al final de MyHTTPServer_Thread.run()
					hilos.put(hilo, recibido);
					hilo.start();
				} else
				{
					Exception ex = new Exception("El servidor no puede atender la peticion");
					throw ex;
				}
			} while (true);
		} catch (Exception e)
		{
			System.err.println("ERROR " + e.toString());
		}
		return true;
	}
}
