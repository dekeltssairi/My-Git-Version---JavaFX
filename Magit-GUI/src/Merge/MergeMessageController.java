package Merge;

import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Branch;
import logic.LocalBranch;

import java.util.List;

public class MergeMessageController extends Notifier {
    @FXML
    private TextField m_TextField;
    private MainAppController m_MainAppController;
    private Branch m_SecondParent;

    public void SetMainAppController(MainAppController i_MainAppController){
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetCommitPerformedObserver();
        AddObservers(list);
    }

    public void SetSecondParent(Branch i_SecondParent){
        m_SecondParent = i_SecondParent;
    }

    public void OnClicked(ActionEvent i_Event) {
        m_MainAppController.MergeCommit(m_TextField.getText(), m_SecondParent);
        setChanged();
        notifyObservers();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully Merged");
        alert.setHeaderText("Successfully Merged");
        alert.setContentText("Local Changes has Sucesfuuly merged");
        alert.showAndWait();
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }
}
