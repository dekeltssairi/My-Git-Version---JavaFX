package logic;

import java.nio.file.Path;
import java.util.function.Consumer;

public class XMLLoader {
    private Path m_XMLPath;
    private Consumer<Path> m_Consumer;

    public XMLLoader(Path i_XMLPath, Consumer<Path> i_Consumer) {
        m_XMLPath = i_XMLPath;
        m_Consumer = i_Consumer;
    }

//    public void ConvertMagitRepositoryToOurRepository(MagitRepository i_MagitRepository) {
//        m_Consumer.accept(Paths.get(i_MagitRepository.getLocation()));
//        createHeadFile(i_MagitRepository);
//        createBranchesFiles(i_MagitRepository);
//        createNewRepositoryAndActivateIt(i_MagitRepository.getName(), i_MagitRepository.getLocation());
//    }
//
//    private void createBranchesFiles(MagitRepository i_MagitRepository) {
//        List<MagitSingleBranch> magitSingleBranches = i_MagitRepository.getMagitBranches().getMagitSingleBranch();
//        List<MagitSingleCommit> magitSingleCommits = i_MagitRepository.getMagitCommits().getMagitSingleCommit();
//
//        for (MagitSingleBranch magitSingleBranch : magitSingleBranches){
//            String commitId = magitSingleBranch.getPointedCommit().getId();
//            if
//        }
//
//    }
//
//    private boolean isUniqeCommitGenerate(List<MagitSingleCommit> i_MagitSingleCommit, String i_ID){
//        boolean isExistCommitGenerate = false;
//
//        for (MagitSingleCommit magitSingleCommit : i_MagitSingleCommit){
//            if (magitSingleCommit.getId().equals(i_ID)){
//                isExistCommitGenerate = true;
//            }
//        }
//        return isExistCommitGenerate;
//    }
//
//    private void createHeadFile(MagitRepository i_MagitRepository) {
//        String nameOfActiveBranch = i_MagitRepository.getMagitBranches().getHead();
//        Path pathToHeadFile = Paths.get(i_MagitRepository.getLocation() + "\\.magit\\Branches\\HEAD.txt");
//        CreateTextFile(pathToHeadFile.toString(), nameOfActiveBranch);
//    }
}
