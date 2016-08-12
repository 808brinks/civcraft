package nl.civcraft.core.interaction.selectors;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import nl.civcraft.core.interaction.MouseTool;
import nl.civcraft.core.interaction.util.CurrentVoxelHighlighter;
import nl.civcraft.core.managers.WorldManager;
import nl.civcraft.core.model.Voxel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Bob on 12-8-2016.
 * <p>
 * This is probably not worth documenting
 */
@Component
public class GroundRectangleSelector implements MouseTool {
    private final CurrentVoxelHighlighter currentVoxelHighlighter;
    private final Node selectionBoxes;
    private final Spatial hoverSpatial;
    private final WorldManager worldManager;
    private Voxel currentVoxel;
    private Voxel startingVoxel;

    @Autowired
    public GroundRectangleSelector(CurrentVoxelHighlighter currentVoxelHighlighter, Node selectionBoxes, Spatial hoverSpatial, WorldManager worldManager) {
        this.currentVoxelHighlighter = currentVoxelHighlighter;
        this.selectionBoxes = selectionBoxes;
        this.hoverSpatial = hoverSpatial;
        this.worldManager = worldManager;
    }

    @Override
    public void handleLeftClick(boolean isPressed) {
        if (isPressed) {
            if (startingVoxel == null) {
                startingVoxel = currentVoxelHighlighter.getCurrentVoxel();
            } else {
                loopThroughSelection(this::deleteBlock);
                selectionBoxes.detachAllChildren();
                startingVoxel = null;
            }
        }
    }

    @Override
    public void handleMouseMotion() {
        if (startingVoxel == null) {
            currentVoxel = currentVoxelHighlighter.highLight();
        } else {
            currentVoxelHighlighter.clear();
            currentVoxel = currentVoxelHighlighter.getCurrentVoxel();
            selectionBoxes.detachAllChildren();
            loopThroughSelection(this::addHighlight);
        }
    }

    private void loopThroughSelection(SelectionLooper selectionLooper) {
        if (startingVoxel.equals(currentVoxel)) {
            selectionLooper.handleElement(startingVoxel.getX(), startingVoxel.getZ());
            return;
        }
        int startX = startingVoxel.getX();
        int startZ = startingVoxel.getZ();
        int endX = currentVoxel.getX();
        int endZ = currentVoxel.getZ();

        if (startX > endX) {
            int tmp = startX;
            startX = endX;
            endX = tmp;
        }

        if (startZ > endZ) {
            int tmp = startZ;
            startZ = endZ;
            endZ = tmp;
        }
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                selectionLooper.handleElement(x, z);
            }
        }
    }

    private void deleteBlock(int x, int z) {
        Voxel voxelAt = worldManager.getWorld().getVoxelAt(x, startingVoxel.getY(), z);
        if (voxelAt != null) {
            voxelAt.breakBlock();
        }
    }

    private void addHighlight(int x, int z) {
        Spatial clone = hoverSpatial.clone();
        clone.setLocalTranslation(clone.getLocalTranslation().x + x, clone.getLocalTranslation().y + startingVoxel.getY(), clone.getLocalTranslation().z + z);
        selectionBoxes.attachChild(clone);
    }

    @FunctionalInterface
    private interface SelectionLooper {
        void handleElement(int x, int z);
    }
}
