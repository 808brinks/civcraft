package nl.civcraft.core.interaction.tools;


import nl.civcraft.core.interaction.MousePicker;
import nl.civcraft.core.managers.PrefabManager;
import nl.civcraft.core.managers.TaskManager;
import nl.civcraft.core.model.GameObject;
import nl.civcraft.core.tasks.PlaceBlock;
import nl.civcraft.core.tasks.Task;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * Created by Bob on 21-4-2017.
 * <p>
 * This is probably not worth documenting
 */
@ExtendWith(MockitoExtension.class)
public class BuildWallToolTest {
    private BuildWallTool underTest;

    @Mock
    private TaskManager taskManager;
    @Mock
    private PrefabManager stockpileManager;
    @Mock
    private PrefabManager blockManager;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;
    @Mock
    private PrefabManager planningGhostManager;
    @Mock
    private MousePicker mousePicker;


    @BeforeEach
    public void setUp() throws Exception {
        underTest = new BuildWallTool(mousePicker, taskManager, stockpileManager, blockManager, planningGhostManager);
    }

    @Test
    public void testPlacement_startNextToEnd() {
        GameObject start = new GameObject(new Matrix4f().translate(new Vector3f(1, 1, 1)));
        GameObject end = new GameObject(new Matrix4f().translate(new Vector3f(2, 1, 1)));
        when(mousePicker.pickNearest(any())).thenReturn(Optional.of(start), Optional.of(end));
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(1, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        verify(taskManager, times(2)).addTask(taskCaptor.capture());
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(0)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(1, 2, 1))));
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(1)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(2, 2, 1))));
    }


    @Test
    public void testPlacement_endNextToStart() {
        GameObject start = new GameObject(new Matrix4f().translate(new Vector3f(2, 1, 1)));
        GameObject end = new GameObject(new Matrix4f().translate(new Vector3f(1, 1, 1)));
        when(mousePicker.pickNearest(any())).thenReturn(Optional.of(start), Optional.of(end));
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(1, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        verify(taskManager, times(2)).addTask(taskCaptor.capture());
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(0)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(1, 2, 1))));
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(1)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(2, 2, 1))));
    }

    @Test
    public void testPlacement_startBehindEnd() {
        GameObject start = new GameObject(new Matrix4f().translate(new Vector3f(1, 1, 1)));
        GameObject end = new GameObject(new Matrix4f().translate(new Vector3f(1, 1, 2)));
        when(mousePicker.pickNearest(any())).thenReturn(Optional.of(start), Optional.of(end));
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(1, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        verify(taskManager, times(2)).addTask(taskCaptor.capture());
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(0)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(1, 2, 1))));
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(1)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(1, 2, 2))));
    }


    @Test
    public void testPlacement_endBehindStart() {
        GameObject start = new GameObject(new Matrix4f().translate(new Vector3f(1, 1, 2)));
        GameObject end = new GameObject(new Matrix4f().translate(new Vector3f(1, 1, 1)));
        when(mousePicker.pickNearest(any())).thenReturn(Optional.of(start), Optional.of(end));
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(1, 0);
        underTest.handleLeftClick();
        underTest.handleMouseMotion(0, 0);
        underTest.handleLeftClick();
        verify(taskManager, times(2)).addTask(taskCaptor.capture());
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(0)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(1, 2, 1))));
        assertThat(((PlaceBlock) taskCaptor.getAllValues().get(1)).getLocationToPlace(), is(new Matrix4f().translate(new Vector3f(1, 2, 2))));
    }

}