package visual.layout;

import MainApp.MainAppController;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.layout.Layout;
import javafx.scene.control.RadioButton;
import logic.*;
import visual.CommitTree.*;

import java.util.*;

// simple test for scattering commits in imaginary tree, where every 3rd node is in a new 'branch' (moved to the right)
public class CommitTreeLayout implements Layout {
    MainAppController m_MainAppController;
    MyAmazingGitEngine m_Engine;

    public CommitTreeLayout(MainAppController i_MainAppController){
        m_MainAppController = i_MainAppController;
        m_Engine = m_MainAppController.GetEngine();
    }

    @Override
    public void execute(Graph graph) {
        final List<ICell> cells = graph.getModel().getAllCells();

        List<Commit> commitsPointedByBranches = getPointedCommitsByBranches();
        RadioButton unaccessibleCommittsRadioBtn = m_MainAppController.GetCommitTreeController().GetUnaccessibleCommittsRadioBtn();
        if(unaccessibleCommittsRadioBtn.isSelected()){
            List<Commit> commitsWithOutBranches = new ArrayList<>(m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetCommits().values());
            commitsWithOutBranches.removeAll(commitsPointedByBranches);
            List<Commit> commitsWithOutSonsAndBranches = new ArrayList<>();
            for (Commit commit: commitsWithOutBranches){
                if (!commitHasSon(commit)){
                    commitsWithOutSonsAndBranches.add(commit);
                }
            }
            commitsPointedByBranches.addAll(commitsWithOutSonsAndBranches);
        }

        if(commitsPointedByBranches.size() > 1){
            commitsPointedByBranches.sort(new Comparator<Commit>() {
                @Override
                public int compare(Commit o1, Commit o2) {
                    Commit A = o1;
                    Commit B = o2;
                    Date dateA = A.GetDateObject();
                    Date dateB = B.GetDateObject();
                    return dateB.compareTo(dateA);
                }
            });
        }


        for(int i = 0, columCounter = 0 ; i < commitsPointedByBranches.size(); i++){
            Commit pointedCommit = commitsPointedByBranches.get(i);
            //CommitNode pointedCommitNode = new CommitNode(pointedCommit);
            CommitNode c = (CommitNode) findCellOfCommitNode(pointedCommit, cells);
            if(!c.GetLocated()) {
                int YPosition = findYPosition(c, cells);
                int XPosition = columCounter * 50 + 10;
                graph.getGraphic(c).relocate(XPosition, YPosition * 50 + 50);
                c.SetLocated(true);
                columCounter += parentsPosition(XPosition, c, cells, graph);
                columCounter++;
            }
        }
    }

    private int parentsPosition(int i_XPosition, CommitNode i_Child, List<ICell> i_Cells, Graph i_Graph) {
        Commit commit = i_Child.GetCommit();
        Commit firstParent = null;
        Commit secondParent = null;
        List<Commit> parents = commit.GetParentCommit();
        int first =0;
        int second =0;
        if(parents == null) { // root
            return 0;
        }

        else{ // first parent exist
            if(parents.size() == 2){          //parents.get(1).GetSha1() != "There is no earlier version"){ // two parents
                if(parents.get(0).GetDateObject().compareTo(parents.get(1).GetDateObject()) > 0) { //
                    firstParent = parents.get(0); // first parent is bigger
                    secondParent = parents.get(1);
                } else{
                    firstParent = parents.get(1);
                    secondParent = parents.get(0);
                }
            }
            else { // only single parent
                firstParent = parents.get(0);
            }
        }
        CommitNode firstParentCommitNode = (CommitNode) findCellOfCommitNode(firstParent ,i_Cells);//new CommitNode(firstParent);

            if (!firstParentCommitNode.GetLocated()) {
                int YPosition = findYPosition(firstParentCommitNode, i_Cells);
                i_Graph.getGraphic(firstParentCommitNode).relocate(i_XPosition, YPosition * 50 + 50);
                firstParentCommitNode.SetLocated(true);
                first = parentsPosition(i_XPosition, firstParentCommitNode, i_Cells, i_Graph);
                //return 0;
            }

        if(secondParent != null) {
            CommitNode secondParentCommitNode = (CommitNode) findCellOfCommitNode(secondParent, i_Cells);
            if (!secondParentCommitNode.GetLocated()) {
                int YPosition = findYPosition(secondParentCommitNode, i_Cells);
                i_Graph.getGraphic(secondParentCommitNode).relocate(i_XPosition + 50, YPosition * 50 + 50);
                secondParentCommitNode.SetLocated(true);
                second = parentsPosition(i_XPosition + 50, secondParentCommitNode, i_Cells, i_Graph) + 1;
            }
        }
        return first + second;
    }

    private List<Commit> getPointedCommitsByBranches() {
        List<LocalBranch> branches = m_Engine.GetActiveRepository().GetMagit().GetBranches();
        List<Commit> commitsList = new ArrayList<>();
        for(Branch branch : branches){
            if(!commitsList.contains(branch.GetCommit()) && branch.GetCommit() != null)
                commitsList.add(branch.GetCommit());
        }

        if (m_Engine.GetActiveRepository().GetIsLocal()) {
            List<RemoteBranch> remoteBranches = m_MainAppController.GetRBList();
            for (RemoteBranch RB : remoteBranches) {
                if (!commitsList.contains(RB.GetCommit()) && RB.GetCommit() != null)
                    commitsList.add(RB.GetCommit());
            }
        }
        return commitsList;
    }

    private int findYPosition(CommitNode c, List<ICell> i_Cells) {
        for(int i = 0; i < i_Cells.size() ; i++){
            CommitNode commitNode = (CommitNode) i_Cells.get(i);
            if(commitNode.equals(c))
                return i;
                //return i_Cells.size() - i;
        }
        return -1;
    }

    private ICell findCellOfCommitNode(Commit i_CommitNode, List<ICell>i_Cells) {
        ICell commitNodeCell = null;
        for(ICell cell : i_Cells){
            CommitNode commitNode = (CommitNode)cell;
            if(commitNode.GetCommit().equals(i_CommitNode))
                commitNodeCell = cell;
        }
        return commitNodeCell;
    }

    private boolean commitHasSon(Commit i_Parent) {
        boolean hasSon = false;
        Collection<Commit> commits =  m_MainAppController.GetEngine().GetActiveRepository().GetMagit().GetCommits().values();
        for (Commit commit: commits){
            if (commit.GetParentCommit() != null){

                if (commit.GetParentCommit().get(0).equals(i_Parent)){
                    hasSon = true;
                }
                if (commit.GetParentCommit().size() == 2){
                    if (commit.GetParentCommit().get(1).equals(i_Parent)){
                        hasSon = true;
                    }
                }
            }
        }
        return hasSon;
    }
}