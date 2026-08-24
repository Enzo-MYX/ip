package SURVEY_PROGRAM.OBJECT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Item {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(OUTPUT_FORMATTER) + " to: " + to.format(OUTPUT_FORMATTER) + ")";
    }

    /** Returns the start date/time string. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the end date/time string. */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public boolean inRange(LocalDate t) {
        return !from.toLocalDate().isAfter(t) && !to.toLocalDate().isBefore(t);
    }
}