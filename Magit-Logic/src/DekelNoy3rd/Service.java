package DekelNoy3rd;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.*;

public class  Service {

    public static class Methods {

        public static String ConvertLinesToContent(List<String> i_Lines) {
            String content = "";
            int linesNum = i_Lines.size();
            for(int i = 0; i < linesNum; i++){
                content += i_Lines.get(i);
                if(i != linesNum - 1){
                    content += System.lineSeparator();
                }
            }
            return content;
        }
        public static void CreateTextFile(String i_FilePath, String i_Content) {
             //assume that content comes with line seperator (\r\n) and not only \n
            File file = new File(i_FilePath);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(i_Content);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String fixString(String i_str) {
            BufferedReader reader;
            String content = "";
            try {
                reader = new BufferedReader(new StringReader(i_str));
                boolean isFirstLine = true;
                String currentLine = reader.readLine();
                while (currentLine != null) {
                    if (!isFirstLine) {
                        content += System.lineSeparator();
                    }
                    content += currentLine;
                    currentLine = reader.readLine();
                    isFirstLine = false;
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  content;
        }

        public static String ReadContentOfTextFile(String i_PathToBlob) {
            BufferedReader reader;
            String content = "";
            try {
                reader = new BufferedReader(new FileReader(i_PathToBlob));
                boolean isFirstLine = true;
                String currentLine = reader.readLine();
                while (currentLine != null) {
                    if (!isFirstLine) {
                        content += System.lineSeparator();
                    }
                    content += currentLine;
                    currentLine = reader.readLine();
                    isFirstLine = false;
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        public static void DeleteTextFile(Path i_Path) {
            File file = new File(i_Path.toString());
            file.delete();
            file.exists();
            i_Path.normalize();
            file = new File(i_Path.toString());
            file.delete();
        }

        public static void Zip(Path i_SourceFile, Path i_DestenationFile) {
            try {
                String sourceFile = i_SourceFile.toString();
                FileOutputStream fos = new FileOutputStream(i_DestenationFile.toString());
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                File fileToZip = new File(sourceFile);
                FileInputStream fis = null;

                fis = new FileInputStream(fileToZip);

                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                zipOut.close();
                fis.close();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static Path Unzip(String zipFilePath, String destDir) {
            File dir = new File(destDir);
            String fileTextName = "";
            // create output directory if it doesn't exist
            if (!dir.exists()) dir.mkdirs();
            FileInputStream fis;
            //buffer for read and write data to file
            byte[] buffer = new byte[1024];
            try {
                fis = new FileInputStream(zipFilePath);
                ZipInputStream zis = new ZipInputStream(fis);
                ZipEntry ze = zis.getNextEntry();
                fileTextName = ze.getName();
                while (ze != null) {
                    String fileName = ze.getName();
                    File newFile = new File(destDir + File.separator + fileName);
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    //close this ZipEntry
                    zis.closeEntry();
                    ze = zis.getNextEntry();
                }
                //close last ZipEntry
                zis.closeEntry();
                zis.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Paths.get(destDir + "/" + fileTextName);

        }

        public static void DeleteDirectory(File i_DirectoryToBeDeleted) {
            File[] allContents = i_DirectoryToBeDeleted.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    DeleteDirectory(file);
                }
            }
            i_DirectoryToBeDeleted.delete();
        }

        public static boolean IsSameName(Path i_FirstPath, Path i_SecondPath) {
            Path firstPathName = i_FirstPath.getFileName();
            Path secondPathName = i_SecondPath.getFileName();

            return firstPathName.equals(secondPathName);
        }

        public static boolean IsSamePathWithoutName(Path i_FirstPath, Path i_SecondPath) {
            Path i_FirstPathWithoutName = i_FirstPath.getParent();
            Path i_SecondPathWithoutName =  i_SecondPath.getParent();
            return (i_FirstPathWithoutName.toString()).equals(i_SecondPathWithoutName.toString());
        }

        public static String ReadFromZip(String pathToZip) {
            String zipContent = null;
            int read = 0;
            try {
                /*
                ZipFile zipFile = new ZipFile(pathToZip);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                ZipEntry entry = entries.nextElement();
                InputStream inputStream = zipFile.getInputStream(entry);
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = inputStream.read(buffer);
                result.write(buffer, 0, length);
                zipContent = result.toString();
                zipFile.close();
                */

                ZipFile zipFile = new ZipFile(pathToZip);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                ZipEntry entry = entries.nextElement();
                InputStream inputStream = zipFile.getInputStream(entry);
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1];
                int length;
                while ((length = inputStream.read(buffer))!=-1){
                    result.write(buffer, 0, length);
                }
                zipContent = result.toString();
                zipFile.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return zipContent;
        }

        public static boolean isDirectoryContainSpecificFolder(File i_Directory, String nameOfSubDirectory) {
            return new File(i_Directory.getAbsolutePath() + "/" + nameOfSubDirectory).isDirectory();
        }

        public static String RemoveSuffix(String i_StringWithSuffix) {
            int strLength = i_StringWithSuffix.length();
            int i;
            for (i = strLength - 1; i_StringWithSuffix.charAt(i) != '.'; i--) ;
            return new StringBuilder(i_StringWithSuffix).substring(0, i);
        }

        public static boolean IsEqualSuffix(String i_Str, String i_Suffix){
            boolean isTxtSuffix = false;
            if(i_Str.length() > 4){
                String suffix = new StringBuilder(i_Str).substring(i_Str.length() - 4, i_Str.length());
                isTxtSuffix = suffix.equals(i_Suffix);
            }
            return isTxtSuffix;
        }

        public static String ReturnSuffix(String i_StringWithSuffix) {
            int strLength = i_StringWithSuffix.length();
            int i;
            for (i = strLength - 1; i_StringWithSuffix.charAt(i) != '.'; i--) ;
            return new StringBuilder(i_StringWithSuffix).substring(i, strLength);
        }

        public static boolean IsBlobFile(Path i_Path){
            boolean isBlob = false;
            String nameFileStr = i_Path.getFileName().toString();
            int lengthStr = nameFileStr.length();
            if(lengthStr > 4) {// is not txt file
                String sb = new StringBuilder(nameFileStr).substring(nameFileStr.length() - 4, lengthStr);
                if(sb.equals(".txt"))
                    isBlob = true;
            }
            return isBlob;
        }

        public static void DeleteEmptyDirectoriesWithoutSpecificFolder(Path m_path, String i_Directory) {
            File mainFolder = new File (m_path.toString());
            File [] subFiles = mainFolder.listFiles();
            for (File file: subFiles) {
                if (!file.getName().equals(i_Directory))
                    DeleteEmptyDirectoriesRec(file);
            }
        }

        private static void DeleteEmptyDirectoriesRec(File i_file) {
            if (i_file.isDirectory()){
                File [] subFiles = i_file.listFiles();
                if (subFiles.length == 0){
                    i_file.delete();
                }
                else{
                    for (File file : subFiles){
                        DeleteEmptyDirectoriesRec(file);
                    }
                }
            }
        }

        public static void CopyFileOfDirectory(File i_FolderSource, File i_FolderDestinition) {
            File [] subFiles = i_FolderSource.listFiles();
            for (File file: subFiles){
                try {
                    FileUtils.copyFileToDirectory(file, i_FolderDestinition);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static String GetZipFileName(Path i_Path) {
            String name = null;
            try (ZipFile zipFile = new ZipFile(i_Path.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                ZipEntry entry = entries.nextElement();
                name = entry.getName();
            } catch (ZipException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return name;
        }

    }
}
