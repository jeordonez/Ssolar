package com.tga.opengl;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Demo {

    // The window handle
    private long window;
    int VAO, VBO, VBO2, VBO3, EBO, indices;
    float angle = 5.0f;
    float angleT = -5.0f;
    float angleLunaTraslacion = 5.0f;
    float angleLunaRotacion = -5.0f;
    int textureIDSol, textureIDT, textureIDL;

    ShaderProgram shaderProgramSol;              
    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f(); 
    Matrix4f modelSol = new Matrix4f(); 
    //Matrix4f viewPos = new Matrix4f();
    
    ShaderProgram shaderProgramT;
    Matrix4f modelT = new Matrix4f(); 
    
    ShaderProgram shaderProgramL;
    Matrix4f modelL = new Matrix4f();
    
    public void run() throws Exception {
        init();
        
        Sphere esfera = new Sphere();
        esfera.create();

        FloatBuffer verticesBuffer = null;
        FloatBuffer normalBuffer = null;
        FloatBuffer texCoordBuffer = null;
        IntBuffer indicesBuffer = null;
        
        try {
        verticesBuffer = MemoryUtil.memAllocFloat(esfera.getVertices().length);
        verticesBuffer.put(esfera.getVertices()).flip();
        
        normalBuffer = MemoryUtil.memAllocFloat(esfera.getNormals().length);
        normalBuffer.put(esfera.getNormals()).flip();
        
        texCoordBuffer = MemoryUtil.memAllocFloat(esfera.getTexCoords().length);
        texCoordBuffer.put(esfera.getTexCoords()).flip();
        
        indicesBuffer = MemoryUtil.memAllocInt(esfera.getIndices().length);
        indicesBuffer.put(esfera.getIndices()).flip();
        indices = esfera.getIndices().length;
        
        VAO = glGenVertexArrays(); // Create VertexArrayObject
        VBO = glGenBuffers(); // Create VertexBufferObject
        VBO2 = glGenBuffers();
        VBO3 = glGenBuffers();
        EBO = glGenBuffers();
        
        glBindVertexArray(VAO); // Bind current VAO
        
        //position attribute
        glBindBuffer(GL_ARRAY_BUFFER, VBO); // Bind Vertex VAO
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW); // Assign buffer to VBO
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // Definition of position attribute in buffer
        glEnableVertexAttribArray(0); // Active attribute 0 on VAO
        
        //normal attribute
        glBindBuffer(GL_ARRAY_BUFFER, VBO2); // Bind Vertex VAO
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW); // Assign buffer to VBO
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // Definition of position attribute in buffer
        glEnableVertexAttribArray(1); // Active attribute 0 on VAO
        
        //textCoord attribute
        glBindBuffer(GL_ARRAY_BUFFER, VBO3); // Bind Vertex VAO
        glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_STATIC_DRAW); // Assign buffer to VBO
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0); // Definition of position attribute in buffer
        glEnableVertexAttribArray(2); // Active attribute 0 on VAO
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        
        // Unbind VBO (0 is equal to null VBO)
        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        glBindVertexArray(0); // Unbind VAO (0 is equal to null VAO)
        
        } finally {
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer); // Destroy auxiliar buffer.
            }
            if (verticesBuffer != null) {
                MemoryUtil.memFree(normalBuffer); // Destroy auxiliar buffer.
            }
            if (verticesBuffer != null) {
                MemoryUtil.memFree(texCoordBuffer); // Destroy auxiliar buffer.
            }
            if (verticesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer); // Destroy auxiliar buffer.
            }
        }
        
        /*Creación del Shader del Sol*/
        shaderProgramSol = new ShaderProgram();
        shaderProgramSol.createVertexShader(leerFile("vertSol.glsl"));
        shaderProgramSol.createFragmentShader(leerFile("fragSol.glsl"));
//        shaderProgramSol.createVertexShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertSol.glsl"));
//	shaderProgramSol.createFragmentShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragSol.glsl"));
        shaderProgramSol.link();
        shaderProgramSol.createUniform("projection");
        shaderProgramSol.createUniform("view");
        shaderProgramSol.createUniform("model");
        shaderProgramSol.createUniform("diffuseTexture");
        projection.perspective( (float) Math.toRadians(60.0f), 600.0f/600.0f, 0.001f, 1000.0f);
        view.setTranslation(new Vector3f(0.0f, 0.0f, -4.0f)); 
        
        /*Creación del Shader de la Tierra*/
        shaderProgramT = new ShaderProgram();
	shaderProgramT.createVertexShader(leerFile("vertT.glsl"));
	shaderProgramT.createFragmentShader(leerFile("fragT.glsl"));
//      shaderProgramT.createVertexShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertT.glsl"));
//      shaderProgramT.createFragmentShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragT.glsl"));
        shaderProgramT.link();
        shaderProgramT.createUniform("projection");
        shaderProgramT.createUniform("view");
        shaderProgramT.createUniform("model");
        shaderProgramT.createUniform("lightColor");
        shaderProgramT.createUniform("lightPos");
        shaderProgramT.createUniform("viewPos");
        shaderProgramT.createUniform("diffuseTexture");

        /*Creación del Shader de la Luna*/
        shaderProgramL = new ShaderProgram();
	shaderProgramL.createVertexShader(leerFile("vertT.glsl"));
	shaderProgramL.createFragmentShader(leerFile("fragT.glsl"));
//        shaderProgramL.createVertexShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertT.glsl"));
//        shaderProgramL.createFragmentShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragT.glsl"));
        shaderProgramL.link();
        shaderProgramL.createUniform("projection");
        shaderProgramL.createUniform("view");
        shaderProgramL.createUniform("model");
        shaderProgramL.createUniform("lightColor");
        shaderProgramL.createUniform("lightPos");
        shaderProgramL.createUniform("viewPos");
        shaderProgramL.createUniform("diffuseTexture");
        
        /*Carga de las texturas*/
        textureIDSol = cargaTex("sun.png", textureIDSol);
        textureIDT = cargaTex("earth.png", textureIDT);
        textureIDL = cargaTex("moon.png", textureIDL);
//        textureIDSol = cargaTex("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\sun.png", textureIDSol);
//        textureIDT = cargaTex("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\earth.png", textureIDT);
//        textureIDL = cargaTex("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\moon.png", textureIDL);


        loop();
        // Libera las devoluciones de llamada de la ventana y destruye la ventana
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() throws Exception {
        
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        // Configure GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Create the window
        window = glfwCreateWindow(600, 600, "TAG", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
            if (key == GLFW_KEY_Q) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.z += 0.1f;
                view = view.setTranslation(translation);
                System.out.println("Translation Z: " + translation.z);
            }
            if (key == GLFW_KEY_E) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.z -= 0.1f;
                view = view.setTranslation(translation);
                
                System.out.println("Translation Z: " + translation.z);
            }
            if (key == GLFW_KEY_D) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.x -= 0.1f;
                view = view.setTranslation(translation);
                System.out.println("Translation X: " + translation.x);
            }
            if (key == GLFW_KEY_A) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.x += 0.1f;
                view = view.setTranslation(translation);
                System.out.println("Translation X: " + translation.x);
            }
            if (key == GLFW_KEY_S) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.y -= 0.1f;
                view = view.setTranslation(translation);
                System.out.println("Translation Y: " + translation.y);
            }
            if (key == GLFW_KEY_W) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.y += 0.1f;
                view = view.setTranslation(translation);
                System.out.println("Translation Y: " + translation.y);
            }
        });

        // Obtener la pila de hilos y empujar un nuevo marco
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Obtener el tamaño de ventana pasado a glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        GL.createCapabilities();
    }

    private void loop() throws Exception {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        
        while (!glfwWindowShouldClose(window)) {
            
            glfwPollEvents();
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            glBindVertexArray(VAO);

            /*Sol*/
            shaderProgramSol.bind();
            shaderProgramSol.setUniform("projection", projection);
            shaderProgramSol.setUniform("view", view);
            shaderProgramSol.setUniform("model", modelSol);
            glActiveTexture(GL_TEXTURE0);
            shaderProgramSol.setUniform("diffuseTexture", 0);
            glBindTexture(GL_TEXTURE_2D, textureIDSol);
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);

            
            /*movimiento y rotacion de la tierra*/
            angle += 0.01f;
            angleT += 0.1f;
            modelT.identity();
            modelT.scale(0.3f);//tamaño de la esfera
            modelT.rotateY(angle);//orbitar
            modelT.translate(new Vector3f(4.0f, 0.0f, 3.0f));//traslacion
            modelT.rotate(angleT, new Vector3f(0.0f, 1.0f, 0.0f));//rotacion

            shaderProgramT.bind();
            shaderProgramT.setUniform("projection", projection);
            shaderProgramT.setUniform("view", view);
            shaderProgramT.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramT.setUniform("viewPos", 0.0f, 0.0f, 0.0f);
            shaderProgramT.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramT.setUniform("model", modelT);
            shaderProgramT.setUniform("diffuseTexture", 0);
            glBindTexture(GL_TEXTURE_2D, textureIDT);

            angleLunaTraslacion += 0.01f;
            angleLunaRotacion += 0.01f;
            
            /*movimiento y rotacion de la luna*/            
            modelL.identity();            
            modelL.scale(0.09f);//tamaño de la esfera  
            modelL.rotateY(angleLunaTraslacion ); //orbitar            
            modelL.translate(new Vector3f(17.0f, 0.0f, 12.0f));//Translacion 
            
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0); 

            shaderProgramL.bind();
            shaderProgramL.setUniform("projection", projection);
            shaderProgramL.setUniform("view", view);
            shaderProgramL.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramL.setUniform("viewPos", 0.0f, 0.0f, 0.0f);
            shaderProgramL.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramL.setUniform("model", modelL);
            shaderProgramL.setUniform("diffuseTexture", 0);
            glBindTexture(GL_TEXTURE_2D, textureIDL);
 
	    //modelL.rotate(angleLunaRotacion, new Vector3f(0.0f, -1.0f, 0.0f));//rotacion
            /*
            modelL.rotate(angleLunaRotacion, new Vector3f(0.0f, -1.0f, 0.0f),
            		modelL.translate(new Vector3f(17.0f, 0.0f, 12.0f),
            				modelL.rotateY(angleLunaTraslacion, 
            						modelL.scale(0.09f,
            								modelL.identity()
            						)	
            				)
            		)
            );
            */
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window); // swap the color buffers
        }
    }

    public static void main(String[] args) throws Exception {
        new Demo().run();
    }
     
    public String leerFile(String p) throws FileNotFoundException, IOException{
        FileReader fileReader = new FileReader(p);
        String fileContents = "";
        int i ;
        while((i =  fileReader.read())!=-1){
         char ch = (char)i;
         fileContents = fileContents + ch; 
        }
        return fileContents;
    }
    
    public int cargaTex(String path, int textureID) throws FileNotFoundException, IOException{
        InputStream in = new FileInputStream(path);
        PNGDecoder decoder = new PNGDecoder(in);
        ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
        decoder.decode(buf, decoder.getWidth()*4, Format.RGBA);
        buf.flip();
        textureID = glGenTextures();
        System.out.println("ID Textura: " + textureID);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID); 
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glGenerateMipmap(GL_TEXTURE_2D); 
        return textureID;
    }
  
}

























/*
package com.tga.opengl;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Demo {

    // The window handle
    private long window;
    int VAO, VBO, VBO2, VBO3, EBO, indices;
    float angle = 5.0f;
    float angleT = -5.0f;
    int textureIDSol, textureIDT, textureIDL;

    ShaderProgram shaderProgramSol;              
    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f(); 
    Matrix4f modelSol = new Matrix4f(); 
    //Matrix4f viewPos = new Matrix4f();
    
    ShaderProgram shaderProgramT;
    Matrix4f modelT = new Matrix4f(); 
    
    ShaderProgram shaderProgramL;
    Matrix4f modelL = new Matrix4f();
    
    public void run() throws Exception {
        init();
        
        Sphere esfera = new Sphere();
        esfera.create();

        FloatBuffer verticesBuffer = null;
        FloatBuffer normalBuffer = null;
        FloatBuffer texCoordBuffer = null;
        IntBuffer indicesBuffer = null;
        
        try {
        verticesBuffer = MemoryUtil.memAllocFloat(esfera.getVertices().length);
        verticesBuffer.put(esfera.getVertices()).flip();
        
        normalBuffer = MemoryUtil.memAllocFloat(esfera.getNormals().length);
        normalBuffer.put(esfera.getNormals()).flip();
        
        texCoordBuffer = MemoryUtil.memAllocFloat(esfera.getTexCoords().length);
        texCoordBuffer.put(esfera.getTexCoords()).flip();
        
        indicesBuffer = MemoryUtil.memAllocInt(esfera.getIndices().length);
        indicesBuffer.put(esfera.getIndices()).flip();
        indices = esfera.getIndices().length;
        
        VAO = glGenVertexArrays(); // Create VertexArrayObject
        VBO = glGenBuffers(); // Create VertexBufferObject
        VBO2 = glGenBuffers();
        VBO3 = glGenBuffers();
        EBO = glGenBuffers();
        
        glBindVertexArray(VAO); // Bind current VAO
        
        //position attribute
        glBindBuffer(GL_ARRAY_BUFFER, VBO); // Bind Vertex VAO
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW); // Assign buffer to VBO
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // Definition of position attribute in buffer
        glEnableVertexAttribArray(0); // Active attribute 0 on VAO
        
        //normal attribute
        glBindBuffer(GL_ARRAY_BUFFER, VBO2); // Bind Vertex VAO
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW); // Assign buffer to VBO
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // Definition of position attribute in buffer
        glEnableVertexAttribArray(1); // Active attribute 0 on VAO
        
        //textCoord attribute
        glBindBuffer(GL_ARRAY_BUFFER, VBO3); // Bind Vertex VAO
        glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_STATIC_DRAW); // Assign buffer to VBO
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0); // Definition of position attribute in buffer
        glEnableVertexAttribArray(2); // Active attribute 0 on VAO
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        
        // Unbind VBO (0 is equal to null VBO)
        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        glBindVertexArray(0); // Unbind VAO (0 is equal to null VAO)
        
        } finally {
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer); // Destroy auxiliar buffer.
            }
            if (verticesBuffer != null) {
                MemoryUtil.memFree(normalBuffer); // Destroy auxiliar buffer.
            }
            if (verticesBuffer != null) {
                MemoryUtil.memFree(texCoordBuffer); // Destroy auxiliar buffer.
            }
            if (verticesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer); // Destroy auxiliar buffer.
            }
        }
        
        shaderProgramSol = new ShaderProgram();
        shaderProgramSol.createVertexShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertSol.glsl"));
	shaderProgramSol.createFragmentShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragSol.glsl"));
//      shaderProgramSol.createVertexShader(leerFile("vertSol.glsl"));
//	shaderProgramSol.createFragmentShader(leerFile("fragSol.glsl"));
   
        shaderProgramSol.link();
        shaderProgramSol.createUniform("projection");
        shaderProgramSol.createUniform("view");
        shaderProgramSol.createUniform("model");
        shaderProgramSol.createUniform("lightColor");
        shaderProgramSol.createUniform("lightPos");
        shaderProgramSol.createUniform("viewPos");
        shaderProgramSol.createUniform("objectColor");
        shaderProgramSol.createUniform("diffuseTexture");
        projection.perspective( (float) Math.toRadians(60.0f), 600.0f/600.0f, 0.001f, 1000.0f);
        view.setTranslation(new Vector3f(0.0f, 0.0f, -4.0f)); 
        
        shaderProgramT = new ShaderProgram();
	shaderProgramT.createVertexShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertT.glsl"));
	shaderProgramT.createFragmentShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragT.glsl"));
//      shaderProgramT.createVertexShader(leerFile("vertT.glsl"));
//	shaderProgramT.createFragmentShader(leerFile("fragT.glsl"));
        
        shaderProgramT.link();
        shaderProgramT.createUniform("projection");
        shaderProgramT.createUniform("view");
        shaderProgramT.createUniform("model");
        shaderProgramT.createUniform("lightColor");
        shaderProgramT.createUniform("lightPos");
        shaderProgramT.createUniform("viewPos");
        shaderProgramT.createUniform("objectColor");
        shaderProgramT.createUniform("diffuseTexture");

        shaderProgramL = new ShaderProgram();
	shaderProgramL.createVertexShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertT.glsl"));
	shaderProgramL.createFragmentShader(leerFile("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragT.glsl"));
//      shaderProgramL.createVertexShader(leerFile("vertT.glsl"));
//	shaderProgramL.createFragmentShader(leerFile("fragT.glsl"));
        
        shaderProgramL.link();
        shaderProgramL.createUniform("projection");
        shaderProgramL.createUniform("view");
        shaderProgramL.createUniform("model");
        shaderProgramL.createUniform("lightColor");
        shaderProgramL.createUniform("lightPos");
        shaderProgramL.createUniform("viewPos");
        shaderProgramL.createUniform("objectColor");
        shaderProgramL.createUniform("diffuseTexture");
        
        textureIDSol = cargaTex("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\sun.png", textureIDSol);
        textureIDT = cargaTex("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\earth.png", textureIDT);
        textureIDL = cargaTex("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\moon.png", textureIDL);

        loop();
        // Libera las devoluciones de llamada de la ventana y destruye la ventana
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() throws Exception {
        
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        // Configure GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Create the window
        window = glfwCreateWindow(600, 600, "TAG", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
            if (key == GLFW_KEY_Q) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.z += 0.1f;
                view = view.setTranslation(translation);
                System.out.println(translation.z);
            }
            if (key == GLFW_KEY_E) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.z -= 0.1f;
                view = view.setTranslation(translation);
                
                System.out.println(translation.z);
            }
            if (key == GLFW_KEY_D) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.x -= 0.1f;
                view = view.setTranslation(translation);
                System.out.println(translation.x);
            }
            if (key == GLFW_KEY_A) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.x += 0.1f;
                view = view.setTranslation(translation);
                System.out.println(translation.x);
            }
            if (key == GLFW_KEY_S) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.y -= 0.1f;
                view = view.setTranslation(translation);
                System.out.println(translation.y);
            }
            if (key == GLFW_KEY_W) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.y += 0.1f;
                view = view.setTranslation(translation);
                System.out.println(translation.y);
            }
        });

        // Obtener la pila de hilos y empujar un nuevo marco
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Obtener el tamaño de ventana pasado a glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        GL.createCapabilities();
    }

    private void loop() throws Exception {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        
        while (!glfwWindowShouldClose(window)) {
            
            glfwPollEvents();
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            glBindVertexArray(VAO);
            
            modelSol.identity();
            //modelSol.identity().scale(0.6f);
            shaderProgramSol.bind();
            shaderProgramSol.setUniform("projection", projection);
            shaderProgramSol.setUniform("view", view);
            shaderProgramSol.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramSol.setUniform("viewPos", 0.0f, 0.0f, 0.0f);
            shaderProgramSol.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramSol.setUniform("objectColor", 3.0f, 3.0f, 0.0f);
            shaderProgramSol.setUniform("model", modelSol);
            
            glActiveTexture(GL_TEXTURE0);
            shaderProgramSol.setUniform("diffuseTexture", 0);
            glBindTexture(GL_TEXTURE_2D, textureIDSol);
            System.out.println(textureIDSol);
            
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);
            
            
            
//            angle += 0.01f;
//            angleT += 0.05f;
//            modelT.identity().scale(0.3f);
//            modelT.translate(new Vector3f(3.0f, 0.0f, 3.0f).rotateY(angle));
//            angleT += 1;
//            modelT.rotate((float)Math.toRadians(angleT), new Vector3f(0.0f, -1.0f, 0.0f));

            
            
            shaderProgramT.bind();
            shaderProgramT.setUniform("projection", projection);
            shaderProgramT.setUniform("view", view);
            shaderProgramT.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramT.setUniform("viewPos", 0.0f, 0.0f, 0.0f);
            shaderProgramT.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramT.setUniform("objectColor", 0.0f, 0.0f, 1.0f);
            shaderProgramT.setUniform("model", modelT);
            
            shaderProgramT.setUniform("diffuseTexture", 0);
            glBindTexture(GL_TEXTURE_2D, textureIDT);
            System.out.println(textureIDT);
            //movimiento y rotacion de la tierra
            angle += 0.01f;
            angleT += 0.01f;
            modelT.identity().scale(0.3f);
            modelT.translate(new Vector3f(3.0f, 0.0f, 3.0f).rotateY(angle)).rotate(angleT, new Vector3f(0.0f, -1.0f, 0.0f));
//            angleT += 1;
//            modelT.rotate((float)Math.toRadians(angleT), new Vector3f(0.0f, -1.0f, 0.0f));
            
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0); 
           
            


            shaderProgramL.bind();
            shaderProgramL.setUniform("projection", projection);
            shaderProgramL.setUniform("view", view);
            shaderProgramL.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramL.setUniform("viewPos", 0.0f, 0.0f, 0.0f);
            shaderProgramL.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramL.setUniform("objectColor", 0.7f, 0.7f, 0.7f);
            shaderProgramL.setUniform("model", modelL);

            shaderProgramL.setUniform("diffuseTexture", 0);
            glBindTexture(GL_TEXTURE_2D, textureIDL);
            System.out.println(textureIDL);
            
            //movimiento y rotacion de la luna
            modelL.identity().scale(0.09f);
            modelL.translate(new Vector3f(15.0f, 0.0f, 12.0f).rotateY(angle));
            
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window); // swap the color buffers
        }
    }

    public static void main(String[] args) throws Exception {
        new Demo().run();
    }
     
    public String leerFile(String p) throws FileNotFoundException, IOException{
        FileReader fileReader = new FileReader(p);
        String fileContents = "";
        int i ;
        while((i =  fileReader.read())!=-1){
         char ch = (char)i;
         fileContents = fileContents + ch; 
        }
        return fileContents;
    }
    
    public int cargaTex(String path, int textureID) throws FileNotFoundException, IOException{
        InputStream in = new FileInputStream(path);
        PNGDecoder decoder = new PNGDecoder(in);
        ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
        decoder.decode(buf, decoder.getWidth()*4, Format.RGBA);
        buf.flip();
        textureID = glGenTextures();
        System.out.println(textureID);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID); 
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glGenerateMipmap(GL_TEXTURE_2D); 
        return textureID;
    }
  
}
*/