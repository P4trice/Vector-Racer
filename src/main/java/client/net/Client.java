package client.net;

import client.game.GameInfo;
import client.game.PlayerInfo;
import client.graphics.GameNode;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.Move;

/**
 * This class acts as the starter for the client.
 *
 * <p>It starts a threaded Sender and a threaded Receiver necessary to receive and send messages.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Client {
  /**
   * Stores the current ping of the client to the server.
   */
  static long ping = 0;

  public static Move move;

  public static String name;

  public static PlayerInfo localPlayerInfo;

  public static GameInfo game;

  public static Sender send;

  public static int portNumber;

  public static String ip;

  public static boolean inLobby = false;

  public static ArrayList<String> list = new ArrayList<String>();

  public static final Logger LOGGER = LogManager.getLogger("CLIENT_LOG");

  /**
   * Parses ip and port, asks for a username and establishes a connection to a server.
   *
   * <p>Arguments are then used to create a connection to a server through a socket.
   * If a connection is successful, an instance of each, Send and Receive, are created.
   * After that, the user will be informed to which socket a connection has been established.
   *
   * @param args required information for a connection: ip and port
   */

  public static void main(String [] args) {

    ip = args[0];
    portNumber = Integer.parseInt(args[1]);

    Socket socket = null;

    //String name = username();

    name = args[2];

    try {
      Client.LOGGER.info("trying to connect...");

      socket = new Socket(ip, portNumber);

      send = new Sender(socket, name);
      Receiver receive = new Receiver(socket);

      new Thread(() -> Application.launch(GameNode.class)).start();

      ClientPing clientPing = new client.net.ClientPing(ip);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Client.LOGGER.info("connection established with: " + socket);
    }
  }

  /**
   * Kills the main thread and with that all other running threads.
   */

  static void exit() {
    System.exit(0);
  }

  public static String getName() {
    return name;
  }
}