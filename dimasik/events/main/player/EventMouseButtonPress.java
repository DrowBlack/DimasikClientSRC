package dimasik.events.main.player;

import dimasik.events.api.main.Event;

public class EventMouseButtonPress
implements Event {
    private int button;

    public int getButton() {
        return this.button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public String toString() {
        return "EventMouseButtonPress(button=" + this.getButton() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventMouseButtonPress)) {
            return false;
        }
        EventMouseButtonPress other = (EventMouseButtonPress)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return this.getButton() == other.getButton();
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventMouseButtonPress;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + this.getButton();
        return result;
    }

    public EventMouseButtonPress(int button) {
        this.button = button;
    }
}
