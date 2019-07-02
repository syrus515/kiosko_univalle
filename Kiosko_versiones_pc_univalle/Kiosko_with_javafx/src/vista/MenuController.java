package vista;

import BD.Medicion;
import BD.Pacientes;
import BD.PacientesPK;
import BD.Antecedentesfamiliares;
import BD.AntecedentesfamiliaresPK;
import BD.Antecedentespersonales;
import BD.AntecedentespersonalesPK;
import BD.HistorialAfinamiento;
import BD.MedicionPersonalizada;

import smartcard.SmartCard;


import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import cliente.AdminDevice;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.EmptyStackException;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javax.persistence.Query;
import javax.swing.BorderFactory;


public class MenuController implements Initializable {

   
    //Banderas
    private static boolean almacenarMedicion= false;
    
    
    // Datos personales.
    @FXML
    private ComboBox cboxTipoIdentificacion;
    @FXML
    private TextField textIdentificacion1;
    @FXML
    private TextField textTipoIdentificacion2;
    @FXML
    private TextField textIdentificacion2;
    @FXML
    private TextField textAdministradora;
    @FXML
    private TextField textNombre1;
    @FXML
    private TextField textNombre2;
    @FXML
    private TextField textApellido1;
    @FXML
    private TextField textApellido2;
    @FXML
    private TextField textTelefonoFijo;
    @FXML
    private TextField textCelular;
    @FXML
    private TextField textDireccion;
    @FXML
    private ChoiceBox<String> textTipoUsuario;
    @FXML
    private ChoiceBox<String> textGenero;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private TextField textDepartamento;
    @FXML
    private TextField textMunicipio;
    @FXML
    private ChoiceBox<String> textZona;
    // Antecedentes personales.
    @FXML
    private TextField textMedicamentosPermanentes1;
    @FXML
    private TextField textMedicamentosPermanentes2;
    @FXML
    private TextField textMedicamentosPermanentes3;
    @FXML
    private TextField textMedicamentosPermanentes4;
    @FXML
    private TextField textMedicamentosPermanentes5;
    @FXML
    private TextField textOtrasSustancias1;
    @FXML
    private TextField textOtrasSustancias2;
    @FXML
    private TextField textOtrasSustancias3;
    @FXML
    private TextField textOtrasSustancias4;
    @FXML
    private TextField textOtrasSustancias5;
    @FXML
    private ChoiceBox<String> textDiabetes;    
    @FXML
    private ChoiceBox<String> textHipertension;
    @FXML
    private Spinner<Integer> textInfartos;
    @FXML
    private Spinner<Integer> textFumaDias;
    @FXML
    private ChoiceBox<String> textConviveConFumadores;
    @FXML
    private ChoiceBox<String> textActividadFisicaMinutos;
    @FXML
    private Spinner<Integer> textCosumeLicor;
    // Antecedentes familiares.
    @FXML
    private Spinner<Integer> textAFDiabetes;
    @FXML
    private Spinner<Integer> textAFHipertension;
    @FXML
    private Spinner<Integer> textAFInfartos;    
    
    @FXML
    private Text ecgTextField;
    @FXML
    private Text spo2OxiTextField;
    @FXML
    private Text spo2HrTextField;
    @FXML
    private Text respTextField;


    @FXML
    private Button btnIniciarSenales;
    @FXML
    private Canvas pintarKiosko;
    
    // Imagenes de la foto y huella dactilar.
    @FXML
    private Label etiquetaFoto;
    @FXML
    private Label etiquetaHuella;

    // Botones de la ventana
    @FXML
    private Button btonBuscar;
    @FXML
    private Button btonGuardar;
    @FXML
    private Button btonModificar;
    @FXML
    private Button btonNuevo;
    @FXML
    private Button btonCancelar;    
    
    // Listas desplegables de valores para las mediciones    
    @FXML ChoiceBox<String> intervalo;
    
    @FXML ChoiceBox<String> duracionMuestra;
    
    @FXML ChoiceBox<String> duracionExamen;

    // Otros atributos necesarios para la interfaz.
    Kiosko programaPrincipal;
    private DPFPTemplate plantillaHuella = null;
    private ImageView fotoTomada = null;
    private ImageView fotoHuella = null;
//    private boolean estadoGuardar = true; //ture para nuevo y false para modificar
    boolean openFoto = true, openHuella = true; //Verifica si las ventanas ya fueron abiertas.
    String usuario = null, nombre = null, rol = null; // Datos del usario del sistema quien lo opera.
    String txtArea = "";
    public GraphicsContext gc;
    public double anchoGC;
    public double altoGC;
    public int ptoInicial=20;
    EntityManager em;
    private boolean opcionNuevo;
    Alert popup;
    AdminDevice admin;
    private int banderaImg = 1;
    private boolean banderaInicio=false;
    private Pacientes pacienteCargado;
    
    //Alterador de la interfaz (usaro por el afinamiento)
    AlterarInterfaz alterador;
    
    //vectores para guardar datos de la medicion
    Vector<Integer> vSPO2 = new Vector(0,1);
    Vector<Integer> vECG1 = new Vector(0,1);
    Vector<Integer> vECG2 = new Vector(0,1);
    Vector<Integer> vRESP = new Vector(0,1);
    Vector<Integer> vsistolica = new Vector(0,1);
    Vector<Integer> vdiastolica = new Vector(0,1);
    Vector<Integer> vpulso = new Vector(0,1);
    Vector<Integer> vmed = new Vector(0,1);
    Vector<Integer> vECG = new Vector(0,1);
    Vector<Integer> vSPO2text = new Vector(0,1);
    Vector<Integer> vHR = new Vector(0,1);
    Vector<Integer> vRESPtext = new Vector(0,1);
    
    Vector<Integer> SubmuestreoSistolica = new Vector(0,1);
    Vector<Integer> SubmuestreoDiastolica = new Vector(0,1);
    Vector<Integer> SubmuestreoPulso = new Vector(0,1);
    Vector<Integer> SubmuestreoMed = new Vector(0,1);
    Vector<Integer> SubmuestreoECG = new Vector(0,1);
    Vector<Integer> SubmuestreoSPO2text = new Vector(0,1);
    Vector<Integer> SubmuestreoHR = new Vector(0,1);
    Vector<Integer> SubmuestreoRESPtext = new Vector(0,1);    
    Vector<Integer> Submuestreominutos = new Vector(0,1);

    
    
    private static final int MAX_DATA_POINTS_SPO2 = 500;
    private static final int MAX_DATA_POINTS_ECG = 500;
    private static final int MAX_DATA_POINTS_RESP = 500;
    private static final int Y_MAX_SPO2 = 256;
    private static final int Y_MAX_ECG = 3000;
    private static final int Y_MAX_RESP = 500;
    
    private static boolean pausar= false;

    private XYChart.Series series;
    private XYChart.Series<Number, Number> series1;
    private XYChart.Series<Number, Number> series2;
    private XYChart.Series<Number, Number> series3;
    private XYChart.Series<Number, Number> series4;
    private int xSeriesData_spo2 = 0;
    private int xSeriesData_ecg1 = 0;
    private int xSeriesData_ecg2 = 0;
    private int xSeriesData_resp = 0;
    private ConcurrentLinkedQueue<Number> dataECG1 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> dataECG2 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> dataSPO2 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> dataRESP = new ConcurrentLinkedQueue<Number>();
    
    private ConcurrentLinkedQueue<Number> reprodSPO2 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> reprodECG1 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> reprodECG2 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> reprodRESP = new ConcurrentLinkedQueue<Number>();
    
    private static ExecutorService executor= Executors.newFixedThreadPool(2);;
    private ExecutorService executorPresion;
    private AddToQueue addToQueue;
    private QueueParametros queueParam;
    private Timeline timeline2;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis xAxis_2;
    @FXML
    private NumberAxis xAxis_3;
    @FXML
    private NumberAxis xAxis_4;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private NumberAxis yAxis_2;
    @FXML
    private NumberAxis yAxis_3;
    @FXML
    private NumberAxis yAxis_4;
    
    @FXML
    LineChart<Number, Number> lc1;
    @FXML
    LineChart<Number, Number> lc2;
    @FXML
    LineChart<Number, Number> lc3;
    @FXML
    LineChart<Number, Number> lc4;
    @FXML
    private MenuItem mnuCerrarPrograma;
    @FXML
    private Label labIdentificacion1;
    @FXML
    private Label labNombre1;
    @FXML
    private Label labApellido1;
    @FXML
    private Label labAdministradora;
    @FXML
    private Label labNombre2;
    @FXML
    private Label labApellido2;
    @FXML
    private Label labTipoIdentificacion1;
    @FXML
    private Accordion siguiente;
    @FXML
    private Label labMedicamentosPermanentes1;
    @FXML
    private Label labMedicamentosPermanentes2;
    @FXML
    private Label labMedicamentosPermanentes3;
    @FXML
    private Label labMedicamentosPermanentes4;
    @FXML
    private Label labMedicamentosPermanentes5;
    @FXML
    private Label labOtrasSustancias1;
    @FXML
    private Label labOtrasSustancias2;
    @FXML
    private Label labOtrasSustancias3;
    @FXML
    private Label labOtrasSustancias4;
    @FXML
    private Label labOtrasSustancias5;
    @FXML
    private Label labActividadFisicaMinutos;
    @FXML
    private Label labSumaDias;
    @FXML
    private Label labConviveConFumadores;
    @FXML
    private Label labDiabetes;
    @FXML
    private Label labHipertension;
    @FXML
    private Label labConsumeLicor;
    @FXML
    private Label labInfartos;
    @FXML
    private Label labAFDiabetes;
    @FXML
    private Label labAFHipertension;
    @FXML
    private Label labAFInfartos;
    @FXML
    private Label labAFAC;
    @FXML
    private Label labTipoIdentificacion2;
    @FXML
    private Label labTelefonoFijo;
    @FXML
    private Label labTipoUsuario;
    @FXML
    private Label labDepartamento;
    @FXML
    private Label labIdentificacion2;
    @FXML
    private Label labCelular;
    @FXML
    private Label labGenero;
    @FXML
    private Label labMunicipio;
    @FXML
    private Label labDireccion;
    @FXML
    private Label labFechaNacimiento;
    @FXML
    private Label labZona;
    @FXML
    private Button botonReproducir;
    @FXML
    private TextArea detallesMedicion;
    @FXML
    private Button tomarPeso;
    @FXML
    private Button tomarPresion;
    @FXML
    private Text pesoImprimir;
    @FXML
    private Text presionImprimir;   
    @FXML
    private Text grasaImprimir;
    @FXML
    private Text masaImprimir;
    @FXML
    private Text pAguaImprimir;
    @FXML
    private Text iMCImprimir;
    @FXML
    private TextField textEstatura;
    @FXML
    private ChoiceBox<String> brazoAfinamiento;
    @FXML
    private ChoiceBox<String> posicionAfinamiento;
    @FXML
    private ChoiceBox<String> jornadaAfinamiento;
    @FXML
    private TextField estadoAfinamiento;
    @FXML
    private TextArea detallesAfinamiento;
    @FXML
    private Button guardarAfinamiento;
    @FXML
    private TableView<HistorialAfinamiento> tablaAfinamientos;
    @FXML
    private TableColumn<HistorialAfinamiento, LocalDate> columnaFechaAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, Double> columnaPesoAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, String> columnaPosicionAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, String> columnaJornadaAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, Integer> columnaDiastolicaAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, Integer> columnaSistolicaAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, Float> columnaGrasaAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, Float> columnaAguaAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, Float> columnaMuscularAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, Float> columnaIMCAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, String> columnaEstadoAfinamiento;
    @FXML
    private TableColumn<HistorialAfinamiento, String> columnaDetallesAfinamiento;
    @FXML
    private MenuItem menuConexion;
    @FXML
    private Button iniciarPersonalizada;
    @FXML
    private Button pausarReproduccion;
    @FXML
    private Button detenerReproduccion;
    @FXML
    private Button busquedaMediciones;
    @FXML
    private Spinner<Integer> textAFAC;
    @FXML
    private ChoiceBox<String> choiceIntervaloSubmuestreo;
    @FXML
    private Label indicadorPersonalizada;
    @FXML
    private Label indicadorMuestra;
    @FXML
    private ChoiceBox<String> grupoSanguineo;
    @FXML
    private ChoiceBox<String> raza;
    @FXML
    private CheckBox aceptacionDatos;
    @FXML
    private TextField perimetroAbdominal;
    
    /**
     * Este método administra el cierre del programa para esta clase, ya que soporta la parte prinicipal de la ejecución.
     */
    @FXML    
    public void cerrarPrograma()
    {
        admin.desconectarCliente();
        //System.out.println("Se cerró el programa");        
        System.exit(0);
    }
    
    /**
     * Este método inicia adminDevice, iniciando también el loop que verifica constantemente la conexión.
     */
    public void iniciarAdmin() 
    {        
        admin= new AdminDevice(this);
        admin.ConectarTcp(); //Se establece la conexión TCP.
        menuConexion.setText("Conectar");
        enableTunning(false);
        enableMeasure(false);
        detenerReproduccion.setDisable(true);
        pausarReproduccion.setDisable(true);
        if(admin.isConnectedTCP())
        {
            iniciarLecturaSenales();//Se inicia la graficación constante;
            enableTunning(true);
            enableMeasure(true);
            menuConexion.setText("Desconectar"); //Se habilita la desconexión manual en el menú superior.
            try {
                validadorConexion(); //Loop de verificación.
            } catch (EmptyStackException ex) 
            {
                if(apagarDesdeHilo)
                {                    
                    apagarDesdeHilo= false; //Bandera para controlar cambio de texto en algunos botones.
                    btnIniciarSenales.setText("Iniciar medición simple");
                    iniciarPersonalizada.setText("Iniciar medición personalizada");
                }
                detenerLecturaSenales();
                mensajeDesconexion();
                
            }
        }        
    }
    
    /**
     * Este método se usa para restringir botones que no deben ser usados durante algún proceso determinado.
     */
    public void restringirBotones()
    {
        guardarAfinamiento.setDisable(true);
    }
    
    /**
     * Este método permite enviar la identificación del paciente del cual se deben listar las mediciones para su reproducción.
     */
    @FXML
    private void buscarMedicion()
    {
        programaPrincipal.setUsuarioABuscar(textIdentificacion1.getText());
        programaPrincipal.mostrarVentanaReproduccion();
    }
    
    /**
     * Este método busca un paciente a través de la huella dactilar o de su número de identificación.
     */
    @FXML
    private void buscarPaciente() {
        boolean bandera = false;
        if (textIdentificacion1.getText() == null || textIdentificacion1.getText().equals("")) {
            popup = new Alert(AlertType.ERROR);
            popup.setTitle("Error huella dactilar");
            popup.setHeaderText(null);
            popup.setContentText("Ingresar una huella dactilar.");
            popup.showAndWait();
        } else {
            em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
            em.getTransaction().begin();
            List<Pacientes> list = em.createNamedQuery("Pacientes.findAll", Pacientes.class).getResultList();
            for (int i = 0; i < list.size(); i++) {
                Pacientes obj = list.get(i);
                String id = textIdentificacion1.getText();
                if (id.equals(obj.getPacientesPK().getIdentificacion())) {
                    mostrarPaciente(obj);
                    pacienteCargado= obj;
                    bandera = true;
                    opcionModificar(true);
                    llenarTablaAfinamientos();
                    break;
                }
            }
        }
        if (!bandera) {
            popup = new Alert(AlertType.ERROR);
            popup.setTitle("Pacientes de la institución");
            popup.setHeaderText(null);
            popup.setContentText("No hay paciente con este criterio.");
            popup.showAndWait();
        }
    }

    /**
     * Este método busca un paciente a través de la huella dactilar o de su número de identificación.
     */
    private Antecedentesfamiliares buscarAntecedenteFamiliares(String id) {
        Antecedentesfamiliares resultado = null;
        List<Antecedentesfamiliares> list = em.createNamedQuery("Antecedentesfamiliares.findAll", Antecedentesfamiliares.class).getResultList();
        for (int i = 0; i < list.size(); i++) {
            Antecedentesfamiliares obj = list.get(i);
            if (id.equals(obj.getAntecedentesfamiliaresPK().getIdentificacion())) {
                return obj;
            }
        }
        return resultado;
    }
    
    /**
     * Este método se usa para .
     */
    private Antecedentespersonales buscarAntecedentesPersonales(String id) {
        Antecedentespersonales resultado = null;
        List<Antecedentespersonales> list = em.createNamedQuery("Antecedentespersonales.findAll", Antecedentespersonales.class).getResultList();
        for (int i = 0; i < list.size(); i++) {
            Antecedentespersonales obj = list.get(i);
            if (id.equals(obj.getAntecedentespersonalesPK().getIdentificacion())) {
                return obj;
            }
        }
        return resultado;
    }

    @FXML
    private void nuevoPaciente() {
        inactivarCampos(false);
        textTipoIdentificacion2.setDisable(true);
        textIdentificacion2.setDisable(true);
        limpiarCampos();
        opcionGuardar(true);
        opcionNuevo = true;
    }

    @FXML
    private void modificarPaciente() {
        inactivarCampos(false);
        opcionGuardar(true);
        opcionNuevo = false;
    }

    @FXML
    private void opcionCancelar() {
        Image foto = new Image(getClass().getResource("/imagenes/untitled.jpg").toString(), 120, 120, true, true);
        Image huella2 = new Image(getClass().getResource("/imagenes/huella2.jpg").toString(), 120, 120, true, true);
        setEtiquetaFoto(new ImageView(foto));
        setEtiquetaHuella(new ImageView(huella2));
        inactivarCampos(true);
        limpiarCampos();
        opcionNuevo(true);
    }

    @FXML
    private void opcionGuardar() {
        boolean banderaGuardar = true;
        byte[] imageInByteFoto = null;
        byte[] imageInByteHuella = null;
        /*if (fotoHuella != null) {
            try {
                Image imageHuella = fotoHuella.getImage();
                BufferedImage bImageHuella = SwingFXUtils.fromFXImage(imageHuella, null);
                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                ImageIO.write(bImageHuella, "png", baos2);
                baos2.flush();
                imageInByteHuella = baos2.toByteArray();
            } catch (IOException ex) {
                banderaGuardar = false;
            }
        }
        if (fotoTomada != null) {
            try {
                Image imageFoto = fotoTomada.getImage();
                BufferedImage bImageFoto = SwingFXUtils.fromFXImage(imageFoto, null);
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                ImageIO.write(bImageFoto, "png", baos1);
                baos1.flush();
                imageInByteFoto = baos1.toByteArray();
            } catch (IOException ex) {
                banderaGuardar = false;
            }
        }*/
        if (banderaGuardar) {
            Pacientes p = new Pacientes();
            PacientesPK ppk = new PacientesPK(cboxTipoIdentificacion.getValue().toString().substring(0, 2), textIdentificacion1.getText());
            p.setPacientesPK(ppk);
            p.setNombre1(textNombre1.getText());
            p.setNombre2(textNombre2.getText());
            p.setApellido1(textApellido1.getText());
            p.setApellido2(textApellido2.getText());
            p.setAdministradora(textAdministradora.getText());
            // *************************************************
            p.setTelFijo(textTelefonoFijo.getText());
            p.setCelular(textCelular.getText());
            p.setDireccion(textDireccion.getText());
            p.setTipousuario(textTipoUsuario.getSelectionModel().getSelectedIndex());
            p.setGenero(textGenero.getSelectionModel().getSelectedItem().toString());
            Date date = Date.from(datePickerFechaNacimiento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            p.setFNacimiento(date);
            p.setDepartamento(textDepartamento.getText());
            p.setMunicipio(textMunicipio.getText());
            p.setZona(textZona.getSelectionModel().getSelectedItem().toString());
            p.setEstatura(Float.parseFloat(textEstatura.getText()));
            p.setGrupoSanguineo(grupoSanguineo.getSelectionModel().getSelectedItem());
            p.setRaza(raza.getSelectionModel().getSelectedItem());
            int autorizacion = 1;
            if(!aceptacionDatos.isSelected())
            {
                autorizacion= 0;
            }
            p.setAutorizacion(autorizacion); 
            // *************************************************
            //p.setFoto(imageInByteFoto);
            //p.setHuella(plantillaHuella.serialize());
            //p.setHuella2(imageInByteHuella);
            em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
            em.getTransaction().begin();
            if (opcionNuevo) {
                em.persist(p);
            } else {
                em.merge(p);
            }
            em.getTransaction().commit();
            // *************************************************
            // *** Antecedente personales
            // *************************************************
            Antecedentespersonales ap = new Antecedentespersonales();
            AntecedentespersonalesPK appk = new AntecedentespersonalesPK(cboxTipoIdentificacion.getValue().toString().substring(0, 2), textIdentificacion1.getText());
            ap.setAntecedentespersonalesPK(appk);
            ap.setMedicamentospermanentes1(textMedicamentosPermanentes2.getText());
            ap.setMedicamentospermanentes2(textMedicamentosPermanentes2.getText());
            ap.setMedicamentospermanentes3(textMedicamentosPermanentes3.getText());
            ap.setMedicamentospermanentes4(textMedicamentosPermanentes4.getText());
            ap.setMedicamentospermanentes5(textMedicamentosPermanentes5.getText());
            ap.setOtrassustancias1(textOtrasSustancias1.getText());
            ap.setOtrassustancias2(textOtrasSustancias2.getText());
            ap.setOtrassustancias3(textOtrasSustancias3.getText());
            ap.setOtrassustancias4(textOtrasSustancias4.getText());
            ap.setOtrassustancias5(textOtrasSustancias5.getText());
            ap.setDiabetes(textDiabetes.getSelectionModel().getSelectedItem().toString());
            ap.setHipertension(textHipertension.getSelectionModel().getSelectedItem().toString());
            ap.setInfartos(textInfartos.getValue().toString());
            ap.setFumadias(textFumaDias.getValue().toString());
            ap.setConviveconfumadores(textConviveConFumadores.getSelectionModel().getSelectedItem().toString());
            ap.setActividadfisica(textActividadFisicaMinutos.getSelectionModel().getSelectedItem().toString());
            ap.setCosumelicor(textCosumeLicor.getValue().toString());
            em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
            em.getTransaction().begin();
            if (opcionNuevo) {
                em.persist(ap);
            } else {
                em.merge(ap);
            }
            em.getTransaction().commit();
            // *************************************************
            // *** Antecedente familiares
            // *************************************************
            Antecedentesfamiliares af = new Antecedentesfamiliares();
            AntecedentesfamiliaresPK afpk = new AntecedentesfamiliaresPK(cboxTipoIdentificacion.getValue().toString().substring(0, 2), textIdentificacion1.getText());
            af.setAntecedentesfamiliaresPK(afpk);
            af.setDiabetes(Integer.parseInt(textAFDiabetes.getValue().toString()));
            af.setHipertension(Integer.parseInt(textAFHipertension.getValue().toString()));
            af.setInfartos(Integer.parseInt(textAFInfartos.getValue().toString()));
            af.setAcv(Integer.parseInt(textAFAC.getValue().toString()));
            em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
            em.getTransaction().begin();
            if (opcionNuevo) {
                em.persist(af);
            } else {
                em.merge(af);
            }
            em.getTransaction().commit();
            String iniciar= textIdentificacion1.getText();
            opcionCancelar();
            textIdentificacion1.setText(iniciar);
            buscarPaciente();
        }
    }
    
    private void pintarLecturaECG() {
        //admin = new AdminDevice(null);
        //admin.dispositivoDesconectado();
        //admin.ConectarTcp();
    }

    private void pararLecturaECG() {
        admin.enviarComando("stopPressure",0);
        //admin.dispositivoDesconectado();
        admin.desconectarCliente();
    }
    
    private void iniciarPresionAutomatica(int minutos) {
        
        admin.enviarComando("stopPressure",0);
        admin.enviarComando("continousPressure", minutos);
        admin.enviarComando("startPressure",0);
    }
    
    private void iniciarPresionManual() {
        admin.enviarComando("manualPressure", 0);        
        admin.enviarComando("stopPressure",0);
        admin.enviarComando("startPressure",0);        
    }
    
    /**
     * Carga la ventana cuando el usuario hace click en la foto.
     */
    @FXML
    public void ventanaFoto() {
        programaPrincipal.mostrarVentanaFoto(this);
    }

    /**
     * Carga la ventana cuando el usuario hace click en la huella.
     */
    @FXML
    public void ventanaHuella() {
        programaPrincipal.mostrarVentanaHuella(this);
    }

    /**
     * Opción "acerca de" del menu.
     */
    private void acercaDe() {
        popup = new Alert(AlertType.ERROR);
        popup.setTitle("Acerca de Registro de Estudiantes.");
        popup.setHeaderText(null);
        popup.setContentText("Derechos de autor.");
        popup.showAndWait();
    }
    
    public void setFotoTomada(ImageView etiquetaFoto) {
        this.fotoTomada = etiquetaFoto;
    }

    public void setFotoHuella(ImageView etiquetaHuella) {
        this.fotoHuella = etiquetaHuella;
    }

    public void setEtiquetaFoto(ImageView etiquetaFoto) {
        etiquetaFoto.setFitHeight(150);
        etiquetaFoto.setFitWidth(200);
        this.etiquetaFoto.setGraphic(etiquetaFoto);
    }

    public void setEtiquetaHuella(ImageView etiquetaHuella) {
        etiquetaHuella.setFitHeight(150);
        etiquetaHuella.setFitWidth(200);
        this.etiquetaHuella.setGraphic(etiquetaHuella);
    }

    public void setPlantillaHuella(DPFPTemplate plantillaHuella) {
        this.plantillaHuella = plantillaHuella;
    }

    public void setProgramaPrincipal(Kiosko programaPrincipal, String usuario, String nombre, String rol) {
        this.programaPrincipal = programaPrincipal;
        this.usuario = usuario;
        this.nombre = nombre;
        this.rol = rol;
        this.cboxTipoIdentificacion.getItems().addAll("CC = Cédula ciudadanía", "CE = Cédula de extranjería", "PA = Pasaporte", "RC = Registro civil", "TI = Tarjeta de identidad", "AS = Adulto sin identificación", "MS = Menor sin identificación");
    }

    public void initialize(URL url, ResourceBundle rb) {
        iniciarGrafica();        
    }
    
    public void iniciarGrafica () {
        inactivarCampos(true);
        graficar();
        initChartSignals();
        graficarECG1();
        graficarECG2();
        //graficarSPO2();
        //graficarRESP();
        parametros();
        
    }

    public void opcionModificar(boolean estado) {
        btonBuscar.setDisable(estado);
        btonModificar.setDisable(!estado);
        btonNuevo.setDisable(estado);
        btonGuardar.setDisable(estado);
        btonCancelar.setDisable(!estado);
    }

    public void opcionNuevo(boolean estado) {
        btonBuscar.setDisable(!estado);
        btonModificar.setDisable(estado);
        btonNuevo.setDisable(!estado);
        btonGuardar.setDisable(estado);
        btonCancelar.setDisable(!estado);
    }

    public void opcionGuardar(boolean estado) {
        btonBuscar.setDisable(estado);
        btonModificar.setDisable(estado);
        btonNuevo.setDisable(estado);
        btonGuardar.setDisable(!estado);
        btonCancelar.setDisable(!estado);
    }

    public void mostrarPaciente(Pacientes paciente) {
        // Imagenes por defecto cuando no hay huella o foto.
        Image foto = new Image(getClass().getResource("/imagenes/untitled.jpg").toString(), 120, 120, true, true);
        Image huella1 = new Image(getClass().getResource("/imagenes/huella1.jpg").toString(), 120, 120, true, true);
        Image huella2 = new Image(getClass().getResource("/imagenes/huella2.jpg").toString(), 120, 120, true, true);
        // Datos del encabezado del menu.
        cboxTipoIdentificacion.setValue(paciente.getPacientesPK().getTipoid());
        textIdentificacion1.setText(paciente.getPacientesPK().getIdentificacion());
        textAdministradora.setText(paciente.getAdministradora());
        textNombre1.setText(paciente.getNombre1());
        textNombre2.setText(paciente.getNombre2());
        textApellido1.setText(paciente.getApellido1());
        textApellido2.setText(paciente.getApellido2());
        textApellido2.setText(paciente.getApellido2());
        // Datos personales del paciente.
        textEstatura.setText(String.valueOf(paciente.getEstatura()));
        grupoSanguineo.getSelectionModel().select(paciente.getGrupoSanguineo());
        raza.getSelectionModel().select(paciente.getRaza());
        textTipoIdentificacion2.setText(paciente.getPacientesPK().getTipoid());
        textIdentificacion2.setText(paciente.getPacientesPK().getIdentificacion());
        textTelefonoFijo.setText(paciente.getTelFijo());
        textCelular.setText(paciente.getCelular());
        textDireccion.setText(paciente.getDireccion());
        textTipoUsuario.getSelectionModel().select(paciente.getTipousuario());
        
        int seleccionGenero= 0;
        if(paciente.getGenero().equals("Masculino"))
        {
            seleccionGenero= 1;
        }
        textGenero.getSelectionModel().select(seleccionGenero); //(paciente.getGenero());
        datePickerFechaNacimiento.setValue((paciente.getFNacimiento() != null) ? Instant.ofEpochMilli(paciente.getFNacimiento().getTime()).atZone(ZoneId.systemDefault()).toLocalDate() : null);
        textDepartamento.setText(paciente.getDepartamento());
        textMunicipio.setText(paciente.getMunicipio());
        textZona.getSelectionModel().select(0); //(paciente.getZona());
        // Recupera la plantilla almacenada en la base de datos.
        byte templateBuffer[] = paciente.getHuella();
        if (templateBuffer != null) {
            plantillaHuella = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
        }
        // pinta las imagenes en el menu de la foto y la huella dactilar.
        if (paciente.getFoto() == null) {
            setEtiquetaFoto(new ImageView(foto));
        } else {
            InputStream in1 = new ByteArrayInputStream(paciente.getFoto());
            Image image1 = new Image(in1);
            fotoTomada = new ImageView(image1);
            setEtiquetaFoto(fotoTomada);
        }
        if (paciente.getHuella() == null) {
            setEtiquetaHuella(new ImageView(huella2));
        } else if (paciente.getHuella2() == null) {
            setEtiquetaHuella(new ImageView(huella1));
        } else {
            InputStream in2 = new ByteArrayInputStream(paciente.getHuella2());
            Image image2 = new Image(in2);
            fotoHuella = new ImageView(image2);
            setEtiquetaHuella(fotoHuella);
        }
        // Antecedentes personales
        Antecedentespersonales ap = buscarAntecedentesPersonales(paciente.getPacientesPK().getIdentificacion());
        textMedicamentosPermanentes1.setText(ap.getMedicamentospermanentes1());
        textMedicamentosPermanentes2.setText(ap.getMedicamentospermanentes2());
        textMedicamentosPermanentes3.setText(ap.getMedicamentospermanentes3());
        textMedicamentosPermanentes4.setText(ap.getMedicamentospermanentes4());
        textMedicamentosPermanentes5.setText(ap.getMedicamentospermanentes5());
        textOtrasSustancias1.setText(ap.getOtrassustancias1());
        textOtrasSustancias2.setText(ap.getOtrassustancias2());
        textOtrasSustancias3.setText(ap.getOtrassustancias3());
        textOtrasSustancias4.setText(ap.getOtrassustancias4());
        textOtrasSustancias5.setText(ap.getOtrassustancias5());
        textDiabetes.setValue(ap.getDiabetes());
        textHipertension.setValue(ap.getHipertension());
        textInfartos.getValueFactory().setValue(Integer.parseInt(ap.getInfartos())); 
        textFumaDias.getValueFactory().setValue(Integer.parseInt(ap.getFumadias()));
        textConviveConFumadores.setValue(ap.getConviveconfumadores());
        textActividadFisicaMinutos.setValue(ap.getActividadfisica());
        textCosumeLicor.getValueFactory().setValue(Integer.parseInt(ap.getCosumelicor()));
        // Antecedentes familiares
        Antecedentesfamiliares af = buscarAntecedenteFamiliares(paciente.getPacientesPK().getIdentificacion());
        textAFDiabetes.getValueFactory().setValue(af.getDiabetes());
        textAFHipertension.getValueFactory().setValue(af.getHipertension());
        textAFInfartos.getValueFactory().setValue(af.getInfartos());
        textAFAC.getValueFactory().setValue(af.getAcv());
    }

    public void inactivarCampos(boolean estado) {
        // Datos personales
        //textTipoIdentificacion.setDisable(estado);
        //textIdentificacion1.setDisable(estado);
        //textAdministradora.setDisable(estado);
        //textNombre1.setDisable(estado);
        //textNombre2.setDisable(estado);
        //textApellido1.setDisable(estado);
        //textApellido2.setDisable(estado);
        textTipoIdentificacion2.setDisable(estado);
        textIdentificacion2.setDisable(estado);
        textTelefonoFijo.setDisable(estado);
        textCelular.setDisable(estado);
        textDireccion.setDisable(estado);
        textTipoUsuario.setDisable(estado);
        textGenero.setDisable(estado);
        datePickerFechaNacimiento.setDisable(estado);
        textDepartamento.setDisable(estado);
        textMunicipio.setDisable(estado);
        textZona.setDisable(estado);
        // Antecedentes personales
        textMedicamentosPermanentes1.setDisable(estado);
        textMedicamentosPermanentes2.setDisable(estado);
        textMedicamentosPermanentes3.setDisable(estado);
        textMedicamentosPermanentes4.setDisable(estado);
        textMedicamentosPermanentes5.setDisable(estado);
        textOtrasSustancias1.setDisable(estado);
        textOtrasSustancias2.setDisable(estado);
        textOtrasSustancias3.setDisable(estado);
        textOtrasSustancias4.setDisable(estado);
        textOtrasSustancias5.setDisable(estado);
        textDiabetes.setDisable(estado);
        textHipertension.setDisable(estado);
        textInfartos.setDisable(estado);
        textFumaDias.setDisable(estado);
        textConviveConFumadores.setDisable(estado);
        textActividadFisicaMinutos.setDisable(estado);
        textCosumeLicor.setDisable(estado);
        // Antecedentes familiares
        textAFDiabetes.setDisable(estado);
        textAFHipertension.setDisable(estado);
        textAFInfartos.setDisable(estado);
        textAFAC.setDisable(estado);
        
        //nuevos
        textEstatura.setDisable(estado);
        grupoSanguineo.setDisable(estado);
        raza.setDisable(estado);
    }

    public void limpiarCampos() {
        // Datos personales
        textTipoIdentificacion2.setText("");
        textIdentificacion1.setText("");
        textAdministradora.setText("");
        textNombre1.setText("");
        textNombre2.setText("");
        textApellido1.setText("");
        textApellido2.setText("");
        textIdentificacion2.setText("");
        textTelefonoFijo.setText("");
        textCelular.setText("");
        textDireccion.setText("");
        textTipoUsuario.getSelectionModel().selectFirst();
        textGenero.getSelectionModel().selectFirst();
        datePickerFechaNacimiento.setValue(null);
        textDepartamento.setText("");
        textMunicipio.setText("");
        textZona.getSelectionModel().selectFirst();
        // Antecedentes personales
        textMedicamentosPermanentes1.setText("");
        textMedicamentosPermanentes2.setText("");
        textMedicamentosPermanentes3.setText("");
        textMedicamentosPermanentes4.setText("");
        textMedicamentosPermanentes5.setText("");
        textOtrasSustancias1.setText("");
        textOtrasSustancias2.setText("");
        textOtrasSustancias3.setText("");
        textOtrasSustancias4.setText("");
        textOtrasSustancias5.setText("");
        textDiabetes.getSelectionModel().selectFirst();
        textHipertension.getSelectionModel().selectFirst();
        textInfartos.getValueFactory().setValue(0);
        textFumaDias.getValueFactory().setValue(0);
        textConviveConFumadores.getSelectionModel().selectFirst();
        textActividadFisicaMinutos.getSelectionModel().selectFirst();
        textCosumeLicor.getValueFactory().setValue(0);
        // Antecedentes familiares
        textAFDiabetes.getValueFactory().setValue(0);
        textAFHipertension.getValueFactory().setValue(0);
        textAFInfartos.getValueFactory().setValue(0);
        textAFAC.getValueFactory().setValue(0);
        
        //nuevo
        textEstatura.setText("");
        grupoSanguineo.getSelectionModel().selectFirst();
        raza.getSelectionModel().selectFirst();
    }
    
    //Variables necesarias para el método
    boolean primeraVez= true;
    private boolean apagarDesdeHilo= false;
    
    public void apagarDesdeHilo()
    {
        apagarDesdeHilo= true;
    }
    
    
    private static Timer timerIniciar;
    private void iniciarLecturaSenales() 
    {   
        /*timerIniciar = new Timer();
        admin.switchLectura(); //Se habilita el hilo de lectura.
        TimerTask taskIniciar = new TimerTask() 
        {

            @Override
            public void run()
            {
                hiloPrimeraVez();
            }
        }; 
        // Empezamos dentro de 0s 
        timerIniciar.schedule(taskIniciar, 0);*/
        admin.switchLectura(); //Se habilita el hilo de lectura.
        graficarECG1();
        graficarECG2();
        //graficarSPO2();
        //graficarRESP();
        pintarLecturaECG();

        

        addToQueue = new AddToQueue();
        queueParam= new QueueParametros();

        //addToQueue.run();
        //queueParam.run(); //Mirar si esto soluciona el error de concurrencia.

        executor.execute(addToQueue);
        executor.execute(queueParam);
        prepareTimeline();
        //btnIniciarSenales.setText("Parar"); 
    }
    
    private void detenerLecturaSenales()
    {
        //timerIniciar.cancel();
        //timerIniciar.purge();
        executor.shutdownNow();

        executor = Executors.newFixedThreadPool(2);

        //pararLecturaECG(); //Este método no está cumpliendo función alguna
        addToQueue=null;
        queueParam=null;
    }
    
    private static boolean cancelarMedicionSimple= false; //Bandera para saber cuándo la función debe cancelar el proceso.
    @FXML 
    private void medicionSimple()
    {
        if(cancelarMedicionSimple) //Aquí sólo entra si el usuario cancela la medición simple
        {
            cancelarMedicionSimple= false; //Se reestablece la bandera para que pueda iniciarse una nueva medición
            almacenarMedicion= false; //Se desactiva el registro de la medición.
            enableMeasure(true); //Se activan los botones de medición.
            btnIniciarSenales.setText("Iniciar medición simple");
            resetVectores();
            //Método de testigo de medición simple
            indicador(false, "");
            mensajePersonalizado("La medición simple ha sido cancelada", "Medición simple cancelada");
                        
        }else
        {
            cancelarMedicionSimple= true;
            //Se activa el almacenado de la medición
            
            //Método de testigo de medición simple
            indicador(true, "Medición simple en curso");
            
            
            //*********************************************
            almacenarMedicion= true;
            enableMeasure(false);
            btnIniciarSenales.setDisable(false);
            btnIniciarSenales.setText("Detener medición");
            
            //Se crea el método que almacenará las señales dentro de un minuto.
            Timer timerMedicionUnMinuto;
            timerMedicionUnMinuto = new Timer();             
            TimerTask taskIniciar = new TimerTask() 
            {

                @Override
                public void run()
                {
                    cancelarMedicionSimple= false; //Se reestablece la bandera para que pueda iniciarse una nueva medición                    
                    almacenarSenales(); //Se almacenan las señales.
                    almacenarMedicion= false; //Se desactiva el registro de la medición.
                    enableMeasure(true); //Se activan los botones de medición.
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {                    
                            //Método de testigo de medición simple
                            indicador(false, "");
                            mensajePersonalizado("La medición simple ha finalizado", "Medición simple terminada");                               
                        }
                    });
                    
                }
            }; 
            // Empezamos dentro de 60s 
            timerMedicionUnMinuto.schedule(taskIniciar, 60000);
        }
    }
    
    private static boolean cancelarMedicionPersonalizada= false; //Bandera para saber cuándo la función debe cancelar el proceso.
    private static boolean terminaMedicion= false; //Bandera que controla que no se ejecuten más registros de los necesarios.
    //Timerse necesarios
    private static Timer timerCancelarGuardado;
    private static Timer timerMuestra;
    private static Timer timerMedicion;
    private static Timer TimerSubmuestreo;
    private static Timer timerSubMuestra;
    @FXML
    private void medicionPersonalizada()
    {        
        if (cancelarMedicionPersonalizada)
        {
            cancelarMedicionPersonalizada= false;
            terminaMedicion= true;            
            //Se cancelan los timers de la medición personalizada
            timerCancelarGuardado.cancel();
            timerMuestra.cancel();        
            timerMedicion.cancel();
            timerSubMuestra.cancel();
            //Se almacenan los datos que se hayan alcanzado a tomar del submuestreo
            actualizarMedicionPersonalizada();
            resetVectoresSubmuestreo();
            
            //Se informa que se ya NO se almacenará una medición personalizada
            esPersonalizada= false;
            //Se genera mensaje
            mensajePersonalizado("La medición personalizada ha sido cancelada", "Medición personalizada cancelada");

            //Se cambia el testigo de medición personalizada
            indicador(false, "");

            //Se habilita la medición
            enableMeasure(true);

        }else
        {
            cancelarMedicionPersonalizada= true;
            terminaMedicion= false;
            int intervalo= obtenerIntervalo()*60*1000;
            int duracionMuestra= obtenerDuracion()*1000;
            int duracionExamen= obtenerDuracionExamen()*60*1000;
            int subMuestreo= obtenerSubmuestreo()*1000;
            
            //Se crea la medición personalizada
            idMedPersonalizada= crearMedicionPersonalizada();
            //Se informa que se almacenará una medición personalizada
            esPersonalizada= true;
            
            //Testigo que advierte de medición personalizada
            indicador(true, "Medición personalizada en curso");
            
            
            //*******************************************************            
                        
            //Control de duración de muestra.            
            timerMedicion = new Timer();             
            TimerTask taskMedicion = new TimerTask() 
            {
                @Override
                public void run()
                {                    
                    cancelarMedicionPersonalizada= false; //Se habilita que se pueda hacer nuevo medición personalizada
                    terminaMedicion= true;
                    almacenarMedicion= false; //Se desactiva el registro de la medición.
                    //Se cancelan los timers de la medición personalizada
                    timerCancelarGuardado.cancel();
                    timerMuestra.cancel(); 
                    timerSubMuestra.cancel();
                    //Se almacenan los datos del submuestreo
                    actualizarMedicionPersonalizada();
                    resetVectoresSubmuestreo();
                    
                    //Se informa que se ya NO se almacenará una medición personalizada
                    esPersonalizada= false;
                    //Se genera mensaje
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {                    
                                //Se cambia el testigo de medición personalizada
                               indicador(false, "");
                               mensajePersonalizado("La medición personalizada ha terminado con éxito", "Medición personalizada terminada");                               
                        }
                    });                    
                    
                    //Se habilita la medición
                    enableMeasure(true);
                                 
                }
            };   
            //Se lanza el timer que detiene todo el examen.
            timerMedicion.schedule(taskMedicion, duracionExamen); 
            
            //Timer de la muestra
            
            timerMuestra = new Timer();             
            TimerTask taskMuestra = new TimerTask() 
            {

                @Override
                public void run() 
                {                    
                    if(!terminaMedicion) //Si la medición personalizada no ha terminado, continúe
                    {
                       //Se activa el registro de señales
                        almacenarMedicion= true;
                        //Activar testigo de muestreo
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {                    
                                //Testigo que advierte de medición personalizada
                                indicadorMuestreo(true, "Medición en curso \nno retire los dispositivos");
                            }
                        }); 
                    }
                }
            };   
            //Se lanza el timer que habilita el registro de datos.
            timerMuestra.schedule(taskMuestra, 10, intervalo); //Se inicia en 10 para que alcance a verificar si ya se temrinó la medición.
            
            //Timer para guardar los datos durante el submuestreo
            
            timerSubMuestra = new Timer();             
            TimerTask taskSubMuestra = new TimerTask() 
            {

                @Override
                public void run() 
                {                    
                    if(!terminaMedicion) //Si la medición personalizada no ha terminado, continúe
                    {
                       actualizarVectoresSubmuestreo(); 
                    }
                }
            };   
            //Se lanza el timer que registra los datos del submuestreo periodicamente.
            timerSubMuestra.schedule(taskSubMuestra, 10, subMuestreo); //Se inicia en 10 para que alcance a verificar si ya se temrinó la medición.
            
            //Timer para cancelar guardado            
            timerCancelarGuardado = new Timer();             
            TimerTask taskCancelar = new TimerTask() 
            {

                @Override
                public void run()
                {
                    if(!terminaMedicion) //Si la medición personalizada no ha terminado, continúe
                    {
                        almacenarMedicion= false; //Se desactiva el registro de la medición.
                    
                        //Desactivar testigo de muestreo
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {                    
                                   //Testigo que advierte de medición personalizada
                                   indicadorMuestreo(false, "");
                            }
                        });


                        almacenarSenales(); //Se almacenan las señales.
                    }                                        
                }
            };   
            //Se lanza el timer que cancela el guardado y almacena lo registrado.
            timerCancelarGuardado.schedule(taskCancelar, duracionMuestra, intervalo);
/*
            //Control de duración de submuestreo.            
            TimerSubmuestreo = new Timer();             
            TimerTask taskSubmuestreo = new TimerTask() 
            {

                @Override
                public void run()
                {   
                                 
                }
            };   
            //Se lanza el timer controla el submuestreo.
            TimerSubmuestreo.schedule(taskSubmuestreo, subMuestreo, subMuestreo); */
            
        }
    }
    
    /*
    private void iniciarSenalesViejo()
    {
        Timer timerIniciar;
        timerIniciar = new Timer();  
        admin.switchLectura();
        
        
        if (banderaInicio)         
        {            
            timerIniciar.cancel();
            timerIniciar.purge();

            executor.shutdownNow();

            executor = Executors.newFixedThreadPool(2);

            //pararLecturaECG(); //Este método no está cumpliendo función alguna
            addToQueue=null;
            queueParam=null;
            if(!apagarDesdeHilo)
            {
                btnIniciarSenales.setText("Iniciar medición simple");                
            }else
            {
                btnIniciarSenales.setDisable(true);
            }
                
            //apagarDesdeHilo= false;            
            //Guardar en la base de datos
            almacenarSenales();
                        
        } else 
        { 
            if(!esPersonalizada)
            {
                btnIniciarSenales.setText("Detener medición");
            }            
            if(primeraVez)
            {
               primeraVez= false;
               TimerTask taskIniciar = new TimerTask() 
                {

                    @Override
                    public void run()
                    {
                        hiloPrimeraVez();
                    }
                }; 
                // Empezamos dentro de 0s 
                timerIniciar.schedule(taskIniciar, 0);
            }
            else
            {
                TimerTask taskIniciar = new TimerTask() 
                {

                    @Override
                    public void run()
                    {
                        hiloSegundaVez();
                    }
                }; 
                // Empezamos dentro de 0s 
                timerIniciar.schedule(taskIniciar, 0);
            }
        }
        banderaInicio = !banderaInicio;
    }*/
    
    /*
    private void hiloPrimeraVez()
    {
        graficarECG1();
        graficarECG2();
        //graficarSPO2();
        //graficarRESP();
        pintarLecturaECG();

        

        addToQueue = new AddToQueue();
        queueParam= new QueueParametros();

        //addToQueue.run();
        //queueParam.run(); //Mirar si esto soluciona el error de concurrencia.

        executor.execute(addToQueue);
        executor.execute(queueParam);
        prepareTimeline();
        //btnIniciarSenales.setText("Parar");        
    }
    
    private void hiloSegundaVez()
    {
        graficarECG1();
        graficarECG2();
        //graficarSPO2();
        //graficarRESP();
        pintarLecturaECG();

        addToQueue = new AddToQueue();
        queueParam= new QueueParametros();

        executor.execute(addToQueue);
        executor.execute(queueParam);
        prepareTimeline();
    }*/
    
    //private boolean terminaMedicion; //Determina si la medición ya terminó del todo
    private boolean banderaMedicion= true; //Determina si la medición personalizada inicia o se cancela
    private static boolean ultimaDeLaSerie= false;
    
    public void iniciarMedicionPersonalizada()
    {               
        
        
    }
    
    private int crearMedicionPersonalizada()
    { 
        em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
        em.getTransaction().begin();
        int intervaloMedicion= obtenerIntervalo();
        int duracionMuestraMedicion= obtenerDuracion();
        int duracionExamenMedicion= obtenerDuracionExamen();
        int duracionSubmuestreo= obtenerSubmuestreo();
        //AquÃ­ colocas tu objeto tipo Date
        Date date= new Date();
        try {                        
            date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        } catch (ParseException ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
        java.sql.Timestamp fechaGuardar = new java.sql.Timestamp(date.getTime());
        MedicionPersonalizada med= new MedicionPersonalizada();
        med.setId(1);
        med.setIntervalo(intervaloMedicion);
        med.setDuracionExamen(duracionExamenMedicion);
        med.setDuracionMuestra(duracionMuestraMedicion);
        med.setSubintervalo(duracionSubmuestreo);
        med.setFecha(fechaGuardar);
        med.setDetalles(detallesMedicion.getText());
        med.setSistolica("");
        med.setDiastolica("");
        med.setPulso("");
        med.setECG("");
        med.setSPO2("");
        med.setHeartRate("");
        med.setRESP("");
        med.setMinutos("");
        
        em.persist(med);                
        em.getTransaction().commit();  
        
        //Obtiendo el ID para guardar cada medicion de la medición personalizada
        Query queryMedicionFindAll = em.createNativeQuery("select * from medicion_personalizada m order by m.id desc", MedicionPersonalizada.class);
                       
        List<MedicionPersonalizada> listMedicion = queryMedicionFindAll.getResultList();
        
        return listMedicion.get(0).getId();     
    }
    
    private void actualizarMedicionPersonalizada()
    {
        em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
        MedicionPersonalizada medicionPersonalizada= em.find(MedicionPersonalizada.class, idMedPersonalizada);
        em.getTransaction().begin();
        medicionPersonalizada.setSistolica(SubmuestreoSistolica.toString());
        medicionPersonalizada.setDiastolica(SubmuestreoDiastolica.toString());
        medicionPersonalizada.setPulso(SubmuestreoPulso.toString());
        medicionPersonalizada.setECG(SubmuestreoECG.toString());
        medicionPersonalizada.setSPO2(SubmuestreoSPO2text.toString());
        medicionPersonalizada.setHeartRate(SubmuestreoHR.toString());
        medicionPersonalizada.setRESP(SubmuestreoRESPtext.toString());
        medicionPersonalizada.setMinutos(Submuestreominutos.toString());
        em.getTransaction().commit();
        System.out.println("Se guardó registro del submuestreo");
        
    }
    
    private void acabarMedicion(int tiempo)
    {
        
    }
    
    private int obtenerIntervalo()
    {
       String valor= this.intervalo.getSelectionModel().getSelectedItem();
       int resultado= 5;
       switch(valor)
       {
           case "10 minutos":
               resultado= 10;
               break;
           case "15 minutos":
               resultado= 15;
               break;
           case "20 minutos":
               resultado= 20;
               break;
           case "25 minutos":
               resultado= 25;
               break;
           case "30 minutos":
               resultado= 30;
               break;
           default: resultado= 5;
               break;
       }       
       return resultado;
    }
    
    private int obtenerDuracion()
    {
       String valor= this.duracionMuestra.getSelectionModel().getSelectedItem();
       int resultado= 15;
       switch(valor)
       {
           case "30 segundos":
               resultado= 30;
               break;
           case "45 segundos":
               resultado= 45;
               break;
           case "60 segundos":
               resultado= 60;
               break;
           default: resultado= 15;
               break;
       }       
       return resultado;
    }
    
    private int obtenerDuracionExamen()
    {
       String valor= this.duracionExamen.getSelectionModel().getSelectedItem();
       int resultado= 30;
       switch(valor)
       {
           case "20 minutos":
               resultado= 20;
               break;
           case "30 minutos":
               resultado= 30;
               break;
           case "1 hora":
               resultado= 60;
               break;
           case "1 hora, 30 minutos":
               resultado= 90;
               break;
           case "2 horas": //Modificar esto, en este momento está para pruebas.
               resultado= 120;               
               break;
           default: resultado= 10;
               break;
       }       
       return resultado;
    }
    
     private int obtenerSubmuestreo()
    {
       String valor= this.choiceIntervaloSubmuestreo.getSelectionModel().getSelectedItem();
       int resultado= 10;
       switch(valor)
       {
           case "20 minutos":
               resultado= 20;
               break;
           case "30 minutos":
               resultado= 30;
               break;
           case "40 minutos":
               resultado= 40;
               break;
           case "50 minutos":
               resultado= 50;
               break;
           case "60 minutos": //Modificar esto, en este momento está para pruebas.
               resultado= 60;               
               break;
           default: resultado= 10;
               break;
       }       
       return resultado;
    }
    
    
    @FXML
    public void almacenarAfinamiento()
    {
        em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
        em.getTransaction().begin();
        List<Pacientes> list = em.createNamedQuery("Pacientes.findAll", Pacientes.class).getResultList();
        for (int i = 0; i < list.size(); i++) 
        {
            Pacientes obj = list.get(i);
            if (obj.getPacientesPK().getIdentificacion().equals(textIdentificacion1.getText()))
            {                    
                try
                {
                    //Aquí colocas tu objeto tipo Date
                    Date date= new Date();
                    date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));                        
                    java.sql.Timestamp fechaGuardar = new java.sql.Timestamp(date.getTime()); 
                    
                    HistorialAfinamiento afi= new HistorialAfinamiento();
                    afi.setId(1);
                    afi.setIdentificacion(textIdentificacion1.getText());
                    afi.setTipoIdentificacion(cboxTipoIdentificacion.getValue().toString().substring(0, 2));                    
                    afi.setFecha(fechaGuardar);
                    afi.setPeso(pesoAlmacenar);
                    afi.setBrazo(brazoAfinamiento.getSelectionModel().getSelectedItem());
                    afi.setPosicion(posicionAfinamiento.getSelectionModel().getSelectedItem());
                    afi.setJornada(jornadaAfinamiento.getSelectionModel().getSelectedItem());
                    afi.setEstadoInicial(estadoAfinamiento.getText());
                    int perimetro=0;
                    try
                    {
                        perimetro=Integer.parseInt(perimetroAbdominal.getText());
                    }catch(Exception e)
                    {
                        //no asigna
                    }
                    afi.setPerimetroAbdominal(perimetro);
                    afi.setPresDiastolica(this.presDiastolica);
                    afi.setPresSistolica(this.presSistolica);
                    afi.setDetalles(detallesAfinamiento.getText());
                    afi.setGrasaCorporal(this.grasa);
                    afi.setPorcentajeAgua(this.porcentajeAgua);
                    afi.setMasaMuscular(this.masaMuscular);
                    afi.setImc(this.imc);
                                           
                    em.persist(afi);
                }catch(Exception e)
                {
                    e.printStackTrace();
                } 
            }
        }
        em.getTransaction().commit();
        guardarAfinamiento.setDisable(true);
        llenarTablaAfinamientos();
}

private boolean esPersonalizada= false; //Determina si la medición a guardar hace parte de una personalizada.
private int idMedPersonalizada; //Almacena el id de la medición personalizada a asociar con cada medición.

public void almacenarSenales()
{
        em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
        em.getTransaction().begin();
        List<Pacientes> list = em.createNamedQuery("Pacientes.findAll", Pacientes.class).getResultList();
        for (int i = 0; i < list.size(); i++) 
        {
            Pacientes obj = list.get(i);
            if (obj.getPacientesPK().getIdentificacion().equals(textIdentificacion1.getText()))
            {                    
                try
                {
                    //Obtención de la medición personalizada
                    EntityManager emAuxiliar = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
                    Query queryMedicionFindAll = em.createNativeQuery("SELECT * from medicion_personalizada m WHERE id= '1'", MedicionPersonalizada.class);
                    if(esPersonalizada)
                    {
                        queryMedicionFindAll = em.createNativeQuery("SELECT * from medicion_personalizada m WHERE id= '"+ idMedPersonalizada + "'", MedicionPersonalizada.class);
                    }                  
                    List<MedicionPersonalizada> listMedicion = queryMedicionFindAll.getResultList();
                    //--------------------------------------
                    
                    Medicion med= new Medicion();
                    med.setId(1);
                    med.setIdentificacion(obj); 
                    med.setTipoid(obj);
                    med.setIdPersonalizada(listMedicion.get(0));                    
                    med.setDetalles(detallesMedicion.getText());

                    //Aquí colocas tu objeto tipo Date
                    Date date= new Date();
                    date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));                        
                    java.sql.Timestamp fechaGuardar = new java.sql.Timestamp(date.getTime());                       

                    med.setFecha(fechaGuardar); 
                    med.setOndaSPO2(vSPO2.toString());
                    med.setOndaECG1(vECG1.toString());
                    med.setOndaECG2(vECG2.toString());
                    med.setOndaRESP(vRESP.toString());
                    med.setPresionSistolica(vsistolica.toString());
                    med.setPresionDiastolica(vdiastolica.toString());
                    med.setPulso(vpulso.toString());
                    med.setMed(vmed.toString());
                    med.setEcg(vECG.toString());
                    med.setSpo2(vSPO2text.toString());
                    med.setHr(vHR.toString());
                    med.setResp(vRESPtext.toString());                        
                    em.persist(med);
                    
                    resetVectores();
                }catch(Exception e)
                {
                    System.out.println(e);
                } 
            }
        }
        em.getTransaction().commit();
    }

public void resetVectores()
{
    //vectores para guardar datos de la medicion
    vSPO2 = new Vector(0,1);
    vECG1 = new Vector(0,1);
    vECG2 = new Vector(0,1);
    vRESP = new Vector(0,1);
    vsistolica = new Vector(0,1);
    vdiastolica = new Vector(0,1);
    vpulso = new Vector(0,1);
    vmed = new Vector(0,1);
    vECG = new Vector(0,1);
    vSPO2text = new Vector(0,1);
    vHR = new Vector(0,1);
    vRESPtext = new Vector(0,1);
}

public void graficar() {
        // INICIAL
        gc = pintarKiosko.getGraphicsContext2D();
        anchoGC = pintarKiosko.getWidth();
        altoGC = pintarKiosko.getHeight();
        
        gc.clearRect(0, 0, anchoGC, altoGC);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, anchoGC, altoGC);   
    }
    
    public void pintarImagenes() {
        // Corazón latiendo en los parámetros.
        Image eje = null;
        if (banderaImg<100) {
            eje = new Image(getClass().getResource("/imagenes/frecuencia1.png").toString(), 300, 100, true, true);
            gc.drawImage(eje, 1300, 170);
            banderaImg ++;
        } else {
            eje = new Image(getClass().getResource("/imagenes/frecuencia2.png").toString(), 300, 100, true, true);
            gc.drawImage(eje, 1300, 170);
            if (banderaImg > 200)
                
                banderaImg = 1;
            else 
                banderaImg ++;
        }
        gc.drawImage(eje, 1300, 170);
    }
    
    public void parametros() {
        
        // Asignación de parámetros para spinners 
        
        //*******************Spinner de antecedentes personales
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 0);        
        textInfartos.setValueFactory(valueFactory);
        
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 7, 0);        
        textFumaDias.setValueFactory(valueFactory);
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 7, 0);        
        textCosumeLicor.setValueFactory(valueFactory);
        
        //*******************Spinner de antecedentes familiares
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0);

        textAFDiabetes.setValueFactory(valueFactory);  
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0);
        textAFHipertension.setValueFactory(valueFactory); 
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0);
        textAFInfartos.setValueFactory(valueFactory); 
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0);
        textAFAC.setValueFactory(valueFactory); 
        
        
        // Asignación de parámetros para desplegables       
        
        ObservableList<String> availableChoices = FXCollections.observableArrayList("5 minutos", "10 minutos", "15 minutos", "20 minutos", "25 minutos", "30 minutos"); 
        intervalo.setItems(availableChoices);
        intervalo.getSelectionModel().selectFirst();        
        
        availableChoices = FXCollections.observableArrayList("15 segundos", "30 segundos", "45 segundos", "60 segundos"); 
        duracionMuestra.setItems(availableChoices);
        duracionMuestra.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("10 segundos", "20 segundos", "30 segundos", "40 segundos", "50 segundos",  "60 segundos"); 
        choiceIntervaloSubmuestreo.setItems(availableChoices);
        choiceIntervaloSubmuestreo.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("10 minutos", "20 minutos", "30 minutos", "1 hora", "1 hora, 30 minutos", "2 horas"); 
        duracionExamen.setItems(availableChoices);
        duracionExamen.getSelectionModel().selectFirst();
        
        //*******************Desplegables de datos personales
       
        availableChoices = FXCollections.observableArrayList("Femenino", "Masculino"); 
        textGenero.setItems(availableChoices);
        textGenero.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("Paciente", "Administrador", "Médico"); 
        textTipoUsuario.setItems(availableChoices);
        textTipoUsuario.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("Urbana", "Rural"); 
        textZona.setItems(availableChoices);
        textZona.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("O+", "0-", "A+", "A-", "B+", "B-", "AB+", "AB-"); 
        grupoSanguineo.setItems(availableChoices);
        grupoSanguineo.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList( "Blanca", "Indígena", "Afroamericana", "Mulato", "Mestizo", "Amerindia o nativo de Alaska", "Indioasiática", "China", "Filipina",
                "Japonesa", "Coreana", "Vietnamita");
        raza.setItems(availableChoices);
        raza.getSelectionModel().selectFirst();
        
        //*******************Desplegables de antecedentes personales
        availableChoices = FXCollections.observableArrayList("No", "Tipo 1", "Tipo 2");        
        textDiabetes.setItems(availableChoices);
        textDiabetes.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("No", "Primaria", "Secundaria");    
        textHipertension.setItems(availableChoices);
        textHipertension.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("No", "Sí");        
        textConviveConFumadores.setItems(availableChoices);
        textConviveConFumadores.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("0 a 30 minutos", "30 a 60 miutos", "1 a 3 horas", "Más de 3 horas");        
        textActividadFisicaMinutos.setItems(availableChoices);
        textActividadFisicaMinutos.getSelectionModel().selectFirst();
        
        //*******************Desplegables del afinamiento
        availableChoices = FXCollections.observableArrayList("Derecho", "Izquierdo");  
        brazoAfinamiento.setItems(availableChoices);
        brazoAfinamiento.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("Sentado", "De pie", "Acostado");  
        posicionAfinamiento.setItems(availableChoices);
        posicionAfinamiento.getSelectionModel().selectFirst();
        
        availableChoices = FXCollections.observableArrayList("Mañana", "Tarde", "Noche");  
        jornadaAfinamiento.setItems(availableChoices);
        jornadaAfinamiento.getSelectionModel().selectFirst();
        
        // INICIAL
        gc.clearRect(1280+1, 0+1, 600-2, 615-2);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(1280, 0, 600, 615);
        
        gc.clearRect(1580+1, 0+1, 5-2, 615-2);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(1580, 0, 5, 615);
        Image eje = null;
        eje = new Image(getClass().getResource("/imagenes/spo22.png").toString(), 100, 100, true, true);
        gc.drawImage(eje, 1800, 350);
        eje = new Image(getClass().getResource("/imagenes/co2.png").toString(), 60, 60, true, true);
        gc.drawImage(eje, 1800, 500);
       
        pintarHR(0);
        pintarSPO2(0, 0);
        pintarRespiracion(0);
        //pintarImagenes();
        pintarPresion(0, 0, 0, 0);        
    }
    
        public void asignarStaticParameters(int hr, int respRate, int spo2Oxi, int spo2Hr, int presRate, int presDias, int presMed, int presSist) {       
        
        //Se guardan los datos en el vector para luego ser almacenados en BD 
        if(almacenarMedicion)
        {
            vsistolica.add(presSist);
            vdiastolica.add(presDias);
            vpulso.add(presRate);
            vmed.add(presMed);
            vSPO2text.add(spo2Oxi);
            vHR.add(spo2Hr);
            vRESPtext.add(respRate);
            vECG.add(hr);
        }
        
        //Se pintan los datos    
        pintarHR(hr);
        pintarSPO2(spo2Oxi, spo2Hr);
        pintarRespiracion(respRate);
        pintarPresion(presRate, presDias, presMed, presSist);  
    }        
        
    
    public void pintarHR(int hr) {
           if (hr < 1 || hr > 999) {
               ecgTextField.setText("---");               
           }else{
               ecgTextField.setText(Integer.toString(hr));
           }        
    }
    
    public void pintarSPO2(int spo2Oxi, int spo2Hr) {
        if (spo2Oxi < 1 || spo2Oxi > 150) {
            spo2OxiTextField.setText("---");
        } else {
            spo2OxiTextField.setText(Integer.toString(spo2Oxi));
        }
        if (spo2Hr < 1 || spo2Hr > 999) {
            spo2HrTextField.setText("---");
        } else {
            spo2HrTextField.setText(Integer.toString(spo2Hr));
        }
    }
    
    public void pintarRespiracion(int respRate) {
        if (respRate== 0 || respRate>1000)
        {
            respTextField.setText("---");              
            
        }else
        {
            respTextField.setText(Integer.toString(respRate));
        }        
    }
    
    public void pintarPresion(int presRate, int presDias, int presMed, int presSist) {
        gc.clearRect(1595+1, 60+1, 300-2, 110-2);
        gc.setFill(Color.ALICEBLUE);
        gc.fillRect(1595, 60, 300, 110);        
        gc.setFill(Color.BLACK);
        Font fontLarge = Font.font("Verdana", FontWeight.BOLD, 20);
        gc.setFont(fontLarge);
        gc.fillText("PRESIÓN", 1700, 50);
        fontLarge = Font.font("Verdana", FontWeight.BOLD, 10);
        gc.setFont(fontLarge);
        gc.fillText("Sistólica", 1600, 90);
        gc.fillText("Diastólica", 1600, 150);
        gc.fillText("Pulso", 1750, 90);
        gc.fillText("Med", 1750, 150);
        fontLarge = Font.font("Verdana", FontWeight.BOLD, 34);
        gc.setFont(fontLarge);
        if (presSist < 1 || presSist > 999) {
            gc.fillText("---", 1660, 100);            
            Platform.runLater(new Runnable(){
                @Override
                public void run() {                    
                       presionImprimir.setText("---/---");
                }
            });
        } else {
            gc.fillText(Integer.toString(presSist), 1660, 100);
            Platform.runLater(new Runnable(){
                @Override
                public void run() {                    
                       presionImprimir.setText(presSist + "/" + presDias);
                       setPresiones(presSist, presDias);                       
                       tomarPresion.setText("Tomar presión");  
                }
            });
        }
        if (presDias < 1 || presDias > 999) {
            gc.fillText("---", 1660, 160);
        } else {
            gc.fillText(Integer.toString(presDias), 1660, 160);
        }
        if (presRate < 1 || presRate > 999) {
            gc.fillText("---", 1800, 100);
        } else {
            gc.fillText(Integer.toString(presRate), 1800, 100);
        }
        if (presMed < 1 || presMed > 999) {
            gc.fillText("---", 1800, 160);
        } else {
            gc.fillText(Integer.toString(presMed), 1800, 160);
        }
        
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
    }
    
    public void initChartSignals(){
    
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        //xAxis.setUpperBound(MAX_DATA_POINTS_SPO2);
        xAxis.setUpperBound(MAX_DATA_POINTS_ECG);
        //xAxis.setTickUnit(MAX_DATA_POINTS_SPO2/10);
        xAxis.setTickUnit(MAX_DATA_POINTS_ECG/10);
     
        xAxis_2.setForceZeroInRange(false);
        xAxis_2.setAutoRanging(false);
        xAxis_2.setLowerBound(0);
        xAxis_2.setUpperBound(MAX_DATA_POINTS_ECG);
        xAxis_2.setTickUnit(MAX_DATA_POINTS_ECG/10);
        
        xAxis_3.setForceZeroInRange(false);
        xAxis_3.setAutoRanging(false);
        xAxis_3.setLowerBound(0);
        xAxis_3.setUpperBound(MAX_DATA_POINTS_ECG);
        xAxis_3.setTickUnit(MAX_DATA_POINTS_ECG/10);
        
        xAxis_4.setForceZeroInRange(false);
        xAxis_4.setAutoRanging(false);
        xAxis_4.setLowerBound(0);
        xAxis_4.setUpperBound(MAX_DATA_POINTS_RESP);
        xAxis_4.setTickUnit(MAX_DATA_POINTS_RESP/10);
        //numero de divisiones en eje Y
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(Y_MAX_SPO2);
        yAxis.setTickUnit(Y_MAX_SPO2/10);
        
        yAxis_2.setAutoRanging(false);
        yAxis_2.setLowerBound(0);
        yAxis_2.setUpperBound(Y_MAX_ECG);
        yAxis_2.setTickUnit(Y_MAX_ECG/10);
        
        yAxis_3.setAutoRanging(false);
        yAxis_3.setLowerBound(0);
        yAxis_3.setUpperBound(Y_MAX_ECG);
        yAxis_3.setTickUnit(Y_MAX_ECG/10);
        
        yAxis_4.setAutoRanging(false);
        yAxis_4.setLowerBound(0);
        yAxis_4.setUpperBound(Y_MAX_RESP);
        yAxis_4.setTickUnit(Y_MAX_RESP/10);      
        
        
        /*lc1.getYAxis().setVisible(false);
        lc1.getYAxis().setOpacity(0);
        lc1.getXAxis().setVisible(false);*/
        lc1.getXAxis().setOpacity(0);
        lc2.getYAxis().setVisible(false);
        lc2.getYAxis().setOpacity(0);
        lc2.getXAxis().setVisible(false);
        lc2.getXAxis().setOpacity(0);
        lc3.getYAxis().setVisible(false);
        lc3.getYAxis().setOpacity(0);
        lc3.getXAxis().setVisible(false);
        lc3.getXAxis().setOpacity(0);
        lc4.getYAxis().setVisible(false);
        lc4.getYAxis().setOpacity(0);
        lc4.getXAxis().setVisible(false);
        lc4.getXAxis().setOpacity(0);
        
        
        lc1.setAnimated(false);
        //lc1.setId("ondaSPO2");
        lc1.setTitle("Onda SPO2");
        
        lc2.setAnimated(false);
        //lc2.setId("ondaECG1");
        lc2.setTitle("Onda ECG CH1");
        
        /*lc2.setBorder(new Border(new BorderStroke(Color.BLACK, 
                BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));*/
        
        lc3.setAnimated(false);
        //lc3.setId("ondaECG2");
        lc3.setTitle("Onda ECG CH2");
        
        lc4.setAnimated(false);
        //lc4.setId("ondaRESP");
        lc4.setTitle("Onda RESP");

        //-- Chart Series
        series1= new XYChart.Series<>();
        series2= new XYChart.Series<>();
        series3= new XYChart.Series<>();
        series4= new XYChart.Series<>();
        //series = new AreaChart.Series<Number, Number>();
        series1.setName("Spo2");
        lc1.getData().add(series1);        
        
        
        series2.setName("ECG1");
        lc2.getData().add(series2);
        
        series3.setName("ECG2");
        lc3.getData().add(series3);
        
        series4.setName("RESP");
        lc4.getData().add(series4);

    }
    
        private class AddToQueue implements Runnable {
        public void run() {
            try {

                if (!admin.ecg1Signal.isEmpty()){
                    int auxEcg1= admin.ecg1Signal.readWave();
                    if(almacenarMedicion) vECG1.add(auxEcg1);
                     dataECG1.add(auxEcg1);
                    // System.out.println(ecg1); 
                 } else{
                     //monitor.getData();
                  }
                 if (!admin.ecg2Signal.isEmpty()){
                     int auxEcg2= admin.ecg2Signal.readWave();
                     if(almacenarMedicion) vECG2.add(auxEcg2);
                     dataECG2.add(auxEcg2);
                 } else{
                     //monitor.getData();
                 }
                 if (!admin.respSignal.isEmpty()){
                     int auxResp= admin.respSignal.readWave();
                     if(almacenarMedicion) vRESP.add(auxResp);
                     dataRESP.add(auxResp);
                 }  else{
                     //monitor.getData();
                 }
                  if (!admin.spo2Signal.isEmpty()){
                     //Con la ayuda de esta sección se guardarán los datos mostrados en pantalla dentro de los vectores.
                      
                     int auxSpo2= admin.spo2Signal.readWave();
                     if(almacenarMedicion) vSPO2.add(auxSpo2);
                     dataSPO2.add(auxSpo2);
                     
                 }else{
                      //monitor.getData();
                  }
                       Thread.sleep(7);                
             
              executor.execute(this);             
                
            } catch (InterruptedException ex) {
                //Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Excepción controlada, ejecución terminada durante un Sleep");
            }
        }
    }

private class QueueParametros implements Runnable {
        public void run() {
        try {
            asignarStaticParameters(admin.staticParameters.readHr(),
                    + admin.staticParameters.readResp(),
                    + admin.staticParameters.readSpo2Oxi(),
                    + admin.staticParameters.readSpo2Hr(),
                    + admin.staticParameters.readPresR(),
                    + admin.staticParameters.readPresDias(),
                    + admin.staticParameters.readPresMed(),
                    + admin.staticParameters.readPresSist());
                    
            Thread.sleep(500);
            executor.execute(this);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
}
       private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override public void handle(long now) {
                addDataToSeries();
                addDataToSeries_2();
                addDataToSeries_3();
                addDataToSeries_4();
            }
        }.start();
    }
//Saca los datos de las queue y los agrega al chart spo2
//El for esta hasta 20 porque dio mejor performance
    private void addDataToSeries() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataSPO2.isEmpty()) break;
            series1.getData().add(new XYChart.Data<>(xSeriesData_spo2++, dataSPO2.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series1.getData().size() > MAX_DATA_POINTS_SPO2) {
            series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS_SPO2);
        }
        // update 
        xAxis.setLowerBound(xSeriesData_spo2-MAX_DATA_POINTS_SPO2);
        xAxis.setUpperBound(xSeriesData_spo2-1);
    }
    //Saca los datos de las queue y los agrega al chart ecg1
    private void addDataToSeries_2() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataECG1.isEmpty()) break;
            series2.getData().add(new XYChart.Data<>(xSeriesData_ecg1++, dataECG1.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series2.getData().size() > MAX_DATA_POINTS_ECG) {
            series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS_ECG);
        }
        // update 
        xAxis_2.setLowerBound(xSeriesData_ecg1-MAX_DATA_POINTS_ECG);
        xAxis_2.setUpperBound(xSeriesData_ecg1-1);
    }
    //Saca los datos de las queue y los agrega al chart ecg2
    private void addDataToSeries_3() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataECG2.isEmpty()) break;
            series3.getData().add(new XYChart.Data<>(xSeriesData_ecg2++, dataECG2.remove()));

        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series3.getData().size() > MAX_DATA_POINTS_ECG) {
            series3.getData().remove(0, series3.getData().size() - MAX_DATA_POINTS_ECG);
        }
        // update 
        xAxis_3.setLowerBound(xSeriesData_ecg2-MAX_DATA_POINTS_ECG);
        xAxis_3.setUpperBound(xSeriesData_ecg2-1);
    }
    //Saca los datos de las queue y los agrega al chart resp
    private void addDataToSeries_4() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataRESP.isEmpty()) break;
            series4.getData().add(new XYChart.Data<>(xSeriesData_resp++, dataRESP.remove()));

        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series4.getData().size() > MAX_DATA_POINTS_RESP) {
            series4.getData().remove(0, series4.getData().size() - MAX_DATA_POINTS_RESP);
        }
        // update 
        xAxis_4.setLowerBound(xSeriesData_resp-MAX_DATA_POINTS_RESP);
        xAxis_4.setUpperBound(xSeriesData_resp-1);
    }
    
    
    
    public void graficarECG1() {
        // INICIAL
        int ptoInicialECG1 = ptoInicial;
        int ptoInicialECG1_Y = 5;
//        double anchoECG1 = pintarKiosko.getWidth()/2;
        double anchoECG1 = 640;
//        double altoECG1 = pintarKiosko.getHeight()/2;
        double altoECG1 = 300;
        double mitadECG1 = (altoECG1 / 2) - ptoInicialECG1;
        Font fontLarge = Font.font("Verdana", FontWeight.BOLD, 10);
        gc.setFont(fontLarge);
        gc.clearRect(0 + 1, 0 + 1 + ptoInicialECG1_Y, anchoECG1-2, altoECG1-2);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0 + ptoInicialECG1_Y, anchoECG1, altoECG1);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);        
    }

    public void graficarECG2() {
        // INICIAL
        int ptoInicialECG2 = ptoInicial;
        int ptoInicialECG2_Y = 5;
        double anchoECG2 = 640;
        double altoECG2 = 300;
        double mitadECG2 = (altoECG2 / 2) - ptoInicialECG2;
        Font fontLarge = Font.font("Verdana", FontWeight.BOLD, 10);
        gc.setFont(fontLarge);
        gc.clearRect(anchoECG2 + 1, 0 + 1 + ptoInicialECG2_Y, anchoECG2 - 2, altoECG2 - 2);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(anchoECG2, 0 + ptoInicialECG2_Y, anchoECG2, altoECG2);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.fillText("1.4", anchoECG2 + (ptoInicialECG2 - 5), ptoInicialECG2 - 5 + ptoInicialECG2_Y );
        gc.strokeLine((anchoECG2 + ptoInicialECG2), ptoInicialECG2 + ptoInicialECG2_Y, (anchoECG2 + ptoInicialECG2), (altoECG2 - ptoInicialECG2) + ptoInicialECG2_Y);
        gc.fillText("V", anchoECG2 + 2, ptoInicialECG2_Y + (mitadECG2 - 30));
        gc.fillText("O", anchoECG2 + 2, ptoInicialECG2_Y + (mitadECG2 - 15));
        gc.fillText("L", anchoECG2 + 2, ptoInicialECG2_Y + mitadECG2);
        gc.fillText("T", anchoECG2 + 2, ptoInicialECG2_Y + (mitadECG2 + 15));
        gc.fillText("A", anchoECG2 + 2, ptoInicialECG2_Y + (mitadECG2 + 30));
        gc.fillText("J", anchoECG2 + 2, ptoInicialECG2_Y + (mitadECG2 + 45));
        gc.fillText("E", anchoECG2 + 2, ptoInicialECG2_Y + (mitadECG2 + 60));
        gc.fillText("-1.4   Segundos", anchoECG2 + (ptoInicialECG2 - 5), (altoECG2 - 5) + ptoInicialECG2_Y);

        // lines of voltage.
        double intervalo = (altoECG2 - (ptoInicialECG2 * 2)) / 14;
        for (int i = 0; i < 6; i++) {
            gc.strokeLine(anchoECG2 + (ptoInicialECG2 - 5), (mitadECG2 + ptoInicialECG2) + ((i + 1) * intervalo) + ptoInicialECG2_Y, anchoECG2 + (ptoInicialECG2 + 5), (mitadECG2 + ptoInicialECG2) + ((i + 1) * intervalo) + ptoInicialECG2_Y);
            gc.strokeLine(anchoECG2 + (ptoInicialECG2 - 5), (mitadECG2 + ptoInicialECG2) - ((i + 1) * intervalo) + ptoInicialECG2_Y, anchoECG2 + (ptoInicialECG2 + 5), (mitadECG2 + ptoInicialECG2) - ((i + 1) * intervalo) + ptoInicialECG2_Y);
        }
    }

    /*public void graficarSPO2() {
        // INICIAL
        int ptoInicialSPO2 = ptoInicial;
        int ptoInicialSPO2_Y = 10;
        double anchoSPO2 = 640;
        double altoSPO2 = 300;
        double mitadSPO2 = (altoSPO2 / 2) - ptoInicialSPO2;
        Font fontLarge = Font.font("Verdana", FontWeight.BOLD, 10);
        gc.setFont(fontLarge);
        gc.clearRect(0 + 1, altoSPO2 + 1 + ptoInicialSPO2_Y, anchoSPO2 - 2, altoSPO2 - 2);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, altoSPO2 + ptoInicialSPO2_Y, anchoSPO2, altoSPO2);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.fillText("256", (ptoInicialSPO2 - 5), altoSPO2 + (ptoInicialSPO2 - 5) + ptoInicialSPO2_Y);
        gc.strokeLine(ptoInicialSPO2, altoSPO2 + ptoInicialSPO2 + ptoInicialSPO2_Y, ptoInicialSPO2, (altoSPO2 + (altoSPO2 - ptoInicialSPO2)) + ptoInicialSPO2_Y);
        gc.fillText("S", 2, (altoSPO2 + ptoInicialSPO2_Y + mitadSPO2));
        gc.fillText("P", 2, (altoSPO2 + ptoInicialSPO2_Y + (mitadSPO2 + 20)));
        gc.fillText("O", 2, (altoSPO2 + ptoInicialSPO2_Y + (mitadSPO2 + 40)));
        gc.fillText("2", 2, (altoSPO2 + ptoInicialSPO2_Y + (mitadSPO2 + 60)));
        gc.fillText("0", (ptoInicialSPO2 - 5), altoSPO2 + (altoSPO2 - 5) + ptoInicialSPO2_Y);

        // lines of voltage.
        double intervalo = (altoSPO2 - (ptoInicialSPO2 * 2)) / 14;
        for (int i = 0; i < 6; i++) {
            gc.strokeLine((ptoInicialSPO2 - 5), altoSPO2 + (mitadSPO2 + ptoInicialSPO2) + ((i + 1) * intervalo) + ptoInicialSPO2_Y, (ptoInicialSPO2 + 5), altoSPO2 + (mitadSPO2 + ptoInicialSPO2) + ((i + 1) * intervalo) + ptoInicialSPO2_Y);
            gc.strokeLine((ptoInicialSPO2 - 5), altoSPO2 + (mitadSPO2 + ptoInicialSPO2) - ((i + 1) * intervalo) + ptoInicialSPO2_Y, (ptoInicialSPO2 + 5), altoSPO2 + (mitadSPO2 + ptoInicialSPO2) - ((i + 1) * intervalo) + ptoInicialSPO2_Y);
        }*/
    //}
    
    
    /*public void graficarRESP() {
        // INICIAL
        int ptoInicialRESP = ptoInicial;
        int ptoInicialRESP_Y = 10;
        double anchoRESP = 640;
        double altoRESP = 300;
        double mitadRESP = (altoRESP / 2) - ptoInicialRESP;
        Font fontLarge = Font.font("Verdana", FontWeight.BOLD, 10);
        gc.setFont(fontLarge);
        gc.clearRect(anchoRESP + 1, altoRESP + 1 + ptoInicialRESP_Y, anchoRESP - 2, altoRESP - 2);
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(anchoRESP, altoRESP+ptoInicialRESP_Y, anchoRESP, altoRESP);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);;
        gc.fillText("256", anchoRESP + (ptoInicialRESP - 5), altoRESP + (ptoInicialRESP - 5) + ptoInicialRESP_Y);
        gc.strokeLine(anchoRESP+ptoInicialRESP, altoRESP+ptoInicialRESP+ptoInicialRESP_Y, anchoRESP+ptoInicialRESP, altoRESP + (altoRESP - ptoInicialRESP)+ ptoInicialRESP_Y);
        gc.fillText("R", anchoRESP+2, altoRESP+(mitadRESP - 60)+ptoInicialRESP_Y);
        gc.fillText("E", anchoRESP+2, altoRESP+(mitadRESP - 45)+ptoInicialRESP_Y);
        gc.fillText("S", anchoRESP+2, altoRESP+(mitadRESP - 30)+ptoInicialRESP_Y);
        gc.fillText("P", anchoRESP+2, altoRESP+(mitadRESP - 15)+ptoInicialRESP_Y);
        gc.fillText("I", anchoRESP+2, altoRESP+(mitadRESP + 0)+ptoInicialRESP_Y);
        gc.fillText("R", anchoRESP+2, altoRESP+(mitadRESP + 15)+ptoInicialRESP_Y);
        gc.fillText("A", anchoRESP+2, altoRESP+(mitadRESP + 30)+ptoInicialRESP_Y);
        gc.fillText("C", anchoRESP+2, altoRESP+(mitadRESP + 45)+ptoInicialRESP_Y);
        gc.fillText("I", anchoRESP+2, altoRESP+(mitadRESP + 60)+ptoInicialRESP_Y);
        gc.fillText("Ó", anchoRESP+2, altoRESP+(mitadRESP + 75)+ptoInicialRESP_Y);
        gc.fillText("N", anchoRESP+2, altoRESP+(mitadRESP + 90)+ptoInicialRESP_Y);
        gc.fillText("0", anchoRESP+(ptoInicialRESP - 5), altoRESP+(altoRESP - 5)+ptoInicialRESP_Y);
        // lines of voltage.
        double intervalo = (altoRESP - (ptoInicialRESP * 2)) / 14;
        for (int i = 0; i < 6; i++) {
            gc.strokeLine(anchoRESP+(ptoInicialRESP - 5), altoRESP+((mitadRESP + ptoInicialRESP) + ((i + 1) * intervalo)) + ptoInicialRESP_Y, anchoRESP+(ptoInicialRESP + 5), altoRESP+((mitadRESP + ptoInicialRESP) + ((i + 1) * intervalo)) + ptoInicialRESP_Y);
            gc.strokeLine(anchoRESP+(ptoInicialRESP - 5), altoRESP+((mitadRESP + ptoInicialRESP) - ((i + 1) * intervalo)) + ptoInicialRESP_Y, anchoRESP+(ptoInicialRESP + 5), altoRESP+((mitadRESP + ptoInicialRESP) - ((i + 1) * intervalo)) + ptoInicialRESP_Y);
        }
    }*/

public void customResize(TableView<?> view) {

        AtomicLong width = new AtomicLong();
        view.getColumns().forEach(col -> {
            width.addAndGet((long) col.getWidth());
        });
        double tableWidth = view.getWidth();

        if (tableWidth > width.get()) {
            view.getColumns().forEach(col -> {
                col.setPrefWidth(col.getWidth()+((tableWidth-width.get())/view.getColumns().size()));
            });
        }
    }

public void reproducirSPO2()
{
    
        for (int i = 0; i < 3; i++) { //-- add 20 numbers to the plot+
           
            if (reprodSPO2.isEmpty()) break;            
            series1.getData().add(new XYChart.Data<>(xSeriesData_spo2++, reprodSPO2.remove()));
        }
        while(pausar)
        {
            //No hace nada mientras esté pausado
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series1.getData().size() > MAX_DATA_POINTS_SPO2) {
            series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS_SPO2);
        }
        // update 
        xAxis.setLowerBound(xSeriesData_spo2-MAX_DATA_POINTS_SPO2);
        xAxis.setUpperBound(xSeriesData_spo2-1);
    
}

public void reproducirECG1()
{
    
        for (int i = 0; i < 3; i++) { //-- add 20 numbers to the plot+
            if (reprodECG1.isEmpty()) break;            
            series2.getData().add(new XYChart.Data<>(xSeriesData_ecg1++, reprodECG1.remove()));

        }
        while(pausar)
        {
            //No hace nada mientras esté pausado
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series2.getData().size() > MAX_DATA_POINTS_ECG) {
            series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS_ECG);
        }
        // update 
        xAxis_2.setLowerBound(xSeriesData_ecg1-MAX_DATA_POINTS_ECG);
        xAxis_2.setUpperBound(xSeriesData_ecg1-1);    
}

public void reproducirECG2()
{
    
        for (int i = 0; i < 3; i++) { //-- add 20 numbers to the plot+
            if (reprodECG2.isEmpty()) break;
            series3.getData().add(new XYChart.Data<>(xSeriesData_ecg2++, reprodECG2.remove()));

        }
        while(pausar)
        {
            //No hace nada mientras esté pausado
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series3.getData().size() > MAX_DATA_POINTS_ECG) {
            series3.getData().remove(0, series3.getData().size() - MAX_DATA_POINTS_ECG);
        }
        // update 
        xAxis_3.setLowerBound(xSeriesData_ecg2-MAX_DATA_POINTS_ECG);
        xAxis_3.setUpperBound(xSeriesData_ecg2-1);   
}

public void reproducirRESP()
{    
        for (int i = 0; i < 3; i++) { //-- add 20 numbers to the plot+
            if (reprodRESP.isEmpty()) break;
            series4.getData().add(new XYChart.Data<>(xSeriesData_resp++, reprodRESP.remove()));

        }
        while(pausar)
        {
            //No hace nada mientras esté pausado
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series4.getData().size() > MAX_DATA_POINTS_RESP) {
            series4.getData().remove(0, series4.getData().size() - MAX_DATA_POINTS_RESP);
        }
        // update 
        xAxis_4.setLowerBound(xSeriesData_resp-MAX_DATA_POINTS_RESP);
        xAxis_4.setUpperBound(xSeriesData_resp-1);  
}

    @FXML
    private void reproducirMedicion()
    { 
        Timer timer;
        timer = new Timer();
        if(botonReproducir.isDisable()) //Entra cuando se presiona "Detener"
        {
            timer.cancel();
            timer.purge();
            reprodSPO2.clear();
            reprodECG1.clear();
            reprodECG2.clear();
            reprodRESP.clear();            
            
            detenerReproduccion.setDisable(true);
            pausarReproduccion.setDisable(true);
            botonReproducir.setDisable(false);
            
            enableMeasure(true);
            
        }else //Entra cuando se prediona "Reproducir"
        {
            detenerReproduccion.setDisable(false);
            pausarReproduccion.setDisable(false);
            botonReproducir.setDisable(true);
            
            enableMeasure(false);

            pausar= false; //Ya que se detiene la reproducción, ya no se puede pausar, si estaba en pausa, se cancela.
            pausarReproduccion.setText("Pausar");            
            
            
            Medicion medicionReproducir= this.programaPrincipal.getMedicionReproducir();            
            String SPO2= medicionReproducir.getOndaSPO2().substring(1, medicionReproducir.getOndaSPO2().length()-1);
            StringTokenizer tokens= new StringTokenizer(SPO2, ", ");
            while(tokens.hasMoreTokens())
            {
                reprodSPO2.add(Integer.parseInt(tokens.nextToken()));
            }
            //Ahora para ECG1
            String ECG1= medicionReproducir.getOndaECG1().substring(1, medicionReproducir.getOndaECG1().length()-1);
            tokens= new StringTokenizer(ECG1, ", ");
            while(tokens.hasMoreTokens())
            {
                reprodECG1.add(Integer.parseInt(tokens.nextToken()));
            }
            
            String ECG2= medicionReproducir.getOndaECG2().substring(1, medicionReproducir.getOndaECG2().length()-1);
            tokens= new StringTokenizer(ECG2, ", ");
            while(tokens.hasMoreTokens())
            {
                reprodECG2.add(Integer.parseInt(tokens.nextToken()));
            } 
            
            String RESP= medicionReproducir.getOndaRESP().substring(1, medicionReproducir.getOndaRESP().length()-1);
            tokens= new StringTokenizer(RESP, ", ");
            
            while(tokens.hasMoreTokens())
            {
                reprodRESP.add(Integer.parseInt(tokens.nextToken()));                
            }             
            
            
            TimerTask task = new TimerTask() 
            {
            
                @Override
                public void run()
                {
                    reproducirSPO2();
                    reproducirECG1();
                    reproducirECG2();
                    reproducirRESP();
                    try 
                    {
                        if(reprodSPO2.isEmpty() && reprodECG1.isEmpty() && reprodECG2.isEmpty() && reprodRESP.isEmpty())
                        {
                            reprodSPO2.clear();
                            reprodECG1.clear();
                            reprodECG2.clear();
                            reprodRESP.clear();
                            enableMeasure(true);
                            detenerReproduccion.setDisable(true);
                            pausarReproduccion.setDisable(true);
                            timer.cancel();
                            timer.purge();
                        }
                    } catch (Throwable ex) 
                    {
                        Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            // Empezamos dentro de 0ms y luego lanzamos la tarea cada 30ms
            timer.schedule(task, 0, 30);
        }  
 
    }                
    
    //Variable necesaria para este método
    private float pesoAlmacenar= 0;
    private float grasa= 0;
    private float porcentajeAgua= 0;
    private float masaMuscular= 0;
    private float imc= 0;
    
    public void actualizarPeso()
    {        
        float peso= admin.staticParameters.readWeight();        
        pesoAlmacenar= peso;
        
        if(peso<=0.0)
        {
            pesoImprimir.setText("---");
        }else
        {
            grasa= admin.staticParameters.readBodyFat();
            porcentajeAgua= admin.staticParameters.readWaterPercent();
            masaMuscular= admin.staticParameters.readMuscleMass();
            imc = (float) (peso/(Math.pow(pacienteCargado.getEstatura()/100, 2)));            
            pesoImprimir.setText(peso + "Kg");
            grasaImprimir.setText(grasa + "%");
            pAguaImprimir.setText(porcentajeAgua + "%");
            masaImprimir.setText("IMM= " + masaMuscular);  
            iMCImprimir.setText(String.valueOf(imc));            
        }
    }
    
    @FXML
    public void tomarPeso()
    {        
        //Cambiar los siguientes datos para que se tomen de la base de datos.
        admin.solicitarTanita("20", "masculino", "27", String.valueOf((int) pacienteCargado.getEstatura()), "regular");//ID:17-65534, genero: masculino-femenino, edad , estatura: en cm, actividad: sedentario, regular o deportista.
        
        float weight= admin.staticParameters.readWeight();
        
        AlterarInterfaz alterador= new AlterarInterfaz(admin, this);
        alterador.setOpcion(2); //Para modificar presión
        alterador.start();
        guardarAfinamiento.setDisable(false);       
    }
    
    private int presSistolica= 0;
    private int presDiastolica= 0;
    
    public void setPresiones(int sist, int diast)
    {
        presSistolica= sist;
        presDiastolica= diast;        
    }
    
    public void actualizarPresion(int dias, int sis)
    {        
        /*int diastolica= dias;
        int sistolica= sis;        
        if(diastolica<=0)
        {
            presionImprimir.setText("---/---");
        }else
        {
            presSistolica= sistolica;
            presDiastolica= diastolica; 
            presionImprimir.setText(sistolica+ "/" + diastolica);
            Platform.runLater(new Runnable(){
                @Override
                public void run() {                    
                       tomarPresion.setText("Tomar presión");
                }
            });
            //admin.enviarComando("stopPressure", 0);//Se resetea la toma de presión.
        }*/
        
    }
    
    public void publicarPresion(String presion)
    {
        presionImprimir.setText(presion);
    }
       
    @FXML    
    public void tomarPresion()
    {   
        if(tomarPresion.getText().equals("Tomar presión"))
        {
            tomarPresion.setText("Detener"); 
            /*Timer timerIniciar;
            timerIniciar = new Timer();

            TimerTask taskIniciar = new TimerTask() //Este hilo inicia la toma de presión
            {

                @Override
                public void run()
                {*/
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(tomarPresion.getText().equals("Detener")) admin.enviarComando("manualPressure", 0);                    
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }

            if(tomarPresion.getText().equals("Detener")) admin.enviarComando("stopPressure", 0);
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(tomarPresion.getText().equals("Detener")) admin.enviarComando("startPressure", 0);
                    /*try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                //}
            //};
            // Empezamos dentro de 0s 
            //timerIniciar.schedule(taskIniciar, 0);
            alterador= new AlterarInterfaz(admin, this);
            alterador.setOpcion(1); //Para modificar presión
            alterador.iniciarProcesos(); //Se habilita el inicio de los procesos.
            alterador.start();
            guardarAfinamiento.setDisable(false);
        }else
        { 
            try {               
                Thread.sleep(10);
                
            }catch (InterruptedException ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            } 
            alterador.detenerProcesos();
            admin.enviarComando("stopPressure", 0); 
            tomarPresion.setText("Tomar presión");  
        }
    }
    
    //este método permite habilitar o inhabilitar los botones de la sección de mediciones.
    public void enableMeasure(boolean value)
    {
        value= !value;
        btnIniciarSenales.setDisable(value);
        iniciarPersonalizada.setDisable(value);
        intervalo.setDisable(value);
        duracionMuestra.setDisable(value);        
        duracionExamen.setDisable(value);
        choiceIntervaloSubmuestreo.setDisable(value);
    }

    //Este método permite habilitar o inhabilitar los botones de la sección de afinamientos.
    public void enableTunning(boolean value)
    {
        value= !value;
        tomarPresion.setDisable(value);
        tomarPeso.setDisable(value);
        brazoAfinamiento.setDisable(value);
        posicionAfinamiento.setDisable(value);
        jornadaAfinamiento.setDisable(value);
        guardarAfinamiento.setDisable(value);
    }
    
    public void llenarTablaAfinamientos()
    { 
       columnaFechaAfinamiento.setCellValueFactory(new PropertyValueFactory<>("fecha"));
       columnaPesoAfinamiento.setCellValueFactory(new PropertyValueFactory<>("peso"));
       columnaPosicionAfinamiento.setCellValueFactory(new PropertyValueFactory<>("posicion"));
       columnaJornadaAfinamiento.setCellValueFactory(new PropertyValueFactory<>("jornada"));
       columnaDiastolicaAfinamiento.setCellValueFactory(new PropertyValueFactory<>("presDiastolica"));
       columnaSistolicaAfinamiento.setCellValueFactory(new PropertyValueFactory<>("presSistolica"));
       columnaGrasaAfinamiento.setCellValueFactory(new PropertyValueFactory<>("GrasaCorporal"));
       columnaAguaAfinamiento.setCellValueFactory(new PropertyValueFactory<>("PorcentajeAgua"));
       columnaMuscularAfinamiento.setCellValueFactory(new PropertyValueFactory<>("MasaMuscular"));
       columnaIMCAfinamiento.setCellValueFactory(new PropertyValueFactory<>("IMC"));
       columnaEstadoAfinamiento.setCellValueFactory(new PropertyValueFactory<>("estadoInicial"));
       columnaDetallesAfinamiento.setCellValueFactory(new PropertyValueFactory<>("Detalles"));      
       
         
       String identificacion= textIdentificacion1.getText();
       
       EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
       Query queryAfinamientoFindAll = em.createNativeQuery("SELECT * from historial_afinamiento m WHERE identificacion= '" + identificacion +"'", HistorialAfinamiento.class);
       List<HistorialAfinamiento> listAfinamientos = queryAfinamientoFindAll.getResultList(); 
       tablaAfinamientos.setItems(FXCollections.observableArrayList(listAfinamientos)); 
    }
    
    //Métodos para conexión y reconexión.
    public void conectar()
    {
       admin.ConectarTcp();
       if(admin.isConnectedTCP())
       {
           enableTunning(true);
           enableMeasure(true);
           menuConexion.setText("Desconectar");        
       }       
    }
    
    //Método para la desconexión manual
    public void desconectar()
    {
        if(admin.isConnectedTCP())
        {
            if(!admin.isTcpNull())
            {
                admin.desconectarCliente();
            }
        }       
        
        menuConexion.setText("Conectar");
        cancelarMediciones();
        try {
            validadorConexion();
        } catch (EmptyStackException ex) {
            if(apagarDesdeHilo)
            {
                System.out.println("Debería cambiar el texto");
                apagarDesdeHilo= false;
                btnIniciarSenales.setText("Iniciar medición simple");                
            }
            detenerLecturaSenales();
            mensajeDesconexion();
            
        }
    }
    
    //Método que cancela una medición en curso (la medición simple)
    public void cancelarMediciones()
    {
       if(btnIniciarSenales.getText().equals("Detener medición"))
       {           
           medicionSimple(); //Se cancela la medición simple en caso de desconexión.
       }
       if(!banderaMedicion)
       {
           iniciarMedicionPersonalizada();
       }
    }
    
    //Método escucha a la acción del usuario de conectar o desconectar.
    @FXML
    public void escuchaDesconexion()
    { 
        if(menuConexion.getText().equals("Conectar"))
        {             
            conectar();
            btnIniciarSenales.setText("Iniciar medición simple");
            iniciarPersonalizada.setText("Iniciar medición personalizada");
            
            if(admin.isConnectedTCP())
            {                
                Alert dialogoAlerta= new Alert(AlertType.INFORMATION);
                dialogoAlerta.setTitle("Conexión exitosa");
                dialogoAlerta.setHeaderText(null);
                dialogoAlerta.setContentText("El dispositivo se ha conectado correctamente");
                iniciarLecturaSenales();
                dialogoAlerta.initStyle(StageStyle.UTILITY);
                dialogoAlerta.showAndWait();                
            }   
            
        }else
        {
            desconectar();
            enableTunning(false);
            enableMeasure(false);
        }
    }
    
    
    /**
     * Este método está en un loop constante para detectar si se corta la conexión con el dispositivo.
     */
    public void validadorConexion() throws EmptyStackException 
    {
        Timer timerIniciar;
        timerIniciar = new Timer();
        
        TimerTask taskIniciar = new TimerTask() 
        {
            @Override
            public void run() throws EmptyStackException
            {
                //El siguiente es un while del que no sale si la conexión continúa.
                while(!admin.isTcpNull())
                {
                    //No hace nada mientras haya conexion.                    
                }
                //En caso de salir del while, se informa que ya no hay conexión.
                
                //Se cancelan los procesos
                
                //El afinamiento:
                try
                {
                    alterador.detenerProcesos();
                }catch(java.lang.NullPointerException e)
                {
                    System.out.println("Excepción controlada, no hay alterador de interfaz construido.");
                }
                
                
                //---------Primero se deben cancelar los procesos críticos.
                //admin.desconectarCliente();                
                menuConexion.setText("Conectar");   
                
                //Reestablecimiento de botones al sufrir desconexión
                //tomarPresion.setText("Tomar presión");                
                
                //Luego se procede a informar la desconexión.
                System.out.println("Se ha desconectado el dispositivo");                            
                //mensajeDesconexion();   
                detenerLecturaSenales();
            }
        };
        // Empezamos dentro de 10s 
        timerIniciar.schedule(taskIniciar, 0);
    }
    
    public void mensajePersonalizado(String mensaje, String titulo)
    {
        Alert dialogoAlerta= new Alert(AlertType.INFORMATION);
        dialogoAlerta.setTitle(titulo);
        dialogoAlerta.setHeaderText(null);
        dialogoAlerta.setContentText(mensaje);
        dialogoAlerta.initStyle(StageStyle.UTILITY);
        dialogoAlerta.showAndWait(); 
    }
    
    public void mensajeDesconexion()
    {
        Alert dialogoAlerta= new Alert(AlertType.INFORMATION);
        dialogoAlerta.setTitle("Dispositivo desconectado");
        dialogoAlerta.setHeaderText(null);
        dialogoAlerta.setContentText("El dispositivo se ha desconectado, por favor conéctelo y\n"
                                    + "reestablezca la conexión a través del menú Conexión-> Conectar");
        dialogoAlerta.initStyle(StageStyle.UTILITY);
        dialogoAlerta.showAndWait();  
    }
    
    public String obtenerDatos()
    {
        String resultado="";
        String identificacion= textIdentificacion1.getText();
        EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
        
        //Consulta del paciente
        Query queryPacienteFindOne = em.createNativeQuery("SELECT * from pacientes p WHERE identificacion= '" + identificacion +"'", Pacientes.class);
        List<Pacientes> listPaciente = queryPacienteFindOne.getResultList();
        Pacientes paciente= listPaciente.get(0);
        
        //Consulta de los antecedentes personales
        
        Query queryPersonalesFindOne = em.createNativeQuery("SELECT * from antecedentespersonales a WHERE identificacion= '" + identificacion +"'", Antecedentespersonales.class);
        List<Antecedentespersonales> listPersonales = queryPersonalesFindOne.getResultList();
        Antecedentespersonales personales= listPersonales.get(0);
        
        //Consulta de los antecedentes familiares
        Query queryFamiliaresFindOne = em.createNativeQuery("SELECT * from antecedentesfamiliares a WHERE identificacion= '" + identificacion +"'", Antecedentesfamiliares.class);
        List<Antecedentesfamiliares> listFamiliares = queryFamiliaresFindOne.getResultList();
        Antecedentesfamiliares familiares= listFamiliares.get(0);
        
        //Toma de datos de paciente
        String tipoId= paciente.getPacientesPK().getTipoid() + ";";
        String id= paciente.getPacientesPK().getIdentificacion() + ";";
        String nombre= paciente.getNombre1() + " " + paciente.getNombre2() + ";";
        String apellido= paciente.getApellido1() + " " + paciente.getApellido2() + ";";
        String administradora= paciente.getAdministradora() + ";";
        String estatura= paciente.getEstatura() + ";";
        
        //Toma de datos de antecedentes personales
        String medicamentos1= personales.getMedicamentospermanentes1() + ";";
        String medicamentos2= personales.getMedicamentospermanentes2() + ";";
        String medicamentos3= personales.getMedicamentospermanentes3() + ";";
        String medicamentos4= personales.getMedicamentospermanentes4() + ";";
        String medicamentos5= personales.getMedicamentospermanentes5() + ";";
        String diabetes= personales.getDiabetes() + ";";
        String hipertension= personales.getHipertension() + ";";
        String infartos= personales.getInfartos()+ ";";
        String fuma= personales.getFumadias() + ";";
        String conviveFumadores= personales.getConviveconfumadores() + ";";
        String actividadFisica= personales.getActividadfisica() + ";";
        String consumeLicor= personales.getCosumelicor() + ";";
        
        //Toma de datos de antecedentes familiares
        String familiaresDiabetes= familiares.getDiabetes() + ";";
        String familiaresHipertension= familiares.getHipertension() + ";";
        String accidentes= familiares.getAcv() + ";";
        
        resultado= tipoId + id+ nombre + apellido + administradora+ "contactoFamiliar(pendiente);" + estatura + 
                medicamentos1 + medicamentos2 + medicamentos3 + medicamentos4 + medicamentos5 + diabetes + hipertension + infartos + fuma + conviveFumadores +
                actividadFisica + consumeLicor + familiaresDiabetes + familiaresHipertension + accidentes + "|";                
        
        return resultado;
    }
    
    public String leerTarjeta()
    {
        String resultado="";
        SmartCard smartCard= new SmartCard();        
        try 
        {       
            resultado= smartCard.readFromCard();
        } catch (Exception ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }
    
    @FXML
    public void iniciarDesdeTarjeta()
    {
        String datosTarjeta= leerTarjeta();
        if(!datosTarjeta.equals("Error"))
        {
            StringTokenizer tokenizer= new StringTokenizer(datosTarjeta, ";");
            tokenizer.nextToken();
            textIdentificacion1.setText(tokenizer.nextToken());
            buscarPaciente();
        }else
        {
            System.err.println("Lanzar mensaje de error");
        }
    }

    @FXML
    public void escribirEnTarjeta()
    {
        SmartCard smartCard= new SmartCard();        
        try 
        {       
            smartCard.writeOnCard(obtenerDatos());            
        } catch (Exception ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void actualizarTarjeta()
    {
        SmartCard smartCard= new SmartCard();        
        try 
        {       
            smartCard.updateCard(obtenerDatos());            
        } catch (Exception ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       
    
    @FXML
    public void pausarReproduccion()
    {
        if(pausarReproduccion.getText().equals("Pausar"))
        {
            pausarReproduccion.setText("Reanudar");
            pausar= !pausar;
        }else
        {
            pausarReproduccion.setText("Pausar");
            pausar= !pausar;
        }
    }
    
    public void indicador(boolean activar, String mensaje)
    {
        if(activar)
        {
            indicadorPersonalizada.setText(mensaje);
            indicadorPersonalizada.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        }else
        {
           indicadorPersonalizada.setText("");
           indicadorPersonalizada.setBackground(null); 
        }
    } 
    
    public void indicadorMuestreo(boolean activar, String mensaje)
    {
        if(activar)
        {
            indicadorMuestra.setText(mensaje);
            indicadorMuestra.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        }else
        {
            indicadorMuestra.setText("");
            indicadorMuestra.setBackground(null);
        }
    }
    
    //Contador necesario para guardar los segundos del submuestreo
    private static int contadorSubmuestreo= 1;
    public void actualizarVectoresSubmuestreo()
    {   
        int tiempo= contadorSubmuestreo*obtenerSubmuestreo();
        SubmuestreoSistolica.add(admin.staticParameters.readPresSist());
        SubmuestreoDiastolica.add(admin.staticParameters.readPresDias());
        SubmuestreoPulso.add(admin.staticParameters.readPresR());
        SubmuestreoMed.add(admin.staticParameters.readPresMed());
        if(admin.staticParameters.readHr()==65436)
        {
            SubmuestreoECG.add(0);
        }else
        {
            SubmuestreoECG.add(admin.staticParameters.readHr());
        }        
        SubmuestreoSPO2text.add(admin.staticParameters.readSpo2Oxi());
        SubmuestreoHR.add(admin.staticParameters.readSpo2Hr());
        if(admin.staticParameters.readResp()==65436)
        {
            SubmuestreoRESPtext.add(0);
        }else
        {
            SubmuestreoRESPtext.add(admin.staticParameters.readResp());
        }        
        Submuestreominutos.add(tiempo);
        contadorSubmuestreo++;
    }
    
    public void resetVectoresSubmuestreo()
    {   
        SubmuestreoSistolica= new Vector(0,1);
        SubmuestreoDiastolica= new Vector(0,1);
        SubmuestreoPulso= new Vector(0,1);
        SubmuestreoMed= new Vector(0,1);
        SubmuestreoECG= new Vector(0,1);
        SubmuestreoSPO2text= new Vector(0,1);
        SubmuestreoHR= new Vector(0,1);
        SubmuestreoRESPtext= new Vector(0,1);
        contadorSubmuestreo= 1;
    }
}