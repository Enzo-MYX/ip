package SURVEY_PROGRAM.OBJECT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Item {
    private final LocalDateTime by;
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

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
    public boolean inRange(LocalDate t) {
        return by.toLocalDate().equals(t);
    }
}