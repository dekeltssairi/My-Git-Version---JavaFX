package logic;

import DataStructures.*;
import puk.team.course.magit.ancestor.finder.AncestorFinder;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class Merger {

    private LocalBranch m_HeadBranch;
    private Branch m_BranchToMerge;
    private Map<String, Commit> m_Commits;
    private Commit m_Ansetor;

    public Merger(LocalBranch i_HeadBranch) {
        m_HeadBranch = i_HeadBranch;
    }

    public void SetCommits(Map<String, Commit> i_Commits){
        m_Commits = i_Commits;
    }

    public void SetBranchToMerge(LocalBranch i_Branch) {
        m_BranchToMerge = i_Branch;
    }

    public void InitializeMerger(Branch i_BranchToMerge){
        m_Ansetor = GetAncestor(m_HeadBranch.GetCommit(), i_BranchToMerge.GetCommit());
        m_BranchToMerge = i_BranchToMerge;
    }

    public boolean IsFastMerge(){
        Commit A = m_HeadBranch.GetCommit();
        Commit B = m_BranchToMerge.GetCommit();
        boolean isFastMerge = false;
        Commit ancsetor = GetAncestor(A, B);
        if (ancsetor == A || ancsetor == B){
            isFastMerge = true;
        }
        return isFastMerge;
    }

    public void FastMerge () {
        //boolean nothingToUpdate = false;
        if (m_HeadBranch.GetCommit() == m_Ansetor){
            m_HeadBranch.SetCommit(m_BranchToMerge.GetCommit());
        }
//        else {
//            nothingToUpdate = true;
//        }
//        return nothingToUpdate;
        // textFiles?
    }

    public Commit GetAncestor(Commit A, Commit B){
        Function<String, CommitRepresentative> sha1ToCommit = new Function<String, CommitRepresentative>() {
            @Override
            public CommitRepresentative apply(String s) {
                CommitRepresentative commitRepresentative = new CommitRepresentative() {
                    @Override
                    public String getSha1() {
                        return s;
                    }
                    @Override
                    public String getFirstPrecedingSha1() {
                        Commit c = m_Commits.get(s);
                        if(c.GetParentCommit() != null)
                            return c.GetParentCommit().get(0).GetSha1();
                        else
                            return "";
                    }
                    @Override
                    public String getSecondPrecedingSha1() {
                        Commit c = m_Commits.get(s);
                        if(c.GetParentCommit() != null && c.GetParentCommit().size() != 1)
                            return c.GetParentCommit().get(1).GetSha1();
                        else
                            return "";
                    }
                };
                return commitRepresentative;
            }
        };

        AncestorFinder ancestorFinder = new AncestorFinder(sha1ToCommit);

        String ancestorSha_1 = ancestorFinder.traceAncestor(A.GetSha1(), B.GetSha1());

        System.out.println(ancestorSha_1);
        return m_Commits.get(ancestorSha_1);
    }

    public List<Conflict> GetConflicts(){
        List<Conflict> conflicts = new ListWithoutDuplications();
        Map<Path,RepositoryFile> mapA = ((Folder)m_BranchToMerge.GetCommit().GetMainFolder()).CopyToMapByPath();
        Map<Path,RepositoryFile>  mapB = ((Folder)m_HeadBranch.GetCommitMainFolder()).CopyToMapByPath();
        Map<Path,RepositoryFile>  mapAncsetor = ((Folder)m_Ansetor.GetMainFolder()).CopyToMapByPath();
        List<RepositoryFile> mergedMapsValues = new ListWithoutDuplications();
        mergedMapsValues.addAll(mapA.values());
        mergedMapsValues.addAll(mapB.values());
        mergedMapsValues.addAll(mapAncsetor.values());
        for (RepositoryFile repositoryFile: mergedMapsValues){
            if(repositoryFile instanceof Blob) {
                Path path = repositoryFile.GetPath();
                boolean consistInChildOne = mapA.containsValue(repositoryFile);
                boolean consistInChildTwo = mapB.containsValue(repositoryFile);
                boolean consistInAncestor = mapAncsetor.containsValue(repositoryFile);
                boolean childOneEqualToChildTwo = (consistInChildOne && consistInChildTwo && mapA.get(path).equals(mapB.get(path)));
                boolean childTwoEqualToAncestor = (consistInChildTwo && consistInAncestor && mapB.get(path).equals(mapAncsetor.get(path)));
                boolean childOneEqualToAncestor = (consistInChildOne && consistInAncestor && mapA.get(path).equals(mapAncsetor.get(path)));

                int caseNumber = getCaseOfRepositoryFile(consistInChildOne, consistInChildTwo, consistInAncestor, childOneEqualToChildTwo, childTwoEqualToAncestor, childOneEqualToAncestor);
                Case myCase = Case.ValueOf(caseNumber);
                if(caseNumber == 58){
                    myCase.SetRepositoryFile(mapA.get(repositoryFile.GetPath()));
                } else if(caseNumber == 57){
                    myCase.SetRepositoryFile(mapB.get(repositoryFile.GetPath()));
                }else{
                    myCase.SetRepositoryFile(repositoryFile);
                }
                Conflict conflict = myCase.doCaseAction();

                if ((conflict) != null) {
                    conflict.SetSonA(mapA.get(repositoryFile.GetPath()));
                    conflict.SetSonB(mapB.get(repositoryFile.GetPath()));
                    conflict.SetAncestor(mapAncsetor.get(repositoryFile.GetPath()));
                    conflicts.add(conflict);
                }
            }
        }
        return conflicts;
    }

    private int getCaseOfRepositoryFile(boolean i_ConsistInChildOne, boolean i_ConsistInChildTwo, boolean i_ConsistInAncsetor, boolean i_ChildOneEqualToChildTwo, boolean i_ChildTwoEqualToAncsetor, boolean i_ChildOneEqualToAnsetor) {
        int caseNumber = 0;
        int num = 1;
        int consistInChildOne = i_ConsistInChildOne ? 1 : 0;
        int consistInChildTwo = i_ConsistInChildTwo ? 1 : 0;
        int consistInChildAncestor = i_ConsistInAncsetor ? 1 : 0;
        int childOneEqualToChildTwo = i_ChildOneEqualToChildTwo ? 1 : 0;
        int childTwoEqualToAncsetor = i_ChildTwoEqualToAncsetor ? 1 : 0;
        int childOneEqualToAnsetor = i_ChildOneEqualToAnsetor ? 1 : 0;
        int[] option = {consistInChildOne, consistInChildTwo, consistInChildAncestor, childOneEqualToChildTwo, childTwoEqualToAncsetor, childOneEqualToAnsetor};
        for (int i = 5; i >= 0; i--) {
            caseNumber += option[i] * num;
            num *= 2;
        }
        return caseNumber;
    }


    private Set mergeMapsToSet(Map<Path, RepositoryFile> mapA, Map<Path, RepositoryFile> mapB, Map<Path, RepositoryFile> mapAncestor) {
        Set<RepositoryFile> set = new HashSet<RepositoryFile>();
//        {
//            @Override
//            public boolean equals(Object o) {
//                boolean equals = false;
//                RepositoryFile repositoryFile = (RepositoryFile)o;
//                for (RepositoryFile setRepositoryFile : this){
//                    if (repositoryFile.GetSha_1().equals(setRepositoryFile.GetSha_1())
//                            && repositoryFile.GetPath().equals(setRepositoryFile.GetPath())){
//                        equals = true;
//                    }
//                }
//                return equals;
//            }
//        };
//        addToSet(mapA);
//        addToSet(mapB);
//        addToSet(mapAncestor);
//
//        for(RepositoryFile repositoryFile : mapA.values()){
//            if(!set.contains(repositoryFile)){
//                set.add(repositoryFile);
//            }
//        }
        set.addAll(mapA.values());
        set.addAll(mapB.values());
        set.addAll(mapAncestor.values());
        return set;
    }

    public void merge() {

    }
}