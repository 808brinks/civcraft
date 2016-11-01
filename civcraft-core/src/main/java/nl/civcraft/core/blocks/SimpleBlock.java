package nl.civcraft.core.blocks;

import nl.civcraft.core.gamecomponents.StaticVoxelRenderer;
import nl.civcraft.core.model.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

/**
 * Created by Bob on 14-10-2016.
 * <p>
 * This is probably not worth documenting
 */
public abstract class SimpleBlock implements VoxelProducer {
    private final ApplicationEventPublisher publisher;

    protected SimpleBlock(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public GameObject produce(int x, int y, int z) {
        StaticVoxelRenderer staticVoxelRenderer = new StaticVoxelRenderer(block());
        GameObject gameObject = new GameObject();
        Voxel voxel = new Voxel(x, y, z, blockName(), publisher);
        gameObject.addComponent(voxel);
        gameObject.addComponent(staticVoxelRenderer);
        return gameObject;
    }

    protected abstract Map<Face, VoxelFace> block();

}
