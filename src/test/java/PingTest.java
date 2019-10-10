import static org.junit.Assert.assertNotEquals;

import client.net.ClientPing;
import org.junit.Test;
import org.mockito.Mockito;

public class PingTest {

  @Test
  public void clientPingIsActivelyPinging() throws Exception {
    ClientPing launcher = Mockito.spy(new ClientPing("localhost"));
    Thread.sleep(100);

    assertNotEquals(null, launcher.msRecieved);
  }
}
