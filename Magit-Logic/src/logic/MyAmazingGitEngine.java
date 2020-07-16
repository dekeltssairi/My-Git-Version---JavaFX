package logic;

import Generate.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import static DekelNoy3rd.Service.Methods.*;
import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

public class MyAmazingGitEngine {

    private Repository m_ActiveRepository;
    private UserName m_UserName;

    public MyAmazingGitEngine(){
        m_UserName = new UserName();
        m_ActiveRepository = null;
    }

    public void SetUsername(String i_NewUsernameStr){
        m_UserName = new UserName(i_NewUsernameStr);
    }

    public UserName GetUserName() {return m_UserName;}

    public Repository GetActiveRepository(){
        return m_ActiveRepository;
    }

    public void createNewRepositoryAndActivateIt(String i_RepositoryName, String i_RepositoryPathStr) {
        m_ActiveRepository = new Repository(i_RepositoryName,i_RepositoryPathStr);
    }

    public boolean IsExistBranch(String i_NewBranchName) {
        boolean existBranch = false;

        for(Branch branchItr : m_ActiveRepository.GetMagit().GetBranches()){
            if(branchItr.GetName().equals(i_NewBranchName)) {
                existBranch = true;
            }
        }
        return existBranch;
    }

    public void CreateRepositoryFromPath(String i_PathStr, String i_RepositoryName) {
        createMagitFolder(Paths.get(i_PathStr));
        createNewRepositoryAndActivateIt(i_RepositoryName, i_PathStr);
    }

    private void createMagitFolder(Path i_Path) {
        i_Path = Paths.get(i_Path.toString() + "/.magit/Branches");
        try {
            Files.createDirectories(i_Path);
            i_Path = i_Path.getParent();
            i_Path = Paths.get(i_Path.toString() + "/Objects").normalize();
            Files.createDirectories(i_Path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Commit(String message) {
         m_ActiveRepository.Commit(message, m_UserName, null);
    }

    public void MergeCommit(String message, Commit i_Parent) {
        m_ActiveRepository.Commit(message, m_UserName, i_Parent);
    }

    public boolean IsSomethingToCommit() {
        return m_ActiveRepository.IsSomethingToCommit(m_UserName);
    }

    public String ShowCurrentCommit() {
        return m_ActiveRepository.ShowCurrentCommit();
    }

    public String GetBranchDetails() {
        return m_ActiveRepository.GetBranchDetails();
    }

    public boolean IsExistBranchInActiveRepository(String i_BranchName){
        return m_ActiveRepository.IsExistBranch(i_BranchName);
    }

    public LocalBranch CreateNewBranchInActiveRepository(String i_BranchName){
        return m_ActiveRepository.CreateNewBranch(i_BranchName);
    }

    public LocalBranch CreateNewBranchForSpecificCommit(String i_BranchName, Commit i_Commit) {
        return m_ActiveRepository.CreateNewBranchForSpecificCommit(i_BranchName, i_Commit);
    }


    public boolean IsHeadBranch(String i_BranchName) {
        return m_ActiveRepository.IsHeadBranch(i_BranchName);
    }

    public void DeleteBranch(String i_BranchName) {
        m_ActiveRepository.DeleteBranch(i_BranchName);
    }

//    public String ShowCurrentBranchHistory() {
//        return m_ActiveRepository.GetBranchCommittsHistory();
//    }

    public void SwitchHeadBranchAndDeployIt(String i_BranchName) {
        m_ActiveRepository.SwitchHeadBranchAndDeployIt(i_BranchName);
    }

    public boolean HasActiveRepository() {
        return m_ActiveRepository != null;
    }

    public boolean HasActiveCommit() {
        return m_ActiveRepository != null && m_ActiveRepository.GetMagit().GetActiveCommit() != null;
    }

//    public String ShowWorkingCopyStatus() {
//        return m_ActiveRepository.ShowWorkingCopyStatus(m_UserName);
//    }

    public Path GetActiveRepositoryPath(){
        return m_ActiveRepository.GetPath();
    }

    public boolean HasBranchesExceptHead() {
        return m_ActiveRepository.HasBranchesExceptHead();
    }

    public void LoadExistRepository(String i_Path) {
        String pathToNameFile = i_Path + "/.magit/Name";
        String name = ReadContentOfTextFile(pathToNameFile);
            createNewRepositoryAndActivateIt(name, i_Path);
    }

    public void ConvertMagitRepositoryToOurRepository(MagitRepository i_MagitRepository) {
        createMagitFolder(Paths.get(i_MagitRepository.getLocation()));
        createHeadFile(i_MagitRepository);
        List <RepositoryFile> repositoryFilesList = createBranchesFiles(i_MagitRepository);
        if(i_MagitRepository.getMagitRemoteReference() != null && i_MagitRepository.getMagitRemoteReference().getName() != null){ // noy-validation
            CreateTextFile(i_MagitRepository.getLocation() + "/.magit/Remote", i_MagitRepository.getMagitRemoteReference().getLocation());
        }

        createNewRepositoryAndActivateIt(i_MagitRepository.getName(), i_MagitRepository.getLocation());
        addZipToMap(repositoryFilesList);
        List <RepositoryFile> repositoryFilesOfCurrentCommit = m_ActiveRepository.DeployCommitToList();
        m_ActiveRepository.GetWC().GenerateSystemFiles(repositoryFilesOfCurrentCommit, Paths.get(i_MagitRepository.getLocation()));
    }

    private void addZipToMap(List<RepositoryFile> i_RepositoryFilesList) {
        for(RepositoryFile repositoryFile : i_RepositoryFilesList){
            m_ActiveRepository.AddZipToMap(repositoryFile);
        }
    }

    private List<RepositoryFile> createBranchesFiles(MagitRepository i_MagitRepository) {
        List <RepositoryFile> repositoryFilesList = new ArrayList<>();
        List<MagitSingleBranch> magitSingleBranches = i_MagitRepository.getMagitBranches().getMagitSingleBranch();
        List<MagitSingleCommit> magitSingleCommits = i_MagitRepository.getMagitCommits().getMagitSingleCommit();
        Path pathToBranches = Paths.get(i_MagitRepository.getLocation() +"/.magit/Branches");

        if(i_MagitRepository.getMagitRemoteReference() != null && i_MagitRepository.getMagitRemoteReference().getName() != null){ // noy-validation
            String remoteRepositoryName = i_MagitRepository.getMagitRemoteReference().getName();
            File remoteBranchFolder = new File(pathToBranches.toString() +"/"+ remoteRepositoryName);
            remoteBranchFolder.mkdir();
        }

        if(!(magitSingleCommits.size() == 0)) {
//            String pathToMAPTxtFile = i_MagitRepository.getLocation() +"/.magit/MAP";
//            CreateTextFile(pathToMAPTxtFile, "");
            for (MagitSingleBranch magitSingleBranch : magitSingleBranches) {
                String commitId = magitSingleBranch.getPointedCommit().getId();
                MagitSingleCommit magitSingleCommit = findCommitByID(commitId, magitSingleCommits);
                String commitSha_1 = loadMagitSingleCommitRec(magitSingleCommit, i_MagitRepository, repositoryFilesList);

//                if(magitSingleBranch.isIsRemote()){
//                    Path pathToSpecificBranch = Paths.get(pathToBranches.toString() + "/" + i_MagitRepository.getMagitRemoteReference().getName() + "/" + magitSingleBranch.getName());
//                    CreateTextFile(pathToSpecificBranch.toString(), commitSha_1);
//                }else{
                    Path pathToSpecificBranch = Paths.get(pathToBranches.toString() + "/" + magitSingleBranch.getName());
                    CreateTextFile(pathToSpecificBranch.toString(), commitSha_1);
                //}
            }
        }
        return repositoryFilesList;
    }

    private String loadMagitSingleCommitRec(MagitSingleCommit i_MagitSingleCommit, MagitRepository i_MagitRepository, List<RepositoryFile> i_RepositoryFilesList) {
        String commitSha1 = null;
        try {
            List<MagitSingleCommit> magitSingleCommits = i_MagitRepository.getMagitCommits().getMagitSingleCommit();
            List<MagitSingleFolder> magitSingleFolders = i_MagitRepository.getMagitFolders().getMagitSingleFolder();

            String commitMessage = i_MagitSingleCommit.getMessage();
            String committer = i_MagitSingleCommit.getAuthor();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
            Date commitDate = dateFormat.parse(i_MagitSingleCommit.getDateOfCreation());
            String date = dateFormat.format(commitDate);

            String parentCommitSha_1;
            String parentCommitSha_2;
            String pathToCommitFile;

            String rootFolderOfCommitID = i_MagitSingleCommit.getRootFolder().getId();
            MagitSingleFolder magitSingleRootFolderOfCommit = findFolderByID(rootFolderOfCommitID, magitSingleFolders);
            Path pathToMagitRepositoryFolder = Paths.get(i_MagitRepository.getLocation());

            String mainFolderSha_1 = loadItemsRec(magitSingleRootFolderOfCommit, i_MagitRepository, pathToMagitRepositoryFolder, i_RepositoryFilesList);

            if(i_MagitSingleCommit.getPrecedingCommits() == null) {
                parentCommitSha_1 = "There is no earlier version";
                parentCommitSha_2 = "There is no earlier version";
            }
            else{
                if(i_MagitSingleCommit.getPrecedingCommits().getPrecedingCommit().size() == 0){
                    parentCommitSha_1 = "There is no earlier version";
                    parentCommitSha_2 = "There is no earlier version";
                }
                else {
                    if (i_MagitSingleCommit.getPrecedingCommits().getPrecedingCommit().size() == 1) {
                        String parentCommitID = i_MagitSingleCommit.getPrecedingCommits().getPrecedingCommit().get(0).getId();
                        MagitSingleCommit magitSingleCommitParent = findCommitByID(parentCommitID, magitSingleCommits);
                        parentCommitSha_1 = loadMagitSingleCommitRec(magitSingleCommitParent, i_MagitRepository, i_RepositoryFilesList);
                        parentCommitSha_2 = "There is no earlier version";
                    }
                    else{// two parents
                        String parent1CommitID = i_MagitSingleCommit.getPrecedingCommits().getPrecedingCommit().get(0).getId();
                        MagitSingleCommit magitSingleCommitParent1 = findCommitByID(parent1CommitID, magitSingleCommits);
                        String parent2CommitID = i_MagitSingleCommit.getPrecedingCommits().getPrecedingCommit().get(1).getId();
                        MagitSingleCommit magitSingleCommitParent2 = findCommitByID(parent2CommitID, magitSingleCommits);
                        parentCommitSha_1 = loadMagitSingleCommitRec(magitSingleCommitParent2, i_MagitRepository, i_RepositoryFilesList);
                        parentCommitSha_2 = loadMagitSingleCommitRec(magitSingleCommitParent2, i_MagitRepository, i_RepositoryFilesList);
                    }
                }
            }
            String CommitStr = chainingFiveStrings(parentCommitSha_1,parentCommitSha_2, commitMessage, date, committer, mainFolderSha_1);
            commitSha1 = sha1Hex(CommitStr);

            pathToCommitFile = i_MagitRepository.getLocation() + "/.magit/" + commitSha1;
            CreateTextFile(pathToCommitFile, CommitStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return commitSha1;
    }
    private String loadItemsRec(MagitSingleFolder i_MagitSingleFolder, MagitRepository i_MagitRepository, Path i_PathToCurrentFolder, List<RepositoryFile> i_RepositoryFilesList) {
        List<MagitSingleFolder> magitSingleFolders = i_MagitRepository.getMagitFolders().getMagitSingleFolder();
        List<MagitBlob> magitSingleBlobs = i_MagitRepository.getMagitBlobs().getMagitBlob();
        List<Item> itemsFolder = i_MagitSingleFolder.getItems().getItem();
        Path pathToObjects = Paths.get(i_MagitRepository.getLocation() + "/.magit/Objects");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        String itemSha_1 = "";

        List<Object> magitSingleFiles = sortItems(itemsFolder, magitSingleBlobs, magitSingleFolders);

        String folderDetails = "";
        String folderSha_1 = "";
        for (int i = 0; i < magitSingleFiles.size(); i++) {
            if (magitSingleFiles.get(i) instanceof MagitBlob) {
                MagitBlob magitBlob = (MagitBlob) magitSingleFiles.get(i);

                String magitBlobName = magitBlob.getName();
                String blobContent = fixString(magitBlob.getContent());
                String zipName = sha1Hex(blobContent);
                folderDetails += magitBlobName + "," + zipName +"," + "Blob," + magitBlob.getLastUpdater() + "," + magitBlob.getLastUpdateDate() + System.lineSeparator();
                Path fullFileName = Paths.get(pathToObjects + "/" + magitBlobName);
                CreateTextFile(fullFileName.toString(), blobContent);
                Path pathToBlobZip = Paths.get(pathToObjects + "/" + zipName + ".zip");
                Zip(fullFileName, pathToBlobZip);

                Path pathToBlob = Paths.get(i_PathToCurrentFolder + "/" + magitBlobName);
                String updater = magitBlob.getLastUpdater();
                try {
                    Date date = sdf.parse(magitBlob.getLastUpdateDate());
                    i_RepositoryFilesList.add(new RepositoryFile(magitBlobName, zipName, updater, date, pathToBlob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DeleteTextFile(fullFileName);
                itemSha_1 += zipName;
            } else {
                MagitSingleFolder magitSingleFolder = (MagitSingleFolder) magitSingleFiles.get(i);
                folderDetails += magitSingleFolder.getName();
                Path pathToFolder = Paths.get(i_PathToCurrentFolder.toString() + "/" + magitSingleFolder.getName());
                folderSha_1 = loadItemsRec(magitSingleFolder, i_MagitRepository, pathToFolder, i_RepositoryFilesList);
                folderDetails += "," + folderSha_1 + "," + "Folder," + magitSingleFolder.getLastUpdater() + "," + magitSingleFolder.getLastUpdateDate() + System.lineSeparator();
                itemSha_1 += folderSha_1;
            }
        }
        itemSha_1 = sha1Hex(itemSha_1);
        String folderName = i_MagitSingleFolder.getName();
        if(folderName == null){
            folderName = Paths.get(i_MagitRepository.getLocation()).getName(Paths.get(i_MagitRepository.getLocation()).getNameCount() -1).toString();
        }

        Path fullFileName = Paths.get(pathToObjects.toString() + "/" + folderName);
        CreateTextFile(fullFileName.toString(), folderDetails);
        Path pathToFolderZip = Paths.get(pathToObjects.toString() + "/" + itemSha_1 + ".zip");
        Zip(fullFileName, pathToFolderZip);

        String updater = i_MagitSingleFolder.getLastUpdater();
        try {
            Date date = sdf.parse(i_MagitSingleFolder.getLastUpdateDate());
            i_RepositoryFilesList.add(new RepositoryFile(i_MagitSingleFolder.getName(), itemSha_1, updater, date, i_PathToCurrentFolder));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DeleteTextFile(fullFileName);
        return itemSha_1;
    }

    private List<Object> sortItems(List<Item> i_ItemsFolder, List<MagitBlob> i_MagitSingleBlobs, List<MagitSingleFolder> i_MagitSingleFolders ) {
        List<Object> magitSingleFiles = new ArrayList<>();

        for (Item item : i_ItemsFolder) {
            if (item.getType().equals("blob")) {
                MagitBlob magitBlob = findBlobByID(item.getId(), i_MagitSingleBlobs);
                magitSingleFiles.add(magitBlob);
            } else {
                MagitSingleFolder magitSingleFolder = findFolderByID(item.getId(), i_MagitSingleFolders);
                magitSingleFiles.add(magitSingleFolder);
            }
        }
        magitSingleFiles.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Class clazzO1 = o1.getClass();
                Class clazzO2 = o2.getClass();
                int returnValue = 0;
                try {

                    Method o1GetNameMethod = clazzO1.getDeclaredMethod("getName");
                    Method o2GetNameMethod = clazzO2.getDeclaredMethod("getName");
                    String o1Name = (String) o1GetNameMethod.invoke(o1);
                    String o2Name = (String) o2GetNameMethod.invoke(o2);

                    returnValue = o1Name.compareTo(o2Name);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return returnValue;
            }
        });
        return magitSingleFiles;
    }

    private MagitBlob findBlobByID(String i_IDBlob, List<MagitBlob> i_MagitSingleBlobs) {
        MagitBlob magitBlob = null;
        for (MagitBlob magitBlobItr : i_MagitSingleBlobs){
            if (magitBlobItr.getId().equals(i_IDBlob)){
                magitBlob = magitBlobItr;
            }
        }
        return magitBlob;
    }

    private String chainingFiveStrings(String i_Parent1CommitSha_1, String i_Parent2CommitSha_1, String i_CommitMessage, String i_Date, String i_Committer, String i_MainFolderSha_1) {
        String commitString = String.format("%s%s%s%s%s%s%s%s%s%s%s", i_MainFolderSha_1, System.lineSeparator(),
                i_Parent1CommitSha_1, System.lineSeparator(),
                i_Parent2CommitSha_1, System.lineSeparator(),
                i_CommitMessage, System.lineSeparator(),
                i_Date, System.lineSeparator(),
                i_Committer);
        return commitString;
    }

    private MagitSingleFolder findFolderByID(String i_FolderID, List<MagitSingleFolder> i_MagitFolders) {
        MagitSingleFolder magitSingleFolder = null;
        for(MagitSingleFolder magitSingleFolderItr : i_MagitFolders) {
            if(magitSingleFolderItr.getId().equals(i_FolderID)){
                magitSingleFolder = magitSingleFolderItr;
            }
        }
        return magitSingleFolder;
    }

    private MagitSingleCommit findCommitByID(String i_CommitId, List<MagitSingleCommit> i_MagitSingleCommits) {
        MagitSingleCommit magitSingleCommit = null;
        for(MagitSingleCommit magitSingleCommitItr : i_MagitSingleCommits){
            if(magitSingleCommitItr.getId().equals(i_CommitId)){
                magitSingleCommit = magitSingleCommitItr;
            }
        }
        return magitSingleCommit;
    }

    private void createHeadFile(MagitRepository i_MagitRepository) {
        String nameOfActiveBranch = i_MagitRepository.getMagitBranches().getHead();
        Path pathToHeadFile = Paths.get(i_MagitRepository.getLocation() + "/.magit/Branches/HEAD");
        CreateTextFile(pathToHeadFile.toString(), nameOfActiveBranch);
        CreateTextFile(Paths.get(pathToHeadFile.getParent().toString()+"/master").normalize().toString(),"");
    }

    public boolean IsPathToXMLFile(String repositoryXMLPath) {
        return IsEqualSuffix(repositoryXMLPath, ".xml");
    }

    public boolean IsExistXMLFile(String repositoryXMLPath) {
        File xmlFile = new File(repositoryXMLPath);
        return xmlFile.exists();
    }

    public boolean IsAlreadyRepository(MagitRepository i_MagitRepository) {
        boolean isRepository = false;
        if(IsAlreadyDirectoryInSystem(i_MagitRepository)) {
            File magitRepositoryDirectory = new File(i_MagitRepository.getLocation());
            File[] innerFiles = magitRepositoryDirectory.listFiles();
            for(File file : innerFiles){
                if(file.getName().equals(".magit")){
                    isRepository = true;
                }
            }
        }
        return isRepository;
    }

    public boolean IsAlreadyDirectoryInSystem(MagitRepository i_MagitRepository) {
        File magitRepositoryDirectory = new File(i_MagitRepository.getLocation());
        return magitRepositoryDirectory.isDirectory();
    }

    public XMLValidation CheckXMLValidation(MagitRepository magitRepository) {
        XMLValidation validation = XMLValidation.VALID_XML;
        boolean isLocalRepository = magitRepository.getMagitRemoteReference() != null && magitRepository.getMagitRemoteReference().getName() != null; // noy-validation

        if(isBlobIdDuplicate(magitRepository)){ validation = XMLValidation.DUPLICATE_BLOB_ID; }
        if(isFolderIdDuplicate(magitRepository)){ validation = XMLValidation.DUPLICATE_FOLDER_ID; }
        if(isCommitIdDuplicate(magitRepository)){ validation = XMLValidation.DUPLICATE_COMMIT_ID; }
        if(!isExistPointedBlobByFolder(magitRepository)){ validation = XMLValidation.FOLDER_POINT_TO_NOT_EXIST_BLOB; }
        if(!isExistPointedFolderByFolder(magitRepository)){ validation = XMLValidation.FOLDER_POINT_TO_NOT_EXIST_FOLDER; }
        if(isFolderPointedToHimself(magitRepository)){ validation = XMLValidation.FOLDER_POINT_HIMSELF; }
        if(!isExistPointedFolderByCommit(magitRepository)){ validation = XMLValidation.COMMIT_POINT_TO_NOT_EXIST_FOLDER; }
        if(!isCommitPointedToRootFolder(magitRepository)){ validation = XMLValidation.COMMIT_POINT_TO_NOT_ROOT_FOLDER; }
        if(!isExistPointedCommitByBranch(magitRepository)){ validation = XMLValidation.BRANCH_POINT_TO_NOT_EXIST_COMMIT; }
        if(!isExistActiveBranch(magitRepository)){ validation = XMLValidation.HEAD_POINT_TO_NOT_EXIST_BRANCH; }
        if(isLocalRepository && !isTrackingAfterRB(magitRepository)) { validation = XMLValidation.BRANCH_TRACING_AFTER_NON_REMOTE_BRANCH; }
        if(isLocalRepository && !isExistRR(magitRepository)) {validation = XMLValidation.REMOTE_REPOSITORY_NOT_FOUND; }

        return validation;
    }

    private boolean isExistRR(MagitRepository i_MagitRepository) {
        boolean isEXistRR = false; // noy-validation- delete some check
        String remoteRepositoryReference = i_MagitRepository.getMagitRemoteReference().getLocation();
        File remoteReference = new File(remoteRepositoryReference);
        if(remoteReference.exists()){
            isEXistRR = true;
        }
        return isEXistRR;
    }

    private boolean isTrackingAfterRB(MagitRepository i_MagitRepository) {
        boolean isTrackingAfterRB = false;
        List<MagitSingleBranch> magitSingleBranchesList = i_MagitRepository.getMagitBranches().getMagitSingleBranch();
        for (MagitSingleBranch magitSingleBranch : magitSingleBranchesList) {
            if(magitSingleBranch.isTracking()){
                String trackingAfter = magitSingleBranch.getTrackingAfter();
                isTrackingAfterRB = isExistRB(trackingAfter, magitSingleBranchesList);
                if(isTrackingAfterRB == false){
                    break;
                }
            }
        }
        return isTrackingAfterRB;
    }

    private boolean isExistRB(String i_TrackingAfter, List<MagitSingleBranch> i_MagitSingleBranchesList) {
        boolean existRB = false;
        for (MagitSingleBranch magitSingleBranch : i_MagitSingleBranchesList) {
            if(magitSingleBranch.getName().equals(i_TrackingAfter)){ // what about branch not exist ???
                existRB = magitSingleBranch.isIsRemote();
            }
        }
        return existRB;
    }

    private boolean isExistActiveBranch(MagitRepository i_MagitRepository) {
        boolean isExistBranch = false;
        String headBranchName = i_MagitRepository.getMagitBranches().getHead();
        List<MagitSingleBranch> magitSingleBranchesList = i_MagitRepository.getMagitBranches().getMagitSingleBranch();
        for (MagitSingleBranch magitSingleBranch : magitSingleBranchesList) {
            if (magitSingleBranch.getName().equals(headBranchName)) {
                isExistBranch = true;
            }
        }
        return isExistBranch;
    }

    private boolean isExistPointedCommitByBranch(MagitRepository i_MagitRepository) {
        boolean isExistCommit = true;
        List<MagitSingleBranch> magitSingleBranchesList = i_MagitRepository.getMagitBranches().getMagitSingleBranch();

        for(MagitSingleBranch magitSingleBranch : magitSingleBranchesList) {
            String commitId = magitSingleBranch.getPointedCommit().getId();
            MagitSingleCommit commit = findCommitByID(commitId, i_MagitRepository.getMagitCommits().getMagitSingleCommit());
            if(commit == null){
                isExistCommit = false;
            }
        }
        return isExistCommit;
    }

    private boolean isCommitPointedToRootFolder(MagitRepository i_MagitRepository) {
        boolean isRootFolder = true;
        List<MagitSingleCommit> magitSingleCommitsList = i_MagitRepository.getMagitCommits().getMagitSingleCommit();
        for(MagitSingleCommit magitSingleCommit : magitSingleCommitsList){
            String idFolder = magitSingleCommit.getRootFolder().getId();
            MagitSingleFolder folder = findFolderByID(idFolder, i_MagitRepository.getMagitFolders().getMagitSingleFolder());
            if(folder != null){
                if(!folder.isIsRoot()){
                    isRootFolder = false;
                }
            }
        }
        return isRootFolder;
    }

    private boolean isExistPointedFolderByCommit(MagitRepository i_MagitRepository) {
        boolean isExistFolder = true;
        List<MagitSingleCommit> magitSingleCommitsList = i_MagitRepository.getMagitCommits().getMagitSingleCommit();
        for(MagitSingleCommit magitSingleCommit : magitSingleCommitsList){
            String idFolder = magitSingleCommit.getRootFolder().getId();
            if(findFolderByID(idFolder, i_MagitRepository.getMagitFolders().getMagitSingleFolder()) == null){
                isExistFolder = false;
            }
        }
        return isExistFolder;
    }

    private boolean isFolderPointedToHimself(MagitRepository i_MagitRepository) {
        boolean isPointedToHimself = false;
        List<MagitSingleFolder> magitSingleFolderList = i_MagitRepository.getMagitFolders().getMagitSingleFolder();
        for(MagitSingleFolder magitSingleFolder : magitSingleFolderList){
            List<Item> itemsList = magitSingleFolder.getItems().getItem();
            for(Item item : itemsList){
                if(item.getType().equals("folder")){
                    if(item.getId().equals(magitSingleFolder.getId())){
                        isPointedToHimself = true;
                    }
                }
            }
        }
        return isPointedToHimself;
    }

    private boolean isExistPointedBlobByFolder(MagitRepository i_MagitRepository) {
        boolean isExistBlob = true;
        List<MagitSingleFolder> magitSingleFolderList = i_MagitRepository.getMagitFolders().getMagitSingleFolder();
        for(MagitSingleFolder magitSingleFolder : magitSingleFolderList){
            List<Item> itemsList = magitSingleFolder.getItems().getItem();
            for(Item item : itemsList){
                if(item.getType().equals("blob")){
                    if(findBlobByID(item.getId(), i_MagitRepository.getMagitBlobs().getMagitBlob()) == null){
                        isExistBlob = false;
                    }
                }
            }
        }
        return isExistBlob;
    }

    private boolean isExistPointedFolderByFolder(MagitRepository i_MagitRepository) {
        boolean isExistFolder = true;
        List<MagitSingleFolder> magitSingleFolderList = i_MagitRepository.getMagitFolders().getMagitSingleFolder();
        for(MagitSingleFolder magitSingleFolder : magitSingleFolderList){
            List<Item> itemsList = magitSingleFolder.getItems().getItem();
            for(Item item : itemsList){
                if(item.getType().equals("folder")){
                    if(findFolderByID(item.getId(), i_MagitRepository.getMagitFolders().getMagitSingleFolder()) == null){
                        isExistFolder = false;
                    }
                }
            }
        }
        return isExistFolder;
    }

    private boolean isBlobIdDuplicate(MagitRepository i_MagitRepository) {
        boolean isDuplicate = false;
        List<MagitBlob> magitBlobsList = i_MagitRepository.getMagitBlobs().getMagitBlob();
        List<String> idMagitBlobsList = new ArrayList<>();
        Set<String> idMagitBlobsSet = new HashSet<>();

        for(MagitBlob magitBlob : magitBlobsList){
           idMagitBlobsList.add(magitBlob.getId());
           idMagitBlobsSet.add(magitBlob.getId());
        }
        if(idMagitBlobsSet.size() < idMagitBlobsList.size()) {
            isDuplicate = true;
        }
        return isDuplicate;
    }

    private boolean isFolderIdDuplicate(MagitRepository i_MagitRepository) {
        boolean isDuplicate = false;
        List<MagitSingleFolder> magitSingleFolderList = i_MagitRepository.getMagitFolders().getMagitSingleFolder();
        List<String> idMagitFoldersList = new ArrayList<>();
        Set<String> idMagitFoldersSet = new HashSet<>();

        for(MagitSingleFolder magitSingleFolder : magitSingleFolderList){
            idMagitFoldersList.add(magitSingleFolder.getId());
            idMagitFoldersSet.add(magitSingleFolder.getId());
        }
        if(idMagitFoldersSet.size() < idMagitFoldersList.size()) {
            isDuplicate = true;
        }
        return isDuplicate;
    }

    private boolean isCommitIdDuplicate(MagitRepository i_MagitRepository) {
        boolean isDuplicate = false;
        List<MagitSingleCommit> magitCommitsList = i_MagitRepository.getMagitCommits().getMagitSingleCommit();
        List<String> idMagitCommitsList = new ArrayList<>();
        Set<String> idMagitCommitsSet = new HashSet<>();

        for(MagitSingleCommit magitSingleCommit : magitCommitsList){
            idMagitCommitsList.add(magitSingleCommit.getId());
            idMagitCommitsSet.add(magitSingleCommit.getId());
        }
        if(idMagitCommitsSet.size() < idMagitCommitsList.size()) {
            isDuplicate = true;
        }
        return isDuplicate;
    }

    public boolean HasFilesInWC() {
        return m_ActiveRepository.HasFilesInWC();
    }

    public RepositoryFile GetCurrentCommitMainFolder() { // Dekel
        return m_ActiveRepository.GetCurrentCommitMainFolder();
    }

    public Delta GetDelta() {
        return m_ActiveRepository.GetDelta(m_UserName);
    }

    public LocalBranch FindBranchByName(String i_BranchName) {
            List<LocalBranch> branches = GetActiveRepository().GetMagit().GetBranches();
            LocalBranch branchToMerge = null;
            for(LocalBranch localBranch : branches){
                if(localBranch.GetName().equals(i_BranchName))
                    branchToMerge = localBranch;
            }
            return branchToMerge;
    }
    public Delta GetDelta(Commit i_Commit, Commit i_ParentCommit) {
        return m_ActiveRepository.GetDelta(i_Commit, i_ParentCommit);
    }


    public void CloneRepository(String i_PathToRepositoryToClone, String i_PathToDestinationToClone, String i_RepositoryName) throws IOException {
        FileUtils.copyDirectory(new File(i_PathToRepositoryToClone), new File(i_PathToDestinationToClone));
        String pathToBranches = i_PathToDestinationToClone + "/.magit/Branches";
        String remoteRepositoryName = getRemoteRepositoryName(i_PathToRepositoryToClone);
        String pathToRemoteBranches = pathToBranches + "/" + remoteRepositoryName;
        File file = new File(pathToBranches);
        boolean createDestination = true;
        File[] subFiles = file.listFiles();
        for (File subFile : subFiles) {
            FileUtils.moveFileToDirectory(subFile, new File(pathToRemoteBranches), true);
            createDestination = false;
        }

        String headBranchName = DekelNoy3rd.Service.Methods.ReadContentOfTextFile(pathToRemoteBranches + "/HEAD");
        FileUtils.copyFileToDirectory(new File (pathToRemoteBranches + "/" + headBranchName), new File(pathToBranches) );
        FileUtils.moveFileToDirectory(new File (pathToRemoteBranches + "/HEAD" ), new File(pathToBranches), false );
        CreateTextFile(Paths.get(pathToBranches).getParent().toString() + "/Name", i_RepositoryName);
        CreateTextFile(i_PathToDestinationToClone + "/.magit/Remote", i_PathToRepositoryToClone);
        Path pathTobObjectsInLocal = Paths.get(i_PathToDestinationToClone + "/.magit/Objects");
        File[] zipFiles = pathTobObjectsInLocal.toFile().listFiles();
        String mainFolderNameOfRemoteRepository = new File(i_PathToRepositoryToClone).getName();
        String mainFolderNameOfLocalRepository = new File(i_PathToDestinationToClone).getName();
        for (File zipFile: zipFiles){
            String name = DekelNoy3rd.Service.Methods.GetZipFileName(zipFile.toPath());
            if (name.equals(mainFolderNameOfRemoteRepository)) {
                Unzip(zipFile.getPath().toString(), pathTobObjectsInLocal.toString());
                Path pathToNewTextFile = Paths.get(pathTobObjectsInLocal + "/" + name);
                Path pathToRenameFile = Paths.get(pathTobObjectsInLocal + "/" + mainFolderNameOfLocalRepository);
                pathToNewTextFile.toFile().renameTo(pathToRenameFile.toFile());
                DekelNoy3rd.Service.Methods.Zip(pathToRenameFile, zipFile.toPath());
                pathToRenameFile.toFile().delete();
            }
        }
    }

    private String getRemoteRepositoryName(String i_pathToRepositoryToClone) {
        Path pathToName = Paths.get(i_pathToRepositoryToClone + "/.magit/Name");
        String remoteRepositoryName = DekelNoy3rd.Service.Methods.ReadContentOfTextFile(pathToName.toString());
        return remoteRepositoryName;
    }

    public void Fetch() {
        m_ActiveRepository.Fetch();
        LoadExistRepository(m_ActiveRepository.GetPath().toString());
    }

    public List<Branch> FindLBOfCommit(Commit i_Commit) {
        List<LocalBranch> LBList = m_ActiveRepository.GetMagit().GetBranches();
        List<Branch> pointedToCommitBranches = new ArrayList<>();
        for(LocalBranch localBranch : LBList){
            if(localBranch.GetCommit() == i_Commit){
                pointedToCommitBranches.add(localBranch);
            }
        }
        return pointedToCommitBranches;
    }

    public List<Branch> FindRBOfCommit(Commit i_Commit) {
        List<RemoteBranch> RBList = m_ActiveRepository.GetRemote().GetRemoteBranches();
        List<Branch> pointedToCommitBranches = new ArrayList<>();

        for(RemoteBranch remoteBranch : RBList){
            if(remoteBranch.GetCommit() == i_Commit){
                pointedToCommitBranches.add(remoteBranch);
            }
        }
        return pointedToCommitBranches;
    }

    public void Pull() {
        m_ActiveRepository.Pull();
        LoadExistRepository(m_ActiveRepository.GetPath().toString());
        m_ActiveRepository.GetWC().Clean();
        m_ActiveRepository.GetMagit().GetActiveBranch().Deploy();
    }

    public void Push() {
        m_ActiveRepository.Push();
        LoadExistRepository(GetActiveRepositoryPath().toString());
    }

    public void PushBranchToRR(LocalBranch i_branch) {
        m_ActiveRepository.PushBranchToRR(i_branch);
        LoadExistRepository(m_ActiveRepository.GetPath().toString());
    }

    public boolean IsSmoethingToPush() {
        return m_ActiveRepository.IsSomethingToPush();
    }

    public void ResetHeadBranch(Commit i_Commit) {
        LocalBranch headBranch = m_ActiveRepository.GetMagit().GetActiveBranch();
        headBranch.SetCommit(i_Commit);
        String pathToHeadBranch = m_ActiveRepository.GetMagit().GetHead().GetActiveBranch().GetPath().toString();
        CreateTextFile(pathToHeadBranch, i_Commit.GetSha1());
    }

    public boolean IsFastMerge() {
        return m_ActiveRepository.IsFastMerge();
    }

    public void FastMerge() {
        m_ActiveRepository.FastMerge();
    }

    public void DeployHeadBranch() {
        m_ActiveRepository.DeployHeadBranch();
    }

    public List<Conflict> GetConflict() {
        return m_ActiveRepository.GetConflicts();
    }

    public void SetCommitInMerger() {
        m_ActiveRepository.SetCommitInMerger();
    }

    public void InitializeMerger(Branch i_branchToMerge) {
        m_ActiveRepository.InitializeMerger(i_branchToMerge);
    }

    public Commit GetActiveCommit() {
        return m_ActiveRepository.GetActiveCommit();
    }

    public boolean IsLocalRepository() {
        return m_ActiveRepository.GetIsLocal();
    }
}