package CreateRTB;

import BranchesList.BranchesListController;
import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;
import logic.Commit;
import logic.RemoteBranch;

import javax.swing.*;
import java.util.List;

import static Service.Methods.ShowAlert;

public class CreateRTBController extends Notifier {
    private MainAppController m_MainAppController;
    private String m_BranchName;
    private Commit m_Commit;
    @FXML private Label m_MessageLabel;
    @FXML private Label m_PointedByMessageLabel;

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetBranchObserver();
        AddObservers(list);
    }

    public void SetBranchName(String i_BranchName) {
        m_BranchName = i_BranchName;
    }

    public void SetCommit(Commit i_Commit) {
        m_Commit = i_Commit;
    }

    public void SetPointedByMessageLabel() {
        m_PointedByMessageLabel.setText("This commit pointed by "+ m_BranchName + " RB!");
    }

    public void SetMessageLabel() {
        m_MessageLabel.setText("Do you want to create RTB after " + m_BranchName + " or regular branch? ");
    }

    public void CreateRTB(ActionEvent i_Event) {
        m_MainAppController.CreateNewBranchForSpecificCommit(m_BranchName, m_Commit);
        setChanged();
        notifyObservers();

        ShowAlert(Alert.AlertType.INFORMATION, "New RTB", "Create new RTB", "Successfully create RTB after " + m_BranchName + "!");
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void CreateRegularBranch(ActionEvent i_Event) {
        BranchesListController branchesListController = m_MainAppController.GetBranchesListController();
        branchesListController.SetCommit(m_Commit);
        branchesListController.AddBranchWindow(i_Event);

        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }
}
