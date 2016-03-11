package nl.civcraft.core.conf;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import nl.civcraft.core.managers.BlockManager;
import nl.civcraft.core.managers.BlockOptimizer;
import nl.civcraft.core.managers.QuadBlockOptimizer;
import nl.civcraft.core.managers.SingleGeometryBoxBlockOptimizer;
import nl.civcraft.core.model.Block;
import nl.civcraft.core.model.Face;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockConfiguration {

    private static final float BLOCK_SIZE = 0.5f;
    @Autowired
    private AssetManager assetManager;

    @Bean
    public BlockManager voxelGeometryManager() {
        return new BlockManager();
    }

    @Bean
    public Block dirt(@Qualifier("singleGeometryBoxBlockOptimizer") BlockOptimizer blockOptimizer, Material dirtMaterial) {
        return createSingleGeometryBoxBlock(dirtMaterial, "dirt", blockOptimizer);
    }

    private Block createSingleGeometryBoxBlock(Material cobbleMaterial, String name, BlockOptimizer blockOptimizer) {
        Box box = new Box(BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        Geometry geometry = new Geometry("box", box);
        geometry.setMaterial(cobbleMaterial);
        geometry.setLocalTranslation(0.5f, 0.5f, 0.5f);
        Block block = new Block(name, blockOptimizer);
        block.attachChild(geometry);
        return block;
    }

    @Bean
    public Block cobbleStone(@Qualifier("singleGeometryBoxBlockOptimizer") BlockOptimizer blockOptimizer, Material cobbleMaterial) {

        return createSingleGeometryBoxBlock(cobbleMaterial, "cobble", blockOptimizer);
    }


    @Bean
    public Block grass(@Qualifier("quadBlockOptimizer") BlockOptimizer blockOptimizer, Material dirtMaterial, Material grassTopMaterial) {



        Block block = new Block("grass", blockOptimizer);

        Geometry topGeometry = getBlockQuadGeometry(Face.TOP);
        topGeometry.setMaterial(grassTopMaterial);
        block.attachChild(topGeometry);

        Geometry bottomGeometry = getBlockQuadGeometry(Face.BOTTOM);
        bottomGeometry.setMaterial(dirtMaterial);
        block.attachChild(bottomGeometry);

        Geometry leftGeometry = getBlockQuadGeometry(Face.LEFT);
        leftGeometry.setMaterial(dirtMaterial);
        block.attachChild(leftGeometry);

        Geometry rightGeometry = getBlockQuadGeometry(Face.RIGHT);
        rightGeometry.setMaterial(dirtMaterial);
        block.attachChild(rightGeometry);

        Geometry frontGeometry = getBlockQuadGeometry(Face.FRONT);
        frontGeometry.setMaterial(dirtMaterial);
        block.attachChild(frontGeometry);

        Geometry backGeometry = getBlockQuadGeometry(Face.BACK);
        backGeometry.setMaterial(dirtMaterial);
        block.attachChild(backGeometry);

        return block;
    }

    private Geometry getBlockQuadGeometry(Face face) {
        Quad quad = new Quad(BLOCK_SIZE * 2, BLOCK_SIZE * 2);
        String name = null;
        Vector3f translation = null;
        float[] rotation = null;
        switch (face) {
            case TOP:
                name = "top";
                translation = new Vector3f(0f, 1f, 1f);
                rotation = new float[]{-90F * FastMath.DEG_TO_RAD, 0F, 0F};
                break;
            case BOTTOM:
                name = "bottom";
                translation = new Vector3f(0f, 0f, 0f);
                rotation = new float[]{90F * FastMath.DEG_TO_RAD, 0F, 0F};
                break;
            case LEFT:
                name = "left";
                translation = new Vector3f(1f,0F, 0f);
                rotation = new float[]{0F, 180F * FastMath.DEG_TO_RAD, 0F };
                break;
            case RIGHT:
                name = "right";
                translation = new Vector3f(0f, 0F, 1f);
                rotation = new float[]{0F, 0F, 0F };
                break;
            case FRONT:
                name = "front";
                translation = new Vector3f(0f, 0F, 0f);
                rotation = new float[]{0F, 270F * FastMath.DEG_TO_RAD, 0F };
                break;
            case BACK:
                name = "back";
                translation = new Vector3f(1f, 0F, 1f);
                rotation = new float[]{0F, 90F * FastMath.DEG_TO_RAD, 0F };
                break;
            default:
                break;
        }
        Geometry geometry = new Geometry(name, quad);
        Quaternion quaternion = new Quaternion(rotation);
        geometry.setLocalRotation(quaternion);
        geometry.setLocalTranslation(translation);

        return geometry;
    }

    @Bean
    public BlockOptimizer singleGeometryBoxBlockOptimizer() {
        return new SingleGeometryBoxBlockOptimizer();
    }

    @Bean
    public BlockOptimizer quadBlockOptimizer() {
        return new QuadBlockOptimizer();
    }
}
