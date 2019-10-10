import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import client.net.Client;
import org.junit.Before;
import org.junit.Test;
import server.net.Server;
import server.net.ServerStarter;

public class ClientTest {

  @Before
  public void startClient() {
    String[] args = { "localhost", "50505", "testName" };
    Client.main(args);
  }

  @Before
  public void startServer() {
    String[] args = { "50505" };
    ServerStarter.main(args);
  }

  @Test
  public void correctlyParsedValues() {
    mock(Server.class);
    mock(Client.class);
    assertEquals("testName", Client.name);
    assertEquals(50505, Client.portNumber);
    assertEquals("localhost", Client.ip);
  }

}
