package surveyprogram.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests task-list validation, mutation, filtering, and persistence integration. */
class TaskListTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void add_emptyAndOverCapacity_rejectsInvalidItems() {
        TaskList list = newList(1);

        String emptyOutput = captureOutput(() -> list.add(new Todo("   ")));
        list.add(new Todo("first"));
        String fullOutput = captureOutput(() -> list.add(new Todo("second")));

        assertEquals(1, list.getList().size());
        assertTrue(emptyOutput.contains("IT IS BARREN AND CANNOT BE CREATED."));
        assertTrue(fullOutput.contains("BUT, THERE IS NO MORE MEMORY TO ALLOCATE."));
    }

    @Test
    void mark_validAndInvalidIndexes_updatesOnlyValidItem() {
        TaskList list = newList(2);
        list.add(new Todo("first"));

        list.mark(0, false);
        String invalidOutput = captureOutput(() -> list.mark(-1, false));

        assertTrue(list.getList().getFirst().isDone());
        assertTrue(invalidOutput.contains("BUT, IT WAS NEVER THERE IN THE FIRST PLACE."));
    }

    @Test
    void delete_validAndInvalidIndexes_removesOnlyValidItem() {
        TaskList list = newList(2);
        list.add(new Todo("first"));
        list.add(new Todo("second"));

        list.delete(0);
        String invalidOutput = captureOutput(() -> list.delete(5));

        assertEquals(1, list.getList().size());
        assertEquals("second", list.getList().getFirst().getName());
        assertTrue(invalidOutput.contains("BUT, IT WAS NEVER THERE IN THE FIRST PLACE."));
    }

    @Test
    void read_emptyAndPopulatedList_printsExpectedNumbering() {
        TaskList list = newList(2);
        assertTrue(captureOutput(list::read).contains("BUT, THERE WAS NOTHING TO READ."));

        list.add(new Todo("first"));
        list.add(new Todo("second"));
        String output = captureOutput(list::read);

        assertTrue(output.contains("1.[T][ ] first"));
        assertTrue(output.contains("2.[T][ ] second"));
    }

    @Test
    void find_matchingMissingAndBlankKeywords_printsExpectedResponses() {
        TaskList list = newList(3);
        list.add(new Todo("read book"));
        list.add(new Todo("buy groceries"));
        list.add(new Deadline("return BOOK", LocalDateTime.of(2026, 6, 6, 0, 0)));

        String matchingOutput = captureOutput(() -> list.find("book"));
        String missingOutput = captureOutput(() -> list.find("phone"));
        String blankOutput = captureOutput(() -> list.find("   "));

        assertTrue(matchingOutput.contains("VERY WELL. HERE IS YOUR MATCHING LIST:"));
        assertTrue(matchingOutput.contains("1.[T][ ] read book"));
        assertTrue(matchingOutput.contains("2.[D][ ] return BOOK"));
        assertFalse(matchingOutput.contains("buy groceries"));
        assertTrue(missingOutput.contains("BUT, THERE WAS NOTHING THAT CONFORMS TO THE TERM."));
        assertTrue(blankOutput.contains("BUT, THERE WAS NOTHING TO LOCATE."));
    }

    @Test
    void listByDate_mixedTasks_printsOnlyMatchingDatedTasks() {
        TaskList list = newList(3);
        list.add(new Todo("undated"));
        list.add(new Deadline("due", LocalDateTime.of(2026, 8, 24, 12, 0)));
        list.add(new Event("trip", LocalDateTime.of(2026, 8, 23, 8, 0),
                LocalDateTime.of(2026, 8, 25, 18, 0)));

        String output = captureOutput(() -> list.listByDate(LocalDate.of(2026, 8, 24)));

        assertFalse(output.contains("undated"));
        assertTrue(output.contains("due"));
        assertTrue(output.contains("trip"));
        assertTrue(captureOutput(() -> list.listByDate(LocalDate.of(2026, 8, 26)))
                .contains("NOTHING OF CONCERN"));
    }

    @Test
    void load_afterMutations_restoresAllTypesAndCompletionState() {
        Path saveFile = temporaryDirectory.resolve("nested/tasks.txt");
        TaskList original = new TaskList(5, saveFile);
        original.add(new Todo("todo"));
        original.add(new Deadline("deadline", LocalDateTime.of(2026, 8, 24, 17, 30)));
        original.add(new Event("event", LocalDateTime.of(2026, 8, 24, 9, 0),
                LocalDateTime.of(2026, 8, 25, 10, 0)));
        original.mark(1, false);

        TaskList restored = new TaskList(5, saveFile);
        restored.load();

        assertEquals(3, restored.getList().size());
        assertInstanceOf(Todo.class, restored.getList().get(0));
        assertInstanceOf(Deadline.class, restored.getList().get(1));
        assertInstanceOf(Event.class, restored.getList().get(2));
        assertTrue(restored.getList().get(1).isDone());
    }

    private TaskList newList(int capacity) {
        return new TaskList(capacity, temporaryDirectory.resolve("tasks.txt"));
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
