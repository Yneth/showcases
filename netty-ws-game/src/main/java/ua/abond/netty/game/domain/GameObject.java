package ua.abond.netty.game.domain;

import ua.abond.netty.game.domain.component.Component;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private List<Component> components = new ArrayList<>();

    public GameObject() {
        components.add(new Transform(Vector2.ZERO.copy()));
    }

    public Transform getTransform() {
        return (Transform) components.get(0);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> type) {
        return (T) components.stream()
                .filter(component -> type.isAssignableFrom(component.getClass()))
                .findFirst()
                .orElse(null);
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void removeComponent(Component component) {
        components.remove(component);
    }
}
