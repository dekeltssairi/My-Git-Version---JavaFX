package visual.CommitTree;

import MainApp.MainAppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class BranchNodeController {

    @FXML private Label m_BranchName;
    private String m_BranchType;
    private BranchNode m_BranchNode;

    public void SetBranchType(String i_BranchType) {
        m_BranchType = i_BranchType;
    }

    public void SetBranchName(String i_BranchName){
        m_BranchName.setText(i_BranchName);
        if(m_BranchType.equals("RemoteBranch")){
            //m_BranchName.setStyle("-fx-text-fill: red");
            m_BranchName.setStyle("-fx-text-fill: rgba(125,88,249,0.42)");
        }
    }

    public void SetBranchNode(BranchNode i_BranchNode) {
        m_BranchNode = i_BranchNode;
        SetBranchType(i_BranchNode.GetBranchType());
        SetBranchName(i_BranchNode.GetBranchName());
    }
}
