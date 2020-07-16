package Clone;

import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;

import static DekelNoy3rd.Service.Methods.*;
import static Service.Methods.*;

public class CloneController extends Notifier {
    @FXML private TextField m_PathToDestinationToClone;
    @FXML private TextField m_PathToRepositoryToClone;
    @FXML private TextField m_RepositoryName;
    @FXML private Button m_CloneBtn;
    private MainAppController m_MainAppController;

    public void SetMainAppController (MainAppController i_MainAppController){
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetLoadRepositoryObservers();
        AddObservers(list);
    }

    public void CloneRepositoryBtn(ActionEvent i_Event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        String repositoryToClonePathStr = m_PathToRepositoryToClone.getText();
        String destinationRepositoryToClonePathStr = m_PathToDestinationToClone.getText();
        Path repositoryToClonePath = Paths.get(repositoryToClonePathStr);
        Path destinationRepositoryToClonePath = Paths.get(destinationRepositoryToClonePathStr);
        if (repositoryToClonePath.toFile().exists()) {
            if (repositoryToClonePath.toFile().isDirectory()) {
                if (isDirectoryContainSpecificFolder(repositoryToClonePath.toFile(), ".magit")) {
                    if(destinationRepositoryToClonePath.toFile().exists()){
                        ShowAlert(Alert.AlertType.ERROR, "Exist location!", "Exist location", "This path is already exist in your file system!");
                    }
                    else{
                        m_MainAppController.CloneRepository(m_PathToRepositoryToClone.getText(), m_PathToDestinationToClone.getText(), m_RepositoryName.getText());
                        setChanged();
                        notifyObservers();
                        ShowAlert(Alert.AlertType.INFORMATION, "Successfully clone", "Successfully clone!",
                                "Successfully clone Repository!");
                        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                        stage.close();
                    }
                } else {
                    ShowAlert(Alert.AlertType.ERROR, "No Repository!", "Wrong path", "This path is not repository!");
                }
            } else {
                ShowAlert(Alert.AlertType.ERROR, "No Directory!", "Wrong path", "This path is not directory!");
            }
        }
        else{
            ShowAlert(Alert.AlertType.ERROR, "No exist path!", "Wrong path", "This path is not exist!");
        }
    }
}
