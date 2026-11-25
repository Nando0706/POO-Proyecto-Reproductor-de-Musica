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
import java.awt.ComponentOrientation;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import javax.swing.JTextArea;

public class MainReproduccionMaximizada extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField textField;
	private JTable tableCanciones;
	private JPanel panelContenedorVistas;
	private CardLayout cardLayoutVistas;
	private JPanel panelCuadriculaCanciones;
	private JLabel lblPortadaGrande;
	private JToggleButton btnCambiarVista;
	private JToggleButton btnModoFuente;

	// Estado de conexión con ListenBrainz
	private boolean usandoListenBrainz = false;
	private String tokenListenBrainz = "";

	// Tamaños de fuente base (ventana normal) y ampliada (pantalla completa)
	private static final int FUENTE_NORMAL = 12;
	private static final int FUENTE_MAXIMIZADA = 16;
	private boolean esTextoGrande = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainReproduccionMaximizada frame = new MainReproduccionMaximizada();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainReproduccionMaximizada() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainReproduccionMaximizada.class.getResource("/imagenes/main.png")));
		setTitle("Reproductor de Música - Vista Maximizada");
		setBounds(new Rectangle(100, 100, 1180, 680));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		// --- DETECTOR DE PANTALLA COMPLETA / MAXIMIZADO ---
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				boolean maximizado = (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH
						|| getWidth() >= 1350;

				if (maximizado != esTextoGrande) {
					esTextoGrande = maximizado;
					int nuevoTamano = esTextoGrande ? FUENTE_MAXIMIZADA : FUENTE_NORMAL;
					actualizarFuentesRecursivo(getContentPane(), nuevoTamano);
					actualizarFuentesRecursivo(getJMenuBar(), nuevoTamano);
					tableCanciones.setRowHeight(esTextoGrande ? 32 : 22);
					tableCanciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, nuevoTamano));
					revalidate();
					repaint();
				}
			}
		});

		// --- BARRA SUPERIOR ---
		JPanel panelSuperior = new JPanel();
		getContentPane().add(panelSuperior, BorderLayout.NORTH);
		panelSuperior.setLayout(new BorderLayout(0, 0));

		JPanel panelNav = new JPanel();
		panelNav.setPreferredSize(new Dimension(220, 42));
		panelSuperior.add(panelNav, BorderLayout.WEST);
		panelNav.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JButton btnAtras = new JButton("");
		btnAtras.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/go-previous.png")));
		panelNav.add(btnAtras);

		JButton btnAdelante = new JButton("");
		btnAdelante.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/go-next.png")));
		panelNav.add(btnAdelante);

		// --- BOTÓN ALTERNAR LOCAL / LISTENBRAINZ ---
		btnModoFuente = new JToggleButton("Fuente: Local");
		btnModoFuente.setToolTipText("Alternar entre biblioteca local y conexión con ListenBrainz");
		btnModoFuente.addActionListener(e -> {
			if (btnModoFuente.isSelected()) {
				if (tokenListenBrainz.isEmpty()) {
					String input = JOptionPane.showInputDialog(
							MainReproduccionMaximizada.this,
							"Ingresa tu User Token de ListenBrainz:",
							"Conectar a ListenBrainz",
							JOptionPane.PLAIN_MESSAGE);

					if (input != null && !input.trim().isEmpty()) {
						tokenListenBrainz = input.trim();
					} else {
						btnModoFuente.setSelected(false);
						return;
					}
				}
				usandoListenBrainz = true;
				btnModoFuente.setText("Fuente: ListenBrainz");
				btnModoFuente.setForeground(new Color(53, 48, 112));
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
		panelSeleccion.add(btnAlbum);

		JButton btnMusica = new JButton("Biblioteca");
		btnMusica.addActionListener(e -> {
			cardLayoutVistas.show(panelContenedorVistas, "VISTA_LISTA");
			btnCambiarVista.setSelected(false);
			btnCambiarVista.setText("Vista: Portada");
		});
		panelSeleccion.add(btnMusica);

		JButton btnArtistas = new JButton("Playlist");
		panelSeleccion.add(btnArtistas);

		JSeparator sepVista = new JSeparator(SwingConstants.VERTICAL);
		sepVista.setPreferredSize(new Dimension(2, 24));
		panelSeleccion.add(sepVista);

		btnCambiarVista = new JToggleButton("Vista: Lista");
		btnCambiarVista.addActionListener(e -> {
			if (btnCambiarVista.isSelected()) {
				cardLayoutVistas.show(panelContenedorVistas, "VISTA_LISTA");
				btnCambiarVista.setText("Vista: Portada");
			} else {
				cardLayoutVistas.show(panelContenedorVistas, "VISTA_PORTADA");
				btnCambiarVista.setText("Vista: Lista");
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
		getContentPane().add(scrollIzquierdo, BorderLayout.WEST);

		JTree treeBiblioteca = new JTree();
		treeBiblioteca.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Canciones") {
				{
					DefaultMutableTreeNode node_1;
					add(new DefaultMutableTreeNode("Actividad reciente"));
					node_1 = new DefaultMutableTreeNode("Rock");
						node_1.add(new DefaultMutableTreeNode(""));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Pop");
						node_1.add(new DefaultMutableTreeNode(""));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Indie");
						node_1.add(new DefaultMutableTreeNode(""));
					add(node_1);
				}
			}
		));
		scrollIzquierdo.setViewportView(treeBiblioteca);

		// --- PANEL CENTRAL CON CARDLAYOUT ---
		cardLayoutVistas = new CardLayout();
		panelContenedorVistas = new JPanel(cardLayoutVistas);
		getContentPane().add(panelContenedorVistas, BorderLayout.CENTER);

		// VISTA PRINCIPAL EN ESTA CLASE: PORTADA DE CANCIÓN MAXIMIZADA
		JPanel panelPortada = new JPanel();
		panelPortada.setBackground(new Color(208, 223, 251));
		panelPortada.setLayout(new BorderLayout(0, 0));
		panelContenedorVistas.add(panelPortada, "VISTA_PORTADA");

		lblPortadaGrande = new JLabel("**Portada de canción**");
		lblPortadaGrande.setFont(new Font("Tahoma", Font.BOLD, 22));
		lblPortadaGrande.setHorizontalAlignment(SwingConstants.CENTER);
		panelPortada.add(lblPortadaGrande, BorderLayout.CENTER);

		// VISTA 2: LISTA DE CANCIONES (JTable)
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

		// VISTA 3: CUADRÍCULA DE IMÁGENES / CARÁTULAS
		JScrollPane scrollCentralImagenes = new JScrollPane();
		scrollCentralImagenes.getVerticalScrollBar().setUnitIncrement(16);
		panelContenedorVistas.add(scrollCentralImagenes, "VISTA_IMAGENES");

		panelCuadriculaCanciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
		panelCuadriculaCanciones.setBackground(Color.WHITE);
		scrollCentralImagenes.setViewportView(panelCuadriculaCanciones);

		agregarTarjetaCancion("Cancion 1", "Yono Fui", null);
		agregarTarjetaCancion("Cancion 2", "Papa Caliente", null);
		agregarTarjetaCancion("Cancion 3", "Pepsi", null);

		// --- PANEL DERECHO (COLA DE REPRODUCCIÓN) ---
		JScrollPane scrollDerecho = new JScrollPane();
		scrollDerecho.setPreferredSize(new Dimension(240, 0));
		getContentPane().add(scrollDerecho, BorderLayout.EAST);
		
		JPanel panelDerecho = new JPanel();
		scrollDerecho.setViewportView(panelDerecho);
		panelDerecho.setLayout(new BorderLayout(0, 0));
		
		JPanel panelReproduccionActual = new JPanel();
		panelReproduccionActual.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panelDerecho.add(panelReproduccionActual, BorderLayout.NORTH);
		panelReproduccionActual.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("Se esta reproduciendo \"Canción 1\"");
		lblNewLabel_1.setPreferredSize(new Dimension(166, 28));
		lblNewLabel_1.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/disc-small.png")));
		panelReproduccionActual.add(lblNewLabel_1, BorderLayout.NORTH);
		
		JPanel panelDerechoCentro = new JPanel();
		panelDerecho.add(panelDerechoCentro, BorderLayout.CENTER);
		panelDerechoCentro.setLayout(new BorderLayout(0, 0));
		
		JPanel tituloLirica = new JPanel();
		panelDerechoCentro.add(tituloLirica, BorderLayout.NORTH);
		tituloLirica.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		JLabel liricasTitulo = new JLabel("Letra");
		liricasTitulo.setPreferredSize(new Dimension(166, 20));
		liricasTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		tituloLirica.add(liricasTitulo);
		
		JPanel letra = new JPanel();
		letra.setPreferredSize(new Dimension(0, 50));
		panelDerechoCentro.add(letra);
		
		JTextArea txtrloremIpsumDolor = new JTextArea();
		txtrloremIpsumDolor.setPreferredSize(new Dimension(200, 22));
		txtrloremIpsumDolor.setWrapStyleWord(true);
		txtrloremIpsumDolor.setTabSize(100);
		txtrloremIpsumDolor.setLineWrap(true);
		txtrloremIpsumDolor.setRows(30);
		txtrloremIpsumDolor.setText("\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"");
		letra.add(txtrloremIpsumDolor);
		
		JPanel panelDerechoSur = new JPanel();
		panelDerechoSur.setPreferredSize(new Dimension(0, 180));
		panelDerecho.add(panelDerechoSur, BorderLayout.SOUTH);
		panelDerechoSur.setLayout(new BoxLayout(panelDerechoSur, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setBorder(new MatteBorder(1, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel.setPreferredSize(new Dimension(10, 9));
		panelDerechoSur.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel_1_1_2_1 = new JLabel("Cola de reproducción");
		lblNewLabel_1_1_2_1.setBorder(new EmptyBorder(0, 0, 0, 0));
		lblNewLabel_1_1_2_1.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/iPod U2 (1).png")));
		lblNewLabel_1_1_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1_2_1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblNewLabel_1_1_2_1.setPreferredSize(new Dimension(166, 16));
		panel.add(lblNewLabel_1_1_2_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new MatteBorder(1, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_1.setPreferredSize(new Dimension(10, 15));
		panelDerechoSur.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Canción");
		lblNewLabel_1_1_1.setPreferredSize(new Dimension(166, 20));
		lblNewLabel_1_1_1.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/disc-small.png")));
		panel_1.add(lblNewLabel_1_1_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new MatteBorder(1, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_2.setPreferredSize(new Dimension(10, 15));
		panelDerechoSur.add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel_1_1_1_1 = new JLabel("Canción");
		lblNewLabel_1_1_1_1.setPreferredSize(new Dimension(166, 20));
		lblNewLabel_1_1_1_1.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/disc-small.png")));
		panel_2.add(lblNewLabel_1_1_1_1);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new MatteBorder(1, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_3.setPreferredSize(new Dimension(10, 15));
		panelDerechoSur.add(panel_3);
		
		JLabel lblNewLabel_1_1_1_2 = new JLabel("Canción");
		lblNewLabel_1_1_1_2.setPreferredSize(new Dimension(166, 20));
		lblNewLabel_1_1_1_2.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/disc-small.png")));
		panel_3.add(lblNewLabel_1_1_1_2);
		
		JPanel panel_6 = new JPanel();
		panel_6.setPreferredSize(new Dimension(10, 15));
		panelDerechoSur.add(panel_6);
		
		JLabel lblNewLabel_1_1_2 = new JLabel("Canción");
		lblNewLabel_1_1_2.setPreferredSize(new Dimension(166, 20));
		lblNewLabel_1_1_2.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/disc-small.png")));
		panel_6.add(lblNewLabel_1_1_2);
	

		// --- PANEL INFERIOR (CONTROLES Y PROGRESO) ---
		JPanel panelControles = new JPanel();
		panelControles.setPreferredSize(new Dimension(0, 90));
		getContentPane().add(panelControles, BorderLayout.SOUTH);
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
		btnRepetir.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/control_repeat_blue.png")));
		panelBotones.add(btnRepetir);

		JButton btnDetener = new JButton("");
		btnDetener.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/Stop.png")));
		panelBotones.add(btnDetener);

		JButton btnAnterior = new JButton("");
		btnAnterior.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/Previous1.png")));
		panelBotones.add(btnAnterior);

		JButton btnPlayPausa = new JButton("Reproducir");
		btnPlayPausa.setIconTextGap(8);
		btnPlayPausa.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/Play.png")));
		panelBotones.add(btnPlayPausa);

		JButton btnSiguiente = new JButton("");
		btnSiguiente.setIcon(new ImageIcon(MainReproduccionMaximizada.class.getResource("/imagenes/Next.png")));
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
		setJMenuBar(menuBar);

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
		if (fuenteActual != null && fuenteActual.getSize() < 21) { // Protege textos grandes como la portada
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

	public JLabel getLblPortadaGrande() {
		return lblPortadaGrande;
	}
}