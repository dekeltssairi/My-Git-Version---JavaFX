package visual.CommitTree;

import DataStructures.ListWithoutDuplications;
import MainApp.MainAppController;
import Observers.*;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.*;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import logic.*;
import visual.layout.CommitTreeLayout;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.*;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR;

public class CommitTreeController implements Initializable, ILoadRepositoryObserver, ICommitPerformedObserver, IBranchObserver, Observer {

    private MainAppController m_MainAppController;
    @FXML private ScrollPane m_ScrollPane;
    @FXML private RadioButton m_UnaccessibleCommittsRadioBtn;

    private Graph m_Tree = new Graph();

    //@FXML private CommitNode m_CommitNode;
    public void SetMainController(MainAppController i_MainAppController) {
        m_MainAppController = i_MainAppController;
        m_UnaccessibleCommittsRadioBtn.setDisable(true);
    }

    public RadioButton GetUnaccessibleCommittsRadioBtn() {
        return m_UnaccessibleCommittsRadioBtn;
    }

    public void InitializeCommitTree(){
        m_Tree = new Graph();
        createCommitsTree();
    }

    private void createCommitsTree() {
        Collection<Commit> Commits;

         if(!m_UnaccessibleCommittsRadioBtn.isSelected()){
             Commits = getAccessibleCommits();
         } else{
             Commits = m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetCommits().values();
         }

        final Model model = m_Tree.getModel();
        List<CommitNode> commitNodeList = new ArrayList<>();

        m_Tree.beginUpdate();

        for (Commit commit : Commits) {
            List<Branch> pointedBranches = m_MainAppController.FindLBOfCommit(commit);
            if(m_MainAppController.GetEngine().GetActiveRepository().GetIsLocal()){
                List<Branch> RBList = m_MainAppController.FindRBOfCommit(commit);
                for(Branch branch : RBList){
                    pointedBranches.add(branch);
                }
            }

            CommitNode commitNode = new CommitNode(commit, pointedBranches, m_MainAppController);
            commitNodeList.add(commitNode);
        }
//        for(CommitNode commitNode : commitNodeList){
//            if(commitNode.GetCommit().GetParentCommit() != null){
//                List<CommitNode> parents = findParent(commitNode, commitNodeList); // change to list of parents
//                for (CommitNode parentCommitNode : parents)
//                    parentCommitNode.SetSon(commitNode);
//            }
//        }
        int listSize = commitNodeList.size();
        if (listSize == 1) {
            ICell nodeA = commitNodeList.get(0);
            model.addCell(nodeA);
        }
        else {
            for (int i = 0; i < listSize; i++) {
                for (int j = i + 1; j < listSize; j++) {
                    ICell nodeA = commitNodeList.get(i);
                    ICell nodeB = commitNodeList.get(j);
                    //if (((CommitNode) nodeA).GetCommit().GetParentCommit() != null && ((CommitNode) nodeB).GetCommit().GetParentCommit() != null) {
                    if ((((CommitNode) nodeA).GetCommit().GetParentCommit() != null && ((CommitNode) nodeA).GetCommit().GetParentCommit().contains(((CommitNode) nodeB).GetCommit())) //== ((CommitNode)nodeB).GetCommit()
                            || (((CommitNode) nodeB).GetCommit().GetParentCommit() != null && ((CommitNode) nodeB).GetCommit().GetParentCommit().contains(((CommitNode) nodeA).GetCommit()))) {// == ((CommitNode)nodeA).GetCommit()){
                        if (!model.getAddedCells().contains(nodeA))
                            model.addCell(nodeA);
                        if (!model.getAddedCells().contains(nodeB))
                            model.addCell(nodeB);

                        final Edge edgeC12 = new Edge(nodeA, nodeB);
                        model.addEdge(edgeC12);
                    }
                    //}
                }
            }
        }

        m_Tree.getModel().getAddedCells().sort(new Comparator<ICell>() {
            @Override
            public int compare(ICell o1, ICell o2) {
                //if(o1 != null && o2 != null) {
                    CommitNode A = (CommitNode) o1;
                    CommitNode B = (CommitNode) o2;
                    Date dateA = A.GetCommit().GetDateObject();
                    Date dateB = B.GetCommit().GetDateObject();
                    return dateB.compareTo(dateA);// if res > 0, dateA > dateB
                //}
            }
        });

        m_Tree.endUpdate();
        m_Tree.layout(new CommitTreeLayout(m_MainAppController));


        PannableCanvas canvas = m_Tree.getCanvas();
        Platform.runLater(() -> {
            m_Tree.getUseViewportGestures().set(false);
            m_Tree.getUseNodeGestures().set(false);
        });

        m_ScrollPane.setContent(canvas);
    }

    private List<Commit> getAccessibleCommits() {
        List<Commit> commits = new ListWithoutDuplications();
        List<LocalBranch> branchesList = m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetBranches();
        List<Branch> allBranches = new ArrayList<>();
        for(LocalBranch branch : branchesList){
            allBranches.add(branch);
        }
        if(m_MainAppController.GetEngine().GetActiveRepository().GetIsLocal()) {
            List<RemoteBranch> remoteBranchesList = m_MainAppController.GetRBList();
            for(RemoteBranch remoteBranch : remoteBranchesList){ // added noy
                allBranches.add(remoteBranch);
            }
        }
        for(Branch branch : allBranches){
            getAccessibleCommitsRec(branch.GetCommit(), commits);
        }
        return commits;
    }

    private void getAccessibleCommitsRec(Commit i_Commit, List<Commit> commits) {
        if(i_Commit != null) {
            commits.add(i_Commit);
            List<Commit> parents = i_Commit.GetParentCommit();
            if (parents != null) {
                if (parents.size() > 0)
                    getAccessibleCommitsRec(parents.get(0), commits);
                if (parents.size() == 2)
                    getAccessibleCommitsRec(parents.get(1), commits);
            }
        }
    }

//    private CommitNode findParent(CommitNode i_CommitNode, List<CommitNode> commitNodeList ){
//        CommitNode parent = null;
//        Commit parentCommit = i_CommitNode.GetCommit().GetParentCommit();
//        for(CommitNode commitNode : commitNodeList){
//            if(commitNode.GetCommit() == parentCommit)
//                parent = commitNode;
//        }
//        return parent;
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //m_CommitTree = new GridPane();
        System.out.println("Try");
    }


    public void CleanTree() {
        m_Tree.beginUpdate();
        m_Tree.endUpdate();
        PannableCanvas canvas = m_Tree.getCanvas();
        m_ScrollPane.setContent(canvas);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(m_MainAppController.HasActiveCommit()){
            m_UnaccessibleCommittsRadioBtn.setDisable(false);
            InitializeCommitTree();
        }
        else{
            CleanTree();
        }
    }

    public void BoltAncetorRec(CommitNodeController i_CommitNodeController, double i_Radius, Paint i_Color) {

        i_CommitNodeController.GetCircle().setRadius(i_Radius);
        i_CommitNodeController.GetCircle().setFill(i_Color);
        List<Commit> parentsCommit = i_CommitNodeController.GetCommitNode().GetCommit().GetParentCommit();
        if (parentsCommit != null) {
            List<ICell> cells = m_Tree.getModel().getAllCells();
            Commit parentCommit = parentsCommit.get(0);
            for (ICell cell : cells) {
                CommitNode commitNode = (CommitNode) cell;
                if (commitNode.GetCommit() == parentCommit) {
                    BoltAncetorRec(commitNode.GetCommitNodeController(), i_Radius, i_Color);
                }
            }
            if (parentsCommit.size() == 2) {
                parentCommit = parentsCommit.get(1);
                for (ICell cell : cells) {
                    CommitNode commitNode = (CommitNode) cell;
                    if (commitNode.GetCommit() == parentCommit) {
                        BoltAncetorRec(commitNode.GetCommitNodeController(), i_Radius, i_Color);
                    }
                }
            }
        }
    }

    public void UnBoltAncetorRec(CommitNodeController i_CommitNodeController, Paint i_Paint) {
        BoltAncetorRec(i_CommitNodeController, 10, i_Paint);
    }

    public void BeamHeartsRec(CommitNodeController i_CommitNodeController) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), i_CommitNodeController.GetCircle());
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(4);
        scaleTransition.setToX(2);
        scaleTransition.setToY(2);
        scaleTransition.play();


        List<Commit> parentsCommit = i_CommitNodeController.GetCommitNode().GetCommit().GetParentCommit();
        if (parentsCommit != null) {
            List<ICell> cells = m_Tree.getModel().getAllCells();
            Commit parentCommit = parentsCommit.get(0);
            for (ICell cell : cells) {
                CommitNode commitNode = (CommitNode) cell;
                if (commitNode.GetCommit() == parentCommit) {
                    BeamHeartsRec(commitNode.GetCommitNodeController());
                }
            }

            if (parentsCommit.size() == 2) {
                parentCommit = parentsCommit.get(1);
                for (ICell cell : cells) {
                    CommitNode commitNode = (CommitNode) cell;
                    if (commitNode.GetCommit() == parentCommit) {
                        BeamHeartsRec(commitNode.GetCommitNodeController());
                    }
                }
            }


        }
    }
}