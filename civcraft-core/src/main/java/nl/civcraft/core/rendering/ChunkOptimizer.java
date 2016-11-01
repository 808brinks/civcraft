package nl.civcraft.core.rendering;

import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import jme3tools.optimize.GeometryBatchFactory;
import nl.civcraft.core.gamecomponents.VoxelRenderer;
import nl.civcraft.core.model.*;
import nl.civcraft.core.utils.BlockUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Bob on 30-10-2016.
 * <p>
 * This is probably not worth documenting
 */
@Component
public class ChunkOptimizer {

    public ChunkOptimizerThread optimizeChunk(Chunk chunk) {
        return new ChunkOptimizerThread(chunk);
    }

    public static class ChunkOptimizerThread implements Callable<ChunkOptimizerThread> {

        private final Chunk chunk;
        private final Set<VoxelFace> renderedFaces;
        private final List<Geometry> geometries;

        public ChunkOptimizerThread(Chunk chunk) {
            this.chunk = chunk;
            renderedFaces = new HashSet<>();
            geometries = new ArrayList<>();
        }

        public Chunk getChunk() {
            return chunk;
        }

        public Set<VoxelFace> getRenderedFaces() {
            return renderedFaces;
        }

        @Override
        public ChunkOptimizerThread call() throws Exception {
            Voxel[] voxels = chunk.getVoxels();
            //Copied from https://raw.githubusercontent.com/roboleary/GreedyMesh/master/src/mygame/Main.java
            /*
         * These are just working variables for the algorithm - almost all taken
         * directly from Mikola Lysenko's javascript implementation.
         */
            int i, j, k, l, w, h, u, v, n;
            Face side = null;

            final int[] x = new int[]{0, 0, 0};
            final int[] q = new int[]{0, 0, 0};
            final int[] du = new int[]{0, 0, 0};
            final int[] dv = new int[]{0, 0, 0};

        /*
         * We create a mask - this will contain the groups of matching voxel faces
         * as we proceed through the chunk in 6 directions - once for each face.
         */
            final VoxelFace[] mask = new VoxelFace[World.CHUNK_SIZE * World.CHUNK_SIZE];

        /*
         * These are just working variables to hold two faces during comparison.
         */
            VoxelFace voxelFace, voxelFace1;

            /**
             * We start with the lesser-spotted boolean for-loop (also known as the old flippy floppy).
             *
             * The variable backFace will be TRUE on the first iteration and FALSE on the second - this allows
             * us to track which direction the indices should run during creation of the quad.
             *
             * This loop runs twice, and the inner loop 3 times - totally 6 iterations - one for each
             * voxel face.
             */
            for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {

            /*
             * We sweep over the 3 dimensions - most of what follows is well described by Mikola Lysenko
             * in his post - and is ported from his Javascript implementation.  Where this implementation
             * diverges, I've added commentary.
             */
                for (int d = 0; d < 3; d++) {

                    u = (d + 1) % 3;
                    v = (d + 2) % 3;

                    x[0] = 0;
                    x[1] = 0;
                    x[2] = 0;

                    q[0] = 0;
                    q[1] = 0;
                    q[2] = 0;
                    q[d] = 1;

                /*
                 * Here we're keeping track of the side that we're meshing.
                 */
                    if (d == 0) {
                        side = backFace ? Face.LEFT : Face.RIGHT;
                    } else if (d == 1) {
                        side = backFace ? Face.BOTTOM : Face.TOP;
                    } else if (d == 2) {
                        side = backFace ? Face.FRONT : Face.BACK;
                    }

                /*
                 * We move through the dimension from front to back
                 */
                    for (x[d] = -1; x[d] < World.CHUNK_SIZE; ) {

                    /*
                     * -------------------------------------------------------------------
                     *   We compute the mask
                     * -------------------------------------------------------------------
                     */
                        n = 0;

                        for (x[v] = 0; x[v] < World.CHUNK_SIZE; x[v]++) {

                            for (x[u] = 0; x[u] < World.CHUNK_SIZE; x[u]++) {

                            /*
                             * Here we retrieve two voxel faces for comparison.
                             */
                                voxelFace = (x[d] >= 0) ? getVoxelFace(voxels, x[0], x[1], x[2], side) : null;
                                voxelFace1 = (x[d] < World.CHUNK_SIZE - 1) ? getVoxelFace(voxels, x[0] + q[0], x[1] + q[1], x[2] + q[2], side) : null;

                            /*
                             * Note that we're using the equals function in the voxel face class here, which lets the faces
                             * be compared based on any number of attributes.
                             *
                             * Also, we choose the face to add to the mask depending on whether we're moving through on a backface or not.
                             */
                                mask[n++] = ((voxelFace != null && voxelFace1 != null && voxelFace.canMerge(voxelFace1)))
                                        ? null
                                        : backFace ? voxelFace1 : voxelFace;
                            }
                        }

                        x[d]++;

                    /*
                     * Now we generate the mesh for the mask
                     */
                        n = 0;

                        for (j = 0; j < World.CHUNK_SIZE; j++) {

                            for (i = 0; i < World.CHUNK_SIZE; ) {

                                if (mask[n] != null) {

                                /*
                                 * We compute the width
                                 */
                                    for (w = 1; i + w < World.CHUNK_SIZE && mask[n + w] != null && mask[n + w].canMerge(mask[n]); w++) {
                                    }

                                /*
                                 * Then we compute height
                                 */
                                    boolean done = false;

                                    for (h = 1; j + h < World.CHUNK_SIZE; h++) {

                                        for (k = 0; k < w; k++) {

                                            if (mask[n + k + h * World.CHUNK_SIZE] == null || !mask[n + k + h * World.CHUNK_SIZE].canMerge(mask[n])) {
                                                done = true;
                                                break;
                                            }
                                        }

                                        if (done) {
                                            break;
                                        }
                                    }

                                /*
                                 * Here we check the "transparent" attribute in the VoxelFace class to ensure that we don't mesh
                                 * any culled faces.
                                 */
                                    if (mask[n].isVisible()) {
                                    /*
                                     * Add quad
                                     */
                                        x[u] = i;
                                        x[v] = j;

                                        du[0] = 0;
                                        du[1] = 0;
                                        du[2] = 0;
                                        du[u] = w;

                                        dv[0] = 0;
                                        dv[1] = 0;
                                        dv[2] = 0;
                                        dv[v] = h;

                                    /*
                                     * And here we call the quad function in order to render a merged quad in the scene.
                                     *
                                     * We pass mask[n] to the function, which is an instance of the VoxelFace class containing
                                     * all the attributes of the face - which allows for variables to be passed to shaders - for
                                     * example lighting values used to create ambient occlusion.
                                     */
                                        quad(new Vector3f(x[0], x[1], x[2]),
                                                new Vector3f(x[0] + du[0], x[1] + du[1], x[2] + du[2]),
                                                new Vector3f(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2]),
                                                new Vector3f(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]),
                                                w,
                                                h,
                                                mask[n],
                                                backFace, side);
                                    }

                                /*
                                 * We zero out the mask
                                 */
                                    for (l = 0; l < h; ++l) {

                                        for (k = 0; k < w; ++k) {
                                            mask[n + k + l * World.CHUNK_SIZE] = null;
                                        }
                                    }

                                /*
                                 * And then finally increment the counters and continue
                                 */
                                    i += w;
                                    n += w;

                                } else {

                                    i++;
                                    n++;
                                }
                            }
                        }
                    }
                }
            }
            List<Geometry> batchedGeometries = GeometryBatchFactory.makeBatches(geometries);
            geometries.clear();
            geometries.addAll(batchedGeometries);
            return this;
        }

        private VoxelFace getVoxelFace(Voxel[] voxels, int x, int y, int z, Face face) {
            int arrayIndex = (World.CHUNK_SIZE * World.CHUNK_SIZE * z) + (World.CHUNK_SIZE * y) + x;
            if (arrayIndex < 0 || arrayIndex >= voxels.length) {
                return null;
            }

            Voxel voxel = voxels[arrayIndex];
            if (voxel == null || !voxel.isVisible()) {
                return null;
            }
            return voxel.getGameObject().getComponent(VoxelRenderer.class).get().getFaces().get(face);
        }

        /**
         * This function renders a single quad in the scene. This quad may represent many adjacent voxel
         * faces - so in order to create the illusion of many faces, you might consider using a tiling
         * function in your voxel shader. For this reason I've included the quad width and height as parameters.
         * <p>
         * For example, if your texture coordinates for a single voxel face were 0 - 1 on a given axis, they should now
         * be 0 - width or 0 - height. Then you can calculate the correct texture coordinate in your fragement
         * shader using coord.xy = fract(coord.xy).
         *
         * @param bottomLeft
         * @param topLeft
         * @param topRight
         * @param bottomRight
         * @param width
         * @param height
         * @param voxel
         * @param backFace
         */
        void quad(final Vector3f bottomLeft,
                  final Vector3f topLeft,
                  final Vector3f topRight,
                  final Vector3f bottomRight,
                  final int width,
                  final int height,
                  final VoxelFace voxel,
                  final boolean backFace,
                  final Face side) {

            final Vector3f[] vertices = new Vector3f[4];

            vertices[2] = topLeft.multLocal(BlockUtil.BLOCK_SIZE * 2);
            vertices[3] = topRight.multLocal(BlockUtil.BLOCK_SIZE * 2);
            vertices[0] = bottomLeft.multLocal(BlockUtil.BLOCK_SIZE * 2);
            vertices[1] = bottomRight.multLocal(BlockUtil.BLOCK_SIZE * 2);

            final Vector2f[] texCoord = new Vector2f[4];
            if (side.equals(Face.LEFT) || side.equals(Face.RIGHT)) {
                texCoord[0] = new Vector2f(1, 0);
                texCoord[1] = new Vector2f(0, 0);
                texCoord[2] = new Vector2f(1, 1);
                texCoord[3] = new Vector2f(0, 1);
            } else {
                texCoord[0] = new Vector2f(1, 0);
                texCoord[1] = new Vector2f(1, 1);
                texCoord[2] = new Vector2f(0, 0);
                texCoord[3] = new Vector2f(0, 1);
            }


            final int[] indexes = backFace ? new int[]{2, 0, 1, 1, 3, 2} : new int[]{2, 3, 1, 1, 0, 2};

            Mesh mesh = new Mesh();

            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
            mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexes));
            if (side.equals(Face.LEFT) || side.equals(Face.RIGHT)) {
                mesh.scaleTextureCoordinates(new Vector2f(height, width));
            } else {
                mesh.scaleTextureCoordinates(new Vector2f(width, height));
            }
            mesh.updateBound();

            Geometry geo = new Geometry("MergedFaces", mesh);
            Material material = voxel.getMaterial().clone();
            for (MatParam matParam : material.getParams()) {
                MatParamTexture textureParam = material.getTextureParam(matParam.getName());
                if (textureParam != null) {
                    textureParam.getTextureValue().setWrap(Texture.WrapMode.Repeat);
                }
            }

            geo.setMaterial(material);
            //geo.getMaterial().getAdditionalRenderState().setWireframe(true);
            geometries.add(geo);
        }

        public List<Geometry> getGeometries() {
            return geometries;
        }
    }
}
