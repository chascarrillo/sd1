package myHTTPServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Integer;
import java.net.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class MyHTTPServer_Thread
extends Thread
{
	public static Map<Thread, Socket> hilos = null;
	private static final int ERROR = 0;
	private static final int SINSALTO = 1;
	private static final int DEBUG = 2;

	private int p_controller;
	private Inet4Address IP_controller;
	private ServerSocket socketServidor;
	private Socket socketNavegador;
	private BufferedReader in;
	private PrintWriter out;

	public void start()
	{
		super.start();
	}

	public void depura(String mensaje)
	{
		depura(mensaje, DEBUG);
	}

	public void depura(String mensaje, int gravedad)
	{
		if(gravedad == ERROR)
			System.err.println(mensaje);
		else if(gravedad == SINSALTO)
			System.out.print("\t" + mensaje);
		else System.out.println("\t" + mensaje);
	}

	public MyHTTPServer_Thread(Socket recibido, Inet4Address iP, int p_controller, Map<Thread, Socket> hilos, ServerSocket socketServidor)
	{
		this.socketNavegador = recibido;
		this.IP_controller = iP;
		this.p_controller = p_controller;
		this.hilos = hilos;
		this.socketServidor = socketServidor;
	}

	public void peticionWeb(Socket s)
	{
		socketNavegador = s;
		setPriority(NORM_PRIORITY - 1);
	}

	public String leeSocket(Socket socketC)
	{
		String devuelto = "";
		try
		{
			DataInputStream flujo = new DataInputStream(socketC.getInputStream());
			devuelto = flujo.readUTF();
		}catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}finally
		{
			return devuelto;
		}
	}

	public void escribeSocket(Socket socketC, String p_Datos)
	{
		try
		{
			DataOutputStream flujo = new DataOutputStream(socketC.getOutputStream());
			flujo.writeUTF(p_Datos);
		}catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		return;
	}

	@Override
	public void run()
	{
		System.err.println("Hilo en marcha");

		String datos = "";
		try
		{
			in = new BufferedReader(new InputStreamReader(socketNavegador.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socketNavegador.getOutputStream(), "UTF8"), true);

			String cadena = "";

			cadena = in.readLine();

			depura("in.readLine() contiene:\n\n\t" + cadena + "\n");

			StringTokenizer stk = new StringTokenizer(cadena);
			cadena = null;

			if ((stk.countTokens() >= 2))
			{
				if(stk.nextToken().equals("GET"))
				{
					String pedido = stk.nextToken();
					depura("Pedido: " + pedido);
			
					if (pedido.length() >= 15)
					{
						if (!pedido.substring(1, 14).equals("controladorSD"))
						{
							retornaFichero(pedido);
							in.close();
						} else
						{ // CONTROLADOR SD
							try
							{
								pedido = pedido.substring(15);
	
								Socket sc = new Socket(IP_controller, p_controller);
								escribeSocket(sc, pedido);
								datos = leeSocket(sc);
	
								out.println(datos);
							}
							catch (Exception e)
							{
								envia40x(null, 9);
								e.printStackTrace();
							}
						}
					}
					else
					{
						retornaFichero(pedido);
					}
				}
				else envia40x(null, 5);
			}
		}
		catch (IOException e)
		{
			depura("Error en hilo del servidor\n");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				socketNavegador.close();
				in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				hilos.remove(this, socketNavegador);
				depura("Hilo terminado! Cerrando...", ERROR);
			}
		}
	}

	private void enviaHTML(File mifichero)
	throws IOException
	{
		out.println("HTTP/1.1 200 OK");
		out.println("Date: " + new Date());
		out.println("Content-Length: " + mifichero.length());
		out.println("Content-Type: text/html");
		out.println("Server: MyHTTPServer Server/1.1");
		out.println("\n\n");
		BufferedReader ficheroLocal = new BufferedReader(new FileReader(mifichero));

		String linea = "";

		do
		{
			linea = ficheroLocal.readLine();

			if (linea != null)
			{
				out.println(linea);
			}
		} while (linea != null);

		depura("fichero enviado");

		ficheroLocal.close();
	}
	
	private void envia40x(File mifichero, int n)
	throws IOException
	{
		mifichero = new File(MyHTTPServer.path + "/error40" + n + ".htm");
		out.println("HTTP/1.1 40" + n);
		out.println("Date: " + new Date());
		out.println("Content-Length: " + mifichero.length());
		out.println("Content-Type: text/html");
		out.println("Server: MyHTTPServer Server/1.1");
		out.println("\n\n");
		BufferedReader ficheroLocal = new BufferedReader(new FileReader(mifichero));

		String linea = "";

		do
		{
			linea = ficheroLocal.readLine();

			if (linea != null)
			{
				out.println(linea);
			}
		} while (linea != null);

		depura("fichero enviado, cerrando comunicacion");

		ficheroLocal.close();
	}

	void retornaFichero(String sfichero)
	{
		depura(("tratamos de recuperar el fichero " + sfichero), ERROR);

		if (sfichero.equals("/"))
		{
			sfichero = sfichero + "index.htm";
		}
		sfichero = MyHTTPServer.path + sfichero;
		try
		{
			File mifichero = new File(sfichero);
			depura("Buscando fichero " + mifichero.getName());

			if (mifichero.exists())
			{
				enviaHTML(mifichero);
			}
			else
			{
				depura("No encuentro el fichero " + mifichero.toString());
				mifichero = null;
				envia40x(mifichero, 4);
			}
		} catch (Exception e)
		{
			depura("Error al retornar fichero");
			e.printStackTrace();
		} finally
		{
			out.close();
		}
	
	}

}
