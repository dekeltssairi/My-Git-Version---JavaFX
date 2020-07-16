package logic;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static DekelNoy3rd.Service.Methods.*;

public class Remote {

    private Repository m_LocalRepository;
    private Repository m_RemoteRepositroy;
    private Path m_PathToRemote;
    private Path m_pathToMagitInRemote;
    private Path m_PathToObjectsInRemote;
    private Path m_PathToBranchesInRemote;
    private Path m_PathToLocal;
    private Path m_PathToMagitInLocal;
    private Path m_PathToObjectsInLocal;
    private Path m_PathToRtbBranches;
    private Path m_PathToRemoteBranchesInLocal;

    private String m_RemoteRepsoitoryName;
    private List<RemoteBranch> m_RemoteBranches;
    private BranchLoader m_BranchLoader;

    public Remote(Path i_PathToRemote, Path i_PathToLocal, Repository i_LocalRepository) {

        m_PathToRemote = i_PathToRemote;
        m_pathToMagitInRemote = Paths.get(m_PathToRemote + "/.magit");
        m_PathToObjectsInRemote = Paths.get(m_pathToMagitInRemote + "/Objects");
        m_PathToBranchesInRemote = Paths.get(m_pathToMagitInRemote + "/Branches");
        m_PathToLocal = i_PathToLocal;
        m_PathToMagitInLocal = Paths.get(m_PathToLocal + "/.magit");
        m_PathToObjectsInLocal = Paths.get(m_PathToMagitInLocal + "/Objects");
        m_PathToRtbBranches = Paths.get(m_PathToMagitInLocal + "/Branches");
        m_RemoteRepsoitoryName = ReadContentOfTextFile(m_pathToMagitInRemote + "/Name");
        m_PathToRemoteBranchesInLocal = Paths.get(m_PathToRtbBranches + "/" + m_RemoteRepsoitoryName);
        m_RemoteRepositroy = new Repository(m_RemoteRepsoitoryName, i_PathToRemote.toString());
        m_LocalRepository = i_LocalRepository;

        m_RemoteBranches = new ArrayList<>();
        // m_BranchLoader = m_LocalRepository.GetMagit().GetBranchLoader();
        loadRemoteBranches(m_LocalRepository.GetMagit().GetCommits());
    }

//    private void loadRemoteBranches() {
//        File[] branchesTextFiles = new File(m_PathToRemoteBranchesInLocal.toString()).listFiles();
//        for (int i = 0; i < branchesTextFiles.length; i++) {
//            Path branchPath = Paths.get(branchesTextFiles[i].getAbsolutePath());
//            m_RemoteBranches.add((m_LocalRepository.GetMagit().GetBranchLoader().loadBranch(branchPath, new HashMap<>())).ToRemoteBranch());//m_BranchLoader.loadBranch(branchPath,i_Commits)).ToRemoteBranch());
//        }
//    }

    private void loadRemoteBranches( Map<String, Commit> i_Commits) {
        File[] branchesTextFiles = new File(m_PathToRemoteBranchesInLocal.toString()).listFiles();
        for (int i = 0; i < branchesTextFiles.length; i++) {
            Path branchPath = Paths.get(branchesTextFiles[i].getAbsolutePath());
            String SHA1ofCommit = DekelNoy3rd.Service.Methods.ReadContentOfTextFile(branchPath.toString());
            Commit commit = i_Commits.get(SHA1ofCommit);
            m_RemoteBranches.add((new LocalBranch(branchPath.toFile().getName(), commit, branchPath).ToRemoteBranch(m_RemoteRepositroy)));
        }
    }

    public void Fetch() {
        m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
        for (LocalBranch localBranch: m_RemoteRepositroy.GetMagit().GetBranches()){
            localBranch.CreateBranchTextFile(m_PathToRemoteBranchesInLocal);
        }

        for (Commit commit : m_RemoteRepositroy.GetMagit().GetCommits().values()){
            commit.CreateTextFile(m_LocalRepository.GetMagit().GetPath());
        }

        for (Path pathToZip : m_RemoteRepositroy.GetMagit().GetObjects().values()){
            try {
                FileUtils.copyFileToDirectory(pathToZip.toFile(), m_PathToObjectsInLocal.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File [] zipFiles = m_PathToObjectsInLocal.toFile().listFiles();
        for (File zipFile: zipFiles){
            String name = DekelNoy3rd.Service.Methods.GetZipFileName(zipFile.toPath());
            if (name.equals(m_PathToRemote.toFile().getName())){
                DekelNoy3rd.Service.Methods.Unzip(zipFile.toPath().toString(),m_PathToObjectsInLocal.toString());
                Path pathToNewTextFile = Paths.get(m_PathToObjectsInLocal + "/" + name);
                String mainFolderName = m_LocalRepository.GetPath().toFile().getName();
                Path pathToRenameFile =  Paths.get(m_PathToObjectsInLocal + "/" + mainFolderName);
                pathToNewTextFile.toFile().renameTo(pathToRenameFile.toFile());
                Path pathToZipInLocal = Paths.get(m_PathToObjectsInLocal + "/" + zipFile.getName());
                DekelNoy3rd.Service.Methods.Zip(pathToRenameFile,pathToZipInLocal);
                pathToRenameFile.toFile().delete();
            }
        }
    }

    public void Pull() {
        LocalBranch headBranchInLocal = m_LocalRepository.GetMagit().GetActiveBranch();
        LocalBranch branchInRemote = null;

        for (LocalBranch localBranch: m_RemoteRepositroy.GetMagit().GetBranches()){
            if (localBranch.GetName().equals(headBranchInLocal.GetName())){
                branchInRemote = localBranch;
            }
        }

        branchInRemote.CreateBranchTextFile(m_PathToRemoteBranchesInLocal);
        branchInRemote.CreateBranchTextFile(m_PathToRtbBranches);
        pullCommitRec(branchInRemote.GetCommit());
    }

    private void pullZipFileRec(String i_SHA1, String i_Item, boolean i_IsFirstRecursionInstance)  {

        if (!m_LocalRepository.GetMagit().GetObjects().containsKey(i_SHA1)){
            Path pathToZipInRemote = m_RemoteRepositroy.GetMagit().GetObjects().get(i_SHA1);
            try {
                FileUtils.copyFileToDirectory(pathToZipInRemote.toFile(), m_PathToObjectsInLocal.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i_IsFirstRecursionInstance){
                DekelNoy3rd.Service.Methods.Unzip(pathToZipInRemote.toString(),m_PathToObjectsInLocal.toString());
                String name = DekelNoy3rd.Service.Methods.GetZipFileName(pathToZipInRemote);
                Path pathToNewTextFile = Paths.get(m_PathToObjectsInLocal + "/" + name);
                String mainFolderName = m_LocalRepository.GetPath().toFile().getName();
                Path pathToRenameFile =  Paths.get(m_PathToObjectsInLocal + "/" + mainFolderName);
                pathToNewTextFile.toFile().renameTo(pathToRenameFile.toFile());
                Path pathToZipInLocal = Paths.get(m_PathToObjectsInLocal + "/" + pathToZipInRemote.toFile().getName());
                DekelNoy3rd.Service.Methods.Zip(pathToRenameFile,pathToZipInLocal);
                pathToRenameFile.toFile().delete();
                i_IsFirstRecursionInstance = false;
            }
            if (i_Item.equals("Folder")){
                String zipContent = DekelNoy3rd.Service.Methods.ReadFromZip(pathToZipInRemote.toString());
                zipContent = fixString(zipContent);
                String[] linesOfZip = zipContent.split(System.lineSeparator());
                for (String str : linesOfZip) { // only if folder
                    String[] output = str.split(",");
                    pullZipFileRec(output[1], output[2], i_IsFirstRecursionInstance);
                }
            }
        }
    }

    public List<RemoteBranch> GetRemoteBranches() {
        return m_RemoteBranches;
    }

    public boolean IsHeadRtb() {
        boolean isHeadRtb = false;
        for (RemoteBranch remoteBranch : m_RemoteBranches){
            if (remoteBranch.GetName().equals(m_LocalRepository.GetMagit().GetActiveBranch().GetName())){
                isHeadRtb = true;
            }
        }
        return isHeadRtb;
    }

    public boolean IsRemoteClear() {
        m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
        return !m_RemoteRepositroy.IsSomethingToCommit(new UserName());
    }

    public void Push() {
        LocalBranch localHeadBranch = m_LocalRepository.GetMagit().GetActiveBranch();
        localHeadBranch.CreateBranchTextFile(m_PathToBranchesInRemote);
        localHeadBranch.CreateBranchTextFile(m_PathToRemoteBranchesInLocal);
        pushCommitRec(localHeadBranch.GetCommit());
        m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
        if (localHeadBranch.GetName().equals(m_RemoteRepositroy.GetMagit().GetActiveBranch().GetName())){
            m_RemoteRepositroy.GetWC().Clean();
            m_RemoteRepositroy.GetMagit().GetActiveBranch().Deploy();
        }

    }

//    public void CommitRec(Commit i_Commit, Repository i_repository, Method method){
//        boolean hasIdenticalCommit = i_repository.GetMagit().GetCommits().containsKey(i_Commit.GetSha1());
//        if (!hasIdenticalCommit) {
//            i_Commit.CreateTextFile(i_repository.GetMagit().GetPath());
//            String mainFolderSHA1 = i_Commit.GetMainFolderSha_1();
//            boolean isFirstRecursionInstance = true;
//            try {
//                method.invoke((Object)mainFolderSHA1, (Object)"Folder", (Object)isFirstRecursionInstance);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            List<Commit> parentsCommit = i_Commit.GetParentCommit();
//            if (parentsCommit != null) {
//                if (parentsCommit.size() > 0) {
//                    CommitRec(parentsCommit.get(0), i_repository, method);
//                }
//                if (parentsCommit.size() == 2) {
//                    CommitRec(parentsCommit.get(0), i_repository, method);
//                }
//            }
//        }
//
//    }

    public void pushZipFileRec(String i_SHA1, String i_Item,  boolean i_IsFirstRecursionInstance){
        if (!m_RemoteRepositroy.GetMagit().GetObjects().containsKey(i_SHA1)){
            Path pathToZipInLocal = m_LocalRepository.GetMagit().GetObjects().get(i_SHA1);
            try {
                FileUtils.copyFileToDirectory(pathToZipInLocal.toFile(), m_PathToObjectsInRemote.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i_IsFirstRecursionInstance){
                DekelNoy3rd.Service.Methods.Unzip(pathToZipInLocal.toString(),m_PathToObjectsInRemote.toString());
                String name = DekelNoy3rd.Service.Methods.GetZipFileName(pathToZipInLocal);
                Path pathToNewTextFile = Paths.get(m_PathToObjectsInRemote + "/" + name);
                String mainFolderName = m_RemoteRepositroy.GetPath().toFile().getName();
                Path pathToRenameFile =  Paths.get(m_PathToObjectsInRemote + "/" + mainFolderName);
                pathToNewTextFile.toFile().renameTo(pathToRenameFile.toFile());
                Path pathToZipInRemote = Paths.get(m_PathToObjectsInRemote + "/" + pathToZipInLocal.toFile().getName());
                DekelNoy3rd.Service.Methods.Zip(pathToRenameFile,pathToZipInRemote);
                pathToRenameFile.toFile().delete();
                i_IsFirstRecursionInstance = false;
            }
            if (i_Item.equals("Folder")){
                String zipContent = DekelNoy3rd.Service.Methods.ReadFromZip(pathToZipInLocal.toString());
                zipContent = fixString(zipContent);
                String[] linesOfZip = zipContent.split(System.lineSeparator());
                for (String str : linesOfZip) { // only if folder
                    String[] output = str.split(",");
                    pushZipFileRec(output[1], output[2], i_IsFirstRecursionInstance);
                }
            }
        }
    }

    public boolean IsRemoteBranchInLocalSameAsRemoteBranchInRemote() {
        String headBranchInLocalName = m_LocalRepository.GetMagit().GetActiveBranch().GetName();
        RemoteBranch remoteBranchInLocal = null;
        LocalBranch branchInRemote = null;
        for (RemoteBranch remoteBranch: m_RemoteBranches){
            if (remoteBranch.GetOriginName().equals(headBranchInLocalName)){
                remoteBranchInLocal = remoteBranch;
            }
        }

        for (LocalBranch localBranch: m_RemoteRepositroy.GetMagit().GetBranches()){
            if (localBranch.GetName().equals(headBranchInLocalName)){
                branchInRemote = localBranch;
            }
        }

        return (remoteBranchInLocal.GetCommit().equals(branchInRemote.GetCommit()));
    }

    public boolean IsSomeThingToPush() {
        LocalBranch headBranchRtb = m_LocalRepository.GetMagit().GetActiveBranch();
        RemoteBranch remoteHeadBranchInLocal =null;
        for (RemoteBranch remoteBranch: m_RemoteBranches){
            String originRBName = remoteBranch.GetOriginName();
            if (originRBName.equals(headBranchRtb.GetName())){
                remoteHeadBranchInLocal = remoteBranch;
            }
        }
        return !remoteHeadBranchInLocal.GetCommit().GetSha1().equals(headBranchRtb.GetCommit().GetSha1());
    }

    public boolean IsHeadBranchInRemoteIsRtbInLocal() {
        boolean IsHeadBranchInRemoteIsRtbInLocal = false;
        m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
        LocalBranch headBranchInRemote = m_RemoteRepositroy.GetMagit().GetActiveBranch();
        for (LocalBranch rtbBranch: m_LocalRepository.GetMagit().GetBranches()){
            if(rtbBranch.GetName().equals(headBranchInRemote.GetName())){
                IsHeadBranchInRemoteIsRtbInLocal = true;
            }
        }
        return IsHeadBranchInRemoteIsRtbInLocal;
    }

    public boolean isHeadBranchInLocalIsRtb() {
        boolean IsHeadBranchInLocalIsRtb = false;
        LocalBranch headBranchInLocal = m_LocalRepository.GetMagit().GetActiveBranch();
        for (RemoteBranch remoteBranch : m_RemoteBranches){
            String originName = remoteBranch.GetOriginName();
            if (headBranchInLocal.GetName().equals(originName)){
                IsHeadBranchInLocalIsRtb = true;
            }
        }
        return IsHeadBranchInLocalIsRtb;
    }

    private void pullCommitRec(Commit i_Commit) {
        boolean hasIdenticalCommit = m_LocalRepository.GetMagit().GetCommits().containsKey(i_Commit.GetSha1());
        if (!hasIdenticalCommit) {
            i_Commit.CreateTextFile(m_LocalRepository.GetMagit().GetPath());
            String mainFolderSHA1 = i_Commit.GetMainFolderSha_1();
            boolean isFirstRecursionInstance = true;
            pullZipFileRec(mainFolderSHA1, "Folder", isFirstRecursionInstance);
            List<Commit> parentsCommit = i_Commit.GetParentCommit();
            if (parentsCommit != null) {
                if (parentsCommit.size() > 0) {
                    pushCommitRec(parentsCommit.get(0));
                }
                if (parentsCommit.size() == 2) {
                    pushCommitRec(parentsCommit.get(1));
                }
            }
        }
    }

    private void pushCommitRec(Commit i_Commit) {
        boolean hasIdenticalCommit = m_RemoteRepositroy.GetMagit().GetCommits().containsKey(i_Commit.GetSha1());
        if (!hasIdenticalCommit) {
            i_Commit.CreateTextFile(m_RemoteRepositroy.GetMagit().GetPath());
            String mainFolderSHA1 = i_Commit.GetMainFolderSha_1();
            boolean isFirstRecursionInstance = true;
            pushZipFileRec(mainFolderSHA1, "Folder", isFirstRecursionInstance);
            List<Commit> parentsCommit = i_Commit.GetParentCommit();
            if (parentsCommit != null) {
                if (parentsCommit.size() > 0) {
                    pushCommitRec(parentsCommit.get(0));
                }
                if (parentsCommit.size() == 2) {
                    pushCommitRec(parentsCommit.get(1));
                }
            }
        }
    }

    public void PushBranchToRR(LocalBranch i_branch) {
        m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
        i_branch.CreateBranchTextFile(m_PathToBranchesInRemote);
        i_branch.CreateBranchTextFile(m_PathToRemoteBranchesInLocal);
        pushCommitRec(i_branch.GetCommit());
    }
}