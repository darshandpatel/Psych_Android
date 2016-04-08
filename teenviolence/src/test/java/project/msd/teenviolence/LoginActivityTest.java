package project.msd.teenviolence;

import junit.framework.TestCase;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;
/**
 * Created by surindersokhal on 4/8/16.
 */
public class LoginActivityTest extends TestCase {


Login_Activity login = new Login_Activity();

//    multipleChoiceQuestion.setInCorrectAnswers("India");
//    multipleChoiceQuestion.setInCorrectAnswers("Brazil");
//    multipleChoiceQuestion.setInCorrectAnswers("Spain");
//    multipleChoiceQuestion.setCorrectAnswers("USA");

        public void setUp() throws Exception {
            super.setUp();

        }

    public void tearDown() throws Exception {

    }

    public void testcheckLogin() throws Exception{

        assertEquals(false,login.isValidUsername("surindersokhal@gmail.com"));
    }

}
