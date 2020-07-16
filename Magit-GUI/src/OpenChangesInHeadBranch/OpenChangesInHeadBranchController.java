package OpenChangesInHeadBranch;

import MainApp.MainAppController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.accessibility.AccessibleAction;

public class OpenChangesInHeadBranchController {
    private MainAppController m_MainAppController;
    @FXML private Button m_CommitAndContinueBtn;
    @FXML private Button m_ContinueWithoutCommitBtn;

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
    }

    public void CommitAndContinueBtnAction(ActionEvent i_Event) {
        m_MainAppController.ShowWorkingCopyOpenChangesButtonClicked(i_Event);
        m_MainAppController.CommitBtn(i_Event);
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void ContinueWithoutCommitBtnAction(ActionEvent i_Event) {
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }
}