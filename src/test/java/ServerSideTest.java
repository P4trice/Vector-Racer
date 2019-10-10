import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import client.net.Client;
import com.sun.javafx.application.LauncherImpl;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import server.net.Connection;
import server.net.Server;
import server.net.ServerProtocol;
import server.net.ServerStarter;
import shared.Launcher;

import java.net.Socket;
import java.text.DecimalFormat;

public class ServerSideTest {

  public static boolean setupdone = false;

  @Before
  public void setUp() {
    if (!setupdone) {
      Server server = new Server(1337);
    } else {
      return;
    }
    setupdone = true;
  }


  @Test
  public void correctlyParsedValues() {
    mock(Server.class);
    assertEquals(1337, Server.portNumber);
  }

  @Test
  public void checkNameTest() {
    String test = ServerProtocol.checkName("testName");
    assertEquals("testName", test);
  }

  @Test
  public void checkProcess() {
    ServerProtocol.process("input", "testName");

  }

  @Test
  public void checkTimeTest() {
    LocalDateTime time = new LocalDateTime();

    DecimalFormat df = new DecimalFormat("00");

    String hour = df.format(time.getHourOfDay());
    String minute = df.format(time.getMinuteOfHour());

    String shouldTime =  "[" + hour + ":" + minute + "] ";
    String timeMinutes = ServerProtocol.getTimeMinutes();
    assertEquals(timeMinutes, shouldTime);
  }

  @Test
  public void createLobbyTest() {
    ServerProtocol.createLobby("testLobby", "testName");
    assertEquals("testLobby", Server.lobbyList.get(0).getLobbyName());
    ServerProtocol.createLobby("testLobby", "testName");
    assertEquals("testLobby", Server.lobbyList.get(0).getLobbyName());
    if (Server.lobbyList.size() < 2) {
      assertEquals(true, true);
    }
    else {
      assertEquals(false, true);
    }
  }
}
