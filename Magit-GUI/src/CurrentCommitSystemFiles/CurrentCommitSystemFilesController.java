package CurrentCommitSystemFiles;

import MainApp.MainAppController;
import Observers.IBranchObserver;
import Observers.ICommitPerformedObserver;
import Observers.ILoadRepositoryObserver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import logic.Blob;
import logic.Commit;
import logic.Folder;
import logic.RepositoryFile;

import java.net.URL;
import java.util.*;

public class CurrentCommitSystemFilesController implements Initializable, ILoadRepositoryObserver, ICommitPerformedObserver, IBranchObserver {

    private MainAppController m_MainAppController;
    @FXML private TreeView<RepositoryFile> m_TreeView;

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
    }

    public void InitializeCurrentCommit() {
        RepositoryFile currentCommitMainFolder = m_MainAppController.GetEngine().GetCurrentCommitMainFolder();
        TreeItem<RepositoryFile> root = BuildTreeItemsRec(currentCommitMainFolder);
        root.setExpanded(true);
        m_TreeView.setRoot(root);
    }

    private TreeItem<RepositoryFile> BuildTreeItemsRec(RepositoryFile i_CurrentCommitMainFolder) {
        TreeItem<RepositoryFile> item;
        Image icon;
        if (i_CurrentCommitMainFolder instanceof Folder) {
            icon = new Image(getClass().getResourceAsStream("/Icons/FolderIcon.png"));
            Folder folder = new Folder((Folder)i_CurrentCommitMainFolder){
                @Override
                public String toString() {
                    return super.GetName();
                }
            };
            item = new TreeItem<RepositoryFile>(folder, new ImageView(icon));
            List<RepositoryFile> subRepositoryFiles = folder.GetRepositoryFiles();
            for (RepositoryFile repositoryFile : subRepositoryFiles){
                item.getChildren().add(BuildTreeItemsRec(repositoryFile));
            }
        }
        else {
            icon = new Image(getClass().getResourceAsStream("/Icons/FileIcon.png"));
            Blob blob = new Blob((Blob)(i_CurrentCommitMainFolder)){
                @Override
                public String toString() {
                    return super.GetName();
                }
            };
            item = new TreeItem<RepositoryFile>(blob, new ImageView(icon));
        }
        item.setExpanded(true);
        return item;
    }

    public void OnClicked(MouseEvent mouseEvent){
        if (mouseEvent.getClickCount() == 2){
            TreeItem<RepositoryFile> item = m_TreeView.getSelectionModel().getSelectedItem();
            RepositoryFile repositoryFile = item.getValue();
            if(repositoryFile instanceof Blob){
                Blob blob = (Blob)repositoryFile;
                ScrollPane root = new ScrollPane();
                root.setFitToWidth(true);
                root.setFitToHeight(true);
                TextArea textArea = new TextArea(blob.GetContent());
                textArea.setEditable(false);
                root.setContent(textArea);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(blob.GetName());
                stage.setResizable(true);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RepositoryFile emptyRepositoryFile = new RepositoryFile("No Repsitory loaded yet","","", new Date(), null){
            @Override
            public String toString() {
                return "No Repository loaded yet";
            }
        };
        TreeItem <RepositoryFile> item = new TreeItem<RepositoryFile>(emptyRepositoryFile);
        m_TreeView.setRoot(item);
    }

    public void InitializeAfterLoadRepositoryWithOutCommit() {
        RepositoryFile emptyRepositoryFile = new RepositoryFile("No Active Commit Yet","","", new Date(), null){
            @Override
            public String toString() {
                return "No Active Commit Yet";
            }
        };
        TreeItem <RepositoryFile> item = new TreeItem<RepositoryFile>(emptyRepositoryFile);
        m_TreeView.setRoot(item);
    }

    public void InitializeByCommit(Commit i_Commit){
        RepositoryFile mainFolder = i_Commit.GetMainFolder();
        TreeItem<RepositoryFile> root = BuildTreeItemsRec(mainFolder);
        root.setExpanded(true);
        m_TreeView.setRoot(root);
    }

    @Override
    public void update(Observable o, Object arg) {
        TreeItem<RepositoryFile> root;
        if(m_MainAppController.GetEngine().HasActiveCommit()) {
            InitializeCurrentCommit();
        }
        else{
            root = new TreeItem<>(new Blob(null,null,null,null,null, "No Active Commit"));
            m_TreeView.setRoot(root);
        }
    }
}
