package es.studium.practica1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Modelo
{
	public static final String CARPETA_DATA = "data";
	public static final String EXTENSION = ".txt";
	private static final String SEPARADOR = ";";

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


	private File obtenerFichero(String nombreLista)
	{
		return new File(CARPETA_DATA, nombreLista + EXTENSION);
	}

	public boolean existeFichero(String nombreLista)
	{
		return obtenerFichero(nombreLista).exists();
	}

	public String rutaFichero(String nombreLista)
	{
		return obtenerFichero(nombreLista).getPath();
	}

	public void guardarLista(ListaDeLaCompra lista) throws IOException
	{
		File carpeta = new File(CARPETA_DATA);
		carpeta.mkdirs();

		File fichero = obtenerFichero(lista.getNombreLista());
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

	public ListaDeLaCompra cargarLista(String nombreLista) throws IOException
	{
		ListaDeLaCompra lista = new ListaDeLaCompra();
		lista.setNombreLista(nombreLista);

		File fichero = obtenerFichero(nombreLista);
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
					String descripcion = campos[0];
					double cantidad = Double.parseDouble(campos[1]);
					String unidad = campos[2];
					lista.agregarProductoAComprar(new ArticuloAComprar(descripcion, cantidad, unidad));
				}
				linea = br.readLine();
			}
		}
		finally
		{
			br.close();
		}
		return lista;
	}
}
