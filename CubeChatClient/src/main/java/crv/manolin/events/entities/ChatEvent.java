package crv.manolin.events.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class ChatEvent implements Serializable {
    private final ChatEventType type;
    private final LocalDateTime timestamp;
    private ArrayList<Object> props = new ArrayList<>();
    protected ChatEvent(ChatEventType type) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    protected ChatEvent(ChatEventType type, Object... props) {
        this.props = new ArrayList<>();
        this.props.addAll(Arrays.asList(props));
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public void setProps(ArrayList<Object> props) {
        this.props = props;
    }
    public void addProp(Object prop) {
        this.props.add(prop);
    }

    public <T> T getProp(int index, Class<T> clazz) {
        if (props.size() <= index) return null;
        Object prop = props.get(index);
        if (clazz.isInstance(prop)) return clazz.cast(prop);
        return null;
    }

    public <T> T getFirstPropOfType(Class<T> clazz) {
        for (Object prop : props) {
            if (clazz.isInstance(prop)) return clazz.cast(prop);
        }
        return null;
    }

    public ArrayList<Object> getProps() {
        return props;
    }

    public ChatEventType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ChatEvent{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}
