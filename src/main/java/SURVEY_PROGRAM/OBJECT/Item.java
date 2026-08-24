package SURVEY_PROGRAM.OBJECT;

import java.time.LocalDate;

/**
 * Represents a task that can be marked as complete or incomplete.
 */
public class Item {
    private final String name;
    private boolean done = false;

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
        return String.format("[%s] %s", done ? "X" : " ", name);
    }

    /** Marks this task as complete and reports the resulting state. */
    public void mark() {
        if (done) {
            System.out.println("BUT, IT WAS ALREADY DONE.");
        } else {
            done = true;
            System.out.println("THEN, IT IS DONE.");
            System.out.println(this);
        }
    }

    /** Marks this task as incomplete and reports the resulting state. */
    public void undo() {
        if (done) {
            done = false;
            System.out.println("THEN, IT WAS AS IF IT WAS NEVER DONE.");
            System.out.println(this);
        } else {
            System.out.println("BUT, YOU HAVEN'T DONE IT IN THE FIRST PLACE.");
        }
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
        return done;
    }

    /**
     * Sets the done status without printing messages.
     *
     * @param done completion state restored from storage
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * Indicates whether this undated base item occurs on a date.
     *
     * @param t date to check
     * @return always {@code false}; dated subclasses override this behavior
     */
    public boolean inRange(LocalDate t) {
        return false;
    }
}
