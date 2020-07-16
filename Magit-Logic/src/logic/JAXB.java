package logic;
import Generate.MagitRepository;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.*;
import java.io.*;

public class JAXB {
    private final static String JAXB_XML_GENERATED_CLASSES_PACKAGE_NAME = "jaxbClasses";

    public static MagitRepository deserializeRepoXML(InputStream i_inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(MagitRepository.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (MagitRepository) unmarshaller.unmarshal(i_inputStream);
    }

    public static MagitRepository loadXML(String i_xmlPath) throws IOException {
        InputStream inputStream = FileUtils.openInputStream(FileUtils.getFile(i_xmlPath));
        try {
            return deserializeRepoXML(inputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}