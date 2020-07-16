package DeployRB;

import BranchesList.BranchesListController;
import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.Branch;
import logic.RemoteBranch;

import java.util.List;

import static Service.Methods.ShowAlert;

public class DeployRBController extends Notifier {
    private MainAppController m_MainAppController;
    private RemoteBranch m_Branch;

    public void SetBranch(Branch i_Branch){
        m_Branch = (RemoteBranch) i_Branch;
    }
    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetBranchObserver();
        AddObservers(list);
    }

    public void CreateRTBAndDeploy(ActionEvent i_Event) {
        BranchesListController branchesListController = m_MainAppController.GetBranchesListController();
//        branchesListController.SetMainAppController(m_MainAppController);
//        branchesListController.SetCommit(m_Branch.GetCommit());
//        String[] RBName = m_Branch.GetName().split("/");
//        String RTBName = RBName[1];
        MenuButton menuBtn = m_MainAppController.CreateNewBranchForSpecificCommit(m_Branch.GetName(), m_Branch.GetCommit());
        m_MainAppController.SwitchHeadBranch(); // do commit or nothing
        branchesListController.UpdateHeadBranchStyle(menuBtn);
        m_MainAppController.SwitchHeadBranch(m_Branch.GetName());

        setChanged();
        notifyObservers();

        ShowAlert(Alert.AlertType.INFORMATION, "Switch Branch", "Create and deploy RTB", "Successfully create RTB and switch head branch to " + menuBtn.getText() + "!");
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void CancleAction(ActionEvent i_Event) {
        Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
        stage.close();
    }
}
