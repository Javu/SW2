package fantasyteam.sw2;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for base server class
 * @author jamessemple
 */
public class ServerTest {
    
    private Server server1;
    private Server server2;
    
    @BeforeMethod
    private void setupServer() {
        server1 = new Server();
        server2 = new Server();
    }
    
    
    @Test
    public void testServer() {
        
    }
}