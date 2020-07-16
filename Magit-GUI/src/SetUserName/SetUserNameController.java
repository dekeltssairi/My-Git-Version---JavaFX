package SetUserName;

import MainApp.MainAppController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class SetUserNameController {

    MainAppController m_MainAppController;
    @FXML private TextField m_NewUserName;
    @FXML private Button m_ChangeNameBtn;
    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
        m_NewUserName.textProperty().bindBidirectional(m_MainAppController.GetUserName().textProperty());
    }

    public void ChangeNameAction(ActionEvent i_Event){
        m_MainAppController.GetEngine().SetUsername(m_NewUserName.getText());
        Stage stage = (Stage) m_ChangeNameBtn.getScene().getWindow();
        stage.close();
    }
    public void SetUserName (String i_UserNameStr){
        m_NewUserName.setText(i_UserNameStr);
    }

}