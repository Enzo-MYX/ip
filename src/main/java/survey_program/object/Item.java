package survey_program.object;

import java.time.LocalDate;

/**
 * Represents a task that can be marked as complete or incomplete.
 */
public class Item {
    private final String name;
    private boolean isDone = false;

    /** Creates an incomplete task with the supplied description. */
    public Item(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", isDone ? "X" : " ", name);
    }

    /** Marks this task as complete and reports the result. */
    public void mark() {
        if (isDone) {
            System.out.println("BUT, IT WAS ALREADY DONE.");
        } else {
            isDone = true;
            System.out.println("THEN, IT IS DONE.");
            System.out.println(this);
        }
    }

    /** Marks this task as incomplete and reports the result. */
    public void undo() {
        if (isDone) {
            isDone = false;
            System.out.println("THEN, IT WAS AS IF IT WAS NEVER DONE.");
            System.out.println(this);
        } else {
            System.out.println("BUT, YOU HAVEN'T DONE IT IN THE FIRST PLACE.");
        }
    }

    /** Returns the task description used for validation and display. */
    public String getName() {
        return name;
    }

    /** Returns whether the task is marked as done. */
    public boolean isDone() {
        return isDone;
    }

    /** Sets the done status without printing messages (used during loading). */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /** Returns whether this task occurs on the supplied date. */
    public boolean inRange(LocalDate date) {
        return false;
    }
}
