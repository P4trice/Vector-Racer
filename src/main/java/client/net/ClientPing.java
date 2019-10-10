package client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class for the client to constantly ping the server and store ping duration.
 *
 * <p>The constructor starts the run() method after variables are declared.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */
public class ClientPing extends Thread {
  /**
   * Holds the ip address of the server the client is connected to.
   */
  InetAddress serverAddress;

  /**
   * Holds the time when the ping is sent.
   * Milliseconds from Midnight Jan 1, 1970 UTC.
   */
  long msSent;

  /**
   * Holds the time when the ping is received.
   * Milliseconds from Midnight Jan 1, 1970 UTC.
   */
  public long msRecieved;

  String ip;


  /**
   * Creates an instance of ClientPing with a specified ip address to ping.
   * Starts the run()-method of the object after initialization of the object variables.
   *
   * @param ip specifies the address of the server.
   */
  public ClientPing(String ip) {
    this.ip = ip;
    Client.LOGGER.info("now pinging " + ip);
    try {
      serverAddress = InetAddress.getByName(ip);
    } catch (UnknownHostException e) {
      Client.LOGGER.info("could not resolve hostname");
    }
    this.start();
  }

  /**
   * This threaded method pings the provided ip address
   * in a regular interval and stores the duration.
   */
  public void run() {
    while (true) {
      try {
        msSent = System.currentTimeMillis();
        if (serverAddress.isReachable(5000)) {
          msRecieved = System.currentTimeMillis();
          Client.ping = msRecieved - msSent;
        } else {
          Client.LOGGER.info("Ping timeout on server, client will now close.");
          Client.exit();
        }
      } catch (IOException e) {
        Client.LOGGER.info("Network error occurred, client will now close.");
        Client.exit();
      }

      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
} 
