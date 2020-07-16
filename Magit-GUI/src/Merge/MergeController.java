//package Merge;
//
//import logic.Commit;
//import puk.team.course.magit.ancestor.finder.AncestorFinder;
//import puk.team.course.magit.ancestor.finder.CommitRepresentative;
//
//import java.util.function.Function;
//
//public class MergeController implements CommitRepresentative{
//
//    Commit m_Commit;
//    Function<String, CommitRepresentative> sha1ToCommit;
//
//    AncestorFinder af = new AncestorFinder(sha1ToCommit);
//
//    String acsha1 = af.traceAncestor("a", "b");
//
//    @Override
//    public String getSha1() {
//        return m_Commit.GetSha1();
//    }
//
//    @Override
//    public String getFirstPrecedingSha1() {
//        return m_Commit.GetParentCommit().GetSha1();
//    }
//
//    @Override
//    public String getSecondPrecedingSha1() {
//        return m_Commit.GetParentCommit().GetSha1();
//    }
//}
