package es.studium.practica1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.studium.practica1.Modelo.ArticuloAComprar;
import es.studium.practica1.Modelo.ListaDeLaCompra;

public class Controlador implements ActionListener, ListSelectionListener
{
	private static final String CARACTERES_NO_VALIDOS = "\\/:*?\"<>|;";

	private final Modelo modelo;
	private final Vista vista;

	private ListaDeLaCompra listaActual;
	private String formatoActual;

	public Controlador(Modelo modelo, Vista vista)
	{
		this.modelo = modelo;
		this.vista = vista;

		vista.getBtnAcceder().addActionListener(this);
		vista.getBtnSalir().addActionListener(this);
		vista.getBtnNuevaLista().addActionListener(this);
		vista.getBtnGuardarComo().addActionListener(this);
		vista.getBtnRenombrar().addActionListener(this);
		vista.getBtnBorrarLista().addActionListener(this);
		vista.getBtnAnadir().addActionListener(this);
		vista.getBtnEliminarArticulo().addActionListener(this);
		vista.getBtnVolver().addActionListener(this);
		vista.getTxtDescripcion().addActionListener(this);
		vista.getTxtCantidad().addActionListener(this);
		vista.getListaListas().addListSelectionListener(this);
		for (JRadioButtonMenuItem item : vista.getItemsTema())
		{
			item.addActionListener(this);
		}

		vista.aplicarTema(temaDesdeNombre(modelo.cargarTema()));

		vista.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				salir();
			}
		});

		vista.mostrarLista(null, null);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object origen = e.getSource();
		if (origen == vista.getBtnAcceder())
		{
			acceder();
		}
		else if (origen == vista.getBtnSalir())
		{
			salir();
		}
		else if (origen == vista.getBtnVolver())
		{
			vista.mostrarBienvenida();
		}
		else if (origen == vista.getBtnNuevaLista())
		{
			nuevaLista();
		}
		else if (origen == vista.getBtnGuardarComo())
		{
			guardarComo();
		}
		else if (origen == vista.getBtnRenombrar())
		{
			renombrarLista();
		}
		else if (origen == vista.getBtnBorrarLista())
		{
			borrarLista();
		}
		else if (origen == vista.getBtnAnadir() || origen == vista.getTxtDescripcion()
				|| origen == vista.getTxtCantidad())
		{
			anadirArticulo();
		}
		else if (origen == vista.getBtnEliminarArticulo())
		{
			eliminarArticulo();
		}
		else
		{
			for (JRadioButtonMenuItem item : vista.getItemsTema())
			{
				if (origen == item)
				{
					cambiarTema(Vista.Tema.valueOf(item.getActionCommand()));
				}
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			abrirListaSeleccionada();
		}
	}

	private void acceder()
	{
		refrescarListas(null);
		vista.mostrarPrincipal();
		if (listaActual == null)
		{
			vista.setEstado("No hay listas guardadas en la carpeta " + Modelo.CARPETA_DATA
					+ ". Pulsa \"Nueva lista\" para crear una.");
		}
	}

	private void salir()
	{
		if (vista.confirmar("¿Seguro que quieres salir de la aplicación?"))
		{
			vista.dispose();
			System.exit(0);
		}
	}

	private void abrirListaSeleccionada()
	{
		String fichero = vista.getListaSeleccionada();
		if (fichero == null)
		{
			cerrarListaActual();
			return;
		}
		String nombre = Modelo.nombreSinExtension(fichero);
		String formato = Modelo.formatoDe(fichero);
		try
		{
			listaActual = modelo.cargarLista(nombre, formato);
			formatoActual = formato;
			vista.mostrarLista(listaActual, formatoActual);
			vista.setEstado("Lista '" + nombre + "' cargada desde " + modelo.rutaFichero(nombre, formato));
		}
		catch (IOException ex)
		{
			cerrarListaActual();
			vista.mostrarError("No se ha podido leer el fichero " + fichero + ":\n" + ex.getMessage());
		}
		catch (RuntimeException ex)
		{
			cerrarListaActual();
			vista.mostrarError("El fichero " + fichero + " tiene un contenido no válido:\n" + ex.getMessage());
		}
	}

	private void nuevaLista()
	{
		String[] datos = pedirNombreYFormatoValidos("Nueva lista", "", Modelo.FORMATO_TXT);
		if (datos == null)
		{
			return;
		}
		String nombre = datos[0];
		String formato = datos[1];
		try
		{
			modelo.guardarLista(new ListaDeLaCompra(nombre), formato);
			refrescarListas(nombre + formato);
			vista.setEstado("Lista '" + nombre + "' creada en " + modelo.rutaFichero(nombre, formato));
		}
		catch (IOException ex)
		{
			vista.mostrarError("No se ha podido crear la lista:\n" + ex.getMessage());
		}
	}

	private void guardarComo()
	{
		if (!hayListaAbierta())
		{
			return;
		}
		String[] datos = pedirNombreYFormatoValidos("Guardar como...",
				listaActual.getNombreLista(), siguienteFormato(formatoActual));
		if (datos == null)
		{
			return;
		}
		String nombre = datos[0];
		String formato = datos[1];
		ListaDeLaCompra copia = new ListaDeLaCompra(nombre);
		for (ArticuloAComprar articulo : listaActual.getArticulos())
		{
			copia.agregarProductoAComprar(new ArticuloAComprar(
					articulo.getDescripcion(), articulo.getCantidad(), articulo.getUnidad()));
		}
		try
		{
			modelo.guardarLista(copia, formato);
			refrescarListas(nombre + formato);
			vista.setEstado("Copia guardada en " + modelo.rutaFichero(nombre, formato));
		}
		catch (IOException ex)
		{
			vista.mostrarError("No se ha podido guardar la copia:\n" + ex.getMessage());
		}
	}

	private void renombrarLista()
	{
		if (!hayListaAbierta())
		{
			return;
		}
		String anterior = listaActual.getNombreLista();
		String nuevo = anterior;
		while (true)
		{
			nuevo = vista.pedirTexto("Nuevo nombre para la lista '" + anterior + "' (se mantiene el formato "
					+ formatoActual + "):", nuevo);
			if (nuevo == null)
			{
				return;
			}
			nuevo = nuevo.trim();
			if (nuevo.equals(anterior))
			{
				return;
			}
			if (nombreValido(nuevo, formatoActual))
			{
				break;
			}
		}
		try
		{
			modelo.renombrarLista(listaActual, formatoActual, nuevo);
			refrescarListas(nuevo + formatoActual);
			vista.setEstado("Lista '" + anterior + "' renombrada a '" + nuevo + "' ("
					+ modelo.rutaFichero(nuevo, formatoActual) + ")");
		}
		catch (IOException ex)
		{
			vista.mostrarError("No se ha podido renombrar la lista:\n" + ex.getMessage());
		}
	}

	private void borrarLista()
	{
		if (!hayListaAbierta())
		{
			return;
		}
		String nombre = listaActual.getNombreLista();
		String ruta = modelo.rutaFichero(nombre, formatoActual);
		if (!vista.confirmar("¿Seguro que quieres borrar la lista '" + nombre + "'?\n"
				+ "Se eliminará el fichero " + ruta + "."))
		{
			return;
		}
		try
		{
			modelo.borrarLista(nombre, formatoActual);
			cerrarListaActual();
			refrescarListas(null);
			vista.setEstado("Fichero " + ruta + " borrado");
		}
		catch (IOException ex)
		{
			vista.mostrarError("No se ha podido borrar la lista:\n" + ex.getMessage());
		}
	}

	private void anadirArticulo()
	{
		if (!hayListaAbierta())
		{
			return;
		}
		String descripcion = vista.getDescripcion().trim();
		String textoCantidad = vista.getCantidadTexto().trim().replace(',', '.');
		String unidad = vista.getUnidad().trim();

		if (descripcion.isEmpty())
		{
			vista.mostrarError("Escribe la descripción del artículo.");
			return;
		}
		if (descripcion.contains(";") || unidad.contains(";"))
		{
			vista.mostrarError("La descripción y la unidad no pueden contener el carácter ';'.");
			return;
		}
		if (unidad.isEmpty())
		{
			vista.mostrarError("Indica la unidad de medida (U, Kg, L...).");
			return;
		}
		double cantidad;
		try
		{
			cantidad = Double.parseDouble(textoCantidad);
		}
		catch (NumberFormatException ex)
		{
			vista.mostrarError("La cantidad debe ser un número (por ejemplo 3 o 1,5).");
			return;
		}
		if (cantidad <= 0 || Double.isInfinite(cantidad) || Double.isNaN(cantidad))
		{
			vista.mostrarError("La cantidad debe ser un número mayor que 0.");
			return;
		}

		listaActual.agregarProductoAComprar(new ArticuloAComprar(descripcion, cantidad, unidad));
		if (guardarListaActual())
		{
			vista.mostrarLista(listaActual, formatoActual);
			vista.limpiarFormulario();
			vista.setEstado("Añadido '" + descripcion + "' y guardado en "
					+ modelo.rutaFichero(listaActual.getNombreLista(), formatoActual));
		}
	}

	private void eliminarArticulo()
	{
		if (!hayListaAbierta())
		{
			return;
		}
		int fila = vista.getFilaSeleccionada();
		if (fila < 0)
		{
			vista.mostrarError("Selecciona en la tabla el artículo que quieres eliminar.");
			return;
		}
		String descripcion = vista.getDescripcionFila(fila);
		listaActual.eliminarArticulo(descripcion);
		if (guardarListaActual())
		{
			vista.mostrarLista(listaActual, formatoActual);
			vista.setEstado("Eliminado '" + descripcion + "' y guardado en "
					+ modelo.rutaFichero(listaActual.getNombreLista(), formatoActual));
		}
	}

	private void cambiarTema(Vista.Tema tema)
	{
		vista.aplicarTema(tema);
		try
		{
			modelo.guardarTema(tema.name());
			vista.setEstado("Tema '" + tema.getNombre() + "' aplicado");
		}
		catch (IOException ex)
		{
			vista.setEstado("Tema aplicado, pero no se ha podido guardar: " + ex.getMessage());
		}
	}

	private Vista.Tema temaDesdeNombre(String nombre)
	{
		if (nombre == null)
		{
			return Vista.Tema.CLARO;
		}
		try
		{
			return Vista.Tema.valueOf(nombre.trim());
		}
		catch (IllegalArgumentException ex)
		{
			return Vista.Tema.CLARO;
		}
	}

	private void refrescarListas(String ficheroASeleccionar)
	{
		List<String> ficheros = modelo.listarListas();
		vista.cargarNombresListas(ficheros);
		if (ficheroASeleccionar != null)
		{
			vista.seleccionarLista(ficheroASeleccionar);
		}
		else if (!ficheros.isEmpty())
		{
			vista.seleccionarLista(ficheros.get(0));
		}
		else
		{
			cerrarListaActual();
		}
	}

	private void cerrarListaActual()
	{
		listaActual = null;
		formatoActual = null;
		vista.mostrarLista(null, null);
	}

	private boolean guardarListaActual()
	{
		try
		{
			modelo.guardarLista(listaActual, formatoActual);
			return true;
		}
		catch (IOException ex)
		{
			vista.mostrarError("No se han podido guardar los cambios:\n" + ex.getMessage());
			return false;
		}
	}

	private boolean hayListaAbierta()
	{
		if (listaActual == null)
		{
			vista.mostrarError("Primero selecciona o crea una lista.");
			return false;
		}
		return true;
	}

	private String[] pedirNombreYFormatoValidos(String titulo, String nombreInicial, String formatoInicial)
	{
		String nombre = nombreInicial;
		String formato = formatoInicial;
		while (true)
		{
			String[] datos = vista.pedirNombreYFormato(titulo, nombre, formato);
			if (datos == null)
			{
				return null;
			}
			nombre = datos[0].trim();
			formato = datos[1];
			if (nombreValido(nombre, formato))
			{
				return new String[] { nombre, formato };
			}
		}
	}

	private boolean nombreValido(String nombre, String formato)
	{
		if (nombre.isEmpty())
		{
			vista.mostrarError("El nombre de la lista no puede estar vacío.");
			return false;
		}
		for (char c : CARACTERES_NO_VALIDOS.toCharArray())
		{
			if (nombre.indexOf(c) >= 0)
			{
				vista.mostrarError("El nombre no puede contener ninguno de estos caracteres: "
						+ CARACTERES_NO_VALIDOS);
				return false;
			}
		}
		if (modelo.existeFichero(nombre, formato))
		{
			vista.mostrarError("Ya existe el fichero " + modelo.rutaFichero(nombre, formato) + ".");
			return false;
		}
		return true;
	}

	private String siguienteFormato(String formato)
	{
		String[] formatos = Modelo.FORMATOS;
		for (int i = 0; i < formatos.length; i++)
		{
			if (formatos[i].equals(formato))
			{
				return formatos[(i + 1) % formatos.length];
			}
		}
		return Modelo.FORMATO_TXT;
	}
}
