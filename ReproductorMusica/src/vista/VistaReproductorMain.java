package vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.JInternalFrame;
import java.awt.Canvas;

public class VistaReproductorMain {

	private JFrame frmReproductorDeMsica;
	private JTextField textField;
	private JTable tableCanciones;
	private JPanel panelContenedorVistas;
	private CardLayout cardLayoutVistas;
	private JPanel panelCuadriculaCanciones;
	private JLabel lblInfoPista;
	private JToggleButton btnCambiarVista;
	private JToggleButton btnModoFuente;

	// Estado de conexión con ListenBrainz
	private boolean usandoListenBrainz = false;
	private String tokenListenBrainz = "";

	// Tamaños de fuente base (ventana normal) y ampliada (pantalla completa)
	private static final int FUENTE_NORMAL = 12;
	private static final int FUENTE_MAXIMIZADA = 16;
	private boolean esTextoGrande = false;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(() -> {
			try {
				VistaReproductorMain window = new VistaReproductorMain();
				window.frmReproductorDeMsica.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public VistaReproductorMain() {
		initialize();
	}

	private void initialize() {
		frmReproductorDeMsica = new JFrame();
		frmReproductorDeMsica.setIconImage(Toolkit.getDefaultToolkit().getImage(VistaReproductorMain.class.getResource("/imagenes/main.png")));
		frmReproductorDeMsica.setTitle("Reproductor de Música");
		frmReproductorDeMsica.setBounds(new Rectangle(100, 100, 1180, 680));
		frmReproductorDeMsica.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmReproductorDeMsica.getContentPane().setLayout(new BorderLayout(0, 0));

		// --- DETECTOR DE PANTALLA COMPLETA / MAXIMIZADO ---
		frmReproductorDeMsica.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				boolean maximizado = (frmReproductorDeMsica.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH
						|| frmReproductorDeMsica.getWidth() >= 1350;

				if (maximizado != esTextoGrande) {
					esTextoGrande = maximizado;
					int nuevoTamano = esTextoGrande ? FUENTE_MAXIMIZADA : FUENTE_NORMAL;
					actualizarFuentesRecursivo(frmReproductorDeMsica.getContentPane(), nuevoTamano);
					actualizarFuentesRecursivo(frmReproductorDeMsica.getJMenuBar(), nuevoTamano);
					tableCanciones.setRowHeight(esTextoGrande ? 32 : 22);
					tableCanciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, nuevoTamano));
					frmReproductorDeMsica.revalidate();
					frmReproductorDeMsica.repaint();
				}
			}
		});

		// --- BARRA SUPERIOR ---
		JPanel panelSuperior = new JPanel();
		frmReproductorDeMsica.getContentPane().add(panelSuperior, BorderLayout.NORTH);
		panelSuperior.setLayout(new BorderLayout(0, 0));

		JPanel panelNav = new JPanel();
		panelNav.setPreferredSize(new Dimension(220, 42));
		panelSuperior.add(panelNav, BorderLayout.WEST);
		panelNav.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JButton btnAtras = new JButton("");
		btnAtras.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/go-previous.png")));
		panelNav.add(btnAtras);

		JButton btnAdelante = new JButton("");
		btnAdelante.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/go-next.png")));
		panelNav.add(btnAdelante);

		// --- BOTÓN ALTERNAR LOCAL / LISTENBRAINZ ---
		btnModoFuente = new JToggleButton("Fuente: Local");
		btnModoFuente.setToolTipText("Alternar entre biblioteca local y conexión con ListenBrainz");
		btnModoFuente.addActionListener(e -> {
			if (btnModoFuente.isSelected()) {
				// Si aún no hay token configurado, solicitarlo
				if (tokenListenBrainz.isEmpty()) {
					String input = JOptionPane.showInputDialog(
							frmReproductorDeMsica,
							"Ingresa tu User Token de ListenBrainz:",
							"Conectar a ListenBrainz",
							JOptionPane.PLAIN_MESSAGE);

					if (input != null && !input.trim().isEmpty()) {
						tokenListenBrainz = input.trim();
					} else {
						// Canceló: regresar a modo Local
						btnModoFuente.setSelected(false);
						return;
					}
				}
				usandoListenBrainz = true;
				btnModoFuente.setText("Fuente: ListenBrainz");
				btnModoFuente.setForeground(new Color(53, 48, 112)); // Tono púrpura/azul ListenBrainz
				lblInfoPista.setText("Conectado a ListenBrainz");
			} else {
				usandoListenBrainz = false;
				btnModoFuente.setText("Fuente: Local");
				btnModoFuente.setForeground(UIManager.getColor("ToggleButton.foreground"));
				lblInfoPista.setText("Modo: Contenido Local");
			}
		});
		panelNav.add(btnModoFuente);

		// Botones de categoría + Actividad reciente + Selector de vista
		JPanel panelSeleccion = new JPanel();
		panelSuperior.add(panelSeleccion, BorderLayout.CENTER);
		panelSeleccion.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));

		JButton btnReciente = new JButton("Actividad reciente");
		btnReciente.addActionListener(e -> {
			if (usandoListenBrainz) {
				lblInfoPista.setText("ListenBrainz: Listens recientes");
			} else {
				lblInfoPista.setText("Local: Actividad reciente");
			}
		});
		panelSeleccion.add(btnReciente);

		JButton btnAlbum = new JButton("Álbum");
		btnAlbum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelSeleccion.add(btnAlbum);

		JButton btnMusica = new JButton("Biblioteca");
		panelSeleccion.add(btnMusica);

		JButton btnArtistas = new JButton("Playlist");
		panelSeleccion.add(btnArtistas);

		JSeparator sepVista = new JSeparator(SwingConstants.VERTICAL);
		sepVista.setPreferredSize(new Dimension(2, 24));
		panelSeleccion.add(sepVista);

		btnCambiarVista = new JToggleButton("Vista: Cuadrícula");
		btnCambiarVista.addActionListener(e -> {
			if (btnCambiarVista.isSelected()) {
				cardLayoutVistas.show(panelContenedorVistas, "VISTA_IMAGENES");
				btnCambiarVista.setText("Vista: Lista");
			} else {
				cardLayoutVistas.show(panelContenedorVistas, "VISTA_LISTA");
				btnCambiarVista.setText("Vista: Cuadrícula");
			}
		});
		panelSeleccion.add(btnCambiarVista);

		JPanel panelBuscar = new JPanel();
		panelBuscar.setPreferredSize(new Dimension(230, 42));
		panelSuperior.add(panelBuscar, BorderLayout.EAST);
		panelBuscar.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		textField = new JTextField();
		panelBuscar.add(textField);
		textField.setColumns(13);

		JButton btnBuscar = new JButton("Buscar");
		panelBuscar.add(btnBuscar);

		// --- PANEL IZQUIERDO (ÁRBOL) ---
		JScrollPane scrollIzquierdo = new JScrollPane();
		scrollIzquierdo.setPreferredSize(new Dimension(200, 0));
		frmReproductorDeMsica.getContentPane().add(scrollIzquierdo, BorderLayout.WEST);

		JTree treeBiblioteca = new JTree();
		treeBiblioteca.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Canciones") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("Actividad reciente");
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Rock");
					node_1.add(new DefaultMutableTreeNode("blue"));
					node_1.add(new DefaultMutableTreeNode("violet"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Pop");
					node_1.add(new DefaultMutableTreeNode("basketball"));
					node_1.add(new DefaultMutableTreeNode("soccer"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Indie");
					node_1.add(new DefaultMutableTreeNode("hot dogs"));
					node_1.add(new DefaultMutableTreeNode("pizza"));
					add(node_1);
				}
			}
		));
		scrollIzquierdo.setViewportView(treeBiblioteca);

		// --- PANEL CENTRAL CON CARDLAYOUT (2 VISTAS) ---
		cardLayoutVistas = new CardLayout();
		panelContenedorVistas = new JPanel(cardLayoutVistas);
		frmReproductorDeMsica.getContentPane().add(panelContenedorVistas, BorderLayout.CENTER);

		// VISTA 1: LISTA DE CANCIONES (JTable)
		JScrollPane scrollCentralLista = new JScrollPane();
		panelContenedorVistas.add(scrollCentralLista, "VISTA_LISTA");

		tableCanciones = new JTable();
		tableCanciones.setRowHeight(22);
		tableCanciones.setModel(new DefaultTableModel(
			new Object[][] {
				{ "Provisional 1", "1", "Cancion 1", "04:37", "★★★★★", "Yono Fui" },
				{ "Provisional 2", "2", "Cancion 2", "02:38", "★★★★☆", "Papa Caliente" },
				{ "Provisional 3", "3", "Cancion 3", "02:56", "★★★★★", "Pepsi" }
			},
			new String[] {
				"Álbum", "#", "Título", "Duración", "Calificación", "Artista"
			}
		));
		scrollCentralLista.setViewportView(tableCanciones);

		// VISTA 2: CUADRÍCULA DE IMÁGENES / CARÁTULAS
		JScrollPane scrollCentralImagenes = new JScrollPane();
		scrollCentralImagenes.getVerticalScrollBar().setUnitIncrement(16);
		panelContenedorVistas.add(scrollCentralImagenes, "VISTA_IMAGENES");

		panelCuadriculaCanciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
		panelCuadriculaCanciones.setBackground(Color.WHITE);
		scrollCentralImagenes.setViewportView(panelCuadriculaCanciones);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(208, 223, 251));
		panelContenedorVistas.add(panel, "name_195040710545000");
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("**Portada de cancion**");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel, BorderLayout.CENTER);

		// Tarjetas de ejemplo sincronizadas con la tabla
		agregarTarjetaCancion("Cancion 1", "Yono Fui", null);
		agregarTarjetaCancion("Cancion 2", "Papa Caliente", null);
		agregarTarjetaCancion("Cancion 3", "Pepsi", null);

		// --- PANEL DERECHO (COLA DE REPRODUCCIÓN) ---
		JScrollPane scrollDerecho = new JScrollPane();
		scrollDerecho.setPreferredSize(new Dimension(240, 0));
		frmReproductorDeMsica.getContentPane().add(scrollDerecho, BorderLayout.EAST);

		JPanel panelCola = new JPanel();
		scrollDerecho.setViewportView(panelCola);
		panelCola.setLayout(new BorderLayout(0, 0));

		JPanel panelInfoActual = new JPanel();
		panelCola.add(panelInfoActual, BorderLayout.NORTH);
		panelInfoActual.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JLabel lblCaratulaMini = new JLabel("");
		lblCaratulaMini.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/disc-small.png")));
		panelInfoActual.add(lblCaratulaMini);

		lblInfoPista = new JLabel("Sin reproducción");
		panelInfoActual.add(lblInfoPista);

		JList<String> listaCola = new JList<>();
		panelCola.add(listaCola, BorderLayout.CENTER);

		// --- PANEL INFERIOR (CONTROLES Y PROGRESO) ---
		JPanel panelControles = new JPanel();
		panelControles.setPreferredSize(new Dimension(0, 90));
		frmReproductorDeMsica.getContentPane().add(panelControles, BorderLayout.SOUTH);
		panelControles.setLayout(new BorderLayout(0, 0));

		JPanel panelEstadoCancion = new JPanel();
		panelEstadoCancion.setBorder(new EmptyBorder(5, 20, 5, 20));
		panelControles.add(panelEstadoCancion, BorderLayout.NORTH);
		panelEstadoCancion.setLayout(new BorderLayout(10, 0));

		JLabel lblTiempo = new JLabel("00:00");
		lblTiempo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTiempo.setPreferredSize(new Dimension(55, 18));
		panelEstadoCancion.add(lblTiempo, BorderLayout.WEST);

		JSlider sliderProgreso = new JSlider();
		sliderProgreso.setValue(0);
		panelEstadoCancion.add(sliderProgreso, BorderLayout.CENTER);

		JPanel panelIzquierdoControles = new JPanel();
		panelIzquierdoControles.setPreferredSize(new Dimension(230, 0));
		panelControles.add(panelIzquierdoControles, BorderLayout.WEST);

		JPanel panelBotones = new JPanel();
		panelControles.add(panelBotones, BorderLayout.CENTER);
		panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));

		JButton btnRepetir = new JButton("");
		btnRepetir.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/control_repeat_blue.png")));
		panelBotones.add(btnRepetir);

		JButton btnDetener = new JButton("");
		btnDetener.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/Stop.png")));
		panelBotones.add(btnDetener);

		JButton btnAnterior = new JButton("");
		btnAnterior.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/Previous1.png")));
		panelBotones.add(btnAnterior);

		JButton btnPlayPausa = new JButton("Reproducir");
		btnPlayPausa.setIconTextGap(8);
		btnPlayPausa.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/Play.png")));
		panelBotones.add(btnPlayPausa);

		JButton btnSiguiente = new JButton("");
		btnSiguiente.setIcon(new ImageIcon(VistaReproductorMain.class.getResource("/imagenes/Next.png")));
		panelBotones.add(btnSiguiente);

		JButton btnAleatorio = new JButton("Aleatorio");
		panelBotones.add(btnAleatorio);

		JPanel panelSliderVolumen = new JPanel();
		panelSliderVolumen.setPreferredSize(new Dimension(230, 0));
		panelSliderVolumen.setBorder(new EmptyBorder(0, 10, 0, 20));
		panelControles.add(panelSliderVolumen, BorderLayout.EAST);
		panelSliderVolumen.setLayout(new BoxLayout(panelSliderVolumen, BoxLayout.X_AXIS));

		JLabel lblVolumen = new JLabel("Volumen");
		lblVolumen.setBorder(new EmptyBorder(0, 0, 0, 10));
		panelSliderVolumen.add(lblVolumen);

		JSlider sliderVolumen = new JSlider();
		panelSliderVolumen.add(sliderVolumen);

		// --- BARRA DE MENÚ ---
		JMenuBar menuBar = new JMenuBar();
		frmReproductorDeMsica.setJMenuBar(menuBar);

		JMenu menuArchivo = new JMenu("Archivo");
		menuBar.add(menuArchivo);
		menuArchivo.add(new JMenuItem("Abrir archivo"));
		menuArchivo.add(new JMenuItem("Abrir carpeta"));
		menuArchivo.add(new JSeparator());
		menuArchivo.add(new JMenuItem("Nueva lista de reproducción"));
		menuArchivo.add(new JMenuItem("Guardar lista de reproducción"));
		menuArchivo.add(new JSeparator());
		JMenuItem salir = new JMenuItem("Salir");
		salir.addActionListener(e -> System.exit(0));
		menuArchivo.add(salir);

		JMenu menuReproduccion = new JMenu("Reproducción");
		menuBar.add(menuReproduccion);
		menuReproduccion.add(new JMenuItem("Reproducir / Pausar"));
		menuReproduccion.add(new JMenuItem("Detener"));
		menuReproduccion.add(new JMenuItem("Anterior"));
		menuReproduccion.add(new JMenuItem("Siguiente"));
		menuReproduccion.add(new JSeparator());
		menuReproduccion.add(new JCheckBoxMenuItem("Aleatorio"));
		menuReproduccion.add(new JCheckBoxMenuItem("Repetir"));

		JMenu menuBuscar = new JMenu("Buscar");
		menuBar.add(menuBuscar);
		menuBuscar.add(new JMenuItem("Buscar en biblioteca"));
		menuBuscar.add(new JMenuItem("Mostrar/Ocultar cola de reproducción"));

		JMenu ordenarPor = new JMenu("Ordenar por");
		menuBuscar.add(ordenarPor);
		ordenarPor.add(new JMenuItem("Título"));
		ordenarPor.add(new JMenuItem("Artista"));
		ordenarPor.add(new JMenuItem("Álbum"));
		ordenarPor.add(new JMenuItem("Duración"));

		JMenu menuAyuda = new JMenu("Ayuda");
		menuBar.add(menuAyuda);
		menuAyuda.add(new JMenuItem("Acerca de"));
	}

	/**
	 * Crea y agrega una tarjeta visual con carátula para la vista de imágenes.
	 */
	private void agregarTarjetaCancion(String titulo, String artista, ImageIcon caratula) {
		JPanel tarjeta = new JPanel(new BorderLayout(0, 5));
		tarjeta.setPreferredSize(new Dimension(150, 195));
		tarjeta.setBackground(new Color(245, 247, 250));
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(210, 215, 225)),
				new EmptyBorder(8, 8, 8, 8)));

		JLabel lblImagen = new JLabel();
		lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
		lblImagen.setOpaque(true);
		lblImagen.setBackground(new Color(220, 228, 240));
		if (caratula != null) {
			lblImagen.setIcon(caratula);
		} else {
			lblImagen.setText("♪");
			lblImagen.setFont(new Font("Segoe UI", Font.BOLD, 36));
		}
		tarjeta.add(lblImagen, BorderLayout.CENTER);

		JPanel panelTexto = new JPanel();
		panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
		panelTexto.setOpaque(false);

		JLabel lblTitulo = new JLabel(titulo);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, esTextoGrande ? FUENTE_MAXIMIZADA : FUENTE_NORMAL));
		JLabel lblArtista = new JLabel(artista);
		lblArtista.setForeground(Color.DARK_GRAY);
		lblArtista.setFont(new Font("Segoe UI", Font.PLAIN, esTextoGrande ? FUENTE_MAXIMIZADA - 2 : FUENTE_NORMAL - 1));

		panelTexto.add(lblTitulo);
		panelTexto.add(lblArtista);
		tarjeta.add(panelTexto, BorderLayout.SOUTH);

		panelCuadriculaCanciones.add(tarjeta);
	}

	/**
	 * Recorre todos los componentes de un contenedor y ajusta el tamaño de su fuente.
	 */
	private void actualizarFuentesRecursivo(Component comp, int tamano) {
		if (comp == null) return;
		Font fuenteActual = comp.getFont();
		if (fuenteActual != null && fuenteActual.getSize() < 30) {
			comp.setFont(new Font(fuenteActual.getName(), fuenteActual.getStyle(), tamano));
		}
		if (comp instanceof JMenu) {
			JMenu menu = (JMenu) comp;
			for (int i = 0; i < menu.getItemCount(); i++) {
				actualizarFuentesRecursivo(menu.getItem(i), tamano);
			}
		} else if (comp instanceof Container) {
			for (Component hijo : ((Container) comp).getComponents()) {
				actualizarFuentesRecursivo(hijo, tamano);
			}
		}
	}

	public boolean isUsandoListenBrainz() {
		return usandoListenBrainz;
	}

	public String getTokenListenBrainz() {
		return tokenListenBrainz;
	}
}