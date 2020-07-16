package LoadExistRepository;

import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;

import static DekelNoy3rd.Service.Methods.*;

public class LoadExistRepositoryController extends Notifier {

    @FXML private TextField m_PathToExistRepository;
    @FXML private Button m_LoadExistRepositoryBtn;
    private MainAppController m_MainAppController;

    public void SetMainAppController (MainAppController i_MainAppController){
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetLoadRepositoryObservers();
        AddObservers(list);
    }

    public void LoadExistRepositoryBtnAction(ActionEvent i_Event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        String repositoryPathStr = m_PathToExistRepository.getText();
        Path repositoryPath = Paths.get(repositoryPathStr);
        if (repositoryPath.toFile().exists()) {
            if (repositoryPath.toFile().isDirectory()) {
                if (isDirectoryContainSpecificFolder(repositoryPath.toFile(), ".magit")) {
                    m_MainAppController.LoadExistRepository(repositoryPath.toFile().getAbsolutePath());
//                    if (!m_MainAppController.GetEngine().HasActiveCommit()){
//                        m_MainAppController.InitializeCenterAferInitializeRepository();
//                    }
//                    else{
//                        //m_MainAppController.UpdateCurrentCommitSystemFiles(i_Event);
//                        //m_MainAppController.UpdateCommitTree(); // Noy
//
                    setChanged();
                    notifyObservers();
                    alert.setTitle("Succescfully Load");
                    alert.setContentText("Repository uploaded successfully!");
                    alert.showAndWait();
                    Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                    stage.close();
                }
                else {
                    errorAlert.setTitle("Not Repository Error");
                    errorAlert.setContentText("The path is file but not reposotiroy of MAGIT!");
                    errorAlert.showAndWait();
                    Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                    stage.close();
                }
            }
            else {
                errorAlert.setTitle("Not Folder Error");
                errorAlert.setContentText("The path is File and not reposotiroy at all! (not a folder)!");
                errorAlert.showAndWait();
                Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                stage.close();
            }
        }
        else{
            errorAlert.setTitle("Error!");
            errorAlert.setHeaderText("Illegal input");
            errorAlert.setContentText("The Path is Not exist!");
            errorAlert.showAndWait();
            Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}
