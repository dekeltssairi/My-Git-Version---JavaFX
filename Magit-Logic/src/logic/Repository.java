package logic;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import static DekelNoy3rd.Service.Methods.*;

public class Repository {
    private String m_Name;
    private Path m_Path;
    boolean m_IsLocal;
    private WorkingCopy m_WC;
    private Magit m_Magit;
    private Remote m_Remote;
    private Delta m_Delta;

    public Repository(String i_Name, String i_PathStr){
        m_Name = i_Name;
        m_Path = Paths.get(i_PathStr).toAbsolutePath();
        m_IsLocal = isLocalRepository();
        m_Magit = new Magit(i_Name ,Paths.get(m_Path.toString() + "/.magit"), m_IsLocal);
        m_WC = new WorkingCopy(m_Path);
        m_Delta = new Delta(m_Path);
        if (m_IsLocal){
            Path path = Paths.get(m_Path.toString() + "/.magit/Remote");
            Path pathToRemote = Paths.get(DekelNoy3rd.Service.Methods.ReadContentOfTextFile(path.toString()));
            m_Remote = new Remote(pathToRemote, m_Path, this);
        }
    }

    private boolean isLocalRepository() {
//        Path path = Paths.get(m_Path.toString() + "/.magit/Branches");
//        File file = new File(path.toString());
//        boolean isLocal = false;
//        File[] subFile = file.listFiles();
//

        File file = new File(m_Path.toString() + "/.magit/Remote");
        return file.exists();
    }

    public Magit GetMagit() {return m_Magit;}

    public WorkingCopy GetWC() {return m_WC; }

    public String GetMainFolderSha_1OfActiveCommit() {
        return m_Magit.GetActiveCommit().GetMainFolderSha_1();
    }

    public void Commit(String i_CommitMessage, UserName i_UserName, Commit i_Parent) {
        //  public void GenerateSystemFiles(UserName i_UserName, Map<String,Path> i_StringZipFilesMap, Map<String,RepositoryFile> i_CurrentCommitMap)
        Map<String,RepositoryFile> sha1RepositroyFileMap = null;
        if (m_Magit.GetActiveCommit() !=null){
            sha1RepositroyFileMap = ((Folder)m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
        }
        m_WC.GenerateSystemFiles(i_UserName, m_Magit.GetObjects(),sha1RepositroyFileMap);
            createZipFilesFromCurrentWc(m_WC.GetMainFolder());
            Commit newCommit = createTheCommit(i_CommitMessage, i_UserName, m_WC.GetPath(), i_Parent);
            m_Magit.GetCommits().put(newCommit.GetSha1(), newCommit);
            UpdateActiveBranch(newCommit);
    }

    public boolean IsSomethingToCommit(UserName i_UserName) {
        Map<String, RepositoryFile> sha1RepositroyFileMap = null;
        if (m_Magit.GetActiveCommit() != null) {
            sha1RepositroyFileMap = ((Folder) m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
        }
        m_WC.GenerateSystemFiles(i_UserName, m_Magit.GetObjects(), sha1RepositroyFileMap);
        File mainFolder = new File(m_WC.GetPath().toString());
        boolean isThereIsActiveCommit = m_Magit.GetActiveBranch().GetCommit() != null;
        boolean isSomethingToCommit = false; // noy-validation
        if (mainFolder.listFiles().length > 1) { // noy-validation
            if (isThereIsActiveCommit) {
                String wcSha_1 = m_WC.GetMainFolder().GetSha_1();
                String activeCommitSha_1 = GetMainFolderSha_1OfActiveCommit();
                isSomethingToCommit = !wcSha_1.equals(activeCommitSha_1);
            } else {
                isSomethingToCommit = true;
            }
        }
        return isSomethingToCommit;
    }

    private void UpdateActiveBranch(Commit i_NewCommit) {
        m_Magit.GetHead().GetActiveBranch().SetCommit(i_NewCommit);
        createTheTextFileCommitInComittedArea(i_NewCommit);
        m_Magit.GetActiveBranch().createBranchTextFile();
    }

    private void createZipFilesFromCurrentWc(RepositoryFile i_RepositoryFile) {
        createZipFilesFromCurrentWcRec(i_RepositoryFile);
    }

    private Commit createTheCommit(String i_CommitMessage, UserName i_UserName, Path i_PathToWc, Commit i_Parent) {
        Commit newCommit = createTheObjectCommitInMagitObjects(i_CommitMessage,i_UserName, i_PathToWc, i_Parent);
        return newCommit;

    }

    private void createTheTextFileCommitInComittedArea(Commit i_Commit) {
        String pathToNewCommitInCommitted = m_Path.toString() +"/.magit/" +i_Commit.GetSha1();
        String contentFile = i_Commit.toString();
        CreateTextFile(pathToNewCommitInCommitted, contentFile);
    }

    private Commit createTheObjectCommitInMagitObjects(String i_CommitMessage, UserName i_UserName, Path i_PathToWc, Commit i_Parent) {

        String sha_1MainFolder = m_WC.GetMainFolder().GetSha_1();
        List<Commit> parentsCommit = null;

        if (m_Magit.GetActiveBranch().GetCommit() != null){
            parentsCommit = new ArrayList<>();
            parentsCommit.add(m_Magit.GetActiveBranch().GetCommit());
            if(i_Parent != null){
                parentsCommit.add(i_Parent);
            }
        }
        Date commitDate = new Date();
        Commit newCommit = new Commit(sha_1MainFolder, parentsCommit, i_CommitMessage, commitDate, i_UserName, i_PathToWc);
        return newCommit;
    }

    private void createZipFilesFromCurrentWcRec(RepositoryFile i_RepositoryFile) {
        if (i_RepositoryFile != null){
            String sha_1_RepositoryFile = i_RepositoryFile.GetSha_1();
            if (!m_Magit.GetObjects().containsKey(sha_1_RepositoryFile)){
                if (i_RepositoryFile instanceof Folder){
                    Folder folder = ((Folder) i_RepositoryFile);
                    int numOfFilesInFolder = folder.GetRepositoryFiles().size();
                    List <RepositoryFile> innerRepositoryFiles = folder.GetRepositoryFiles();

                    for (int i = 0; i < numOfFilesInFolder; i++){
                        createZipFilesFromCurrentWcRec(innerRepositoryFiles.get(i));
                    }
                }
                CreateZipFromRepositoryFileInObjectsFolder(i_RepositoryFile);
                AddZipToMap(i_RepositoryFile);
            }
        }
    }

    public void AddZipToMap(RepositoryFile i_RepositoryFile) {
        Path zipPath = Paths.get(m_Magit.GetPath() + "/Objects/" + i_RepositoryFile.GetSha_1() + ".zip");
        m_Magit.GetObjects().put(i_RepositoryFile.GetSha_1(), zipPath);
    }

    private void CreateZipFromRepositoryFileInObjectsFolder(RepositoryFile i_RepositoryFile) {
        CreateTemporaryTxtFileInObjectsFromRepositoryFile(i_RepositoryFile);
        CreateZipFromTemporaryTextFile(i_RepositoryFile);
        new File(m_Magit.GetPath().toString() + "/Objects/" + i_RepositoryFile.GetName()).delete();

    }

    private void CreateZipFromTemporaryTextFile(RepositoryFile i_RepositoryFile) {
        String zipName = i_RepositoryFile.GetSha_1() + ".zip";
        String destination =  m_Path.toString()+"/.magit/Objects/" + zipName;
        String source = m_Path.toString()+"/.magit/Objects/" + i_RepositoryFile.GetName();
        Zip(Paths.get(source), Paths.get(destination));
    }

    private void CreateTemporaryTxtFileInObjectsFromRepositoryFile(RepositoryFile i_RepositoryFile) {
        String destenationPath = m_Magit.GetPath().toString() + "/Objects/" + i_RepositoryFile.GetName();
        String contentFile = i_RepositoryFile.toString();
        CreateTextFile(destenationPath, contentFile);
    }

    public String GetBranchDetails(){
        return m_Magit.BranchesDetails();
    }

    public boolean IsExistBranch(String i_BranchName){
        return m_Magit.IsExistBranch(i_BranchName);
    }

    public LocalBranch CreateNewBranch(String i_BranchName){
        return m_Magit.CreateNewBranch(i_BranchName);
    }


    public LocalBranch CreateNewBranchForSpecificCommit(String i_BranchName, Commit i_Commit) {
        return m_Magit.CreateNewBranchForSpecificCommit(i_BranchName, i_Commit);
    }

    public String ShowCurrentCommit() {
        return m_Magit.ShowCurrrentCommit();
    }

    public boolean IsHeadBranch(String i_BranchName) {
        return m_Magit.IsHeadBranch(i_BranchName);
    }

    public void DeleteBranch(String i_BranchName) {
        m_Magit.DeleteBranch(i_BranchName);
    }

    public void SwitchHeadBranchAndDeployIt(String i_BranchName) {
        m_Magit.SwitchHeadBranch(i_BranchName);
        DeployHeadBranch();
    }

    public void DeployHeadBranch(){
        if (m_Magit.GetActiveCommit() != null) {
            cleanWC();
            m_Magit.DeployHeadBranch();
            m_WC.GetMainFolder().SetSha_1(m_Magit.GetActiveBranch().GetCommit().GetMainFolderSha_1());
        }
    }

    private void cleanWC() {
        m_WC.Clean();
    }

    public List<RepositoryFile> DeployCommitToList() {
        String Sha_1CommitMainFolder = m_Magit.GetActiveCommit().GetMainFolderSha_1();
        List<RepositoryFile> repositoryFiles = new ArrayList<>();
        createRepositoryFileListOfCommitZipRec(Sha_1CommitMainFolder, m_Path, repositoryFiles);
        return repositoryFiles;
    }

    private RepositoryFile createRepositoryFileListOfCommitZipRec(String i_Sha_1Folder, Path i_PathToFile, List<RepositoryFile> i_RepositoryFiles) {
        RepositoryFile file = null;
        String pathToZip = m_Path.toString() + "/.magit/Objects/" + i_Sha_1Folder + ".zip";
        String zipContent = ReadFromZip(pathToZip);
        List<RepositoryFile> subFilesInFolder = new ArrayList<>();
        String[] linesOfZip = zipContent.split("\n");
        for(String str: linesOfZip){
            String[] output = str.split(",");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
            Date date = null;
            try {
                date = dateFormat.parse(output[4]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Path pathToFile = Paths.get(i_PathToFile.toString() + "/" + output[0]);

            if(output[2].equals("Folder")) {
                RepositoryFile subFile = createRepositoryFileListOfCommitZipRec(output[1], pathToFile, i_RepositoryFiles);
                subFilesInFolder.add(subFile);
                file = new Folder(output[0], output[1], output[3], date, pathToFile, subFilesInFolder);
                i_RepositoryFiles.add(file);
            }
            else{
                String content = ReadFromZip(m_Path.toString() + "/.magit/Objects/" + output[1] + ".zip");
                file = new Blob(output[0], output[1], output[3], date, pathToFile, content);
                i_RepositoryFiles.add(file);
            }
        }
        return file;
    }

    public Path GetPath() {
        return m_Path;
    }

    public boolean HasBranchesExceptHead() {
        return m_Magit.HasBranchesExceptHead();
    }

    public boolean HasFilesInWC() {
        return m_WC.HasFilesInWC();
    }

    public RepositoryFile GetCurrentCommitMainFolder(){ // Dekel
        return m_Magit.GetCurrentCommitMainFolder();
    }

    public String GetName() {
        return m_Name;
    }

    public Delta GetDelta(UserName i_userName) {
        Map<String,RepositoryFile> sha1RepositroyFileMap = null;
        if (m_Magit.GetActiveCommit() !=null){
            sha1RepositroyFileMap = ((Folder)m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
        }
        m_WC.GenerateSystemFiles(i_userName, m_Magit.GetObjects(), sha1RepositroyFileMap);
        m_Delta.Clean();
        RepositoryFile currentCommitMainFolder = GetCurrentCommitMainFolder();
        calculateDelta(currentCommitMainFolder, m_WC.GetMainFolder());
        return m_Delta;
    }

    private void calculateDelta(RepositoryFile i_CurrentCommitMainFolder, RepositoryFile i_WCMainFolder) {
        List <RepositoryFile>currentCommitRepositoryFilesList = ((Folder)i_CurrentCommitMainFolder).CopyToList();
        List <RepositoryFile> wcMainFolderList =(((Folder)i_WCMainFolder).CopyToList());

        for (RepositoryFile commitRepositoryFile: currentCommitRepositoryFilesList){
            boolean existAndSameInWC = false;
            boolean existAndNotSameInWC = false;

            for (RepositoryFile wcRepositoryFile: wcMainFolderList){
                if(commitRepositoryFile.GetPath().equals(wcRepositoryFile.GetPath())){
                    if (commitRepositoryFile.GetSha_1().equals(wcRepositoryFile.GetSha_1())){
                        existAndSameInWC = true;
                    }
                    else{
                        existAndNotSameInWC = true;
                    }
                }
            }
            if (existAndNotSameInWC){
                m_Delta.GetChanged().add(commitRepositoryFile);
            }
            else if (!existAndSameInWC){
                m_Delta.GetDeleted().add(commitRepositoryFile);
            }
        }

        for (RepositoryFile wcRepositoryFile: wcMainFolderList){
            boolean existInCommit = false;
            for (RepositoryFile commitRepositoryFile: currentCommitRepositoryFilesList){
                if(commitRepositoryFile.GetPath().equals(wcRepositoryFile.GetPath())){
                    existInCommit = true;
                }
            }
            if (!existInCommit){
                m_Delta.GetAdded().add(wcRepositoryFile);
            }
        }
    }

    public Delta GetDelta(Commit i_commit, Commit i_parentCommit) {
        m_Delta.Clean();
        RepositoryFile CommitMainFolder = i_commit.GetMainFolder();
        RepositoryFile parentCommitCommitMainFolder = i_parentCommit.GetMainFolder();
        calculateDelta(CommitMainFolder, parentCommitCommitMainFolder);
        return m_Delta;
    }


    public boolean GetIsLocal() {
        return m_IsLocal;
    }

    public void Fetch() {
        m_Remote.Fetch();
    }

    public void Pull() {
        m_Remote.Pull();
    }

    public Remote GetRemote() {
        return m_Remote;
    }

    public void Push() {
        m_Remote.Push();
    }

    public boolean IsSomethingToPush() {
        return m_Remote.IsSomeThingToPush();
    }

    public boolean IsFastMerge() {
        return m_Magit.IsFastMerge();
    }

    public void FastMerge() {
        m_Magit.FastMerge();
    }

    public List<Conflict> GetConflicts() {
        m_WC.Clean();
        return m_Magit.GetConflicts();
    }

    public void SetCommitInMerger() {
        m_Magit.SetCommitInMerger();
    }

    public void InitializeMerger(Branch i_branchToMerge) {
        m_Magit.InitializeMerger(i_branchToMerge);
    }

    public Commit GetActiveCommit() {
        return m_Magit.GetActiveCommit();
    }

    public void PushBranchToRR(LocalBranch i_branch) {
        m_Remote.PushBranchToRR(i_branch);
    }
}