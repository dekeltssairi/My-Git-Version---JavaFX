package logic;

import java.nio.file.Path;
import java.util.*;

public class Delta {

    private Path m_RepositoryPath;
    private List<RepositoryFile> m_Added;
    private List<RepositoryFile> m_Deleted;
    private List<RepositoryFile> m_Changed;

    public Delta(Path i_RepositoryPath) {
        m_RepositoryPath = i_RepositoryPath;
        m_Added = new ArrayList<>();
        m_Changed = new ArrayList<>();
        m_Deleted = new ArrayList<>();
    }

    public List<RepositoryFile> GetAdded() {
        return m_Added;
    }

    public List<RepositoryFile> GetDeleted() {
        return m_Deleted;
    }

    public List<RepositoryFile> GetChanged() {
        return m_Changed;
    }

    public void Clean() {
        m_Changed.clear();
        m_Added.clear();
        m_Deleted.clear();
    }
}