package MainApp;

import BranchesList.BranchesListController;
import Clone.CloneController;
import CommitMessage.CommitMessageController;
import Conflicts.ConflictsController;
import CreateRTB.CreateRTBController;
import CurrentCommitSystemFiles.CurrentCommitSystemFilesController;
import DataStructures.ListWithoutDuplications;
import DeployRB.DeployRBController;
import Generate.MagitRepository;
import InitializeRepository.InitializeRepositoryController;
import LoadExistRepository.LoadExistRepositoryController;
import LoadRepositoryFromXML.LoadRepositoryFromXMLController;
import Merge.MergeMessageController;
import Observers.*;
import OpenChangesInHeadBranch.OpenChangesInHeadBranchController;
import RepositoryPresentation.RepositoryPresentationController;
//import ResetHeadBranch.ResetHeadBranchController;
import SetUserName.SetUserNameController;
import WorkingCopyStatus.WorkingCopyStatusController;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import logic.*;
import visual.CommitTree.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static Service.Methods.*;

public class MainAppController implements Initializable, ILoadRepositoryObserver {
    private MyAmazingGitEngine m_MyAmazingGitEngine;

    private Stage m_PrimaryStage;

    @FXML
    private TextField m_UserName;
    @FXML
    public GridPane m_BranchesGridPane;
    @FXML
    private StackPane m_RepositoryPresentation;
    @FXML
    private RepositoryPresentationController m_RepositoryPresentationController;
    @FXML
    private TreeView m_CurrentCommitFileSystem;
    @FXML
    private CurrentCommitSystemFilesController m_CurrentCommitFileSystemController;
    @FXML
    private ScrollPane m_BranchesList;
    @FXML
    private BranchesListController m_BranchesListController;
    @FXML
    private GridPane m_CommitTree;
    @FXML
    private CommitTreeController m_CommitTreeController;
    @FXML
    private CommitNodeController m_CommitNodeController;

    @FXML private RadioButton m_AnimationRadioBtn;
    @FXML private Button m_ShowWCStatusBtn;
    @FXML private Button m_CommitBtn;
    @FXML private Button m_AddBranchBtn;
    @FXML private Button m_CloneBtn;
    @FXML private Button m_PullBtn;
    @FXML private Button m_PushBtn;
    @FXML private Button m_FetchBtn;

    // -----------------Observers------------------------------
    //private List<IDeletedBranchObserver> m_DeletedBranchObservers = new ListWithoutDuplications();
    //private List<IDeletedBranchObserver> m_AddedBranchObservers = new ListWithoutDuplications();
    private List<ICommitPerformedObserver> m_CommitPerformedObservers = new ListWithoutDuplications();
    private List<IResetHeadBranchChangedObserver> m_ResetHeadBranchObservers = new ListWithoutDuplications();
    private List<ILoadRepositoryObserver> m_LoadRepositoryObservers = new ListWithoutDuplications();
    //    private List<IInitializeRepositoryObserver> m_InitializeRepositoryObservers = new ListWithoutDuplications();
//    private List<ILoadRepositoryFromXMLObserver> m_LoadRepositoryFromXMLObservers = new ListWithoutDuplications();
    //private List<IDeployRBObserver> m_DeployRBObservers = new ListWithoutDuplications();
    private List<IBranchObserver> m_BranchObservers = new ListWithoutDuplications();
    @FXML
    private MenuItem m_CloneMenuItem;
    @FXML
    private MenuItem m_FetchMenuItem;
    @FXML
    private MenuItem m_PullMenuItem;
    @FXML
    private MenuItem m_PushMenuItem;

    public BranchesListController GetBranchesListController() {
        return m_BranchesListController;
    }

    public Stage GetPrimaryStage() {
        return m_PrimaryStage;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        m_MyAmazingGitEngine = new MyAmazingGitEngine();

        m_UserName.setText("Administrator");
        m_CommitPerformedObservers.add(m_CurrentCommitFileSystemController);
        m_CommitPerformedObservers.add(m_CommitTreeController);
        m_LoadRepositoryObservers.add(m_CommitTreeController);
        m_LoadRepositoryObservers.add(m_CurrentCommitFileSystemController);
        //m_DeletedBranchObservers.add(m_CommitTreeController);
        m_LoadRepositoryObservers.add(m_BranchesListController);
        m_LoadRepositoryObservers.add(m_RepositoryPresentationController);
        m_BranchObservers.add(m_BranchesListController);
        m_BranchObservers.add(m_CommitTreeController);
        m_BranchObservers.add(m_CurrentCommitFileSystemController);
        m_LoadRepositoryObservers.add(this);

        m_CloneMenuItem.setDisable(false);
        m_FetchMenuItem.setDisable(true);
        m_PullMenuItem.setDisable(true);
        m_PushMenuItem.setDisable(true);
        m_ShowWCStatusBtn.setDisable(true);
        m_CommitBtn.setDisable(true);
        m_AddBranchBtn.setDisable(true);
        m_CloneBtn.setDisable(false);
        m_PullBtn.setDisable(true);
        m_PushBtn.setDisable(true);
        m_FetchBtn.setDisable(true);

        m_RepositoryPresentationController.SetMainAppController(this);
        m_CurrentCommitFileSystemController.SetMainAppController(this);
        m_BranchesListController.SetMainAppController(this);
        m_CommitTreeController.SetMainController(this);
    }

    public TextField GetUserName() {
        return m_UserName;
    }

    public void SetPrimaryStage(Stage i_PrimaryStage){
        m_PrimaryStage = i_PrimaryStage;
    }

    @FXML
//    public void AnimationBtnAction(ActionEvent i_Event){
//        if(m_AnimationRadioBtn.isSelected()){
//
//        }else{
//
//        }
//    }

    public CommitTreeController GetCommitTreeController() {
        return m_CommitTreeController;
    }

    @FXML public void SwitchSkin(ActionEvent i_Event){
        BorderPane borderPane = (BorderPane) m_PrimaryStage.getScene().lookup("#root");
        if(m_PrimaryStage.getTitle().equals("Magit")){
            m_PrimaryStage.setTitle("M-A-Git");
            borderPane.getStylesheets().clear();
            borderPane.getStylesheets().add("/MainApp/MainApp2.css");
        }
        else if(m_PrimaryStage.getTitle().equals("M-A-Git")){
            m_PrimaryStage.setTitle("My Amazing Git");
            borderPane.getStylesheets().clear();
            borderPane.getStylesheets().add("/MainApp/MainApp3.css");
        }
        else if (m_PrimaryStage.getTitle().equals("My Amazing Git")){
            m_PrimaryStage.setTitle("MA-Git");
            borderPane.getStylesheets().clear();
            borderPane.getStylesheets().add("/MainApp/MainApp4.css");
        } else{
            m_PrimaryStage.setTitle("Magit");
            borderPane.getStylesheets().clear();
            borderPane.getStylesheets().add("/MainApp/MainApp.css");
        }

    }

    @FXML
    public void Close(ActionEvent i_Event){
       System.exit(0);
    }

    @FXML
    public void InitializeRepositoryWindow(ActionEvent i_Event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/InitializeRepository/InitializeRepository.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            InitializeRepositoryController IRController =(InitializeRepositoryController)fxmlLoader.getController();
            IRController.SetMainAppController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void LoadExistRepositoryWindow(ActionEvent i_Event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LoadExistRepository/LoadExistRepository.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            LoadExistRepositoryController LERController =(LoadExistRepositoryController)fxmlLoader.getController();
            LERController.SetMainAppController(this);
            Stage stage = new Stage();
            stage.setTitle("Load Repository");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void LoadRepositoryFromXMLWindow(ActionEvent i_Event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LoadRepositoryFromXML/LoadRepositoryFromXML.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            LoadRepositoryFromXMLController LXMLRController =(LoadRepositoryFromXMLController)fxmlLoader.getController();
            LXMLRController.SetMainAppController(this);
            Stage stage = new Stage();
            stage.setTitle("Load Repository from XML");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Clone(ActionEvent i_Event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Clone/Clone.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            CloneController cloneController =(CloneController)fxmlLoader.getController();
            cloneController.SetMainAppController(this);
            Stage stage = new Stage();
            stage.setTitle("Clone Repository");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DeployRBWindow(Branch i_Branch) { // NOY-FRIDAY
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DeployRB/DeployRB.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            DeployRBController deployRBController = (DeployRBController) fxmlLoader.getController();
            deployRBController.SetMainAppController(this);
            deployRBController.SetBranch(i_Branch);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Deploy RB!");
            stage.setResizable(false);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CreateRTBWindow(String i_BranchName, Commit i_Commit){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CreateRTB/CreateRTB.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            CreateRTBController createRTBController = (CreateRTBController) fxmlLoader.getController();
            createRTBController.SetMainAppController(this);
            createRTBController.SetBranchName(i_BranchName);
            createRTBController.SetCommit(i_Commit);
            createRTBController.SetMessageLabel();
            createRTBController.SetPointedByMessageLabel();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create branch!");
            stage.setResizable(false);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SwitchHeadBranch(){
        if(HasFilesInWC()){
            if(IsSomethingToCommit()){
                OpenChangesInHeadBranchWindow();
            }
        }
    }

//    public void ResetHeadBranchWindow(Commit i_Commit) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ResetHeadBranch/ResetHeadBranch.fxml"));
//            Parent root = (Parent) fxmlLoader.load();
//            ResetHeadBranchController resetHeadBranchController = (ResetHeadBranchController) fxmlLoader.getController();
//            resetHeadBranchController.SetMainAppController(this);
//            resetHeadBranchController.SetCommit(i_Commit);
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Open Changes!");
//            stage.setResizable(false);
//            stage.initStyle(StageStyle.UNDECORATED);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.showAndWait();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void ResetHeadBranch(Commit i_Commit) {
        m_MyAmazingGitEngine.ResetHeadBranch(i_Commit);
        m_MyAmazingGitEngine.DeployHeadBranch();
    }

    private void OpenChangesInHeadBranchWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/OpenChangesInHeadBranch/OpenChangesInHeadBranch.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            OpenChangesInHeadBranchController openChangesInHeadBranchController = (OpenChangesInHeadBranchController) fxmlLoader.getController();
            openChangesInHeadBranchController.SetMainAppController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Open Changes!");
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private boolean OpenChangesInHeadBranchWindow() {
//        final boolean[] res = {true};
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/OpenChangesInHeadBranch/OpenChangesInHeadBranch.fxml"));
//            Parent root = (Parent) fxmlLoader.load();
//            OpenChangesInHeadBranchController openChangesInHeadBranchController = (OpenChangesInHeadBranchController) fxmlLoader.getController();
//            openChangesInHeadBranchController.SetMainAppController(this);
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Open Changes!");
//            stage.setResizable(false);
//            //stage.initStyle(StageStyle.UNDECORATED);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.showAndWait();
//
//            System.out.println("bye bye targil 2!!!!!");
//            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent event) {
//                    res[0] = false;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res[0];
//    }

    public void ShowWorkingCopyOpenChangesButtonClicked(ActionEvent i_Event) {
        if (m_MyAmazingGitEngine.HasActiveRepository()) {
            if (m_MyAmazingGitEngine.HasActiveCommit()) {
                if (m_MyAmazingGitEngine.HasFilesInWC()) {
                    if (m_MyAmazingGitEngine.IsSomethingToCommit()) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WorkingCopyStatus/WorkingCopyStatus.fxml"));
                        Parent root = null;
                        try {
                            root = (Parent) fxmlLoader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        WorkingCopyStatusController workingCopyStatusController = (WorkingCopyStatusController) fxmlLoader.getController();
                        workingCopyStatusController.SetMainAppController(this);
                        workingCopyStatusController.SetDelta(m_MyAmazingGitEngine.GetDelta());
                        workingCopyStatusController.InitalizeChanges();
                        Stage stage = new Stage();
                        Scene scene = new Scene(root);

                        stage.setScene(scene);
                        stage.sizeToScene();
                        //stage.setScene(new Scene(root));
                        stage.setTitle("Open Changes!");
                        stage.setResizable(true);
                        //stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("State Clear");
                        alert.setHeaderText("State Clear");
                        alert.setContentText("No Local Changes In WC");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Working Copy Empty");
                    alert.setHeaderText("Working Copy Empty");
                    alert.setContentText("All Files Of Current Commit Deleted!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Active Commit");
                alert.setHeaderText("No Active Commit");
                alert.setContentText("No Local Changes Due to lack of Active Commit");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Active Repository");
            alert.setHeaderText("No Active Repository");
            alert.setContentText("No Local Changes Due to lack of Active Repository");
            alert.showAndWait();
        }
    }

    @FXML
    public void SetUserNameWindow(ActionEvent i_Event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SetUserName/SetUserName.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            SetUserNameController setUserNameController = (SetUserNameController) fxmlLoader.getController();
            setUserNameController.SetMainAppController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Set username");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    setUserNameController.SetUserName(m_MyAmazingGitEngine.GetUserName().GetName());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void AddBranchBtn(ActionEvent i_Event){
        m_BranchesListController.AddBranchWindow(i_Event);
    }

    @FXML
    public void CommitBtn(ActionEvent i_Event) {
        if (m_MyAmazingGitEngine.HasActiveRepository()) {
            if (m_MyAmazingGitEngine.HasFilesInWC()) {
                if (m_MyAmazingGitEngine.IsSomethingToCommit()) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CommitMessage/CommitMessage.fxml"));
                    Parent root = null;
                    try {
                        root = (Parent) fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CommitMessageController commitMessageController = (CommitMessageController) fxmlLoader.getController();
                    commitMessageController.SetMainAppController(this);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Commit");
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait(); // Noy- added wait
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("State Clear");
                    alert.setHeaderText("State Clear");
                    alert.setContentText("No Local Changes To Commit");
                    alert.showAndWait();
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erorr");
                alert.setHeaderText("Error");
                alert.setContentText("No Files in WC! nothing to commit!");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erorr");
            alert.setHeaderText("Error");
            alert.setContentText("No Active Repository!, nothing to commit!");
            alert.showAndWait();
        }
    }


    public void  InitializeRepository(String i_RepositoryPathStr, String i_RepositoryName) {
        m_MyAmazingGitEngine.CreateRepositoryFromPath(i_RepositoryPathStr, i_RepositoryName);
        m_RepositoryPresentationController.GetReositoryLocation().setText(i_RepositoryPathStr);
        m_RepositoryPresentationController.GetReositoryName().setText(i_RepositoryName);
    }

    public void LoadExistRepository(String i_AbsolutePath) {
        m_MyAmazingGitEngine.LoadExistRepository(i_AbsolutePath);
//        m_RepositoryPresentationController.GetReositoryName().setText(m_MyAmazingGitEngine.GetActiveRepository().GetName());
//        m_RepositoryPresentationController.GetReositoryLocation().setText(m_MyAmazingGitEngine.GetActiveRepository().GetPath().toString());
    }

    public void ConvertMagitRepositoryToOurRepository(MagitRepository i_MagitRepository) {
        m_MyAmazingGitEngine.ConvertMagitRepositoryToOurRepository(i_MagitRepository);
        Platform.runLater(() -> {
            m_RepositoryPresentationController.GetReositoryName().setText(m_MyAmazingGitEngine.GetActiveRepository().GetName());
            m_RepositoryPresentationController.GetReositoryLocation().setText(m_MyAmazingGitEngine.GetActiveRepository().GetPath().toString());
        });
    }
//    private void SetBranches() {
//        GridPane branchesGridPane = m_BranchesListController.GetBranchesGridPane();
//        branchesGridPane.getChildren().remove(0,branchesGridPane.getChildren().size());
//        List<Branch> branches = m_MyAmazingGitEngine.GetActiveRepository().GetMagit().GetBranches();
//
//        for(int i = 0; i < branches.size(); i++) {
//            createMenuButtonAndAddToGridPane(branches.get(i));
//        }
//        branchesGridPane.setVgap(5);
//        branchesGridPane.setAlignment(Pos.TOP_CENTER);
//    }

    public void CreateNewBranch(String i_BranchName) {
        LocalBranch newBranch = m_MyAmazingGitEngine.CreateNewBranchInActiveRepository(i_BranchName);
        m_BranchesListController.createMenuButtonAndAddToGridPane(newBranch);
    }

    public MenuButton CreateNewBranchForSpecificCommit(String i_BranchName, Commit  i_Commit) {
        LocalBranch newBranch = m_MyAmazingGitEngine.CreateNewBranchForSpecificCommit(i_BranchName, i_Commit);
        return m_BranchesListController.createMenuButtonAndAddToGridPane(newBranch);
    }

    public boolean HasActiveCommit() {
        return m_MyAmazingGitEngine.HasActiveCommit();
    }

    public boolean IsExistBranchInActiveRepository(String i_BranchName) {
        return m_MyAmazingGitEngine.IsExistBranchInActiveRepository(i_BranchName);
    }

    public void DeleteBranch(String i_BranchName) {
        m_MyAmazingGitEngine.DeleteBranch(i_BranchName);
    }

    public boolean HasFilesInWC() {
        return m_MyAmazingGitEngine.HasFilesInWC();
    }

    public boolean IsSomethingToCommit() {
        return m_MyAmazingGitEngine.IsSomethingToCommit();
    }

    public void SwitchHeadBranch(String i_BranchName) {
        m_MyAmazingGitEngine.SwitchHeadBranchAndDeployIt(i_BranchName);
    }

    public MyAmazingGitEngine GetEngine() {
        return m_MyAmazingGitEngine;
    }

//    public void ShowCurrentCommitSystemFiles(ActionEvent i_Event){ // Dekel
//        try {
//            //C:\Users\USER\Desktop\MyAmazingGit-GUI\Magit-GUI\src\CurrentCommitSystemFiles\CurrentCommitSystemFiles.fxml
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CurrentCommitSystemFiles/CurrentCommitSystemFiles.fxml"));
//            Parent root = (Parent) fxmlLoader.load();;
//            CurrentCommitSystemFilesController currentCommitSystemFilesController = (CurrentCommitSystemFilesController) fxmlLoader.getController();
//            currentCommitSystemFilesController.SetMainAppController(this);
//            boolean hasSomethingToShow = currentCommitSystemFilesController.Initialize(); // Lazy Solution
//            if (hasSomethingToShow) {
//                Stage stage = new Stage();
//                stage.setScene(new Scene(root));
//                stage.setTitle("current Commit System Files");
//                stage.setResizable(false);
//                stage.initModality(Modality.APPLICATION_MODAL);
//                stage.show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public boolean IsPathToXMLFile(String i_XMLRepositoryPathStr) {
        return m_MyAmazingGitEngine.IsPathToXMLFile(i_XMLRepositoryPathStr);
    }

    public boolean IsExistXMLFile(String i_XMLRepositoryPathStr) {
        return m_MyAmazingGitEngine.IsExistXMLFile(i_XMLRepositoryPathStr);
    }

    public XMLValidation CheckXMLValidation(MagitRepository i_MagitRepository) {
        return m_MyAmazingGitEngine.CheckXMLValidation(i_MagitRepository);
    }

    public boolean IsAlreadyDirectoryInSystem(MagitRepository i_MagitRepository) {
        return m_MyAmazingGitEngine.IsAlreadyDirectoryInSystem(i_MagitRepository);
    }

    public boolean IsAlreadyRepository(MagitRepository i_MagitRepository) {
        return m_MyAmazingGitEngine.IsAlreadyRepository(i_MagitRepository);
    }


    public void UpdateCommitTree(){
        m_CommitTreeController.InitializeCommitTree();
    }

//    public void UpdateCurrentCommitSystemFiles(ActionEvent i_Event) { // Dekel
//        m_CurrentCommitFileSystemController.InitializeCurrentCommit(); // Lazy Solution
//    }

    public void Commit(String text) {
        m_MyAmazingGitEngine.Commit(text);
//        m_CurrentCommitFileSystemController.InitializeCurrentCommit();
//        UpdateCommitTree();
    }

    public void MergeCommit(String text, Branch i_SecondParent) {
        m_MyAmazingGitEngine.MergeCommit(text, i_SecondParent.GetCommit());
        //m_CurrentCommitFileSystemController.InitializeCurrentCommit();
    }

    public void Merge(Branch i_BranchToMerge) {
        if (IsSomethingToCommit()) {
            ShowAlert(Alert.AlertType.WARNING, "Open changes", "Open changes!", "This is open changes. Can't merge with open changes!");
        } else {
            //Merger merger = m_MyAmazingGitEngine.GetMerger();
            //merger.SetCommits(m_MyAmazingGitEngine.GetActiveRepository().GetMagit().GetCommits());
            //merger.InitializeMerger(i_BranchToMerge);
            m_MyAmazingGitEngine.SetCommitInMerger();
            m_MyAmazingGitEngine.InitializeMerger(i_BranchToMerge);
            if (m_MyAmazingGitEngine.IsFastMerge()) {
                m_MyAmazingGitEngine.FastMerge();
                if (m_MyAmazingGitEngine.IsSomethingToCommit()) {
                    m_MyAmazingGitEngine.DeployHeadBranch();
                    //NotifyLoadRepositoryObservers();
                    Service.Methods.ShowAlert(Alert.AlertType.INFORMATION, "Fast Forward Merge", "Fast Forward Merge!", "Fast Forward Merge successfull!");
                } else {
                    ShowAlert(Alert.AlertType.INFORMATION, "Fast Forward Merge", "Nothing to merge", "This is Fast Forward Merge! Active branch is already contain the data of this branch!");
                }
            }
//        if(merger.IsFastMerge()){
//            if(merger.FastMerge()) { // Check if Right
//                ShowAlert(Alert.AlertType.INFORMATION, "Fast Forward Merge", "Nothing to merge", "This is Fast Forward Merge! Active branch is already contain the data of this branch!");
//            }
//            else {
//                Service.Methods.ShowAlert(Alert.AlertType.INFORMATION, "Fast Forward Merge", "Fast Forward Merge!", "Fast Forward Merge successfull!");
//            }
//        }
            else {
                List<Conflict> conflicts = m_MyAmazingGitEngine.GetConflict();
                if (conflicts.size() > 0) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Conflicts/Conflicts.fxml"));
                        //Parent root = (Parent) fxmlLoader.load();
                        fxmlLoader.load();
                        Parent root = fxmlLoader.getRoot();
                        ConflictsController conflictsController = fxmlLoader.getController();
                        conflictsController.SetConflicts(conflicts);
                        conflictsController.InitializeConflicts();
                        Stage stage = new Stage();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//            //else{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Merge/MergeMessage.fxml"));
                Parent root = null;
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                root = fxmlLoader.getRoot();
                MergeMessageController mergeMessageController = (MergeMessageController) fxmlLoader.getController();
                mergeMessageController.SetMainAppController(this);
                mergeMessageController.SetSecondParent(i_BranchToMerge);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Merge");
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait(); // Noy- added wait
                //}
                //ShowAlert(Alert.AlertType.INFORMATION, "Successfully merge", "Merge", "Branch merge with head branch successfully!");
            }
        }
    }

    public List<Branch> FindLBOfCommit(Commit i_Commit) {
        return m_MyAmazingGitEngine.FindLBOfCommit(i_Commit);
//        List<LocalBranch> branches = m_MyAmazingGitEngine.GetActiveRepository().GetMagit().GetBranches();
//        List<Branch> pointedToCommitBranches = new ArrayList<>();
//        for(LocalBranch localBranch : branches){
//            if(localBranch.GetCommit() == i_Commit){
//                pointedToCommitBranches.add(localBranch);
//            }
//        }
//        return pointedToCommitBranches;
    }
    public List<Branch> FindRBOfCommit(Commit i_Commit) {
        return m_MyAmazingGitEngine.FindRBOfCommit(i_Commit);
    }






    public void ShowDelta(Delta delta) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WorkingCopyStatus/WorkingCopyStatus.fxml"));
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WorkingCopyStatusController workingCopyStatusController = (WorkingCopyStatusController) fxmlLoader.getController();
        workingCopyStatusController.SetMainAppController(this);
        workingCopyStatusController.SetDelta(delta);
        workingCopyStatusController.InitalizeChanges();
        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.sizeToScene();
        //stage.setScene(new Scene(root));
        stage.setTitle("Open Changes!");
        stage.setResizable(true);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public List<ILoadRepositoryObserver> GetLoadRepositoryObservers() {
        return m_LoadRepositoryObservers;
    }

    public List<ICommitPerformedObserver> GetCommitPerformedObserver() {
        return m_CommitPerformedObservers;
    }

    public List<IBranchObserver> GetBranchObserver() {
        return m_BranchObservers;
    }

    public boolean HasBranchesExceptHead() {
        return m_MyAmazingGitEngine.HasBranchesExceptHead();
    }

    public boolean IsHeadBranch(String i_BranchName) {
        return m_MyAmazingGitEngine.IsHeadBranch(i_BranchName);
    }

    public boolean IsExistBranch(String i_BranchName) {
        return m_MyAmazingGitEngine.IsExistBranch(i_BranchName);
    }

    public boolean HasActiveRepository() {
        return  m_MyAmazingGitEngine.HasActiveRepository();
    }

    public void CleanCommitTree() {
        m_CommitTreeController.CleanTree();
    }

    public void InitializeCenterAferInitializeRepository() {
        m_CurrentCommitFileSystemController.InitializeAfterLoadRepositoryWithOutCommit();
    }

    public void  SetUserName(String i_Name) {
        m_MyAmazingGitEngine.SetUsername(i_Name);
        m_UserName.setText(i_Name);
    }

    public void CloneRepository(String i_PathToRepositoryToClone, String i_PathToDestinationToClone, String i_RepositoryName) throws IOException {
        m_MyAmazingGitEngine.CloneRepository(i_PathToRepositoryToClone, i_PathToDestinationToClone, i_RepositoryName);
        LoadExistRepository(i_PathToDestinationToClone);
    }

    @FXML
    public void Fetch(ActionEvent i_Event){
        m_MyAmazingGitEngine.Fetch();
        NotifyLoadRepositoryObservers();
    }

    @FXML
    public void Pull (ActionEvent i_Event) {
        boolean isHeadBranchInLocalIsRtb = m_MyAmazingGitEngine.GetActiveRepository().GetRemote().isHeadBranchInLocalIsRtb();
        if (isHeadBranchInLocalIsRtb) {
            if (!m_MyAmazingGitEngine.IsSomethingToCommit()) {
                if(!m_MyAmazingGitEngine.IsSmoethingToPush()){
                    m_MyAmazingGitEngine.Pull();
                    NotifyLoadRepositoryObservers();
                }
                else{
                    ShowAlert(Alert.AlertType.ERROR, "Error Rtb", "Rtb doesnt match his Remote Branch", "Please Push first!");
                }
            } else {
                ShowAlert(Alert.AlertType.ERROR, "Open Chnages", "Open Changes!", "Cannot Pull with Open Changes, Please Commit!");
            }
        }
        else{
            ShowAlert(Alert.AlertType.ERROR, "No Rtb", "Current Branch isn't RTB!", "Cannot Pull with Local Branch which is'nt Tracking Remote Branch!");
        }
    }
    @FXML
    public void Push (ActionEvent i_Event) {
        boolean isHeadBranchInLocalIsRtb = m_MyAmazingGitEngine.GetActiveRepository().GetRemote().isHeadBranchInLocalIsRtb();
        if (isHeadBranchInLocalIsRtb) {
            if (m_MyAmazingGitEngine.GetActiveRepository().GetRemote().IsRemoteClear()) {// if not pushed yet
                if (m_MyAmazingGitEngine.GetActiveRepository().GetRemote().IsRemoteBranchInLocalSameAsRemoteBranchInRemote()){
                    m_MyAmazingGitEngine.Push();
                    NotifyLoadRepositoryObservers();
                }
                else{
                    ShowAlert(Alert.AlertType.ERROR, "Not Sync", "Remote branch in local isnt same as remote branch in remote", "remote branch in remote has change since last time its sync");
                }
            } else {
                ShowAlert(Alert.AlertType.ERROR, "Open Chnages", "Open Changes!", "Cannot Pull with Open Changes, Please Commit!");
            }
        }
        else{
            ShowAlert(Alert.AlertType.ERROR, "No Rtb", "Current Branch isn't RTB!", "Cannot Pull with Local Branch which is'nt Tracking Remote Branch!");
        }
    }

    private void NotifyLoadRepositoryObservers() {
        for (ILoadRepositoryObserver iLoadRepositoryObserver : m_LoadRepositoryObservers){
            iLoadRepositoryObserver.update(null,null);
        }
    }

    public List<RemoteBranch> GetRBList() {
        return m_MyAmazingGitEngine.GetActiveRepository().GetRemote().GetRemoteBranches();
    }


    public String GetHeadBranchName() {// noy-validation
        return m_MyAmazingGitEngine.GetActiveRepository().GetMagit().GetHead().GetActiveBranch().GetName();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (m_MyAmazingGitEngine.GetActiveRepository().GetIsLocal()){
            m_FetchMenuItem.setDisable(false);
            m_PullMenuItem.setDisable(false);
            m_PushMenuItem.setDisable(false);

            m_ShowWCStatusBtn.setDisable(false);
            m_CommitBtn.setDisable(false);
            m_AddBranchBtn.setDisable(false);
            m_PullBtn.setDisable(false);
            m_PushBtn.setDisable(false);
            m_FetchBtn.setDisable(false);
        }
        else{
            m_FetchMenuItem.setDisable(true);
            m_PullMenuItem.setDisable(true);
            m_PushMenuItem.setDisable(true);

            m_ShowWCStatusBtn.setDisable(false);
            m_CommitBtn.setDisable(false);
            m_AddBranchBtn.setDisable(false);
            m_PullBtn.setDisable(true);
            m_PushBtn.setDisable(true);
            m_FetchBtn.setDisable(true);
        }
    }

    public boolean IsLocalRepository() {
        return m_MyAmazingGitEngine.IsLocalRepository();
    }

    public RadioButton GetAnimationRadioButton() {
        return m_AnimationRadioBtn;
    }
}