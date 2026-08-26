package surveyprogram.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import surveyprogram.object.Deadline;
import surveyprogram.object.TaskList;
import surveyprogram.object.Todo;

/** Tests persistence parsing independently from the application's real save file. */
class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_leavesListEmpty() {
        TaskList list = new TaskList(10);

        TaskStorage.load(list, 10, temporaryDirectory.resolve("missing.txt"));

        assertEquals(0, list.getList().size());
    }

    @Test
    void load_malformedAndUnknownRecords_skipsThemAndLoadsValidRecords() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "missing separators",
                "Q | n | unknown",
                "D | n | missing date",
                "T | y | restored todo",
                "D | n | restored deadline | 2026-08-24T17:30:00"));
        TaskList list = new TaskList(10);

        TaskStorage.load(list, 10, file);

        assertEquals(2, list.getList().size());
        assertInstanceOf(Todo.class, list.getList().get(0));
        assertInstanceOf(Deadline.class, list.getList().get(1));
        assertEquals("restored todo", list.getList().get(0).getName());
        assertFalse(list.getList().get(1).isDone());
    }

    @Test
    void save_emptyList_createsEmptyFileAndParentDirectories() throws IOException {
        Path file = temporaryDirectory.resolve("deep/data/tasks.txt");

        TaskStorage.save(new TaskList(10), file);

        assertEquals("", Files.readString(file));
    }
}
