package Andreol;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();

    public Scene() {

    }

    public abstract void update(float deltaTime);

    public void init() {

    }

    public void start() {
        for (GameObject gameObject : gameObjects) {
            gameObject.start();
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject) {
        gameObjects.add(gameObject);

        if (!isRunning) {
            gameObject.start();
        }
    }

    public Camera getCamera() {
        return camera;
    }
}
