package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shader {
    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] shaders = source.split("#type +[A-Za-z]+");

            Pattern typePattern = Pattern.compile("#type +([A-Za-z]+)");
            Matcher typeMatcher = typePattern.matcher(source);
            int i = 0;
            while (typeMatcher.find()) {
                String shaderType = typeMatcher.group(1);
                if (shaderType.equalsIgnoreCase("vertex")) {
                    vertexSource = shaders[i + 1];
                } else if (shaderType.equalsIgnoreCase("fragment")) {
                    fragmentSource = shaders[i + 1];
                } else {
                    throw new IOException(String.format("Unexpected shader type '%s' in '%s'.", shaderType, filepath));
                }
                i++;
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            assert false : String.format("Error: Could not open file for shader: '%s'", filepath);
        }

        System.out.println("VERTEX:");
        System.out.println(vertexSource);
        System.out.println("FRAGMENT:");
        System.out.println(fragmentSource);
    }

    public void compile() {

    }

    public void use() {

    }

    public void detach() {

    }
}
