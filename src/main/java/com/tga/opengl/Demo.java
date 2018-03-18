package com.tga.opengl;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.FileInputStream;
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
    float angle = 0.0f;
    int textureIDSol, textureIDT, textureIDL;

    ShaderProgram shaderProgramSol;                                                                              //aspectratio
    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f(); // identity() not necesary because Matrix4f() generated a identitiy matrix
    Matrix4f modelSol = new Matrix4f(); 
    
    
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
//	shaderProgram.createShader("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertSol.glsl",ARBVertexShader.GL_VERTEX_SHADER_ARB);
//	shaderProgram.createShader("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragSol.glsl", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
	shaderProgramSol.createVertexShader(vertexShaderSourceSol);
	shaderProgramSol.createFragmentShader(fragmentShaderSourceSol);
        shaderProgramSol.link();
        shaderProgramSol.createUniform("projection");
        shaderProgramSol.createUniform("view");
        shaderProgramSol.createUniform("model");
        shaderProgramSol.createUniform("lightColor");
        shaderProgramSol.createUniform("lightPos");
        projection.perspective( (float) Math.toRadians(60.0f), 600.0f/600.0f, 0.001f, 1000.0f);
        view.setTranslation(new Vector3f(0.0f, 0.0f, -4.0f));             
        //angle += glfwGetTime();
            //angle += 1;
        //modelSol.identity().translate(new Vector3f(0.0f, 0.0f, 0.0f)).rotate((float)Math.toRadians(angle), new Vector3f(0.0f, 1.0f, 0.0f));    
        modelSol.identity();//.translate(new Vector3f(0.0f, 1.0f, 0.0f)).rotate(angle, new Vector3f(1.0f, 0.0f, 0.0f));
        
        
        shaderProgramT = new ShaderProgram();
//	shaderProgram.createShader("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertSol.glsl",ARBVertexShader.GL_VERTEX_SHADER_ARB);
//	shaderProgram.createShader("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragSol.glsl", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
	shaderProgramT.createVertexShader(vertexShaderSourceT);
	shaderProgramT.createFragmentShader(fragmentShaderSourceT);
        shaderProgramT.link();
        shaderProgramT.createUniform("projection");
        shaderProgramT.createUniform("view");
        shaderProgramT.createUniform("model");
        shaderProgramT.createUniform("lightColor");
        shaderProgramT.createUniform("lightPos");
//        projection.perspective( (float) Math.toRadians(90.0f), 600.0f/600.0f, 0.001f, 1000.0f);
//        view.setTranslation(new Vector3f(0.0f, 0.0f, -4.0f));             
        //angle += glfwGetTime();
        //angle += 1;
        //modelT.identity(); //.translate(new Vector3f(1.0f, 0.0f, 1.0f));    
        //modelT.identity();//.translate(new Vector3f(0.0f, 1.0f, 0.0f)).rotate(angle, new Vector3f(1.0f, 0.0f, 0.0f));
        
        
        
        
        shaderProgramL = new ShaderProgram();
//	shaderProgram.createShader("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\vertSol.glsl",ARBVertexShader.GL_VERTEX_SHADER_ARB);
//	shaderProgram.createShader("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\fragSol.glsl", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
	shaderProgramL.createVertexShader(vertexShaderSourceL);
	shaderProgramL.createFragmentShader(fragmentShaderSourceL);
        shaderProgramL.link();
        shaderProgramL.createUniform("projection");
        shaderProgramL.createUniform("view");
        shaderProgramL.createUniform("model");
        shaderProgramL.createUniform("lightColor");
        shaderProgramL.createUniform("lightPos");
//        projection.perspective( (float) Math.toRadians(90.0f), 600.0f/600.0f, 0.001f, 1000.0f);
//        view.setTranslation(new Vector3f(0.0f, 0.0f, -4.0f));             
        //angle += glfwGetTime();
            //angle += 1;
        //modelL.identity().translate(new Vector3f(1.0f, 0.0f, 2.0f));    
        //modelL.identity();//.translate(new Vector3f(0.0f, 1.0f, 0.0f)).rotate(angle, new Vector3f(1.0f, 0.0f, 0.0f));
        
        
       
        
        
        
        
        
        InputStream in = new FileInputStream("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\sun.png");
        PNGDecoder decoder = new PNGDecoder(in);
        ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
        decoder.decode(buf, decoder.getWidth()*4, Format.RGBA);
        buf.flip();
        textureIDSol = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureIDSol); // all upcoming GL_TEXTURE_2D operations now have effect on this texture object
        // set the texture wrapping parameters
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glGenerateMipmap(GL_TEXTURE_2D); 
        
        
//        InputStream inT = new FileInputStream("E:\\Master\\TAG\\SistemaSolar\\Ssolar\\src\\main\\java\\com\\tga\\opengl\\earth_clouds.png");
//        PNGDecoder decoderT = new PNGDecoder(inT);
//        ByteBuffer bufT = ByteBuffer.allocateDirect(4*decoderT.getWidth()*decoderT.getHeight());
//        decoderT.decode(bufT, decoderT.getWidth()*4, Format.RGBA);
//        bufT.flip();
//        textureIDT = glGenTextures();
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, textureIDT); // all upcoming GL_TEXTURE_2D operations now have effect on this texture object
//         
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoderT.getWidth(), decoderT.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bufT);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glGenerateMipmap(GL_TEXTURE_2D); 
//        

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
            if (key == GLFW_KEY_A) {
                Vector3f translation = new Vector3f();
                view.getTranslation(translation);
                translation.x -= 0.1f;
                view = view.setTranslation(translation);
                System.out.println(translation.x);
            }
            if (key == GLFW_KEY_D) {
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
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    private void loop() throws Exception {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        
        while (!glfwWindowShouldClose(window)) {
            angle += 1;
            glfwPollEvents();
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            
            shaderProgramSol.bind();
            shaderProgramSol.setUniform("projection", projection);
            shaderProgramSol.setUniform("view", view);
            shaderProgramSol.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramSol.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramSol.setUniform("model", modelSol);
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0); // 12*3 los índices comienzan en 0 -> 12 triángulos -> 6 cuadrados
            
            
            shaderProgramT.bind();
            shaderProgramT.setUniform("projection", projection);
            shaderProgramT.setUniform("view", view);
            shaderProgramT.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramT.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramT.setUniform("model", modelT);
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0); // 12*3 los índices comienzan en 0 -> 12 triángulos -> 6 cuadrados
            //angle += glfwGetTime();
            angle += 0.2;
            modelT.identity().scale(0.5f).translate(new Vector3f(2.0f, 0.0f, 3.0f)).rotate(angle, new Vector3f(1.0f, 0.0f, 0.0f));
        
//            modelT.identity().translate(new Vector3f(1.0f, 0.0f, 1.0f)).rotate((float)Math.toRadians(angle), new Vector3f(0.0f, 1.0f, 0.0f));
            //modelT.scale(0.1f);
            //modelT.identity().translate(new Vector3f(1.0f, 0.0f, 1.0f)).rotate((float)Math.toRadians(angle), new Vector3f(1.0f, 0.0f, 0.0f));    
            //modelT.identity().translate(new Vector3f(1.0f, 0.0f, 1.0f)).rotate((float)Math.toRadians(angle), new Vector3f(1.0f, 0.0f, 0.0f));    

            shaderProgramL.bind();
            shaderProgramL.setUniform("projection", projection);
            shaderProgramL.setUniform("view", view);
            shaderProgramL.setUniform("lightColor", 1.0f,1.0f,1.0f);
            shaderProgramL.setUniform("lightPos", 0.0f, 0.0f, 0.0f);
            shaderProgramL.setUniform("model", modelL);
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);
            modelL.identity().scale(0.3f).translate(new Vector3f(6.0f, 0.0f, 8.0f)).rotate(angle, new Vector3f(1.0f, 0.0f, 0.0f));
       
            //modelL.identity().translate(new Vector3f(1.0f, 0.0f, 2.0f)).rotate((float)Math.toRadians(angle), new Vector3f(1.0f, 0.0f, 0.0f));

//            shaderProgram.setUniform("objectColor", 0.0f,0.0f,0.0f);
            
            //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glBindVertexArray(VAO);

            //glDrawArrays(GL_TRIANGLES, 0, 3);
           // glBindVertexArray(0); // no need to unbind it every time
            glfwSwapBuffers(window); // swap the color buffers
        }
    }

    public static void main(String[] args) throws Exception {
        new Demo().run();
    }
    
    
    
    
   
    
    String fragmentShaderSourceSol = "#version 330 core\n"
        + "out vec4 fragColor;\n"
        + "in vec3 fragPos;\n"
        + "in vec3 Normal;\n"
        + "in vec2 UV;\n"
        + "uniform vec3 lightPos;\n"
        + "uniform vec3 viewPos;"
        + "uniform vec3 lightColor;\n"
//        + "uniform vec3 objectColor;\n"
        + "uniform sampler2D diffuseTex;\n"
        + "const float ambientStrength = 0.3;\n"
        + "void main() {\n"
        + "// ambient\n"
        + " vec3 ambient = ambientStrength * lightColor;\n"
                
        + " // diffuse\n"
        + "vec3 norm = normalize(Normal);\n"
        + "vec3 lightDir = normalize(lightPos - fragPos);\n"
        + "float diff = max(dot(norm, lightDir), 0.0);\n"
        + "vec3 diffuse = diff * lightColor;\n"
        
//        + "//Specular\n" 
//        + "float specularStrength = 0.5;\n"
//        + "vec3 viewDir = normalize(viewPos - fragPos);\n"
//        + "vec3 reflectDir = reflect(-lightDir, norm);\n"
//        + "float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);\n"
//        + "vec3 specular = specularStrength * spec * lightColor;\n"
//        
        //+ "vec3 result = (ambient + diffuse + specular) * vec3(1.0, 1.0, 1.0);\n"               
        + "vec3 result = (ambient + diffuse) * texture(diffuseTex, UV).rgb;\n"
        //+ "vec3 result = (ambient + diffuse + specular) * texture(diffuseTex, UV).rgb;\n"
        + "fragColor = vec4(result, 1.0);\n"
        + "}";

        String vertexShaderSourceSol = "#version 330 core\n"
        + "out vec3 fragPos;"                  
        + "layout (location = 0) in vec3 aPos;\n"
        + "layout (location = 1) in vec3 aNormal;\n"
        + "layout (location = 2) in vec2 aTexCoord;\n"
        + "uniform mat4 projection;\n"
        + "uniform mat4 view;\n"
        + "uniform mat4 model;\n"
        + "out vec3 Normal;\n"
        + "out vec2 UV;\n"
        + "void main()\n"
        + "{\n"
        + " gl_Position = projection * view * model * vec4(aPos, 1.0);\n"
        + " //Normal = aNormal;\n"
        + " UV = aTexCoord;\n"
        + "fragPos = vec3(model * vec4(aPos, 1.0));"
        + " mat3 normalMatrix = transpose(inverse(mat3(model)));\n"
        + " Normal = normalMatrix * aNormal;\n"
        + "}";
        
        String fragmentShaderSourceT = "#version 330 core\n"
        + "out vec4 fragColor;\n"
        + "in vec3 fragPos;\n"
        + "in vec3 Normal;\n"
        + "in vec2 UV;\n"
        + "uniform vec3 lightPos;\n"
        + "uniform vec3 viewPos;"
        + "uniform vec3 lightColor;\n"
//        + "uniform vec3 objectColor;\n"
        + "uniform sampler2D diffuseTex;\n"
        + "const float ambientStrength = 0.3;\n"
        + "void main() {\n"
        + "// ambient\n"
        + " vec3 ambient = ambientStrength * lightColor;\n"
                
        + " // diffuse\n"
        + "vec3 norm = normalize(Normal);\n"
        + "vec3 lightDir = normalize(lightPos - fragPos);\n"
        + "float diff = max(dot(norm, lightDir), 0.0);\n"
        + "vec3 diffuse = diff * lightColor;\n"
        
//        + "//Specular\n" 
//        + "float specularStrength = 0.5;\n"
//        + "vec3 viewDir = normalize(viewPos - fragPos);\n"
//        + "vec3 reflectDir = reflect(-lightDir, norm);\n"
//        + "float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);\n"
//        + "vec3 specular = specularStrength * spec * lightColor;\n"
//        
        //+ "vec3 result = (ambient + diffuse + specular) * vec3(1.0, 1.0, 1.0);\n" 
        + "vec3 result = (ambient + diffuse) * vec3(1.0, 1.0, 1.0);\n" 
        //+ "vec3 result = (ambient + diffuse) * texture(diffuseTex, UV).rgb;\n"
        //+ "vec3 result = (ambient + diffuse + specular) * texture(diffuseTex, UV).rgb;\n"
        + "fragColor = vec4(result, 1.0);\n"
        + "}";

        String vertexShaderSourceT = "#version 330 core\n"
        + "out vec3 fragPos;"                  
        + "layout (location = 0) in vec3 aPos;\n"
        + "layout (location = 1) in vec3 aNormal;\n"
        + "layout (location = 2) in vec2 aTexCoord;\n"
        + "uniform mat4 projection;\n"
        + "uniform mat4 view;\n"
        + "uniform mat4 model;\n"
        + "out vec3 Normal;\n"
        + "out vec2 UV;\n"
        + "void main()\n"
        + "{\n"
        + " gl_Position = projection * view * model * vec4(aPos, 1.0);\n"
        + " //Normal = aNormal;\n"
        + " UV = aTexCoord;\n"
        + "fragPos = vec3(model * vec4(aPos, 1.0));"
        + " mat3 normalMatrix = transpose(inverse(mat3(model)));\n"
        + " Normal = normalMatrix * aNormal;\n"
        + "}";
        
        
        
        String fragmentShaderSourceL = "#version 330 core\n"
        + "out vec4 fragColor;\n"
        + "in vec3 fragPos;\n"
        + "in vec3 Normal;\n"
        + "in vec2 UV;\n"
        + "uniform vec3 lightPos;\n"
        + "uniform vec3 viewPos;"
        + "uniform vec3 lightColor;\n"
//        + "uniform vec3 objectColor;\n"
        + "uniform sampler2D diffuseTex;\n"
        + "const float ambientStrength = 0.3;\n"
        + "void main() {\n"
        + "// ambient\n"
        + " vec3 ambient = ambientStrength * lightColor;\n"
                
        + " // diffuse\n"
        + "vec3 norm = normalize(Normal);\n"
        + "vec3 lightDir = normalize(lightPos - fragPos);\n"
        + "float diff = max(dot(norm, lightDir), 0.0);\n"
        + "vec3 diffuse = diff * lightColor;\n"
        
//        + "//Specular\n" 
//        + "float specularStrength = 0.5;\n"
//        + "vec3 viewDir = normalize(viewPos - fragPos);\n"
//        + "vec3 reflectDir = reflect(-lightDir, norm);\n"
//        + "float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);\n"
//        + "vec3 specular = specularStrength * spec * lightColor;\n"
//        
        //+ "vec3 result = (ambient + diffuse + specular) * vec3(0.0, 1.0, 0.0);\n"
        + "vec3 result = (ambient + diffuse) * vec3(0.0, 1.0, 0.0);\n" 
        //+ "vec3 result = (ambient + diffuse) * texture(diffuseTex, UV).rgb;\n"
        //+ "vec3 result = (ambient + diffuse + specular) * texture(diffuseTex, UV).rgb;\n"
        + "fragColor = vec4(result, 1.0);\n"
        + "}";

        String vertexShaderSourceL = "#version 330 core\n"
        + "out vec3 fragPos;"                  
        + "layout (location = 0) in vec3 aPos;\n"
        + "layout (location = 1) in vec3 aNormal;\n"
        + "layout (location = 2) in vec2 aTexCoord;\n"
        + "uniform mat4 projection;\n"
        + "uniform mat4 view;\n"
        + "uniform mat4 model;\n"
        + "out vec3 Normal;\n"
        + "out vec2 UV;\n"
        + "void main()\n"
        + "{\n"
        + " gl_Position = projection * view * model * vec4(aPos, 1.0);\n"
        + " //Normal = aNormal;\n"
        + " UV = aTexCoord;\n"
        + "fragPos = vec3(model * vec4(aPos, 1.0));"
        + " mat3 normalMatrix = transpose(inverse(mat3(model)));\n"
        + " Normal = normalMatrix * aNormal;\n"
        + "}";
}
