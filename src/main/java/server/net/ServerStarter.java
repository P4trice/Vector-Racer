package server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class functions as a starter for the server.
 *
 * <p>It creates a new instance of our server object,
 * which handles all incoming connections and clients.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class ServerStarter {

  /**
   * Creates a new server object, which will bind
   * a server socket on a specific port. The newly created server will
   * handle all incoming connections and clients.
   *
   * @param args the port number, on which a server should create a server socket.
   */
  public static void main(String[] args) {
    final Logger logger = LogManager.getLogger(ServerStarter.class);
    Server server = new Server(Integer.parseInt(args[0]));
  }
}