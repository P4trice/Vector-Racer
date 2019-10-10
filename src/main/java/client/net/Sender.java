package client.net;

import client.graphics.GameNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.net.ServerProtocol;

/**
 * Creates an instance of Sender using a given socket.
 * A connection is made to the server from the specified socket.
 *
 * <p>The constructor starts the run() method after variables are declared.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Sender extends Thread {
  /**
   * Holds the socket used to establish the connection to a server.
   */
  Socket socket;

  /**
   * Holds the by the user chosen name, that will be displayed.
   */
  String name;

  /**
   * Stores the input coming from the command line.
   */
  String userInput;

  static PrintWriter output; //static may be able to be removed later

  /**
   * Creates an instance of Sender on a given socket with a clients name.
   *
   * <p>Starts the run() method after variables are initialized.
   *
   * @param socket the socket bound to the client
   * @param name username chosen by the client
   */

  public Sender(Socket socket, String name) {
    this.socket = socket;
    this.name = name;
    try {
      output = new PrintWriter(socket.getOutputStream(), true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.start();
  }

  /**
   * This threaded method handles the output respectively a Chat message from the client.
   *
   * <p>After a connection is established it constantly checks
   * whether a user has typed a message in the cmd, then sends this message to the server
   * with the clients name. When the client disconnects, a notification prompt is displayed.
   */

  public void run() {

    Client.LOGGER.info("starting sender...");
    try (
            //PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
    ) {
      output.println(name);
      while ((userInput = input.readLine()) != null) {
        if (userInput.matches(".*[a-zA-Z0-9äöüÄÖÜ]+.*")) {
          sendMessage(userInput);
        }
        /*if (userInput.equalsIgnoreCase("/lout")) {
          GameNode.chatWindow.appendText("you have been disconnected...");
          Client.exit();
          break;
        }*/
        if (userInput.equalsIgnoreCase("/ping")) {
          GameNode.chatWindow.appendText(ServerProtocol.getTimeMinutes()
                  + "Client to Server ping: " + Client.ping + "ms");
        }
      }
    } catch (Exception e) {
      //does not need to be caught because of disconnection
    }
  }

  public static void sendMessage(String message) {
    output.println(message);
    output.flush();
  }
}