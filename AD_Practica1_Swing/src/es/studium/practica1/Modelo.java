package es.studium.practica1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Modelo
{
	public static final String CARPETA_DATA = "data";

	public static final String FORMATO_TXT = ".txt";
	public static final String FORMATO_DAT = ".dat";
	public static final String FORMATO_JSON = ".json";
	public static final String[] FORMATOS = { FORMATO_TXT, FORMATO_DAT, FORMATO_JSON };

	private static final String SEPARADOR = ";";

	private static final String FICHERO_CONFIG = "config.properties";
	private static final String CLAVE_TEMA = "tema";

	public static class ArticuloAComprar
	{
		private String descripcion;
		private double cantidad;
		private String unidad;

		public ArticuloAComprar()
		{
			this.descripcion = "";
			this.cantidad = 0;
			this.unidad = "";
		}

		public ArticuloAComprar(String descripcion, double cantidad, String unidad)
		{
			this.descripcion = descripcion;
			this.cantidad = cantidad;
			this.unidad = unidad;
		}

		public String getDescripcion()
		{
			return descripcion;
		}

		public void setDescripcion(String descripcion)
		{
			this.descripcion = descripcion;
		}

		public double getCantidad()
		{
			return cantidad;
		}

		public void setCantidad(double cantidad)
		{
			this.cantidad = cantidad;
		}

		public String getUnidad()
		{
			return unidad;
		}

		public void setUnidad(String unidad)
		{
			this.unidad = unidad;
		}

		@Override
		public String toString()
		{
			String textoCantidad;
			if (cantidad == Math.floor(cantidad))
			{
				textoCantidad = String.valueOf((long) cantidad);
			}
			else
			{
				textoCantidad = String.valueOf(cantidad);
			}
			return descripcion + " -> " + textoCantidad + " " + unidad;
		}
	}

	public static class ListaDeLaCompra
	{
		private String nombreLista;
		private List<ArticuloAComprar> articulos;

		public ListaDeLaCompra()
		{
			this.nombreLista = "";
			this.articulos = new ArrayList<ArticuloAComprar>();
		}

		public ListaDeLaCompra(String nombreLista)
		{
			this.nombreLista = nombreLista;
			this.articulos = new ArrayList<ArticuloAComprar>();
		}

		public String getNombreLista()
		{
			return nombreLista;
		}

		public void setNombreLista(String nombreLista)
		{
			this.nombreLista = nombreLista;
		}

		public void agregarProductoAComprar(ArticuloAComprar articulo)
		{
			if (articulo != null)
			{
				articulos.add(articulo);
			}
		}

		public void eliminarArticulo(String descripcionArticulo)
		{
			if (articulos.isEmpty() || descripcionArticulo == null)
			{
				return;
			}
			Iterator<ArticuloAComprar> it = articulos.iterator();
			while (it.hasNext())
			{
				ArticuloAComprar articulo = it.next();
				if (articulo.getDescripcion().equalsIgnoreCase(descripcionArticulo))
				{
					it.remove();
					return;
				}
			}
		}

		public List<ArticuloAComprar> getArticulos()
		{
			return articulos;
		}
	}

	public static boolean esFormatoValido(String nombreFichero)
	{
		return formatoDe(nombreFichero) != null;
	}

	public static String formatoDe(String nombreFichero)
	{
		for (String formato : FORMATOS)
		{
			if (nombreFichero.toLowerCase().endsWith(formato))
			{
				return formato;
			}
		}
		return null;
	}

	public static String nombreSinExtension(String nombreFichero)
	{
		String formato = formatoDe(nombreFichero);
		if (formato == null)
		{
			return nombreFichero;
		}
		return nombreFichero.substring(0, nombreFichero.length() - formato.length());
	}

	private File obtenerFichero(String nombreLista, String formato)
	{
		return new File(CARPETA_DATA, nombreLista + formato);
	}

	public boolean existeFichero(String nombreLista, String formato)
	{
		return obtenerFichero(nombreLista, formato).exists();
	}

	public String rutaFichero(String nombreLista, String formato)
	{
		return obtenerFichero(nombreLista, formato).getPath();
	}

	public void guardarLista(ListaDeLaCompra lista, String formato) throws IOException
	{
		new File(CARPETA_DATA).mkdirs();
		File fichero = obtenerFichero(lista.getNombreLista(), formato);

		if (FORMATO_TXT.equals(formato))
		{
			guardarTxt(lista, fichero);
		}
		else if (FORMATO_DAT.equals(formato))
		{
			guardarDat(lista, fichero);
		}
		else if (FORMATO_JSON.equals(formato))
		{
			guardarJson(lista, fichero);
		}
		else
		{
			throw new IOException("Formato no admitido: " + formato);
		}
	}

	public ListaDeLaCompra cargarLista(String nombreLista, String formato) throws IOException
	{
		ListaDeLaCompra lista = new ListaDeLaCompra();
		lista.setNombreLista(nombreLista);
		File fichero = obtenerFichero(nombreLista, formato);

		if (FORMATO_TXT.equals(formato))
		{
			cargarTxt(lista, fichero);
		}
		else if (FORMATO_DAT.equals(formato))
		{
			cargarDat(lista, fichero);
		}
		else if (FORMATO_JSON.equals(formato))
		{
			cargarJson(lista, fichero);
		}
		else
		{
			throw new IOException("Formato no admitido: " + formato);
		}
		return lista;
	}

	private void guardarTxt(ListaDeLaCompra lista, File fichero) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fichero), StandardCharsets.UTF_8));
		try
		{
			for (ArticuloAComprar articulo : lista.getArticulos())
			{
				bw.write(articulo.getDescripcion() + SEPARADOR
						+ articulo.getCantidad() + SEPARADOR
						+ articulo.getUnidad());
				bw.newLine();
			}
		}
		finally
		{
			bw.close();
		}
	}

	private void cargarTxt(ListaDeLaCompra lista, File fichero) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(fichero), StandardCharsets.UTF_8));
		try
		{
			String linea = br.readLine();
			while (linea != null)
			{
				String[] campos = linea.split(SEPARADOR);
				if (campos.length == 3)
				{
					lista.agregarProductoAComprar(new ArticuloAComprar(
							campos[0], Double.parseDouble(campos[1]), campos[2]));
				}
				linea = br.readLine();
			}
		}
		finally
		{
			br.close();
		}
	}

	private void guardarDat(ListaDeLaCompra lista, File fichero) throws IOException
	{
		DataOutputStream salida = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(fichero)));
		try
		{
			for (ArticuloAComprar articulo : lista.getArticulos())
			{
				salida.writeUTF(articulo.getDescripcion());
				salida.writeDouble(articulo.getCantidad());
				salida.writeUTF(articulo.getUnidad());
			}
		}
		finally
		{
			salida.close();
		}
	}

	private void cargarDat(ListaDeLaCompra lista, File fichero) throws IOException
	{
		DataInputStream entrada = new DataInputStream(
				new BufferedInputStream(new FileInputStream(fichero)));
		try
		{
			while (true)
			{
				String descripcion;
				try
				{
					descripcion = entrada.readUTF();
				}
				catch (EOFException fin)
				{
					break;
				}
				double cantidad = entrada.readDouble();
				String unidad = entrada.readUTF();
				lista.agregarProductoAComprar(new ArticuloAComprar(descripcion, cantidad, unidad));
			}
		}
		finally
		{
			entrada.close();
		}
	}

	private void guardarJson(ListaDeLaCompra lista, File fichero) throws IOException
	{
		JSONArray articulos = new JSONArray();
		for (ArticuloAComprar articulo : lista.getArticulos())
		{
			JSONObject objeto = new JSONObject();
			objeto.put("descripcion", articulo.getDescripcion());
			objeto.put("cantidad", articulo.getCantidad());
			objeto.put("unidad", articulo.getUnidad());
			articulos.put(objeto);
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(fichero), StandardCharsets.UTF_8);
		try
		{
			writer.write(articulos.toString(2));
		}
		finally
		{
			writer.close();
		}
	}

	private void cargarJson(ListaDeLaCompra lista, File fichero) throws IOException
	{
		InputStreamReader reader = new InputStreamReader(
				new FileInputStream(fichero), StandardCharsets.UTF_8);
		try
		{
			JSONArray articulos = new JSONArray(new JSONTokener(reader));
			for (int i = 0; i < articulos.length(); i++)
			{
				JSONObject objeto = articulos.getJSONObject(i);
				lista.agregarProductoAComprar(new ArticuloAComprar(
						objeto.getString("descripcion"),
						objeto.getDouble("cantidad"),
						objeto.getString("unidad")));
			}
		}
		catch (JSONException e)
		{
			throw new IOException("El fichero " + fichero.getName() + " no tiene un JSON válido: "
					+ e.getMessage());
		}
		finally
		{
			reader.close();
		}
	}

	public List<String> listarListas()
	{
		File carpeta = new File(CARPETA_DATA);
		carpeta.mkdirs();
		String[] ficheros = carpeta.list((ruta, nombreFichero) -> esFormatoValido(nombreFichero));
		if (ficheros == null)
		{
			return Collections.emptyList();
		}
		Arrays.sort(ficheros, String.CASE_INSENSITIVE_ORDER);
		return Collections.unmodifiableList(Arrays.asList(ficheros));
	}

	public void renombrarLista(ListaDeLaCompra lista, String formato, String nuevoNombre) throws IOException
	{
		File ficheroActual = obtenerFichero(lista.getNombreLista(), formato);
		File ficheroNuevo = obtenerFichero(nuevoNombre, formato);
		if (ficheroNuevo.exists())
		{
			throw new IOException("Ya existe el fichero " + ficheroNuevo.getPath());
		}
		if (ficheroActual.exists() && !ficheroActual.renameTo(ficheroNuevo))
		{
			throw new IOException("No se ha podido renombrar " + ficheroActual.getPath());
		}
		lista.setNombreLista(nuevoNombre);
		guardarLista(lista, formato);
	}

	public void borrarLista(String nombreLista, String formato) throws IOException
	{
		File fichero = obtenerFichero(nombreLista, formato);
		if (fichero.exists() && !fichero.delete())
		{
			throw new IOException("No se ha podido borrar " + fichero.getPath());
		}
	}

	public void guardarTema(String tema) throws IOException
	{
		new File(CARPETA_DATA).mkdirs();
		Properties config = new Properties();
		config.setProperty(CLAVE_TEMA, tema);
		FileOutputStream salida = new FileOutputStream(new File(CARPETA_DATA, FICHERO_CONFIG));
		try
		{
			config.store(salida, "Configuracion de Listas de la compra");
		}
		finally
		{
			salida.close();
		}
	}

	public String cargarTema()
	{
		File fichero = new File(CARPETA_DATA, FICHERO_CONFIG);
		if (!fichero.exists())
		{
			return null;
		}
		Properties config = new Properties();
		try
		{
			FileInputStream entrada = new FileInputStream(fichero);
			try
			{
				config.load(entrada);
			}
			finally
			{
				entrada.close();
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return config.getProperty(CLAVE_TEMA);
	}
}
