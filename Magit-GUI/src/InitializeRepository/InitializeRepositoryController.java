package InitializeRepository;

import MainApp.MainAppController;
import Observers.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import puk.team.course.magit.ancestor.finder.AncestorFinder;

import java.nio.file.*;
import java.util.List;

import static Service.Methods.*;

public class InitializeRepositoryController extends Notifier {
    @FXML private TextField m_PathToRepository;
    @FXML private TextField m_RepositoryName;
    private MainAppController m_MainAppController;

    public void SetMainAppController (MainAppController i_MainAppController){
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetLoadRepositoryObservers();
        AddObservers(list);
    }

    public void LoadBtnAction(ActionEvent i_Event){
        Path repositoryPath = Paths.get( m_PathToRepository.getText());
        if (!repositoryPath.toFile().exists()){
            m_MainAppController.InitializeRepository(repositoryPath.toString(), m_RepositoryName.getText());
            setChanged();
            notifyObservers();
            //m_MainAppController.InitializeCenterAferInitializeRepository();
            //m_MainAppController.CleanCommitTree();
            //todo: check if commit tree empty
            ShowAlert(Alert.AlertType.INFORMATION,
                    "Successfully",
                    "Successfully Initialized Repository!",
                    "Successfully Initilized Repository in " + repositoryPath.toString() );
            Stage stage = (Stage)((Button)i_Event.getSource()).getScene().getWindow();
            stage.close();
        }
        else{
            ShowAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Illegal input!",
                    "The Path is Already existed!");
        }
    }
}