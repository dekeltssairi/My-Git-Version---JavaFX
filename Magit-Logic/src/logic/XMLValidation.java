package logic;

public enum XMLValidation {
    VALID_XML("The XML file is OK!"),
    DUPLICATE_BLOB_ID("The XML file is invalid. There are two or more blobs with the same ID"),
    DUPLICATE_FOLDER_ID("The XML file is invalid. There are two or more folders with the same ID"),
    DUPLICATE_COMMIT_ID("The XML file is invalid. There are two or more commits with the same ID"),
    FOLDER_POINT_TO_NOT_EXIST_BLOB("The XML file is invalid. There is a folder that points to a blob that does not exist"),
    FOLDER_POINT_TO_NOT_EXIST_FOLDER("The XML file is invalid. There is a folder that points to a folder that does not exist"),
    FOLDER_POINT_HIMSELF("The XML file is invalid. There is a folder that points to itself"),
    COMMIT_POINT_TO_NOT_EXIST_FOLDER("The XML file is invalid. There is a commit that points to a folder that does not exist"),
    COMMIT_POINT_TO_NOT_ROOT_FOLDER("The XML file is invalid. There is a commit that points to a folder that does not a root folder"),
    BRANCH_POINT_TO_NOT_EXIST_COMMIT("The XML file is invalid. There is a branch that points to a commit that does not exist"),
    HEAD_POINT_TO_NOT_EXIST_BRANCH("The XML file is invalid. The Head points to a branch that does not exist"),
    BRANCH_TRACING_AFTER_NON_REMOTE_BRANCH("The XML file is invalid. There is a branch that tracking after non remote branch!"),
    REMOTE_REPOSITORY_NOT_FOUND("The XML file is invalid. The repository has a reference to not exist remote repository");

    private final String error;

    XMLValidation(final String error) {
        this.error = error;
    }

    public String getName() {
        return error;
    }
}
