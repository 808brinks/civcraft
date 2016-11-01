package nl.civcraft.core.model;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;

/**
 * Created by Bob on 30-10-2016.
 * <p>
 * This is probably not worth documenting
 */
public class VoxelFace {
    private final Geometry geometry;
    private final Material material;
    private boolean visible = true;

    public VoxelFace(Material material, Geometry geometry) {
        this.material = material;
        this.geometry = geometry;
        geometry.setMaterial(material);
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public boolean canMerge(VoxelFace voxelFace) {
        return getMaterial().equals(voxelFace.getMaterial());

    }

    public Material getMaterial() {
        return material;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
