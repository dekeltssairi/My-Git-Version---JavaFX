package visual.CommitTree;

import BranchesList.BranchesListController;
import CurrentCommitSystemFiles.CurrentCommitSystemFilesController;
import MainApp.MainAppController;
import Observers.Notifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.*;
import static Service.Methods.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class CommitNodeController extends Notifier {

    private MainAppController m_MainAppController;
    @FXML private Label commitTimeStampLabel;

    private CommitNode member_CommitNode;
    @FXML private Label messageLabel;
    @FXML private Label committerLabel;
    @FXML private Circle CommitCircle;
    @FXML private ListView m_BranchesList;
    @FXML private GridPane m_CommitNode;
    //private Paint m_Paint;

    private String m_Sha_1;
    private List<Commit> m_Parents;

    public void SetMainController(MainAppController i_mainAppController) {
        m_MainAppController = i_mainAppController;
        List list = m_MainAppController.GetBranchObserver();
        AddObservers(list);
        //m_Paint = CommitCircle.getFill();
        mouseEnterClickedOnBranchListEvenet();
        mouseEnterEvent();
        mouseLeaveEvent();

    }

    public void SetSha_1(String i_Sha1) {
        m_Sha_1 = i_Sha1;
    }

    public void SetParents(List<Commit> i_Parents) {
        m_Parents = i_Parents;
    }

    public ContextMenu createContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem sha_1Item = new MenuItem("Get sha-1");
        sha_1Item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ShowAlert(Alert.AlertType.INFORMATION, "Sha_1 of commit", "Sha_1 of commit", m_Sha_1);
            }
        });
        MenuItem parentsSha_1Item = new MenuItem("Show parents sha_1");
        parentsSha_1Item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String parentsSha_1;
                if(m_Parents.size() == 1){
                    parentsSha_1 = "First parent sha1: " + m_Parents.get(0).GetSha1();
                }else{
                    parentsSha_1 = "First parent sha1: " + m_Parents.get(0).GetSha1() + System.lineSeparator() +
                            "Second parent sha1: " + m_Parents.get(1).GetSha1();
                }
                ShowAlert(Alert.AlertType.INFORMATION, "Sha_1 of parents", "Sha_1 of parents", parentsSha_1);
            }
        });

        MenuItem addBranchItem = new MenuItem("Add branch to here");
        addBranchItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(m_MainAppController.GetEngine().GetActiveRepository().GetIsLocal() && getRBPointed() != null){
                    RemoteBranch RB = getRBPointed();
                    m_MainAppController.CreateRTBWindow(RB.GetName(), RB.GetCommit());
                }
                else{
                    BranchesListController branchesListController = m_MainAppController.GetBranchesListController();
                    branchesListController.SetCommit(member_CommitNode.GetCommit());
                    branchesListController.AddBranchWindow(event);
                }
            }
        });

        MenuItem deltaWithParents = new MenuItem("Show delta with parent");
        Commit commit = member_CommitNode.GetCommit();
        if(member_CommitNode.GetCommit().GetParentCommit() != null) {
            if (member_CommitNode.GetCommit().GetParentCommit().size() == 2) {
                deltaWithParents = new Menu("Show Delta With Parents");
                MenuItem deltaWithParentsOne = new MenuItem(commit.GetParentCommit().get(0).GetSha1());
                MenuItem deltaWithParentsTwo = new MenuItem(commit.GetParentCommit().get(1).GetSha1());

                deltaWithParentsOne.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Commit parentCommit = commit.GetParentCommit().get(0);
                        Delta delta = member_CommitNode.GetMainAppController().GetEngine().GetDelta(parentCommit, commit);
                        member_CommitNode.GetMainAppController().ShowDelta(delta);
                    }

                });

                deltaWithParentsTwo.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Commit parentCommit = commit.GetParentCommit().get(1);
                        Delta delta = member_CommitNode.GetMainAppController().GetEngine().GetDelta(parentCommit, commit);
                        member_CommitNode.GetMainAppController().ShowDelta(delta);
                    }

                });
                ((Menu) deltaWithParents).getItems().addAll(deltaWithParentsOne, deltaWithParentsTwo);
            } else {
                deltaWithParents.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        Commit parentCommit = commit.GetParentCommit().get(0);
                        Delta delta = member_CommitNode.GetMainAppController().GetEngine().GetDelta(parentCommit, commit);
                        member_CommitNode.GetMainAppController().ShowDelta(delta);
                    }
                });
            }
        }

        MenuItem showCommitFilesItem = new MenuItem("Show files of commit");
        showCommitFilesItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CurrentCommitSystemFiles/CurrentCommitSystemFiles.fxml"));
                Parent root = null;
                try {
                    root = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CurrentCommitSystemFilesController currentCommitSystemFilesController = (CurrentCommitSystemFilesController) fxmlLoader.getController();
                currentCommitSystemFilesController.SetMainAppController(m_MainAppController);
                currentCommitSystemFilesController.InitializeByCommit(member_CommitNode.GetCommit());
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(member_CommitNode.GetCommit().GetSha1());
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait(); // Noy- added wait
            }
        });

        MenuItem resetHeadBranchItem = new MenuItem("Reset head branch to here");
        resetHeadBranchItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(m_MainAppController.HasFilesInWC()) {
                    if (m_MainAppController.IsSomethingToCommit()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Open changes in head branch");
                        alert.setTitle("Open changes in head branch");
                        alert.setContentText("There are open changes in head branch. Do you want to continue? If you perss OK you lose information." + System.lineSeparator() + "If you exit or press cancle the operation cancle");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            m_MainAppController.ResetHeadBranch(member_CommitNode.GetCommit());
                        }
                    }
                    else{
                        m_MainAppController.ResetHeadBranch(member_CommitNode.GetCommit());
                    }
                }
                else{
                    m_MainAppController.ResetHeadBranch(member_CommitNode.GetCommit());
                }
                setChanged();
                notifyObservers();
            }
        });


        if(member_CommitNode.GetCommit().GetParentCommit() == null){
            deltaWithParents.setDisable(true);
            parentsSha_1Item.setDisable(true);
        }

        contextMenu.getItems().addAll(sha_1Item, parentsSha_1Item, deltaWithParents, addBranchItem, showCommitFilesItem, resetHeadBranchItem);
        return contextMenu;
    }

    private RemoteBranch getRBPointed() {
        List<RemoteBranch> RBList = m_MainAppController.GetRBList();
        RemoteBranch pointedRB = null;
        for(Object branch : m_BranchesList.getItems()) { // there is only one RB
            if (branch instanceof BranchNode) {
                if (((BranchNode) branch).IsRemote()) {
                    for (RemoteBranch RB : RBList) {
                        if (RB.GetName().equals(((BranchNode) branch).GetBranchName())) {
                            pointedRB = RB;
                        }
                    }
                }
            }
        }
        return pointedRB;
    }

    public void setContextMenu(){
        m_CommitNode.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                ContextMenu contextMenu = createContextMenu();
                contextMenu.show(m_CommitNode, event.getScreenX(), event.getScreenY());
            }
        });
    }

    public void setBranchesList(List<Branch> i_PointedBranches) throws IOException {
        ObservableList<BranchNode> branchNodeList = FXCollections.observableArrayList();
        BranchesListController branchesListController = m_MainAppController.GetBranchesListController();
        for(Branch branch : i_PointedBranches) {
            if (branch instanceof RemoteBranch) {
                BranchNode remoteBranchNode = new BranchNode(branch.GetName(), "RemoteBranch", false);
                remoteBranchNode.SetIsRTB(false);
                branchNodeList.add(remoteBranchNode);
            } else {
                BranchNode localBranchNode;// noy-validation
                if(branch.GetName().equals(m_MainAppController.GetHeadBranchName())){ // noy-validation
                    localBranchNode = new BranchNode(branch.GetName(), "LocalBranch", true);
                }
                else{
                    localBranchNode = new BranchNode(branch.GetName(), "LocalBranch", false);
                }
                localBranchNode.SetIsRTB(branchesListController.IsRTB(branch.GetName()));
                branchNodeList.add(localBranchNode);
            }
        }
        m_BranchesList.setItems(branchNodeList);
        m_BranchesList.setCellFactory(new Callback<ListView<BranchNode>, ListCell<BranchNode>>() {
            @Override public ListCell<BranchNode> call(ListView<BranchNode> list) {
                return new BranchNode();
            }
        });
    }

    public void setCommitTimeStamp(String timeStamp) {
        commitTimeStampLabel.setText(timeStamp);
        commitTimeStampLabel.setTooltip(new Tooltip(timeStamp));
    }

    public void setCommitter(String committerName) {
        committerLabel.setText(committerName);
        committerLabel.setTooltip(new Tooltip(committerName));
    }

    public void setCommitMessage(String commitMessage) {
        messageLabel.setText(commitMessage);
        messageLabel.setTooltip(new Tooltip(commitMessage));
    }

    public int getCircleRadius() {
        return (int)CommitCircle.getRadius();
    }

    public void setMember_CommitNode(CommitNode member_CommitNode) throws IOException {
        this.member_CommitNode = member_CommitNode;
        Commit commit = member_CommitNode.GetCommit();
        setCommitMessage(commit.GetMessage());
        setCommitter(commit.GetCommitter());
        setCommitTimeStamp(commit.GetDate());
        setBranchesList(member_CommitNode.GetPointedBranches());
        SetSha_1(commit.GetSha1());
        SetParents(commit.GetParentCommit());
        setContextMenu();
    }

    private void mouseEnterClickedOnBranchListEvenet() {
        m_BranchesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (m_MainAppController.GetAnimationRadioButton().isSelected())
                    BeamHerats();
            }
        });
    }

    private void mouseLeaveEvent() {
        m_CommitNode.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                unBoltCommitNodeAndHisAnsector();
            }
        });
    }

    private void unBoltCommitNodeAndHisAnsector() {
        String title = m_MainAppController.GetPrimaryStage().getTitle();
        Paint paint;
        if(title.equals("Magit")){
            paint = Paint.valueOf("#087fee");
        } else if(title.equals("M-A-Git")){
            paint = Paint.valueOf("#ff4fff");
        } else if(title.equals("My Amazing Git")){
            paint = Paint.valueOf("#01eb10");
        } else{
            paint = Paint.valueOf("#eb6f00");
        }
        m_MainAppController.GetCommitTreeController().UnBoltAncetorRec(this, paint);
    }

    private void mouseEnterEvent() {
        m_CommitNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                boltCommitNodeAndHisAnsector();
            }
        });
    }
    public Circle GetCircle(){
        return CommitCircle;
    }

    private void boltCommitNodeAndHisAnsector() {
       m_MainAppController.GetCommitTreeController().BoltAncetorRec(this, 15, Paint.valueOf("Black"));
    }

    public synchronized void BeamHerats() {
        m_MainAppController.GetCommitTreeController().BeamHeartsRec(this);
    }

    public CommitNode GetCommitNode() {
        return member_CommitNode;
    }

//    public void SetPaint(Paint i_Paint) {
//        m_Paint = i_Paint;
//    }
}