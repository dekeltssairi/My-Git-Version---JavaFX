package Conflicts;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.Blob;
import logic.Conflict;
import logic.RepositoryFile;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ConflictsController implements Initializable {

    private List<Conflict> m_Conflicts;

    @FXML private ListView<Conflict> m_ListView;
    @FXML private TextArea m_AncsetorTextArea;
    @FXML private TextArea m_ChildATextArea;
    @FXML private TextArea m_ChildBTextArea;
    @FXML private Button m_Save;
    @FXML private Label m_Conflict;
    @FXML private TextArea m_Solve;
    @FXML private HBox m_HBox;

    public void SetConflicts(List<Conflict> i_Conflicts){
        m_Conflicts = i_Conflicts;
    }

    public void InitializeConflicts(){
        m_ListView.setItems(FXCollections.observableArrayList(m_Conflicts));
//       m_ListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//           @Override
//           public void handle(MouseEvent event) {
////               Conflict conflict = m_ListView.getSelectionModel().getSelectedItem();
////               if (conflict != null) {
////
////                   //  m_AncsetorTextArea.setText(conflict.GetAncestor().toString());
////                   m_ChildATextArea.setText(conflict.GetSonA().toString());
////                   m_ChildBTextArea.setText(conflict.GetSonB().toString());
////                   m_Conflict.setText(conflict.toString());
////               }
//           }
//       });
    }

    @FXML
    public void OnClicked(MouseEvent mouseEvent){
        renderChanges();
    }

    public void SaveButtonOnAction(ActionEvent i_Event){
        if (m_Solve.getText().equals("")){
            Service.Methods.ShowAlert(Alert.AlertType.ERROR,"Error", "No contaent", "Can't solve case with empty content");
        }
        else{
            Conflict conflict = m_ListView.getSelectionModel().getSelectedItem();
            String pathToFile;
            if(conflict.GetSonA() == null)
                if(conflict.GetSonB() == null)
                    pathToFile = conflict.GetAncestor().GetPath().toString();
                else
                    pathToFile = conflict.GetSonB().GetPath().toString();
                else
                pathToFile = conflict.GetSonA().GetPath().toString();

            DekelNoy3rd.Service.Methods.CreateTextFile(pathToFile, m_Solve.getText());
            m_ListView.getItems().remove(conflict);
            if (m_ListView.getItems().size() == 0){
                Stage stage = (Stage)((Button)i_Event.getSource()).getScene().getWindow();
                stage.close();
            }
            else {
                renderChanges();
            }
        }
    }

    public void DeleteButtonOnAction(ActionEvent i_Event){
        Conflict conflict = m_ListView.getSelectionModel().getSelectedItem();
        m_ListView.getItems().remove(conflict);
        //File file;
//        if (conflict.GetSonA() != null){
//            file = new File(conflict.GetSonA().GetPath().toString());
//        }
//        else if(conflict.GetSonB() != null){
//            file = new File(conflict.GetSonB().GetPath().toString());
//        }
//        else{
//            file = new File(conflict.GetAncestor().GetPath().toString());
//        }
        if (conflict.GetSonA() != null || conflict.GetSonB() != null){
            File file;
            if (conflict.GetSonA() != null){
                file = new File(conflict.GetSonA().GetPath().toString());
            }
            else{
                file = new File(conflict.GetSonB().GetPath().toString());
            }
            file.delete();
        }
        //file.delete();
        if (m_ListView.getItems().size() == 0){
            Stage stage = (Stage)((Button)i_Event.getSource()).getScene().getWindow();
            stage.close();
        }
        else{
            renderChanges();
        }
    }

    private void renderChanges() {
        Conflict conflict = m_ListView.getSelectionModel().getSelectedItem();
        if (conflict != null) {
            m_Solve.setEditable(true);
            m_Conflict.setText(conflict.toString());
            if (conflict.GetSonA() != null)
                m_ChildATextArea.setText(conflict.GetSonA().toString());
            else {
                m_ChildATextArea.setText("");
            }
            if (conflict.GetSonB() != null)
                m_ChildBTextArea.setText(conflict.GetSonB().toString());
            else {
                m_ChildBTextArea.setText("");
            }

            if (conflict.GetAncestor() != null)
                m_AncsetorTextArea.setText(conflict.GetAncestor().toString());
            else {
                m_AncsetorTextArea.setText("");
            }
            m_Solve.setText("");
        }
        else{
            m_Solve.setEditable(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        m_Solve.setEditable(false);
    }
}