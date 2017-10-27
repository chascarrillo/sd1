package myHTTPServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import static myHTTPServer.MyHTTPServer.cont;
import static myHTTPServer.MyHTTPServer.path;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class MyHTTPServer_Thread
extends Thread
{
	private static int DEBUG = 2;

	private int p_controller;
	private Inet4Address IP_controller;
	private Socket socketServidor;
	private BufferedReader in;
	private PrintWriter out;

	public void start()
	{
		super.start();

		MyHTTPServer.cont++;
	}

	public void depura(String mensaje)
	{
		depura(mensaje, DEBUG);
	}

	public void depura(String mensaje, int gravedad)
	{
		System.out.println("Mensaje: " + mensaje);
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

	public void run()
	{
		depura("Procesando conexion...");

		String datos = "";
		try
		{
			in = new BufferedReader(new InputStreamReader(socketServidor.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socketServidor.getOutputStream(), "UTF8"), true);

			String cadena = "";

			do
			{
				cadena = in.readLine();

				if (cadena == null)
				{
					depura("No se puede atender la peticion, la cadena esta vacia.");
				}
				else
				{
					depura("in.readLine() contiene:\n." + cadena + ".");
				}

				StringTokenizer stk = new StringTokenizer(cadena);

				if ((stk.countTokens() >= 2) && stk.nextToken().equals("GET"))
				{
					String pedido = stk.nextToken();
					System.out.println("Pedido: " + pedido);
					if (pedido.substring(1).equals("") || pedido.substring(1).equals("index.htm"))
					{
						retornaFichero(pedido);
						socketServidor.close();
						MyHTTPServer.cont--;
					} else if (pedido.substring(1, 14).equals("controladorSD"))
					{
						pedido = pedido.substring(15);

						Socket sc = new Socket(IP_controller, p_controller);
						escribeSocket(sc, pedido);
						String print = leeSocket(sc, datos);

						PrintWriter pw = new PrintWriter(socketServidor.getOutputStream());
						pw.println(print);
						pw.flush();
						socketServidor.close();
						cadena = null;
					}
				}

			} while (cadena != null && cadena.length() != 0);
		} catch (Exception e) {
			depura("Server error\n" + e.toString());
		}
		depura("Terminado!");
	}

	void retornaFichero(String sfichero)
	{
		depura("Recuperamos el fichero " + sfichero);

	// comprobamos si tiene una barra al principio
	if (sfichero.startsWith("/"))
	{

		if (sfichero.length() == 1)
		{
			sfichero = sfichero + "index.htm";
		}
		sfichero = MyHTTPServer.path + sfichero;
		try
		{
			File mifichero = new File(sfichero);
			System.out.println("Buscando fichero " + mifichero.getName());

			if (mifichero.exists())
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

				depura("fin envio fichero");

				ficheroLocal.close();
				out.close();

			} // fin si el fichero existe
			else
			{
				depura("No encuentro el fichero " + mifichero.toString());
				out.println("HTTP/1.1 404");
				out.close();
			}
		} catch (Exception e)
		{
			depura("Error al retornar fichero");
		}
	}
	}

}
