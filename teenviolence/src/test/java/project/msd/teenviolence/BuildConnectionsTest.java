package project.msd.teenviolence;

import junit.framework.TestCase;

import java.io.InputStream;

/**
 * Created by Chini Sinha on 04/17/16.
 */
public class BuildConnectionsTest extends TestCase {


    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {

    }

    public void testBuildConnection() throws Exception{
        String url="http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/AuthenticatingUser?queryType=login&username=meenakshi&password=abc";
        assertNotNull(BuildConnections.buildConnection(url));

    }

    public void testisJSON() throws Exception{
        String url="http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/AuthenticatingUser?queryType=login&username=meenakshi&password=abc";
        assertNotNull(BuildConnections.getJSOnObject(BuildConnections.buildConnection(url)));

    }
}
