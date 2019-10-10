package client.net;

import client.game.GameInfo;
import client.graphics.GameNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

import javafx.application.Platform;
import server.game.Location;

/**
 * Creates an instance of Receiver using a given socket.
 * A connection is made to the server from the specified socket.
 *
 * <p>The constructor starts the run() method after variables are declared.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Receiver extends Thread {
  /**
   * Holds the socket used to establish the connection to a server.
   */
  Socket socket;

  /**
   * Creates an instance of Receiver on a given socket with a clients name.
   *
   * <p>Starts the run() method after variables are initialized.
   *
   * @param socket the socket bound to the client
   */

  public Receiver(Socket socket) {
    this.socket = socket;
    this.start();
  }

  /**
   * This threaded method handles the input a user receives from the connection.
   *
   * <p>After a connection is established it constantly checks if the Server
   * has sent a message (has written a line in the receivers BufferedReader).
   * If there is a message, it is displayed.
   */

  public void run() {

    Client.LOGGER.info("starting receiver...");

    try (BufferedReader input = new BufferedReader(
            new InputStreamReader(socket.getInputStream()))) {
      String text;

      while (true) {
        text = input.readLine();
        if (text != null) {
          processInput(text);
          //Client.LOGGER.info(text);
        }
      }
    } catch (Exception e) {
      //e.printStackTrace();
      Client.LOGGER.info("The server has disconnected you...");
      System.exit(0);
    }
  }

  /**
   * This lets the client-side process input given from server.
   * With that it's able to differentiate between moves/messages.
   *
   * @param text the input given.
   */

  public void processInput(String text) {
    if (text.charAt(0) == '/') {
      String subText = text.substring(1, 5);
      subText.toLowerCase();
      switch (subText) {
        case "race" : {
          String[] playerNames = text.substring(6).split(",");
          Platform.runLater(() -> GameNode.initializeGame(playerNames));
          Platform.runLater(() -> GameNode.receiveMessage("All players ready. "
                  + "Race has begun!"));
          break;
        }

        case "move" : {
          String[] moveInfo = text.substring(6).split(",");
          Platform.runLater(() -> GameNode.updateNode(moveInfo));
          break;
        }

        case "name" : {
          Client.LOGGER.info("changing name to " + text.substring(6));
          Client.name = text.substring(6);
          break;
        }

        case "done" : {
          Platform.runLater(GameNode::removeCircles);
          Platform.runLater(GameNode::resetClickableTiles);
          Platform.runLater(() -> GameNode.finished = true);
          break;
        }

        case "fini" : {
          Platform.runLater(GameNode::clearField);
          Platform.runLater(GameNode::resetReadyButton);
          Platform.runLater(() -> GameNode.leaveLobby.setDisable(false));
          break;
        }

        case "dced" : {
          try {
            Thread.sleep(500);
          } catch (Exception e) {
            e.printStackTrace();
          }
          Client.exit();
          break;
        }

        case "lbby" : {
          if (Client.inLobby) {
            Client.inLobby = false;
            GameNode.setReady();
          } else {
            Client.inLobby = true;
            GameNode.setReady();
          }
          break;
        }

        case "list" : {
          for (int i = 0; i < Client.list.size(); i++) {
            Client.list.remove(i);
          }

          String[] temp = text.substring(6).split(", ");
          for (int i = 0; i < temp.length; i++) {
            Client.list.add(temp[i]);
          }
          break;
        }

        default: {
          Platform.runLater(() -> GameNode.receiveMessage(text));
        }
      }
    } else {
      //Client.LOGGER.info(text);
      Platform.runLater(() -> GameNode.receiveMessage(text));
    }
  }
}