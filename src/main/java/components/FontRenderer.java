package components;

import Andreol.Component;
import com.sun.security.jgss.GSSUtil;

public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Found SpriteRenderer!");
        }
    }
    @Override
    public void update(float deltaTime) {

    }
}
