package server.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * This class functions as a client handler for incoming connections
 * by reading and writing from a socket connected to a client using threads.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Connection extends Thread {
  /**
   * Socket connected to the client.
   */
  public Socket playerSocket;

  /**
   * Functions as a communication channel to the connected client.
   */
  public PrintWriter output;

  /**
   * Functions as a communication channel from connected client.
   */
  public BufferedReader input;

  /**
   * Username chosen by the client.
   */
  public String name;

  /**
   * Holds a list of all currently connected clients.
   */
  List<Connection> playerList;

  /**
   * The port number used to communicate to the players socket.
   */
  int portNumber;

  /**
   * Holds the duration needed for the last ping from the server to the
   * to this socket bound client address.
   */
  public long latestPing;

  public Lobby inLobby;

  /**
   * Creates a client handler with a communication gateway to
   * the connected player using the socket bound to them
   * and a list of all connected players, updated by the server.
   *
   * <p>This constructor calls the run() method after initializing the class variables.
   *
   * @param playerSocket socket bound to the connected player
   * @param players list of all connected players
   * @param portNumber The port number used to communicate to the players socket.
   */
  public Connection(Socket playerSocket, List<Connection> players, int portNumber) {
    this.playerSocket = playerSocket;
    this.playerList = players;
    this.portNumber = portNumber;
    this.start();
  }

  /**
   * This threaded method handles all input and output
   * from/to a player socket, providing information such as message,
   * name and time.
   *
   * <p>The first input of this method specifies the player sockets name.
   * After that, a loop will start that constantly
   * checks for input coming from the socket.
   * Upon input, this method requests an action from the server protocol.
   * Depending on the disconnection circumstances, a different disconnection message is displayed.
   */
  public void run() {
    try {
      output = new PrintWriter(playerSocket.getOutputStream());
      input = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));


      name = input.readLine();
      name = ServerProtocol.checkName(name);

      output.println("/name " + name);
      output.flush();

    } catch (Exception e) {
      ServerProtocol.LOGGER.info("lost connection to " + playerSocket);
    }

    String inputLine;
    try {
      while ((inputLine = input.readLine()) != null || true) {
        try {
          ServerProtocol.process(inputLine, name);

        } catch (Exception e) {
          //e.printStackTrace();
          output.println("Empty message. Nothing happened");
          output.flush();
        }
      }
    } catch (Exception e) {
      //e.printStackTrace();
      ServerProtocol.LOGGER.info("disconnected: " + playerSocket);
    }
  }
}