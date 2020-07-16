package visual.CommitTree;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import logic.*;
import logic.Commit;
import MainApp.MainAppController;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class CommitNode extends AbstractCell {

    private SortedSet<CommitNode> m_Sons = new TreeSet<CommitNode>() {
        @Override
        public Comparator comparator() {
            return super.comparator();
        }
    };

    public Commit GetCommit() {
        return m_Commit;
    }
    private Commit m_Commit;
    private  List<Branch> m_PointedBranches;
    private boolean m_located = false;
    private CommitNodeController commitNodeController;
    private MainAppController m_MainAppController;

    public CommitNode(Commit i_Commit, List<Branch> i_PointedBranches, MainAppController i_MainAppController) {
        m_Commit = i_Commit;
        m_PointedBranches = i_PointedBranches;
        m_MainAppController = i_MainAppController;
    }

    public void SetLocated(boolean i_IsLocated){
        m_located = i_IsLocated;
    }

    public boolean GetLocated(){
        return m_located;
    }

    @Override
    public Region getGraphic(Graph graph) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("commitNode.fxml");
            fxmlLoader.setLocation(url);
            GridPane root = fxmlLoader.load(url.openStream());
            commitNodeController = fxmlLoader.getController();
            commitNodeController.SetMainController(m_MainAppController);
            commitNodeController.setMember_CommitNode(this);

//            commitNodeController.setCommitMessage(m_Commit.GetMessage());
//            commitNodeController.setCommitter(m_Commit.GetCommitter());
//            commitNodeController.setCommitTimeStamp(m_Commit.GetDate());
//            commitNodeController.setBranchesList(m_PointedBranches);
//            commitNodeController.SetSha_1(m_Commit.GetSha1());
//            commitNodeController.SetParents(m_Commit.GetParentCommit());
//            commitNodeController.setContextMenu();
            return root;
        } catch (IOException e) {
            return new Label("Error when tried to create graphic node !");
        }
    }

    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(commitNodeController.getCircleRadius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitNode that = (CommitNode) o;

        return m_Commit.GetDate() != null ? m_Commit.GetDate().equals(that.m_Commit.GetDate()) : that.m_Commit.GetDate() == null;
    }

    @Override
    public int hashCode() {
        return m_Commit.GetDate() != null ? m_Commit.GetDate().hashCode() : 0;
    }

    public List<Branch> GetPointedBranches() {
        return m_PointedBranches;
    }
    public MainAppController GetMainAppController() {
        return m_MainAppController;
    }

    public CommitNodeController GetCommitNodeController() {
        return commitNodeController;
    }
}