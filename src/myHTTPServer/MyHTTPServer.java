import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
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

	private int puerto;
	private Inet4Address IP;
	private Inet4Address IP_controller;
	private int puerto_controller;
	private int maxConexPermitidas;

	private static final int ERROR = 0;
	private static final int DEBUG = 2;

	public void depura(String mensaje)
	{
		depura(mensaje, DEBUG);
	}

	public void depura(String mensaje, int gravedad)
	{
		if (gravedad != ERROR)
			System.out.println("\t" + mensaje);
		else System.err.println(mensaje);
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
		if (args.length == 4)
		{
			// this.IP = (Inet4Address) InetAddress.getLocalHost();
			this.IP = (Inet4Address) InetAddress.getByAddress(InetAddress.getLocalHost().getAddress());
			this.puerto = Integer.parseInt(args[0]);
			this.maxConexPermitidas = Integer.parseInt(args[1]);
			this.IP_controller = (Inet4Address) InetAddress.getByName(args[2]);
			this.puerto_controller = Integer.parseInt(args[3]);
		} else
		{
			depura("Error en servidor, parametros invalidos.", ERROR);
		}
	}

	boolean run()
	{
		depura("Servidor en marcha...");

		try
		{
			ServerSocket sk = new ServerSocket(puerto);

			depura("Escuchando en " + IP.getHostAddress() + ":" + puerto);
			System.out.print("\n");
			int peticionesAtendidas = 0;

			do
			{
				if (hilos.size() < maxConexPermitidas)
				{
					Socket recibido = sk.accept();
					peticionesAtendidas++;
					depura("Peticiones atendidas: " + peticionesAtendidas);
					Thread hilo = new MyHTTPServer_Thread(recibido, IP_controller, puerto_controller, hilos);
					hilos.put(hilo, recibido);
					hilo.start();
				} else
				{
					Exception e = new Exception("El servidor no puede atender la peticion");
					throw e;
				}
			} while (peticionesAtendidas < 100);

			sk.close();
		} catch (Exception e)
		{
			System.err.print("ERROR");
			e.printStackTrace();
		}
		return true;
	}
}
