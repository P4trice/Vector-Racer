package client.graphics;

import client.net.Client;
import client.net.Sender;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class makes an AlertBox for the join-lobby-function.
 * It could be used as a blueprint for other AlertBoxes as well.
 *
 *@author Nakarin Srijumrat
 *@author Patrice Delley
 *@version %G%
 */

public class LobbyAlertBox {

  static ComboBox<String> comboBox;
  static String answer;

  /**
   * This method is for displaying the content of LobbyAlert.
   *
   * @param title for giving the title into the AlertBox.
   * @param message for the message that should be displayed.
   * @return data to be displayed/transmitted.
   */

  public static String display(String title, String message) {

    comboBox = new ComboBox<String>();

    for (int i = 0; i < Client.list.size(); i++) {
      comboBox.getItems().add(Client.list.get(i));
    }

    Stage window = new Stage();

    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle(title);
    window.setWidth(250);
    window.setOnCloseRequest(event -> {
      event.consume();
      answer = null;
      window.close();
    });


    Label label = new Label();
    label.setText(message);


    Button button = new Button("Submit");
    button.setOnAction(event -> {
      answer = comboBox.getValue();
      window.close();
    });


    comboBox.setPromptText("Your Choice");
    comboBox.setEditable(true);


    VBox layout = new VBox(10);
    layout.getChildren().addAll(label, comboBox, button);
    layout.setAlignment(Pos.CENTER);


    Scene scene = new Scene(layout);
    window.setScene(scene);
    window.showAndWait();


    if (answer == null) {
      return "/listempty";
    } else {
      return answer;
    }
  }
}
