import Andreol.Window;
import renderer.Shader;

public class Main {

    public static void main(String[] args) {
        Window window = Window.get();
        new Shader("D:\\cmasenv\\workspace\\java\\Andreol-Engine\\assets\\shaders\\default.glsl");
        window.run();
    }
}
