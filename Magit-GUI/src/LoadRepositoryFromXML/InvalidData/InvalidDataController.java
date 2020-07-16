package LoadRepositoryFromXML.InvalidData;

import MainApp.MainAppController;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InvalidDataController {
    private MainAppController m_MainAppController;

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
    }

    public void TryAgainBtnAction(ActionEvent i_Event) {
        m_MainAppController.LoadRepositoryFromXMLWindow(i_Event);
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
