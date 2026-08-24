package survey_program.object;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Item {
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    private final LocalDateTime by;

    /** Creates a deadline with its description and due date/time. */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMATTER) + ")";
    }

    /** Returns the deadline date/time string. */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public boolean inRange(LocalDate date) {
        return by.toLocalDate().equals(date);
    }
}
