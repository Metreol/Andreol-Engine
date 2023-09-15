package components;

import Andreol.Component;

public class SpriteRenderer extends Component {
    private boolean firstTime = false;

    @Override
    public void start() {
        System.out.println("I'm starting!");
    }

    @Override
    public void update(float deltaTime) {
        if (!firstTime) {
            System.out.println("I'm updating");
            firstTime = true;
        }
    }
}
