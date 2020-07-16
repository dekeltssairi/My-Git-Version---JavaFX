package logic;

import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class Folder extends RepositoryFile {

    private List <RepositoryFile> m_RepositoryFiles;

    public Folder(String i_name, String i_Sha_1, String i_Committer, Date i_Date, Path i_Path, List<RepositoryFile> i_RepositoryFiles) {
        super(i_name, i_Sha_1, i_Committer, i_Date, i_Path);
        this.m_RepositoryFiles = i_RepositoryFiles;
    }

    public Folder(Folder i_Folder) { // Dekel
        super((RepositoryFile) i_Folder);
        m_RepositoryFiles = i_Folder.GetRepositoryFiles();
    }

    @Override
    public String toString() {
        String describtionOfSubFiles = "";
        for(int i = 0; i < m_RepositoryFiles.size(); i++) {
            String subFileName;
            subFileName = m_RepositoryFiles.get(i).GetName();
            String subFileSha_1 = m_RepositoryFiles.get(i).GetSha_1();
            String subFileType = m_RepositoryFiles.get(i).getClass().getSimpleName();
            String subFileCommitter = m_RepositoryFiles.get(i).GetCommitter();
            String subFileDate = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").format(m_RepositoryFiles.get(i).GetDate());
            describtionOfSubFiles += String.format("%s,%s,%s,%s, %s",subFileName,subFileSha_1,subFileType,subFileCommitter,subFileDate);
            if (i < m_RepositoryFiles.size() - 1){
                describtionOfSubFiles += System.lineSeparator();
            }
        }
        return describtionOfSubFiles;
    }

    public List<RepositoryFile> GetRepositoryFiles() {
        return m_RepositoryFiles;
    }


    public void SetRepositoryFiles(List<RepositoryFile> i_RepositoryFiles) { // Dekel
        m_RepositoryFiles=i_RepositoryFiles;
    }


    public List<RepositoryFile> CopyToList() {
        return CopyToListRec(this);
    }

    public Map CopyToMap(){
        List<RepositoryFile> list = CopyToList();
        Map<String,RepositoryFile> map = new HashMap<>();
        for (RepositoryFile repositoryFile: list){
            map.put(repositoryFile.GetSha_1(), repositoryFile);
        }
        return map;
    }

    public Map CopyToMapByPath(){
        return copyToMapByPathRec(this);
    }

    private Map copyToMapByPathRec(RepositoryFile i_RepositoryFile) {
        Map <Path, RepositoryFile> map = new HashMap<Path, RepositoryFile>(){
            @Override
            public boolean containsValue(Object repositoryFile){
                return (repositoryFile instanceof RepositoryFile &&
                        (this.containsKey(((RepositoryFile) repositoryFile).GetPath())));
            }
        };
        map.put(i_RepositoryFile.GetPath(),i_RepositoryFile);
        if (i_RepositoryFile instanceof Folder) {
            Folder folder = (Folder) i_RepositoryFile;
            for (RepositoryFile repositoryFile : folder.GetRepositoryFiles()) {
                map.putAll(copyToMapByPathRec(repositoryFile));
            }
        }
        return map;
    }



    private List <RepositoryFile> CopyToListRec(RepositoryFile i_RepositoryFile) {
        List <RepositoryFile> list = new ArrayList<>();
        list.add(i_RepositoryFile);
        if (i_RepositoryFile instanceof Folder) {
            Folder folder = (Folder) i_RepositoryFile;
            for (RepositoryFile repositoryFile : folder.GetRepositoryFiles()) {
                list.addAll(CopyToListRec(repositoryFile));
            }
        }
        return list;
    }
}