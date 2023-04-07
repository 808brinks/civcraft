package nl.civcraft.core.gamecomponents;

import nl.civcraft.core.model.GameObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Bob on 27-1-2017.
 * <p>
 * This is probably not worth documenting
 */
@ExtendWith(MockitoExtension.class)
public class ItemComponentTest {

    public static final String TEST_TYPE = "testType";
    private ItemComponent underTest;
    @Spy
    private GameObject testGameObject = new GameObject();

    @BeforeEach
    public void setUp() throws Exception {
        underTest = new ItemComponent.Factory(TEST_TYPE).build();
        testGameObject.addComponent(underTest);
        verify(testGameObject).addComponent(underTest);
    }


    @Test
    public void construction() {
        assertThat(underTest.getGameObject(), is(testGameObject));
        assertThat(underTest.getType(), is(TEST_TYPE));
    }

    @Test
    public void factoryComponentType() {
        assertThat(new ItemComponent.Factory(TEST_TYPE).getComponentType(), equalTo(ItemComponent.class));
    }


    @Test
    public void setInInventory_gameObjectIsNotifiedOnChange() throws Exception {
        underTest.setInInventory(false);
        assertThat(underTest.isInInventory(), is(false));
        verify(testGameObject).changed();
    }

    @Test
    public void setInInventory_gameObjectIsNotNotifiedOnNoChange() throws Exception {
        underTest.setInInventory(true);
        assertThat(underTest.isInInventory(), is(true));
        verify(testGameObject, never()).changed();
    }


}