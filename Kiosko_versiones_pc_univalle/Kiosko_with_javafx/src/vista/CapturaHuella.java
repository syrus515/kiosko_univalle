package vista;

import BD.Pacientes;
import BD.PacientesPK;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.controlsfx.dialog.Dialogs;

public class CapturaHuella  extends BorderPane {
    FlowPane imagenPane;
    FlowPane botonesPane;
    FlowPane texto;
    Button btnGuardar;
    Button btnBuscar;
    Button btnSalir;
    TextArea txtArea;
    ImageView lblImagenHuella = null;
    MenuController refController = null;
    ResultSet rs = null;
    Stage primaryStage = null;
    
    //public CapturaHuella(Stage ventanaPrincipal, Stage primaryStage, MenuController refController, ResultSet rs) {
    public CapturaHuella(Stage ventanaPrincipal, Stage primaryStage, MenuController refController) {
        this.refController = refController;
        this.primaryStage = primaryStage;
        this.rs = rs;
        lblImagenHuella = new ImageView("/imagenes/huella.jpg");
        lblImagenHuella.setFitHeight(400);
        lblImagenHuella.setFitWidth(400);
        imagenPane = new FlowPane();
        imagenPane.getChildren().add(lblImagenHuella);        
        imagenPane.setAlignment(Pos.CENTER);
        this.setTop(imagenPane);
        
        botonesPane = new FlowPane();
        btnGuardar = new Button();
        btnGuardar.setText("Guardar");
        btnGuardar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnGuardarActionPerformed(event);
                Reclutador.clear();
            }
        });
        btnBuscar = new Button();
        btnBuscar.setText("Buscar");
        btnBuscar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnBuscarActionPerformed(event);
                Reclutador.clear();
            }
        });
        btnSalir = new Button();
        btnSalir.setText("Salir");
        btnSalir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnSalirActionPerformed(event);
                Reclutador.clear();
            }
        });
        botonesPane.getChildren().add(btnGuardar);
        botonesPane.getChildren().add(btnBuscar);
        botonesPane.getChildren().add(btnSalir);
        botonesPane.setAlignment(Pos.CENTER);
        this.setCenter(botonesPane);
        
        txtArea = new TextArea();
        texto = new FlowPane();
        txtArea.appendText("");
        txtArea.setMaxSize(390, 300);
        texto.getChildren().add(txtArea);
        texto.setAlignment(Pos.CENTER);
        this.setBottom(texto);

        this.primaryStage.setTitle("Sistema de captura de huella");
        this.primaryStage.setWidth(500);
        this.primaryStage.setHeight(600);
        this.primaryStage.setResizable(false);
        this.primaryStage.initOwner(ventanaPrincipal);
        this.primaryStage.initModality(Modality.APPLICATION_MODAL);
        
        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override public void handle(WindowEvent event) {
                refController.openHuella = true;
                formWindowClosing(event);
            }
        });
        this.primaryStage.setOnShown(new EventHandler<WindowEvent>(){
            @Override public void handle(WindowEvent event) {
                formWindowOpened(event);
            }
        });

    }
    
    private void btnSalirActionPerformed(ActionEvent evt) {
        //refController.setPlantillaHuella(null);
        setTemplate(null);
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
        this.formWindowClosing(null);
        stop();
    }

    private void btnBuscarActionPerformed(ActionEvent evt) {
          identificarHuella();
    }
    
    private void btnGuardarActionPerformed(ActionEvent evt) {
        refController.setEtiquetaHuella(lblImagenHuella);
        refController.setFotoHuella(lblImagenHuella);
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
        this.formWindowClosing(null);
        setTemplate(null);
        stop();
    }
    
    private void formWindowOpened(WindowEvent evt) {
        Iniciar();
        start();
        EstadoHuellas();
        btnGuardar.setDisable(true);
        btnBuscar.setDisable(true);
        btnSalir.setFocusTraversable(false);
    }

    private void formWindowClosing(WindowEvent evt) {
        refController.openHuella = true;
        stop();
    }

    // Varible que permite iniciar el dispositivo de lector de huella conectado
    // con sus distintos metodos.
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();

    // Varible que permite establecer las capturas de la huellas, para determina sus caracteristicas
    // y poder estimar la creacion de un template de la huella para luego poder guardarla
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();

    // Esta variable tambien captura una huella del lector y crea sus caracteristcas para auntetificarla
    // o verificarla con alguna guardada en la BD
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();

    // Variable que para crear el template de la huella luego de que se hallan creado las caracteriticas
    // necesarias de la huella si no ha ocurrido ningun problema
    private DPFPTemplate template;
//    private DPFPSample sample;
    public static String TEMPLATE_PROPERTY = "template";

    protected void Iniciar() {
        Lector.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                EnviarTexto("La Huella Digital ha sido Capturada");
                ProcesarCaptura(e.getSample());
            }
        });
        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
            }
            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");
            }
        });
        Lector.addSensorListener(new DPFPSensorAdapter() {
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
            }
            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                EnviarTexto("El dedo ha sido quitado del Lector de Huella");
            }
        });
        Lector.addErrorListener(new DPFPErrorAdapter() {
            public void errorReader(final DPFPErrorEvent e) {
                EnviarTexto("Error: " + e.getError());
            }
        });
    }

    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;

    public void ProcesarCaptura(DPFPSample sample) {
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null) {
            try {
//                System.out.println("Las Caracteristicas de la Huella han sido creada");
                Reclutador.addFeatures(featuresinscripcion);// Agregar las caracteristicas de la huella a la plantilla a crear
                // Dibuja la huella dactilar capturada.
                Image image = CrearImagenHuella(sample);
                DibujarHuella(image);
                btnBuscar.setDisable(false);
            } catch (DPFPImageQualityException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                EstadoHuellas();
                // Comprueba si la plantilla se ha creado.
                switch (Reclutador.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY:	// informe de éxito y detiene  la captura de huellas
                        stop();
                        setTemplate(Reclutador.getTemplate());
                        refController.setPlantillaHuella(Reclutador.getTemplate());
                        EnviarTexto("La Plantilla de la Huella ha Sido Creada, ya puede ser Guardada.");
                        btnBuscar.setDisable(true);
                        btnGuardar.setDisable(false);
                        btnGuardar.setFocusTraversable(false);
                        break;
                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
                        Dialogs.create()
                            .title("Inscripcion de Huellas Dactilares")
                            .message("La Plantilla de la Huella no pudo ser creada, Repita el Proceso")
                            .showInformation();
                        start();
                        break;
                }
            }
        }
    }

    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose) {
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        } catch (DPFPImageQualityException ex) {
            return null;
        }
    }

    public Image CrearImagenHuella(DPFPSample sample) {
        double factor = 0.5;
        java.awt.Image tmp = DPFPGlobal.getSampleConversionFactory().createImage(sample);
        java.awt.Image tmp1 = tmp.getScaledInstance((int)(tmp.getWidth(null)*factor),(int)(tmp.getHeight(null)*factor), java.awt.Image.SCALE_DEFAULT);
        java.awt.image.BufferedImage ib2 = toBufferedImage(tmp1);
        return SwingFXUtils.toFXImage(ib2, null);
    }

    public static java.awt.image.BufferedImage toBufferedImage(java.awt.Image img) {
        if (img instanceof java.awt.image.BufferedImage) {
            return (java.awt.image.BufferedImage) img;
        }
        // Create a buffered image with transparency
        java.awt.image.BufferedImage bimage = new java.awt.image.BufferedImage(img.getWidth(null), img.getHeight(null), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        java.awt.Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }
    
    public void DibujarHuella(Image image) {
        lblImagenHuella.setImage(image);
    }

    public void EstadoHuellas() {
        EnviarTexto("Muestra de Huellas Necesarias para Guardar Template " + Reclutador.getFeaturesNeeded());
    }

    public void EnviarTexto(String string) {
        txtArea.appendText("" + string + "\n");
    }

    public void start() {
        Lector.startCapture();
        EnviarTexto("Utilizando el Lector de Huella Dactilar ");
    }

    public void stop() {
        Lector.stopCapture();
        EnviarTexto("No se está usando el Lector de Huella Dactilar ");
    }

    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
        this.template = template;
        // OjO con esta propiedad, no se que error puede ocurrir.
        // firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }
    
    /**
     * Identifica a una persona registrada por medio de su huella digital
     */
    public void identificarHuella() {
        boolean bandera = false;
        if (featuresverificacion==null) {
            Dialogs.create()
                .title("Error huella dactilar")
                .masthead("Error huella dactilar. ")
                .message("Ingresar una huella dactilar. ")
                .showError();
        } else {
            EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
            em.getTransaction().begin();
            List<Pacientes> list = em.createNamedQuery("Pacientes.findAll", Pacientes.class).getResultList();
            for (int i=0; i < list.size(); i++) {
                Pacientes obj = list.get(i);
                // Lee la plantilla de la base de datos
                byte templateBuffer[] = obj.getHuella();
                //String idEstudiante = rs.getString("idestudiante");
                PacientesPK idEstudiante = obj.getPacientesPK();
                // Crea una nueva plantilla a partir de la guardada en la base de datos
                if (templateBuffer != null) {
                    DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                    // Envia la plantilla creada al objeto contendor de Template del componente de huella digital
                    setTemplate(referenceTemplate);

                    // Compara las caracteriticas de la huella recientemente capturda con la
                    // alguna plantilla guardada en la base de datos que coincide con ese tipo
                    DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());

                    // Compara las plantilas (actual vs bd)
                    // Si encuentra correspondencia dibuja el mapa
                    // e indica el nombre de la persona que coincidió.
                    if (result.isVerified()) {
                        // Crea la imagen de los datos guardado de las huellas guardadas en la base de datos
                        this.refController.mostrarPaciente(obj);
                        bandera = true;
                        this.btnSalirActionPerformed(null);
                        refController.opcionModificar(true);
                    }
                }
            }
        }
        if(!bandera)
            Dialogs.create()
                .title("Pacientes de la institución")
                .masthead("Pacientes de la institución. ")
                .message("No hay paciente con esta huella. ")
                .showError();
    }
}
