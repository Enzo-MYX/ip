package survey_program.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import survey_program.object.Deadline;
import survey_program.object.Event;
import survey_program.object.Item;
import survey_program.object.TaskList;
import survey_program.object.Todo;

/** Tests command recognition, parsing, fallback behavior, and routing. */
class TaskCommandProcessorTest {
    private static final String DIVIDER = "---";

    @Test
    void process_byeWithWhitespaceAndMixedCase_returnsFalse() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list, DIVIDER);

        assertFalse(processor.process("  ByE  "));
        assertTrue(list.addedItems.isEmpty());
    }

    @Test
    void process_supportedDeadlineFormats_createEquivalentDeadlines() {
        List<String> dateInputs = List.of(
                "2026-8-24 17:30", "2026-8-24 1730",
                "2026/8/24 17:30", "2026/8/24 1730",
                "24-8-2026 17:30", "24-8-2026 1730",
                "24/8/2026 17:30", "24/8/2026 1730");
        LocalDateTime expected = LocalDateTime.of(2026, 8, 24, 17, 30);

        for (String dateInput : dateInputs) {
            RecordingList list = new RecordingList();
            new TaskCommandProcessor(list, DIVIDER)
                    .process("deadline submit report /by " + dateInput);

            Deadline deadline = assertInstanceOf(Deadline.class, list.addedItems.getFirst());
            assertEquals(expected, deadline.getBy(), dateInput);
        }
    }

    @Test
    void process_dateWithoutTime_defaultsToStartOfDay() {
        RecordingList list = new RecordingList();

        new TaskCommandProcessor(list, DIVIDER).process("deadline submit /by 2026-8-24");

        Deadline deadline = assertInstanceOf(Deadline.class, list.addedItems.getFirst());
        assertEquals(LocalDateTime.of(2026, 8, 24, 0, 0), deadline.getBy());
    }

    @Test
    void process_validEvent_parsesBothEndpoints() {
        RecordingList list = new RecordingList();

        new TaskCommandProcessor(list, DIVIDER)
                .process("event workshop /from 2026-8-24 0900 /to 25-8-2026 17:30");

        Event event = assertInstanceOf(Event.class, list.addedItems.getFirst());
        assertEquals(LocalDateTime.of(2026, 8, 24, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 25, 17, 30), event.getTo());
    }

    @Test
    void process_invalidDatedCommands_fallBackToTodo() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list, DIVIDER);

        String deadlineOutput = captureOutput(() -> processor.process("deadline submit /by someday"));
        String eventOutput = captureOutput(() -> processor.process("event meeting /from now"));

        assertInstanceOf(Todo.class, list.addedItems.get(0));
        assertInstanceOf(Todo.class, list.addedItems.get(1));
        assertTrue(deadlineOutput.contains("YOU MUST BE"));
        assertTrue(eventOutput.contains("YOU MUST BE"));
    }

    @Test
    void process_dateCommand_routesValidDateAndRejectsInvalidDate() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list, DIVIDER);

        processor.process("date 24/8/2026");
        String invalidOutput = captureOutput(() -> processor.process("date yesterday"));

        assertEquals(LocalDate.of(2026, 8, 24), list.requestedDate);
        assertTrue(invalidOutput.contains("BUT, THE DATE IS INVALID."));
    }

    @Test
    void process_markUnmarkDelete_routesOneBasedIndexesAndRejectsNonNumbers() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list, DIVIDER);

        processor.process("mark 2");
        assertEquals(1, list.markedIndex);
        assertFalse(list.isReverseMark);
        processor.process("unmark 1");
        assertEquals(0, list.markedIndex);
        assertTrue(list.isReverseMark);
        processor.process("delete 3");
        assertEquals(2, list.deletedIndex);
        assertTrue(captureOutput(() -> processor.process("delete no"))
                .contains("BUT, IT IS INVALID."));
    }

    @Test
    void process_unknownAndListCommands_printExpectedResponses() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list, DIVIDER);

        assertTrue(captureOutput(() -> processor.process("list")).contains("VERY WELL. HERE IS YOUR LIST:"));
        assertTrue(list.isReadCalled);
        assertTrue(captureOutput(() -> processor.process("unknown"))
                .contains("WELL, THAT IS NO LONGER A COMMAND."));
    }

    /** Minimal list double that records processor calls without touching persistence. */
    private static class RecordingList extends TaskList {
        private final ArrayList<Item> addedItems = new ArrayList<>();
        private boolean isReadCalled;
        private int markedIndex = Integer.MIN_VALUE;
        private boolean isReverseMark;
        private int deletedIndex = Integer.MIN_VALUE;
        private LocalDate requestedDate;

        RecordingList() {
            super(100);
        }

        @Override
        public void add(Item item) {
            addedItems.add(item);
        }

        @Override
        public void read() {
            isReadCalled = true;
        }

        @Override
        public void mark(int index, boolean isReverse) {
            markedIndex = index;
            isReverseMark = isReverse;
        }

        @Override
        public void delete(int index) {
            deletedIndex = index;
        }

        @Override
        public void listByDate(LocalDate date) {
            requestedDate = date;
        }
    }

    private static String captureOutput(Runnable action) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
