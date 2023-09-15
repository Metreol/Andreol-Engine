package Andreol;

import java.awt.event.KeyEvent;

public class LevelScene extends Scene {
    private static final double sceneTransitionTimeTotal = 1.0d;

    private boolean changingScene = false;
    private double sceneTransitionTimeRemaining;

    public LevelScene() {
        System.out.println("Inside Level Scene");
        Window window = Window.get();
        window.r = 1;
        window.g = 1;
        window.b = 1;
    }

    @Override
    public void update(float deltaTime) {
        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
            sceneTransitionTimeRemaining = sceneTransitionTimeTotal;
        }

        if (changingScene && sceneTransitionTimeRemaining > 0) {
            sceneTransitionTimeRemaining -= deltaTime;
            double fadeValue = deltaTime / sceneTransitionTimeTotal;
            Window window = Window.get();
            window.r -= fadeValue;
            window.g -= fadeValue;
            window.b -= fadeValue;
        } else if (changingScene) {
            Window.changeScene(0);
        }
    }
}
