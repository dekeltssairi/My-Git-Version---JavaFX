package CreateNewBranch;


import MainApp.MainAppController;
import Observers.ICommitPerformedObserver;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static Service.Methods.*;
import logic.Branch;
import logic.Commit;

import java.util.List;

public class CreateNewBranchController extends Notifier{

    @FXML private TextField m_NewBranchName;
    private MainAppController m_MainAppController;
    private Commit m_Commit;

    public void SetCommit(Commit i_Commit){
        m_Commit = i_Commit;
    }
    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetBranchObserver();
        AddObservers(list);
    }

    public void CreateNewBranchBtnAction(ActionEvent i_Event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        String branchName = m_NewBranchName.getText();
        //if (m_MainAppController.HasActiveCommit()) {
            if (!m_MainAppController.IsExistBranchInActiveRepository(branchName)) {
                if(branchName.equals("")){
                    ShowAlert(Alert.AlertType.ERROR, "Empty Name", "Empty Name", "Beranch name is empty. Can't create branch with empty name." + System.lineSeparator() + "Please try again");
                }
                else {
                    if(m_Commit != null){
                        m_MainAppController.CreateNewBranchForSpecificCommit(branchName, m_Commit);
                    }
                    else{
                        m_MainAppController.CreateNewBranch(branchName);
                    }
                    setChanged();
                    notifyObservers();

//                  m_MainAppController.UpdateCommitTree();
                    alert.setTitle("successfully create new branch");
                    alert.setContentText("New branch added successfully!");
                    alert.showAndWait();
                    Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                    stage.close();
                }
            }
            else{
                errorAlert.setTitle("Exist Branch");
                errorAlert.setContentText("This branch name is already exist!");
                errorAlert.showAndWait();
                Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
                stage.close();
            }
//        }
//        else{
//            errorAlert.setTitle("No active commit!");
//            errorAlert.setContentText("No active Commit yet! Head Branch doesnt point to any commit!");
//            errorAlert.showAndWait();
//            Stage stage = (Stage) ((Button) i_Event.getSource()).getScene().getWindow();
//            stage.close();
//        }
    }
}

