package Andreol;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {
    private static final double sceneTransitionTimeTotal = 5.0d;

    private boolean changingScene = false;
    private double sceneTransitionTimeRemaining;

    public LevelEditorScene() {
        System.out.println("Inside LevelEditor Scene");
        Window window = Window.get();
        window.r = 1;
        window.g = 1;
        window.b = 1;
    }

    @Override
    public void update(double deltaTime) {
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
            Window.changeScene(1);
        }
    }
}
