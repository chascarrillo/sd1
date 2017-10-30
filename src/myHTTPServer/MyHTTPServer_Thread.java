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
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

import static myHTTPServer.MyHTTPServer.hilos;
import static myHTTPServer.MyHTTPServer.cont;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class MyHTTPServer_Thread
extends Thread
{
	private static final int ERROR = 0;
	private static final int WARNING = 1;
	private static final int DEBUG = 2;

	private int p_controller;
	private Inet4Address IP_controller;
	private Socket socketServidor;
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
		if(gravedad != ERROR)
			System.out.println("\t" + mensaje);
		else System.err.println(mensaje);
	}

	public MyHTTPServer_Thread(Socket recibido, Inet4Address iP, int p_controller)
	{
		this.socketServidor = recibido;
		this.IP_controller = iP;
		this.p_controller = p_controller;
	}

	public void peticionWeb(Socket s)
	{
		socketServidor = s;
		setPriority(NORM_PRIORITY - 1);
	}

	public String leeSocket(Socket p_sk, String p_Datos)
	{
		try
		{
			InputStream aux = p_sk.getInputStream();
			DataInputStream flujo = new DataInputStream(aux);
			p_Datos = flujo.readUTF();
		}catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}

		return p_Datos;
	}

	public void escribeSocket(Socket p_sk, String p_Datos)
	{
		try
		{
			OutputStream aux = p_sk.getOutputStream();
			DataOutputStream flujo = new DataOutputStream(aux);
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
			in = new BufferedReader(new InputStreamReader(socketServidor.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socketServidor.getOutputStream(), "UTF8"), true);

			String cadena = "";

			
			cadena = in.readLine();

			depura("in.readLine() contiene:\n\n\t" + cadena + "\n");

			StringTokenizer stk = new StringTokenizer(cadena);
			cadena = null;

			if ((stk.countTokens() >= 2) && stk.nextToken().equals("GET"))
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
						pedido = pedido.substring(15);
	
						Socket sc = new Socket(IP_controller, p_controller);
						escribeSocket(sc, pedido);
						String print = leeSocket(sc, datos);
	
						PrintWriter pw = new PrintWriter(socketServidor.getOutputStream());
						pw.println(print);
						pw.flush();
						in.close();
					}
				}
				else
				{
					retornaFichero(pedido);
					in.close();
				}
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
				socketServidor.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				hilos.remove(this, socketServidor);
				cont--;
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

		depura("fichero enviado, cerrando comunicacion");

		ficheroLocal.close();
	}
	
	private void envia404(File mifichero)
	throws IOException
	{
		out.println("HTTP/1.1 404");
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
				mifichero = new File(MyHTTPServer.path + "/error404.htm");
				envia404(mifichero);
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
