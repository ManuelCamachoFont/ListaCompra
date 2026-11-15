package es.studium.practica1;

import java.util.List;

import es.studium.practica1.Modelo.ArticuloAComprar;
import es.studium.practica1.Modelo.ListaDeLaCompra;

public class Vista
{
	public void mostrarTitulo(String titulo)
	{
		System.out.println();
		System.out.println("==================================================");
		System.out.println(" " + titulo);
		System.out.println("==================================================");
	}

	public void mostrarLista(ListaDeLaCompra lista)
	{
		System.out.println("Lista de la compra: " + lista.getNombreLista());
		List<ArticuloAComprar> articulos = lista.getArticulos();
		if (articulos.isEmpty())
		{
			System.out.println("\tLa lista está vacía");
		}
		else
		{
			for (int i = 0; i < articulos.size(); i++)
			{
				System.out.println("\t" + (i + 1) + ". " + articulos.get(i));
			}
		}
		System.out.println("\tTotal de artículos: " + articulos.size());
		System.out.println();
	}

	public void mostrarMensaje(String mensaje)
	{
		System.out.println(mensaje);
	}

	public void mostrarError(String mensaje)
	{
		System.err.println("ERROR: " + mensaje);
	}
}
