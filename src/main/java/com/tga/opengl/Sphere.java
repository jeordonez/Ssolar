
package com.tga.opengl;

import java.util.ArrayList;
import org.joml.Vector3f;
public class Sphere {

        private ArrayList<Float> vertices = new ArrayList<Float>();
        private ArrayList<Float> normals = new ArrayList<Float>();
        private ArrayList<Float> texCoords = new ArrayList<Float>();
        private ArrayList<Integer> indices = new ArrayList<Integer>();
        private ArrayList<ArrayList<Integer>> grid = new ArrayList<ArrayList<Integer>>();

    public Sphere(){  
    }
    
    public void create(){
    
        int ix, iy;
        int idx = 0;
        Vector3f vertex = new Vector3f();
        Vector3f normal = new Vector3f();
        
        // Generate vertices, normals and texCoords
        
        
        float height = 25;
        
        //valor para heightSegments
        float heightSegments = 25;
        
        float width = 25;
        float radius = 0.8f;
        
        for(iy = 0; iy <= height; iy++){
            
            ArrayList<Integer> verticesRow = new ArrayList<Integer>();
            
            float v = iy / heightSegments;
            
            for(ix=0; ix<= width; ix++){
                float u = ix/width;
                
                // Vertex 
                vertex.x = (float) (-radius * Math.cos( u * Math.PI * 2.0 ) * Math.sin( v * Math.PI )); 
                vertex.y = (float) (radius * Math.cos( v * Math.PI )); 
                vertex.z = (float) (radius * Math.sin( u * Math.PI * 2.0 ) * Math.sin( v * Math.PI )); 
                vertices.add(vertex.x);
                vertices.add(vertex.y);
                vertices.add(vertex.z); 
                
                 
                 //normal = vertex.clone();  no encuentro hay metodo clone()
                normal = vertex;
                
                normal.normalize(); 
                normals.add(normal.x);
                normals.add(normal.y);
                normals.add(normal.z); 
                
                // TexCoord 
                texCoords.add( u );
                float v1 = (float) (1.0 + v);
                texCoords.add(v1);
                verticesRow.add( idx++ );

            }
            grid.add((ArrayList<Integer>) verticesRow);
        }

        for ( iy = 0; iy < height; iy++ )
        {
            for ( ix = 0; ix < width; ix++ )
            {
                
                int a = grid.get(iy).get(ix + 1);
                int b = grid.get(iy).get(ix);
                int c = grid.get(iy + 1).get(ix);
                int d = grid.get(iy + 1).get(ix + 1);
                indices.add(a);
                indices.add(b);
                indices.add(d);
                indices.add(b);
                indices.add(c);
                indices.add(d); 
            }
        }
    }  

    /**
     * @return the vertices
     */
    public float[] getVertices() {
        float[] floatArrayV = new float[vertices.size()];
        int i = 0;

        for (Float f : vertices) {
            floatArrayV[i++] = f; // Or whatever default you want.
        }
        return floatArrayV;
    }

    /**
     * @return the normals
     */
    public float[] getNormals() {
        float[] floatArrayN = new float[normals.size()];
        int i = 0;

        for (Float f : normals) {
            floatArrayN[i++] = f; // Or whatever default you want.
        }
        return floatArrayN;
    }

    /**
     * @return the texCoords
     */
    public float[] getTexCoords() {
        float[] floatArrayGC = new float[texCoords.size()];
        int i = 0;

        for (Float f : texCoords) {
            floatArrayGC[i++] = f; // Or whatever default you want.
        }
        return floatArrayGC;
    }

    /**
     * @return the indices
     */
    public int[] getIndices() {
    int[] IntArrayI = new int[indices.size()];
        int i = 0;

        for (Integer f : indices) {
            IntArrayI[i++] = f; // Or whatever default you want.
        }
        return IntArrayI;
    }
    
} 
    
    

