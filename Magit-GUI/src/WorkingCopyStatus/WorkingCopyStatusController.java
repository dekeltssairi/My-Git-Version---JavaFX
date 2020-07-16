package WorkingCopyStatus;

import MainApp.MainAppController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import logic.*;

import javax.swing.text.StyledEditorKit;
import java.util.*;

public class WorkingCopyStatusController {

    @FXML private ScrollPane m_ScrollPane;
    private Delta m_Delta;
    private MainAppController m_MainAppController;


    public void SetDelta (Delta i_Delta){
        m_Delta = i_Delta;
    }

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController=i_MainAppController;
    }

    public void InitalizeChanges() {
        int j = 0;
        GridPane m_GridPane = new GridPane();
        if (m_Delta.GetAdded().size() > 0){
            VBox vBox = new VBox();
            Label label = new Label("Added");
            //label.setStyle("-fx-font-weight: bold");
            label.setFont(new Font(14));
            label.setStyle("-fx-background-color: #7ABEC9");

            //label.setFont(Font.font(String.valueOf(FontWeight.BOLD), 18));
            label.setAlignment(Pos.CENTER);

            ListView<HBox> listView = new ListView<HBox>();
            List<HBox> list = new ArrayList<>();

            for (RepositoryFile repositoryFile: m_Delta.GetAdded()){
                Image icon;
                HBox hBox = new HBox();
                if (repositoryFile instanceof Folder)
                    icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/Icons/FolderIcon.png"));
                else{
                    icon = new Image(getClass().getResourceAsStream("/Icons/FileIcon.png"));
                }
                hBox.getChildren().addAll(new ImageView(icon), new Label(repositoryFile.GetPath().toString()));
                list.add(hBox);
            }


            listView.setItems(FXCollections.observableArrayList(list));
            vBox.getChildren().addAll(label, listView);
            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.setPrefWidth(300);
            StackPane pane = new StackPane();
            pane.getChildren().add(vBox);
            m_GridPane.add(pane, m_GridPane.impl_getColumnCount(), j);
            //i++;
        }

        if (m_Delta.GetDeleted().size() > 0){
            VBox vBox = new VBox();
            Label label = new javafx.scene.control.Label("Deleted");
            label.setFont(new Font(14));
            label.setStyle("-fx-background-color: #7ABEC9");
            javafx.scene.control.ListView<HBox> listView = new ListView<HBox>();
            List<HBox> list = new ArrayList<>();

            for (RepositoryFile repositoryFile: m_Delta.GetDeleted()){
                Image icon;
                HBox hBox = new HBox();
                if (repositoryFile instanceof Folder)
                    icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/Icons/FolderIcon.png"));
                else{
                    icon = new Image(getClass().getResourceAsStream("/Icons/FileIcon.png"));
                }
                hBox.getChildren().addAll(new ImageView(icon), new Label(repositoryFile.GetPath().toString()));
                list.add(hBox);
            }
            listView.setItems(FXCollections.observableArrayList(list));
            vBox.getChildren().addAll(label, listView);
            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.setPrefWidth(300);
            StackPane pane = new StackPane();
            pane.getChildren().add(vBox);
            m_GridPane.add(pane, m_GridPane.impl_getColumnCount(), j);
            //i++;
        }


        if (m_Delta.GetChanged().size() > 0){
            VBox vBox = new VBox();
            Label label = new javafx.scene.control.Label("Changed");
            label.setFont(new Font(14));
            label.setStyle("-fx-background-color: #7ABEC9");
            javafx.scene.control.ListView<HBox> listView = new ListView<HBox>();
            List<HBox> list = new ArrayList<>();

            for (RepositoryFile repositoryFile: m_Delta.GetChanged()){
                Image icon;
                HBox hBox = new HBox();
                if (repositoryFile instanceof Folder)
                    icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/Icons/FolderIcon.png"));
                else{
                    icon = new Image(getClass().getResourceAsStream("/Icons/FileIcon.png"));
                }
                hBox.getChildren().addAll(new ImageView(icon), new Label(repositoryFile.GetPath().toString()));
                list.add(hBox);
            }

            listView.setItems(FXCollections.observableArrayList(list));
            vBox.getChildren().addAll(label, listView);
            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.setPrefWidth(300);
            StackPane pane = new StackPane();
            pane.getChildren().add(vBox);
            m_GridPane.add(pane, m_GridPane.impl_getColumnCount(), j);
            //i++;
        }
        m_ScrollPane.setContent(m_GridPane);
    }
}