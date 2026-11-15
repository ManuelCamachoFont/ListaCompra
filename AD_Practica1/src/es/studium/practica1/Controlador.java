package es.studium.practica1;

import java.io.IOException;

import es.studium.practica1.Modelo.ArticuloAComprar;
import es.studium.practica1.Modelo.ListaDeLaCompra;

public class Controlador
{
	private Modelo modelo;
	private Vista vista;

	public Controlador(Modelo modelo, Vista vista)
	{
		this.modelo = modelo;
		this.vista = vista;
	}

	public void ejecutar()
	{
		try
		{
			ArticuloAComprar arroz = new ArticuloAComprar("Paquete de arroz de 1 Kg", 3, "Kg");
			ArticuloAComprar leche = new ArticuloAComprar("Brick de leche entera", 6, "L");
			ArticuloAComprar pan = new ArticuloAComprar("Barra de pan", 2, "U");
			ArticuloAComprar huevos = new ArticuloAComprar("Huevos camperos", 12, "U");
			ArticuloAComprar aceite = new ArticuloAComprar("Aceite de oliva virgen extra", 1, "L");
			ArticuloAComprar tomates = new ArticuloAComprar("Tomates de ensalada", 1.5, "Kg");
			ArticuloAComprar yogures = new ArticuloAComprar("Yogures naturales", 8, "U");

			ListaDeLaCompra mercadona = new ListaDeLaCompra("Mercadona");
			mercadona.agregarProductoAComprar(arroz);
			mercadona.agregarProductoAComprar(leche);
			mercadona.agregarProductoAComprar(pan);
			mercadona.agregarProductoAComprar(huevos);
			mercadona.agregarProductoAComprar(aceite);

			ListaDeLaCompra lidl = new ListaDeLaCompra("Lidl");
			lidl.agregarProductoAComprar(tomates);
			lidl.agregarProductoAComprar(yogures);
			lidl.agregarProductoAComprar(pan);
			lidl.agregarProductoAComprar(leche);

			vista.mostrarTitulo("LISTAS CREADAS");
			vista.mostrarLista(mercadona);
			vista.mostrarLista(lidl);

			guardar(mercadona);
			guardar(lidl);

			vista.mostrarTitulo("LISTAS RECUPERADAS DESDE FICHERO (ANTES DE ELIMINAR)");
			ListaDeLaCompra mercadonaFichero = modelo.cargarLista(mercadona.getNombreLista());
			ListaDeLaCompra lidlFichero = modelo.cargarLista(lidl.getNombreLista());
			vista.mostrarLista(mercadonaFichero);
			vista.mostrarLista(lidlFichero);

			vista.mostrarTitulo("ELIMINANDO ARTÍCULOS");
			eliminar(mercadonaFichero, "Barra de pan");
			eliminar(mercadonaFichero, "Huevos camperos");
			eliminar(lidlFichero, "Yogures naturales");
			eliminar(lidlFichero, "Chocolate negro");

			vista.mostrarTitulo("LISTAS DESPUÉS DE ELIMINAR");
			vista.mostrarLista(mercadonaFichero);
			vista.mostrarLista(lidlFichero);

			guardar(mercadonaFichero);
			guardar(lidlFichero);

			vista.mostrarTitulo("LISTAS RECUPERADAS DESDE FICHERO (DESPUÉS DE ELIMINAR)");
			vista.mostrarLista(modelo.cargarLista("Mercadona"));
			vista.mostrarLista(modelo.cargarLista("Lidl"));
		}
		catch (IOException e)
		{
			vista.mostrarError("Se ha producido un error al trabajar con los ficheros: " + e.getMessage());
		}
	}

	private void guardar(ListaDeLaCompra lista) throws IOException
	{
		modelo.guardarLista(lista);
		if (modelo.existeFichero(lista.getNombreLista()))
		{
			vista.mostrarMensaje("Lista '" + lista.getNombreLista() + "' guardada en "
					+ modelo.rutaFichero(lista.getNombreLista()));
		}
	}

	private void eliminar(ListaDeLaCompra lista, String descripcion)
	{
		int antes = lista.getArticulos().size();
		lista.eliminarArticulo(descripcion);
		if (lista.getArticulos().size() < antes)
		{
			vista.mostrarMensaje("Eliminado '" + descripcion + "' de la lista " + lista.getNombreLista());
		}
		else
		{
			vista.mostrarMensaje("'" + descripcion + "' no está en la lista " + lista.getNombreLista()
					+ " (no se elimina nada)");
		}
	}
}
