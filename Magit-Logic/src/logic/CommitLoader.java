package logic;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import static DekelNoy3rd.Service.Methods.*;

public class CommitLoader {

    private Path m_PathToMagit;
    public CommitLoader(Path i_PathToMagit) {
        m_PathToMagit = i_PathToMagit;
    }

    public Commit getCommit(Path i_PathToBranch, Map<String,Commit> i_sha_1CommitMap) {
        String commitSha_1 = getSha1OfCommitDuePathOfBranch(i_PathToBranch);
        Commit commit =null;
        if(commitSha_1 != null && !commitSha_1.equals("")){
            commit = getActiveCommitRec(commitSha_1, i_sha_1CommitMap);
        }

        return commit;
    }

    public Commit GetCommit(File i_CommitFile, Map<String, Commit> i_Commits){

        return getActiveCommitRec(i_CommitFile.getName(), i_Commits);
    }

    private String getSha1OfCommitDuePathOfBranch(Path i_PathToBranch) {
        BufferedReader sha_1Reader;
        String sha_1OfCommit ="";
        try {
            sha_1Reader = new BufferedReader(new FileReader(i_PathToBranch.toString()));
            sha_1OfCommit = sha_1Reader.readLine();
            sha_1Reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sha_1OfCommit;
    }

    private Commit getActiveCommitRec(String activeCommitSha_1, Map<String,Commit> i_sha_1CommitMap) {
        List <Commit> parentsCommit = null;
        Commit commit = null;
        if(!activeCommitSha_1.equals("There is no earlier version" )){
            try {
                Path pathToCommit = Paths.get(m_PathToMagit.toString() +"/" + activeCommitSha_1);
                String commitContent = ReadContentOfTextFile(pathToCommit.toString());
                String[] commitTextLines = commitContent.split(System.lineSeparator());

                String sha_1MainFolder = commitTextLines[0];
                String sha_1OfParentOneCommit = commitTextLines[1];
                String sha_1OfParentTwoCommit = commitTextLines[2];
                String messageCommit = commitTextLines[3];
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
                Date date = sdf.parse(commitTextLines[4]);
                UserName userName = new UserName(commitTextLines[5]);
                if(i_sha_1CommitMap.containsKey(activeCommitSha_1)) {
                    commit = i_sha_1CommitMap.get(activeCommitSha_1);
                }
                else{
                    Commit parentsOneCommit  = getActiveCommitRec(sha_1OfParentOneCommit, i_sha_1CommitMap);
                    if (parentsOneCommit != null){
                        parentsCommit = new ArrayList<>();
                        parentsCommit.add(parentsOneCommit);
                    }
                    Commit parentsTwoCommit  = getActiveCommitRec(sha_1OfParentTwoCommit, i_sha_1CommitMap);

                    if (parentsTwoCommit != null){
                        parentsCommit.add(parentsTwoCommit);
                    }
                    Path pathToMainFolder = m_PathToMagit.getParent();
                    commit = new Commit(sha_1MainFolder,parentsCommit,messageCommit,date,userName,pathToMainFolder);
                    i_sha_1CommitMap.put(commit.GetSha1(), commit);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return commit;
    }
}