package RepositoryPresentation;

import MainApp.MainAppController;
import Observers.ILoadRepositoryObserver;
import Observers.Notifier;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

public class RepositoryPresentationController implements Initializable, ILoadRepositoryObserver {
    @FXML private Label m_ReositoryName;
    @FXML private TextField m_ReositoryLocation;

    private MainAppController m_MainAppController;

    public TextField GetReositoryLocation() {
        return m_ReositoryLocation;
    }

    public Label GetReositoryName() {
        return m_ReositoryName;
    }

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        m_ReositoryName.setText("No repository yet!");
        m_ReositoryLocation.setText("No Location yet");
    }

    @Override
    public void update(Observable o, Object arg) {
        m_ReositoryName.setText(m_MainAppController.GetEngine().GetActiveRepository().GetName());
        m_ReositoryLocation.setText(m_MainAppController.GetEngine().GetActiveRepository().GetPath().normalize().toAbsolutePath().toString());
    }
}
