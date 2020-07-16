package LoadRepositoryFromXML.AlreadyExistRepositoryInPath;

import LoadRepositoryFromXML.LoadRepositoryFromXMLController;
import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

public class AlreadyExistRepositoryInPathController extends Notifier {
    @FXML public Button m_OverrideAndLoadNewOne;
    private MainAppController m_MainAppController;
    private LoadRepositoryFromXMLController m_LoadRepositoryFromXMLController;

    public void SetLoadRepositoryFromXMLController(LoadRepositoryFromXMLController i_LoadRepositoryFromXMLController) {
        m_LoadRepositoryFromXMLController = i_LoadRepositoryFromXMLController;
    }

    public void SetMainAppController (MainAppController i_MainAppController){
        m_MainAppController = i_MainAppController;
        List list =m_MainAppController.GetLoadRepositoryObservers();
        AddObservers(list);
    }

    public void OverrideCurrentRepositoryAndLoadNewBtnAction(ActionEvent i_Event) {
        m_LoadRepositoryFromXMLController.OverrideCurrentRepositoryAndLoadNewOne();
        //m_MainAppController.UpdateCurrentCommitSystemFiles(i_Event); // Noy
        setChanged();
        notifyObservers();
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void CancleTheOperationBtnAction(ActionEvent i_Event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancle");
        alert.setContentText("The operation was cancled!");
        alert.showAndWait();
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }
}
