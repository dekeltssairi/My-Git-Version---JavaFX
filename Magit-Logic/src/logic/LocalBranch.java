package logic;
import java.nio.file.*;
import static DekelNoy3rd.Service.Methods.*;

public class LocalBranch extends Branch {

    public LocalBranch(String i_newBranchName, Commit i_Commit, Path i_Path) {
        super(i_newBranchName,i_Commit,i_Path);
        createBranchTextFile(); // this suppose to override
    }

    public void createBranchTextFile() {
        String pathStr = m_Path.normalize().toString();
        String content;
        if (m_Commit == null) {
            content = "";
        } else {
            content = m_Commit.GetSha1();
        }
        CreateTextFile(pathStr, content);
    }

    public void SetCommit(Commit i_Commit) {
        m_Commit = i_Commit;
    }

    public RemoteBranch ToRemoteBranch(Repository i_RemoteRepsoitory){
        return new RemoteBranch(this, i_RemoteRepsoitory);
    }

    @Override
    public String toString() {
//        String nameBranchPresentation = "Name branch:  " + m_Name + System.lineSeparator();
//        String Sha1OfCommitBranchPointTo = "SHA_1 of commit Branch point to is: " + m_Commit.GetSha1() + System.lineSeparator();
//        String messageOfTheCommit = "Message of the commit: " + m_Commit.GetMessage() + System.lineSeparator();
//        String branchDetails = nameBranchPresentation + Sha1OfCommitBranchPointTo + messageOfTheCommit;
//
//        return branchDetails;
        return m_Name;
    }

    public String ShowCommit() {
        return m_Commit.toString();
    }

    public void DeleteBranchTextFile(){
        Path path = m_Path;
        m_Path = null;
        DekelNoy3rd.Service.Methods.DeleteTextFile(path);
    }

    public void Deploy(){
        if (m_Commit != null) {
            m_Commit.DeployRec(m_Commit.GetPathToMainFolder(), m_Commit.GetMainFolderSha_1());
        }
    }

    public RepositoryFile GetCommitMainFolder() { // Dekel
        return m_Commit.GetMainFolder();
    }

//    public RemoteBranch ToRemoteBranch(String i_RemoteRepsoitoryName){
//        String remoteBranchName = i_RemoteRepsoitoryName + "/" + this.m_Name;
//        return new RemoteBranch(remoteBranchName, this.m_Commit, this.m_Path);
//    }


    public void CreateBranchTextFile(Path i_pathToDirectory) {
        String content = "";
        if (m_Commit != null) {
            content = m_Commit.GetSha1();
        }
        CreateTextFile(i_pathToDirectory + "/" + m_Name, content);
    }
}