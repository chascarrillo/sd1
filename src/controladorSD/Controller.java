package controladorSD;

import java.io.*;
import java.net.*;
import java.net.UnknownHostException;
import java.util.*;

import sondasRMI.InterfRemSondas;

import java.rmi.*;

/**
 *
 * @author ALFONSO ARACIL ANDRES, 48563029R
 */
public class Controller
{
	private int puerto = 0;
	private Inet4Address IP_RMI;
	private Inet4Address IP;
	private int puerto_RMI;
	private String html = "";

	private static final int ERROR = 0;
	private static final int SINSALTO = 1;
	private static final int DEBUG = 2;

	public void depura(String mensaje)
	{
		depura(mensaje, DEBUG);
	}

	public void depura(String mensaje, int gravedad)
	{
		switch(gravedad)
		{
			case ERROR:
				System.err.println(mensaje);
				break;
			case SINSALTO:
				System.err.print(mensaje);
				break;
			default:
				System.out.print("\t" + mensaje);
				break;
		}
	}

	public void escribeSocket(Socket socketHTTPServer, String p_Datos)
	{
		try
		{
			DataOutputStream flujo = new DataOutputStream(socketHTTPServer.getOutputStream());
			flujo.writeUTF(p_Datos);
		}
		catch (Exception e)
		{
			depura("Error: ", SINSALTO);
			e.printStackTrace();
		}
	}

	public String leeSocket(Socket socketHTTPServer)
	throws IOException
	{
		DataInputStream flujo = new DataInputStream(socketHTTPServer.getInputStream());
		return (String) flujo.readUTF();
	}

	public void compruebaParametros(String[] args)
	throws UnknownHostException
	{
		if (args.length == 2)
		{
			this.IP = (Inet4Address) InetAddress.getByAddress(InetAddress.getLocalHost().getAddress());
			this.puerto = Integer.parseInt(args[0]);
			this.IP_RMI = (Inet4Address) InetAddress.getByName(args[1]);
			this.puerto_RMI = 1099;
		}
		else
		{
			depura("Error en servidor, parametros invalidos.\n");
		}
	}

	public Controller(String[] args)
	{
		try
		{
			compruebaParametros(args);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		Controller control = new Controller(args);
		control.run();
	}

	public void enviarRMI(Integer nsonda, int op, Socket socketHTTPServer)
	{

		try
		{
			/*
			 * Recorrer todas las sondas for(int i = 1; i <=
			 * Naming.list(servidor).length;i++){ System.setSecurityManager(new
			 * SecurityManager()); InterfRemSondas objetoRemoto = (InterfRemSondas)
			 * Naming.lookup(servidor+"/Sonda"+i);
			 * 
			 * rmi://localhost:1099/Sonda1 }
			 */
			InterfRemSondas objetoRemoto = null;
			String servidor = "rmi://" + IP_RMI.getHostAddress() + ":" + puerto_RMI + "/ObjetoRemSondas";
//			System.setSecurityManager(new RMISecurityManager());
			System.setSecurityManager(new SecurityManager());
			if (op != 6)
			{
				objetoRemoto = (InterfRemSondas) Naming.lookup(servidor + "/Sonda" + nsonda.toString());
			}

			switch (op)
			{
				case 1:
					html = "<html>\n" + "<head><h1>Acceso sondas</h1></head>\n" + "<body><h4>Sonda" + nsonda + "</h4>\n"
							+ "	<p>La humedad de la sonda es: " + objetoRemoto.getHumedad(nsonda).toString() + "</p>\n"
							+ "</body>\n" + "</html>";
					break;

				case 2:
					html = "<html>\n" + "<head><h1>Acceso sondas</h1></head>\n" + "<body><h4>Sonda" + nsonda + "</h4>\n"
							+ "	<p>La temperatura de la sonda es: " + objetoRemoto.getTemperatura(nsonda).toString() + "</p>\n"
							+ "</body>\n" + "</html>";
					break;

				case 3:
					html = "<html><h1>Lista sondas activas</h1>\n";
					for (int j = 1; j <= 3; j++)
					{
						html = html + "<h4>Sonda " + j + "</h4>\n" + "<ol>\n" + "<ul>\n"
								+ "   <li><a href=\"/controladorSD/humedad?sonda=" + j + "\" >Humedad</a></li>\n"
								+ "   <li><a href=\"/controladorSD/temperatura?sonda=" + j + "\" >Temperatura</a></li>\n"
								+ "   <li><a href=\"/controladorSD/luz?sonda=" + j + "\" >Luz</a></li>\n"
								+ "   <li><input type=\"text\" name=\"nombredelacaja\"><button type=\"submit\" onclick=\"myFunction()\"></li>\n"
								+ "</ul>\n" + "</ol>\n" + "<script type=\"text/javascript\">\n"
								+ "var baseUrl = window.location.hostname;\n"
								+ "if(window.location.port != \"\") baseUrl += \":\" + window.location.port + \"/\";\n"
								+ "baseUrl = \"http://\" + baseUrl;\n" + "function myFunction() {\n"
								+ "var x = document.getElementById(\"nombredelacaja\").value;\n" + "var y = " + j + ";\n"
								+ "window.location.replace(baseUrl + \"controladorSD/setluz=\" + x + \"?sonda=\"" + j
								+ ");\n"
								+ "    document.location.href=\"/controladorSD/setluz=\"document.getElementById(\"texto\").value\"?sonda="
								+ j + "\";\n" + "}\n" + "</script>";
						System.out.println(html);
					}
					html = html + "</html>";
					System.out.println(html);
					break;
	
				default:
					break;
			}
			html = "HTTP/1.1 200 OK" + "Date: " + new Date()
			// +"Content-Length: " +html.length()
					+ "Content-Type: text/html" + "Server: MyHTTPServer Server/1.1" + "\n\n" + html;
			System.out.println("Llega a escribesocket " + html);
			escribeSocket(socketHTTPServer, html);

		} catch (Exception e) {

		}

	}

	private void ejecutarPeticion(int op, int nsonda, Socket socketHTTPServer)
	{
		enviarRMI(nsonda, op, socketHTTPServer);
	}

	private void setLuz(int nsonda, int luz)
	{
		System.out.println("Setteamos luz");
	}

	private boolean run()
	{
		depura("Controller running...", ERROR);
		int pos = 0;
		int op = 0; // Indicamos la solicitud del servidor: 1- humedad, 2-temperatura, 3-luz,
					// 4-ultimafecha, 5-setluz
		int nsonda = 0;
		int setluz = 0;
		String lecturaRecibida = "";
		try
		{
			ServerSocket sk = new ServerSocket(puerto);

			depura("Escuchando en " + IP.getHostAddress() + ":" + puerto);
			System.out.print("\n");
			int peticionesAtendidas = 0;

			do
			{
				Socket socketHTTPServer = sk.accept();
				peticionesAtendidas++;
				depura("Peticiones atendidas: " + peticionesAtendidas);

				lecturaRecibida = leeSocket(socketHTTPServer);
// ---------------------------------------------------------------------------------------
				pos = lecturaRecibida.indexOf('?');
				if (pos != -1)
				{
					switch (lecturaRecibida.substring(0, pos))
					{
						case "humedad":
							System.out.println("ha detectado humedad");
							op = 1;
							break;
						case "temperatura":
							System.out.println("ha detectado temperatura");
							op = 2;
							break;
						case "luz":
							System.out.println("ha detectado luz");
							op = 3;
							break;
						case "ultimafecha":
							System.out.println("ha detectado fecha");
							op = 4;
							break;
						default:
							if (lecturaRecibida.substring(0, 6).equals("setluz"))
							{
								setluz = Integer.parseInt(lecturaRecibida.substring(7, pos));
								op = 5;
							}
							else
							{
								op = -1;
							}
							break;
					}
					if(lecturaRecibida.substring(pos+1, pos+7) == "sonda"  &&
					lecturaRecibida.charAt(pos+7) == '=')
					{
						nsonda = Integer.valueOf(lecturaRecibida.substring(pos+8, pos+9));
					}
					ejecutarPeticion(op, nsonda, socketHTTPServer);
					depura("Posicion: " + pos);
					depura("Solicitud: " + op);
					lecturaRecibida = lecturaRecibida.substring(pos + 1, lecturaRecibida.length());
					depura("\nlectura recibida: \n\n" + lecturaRecibida + "\n\n", SINSALTO);
					
				}
				else if (lecturaRecibida.length() == 0 || lecturaRecibida.equals("index"))
				{
					op = 3;
					ejecutarPeticion(op, nsonda, socketHTTPServer);
				}
				else
				{
					depura("Peticion incorrecta, comprueba la URL", ERROR);
				}

				depura("Posicion: " + pos);
				depura("Solicitud: " + op);

				socketHTTPServer.close();
			} while (true);
		}
		catch (Exception e)
		{
			depura("Controller error");
			e.printStackTrace();
		}
		return true;
	}
}
