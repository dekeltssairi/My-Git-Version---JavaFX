package logic;

import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static DekelNoy3rd.Service.Methods.*;

public class Commit {
    private String m_MainFolderSha_1;
    private List <Commit>  m_ParentsCommit;
    private String m_Message;
    private Date m_Date;
    private UserName m_Committer;
    private String m_Sha1;
    private Path m_PathToMainFolder;

    public Commit(String i_MainFolderSha_1, List <Commit> i_ParentsCommit, String i_Message, Date i_Date, UserName i_Committer, Path i_PathToMainFolder) {
        m_MainFolderSha_1 = i_MainFolderSha_1;
        m_ParentsCommit = i_ParentsCommit;
        m_Message = i_Message;
        m_Date = i_Date;
        m_Committer = i_Committer;
        m_Sha1 = org.apache.commons.codec.digest.DigestUtils.sha1Hex(toString());
        m_PathToMainFolder = i_PathToMainFolder;
    }

    public String GetSha1() {
        return m_Sha1;
    }

    public String GetMainFolderSha_1() {
        return m_MainFolderSha_1;
    }

    public Path GetPathToMainFolder() {
        return m_PathToMainFolder;
    }

    public List<Commit> GetParentCommit() {
        return m_ParentsCommit;
    }

    public String GetMessage() {
        return m_Message;
    }

    public String GetDate() {
        return m_Date.toString();
    }

    public Date GetDateObject() {return m_Date;}

    public String GetCommitter() {
        return m_Committer.GetName();
    }


    @Override
    public String toString() {
        String parentsCommitSha1;
        if (m_ParentsCommit == null) {
            parentsCommitSha1 = "There is no earlier version" + System.lineSeparator() + "There is no earlier version";
        }else {
            parentsCommitSha1 = m_ParentsCommit.get(0).GetSha1() + System.lineSeparator();
            if (m_ParentsCommit.size() == 2) {
                parentsCommitSha1 += m_ParentsCommit.get(1).GetSha1();
            }
            else{
                parentsCommitSha1 += "There is no earlier version";
            }
        }
        String dateStr = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").format(m_Date);
        String commitString = String.format("%s%s%s%s%s%s%s%s%s", m_MainFolderSha_1, System.lineSeparator(),
                parentsCommitSha1, System.lineSeparator(),
                m_Message, System.lineSeparator(),
                dateStr, System.lineSeparator(),
                m_Committer.toString());
        return commitString;
    }

    public void DeployRec(Path i_pathToFile, String i_Sha1) {
        String pathToObjects = m_PathToMainFolder.toString() + "/.magit/Objects";
        String pathToZipFile = pathToObjects + "/" + i_Sha1 + ".zip";
        String zipContent = ReadFromZip(pathToZipFile);
        zipContent = fixString(zipContent);
        String[] linesOfZip = zipContent.split(System.lineSeparator());

        for(String str: linesOfZip) {
            String[] output = str.split(",");
            if (output[2].equals("Blob")) {
                //String pathToCreateFile = i_pathToFile.toString() + "\\" + output[0] + ".txt";
                String pathToZipBlobFile = pathToObjects + "/" + output[1] + ".zip";
                Unzip(pathToZipBlobFile, i_pathToFile.toString());
            } else {
                Path pathToFile = Paths.get(i_pathToFile + "/" + output[0]);
                new File(pathToFile.toString()).mkdir();
                DeployRec(pathToFile, output[1]);
            }
        }
    }
    public RepositoryFile GetMainFolder() { // Dekel
        Folder mainFolder = new Folder(m_PathToMainFolder.getFileName().toString(),m_MainFolderSha_1,m_Committer.GetName(),m_Date,m_PathToMainFolder,null);
        GetMainFolderRec(m_PathToMainFolder,m_MainFolderSha_1, mainFolder);
        return  mainFolder;

    }

    public void GetMainFolderRec(Path i_pathToFile, String i_Sha1, Folder i_MainFolder) { // Dekel
        List<RepositoryFile> subFiles = new ArrayList<>();
        String pathToObjects = m_PathToMainFolder.toString() + "/.magit/Objects";
        String pathToZipFile = pathToObjects + "/" + i_Sha1 + ".zip";
        String zipContent = ReadFromZip(pathToZipFile);
        String[] linesOfZip = zipContent.split(System.lineSeparator());

        for(String str: linesOfZip) {
            String[] output = str.split(",");
            String repositoryFileName = output[0];
            String repositoryFileSHA_1 = output[1];
            String repositoryFileCommitter = output[3];
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
            Date repositoryFileDate = null;
            try {
                repositoryFileDate = dateFormat.parse(output[4]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Path repositoryFilePath = Paths.get(i_MainFolder.GetPath().toString() +"/" + repositoryFileName);
            if (output[2].equals("Blob")) {
                //String pathToCreateFile = i_pathToFile.toString() + "\\" + output[0] + ".txt";
                String pathToZipBlobFile = pathToObjects + "/" + output[1] + ".zip";
                String content = ReadFromZip(pathToZipBlobFile);
                subFiles.add(new Blob(repositoryFileName,repositoryFileSHA_1, repositoryFileCommitter,repositoryFileDate, repositoryFilePath, content));
            } else {
                Folder folder = new Folder(repositoryFileName,repositoryFileSHA_1, repositoryFileCommitter,repositoryFileDate, repositoryFilePath, null);
                GetMainFolderRec(repositoryFilePath, repositoryFileSHA_1, folder);
                subFiles.add(folder);
            }
            i_MainFolder.SetRepositoryFiles(subFiles);
        }
    }

    public void CreateTextFile(Path i_PathToFolder) {
        DekelNoy3rd.Service.Methods.CreateTextFile(i_PathToFolder + "/" + m_Sha1, toString());
    }

    @Override
    public boolean equals(Object obj) {
        Commit commit = (Commit)obj;
        return this.GetSha1().equals(commit.GetSha1());
    }

    public void AddParent(Commit i_Parent) {
        if(i_Parent != null){
            m_ParentsCommit.add(i_Parent);
        }
    }
}
