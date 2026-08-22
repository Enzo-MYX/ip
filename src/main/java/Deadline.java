/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Item {
    private final String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    /** Returns the deadline date/time string. */
    public String getBy() {
        return by;
    }
}