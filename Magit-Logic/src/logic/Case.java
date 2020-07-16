package logic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum Case {
    ONE(1),
    TWO(2),
    TREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8){
        @Override
        public Conflict doCaseAction() {
            return new Conflict("File deleted by theirs and deleted by ours");
        }
    },
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    THIRTEEN(13),
    FOURTEEN(14),
    FIFTEEN(15),
    SIXTEEN(16){
        @Override
        public Conflict doCaseAction() {
            File file = new File(SIXTEEN.m_RepositpryFile.GetPath().toString());
            if(file.isDirectory()){
                if(!file.exists()){
                    file.mkdirs();
                }
            }else{
                Blob blob = (Blob)SIXTEEN.m_RepositpryFile;
                DekelNoy3rd.Service.Methods.CreateTextFile(file.getPath(), blob.GetContent());
            }
            return null;
        }

    },
    SWVWNTEEN(17),
    EIGHTEEN(18),
    NINETEEN(19),
    TWENTY(20),
    TWENTY_ONE(21),
    TWENTY_TWO(22),
    TWENTY_THREE(23),
    TWENTY_FOUR(24){
        @Override
        public Conflict doCaseAction() {
            return new Conflict("File deleted by theirs and changed by ours");
        }
    },
    TWENTY_FIVE(25),
    TWENTY_SIX(26),
    TWENTY_SEVEN(27),
    TWENTY_EIGHT(28),
    TWENTY_NINE(29),
    THIRTY(30),
    THIRTY_ONE(31),
    THIRTY_TWO(32){
        @Override
        public Conflict doCaseAction() {
            File file = new File(THIRTY_TWO.m_RepositpryFile.GetPath().toString());
            if(file.isDirectory()){
                if(!file.exists()){
                    file.mkdirs();
                }
            }else{
                Blob blob = (Blob)THIRTY_TWO.m_RepositpryFile;
                DekelNoy3rd.Service.Methods.CreateTextFile(file.getPath(), blob.GetContent());
            }
            return null;
        }
    },
    THIRTY_THREE(33),
    THIRTY_FOUR(34),
    THIRTY_FIVE(35),
    THIRTY_SIX(36),
    THIRTY_SEVEN(37),
    THIRTY_EIGHT(38),
    THIRTY_NINE(39),
    FORTY(40){
        @Override
        public Conflict doCaseAction() {
            return new Conflict("File deleted by theirs and changed by ours");
        }
    },
    FORTY_ONE(41),
    FORTY_TWO(42),
    FORTY_THREE(43),
    FORTY_FOUR(44),
    FORTY_FIVE(45),
    FORTY_SIX(46),
    FORTY_SEVEN(47),
    FORTY_EIGHT(48){
        @Override
        public Conflict doCaseAction() {
            return new Conflict("Added file by theirs, same file added by ours with different content");
        }
    },
    FORTY_NINE(49),
    FIFTY(50),
    FIFTY_ONE(51),
    FIFTY_TWO(52){
        @Override
        public Conflict doCaseAction() {
            return new Conflict("Added file by theirs, same file added by ours with same content");
        }
//        public Conflict doCaseAction() {
//            File file = new File(FIFTY_TWO.m_RepositpryFile.GetPath().toString());
//            if(file.isDirectory()){
//                if(!file.exists()){
//                    file.mkdirs();
//                }
//            }else{
//                Blob blob = (Blob)FIFTY_TWO.m_RepositpryFile;
//                DekelNoy3rd.Service.Methods.CreateTextFile(file.getPath(), blob.GetContent());
//            }
//            return null;
//        }
    },
    FIFTY_THREE(53),
    FIFTY_FOUR(54),
    FIFTY_FIVE(55),
    FIFTY_SIX(56){
        @Override
        public Conflict doCaseAction() {
            return new Conflict("Changed file by theirs, same file changed by ours with different content");
        }
    },
    FIFTY_SEVEN(57){
        @Override
        public Conflict doCaseAction() {
            File file = new File(FIFTY_SEVEN.m_RepositpryFile.GetPath().toString());
            if(file.isDirectory()){
                if(!file.exists()){
                    file.mkdirs();
                }
            }else{
                Blob blob = (Blob)FIFTY_SEVEN.m_RepositpryFile;
                DekelNoy3rd.Service.Methods.CreateTextFile(file.getPath(), blob.GetContent());
            }
            return null;
        }
    },
    FIFTY_EIGHT(58){
        @Override
        public Conflict doCaseAction() {
            File file = new File(FIFTY_EIGHT.m_RepositpryFile.GetPath().toString());
            if(file.isDirectory()){
                if(!file.exists()){
                    file.mkdirs();
                }
            }else{
                Blob blob = (Blob)FIFTY_EIGHT.m_RepositpryFile;
                DekelNoy3rd.Service.Methods.CreateTextFile(file.getPath(), blob.GetContent());
            }
            return null;
        }
    },
    FIFTY_NINE(59),
    SIXTY(60){
        @Override
        public Conflict doCaseAction() {
            return new Conflict("Changed file by theirs, same file changed by ours with same content");
        }
//        public Conflict doCaseAction() {
//            File file = new File(SIXTY.m_RepositpryFile.GetPath().toString());
//            if(file.isDirectory()){
//                if(!file.exists()){
//                    file.mkdirs();
//                }
//            }else{
//                Blob blob = (Blob)SIXTY.m_RepositpryFile;
//                DekelNoy3rd.Service.Methods.CreateTextFile(file.getPath(), blob.GetContent());
//            }
//            return null;
//        }
    },
    SIXTY_ONE(61),
    SIXTY_TWO(62),
    SIXTY_THREE(63){
        @Override
        public Conflict doCaseAction() {
            File file = new File(SIXTY_THREE.m_RepositpryFile.GetPath().toString());
            if(file.isDirectory()){
                if(!file.exists()){
                    file.mkdirs();
                }
            }else{
                Blob blob = (Blob)SIXTY_THREE.m_RepositpryFile;
                DekelNoy3rd.Service.Methods.CreateTextFile(file.getPath(), blob.GetContent());
            }
            return null;
        }
    };


    private RepositoryFile m_RepositpryFile;

    public void SetRepositoryFile(RepositoryFile i_RepositoryFile){
        m_RepositpryFile = i_RepositoryFile;
    }

    public Conflict doCaseAction() {
        return null;
    }

    private int value;


    private static Map map = new HashMap<>();
    private Case(int value) {
        this.value = value;
    }

    static {
        for (Case i_case : Case.values()) {
            map.put(i_case.value, i_case);
        }
    }

    public static Case ValueOf(int i_Case) {
        return (Case) map.get(i_Case);
    }
}
