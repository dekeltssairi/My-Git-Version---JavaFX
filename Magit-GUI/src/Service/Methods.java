package Service;
import javafx.scene.control.Alert;

public class Methods {
    public static void ShowAlert(Alert.AlertType i_Type,String i_Title, String i_HeaderText, String i_ContentText){
        Alert alert = new Alert(i_Type);
        alert.setTitle(i_Title);
        alert.setHeaderText(i_HeaderText);
        alert.setContentText(i_ContentText);
        alert.showAndWait();
    }
}