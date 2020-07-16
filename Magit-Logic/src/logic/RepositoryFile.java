package logic;

import java.nio.file.Path;
import java.util.Date;

public class RepositoryFile {
    private String m_Name;
    private String m_Sha_1;
    private String m_Committer;
    private Date m_Date;
    private Path m_Path;

    public RepositoryFile(String i_Name, String i_Sha_1, String i_Committer, Date i_Date, Path i_Path) {
        m_Name = i_Name;
        m_Sha_1 = i_Sha_1;
        m_Committer = i_Committer;
        m_Date = i_Date;
        m_Path = i_Path;
    }

    public RepositoryFile(RepositoryFile i_RepositoryFile) {
        m_Name = i_RepositoryFile.GetName();
        m_Sha_1 = i_RepositoryFile.GetSha_1();
        m_Committer = i_RepositoryFile.GetCommitter();
        m_Date = i_RepositoryFile.GetDate();
        m_Path = i_RepositoryFile.GetPath();
    }

//    public boolean equals(Object o) {
//        return (o instanceof RepositoryFile &&
//                (((RepositoryFile) o).GetSha_1().equals(m_Sha_1)) &&
//                (((RepositoryFile) o).GetPath().equals(m_Path)));
//    }

    public int hashCode() {
        return m_Sha_1.hashCode() & m_Path.hashCode();
    }


    public String GetSha_1() {
        return m_Sha_1;
    }

    public String GetName() {
        return m_Name;
    }

    public Path GetPath() {
        return m_Path;
    }

    public String GetCommitter() {
        return m_Committer;
    }

    public Date GetDate() {
        return m_Date;
    }

    public void SetSha_1(String i_Sha1) {
        m_Sha_1 = i_Sha1;
    }

    @Override
    public boolean equals(Object object){
        boolean equal = false;
        if (object != null) {
            if (object instanceof RepositoryFile) {
                RepositoryFile repositoryFile = (RepositoryFile) object;
                if(m_Path != null){
                    boolean isPathsEquals = m_Path.toString().equals(repositoryFile.GetPath().toString());
                    boolean isSha1Equals = m_Sha_1.equals(repositoryFile.GetSha_1());
                    equal = isPathsEquals && isSha1Equals;
                }
            }
        }
        return equal;
    }

    public void SetPath(Path i_path) {
        m_Path = i_path;
    }

    public void SetCommitter(String i_Committer) {
        m_Committer = i_Committer;
    }
}
