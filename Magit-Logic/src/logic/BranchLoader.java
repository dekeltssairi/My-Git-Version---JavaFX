package logic;
import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class BranchLoader {
    private Path m_PathToMagit;
    private CommitLoader m_CommitLoader;

    public BranchLoader(Path i_PathToMagit) {
        m_PathToMagit = i_PathToMagit;
        m_CommitLoader = new CommitLoader(m_PathToMagit);
    }

    public LocalBranch loadBranch(Path i_PathToBranch, Map<String,Commit> i_sha_1CommitMap) {
        File branchFile = new File(i_PathToBranch.toString());
        String branchName =  branchFile.getName();
        Commit commit = m_CommitLoader.getCommit(i_PathToBranch, i_sha_1CommitMap);
        return new LocalBranch(branchName, commit, i_PathToBranch);
    }
}