package project.msd.teenviolence;

import android.view.MenuItem;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Created by Chini Sinha on 04/17/16.
 */
public class HomeScreenTest extends TestCase {

    @Mock
    HomeScreen homeScreen;

    @Test
    public void testOnOptionsItemSelected() throws Exception {

        //PowerMockito.doThrow(new ArrayStoreException("Mock error")).when(StaticService.class);

        MenuItem menuItem = Mockito.mock(MenuItem.class);
        Mockito.when(menuItem.getItemId()).thenReturn(R.id.help);
        MenuPouplateItems menuPouplateItems = Mockito.mock(MenuPouplateItems.class);

        assertEquals(true, homeScreen.onOptionsItemSelected(menuItem));
        //Mockito.verify(MenuPouplateItems.showHelp(), Mockito.atLeastOnce());
    }
}
