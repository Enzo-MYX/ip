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

    /**
     * Creates an event with an inclusive start and end date-time.
     *
     * @param description event description
     * @param from event start
     * @param to event end
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's display form, including its range and completion state.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(OUTPUT_FORMATTER) + " to: " + to.format(OUTPUT_FORMATTER) + ")";
    }

    /**
     * Returns the event start.
     *
     * @return start date-time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end.
     *
     * @return end date-time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Checks whether a date falls within the event's inclusive date range.
     *
     * @param t date to check
     * @return {@code true} when the date is between the start and end dates, inclusive
     */
    @Override
    public boolean inRange(LocalDate t) {
        return !from.toLocalDate().isAfter(t) && !to.toLocalDate().isBefore(t);
    }
}
