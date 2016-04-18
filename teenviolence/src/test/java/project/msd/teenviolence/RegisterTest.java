package project.msd.teenviolence;

import android.util.Base64;
import android.widget.ArrayAdapter;
import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by Chini Sinha on 04/17/16.
 */
public class RegisterTest extends TestCase {

    Register register = new Register();


    public void setUp() throws Exception {
        super.setUp();

    }

    public void testPerformRegistration() throws Exception {
        final Semaphore semaphore = new Semaphore(0, true);
        byte[] b=new byte[10];
        Base64.encodeToString(b,Base64.URL_SAFE|Base64.NO_WRAP);
        //Boolean result;
        try {
            //result = register.performRegistration(semaphore);
            assertEquals(true, register.performRegistration(semaphore));
        } catch (Exception e) {
            assertNull(register.performRegistration(semaphore));
        }

    }

}
