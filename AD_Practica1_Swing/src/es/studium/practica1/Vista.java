package es.studium.practica1;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import es.studium.practica1.Modelo.ArticuloAComprar;
import es.studium.practica1.Modelo.ListaDeLaCompra;

public class Vista extends JFrame
{
	private static final long serialVersionUID = 1L;

	private static final String PANTALLA_BIENVENIDA = "bienvenida";
	private static final String PANTALLA_PRINCIPAL = "principal";
	private static final String TITULO = "Listas de la compra";

	public enum Tema
	{
		CLARO("Claro", new Color(46, 134, 171), new Color(245, 248, 250), Color.WHITE,
				new Color(33, 33, 33), Color.DARK_GRAY),
		OSCURO("Oscuro", new Color(86, 156, 214), new Color(43, 43, 43), new Color(60, 63, 65),
				new Color(220, 220, 220), new Color(170, 170, 170)),
		VERDE("Verde", new Color(46, 139, 87), new Color(240, 248, 242), Color.WHITE,
				new Color(33, 33, 33), new Color(85, 85, 85));

		private final String nombre;
		private final Color primario;
		private final Color fondo;
		private final Color superficie;
		private final Color texto;
		private final Color textoSecundario;

		Tema(String nombre, Color primario, Color fondo, Color superficie, Color texto, Color textoSecundario)
		{
			this.nombre = nombre;
			this.primario = primario;
			this.fondo = fondo;
			this.superficie = superficie;
			this.texto = texto;
			this.textoSecundario = textoSecundario;
		}

		public String getNombre()
		{
			return nombre;
		}
	}

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel contenedor = new JPanel(cardLayout);

	private final JRadioButtonMenuItem[] itemsTema = new JRadioButtonMenuItem[Tema.values().length];
	private Tema temaActual = Tema.CLARO;

	private final JLabel lblTitulo = new JLabel(TITULO);
	private final JLabel lblSubtitulo = new JLabel("Acceso a datos · Tema 1: Persistencia en ficheros");
	private final TitledBorder bordeListas = BorderFactory.createTitledBorder("Mis listas");
	private final TitledBorder bordeArticulos = BorderFactory.createTitledBorder("Artículos");

	private final JButton btnAcceder = new JButton("Acceder");
	private final JButton btnSalir = new JButton("Salir");

	private final DefaultListModel<String> modeloListas = new DefaultListModel<String>();
	private final JList<String> listaListas = new JList<String>(modeloListas);
	private final JButton btnNuevaLista = new JButton("Nueva lista");
	private final JButton btnRenombrar = new JButton("Renombrar");
	private final JButton btnBorrarLista = new JButton("Borrar lista");
	private final JButton btnGuardarComo = new JButton("Guardar como...");

	private final JLabel lblNombreLista = new JLabel();
	private final DefaultTableModel modeloTabla = new DefaultTableModel(
			new Object[] { "Descripción", "Cantidad", "Unidad" }, 0)
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int fila, int columna)
		{
			return false;
		}
	};
	private final JTable tablaArticulos = new JTable(modeloTabla);
	private final DefaultTableCellRenderer rendererCabecera = new DefaultTableCellRenderer()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
				boolean conFoco, int fila, int columna)
		{
			super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
			setFont(tabla.getFont().deriveFont(Font.BOLD));
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 1, tabla.getGridColor()),
					BorderFactory.createEmptyBorder(4, 4, 4, 4)));
			return this;
		}
	};

	private final JTextField txtDescripcion = new JTextField(22);
	private final JTextField txtCantidad = new JTextField(6);
	private final JComboBox<String> cmbUnidad = new JComboBox<String>(new String[] { "U", "Kg", "g", "L", "ml" });
	private final JButton btnAnadir = new JButton("Añadir artículo");
	private final JButton btnEliminarArticulo = new JButton("Eliminar artículo seleccionado");

	private final JLabel lblEstado = new JLabel(" ");
	private final JButton btnVolver = new JButton("Volver");

	public Vista()
	{
		super(TITULO);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		contenedor.add(crearPantallaBienvenida(), PANTALLA_BIENVENIDA);
		contenedor.add(crearPantallaPrincipal(), PANTALLA_PRINCIPAL);
		setContentPane(contenedor);
		setJMenuBar(crearMenu());
		aplicarTema(Tema.CLARO);

		setSize(900, 600);
		setMinimumSize(new Dimension(760, 500));
		setLocationRelativeTo(null);
		mostrarBienvenida();
	}

	private JMenuBar crearMenu()
	{
		JMenuBar barra = new JMenuBar();
		JMenu menuTema = new JMenu("Tema");
		ButtonGroup grupo = new ButtonGroup();
		Tema[] temas = Tema.values();
		for (int i = 0; i < temas.length; i++)
		{
			itemsTema[i] = new JRadioButtonMenuItem(temas[i].getNombre());
			itemsTema[i].setActionCommand(temas[i].name());
			grupo.add(itemsTema[i]);
			menuTema.add(itemsTema[i]);
		}
		barra.add(menuTema);
		return barra;
	}

	private JPanel crearPantallaBienvenida()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		JPanel caja = new JPanel();
		caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
		caja.setOpaque(false);

		lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 36));
		lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel lblDescripcion = new JLabel("<html><div style='text-align:center;width:420px'>"
				+ "Crea listas de la compra para cada tienda, añade o elimina artículos "
				+ "y todo se guardará automáticamente dentro de la carpeta <b>data</b> "
				+ "en el formato que elijas: texto (<b>.txt</b>), binario (<b>.dat</b>) "
				+ "o <b>JSON</b> (<b>.json</b>).</div></html>");
		lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
		lblDescripcion.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		botones.setOpaque(false);
		estilizarBoton(btnAcceder, true);
		estilizarBoton(btnSalir, false);
		botones.add(btnAcceder);
		botones.add(btnSalir);

		for (Component c : new Component[] { lblTitulo, lblSubtitulo, lblDescripcion, botones })
		{
			((javax.swing.JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
		}

		caja.add(lblTitulo);
		caja.add(Box.createVerticalStrut(8));
		caja.add(lblSubtitulo);
		caja.add(Box.createVerticalStrut(25));
		caja.add(lblDescripcion);
		caja.add(Box.createVerticalStrut(35));
		caja.add(botones);

		panel.add(caja);
		return panel;
	}

	private JPanel crearPantallaPrincipal()
	{
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel panelListas = new JPanel(new BorderLayout(5, 5));
		panelListas.setBorder(bordeListas);
		listaListas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaListas.setFont(new Font("SansSerif", Font.PLAIN, 14));
		JScrollPane scrollListas = new JScrollPane(listaListas);
		scrollListas.setPreferredSize(new Dimension(190, 0));
		panelListas.add(scrollListas, BorderLayout.CENTER);

		JPanel botonesListas = new JPanel(new GridLayout(4, 1, 5, 5));
		botonesListas.add(btnNuevaLista);
		botonesListas.add(btnGuardarComo);
		botonesListas.add(btnRenombrar);
		botonesListas.add(btnBorrarLista);
		panelListas.add(botonesListas, BorderLayout.SOUTH);

		JPanel panelArticulos = new JPanel(new BorderLayout(5, 5));
		lblNombreLista.setFont(new Font("SansSerif", Font.BOLD, 20));
		panelArticulos.add(lblNombreLista, BorderLayout.NORTH);

		tablaArticulos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablaArticulos.getTableHeader().setReorderingAllowed(false);
		tablaArticulos.getTableHeader().setDefaultRenderer(rendererCabecera);
		tablaArticulos.setRowHeight(24);
		tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(320);
		panelArticulos.add(new JScrollPane(tablaArticulos), BorderLayout.CENTER);
		panelArticulos.add(crearFormularioArticulo(), BorderLayout.SOUTH);

		JPanel barraEstado = new JPanel(new BorderLayout());
		barraEstado.add(lblEstado, BorderLayout.CENTER);
		barraEstado.add(btnVolver, BorderLayout.EAST);

		panel.add(panelListas, BorderLayout.WEST);
		panel.add(panelArticulos, BorderLayout.CENTER);
		panel.add(barraEstado, BorderLayout.SOUTH);
		return panel;
	}

	private JPanel crearFormularioArticulo()
	{
		JPanel formulario = new JPanel(new GridBagLayout());
		formulario.setBorder(bordeArticulos);
		cmbUnidad.setEditable(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridy = 0;
		gbc.gridx = 0;
		formulario.add(new JLabel("Descripción:"), gbc);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		formulario.add(txtDescripcion, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		formulario.add(new JLabel("Cantidad:"), gbc);
		gbc.gridx = 3;
		formulario.add(txtCantidad, gbc);
		gbc.gridx = 4;
		formulario.add(new JLabel("Unidad:"), gbc);
		gbc.gridx = 5;
		formulario.add(cmbUnidad, gbc);

		JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		estilizarBoton(btnAnadir, true);
		botones.add(btnAnadir);
		botones.add(btnEliminarArticulo);
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 6;
		gbc.anchor = GridBagConstraints.EAST;
		formulario.add(botones, gbc);
		return formulario;
	}

	private void estilizarBoton(JButton boton, boolean principal)
	{
		boton.setFont(new Font("SansSerif", Font.BOLD, 14));
		boton.setFocusPainted(false);
		if (principal)
		{
			boton.setContentAreaFilled(false);
			boton.setOpaque(true);
			boton.setForeground(Color.WHITE);
			boton.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
		}
		boton.setPreferredSize(new Dimension(Math.max(130, boton.getPreferredSize().width + 20), 36));
	}

	public void aplicarTema(Tema tema)
	{
		temaActual = tema;
		UIManager.put("Panel.background", tema.fondo);
		UIManager.put("OptionPane.background", tema.fondo);
		UIManager.put("OptionPane.messageForeground", tema.texto);
		UIManager.put("Label.foreground", tema.texto);

		pintar(contenedor, tema);

		lblTitulo.setForeground(tema.primario);
		lblNombreLista.setForeground(tema.primario);
		lblSubtitulo.setForeground(tema.textoSecundario);
		lblEstado.setForeground(tema.textoSecundario);
		btnAcceder.setBackground(tema.primario);
		btnAnadir.setBackground(tema.primario);
		bordeListas.setTitleColor(tema.texto);
		bordeArticulos.setTitleColor(tema.texto);

		itemsTema[tema.ordinal()].setSelected(true);
		repaint();
	}

	public Tema getTemaActual()
	{
		return temaActual;
	}

	private void pintar(Component componente, Tema tema)
	{
		if (componente instanceof JPanel)
		{
			componente.setBackground(tema.fondo);
		}
		else if (componente instanceof JLabel)
		{
			componente.setForeground(tema.texto);
		}
		else if (componente instanceof JScrollPane)
		{
			componente.setBackground(tema.fondo);
			((JScrollPane) componente).getViewport().setBackground(tema.superficie);
		}
		else if (componente instanceof JList)
		{
			JList<?> lista = (JList<?>) componente;
			lista.setBackground(tema.superficie);
			lista.setForeground(tema.texto);
			lista.setSelectionBackground(tema.primario);
			lista.setSelectionForeground(Color.WHITE);
		}
		else if (componente instanceof JTable)
		{
			JTable tabla = (JTable) componente;
			tabla.setBackground(tema.superficie);
			tabla.setForeground(tema.texto);
			tabla.setSelectionBackground(tema.primario);
			tabla.setSelectionForeground(Color.WHITE);
			tabla.setGridColor(tema.fondo);
		}
		else if (componente instanceof JTableHeader)
		{
			componente.setBackground(tema.fondo);
			rendererCabecera.setBackground(tema.fondo);
			rendererCabecera.setForeground(tema.texto);
		}
		else if (componente instanceof JComboBox)
		{
			componente.setBackground(tema.superficie);
			componente.setForeground(tema.texto);
			Component editor = ((JComboBox<?>) componente).getEditor().getEditorComponent();
			if (editor instanceof JComponent)
			{
				((JComponent) editor).setOpaque(true);
			}
		}
		else if (componente instanceof JTextField)
		{
			JTextField campo = (JTextField) componente;
			campo.setBackground(tema.superficie);
			campo.setForeground(tema.texto);
			campo.setCaretColor(tema.texto);
		}

		if (componente instanceof Container)
		{
			for (Component hijo : ((Container) componente).getComponents())
			{
				pintar(hijo, tema);
			}
		}
	}

	public void mostrarBienvenida()
	{
		cardLayout.show(contenedor, PANTALLA_BIENVENIDA);
		getRootPane().setDefaultButton(btnAcceder);
	}

	public void mostrarPrincipal()
	{
		cardLayout.show(contenedor, PANTALLA_PRINCIPAL);
		getRootPane().setDefaultButton(null);
	}

	public void cargarNombresListas(List<String> nombres)
	{
		modeloListas.clear();
		for (String nombre : nombres)
		{
			modeloListas.addElement(nombre);
		}
	}

	public void seleccionarLista(String nombre)
	{
		listaListas.setSelectedValue(nombre, true);
	}

	public void mostrarLista(ListaDeLaCompra lista, String formato)
	{
		modeloTabla.setRowCount(0);
		boolean hayLista = lista != null;
		if (hayLista)
		{
			lblNombreLista.setText("Lista: " + lista.getNombreLista()
					+ "  ·  formato " + formato
					+ "  ·  " + lista.getArticulos().size() + " artículos");
			for (ArticuloAComprar articulo : lista.getArticulos())
			{
				modeloTabla.addRow(new Object[] { articulo.getDescripcion(),
						formatearCantidad(articulo.getCantidad()), articulo.getUnidad() });
			}
		}
		else
		{
			lblNombreLista.setText("Selecciona o crea una lista");
		}

		btnRenombrar.setEnabled(hayLista);
		btnBorrarLista.setEnabled(hayLista);
		btnGuardarComo.setEnabled(hayLista);
		btnAnadir.setEnabled(hayLista);
		btnEliminarArticulo.setEnabled(hayLista);
		txtDescripcion.setEnabled(hayLista);
		txtCantidad.setEnabled(hayLista);
		cmbUnidad.setEnabled(hayLista);
	}

	private String formatearCantidad(double cantidad)
	{
		if (cantidad == Math.floor(cantidad))
		{
			return String.valueOf((long) cantidad);
		}
		return String.valueOf(cantidad);
	}

	public void limpiarFormulario()
	{
		txtDescripcion.setText("");
		txtCantidad.setText("");
		cmbUnidad.setSelectedIndex(0);
		txtDescripcion.requestFocusInWindow();
	}

	public void setEstado(String mensaje)
	{
		lblEstado.setText(mensaje);
	}

	public String getListaSeleccionada()
	{
		return listaListas.getSelectedValue();
	}

	public int getFilaSeleccionada()
	{
		return tablaArticulos.getSelectedRow();
	}

	public String getDescripcionFila(int fila)
	{
		return (String) modeloTabla.getValueAt(fila, 0);
	}

	public String getDescripcion()
	{
		return txtDescripcion.getText();
	}

	public String getCantidadTexto()
	{
		return txtCantidad.getText();
	}

	public String getUnidad()
	{
		Object unidad = cmbUnidad.getEditor().getItem();
		return unidad == null ? "" : unidad.toString();
	}

	public void mostrarError(String mensaje)
	{
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void mostrarInfo(String mensaje)
	{
		JOptionPane.showMessageDialog(this, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
	}

	public String pedirTexto(String mensaje, String valorInicial)
	{
		Object respuesta = JOptionPane.showInputDialog(this, mensaje, TITULO,
				JOptionPane.QUESTION_MESSAGE, null, null, valorInicial);
		return respuesta == null ? null : respuesta.toString();
	}

	public String[] pedirNombreYFormato(String titulo, String nombreInicial, String formatoInicial)
	{
		JTextField txtNombre = new JTextField(nombreInicial, 20);
		JComboBox<String> cmbFormato = new JComboBox<String>(Modelo.FORMATOS);
		cmbFormato.setSelectedItem(formatoInicial);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Nombre de la lista:"), gbc);
		gbc.gridx = 1;
		panel.add(txtNombre, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Formato del fichero:"), gbc);
		gbc.gridx = 1;
		panel.add(cmbFormato, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		JLabel ayuda = new JLabel("<html><small>.txt = texto · .dat = binario · .json = JSON</small></html>");
		ayuda.setForeground(Color.GRAY);
		panel.add(ayuda, gbc);

		int opcion = JOptionPane.showConfirmDialog(this, panel, titulo,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (opcion != JOptionPane.OK_OPTION)
		{
			return null;
		}
		return new String[] { txtNombre.getText(), (String) cmbFormato.getSelectedItem() };
	}

	public boolean confirmar(String mensaje)
	{
		return JOptionPane.showConfirmDialog(this, mensaje, TITULO,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}

	public JRadioButtonMenuItem[] getItemsTema()
	{
		return itemsTema;
	}

	public JButton getBtnAcceder()
	{
		return btnAcceder;
	}

	public JButton getBtnSalir()
	{
		return btnSalir;
	}

	public JButton getBtnNuevaLista()
	{
		return btnNuevaLista;
	}

	public JButton getBtnRenombrar()
	{
		return btnRenombrar;
	}

	public JButton getBtnBorrarLista()
	{
		return btnBorrarLista;
	}

	public JButton getBtnGuardarComo()
	{
		return btnGuardarComo;
	}

	public JButton getBtnAnadir()
	{
		return btnAnadir;
	}

	public JButton getBtnEliminarArticulo()
	{
		return btnEliminarArticulo;
	}

	public JButton getBtnVolver()
	{
		return btnVolver;
	}

	public JList<String> getListaListas()
	{
		return listaListas;
	}

	public JTextField getTxtDescripcion()
	{
		return txtDescripcion;
	}

	public JTextField getTxtCantidad()
	{
		return txtCantidad;
	}
}
