package surveyprogram.object;

import java.time.LocalDate;

/**
 * Represents a task that can be marked as complete or incomplete.
 */
public class Item {
    private final String name;
    private boolean isDone = false;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param name task description
     */
    public Item(String name) {
        this.name = name;
    }

    /**
     * Returns the task description with its completion marker.
     *
     * @return formatted task state and description
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", isDone ? "X" : " ", name);
    }

    /**
     * Marks this task as complete and returns a description of the result.
     *
     * @return message describing the resulting task state
     */
    public String mark() {
        if (isDone) {
            return "BUT, IT WAS ALREADY DONE.";
        }

        isDone = true;
        return "THEN, IT IS DONE.\n" + this;
    }

    /**
     * Marks this task as incomplete and returns a description of the result.
     *
     * @return message describing the resulting task state
     */
    public String undo() {
        if (isDone) {
            isDone = false;
            return "THEN, IT WAS AS IF IT WAS NEVER DONE.\n" + this;
        }

        return "BUT, YOU HAVEN'T DONE IT IN THE FIRST PLACE.";
    }

    /**
     * Returns the task description used for validation and display.
     *
     * @return task description
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return {@code true} when the task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Sets the done status without printing messages.
     *
     * @param isDone completion state restored from storage
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Indicates whether this undated base item occurs on a date.
     *
     * @param date date to check
     * @return always {@code false}; dated subclasses override this behavior
     */
    public boolean inRange(LocalDate date) {
        return false;
    }
}
