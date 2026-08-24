package SURVEY_PROGRAM.OBJECT;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Item {
    /**
     * Creates an undated task.
     *
     * @param description task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the display form of this todo, including its type and completion markers.
     *
     * @return formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
