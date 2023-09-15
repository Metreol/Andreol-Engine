package Andreol;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;
    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
    }

    public void start() {
        for (Component component : components) {
            component.start();
        }
    }

    public void update(float deltaTime) {
        for (Component component : components) {
            component.update(deltaTime);
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        // Might need to change to normal for loops, in other methods too
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                try {
                    return componentClass.cast(component);
                } catch (ClassCastException cce) {
                    cce.printStackTrace();
                    assert false : String.format("Error: (GameObject) Could not cast '%s' to '%s'.", component, componentClass);
                }
            }
        }

        return null;
    }

    public <T extends Component> void addComponent(Component component) {
        components.add(component);
        component.gameObject = this;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                components.remove(component);
                return;
            }
        }
    }

}
