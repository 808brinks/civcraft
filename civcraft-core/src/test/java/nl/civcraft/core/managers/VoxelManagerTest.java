package nl.civcraft.core.managers;


import nl.civcraft.core.gamecomponents.Voxel;
import nl.civcraft.core.model.GameObject;
import nl.civcraft.test.util.VoxelUtil;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Bob on 17-2-2017.
 * <p>
 * This is probably not worth documenting
 */
@ExtendWith(MockitoExtension.class)
public class VoxelManagerTest {


    private VoxelManager underTest;

    @BeforeEach
    public void setUp() throws Exception {
        underTest = new VoxelManager();
    }

    //<editor-fold desc="addVoxel">
    @Test
    public void testAddVxoel_doesntAddNonVoxelGameObject() {
        GameObject voxel = VoxelUtil.createVoxel(new Vector3f(), underTest);
        voxel.removeComponent(voxel.getComponent(Voxel.class).get());
        assertThrows(IllegalStateException.class, () -> underTest.addVoxel(voxel));
    }

    @Test
    public void testAddVoxel_addsVoxel() {
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(), underTest);
        underTest.addVoxel(expected);
        Optional<GameObject> voxelAt = underTest.getVoxelAt(new Vector3f());
        assertThat(voxelAt.isPresent(), is(true));
        assertThat(voxelAt.get(), is(expected));
    }

    @Test
    public void testAddVoxel_addNegativeCoords() {
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(-100, -100, -100), underTest);
        underTest.addVoxel(expected);
        Optional<GameObject> voxelAt = underTest.getVoxelAt(new Vector3f(-100, -100, -100));
        assertThat(voxelAt.isPresent(), is(true));
        assertThat(voxelAt.get(), is(expected));
    }

    @Test
    public void testAddVoxel_replacesExistingVoxel() {
        GameObject replaced = VoxelUtil.createVoxel(new Vector3f(), underTest);
        underTest.addVoxel(replaced);
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(), underTest);
        underTest.addVoxel(expected);
        Optional<GameObject> voxelAt = underTest.getVoxelAt(new Vector3f());
        assertThat(voxelAt.isPresent(), is(true));
        assertThat(voxelAt.get(), is(expected));
    }
    //</editor-fold>


    //<editor-fold desc="getGroundAt">
    @Test
    public void testGetGroundAt_returnsGroundAtBelow() {
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(), underTest);
        underTest.addVoxel(expected);
        Optional<GameObject> groundAt = underTest.getGroundAt(new Vector3f(0, 1, 0), 1);
        assertThat(groundAt.isPresent(), is(true));
        assertThat(groundAt.get(), is(expected));
    }

    @Test
    public void testGetGroundAt_returnsGroundAtBelowHighMax() {
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(), underTest);
        underTest.addVoxel(expected);
        Optional<GameObject> groundAt = underTest.getGroundAt(new Vector3f(0, 20, 0), 20);
        assertThat(groundAt.isPresent(), is(true));
        assertThat(groundAt.get(), is(expected));
    }

    @Test
    public void testGetGroundAt_returnsGroundAtAbove() {
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(), underTest);
        VoxelUtil.createVoxel(new Vector3f(0, 1, 0).mul(-1), underTest);
        underTest.addVoxel(expected);
        Optional<GameObject> groundAt = underTest.getGroundAt(new Vector3f(0, 1, 0).mul(-1), 1);
        assertThat(groundAt.isPresent(), is(true));
        assertThat(groundAt.get(), is(expected));
    }

    @Test
    public void testGetGroundAt_returnsGroundAtAboveHighMax() {
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(), underTest);
        for (int i = -1; i > -21; i--) {
            VoxelUtil.createVoxel(new Vector3f(0, i, 0), underTest);
        }
        underTest.addVoxel(expected);
        Optional<GameObject> groundAt = underTest.getGroundAt(new Vector3f(0, 20, 0).mul(-1), 20);
        assertThat(groundAt.isPresent(), is(true));
        assertThat(groundAt.get(), is(expected));
    }

    @Test
    public void testGetGroundAt_returnsGroundAtAboveGroundToHeigh() {
        GameObject expected = VoxelUtil.createVoxel(new Vector3f(), underTest);
        for (int i = -1; i > -21; i--) {
            VoxelUtil.createVoxel(new Vector3f(0, i, 0), underTest);
        }
        underTest.addVoxel(expected);
        Optional<GameObject> groundAt = underTest.getGroundAt(new Vector3f(0, 20, 0).mul(-1), 5);
        assertThat(groundAt.isPresent(), is(true));
        assertThat(groundAt.get(), is(expected));
    }

    @Test
    public void testGetGroundAt_groundNotFound() {
        Optional<GameObject> groundAt = underTest.getGroundAt(new Vector3f(0, 0, 0).mul(-1), 20);
        assertThat(groundAt.isPresent(), is(false));
    }
    //</editor-fold>

    @Test
    public void testClear() {
        underTest.addVoxel(VoxelUtil.createVoxel(new Vector3f(), underTest));
        underTest.clear();
        Optional<GameObject> voxelAt = underTest.getVoxelAt(new Vector3f());
        assertThat(voxelAt.isPresent(), is(false));
    }
}