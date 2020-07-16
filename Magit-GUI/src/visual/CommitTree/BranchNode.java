package visual.CommitTree;

import MainApp.MainAppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;

public class BranchNode extends ListCell<BranchNode> {
    private String m_BranchType;
    private String m_BranchName;
    private boolean m_IsHead; // noy-validation
    private boolean m_IsRTB;
    private BranchNodeController branchNodeController;

    public String GetBranchName() {
        return m_BranchName;
    }
    public String GetBranchType() {
        return m_BranchType;
    }

    public BranchNode(){

    }

    public BranchNode(String i_BranchName, String i_BranchType, boolean i_IsHead) throws IOException {
        m_BranchName = i_BranchName;
        m_BranchType = i_BranchType;
        m_IsHead = i_IsHead;// noy-validation
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("BranchNode.fxml");
        fxmlLoader.setLocation(url);
        Label root = fxmlLoader.load(url.openStream());
        branchNodeController = fxmlLoader.getController();
        branchNodeController.SetBranchNode(this);
    }

    public void SetIsRTB(boolean i_IsRTB) {
        m_IsRTB = i_IsRTB;
    }

    public boolean IsRemote(){
        return m_BranchType.equals("RemoteBranch");
    }

    @Override
    protected void updateItem(BranchNode item, boolean empty) {
        super.updateItem(item, empty);
        setText(item == null ? "" : item.GetBranchName());
        if (item != null) {
            String value = item.m_BranchType;
            setTextFill(value == "RemoteBranch" ? Color.MEDIUMPURPLE : item.m_IsHead ? Color.BLUE : item.m_IsRTB ? Color.RED : Color.GREEN); // noy-validation
        }
    }

    @Override
    public String toString(){
        return m_BranchName;
    }
}
