package logic;

import java.nio.file.Path;

public abstract class Branch {
    protected String m_Name;
    protected Commit m_Commit;
    protected Path m_Path;

    public Path GetPath(){
        return m_Path;
    };

    public Branch(String i_Name, Commit i_Commit, Path i_Path) {
        this.m_Name = i_Name;
        this.m_Commit = i_Commit;
        this.m_Path = i_Path;
    }

    public String GetName(){
        return m_Name;
    };

    public Commit GetCommit(){
        return m_Commit;
    };
}