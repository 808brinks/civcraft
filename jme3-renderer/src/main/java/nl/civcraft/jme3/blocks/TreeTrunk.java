package nl.civcraft.jme3.blocks;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import nl.civcraft.core.model.Face;
import nl.civcraft.jme3.model.VoxelFace;
import nl.civcraft.jme3.utils.BlockUtil;
import nl.civcraft.jme3.utils.MaterialUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Bob on 14-10-2016.
 * <p>
 * This is probably not worth documenting
 */
@Component
public class TreeTrunk extends SimpleBlock {
    public static final String BLOCK_NAME = "treeTrunk";
    private final Material treeTrunkTopMaterial;
    private final Material treeTrunkSideMaterial;

    @Autowired
    public TreeTrunk(AssetManager assetManager, ApplicationEventPublisher publisher) {
        super(publisher);
        treeTrunkTopMaterial = MaterialUtil.getUnshadedMaterial(assetManager, "textures/log_oak_top.png");
        treeTrunkSideMaterial = MaterialUtil.getUnshadedMaterial(assetManager, "textures/log_oak.png");
    }

    @Override
    protected Map<Face, VoxelFace> block() {
        return BlockUtil.getQuadBlock(treeTrunkTopMaterial, treeTrunkSideMaterial, treeTrunkTopMaterial);
    }

    @Override
    public String blockName() {
        return BLOCK_NAME;
    }
}
