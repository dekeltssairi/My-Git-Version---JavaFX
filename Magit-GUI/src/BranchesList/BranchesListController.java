package BranchesList;

import CreateNewBranch.CreateNewBranchController;
import MainApp.MainAppController;
import Observers.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.*;
import logic.*;
import logic.Commit;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static Service.Methods.ShowAlert;

public class BranchesListController extends Notifier implements ILoadRepositoryObserver, IBranchObserver, Observer {

    @FXML private GridPane m_BranchesGridPane;
    private MainAppController m_MainAppController;
    private Commit m_Commit;

    public void SetCommit(Commit i_Commit){
        m_Commit = i_Commit;
    }

    public void SetMainAppController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
        List list = m_MainAppController.GetBranchObserver();
        AddObservers(list);
//        list = m_MainAppController.GetLoadRepositoryObservers();
//        AddObservers(list);

    }

    public GridPane GetBranchesGridPane() {
        return m_BranchesGridPane;
    }

    @FXML
    //public void AddBranchWindow(ActionEvent i_Event, Commit i_Commit){
        public void AddBranchWindow(ActionEvent i_Event){
        try {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            if (m_MainAppController.HasActiveCommit()) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CreateNewBranch/CreateNewBranch.fxml"));
                Parent root = (Parent) fxmlLoader.load();
                CreateNewBranchController createNewBranchController = (CreateNewBranchController) fxmlLoader.getController();
                createNewBranchController.SetMainAppController(m_MainAppController);
                createNewBranchController.SetCommit(m_Commit);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Add Branch");
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait(); // wait or not ????
            } else{
                errorAlert.setTitle("No active commit!");
                errorAlert.setContentText("No active Commit yet!");
                errorAlert.showAndWait();
            }
//            setChanged();
//            notifyObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetBranches(){
        m_BranchesGridPane.getChildren().remove(0,m_BranchesGridPane.getChildren().size());
        List<LocalBranch> branches = m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetBranches();

        for(int i = 0; i < branches.size(); i++) {
            createMenuButtonAndAddToGridPane(branches.get(i));
        }

        if(m_MainAppController.GetEngine().GetActiveRepository().GetIsLocal()){
            List<RemoteBranch> remoteBranchesList = m_MainAppController.GetRBList();
            for(int i = 0; i < remoteBranchesList.size(); i++) {
                createMenuButtonAndAddToGridPane(remoteBranchesList.get(i));
            }
        }

        m_BranchesGridPane.setVgap(5);
        m_BranchesGridPane.setAlignment(Pos.TOP_CENTER);
    }

    public MenuButton createMenuButtonAndAddToGridPane(Branch i_Branch) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        LocalBranch head = m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetActiveBranch();
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem deployItem = new MenuItem("Set as HEAD and deploy");
        MenuItem mergeItem = new MenuItem("Merge to HEAD branch");
        MenuItem addBranchToRRItem = new MenuItem("Add branch to remote repository");

        MenuButton menuBtn = new MenuButton();
        menuBtn.setText(i_Branch.GetName());

        if(i_Branch instanceof RemoteBranch){
            menuBtn.getItems().addAll(mergeItem, deployItem);
        }
        else{
            menuBtn.getItems().addAll(deleteItem, deployItem, mergeItem);
            if(m_MainAppController.IsLocalRepository() && !IsRTB(menuBtn.getText())) {
                menuBtn.getItems().add(addBranchToRRItem);
            }
        }


//        if(i_Branch.GetName().equals(head.GetName()) && i_Branch instanceof LocalBranch){
//            menuBtn.getItems().get(0).setDisable(true);
//            menuBtn.getItems().get(2).setDisable(true);
//            //menuBtn.setStyle("-fx-background-color: #7ABEC9");
//            menuBtn.setStyle("-fx-background-color: rgba(13,221,217,0.57)");
//        }

        if(i_Branch instanceof LocalBranch){
            if(i_Branch.GetName().equals(head.GetName())){
                menuBtn.getItems().get(0).setDisable(true);
                menuBtn.getItems().get(2).setDisable(true);
                //menuBtn.setStyle("-fx-background-color: #7ABEC9");
                menuBtn.setStyle("-fx-background-color: rgba(13,221,217,0.57)");
            } else if(IsRTB(i_Branch.GetName())){
                menuBtn.setStyle("-fx-background-color: red");
            } else{
                menuBtn.setStyle("-fx-background-color: green");
            }
        }

        if(i_Branch instanceof RemoteBranch){
            menuBtn.setStyle("-fx-background-color:#d2ceff");
        }
//        else {
//            menuBtn.setStyle("-fx-background-color: #7ABEC9");
//        }

        deleteItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_BranchesGridPane.getChildren().remove(menuBtn);
                m_MainAppController.DeleteBranch(menuBtn.getText());
                setChanged();
                notifyObservers();
                alert.setTitle("successfully delete branch");
                alert.setContentText("Branch deleted successfully!");
                alert.showAndWait();
            }
        });

        deployItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(m_MainAppController.HasActiveCommit()) {
                    //Branch branch = m_MainAppController.GetEngine().FindBranchByName(menuBtn.getText());
                    if(i_Branch instanceof RemoteBranch){ // is RB // NOY-FRIDAY
                        //menuBtn.setStyle("-fx-background-color:#d2ceff");
                        m_MainAppController.DeployRBWindow(i_Branch);
                    } else {
                        m_MainAppController.SwitchHeadBranch();
                        UpdateHeadBranchStyle(menuBtn);
                        m_MainAppController.SwitchHeadBranch(menuBtn.getText());
                        alert.setTitle("Switch Branch");
                        alert.setContentText("Successfully switch head branch to " + menuBtn.getText() + "!");
                        alert.showAndWait();
                        //m_MainAppController.UpdateCurrentCommitSystemFiles(event);
                    }
//                    else {
//                        if (m_MainAppController.SwitchHeadBranch()) {
//                            UpdateHeadBranchStyle(menuBtn);
//                            m_MainAppController.SwitchHeadBranch(menuBtn.getText());
//                            alert.setTitle("Switch Branch");
//                            alert.setContentText("Successfully switch head branch to " + menuBtn.getText() + "!");
//                            alert.showAndWait();
//                            setChanged(); // noy-validation
//                            notifyObservers();
//                        }
//                    }
                    setChanged(); // noy-validation
                    notifyObservers();
                }else{
                    ShowAlert(Alert.AlertType.ERROR, "No active commit yet!", "No active commit yet!", "There are no active commit. Nothing to deploy");
                }
            }

//            private void updateHeadBranchStyle(MenuButton menuBtn) {
//                LocalBranch head = m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetActiveBranch();
//                ObservableList<Node> gridPaneItems = m_BranchesGridPane.getChildren();
//                menuBtn.setStyle("-fx-background-color: #7ABEC9");
//                menuBtn.getItems().get(0).setDisable(true);
//                menuBtn.getItems().get(3).setDisable(true);
//                for(Node node : gridPaneItems){
//                    MenuButton mb = (MenuButton)node;
//                    if(mb.getText().equals(head.GetName())){
//                        mb.setStyle("CLEAR()");
//                        mb.getItems().get(0).setDisable(false);
//                        mb.getItems().get(3).setDisable(false);
//                    }
//                }
//            }
        });

        mergeItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_MainAppController.Merge(i_Branch);
                setChanged();
                notifyObservers();
                //m_MyAmazingGitEngine.GetActiveRepository().GetMagit().GetActiveCommit().GetParentCommit().add(i_Branch.GetCommit());

                //m_MainAppController.UpdateCommitTree();
// update tree
                //Commit("Which Text To Put??");
//                alert.setTitle("successfully merge");
//                alert.setContentText("Branch merge with head branch successfully!");
//                alert.showAndWait();
            }
        });

        addBranchToRRItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_MainAppController.GetEngine().PushBranchToRR((LocalBranch) i_Branch);
                setChanged();
                notifyObservers();
            }
        });
        m_BranchesGridPane.add(menuBtn,0, m_BranchesGridPane.impl_getRowCount());
//        menuBtn.setPrefWidth(Region.USE_COMPUTED_SIZE);
//        menuBtn.setMinWidth(Region.USE_PREF_SIZE);
        return menuBtn;
    }

    public boolean IsRTB(String i_BranchName){
        boolean isRTB = false;
        if(m_MainAppController.IsLocalRepository()){
            List<RemoteBranch> RBList = m_MainAppController.GetEngine().GetActiveRepository().GetRemote().GetRemoteBranches();
            for(RemoteBranch remoteBranch : RBList){
                isRTB = remoteBranch.GetOriginName().equals(i_BranchName);
                if(isRTB)
                    break;
            }
        }
        return isRTB;
    }
    public void UpdateHeadBranchStyle(MenuButton menuBtn) {
        LocalBranch head = m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetActiveBranch();
        ObservableList<Node> gridPaneItems = m_BranchesGridPane.getChildren();
        menuBtn.setStyle("-fx-background-color: rgba(13,221,217,0.57)");
        menuBtn.getItems().get(0).setDisable(true);
        menuBtn.getItems().get(2).setDisable(true);
        for(Node node : gridPaneItems){
            MenuButton mb = (MenuButton)node;
            if(mb.getText().equals(head.GetName())){
                //if(mb.getItems().size() != 2){ // todo: find another solution !!!
                    mb.setStyle("CLEAR()");
                    mb.getItems().get(0).setDisable(false);
                    mb.getItems().get(2).setDisable(false);
                //}
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        SetBranches();
        m_Commit = null;
    }
}
