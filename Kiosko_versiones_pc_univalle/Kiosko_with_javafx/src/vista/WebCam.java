package vista;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.dialog.Dialogs;
import static vista.CapturaHuella.toBufferedImage;

public class WebCam extends BorderPane {
    private FlowPane bottomCameraControlPane;
    private FlowPane topPane;
    private String cameraListPromptText = "Seleccione Cámara";
    private ImageView imgWebCamCapturedImage;
    private Webcam webCam = null;
    private boolean stopCamera = false;
    private BufferedImage grabbedImage;
    ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
    private BorderPane webCamPane;
    private Button btnCamreaStop;
    private Button btnCamreaStart;
    private Button btnCameraDispose;
    private Button btnCamaraCaptura;
    private ImageView imagenTomada = null;
    Stage primaryStage = null;
    MenuController refController = null;
    
    public WebCam(Stage ventanaPrincipal, Stage primaryStage, MenuController refController) {
        // Using Sarxos API
        this.refController = refController;
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Captura foto con WebCam");
        topPane = new FlowPane();
        topPane.setAlignment(Pos.CENTER);
        topPane.setHgap(20);
        topPane.setOrientation(Orientation.HORIZONTAL);
        topPane.setPrefHeight(40);
        this.setTop(topPane);
        webCamPane = new BorderPane();
        webCamPane.setStyle("-fx-background-color: #ccc;");
        imgWebCamCapturedImage = new ImageView();
        webCamPane.setCenter(imgWebCamCapturedImage);
        this.setCenter(webCamPane);
        createTopPanel();
        bottomCameraControlPane = new FlowPane();
        bottomCameraControlPane.setOrientation(Orientation.HORIZONTAL);
        bottomCameraControlPane.setAlignment(Pos.CENTER);
        bottomCameraControlPane.setHgap(20);
        bottomCameraControlPane.setVgap(10);
        bottomCameraControlPane.setPrefHeight(40);
        bottomCameraControlPane.setDisable(true);
        createCameraControls();
        this.setBottom(bottomCameraControlPane);
//        this.primaryStage.setScene(new Scene(this));
        this.primaryStage.setHeight(350);
        this.primaryStage.setWidth(300);
        this.primaryStage.centerOnScreen();
        this.primaryStage.setResizable(false);
        this.primaryStage.initOwner(ventanaPrincipal);
        this.primaryStage.initModality(Modality.APPLICATION_MODAL);
//        this.primaryStage.show();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setImageViewSize();
            }
        });
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override public void handle(WindowEvent event) {
                primaryStage.hide();
                refController.openFoto = true;
                disposeWebCamCamera(); //Comprobar que la cámara sí se apaga
                stopWebCamCamera();                
            }
        });
    }
    protected void setImageViewSize() {
        double height = webCamPane.getHeight();
        double width = webCamPane.getWidth();
        imgWebCamCapturedImage.setFitHeight(height);
        imgWebCamCapturedImage.setFitWidth(width);
        imgWebCamCapturedImage.prefHeight(height);
        imgWebCamCapturedImage.prefWidth(width);
        imgWebCamCapturedImage.setPreserveRatio(true);
    }
    public void webcamImageObtained() {
        Scene scene = null;
        final Stage secStage = new Stage();
        BorderPane bp = new BorderPane();
        imagenTomada = new ImageView();
        imagenTomada.setImage(cambiarTamanoImagen(imgWebCamCapturedImage.getImage()));
        
        FlowPane imagenFoto = new FlowPane();
        imagenFoto.setOrientation(Orientation.HORIZONTAL);
        imagenFoto.setAlignment(Pos.CENTER);
        imagenFoto.setHgap(20);
        imagenFoto.setVgap(10);
        imagenFoto.setPrefHeight(40);
        imagenFoto.getChildren().add(imagenTomada);
        bp.setCenter(imagenFoto);
        
        FlowPane bottomControlPane = new FlowPane();
        bottomControlPane.setOrientation(Orientation.HORIZONTAL);
        bottomControlPane.setAlignment(Pos.CENTER);
        bottomControlPane.setHgap(20);
        bottomControlPane.setVgap(10);
        bottomControlPane.setPrefHeight(40);
        Button btnOk = new Button();
        btnOk.setText("Ok");
        btnOk.setOnAction(new EventHandler<ActionEvent>() {   
            @Override
            public void handle(ActionEvent event) {
                refController.setEtiquetaFoto(new ImageView(imagenTomada.getImage()));
                refController.setFotoTomada(new ImageView(imagenTomada.getImage()));
                refController.openFoto = true;
                stopWebCamCamera();
                secStage.close();
                primaryStage.close();
            }
        });
//        Button btnCancelar = new Button();
//        btnCancelar.setText("Cancelar");
//        btnCancelar.setOnAction(new EventHandler<ActionEvent>() {   
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World!");
//            }
//        });
        bottomControlPane.getChildren().add(btnOk);
//        bottomControlPane.getChildren().add(btnCancelar);
        bp.setBottom(bottomControlPane);
        
        //Adding HBox to the scene 
        scene = new Scene(bp, 220, 200);
        secStage.setTitle("Ver Image");
        secStage.setScene(scene);
        secStage.show();
        
    }
    private void createTopPanel() {
        int webCamCounter = 0;
        Label lbInfoLabel = new Label("Seleccionar cámara WebCam");
        ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
        topPane.getChildren().add(lbInfoLabel);
        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }
        ComboBox<WebCamInfo> cameraOptions = new ComboBox<WebCamInfo>();
        cameraOptions.setItems(options);
        cameraOptions.setPromptText(cameraListPromptText);
        cameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {
            @Override
            public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
                if (arg2 != null) {
//                    System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
                    initializeWebCam(arg2.getWebCamIndex());
                    cameraOptions.setDisable(true);
                }
            }
        });
        topPane.getChildren().add(cameraOptions);
    }
    protected void initializeWebCam(final int webCamIndex) {
        Task<Void> webCamTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (webCam != null) {
                    disposeWebCamCamera();
                    webCam = Webcam.getWebcams().get(webCamIndex);
                    webCam.open();
                } else {
                    webCam = Webcam.getWebcams().get(webCamIndex);
                    webCam.open();
                }
                startWebCamStream();
                return null;
            }
        };
        Thread webCamThread = new Thread(webCamTask);
        webCamThread.setDaemon(true);
        webCamThread.start();
        bottomCameraControlPane.setDisable(false);
        btnCamreaStart.setDisable(true);
    }
    protected void startWebCamStream() {
        stopCamera = false;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!stopCamera) {
                    try {
                        if ((grabbedImage = webCam.getImage()) != null) {
//			System.out.println("Captured Image height*width:"+grabbedImage.getWidth()+"*"+grabbedImage.getHeight());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    final Image mainiamge = SwingFXUtils
                                            .toFXImage(grabbedImage, null);
                                    imageProperty.set(mainiamge);
                                }
                            });
                            grabbedImage.flush();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                        Dialogs.create()
                            .title("Registro asistencia")
                            .masthead("Error en el sistema, consultar con el proveedor")
                            .showError();
                    } finally { }
                }
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imgWebCamCapturedImage.imageProperty().bind(imageProperty);
    }
    private void createCameraControls() {
        btnCamreaStop = new Button();
        btnCamreaStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                stopWebCamCamera();
            }
        });
        btnCamreaStop.setText("Stop Camera");
        btnCamreaStart = new Button();
        btnCamreaStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                startWebCamCamera();
            }
        });
        btnCamreaStart.setText("Start Camera");
        btnCameraDispose = new Button();
        btnCameraDispose.setText("Dispose Camera");
        btnCameraDispose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                disposeWebCamCamera();
            }
        });
        btnCamaraCaptura = new Button();
        btnCamaraCaptura.setText("Captura imagen de la Camera");
        btnCamaraCaptura.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                webcamImageObtained();
            }
        });
//        bottomCameraControlPane.getChildren().add(btnCamreaStart);
//        bottomCameraControlPane.getChildren().add(btnCamreaStop);
//        bottomCameraControlPane.getChildren().add(btnCameraDispose);
        bottomCameraControlPane.getChildren().add(btnCamaraCaptura);
    }
    protected void disposeWebCamCamera() {
        stopCamera = true;
        webCam.close();
        Webcam.shutdown();
        btnCamreaStart.setDisable(true);
        btnCamreaStop.setDisable(true);
    }
    protected void startWebCamCamera() {
        stopCamera = false;
        startWebCamStream();
        btnCamreaStop.setDisable(false);
        btnCamreaStart.setDisable(true);
    }
    protected void stopWebCamCamera() {
        stopCamera = true;
        btnCamreaStart.setDisable(false);
        btnCamreaStop.setDisable(true);
    }
    public Image cambiarTamanoImagen(Image imagenOriginal) {
        double factor = 0.7;
        java.awt.Image imagen = SwingFXUtils.fromFXImage(imagenOriginal, null);
        java.awt.Image tmp1 = imagen.getScaledInstance((int)(imagen.getWidth(null)*factor),(int)(imagen.getHeight(null)*factor), java.awt.Image.SCALE_DEFAULT);
        java.awt.image.BufferedImage ib2 = toBufferedImage(tmp1);
        return SwingFXUtils.toFXImage(ib2, null);
    }
    class WebCamInfo {
        private String webCamName;
        private int webCamIndex;
        public String getWebCamName() {
            return webCamName;
        }
        public void setWebCamName(String webCamName) {
            this.webCamName = webCamName;
        }
        public int getWebCamIndex() {
            return webCamIndex;
        }
        public void setWebCamIndex(int webCamIndex) {
            this.webCamIndex = webCamIndex;
        }
        @Override
        public String toString() {
            return webCamName;
        }
    }
}