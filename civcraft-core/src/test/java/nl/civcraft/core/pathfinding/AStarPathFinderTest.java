package nl.civcraft.core.pathfinding;

import nl.civcraft.core.managers.VoxelManager;
import nl.civcraft.core.model.GameObject;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Queue;

import static nl.civcraft.test.util.VoxelUtil.createVoxel;
import static nl.civcraft.test.util.VoxelUtil.createVoxels;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Bob on 19-2-2017.
 * <p>
 * This is probably not worth documenting
 */
@ExtendWith(MockitoExtension.class)
public class AStarPathFinderTest {

    private AStarPathFinder underTest;
    @Mock
    private GameObject testCivvy;
    @Mock
    private PathFindingTarget target;
    private VoxelManager voxelManager;

    @BeforeEach
    public void setUp() throws Exception {
        voxelManager = new VoxelManager();
        underTest = new AStarPathFinder();

        when(target.getMaxSearchArea(any())).thenReturn(500);
    }

    @Test
    public void testFindPath_targetEqualsStart() {
        GameObject start = new GameObject();
        when(target.isReached(eq(testCivvy), any(AStarNode.class))).thenAnswer(invocation -> ((AStarNode) invocation.getArgument(1)).getGameObject().equals(start));
        Optional<Queue<GameObject>> path = underTest.findPath(testCivvy, start, target);
        assertThat(path.isPresent(), equalTo(true));
        assertThat(path.get(), hasSize(1));
    }

    @Test
    public void testFindPath_directNeighbour() {
        GameObject start = createVoxel(new Vector3f(), voxelManager);
        GameObject targetVoxel = createVoxel(new Vector3f(1, 0, 0), voxelManager);
        target = new MoveToVoxelTarget(targetVoxel);
        Optional<Queue<GameObject>> path = underTest.findPath(testCivvy, start, target);
        assertThat(path.isPresent(), equalTo(true));
        assertThat(path.get(), hasSize(2));
        assertThat(path.get().poll(), is(start));
        assertThat(path.get().poll(), is(targetVoxel));
    }

    @Test
    public void testFindPath_flat3x3WithHole() {
        createVoxels(new boolean[][][]
                {
                        {
                                {true, true, true},
                                {true, false, false},
                                {true, true, true}
                        }
                }, voxelManager);
        GameObject start = voxelManager.getVoxelAt(0, 0, 0).get();
        GameObject targetVoxel = voxelManager.getVoxelAt(2, 0, 2).get();
        this.target = new MoveToVoxelTarget(targetVoxel);
        Optional<Queue<GameObject>> path = underTest.findPath(testCivvy, start, this.target);
        assertThat(path.isPresent(), equalTo(true));
        assertThat(path.get(), hasSize(5));
        assertThat(path.get().poll(), is(start));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(0, 0, 1).get()));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(0, 0, 2).get()));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(1, 0, 2).get()));
        assertThat(path.get().poll(), is(targetVoxel));
    }

    @Test
    public void testFindPath_3x3WithPartialWall() {
        createVoxels(new boolean[][][]
                {
                        {
                                {true, true, true},
                                {true, true, true},
                                {true, true, true}
                        },
                        {
                                {false, false, false},
                                {true, true, false},
                                {false, false, false}
                        },
                        {
                                {false, false, false},
                                {true, true, false},
                                {false, false, false}
                        }
                }, voxelManager);
        GameObject start = voxelManager.getVoxelAt(0, 0, 0).get();
        GameObject targetVoxel = voxelManager.getVoxelAt(2, 0, 2).get();
        this.target = new MoveToVoxelTarget(targetVoxel);
        Optional<Queue<GameObject>> path = underTest.findPath(testCivvy, start, this.target);
        assertThat(path.isPresent(), equalTo(true));
        assertThat(path.get(), hasSize(5));
        assertThat(path.get().poll(), is(start));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(1, 0, 0).get()));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(2, 0, 0).get()));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(2, 0, 1).get()));
        assertThat(path.get().poll(), is(targetVoxel));
    }

    @Test
    public void testFindPath_3x3WithFullWall() {
        createVoxels(new boolean[][][]
                {
                        {
                                {true, true, true},
                                {true, true, true},
                                {true, true, true}
                        },
                        {
                                {false, false, false},
                                {true, true, true},
                                {false, false, false}
                        },
                        {
                                {false, false, false},
                                {true, true, true},
                                {false, false, false}
                        }
                }, voxelManager);
        GameObject start = voxelManager.getVoxelAt(0, 0, 0).get();
        GameObject targetVoxel = voxelManager.getVoxelAt(2, 0, 2).get();
        this.target = new MoveToVoxelTarget(targetVoxel);
        Optional<Queue<GameObject>> path = underTest.findPath(testCivvy, start, this.target);
        assertThat(path.isPresent(), equalTo(false));
    }

    @Test
    public void testFindPath_3x3WithHill() {
        createVoxels(new boolean[][][]
                {
                        {
                                {true, true, true},
                                {true, true, true},
                                {true, true, true}
                        },
                        {
                                {false, false, false},
                                {true, true, true},
                                {false, false, false}
                        }
                }, voxelManager);
        GameObject start = voxelManager.getVoxelAt(0, 0, 0).get();
        GameObject targetVoxel = voxelManager.getVoxelAt(2, 0, 2).get();
        this.target = new MoveToVoxelTarget(targetVoxel);
        Optional<Queue<GameObject>> path = underTest.findPath(testCivvy, start, this.target);
        assertThat(path.isPresent(), equalTo(true));
        assertThat(path.get(), hasSize(5));
        assertThat(path.get().poll(), is(start));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(1, 0, 0).get()));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(2, 0, 0).get()));
        assertThat(path.get().poll(), is(voxelManager.getVoxelAt(2, 1, 1).get()));
        assertThat(path.get().poll(), is(targetVoxel));
    }

}