package logic;

import java.nio.file.Path;

public class RemoteBranch extends Branch {

    private Repository m_RemoteRepository;

    public RemoteBranch(LocalBranch i_localBranch, Repository i_remoteRepsoitory) {
        super(i_remoteRepsoitory.GetName() + "/" + i_localBranch.GetName(), i_localBranch.GetCommit(), i_remoteRepsoitory.GetPath());
        m_RemoteRepository = i_remoteRepsoitory;
    }

    public String GetOriginName(){
        StringBuilder sb = new StringBuilder(m_Name);
        int startOfSubString = m_RemoteRepository.GetName().length() + 1;
        int endOfSubString = m_Name.length();
        return sb.substring(startOfSubString, endOfSubString);
    }

    @Override
    public String toString() {
        return m_Name;
    }

}


