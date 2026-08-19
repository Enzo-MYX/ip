/**
 * Represents a task without a date or time.
 */
public class Todo extends Item {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
