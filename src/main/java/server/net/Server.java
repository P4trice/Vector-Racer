package server.net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class holds the server socket and handles all connections
 * from clients by creating a threaded client handler object called
 * "Connection", also keeps track of all connections in a list.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Server extends Thread {
  /**
   * Holds the socket used to listen for incoming connections.
   */
  private ServerSocket server;

  /**
   * Holds the list of all connected clients.
   */
  public static List<Connection> playerList;

  /**
   *  Holds the list of all lobbies.
   */

  public static List<Lobby> lobbyList;

  /**
   * Holds the port number, that is opened and available for incoming
   * connections.
   */
  public static int portNumber;

  /**
   * Object responsible for regularly pinging all connected clients.
   */
  ServerPing serverPing;

  /**
   * This text is responsible for correct working of high score in jar and out of it.
   */
  public static String path;


  /**
   * Creates a server socket bound to a specified port
   * and a list of connected players, then starts listener thread.
   * Also instantiates a serverPing object.
   *
   * <p>Because the listener needs to be threaded,
   * the .start() of this class will be called in the constructor.
   * @param portNumber specifies port number to be opened for the server socket
   */
  public Server(int portNumber) {
    this.portNumber = portNumber;
    try {
      server = new ServerSocket(portNumber);
      ServerProtocol.LOGGER.info("waiting for connections...");
      playerList = Collections.synchronizedList(new ArrayList<>());
      lobbyList = Collections.synchronizedList(new ArrayList<>());
      ServerProtocol.setPlayerList(playerList);
      ServerProtocol.setLobbyList(lobbyList);
      serverPing = new ServerPing(playerList);

      createHighscore();

      this.start();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * This method is always listening for connections onto the
   * server socket, creates object of it and adds them to the
   * list of connected players followed by calling the run()
   * method of a newly created object.
   *
   * <p>This is achieved by a loop with a blocking method.
   * At the beginning of the loop, server.accept() blocks the
   * thread until a new connection is established onto the server socket,
   * unblocking the thread and adding the new
   * connection to the list of connections. From there, the loop begins again.
   *
   * <p>The newly created object can also be called a client handler,
   * as the object processes the new connection in a new
   * thread.
   */
  @Override
  public void run() {
    try {
      while (true) {
        Socket player = server.accept();
        ServerProtocol.LOGGER.info("connection established with " + player);
        playerList.add(new Connection(player,playerList,portNumber));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Method to read Highscore.txt
   *
   * @return returns an ArrayList containing all lines from Highscore.txt
   */
  public static ArrayList<String> readHighscore() {
    try {
      ArrayList<String> lines = new ArrayList<String>();

      String line;

      FileInputStream fileIn = new FileInputStream(path);
      BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn));

      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
      return lines;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<String>(); //please never go here
  }

  /**
   *Method to create the correct path to our Highscore.
   */
  public static void createHighscore() {

    path = Server.class.getClassLoader().getResource("Highscore.txt").toString();
    if (path.contains("jar:")) {
      //string manipulation
      ServerProtocol.LOGGER.info("Accessed through jar");
      path = path.substring(10);
      int index = path.indexOf("build/");
      path = path.substring(0, index + 6);
      path = path.concat("resources/main/Highscore.txt");
      ServerProtocol.LOGGER.info(path);
    } else {
      ServerProtocol.LOGGER.info("Accessed through programm");
      path = path.substring(6);
      int index = path.indexOf("gruppe-3/");
      path = path.substring(0, index + 9);
      path = path.concat("src/main/resources/Highscore.txt");
      ServerProtocol.LOGGER.info(path);
    }
  }
}