package logic;

import java.nio.file.Path;

public class CommitFile {

    private Path m_Path;
    private String m_Sha_1;

    public CommitFile(Path i_Path, String i_Sha_1) {
        m_Path = i_Path;
        m_Sha_1 = i_Sha_1;
    }

    public Path GetPath() {
        return m_Path;
    }

    public String GetSha_1() {
        return m_Sha_1;
    }
}
