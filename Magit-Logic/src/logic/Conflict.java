package logic;

public class Conflict {
    RepositoryFile m_SonA;
    RepositoryFile m_SonB;
    RepositoryFile m_Ancestor;

    String m_ConflictDescription;

    public Conflict(String i_ConflictDescription) { m_ConflictDescription = i_ConflictDescription; }

    public void SetSonA(RepositoryFile i_SonA) { m_SonA = i_SonA; }

    public void SetSonB(RepositoryFile i_SonB) { m_SonB = i_SonB; }

    public void SetAncestor(RepositoryFile i_Ancestor) { m_Ancestor = i_Ancestor; }

    public RepositoryFile GetSonA() { return m_SonA; }

    public RepositoryFile GetSonB() { return m_SonB; }

    public RepositoryFile GetAncestor() { return m_Ancestor; }

    @Override
    public String toString(){
        return m_ConflictDescription;
    }

    @Override
    public boolean equals(Object o){
        boolean equal = false;
        if (o instanceof Conflict){
            Conflict conflict = (Conflict)o;
            boolean hasSonA = this.m_SonA != null;
            boolean hasSonB = this.m_SonB != null;
            boolean hasAncestor = this.m_Ancestor != null;

            boolean sameConflictDescription = this.m_ConflictDescription.equals(conflict.GetConflictDescription());
            boolean sameSonA = false;
            boolean sameSonB = false;
            boolean sameAncestor = false;

            if (hasSonA){
                sameSonA = m_SonA.equals(conflict.GetSonA());
            }
            else{
                sameSonA = conflict.GetSonA() == null;
            }

            if (hasSonB){
                sameSonB= m_SonB.equals(conflict.GetSonB());
            }
            else{
                sameSonB = conflict.GetSonB() == null;
            }

            if (hasAncestor){
                sameAncestor = m_Ancestor.equals(conflict.GetAncestor());
            }
            else{
                sameAncestor = conflict.GetAncestor() == null;
            }

            equal = sameSonA && sameSonB && sameAncestor && sameConflictDescription;
        }
        return  equal;
    }

    private String GetConflictDescription() {
        return this.m_ConflictDescription;
    }

}
