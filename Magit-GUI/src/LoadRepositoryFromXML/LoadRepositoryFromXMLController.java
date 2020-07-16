package LoadRepositoryFromXML;

import LoadRepositoryFromXML.AlreadyExistRepositoryInPath.*;
import Generate.MagitRepository;
import LoadRepositoryFromXML.InvalidData.InvalidDataController;
import MainApp.MainAppController;
import Observers.Notifier;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.JAXB;
import logic.XMLValidation;
import static DekelNoy3rd.Service.Methods.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LoadRepositoryFromXMLController extends Notifier {

    @FXML
    private TextField m_PathToXMLFileRepository;
    private MainAppController m_MainAppController;
    private MagitRepository m_MagitRepository;
    @FXML private ProgressBar m_ProgressBar;
    @FXML private Label m_Label;

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetLoadRepositoryObservers();
        AddObservers(list);
    }

    public void ChooseFileBtnAction(ActionEvent i_Event){
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        m_PathToXMLFileRepository.setText(selectedFile.getAbsolutePath());
    }

    public void LoadRepositoryFromXMLBtnAction(ActionEvent i_Event) throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(1,10);
                updateMessage("Checking existance");
                Thread.sleep(50);
                //new Alert(Alert.AlertType.INFORMATION);
                String XMLRepositoryPathStr = m_PathToXMLFileRepository.getText();
                if (m_MainAppController.IsPathToXMLFile(XMLRepositoryPathStr) && m_MainAppController.IsExistXMLFile(XMLRepositoryPathStr)) {
                    m_MagitRepository = JAXB.loadXML(XMLRepositoryPathStr);
                    //MagitRepository magitRepository = JAXB.loadXML(XMLRepositoryPathStr);
                    XMLValidation validationMessage = m_MainAppController.CheckXMLValidation(m_MagitRepository);
                    updateProgress(2,10);
                    updateMessage("Checking Validation");
                    Thread.sleep(50);
                    if (!validationMessage.equals(XMLValidation.VALID_XML)){
                        Platform.runLater(()-> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Validation");
                            alert.setContentText(validationMessage.getName());
                            alert.showAndWait();
                            Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                            stage.close();
                            invalidDataWindow();
                        });
                        ;
                    } else {
                        updateProgress(3,10);
                        updateMessage("Checking overriding");
                        Thread.sleep(50);
                        if (m_MainAppController.IsAlreadyDirectoryInSystem(m_MagitRepository)) {
                            if (m_MainAppController.IsAlreadyRepository(m_MagitRepository)) {
                                Platform.runLater(()-> {
                                    alreadyExistRepositoryWindow();
                                    Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                                    stage.close();
                                });

                            } else {
                                Platform.runLater(()-> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Exist file in repository location");
                                    alert.setContentText("This location of repository is already contain another directory in the file system."
                                            + System.lineSeparator() + "The operation is canceled!");
                                    alert.showAndWait();
                                    Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                                    stage.close();
                                });
                            }
                        } else {
                            m_MainAppController.ConvertMagitRepositoryToOurRepository(m_MagitRepository);
                            for (int i = 3; i < 10; i++){
                                updateProgress(i,10);
                                updateMessage("Loading XML");
                                Thread.sleep(50);
                            }
                            Platform.runLater(()-> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Successfully Load!");
                                alert.setContentText("The repository was loaded successfily");
                                setChanged();
                                notifyObservers();

                                //m_MainAppController.UpdateCurrentCommitSystemFiles(i_Event); // Noy
                                alert.showAndWait();
                                Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                                stage.close();
                            });

                        }
                    }
                } else {

                    Platform.runLater(()-> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Not XML file!");
                        alert.setContentText("This path is not XML file");
                        alert.showAndWait();

                        invalidDataWindow();
                        //notXMLFileWindow();
                        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                        stage.close();
                    });

                }


                return null;
            }
        };
        m_ProgressBar.progressProperty().unbind();
        m_ProgressBar.progressProperty().bind(task.progressProperty());
        m_Label.textProperty().bind(task.messageProperty());

        Thread thread = new Thread(task);
        thread.start();
    }

    public void OverrideCurrentRepositoryAndLoadNewOne() {
        DeleteDirectory(new File(m_MagitRepository.getLocation()));
        m_MainAppController.ConvertMagitRepositoryToOurRepository(m_MagitRepository);
        setChanged();
        notifyObservers();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully load");
        alert.setContentText("The previous repository has been successfully replaced with the new repository!");
        alert.showAndWait();
    }

//    private void notXMLFileWindow() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/NotXMLFile/NotXMLFile.fxml"));
//            Parent root = (Parent) fxmlLoader.load();
//            NotXMLFileController notXMLFileController = (NotXMLFileController) fxmlLoader.getController();
//            notXMLFileController.SetMainAppController(m_MainAppController);
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Not an XML file!");
//            stage.setResizable(false);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.showAndWait();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void invalidDataWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InvalidData/InvalidData.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            InvalidDataController invalidDataController = (InvalidDataController) fxmlLoader.getController();
            invalidDataController.SetMainAppController(m_MainAppController);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Invalid data!");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alreadyExistRepositoryWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LoadRepositoryFromXML/AlreadyExistRepositoryInPath/AlreadyExistRepositoryInPath.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            AlreadyExistRepositoryInPathController alreadyExistRepositoryInPathController = (AlreadyExistRepositoryInPathController) fxmlLoader.getController();
            alreadyExistRepositoryInPathController.SetLoadRepositoryFromXMLController(this);
            alreadyExistRepositoryInPathController.SetMainAppController(m_MainAppController);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Already exist repository!");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void invalidXMLFileWindow() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/InvalidXMLFile/InvalidXMLFile.fxml"));
//            Parent root = (Parent) fxmlLoader.load();
//            InvalidXMLFileController invalidXMLFileController = (InvalidXMLFileController) fxmlLoader.getController();
//            invalidXMLFileController.SetMainAppController(m_MainAppController);
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Invalid XML File!");
//            stage.setResizable(false);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.showAndWait();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
