package logic;

public class PairRepositoryFile {
    private RepositoryFile m_Source;
    private RepositoryFile m_Destination;

    public RepositoryFile GetSource() {
        return m_Source;
    }

    public PairRepositoryFile(RepositoryFile i_Source, RepositoryFile i_Destination) {
        m_Source = i_Source;
        m_Destination = i_Destination;
    }

    @Override
    public String toString() {
        String relocation = "relocated from: " + m_Source.GetPath() + " to " + m_Destination.GetPath();
        return relocation;
    }

    public RepositoryFile GetDestination() {
        return m_Destination;
    }
}