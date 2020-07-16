package CommitMessage;

import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;


public class CommitMessageController extends Notifier {

    @FXML private TextField m_TextField;
    @FXML private TextField m_Button;
    private MainAppController m_MainAppController;

    public void SetMainAppController(MainAppController i_MainAppController){
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetCommitPerformedObserver();
        AddObservers(list);
    }

    public void OnClicked(ActionEvent i_Event) {
        m_MainAppController.Commit(m_TextField.getText());
        setChanged();
        notifyObservers();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesfully comitted");
        alert.setHeaderText("Sucecfully Comitted");
        alert.setContentText("Local Changes has Sucesfuuly comitted");
        alert.showAndWait();
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }
}
