package survey_program.object;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Item {
    /** Creates an undated task with the supplied description. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
