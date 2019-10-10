package server.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * Class for the Server to continuously ping all the connected clients.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */
public class ServerPing extends Thread {

  /** Holds the ip-address of the client. */
  InetAddress clientAddress;

  /** Holds the time when sent in milliseconds. */
  long msSent;

  /** Holds the time when received in milliseconds. */
  public long msReceived;

  /** Holds the list of all connected clients. */
  List<Connection> playerList;

  /**
   * Creates an instance of ServerPing on the chosen port using a list of all connected players.
   *
   * @param list the list that is assigned list of all connected clients
   */
  public ServerPing(List<Connection> list) {
    playerList = list;
    this.start();
  }

  /**
   * This threaded method constantly sends the ping to the connected clients using their ip-address
   * and saves the time when the ping is sent.
   * Furthermore it calculates the ping and displays it if it gets a
   * reply of the clients. Otherwise it displays a ping timeout notification.
   *
   * <p>This is achieved by a loop, that is activated as long as a client is connected to the
   * server.
   */
  public void run() {
    while (true) {

      for (Connection player : playerList) {

        clientAddress = player.playerSocket.getInetAddress();

        try {
          msSent = System.currentTimeMillis();
          if (clientAddress.isReachable(5000)) {
            msReceived = System.currentTimeMillis();
            player.latestPing = msReceived - msSent;
          } else {
            System.out.println(
                "Ping timeout on client address " + clientAddress + ", disconnecting client...");
            for (int i = 0; i < playerList.size(); i++) {
              if (playerList.get(i).name.equalsIgnoreCase(player.name)) {
                ServerProtocol.logout(i);
              }
            }
          }
        } catch (IOException e) {
          System.out.println("Network error occurred, please make sure your ports are valid.");
        }
      }
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
