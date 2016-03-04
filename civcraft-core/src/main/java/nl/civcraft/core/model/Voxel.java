package nl.civcraft.core.model;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Bob on 25-11-2015.
 * <p>
 * This is probably not worth documenting
 */
public class Voxel {

    private final String type;
    private final int x;
    private final int y;
    private final int z;
    private final Block block;
    private final List<Voxel> neighbours;
    private Chunk chunk;
    private int localX;
    private int localY;
    private int localZ;
    public Voxel(int x, int y, int z, String type, Block block) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;

        this.block = block;

        neighbours = new ArrayList<>();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Block cloneBlock() {
        return (Block) block.clone();
    }

    public void breakBlock() {
        chunk.removeVoxel(this);
    }

    public boolean isVisible() {
        return neighbours.size() != 6;
    }

    private void addNeighbour(Voxel voxel) {
        if (!neighbours.contains(voxel)) {
            neighbours.add(voxel);
            voxel.addNeighbour(this);
        }
    }

    private void removeNeighbour(Voxel voxel) {
        if (neighbours.contains(voxel)) {
            voxel.removeNeighbour(voxel);
            neighbours.remove(voxel);
        }
    }

    public void remove() {
        for (Voxel neighbour : neighbours) {
            neighbour.removeNeighbour(this);
        }
    }

    public void addNeighbours(List<Voxel> voxelNeighbours) {
        voxelNeighbours.forEach(this::addNeighbour);
    }

    public List<Voxel> getNeighbours() {
        return neighbours;
    }

    public Vector3f findLocalChunkTranslation() {
        return new Vector3f(x, y, z).subtract(new Vector3f(chunk.getChunkX() * World.CHUNK_SIZE, 0, chunk.getChunkZ() * World.CHUNK_SIZE));
    }

    @Override
    public String toString() {
        return type + "@" + x + "x" + y + "x" + z + " local " + localX + "x" + localY + "x" + localZ;
    }

    public int getLocalX() {
        return localX;
    }

    public void setLocalX(int localX) {
        this.localX = localX;
    }

    public int getLocalY() {
        return localY;
    }

    public void setLocalY(int localY) {
        this.localY = localY;
    }

    public int getLocalZ() {
        return localZ;
    }

    public void setLocalZ(int localZ) {
        this.localZ = localZ;
    }

    public List<Voxel> getEnterableNeighbours() {
        return neighbours.stream().filter(v -> v.getNeighbour(Face.TOP) == null).collect(Collectors.toList());
    }

    public Voxel getNeighbour(Face face) {
        List<Voxel> collect = neighbours.stream().filter(v -> v.getLocation().equals(face.getTranslation().add(getLocation()))).limit(1).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            return collect.get(0);
        }
        return null;
    }

    public Vector3f getLocation() {
        return new Vector3f(x, y, z);
    }

    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("voxel[");
        stringBuilder.append(x);
        stringBuilder.append(",");
        stringBuilder.append(y);
        stringBuilder.append(",");
        stringBuilder.append(z);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
