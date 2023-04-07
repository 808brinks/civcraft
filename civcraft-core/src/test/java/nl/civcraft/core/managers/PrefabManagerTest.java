package nl.civcraft.core.managers;

import io.reactivex.Observable;
import nl.civcraft.core.gamecomponents.GameComponent;
import nl.civcraft.core.model.GameObject;
import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Created by Bob on 14-10-20017.
 * <p>
 * This is probably not worth documenting
 */
@ExtendWith(MockitoExtension.class)
public class PrefabManagerTest {

    private PrefabManager underTest;

    @BeforeEach
    public void setUp() throws Exception {
        underTest = new PrefabManager();
    }

    @Test
    public void testCreateObject_DoesntSubscribeToParent() throws ExecutionException, InterruptedException {
        PrefabManager child = new PrefabManager(underTest);
        CompletableFuture<List<GameObject>> future = new CompletableFuture<>();
        Observable<List<GameObject>> buffer = child.getGameObjectCreated().buffer(200, TimeUnit.MILLISECONDS);
        buffer.subscribe(future::complete);
        child.build(new Matrix4f(), true);
        assertThat(future.get(), hasSize(1));

    }

    @Test
    public void testCreateObject_DoesntRecieveDoubleFromChildren() throws ExecutionException, InterruptedException {
        PrefabManager child = new PrefabManager(underTest);
        CompletableFuture<List<GameObject>> future = new CompletableFuture<>();
        Observable<List<GameObject>> buffer = underTest.getGameObjectCreated().buffer(200, TimeUnit.MILLISECONDS);
        buffer.subscribe(future::complete);
        child.build(new Matrix4f(), true);
        assertThat(future.get(), hasSize(1));
    }

    @Test
    public void testCreateObject_DoesntRecievePartialGameObjectsFromParent() throws ExecutionException, InterruptedException {
        PrefabManager child = new PrefabManager(underTest);
        child.registerComponent(new GameComponent.GameComponentFactory() {
            @Override
            public GameComponent build() {
                return new GameComponent() {
                    @Override
                    public void addTo(GameObject gameObject) {

                    }

                    @Override
                    public GameObject getGameObject() {
                        return null;
                    }

                    @Override
                    public void destroyed() {

                    }

                    @Override
                    public void changed() {

                    }

                    @Override
                    public void removeFrom(GameObject gameObject) {

                    }
                };
            }

            @Override
            public Class getComponentType() {
                return GameComponent.class;
            }
        });
        CompletableFuture<List<GameObject>> future = new CompletableFuture<>();
        Observable<List<GameObject>> buffer = underTest.getGameObjectCreated().buffer(200, TimeUnit.MILLISECONDS);
        buffer.subscribe(future::complete);
        child.build(new Matrix4f(), true);
        List<GameObject> createdObjects = future.get();
        assertThat(createdObjects, hasSize(1));
        // 2x ManagedObject and the test component
        assertThat(createdObjects.get(0).getComponents(), hasSize(3));
    }

    @Test
    public void testCreateObject_DoesntRecievePartialGameObjectsFromParent_GrandChild() throws ExecutionException, InterruptedException {
        PrefabManager child = new PrefabManager(underTest);
        PrefabManager grandChild = new PrefabManager(child);
        grandChild.registerComponent(new GameComponent.GameComponentFactory() {
            @Override
            public GameComponent build() {
                return new GameComponent() {
                    @Override
                    public void addTo(GameObject gameObject) {

                    }

                    @Override
                    public GameObject getGameObject() {
                        return null;
                    }

                    @Override
                    public void destroyed() {

                    }

                    @Override
                    public void changed() {

                    }

                    @Override
                    public void removeFrom(GameObject gameObject) {

                    }
                };
            }

            @Override
            public Class getComponentType() {
                return GameComponent.class;
            }
        });
        CompletableFuture<List<GameObject>> future = new CompletableFuture<>();
        Observable<List<GameObject>> buffer = underTest.getGameObjectCreated().buffer(200, TimeUnit.MILLISECONDS);
        buffer.subscribe(future::complete);
        grandChild.build(new Matrix4f(), true);
        List<GameObject> createdObjects = future.get();
        assertThat(createdObjects, hasSize(1));
        // 2x ManagedObject and the test component
        assertThat(createdObjects.get(0).getComponents(), hasSize(4));
    }


}