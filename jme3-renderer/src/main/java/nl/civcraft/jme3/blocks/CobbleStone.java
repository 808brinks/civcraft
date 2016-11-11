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
public class CobbleStone extends SimpleBlock {
    public static final String BLOCK_NAME = "cobbleStone";
    private final Material cobbleMaterial;

    @Autowired
    public CobbleStone(AssetManager assetManager, ApplicationEventPublisher publisher) {
        super(publisher);
        cobbleMaterial = MaterialUtil.getUnshadedMaterial(assetManager, "textures/bdc_cobblestone01.png");
    }

    @Override
    protected Map<Face, VoxelFace> block() {
        return BlockUtil.getQuadBlock(cobbleMaterial, cobbleMaterial, cobbleMaterial);
    }

    @Override
    public String blockName() {
        return BLOCK_NAME;
    }
}
