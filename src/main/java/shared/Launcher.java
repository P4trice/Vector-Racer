package shared;

import client.net.Client;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import server.net.ServerStarter;

/**
 * This class provides the possibility to either start a server or a client of the game.
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Launcher { //right now we can't put the starter in an interface

  Button buttonServer;
  Button buttonClient;
  Button wisely;
  static String[] clientInfo = new String [3];
  static String[] serverInfo = new String [1];
  Stage window;

  /**
   * Parses arguments and starts a corresponding class.
   *
   * @param args input from commandline.
   */
  public static void main(String[] args) {
    if (args[0].equalsIgnoreCase("client")) {
      int i = args[1].indexOf(":");

      clientInfo[0] = args[1].substring(0, i);
      clientInfo[1] = args[1].substring(i + 1);

      if (args.length == 2) {
        clientInfo[2] = System.getProperty("user.name");
      } else {
        clientInfo[2] = args[2];
      }

      System.out.println(clientInfo[0] + " " + clientInfo[1] + " " + clientInfo[2] + " ");

      Client.main(clientInfo);

    } else if (args[0].equalsIgnoreCase("server")) {
      serverInfo[0] = args[1];

      ServerStarter.main(serverInfo);
    }
  }
}