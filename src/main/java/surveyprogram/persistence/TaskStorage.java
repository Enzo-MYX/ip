package surveyprogram.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import surveyprogram.object.Deadline;
import surveyprogram.object.Event;
import surveyprogram.object.Item;
import surveyprogram.object.TaskList;
import surveyprogram.object.Todo;
import surveyprogram.ui.Secret;

/**
 * Serializes task lists to disk and reconstructs them from saved records.
 */
public class TaskStorage {
    private static final Path SAVE_FILE = Path.of("data", "PERSIST.txt");
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Loads tasks from the application's default save file.
     *
     * @param taskList task list that receives loaded items
     */
    public static void load(TaskList taskList) {
        load(taskList, SAVE_FILE);
    }

    /**
     * Loads tasks from a specified file, skipping malformed or unknown records.
     *
     * @param taskList task list that receives loaded items
     * @param saveFile file containing serialized task records
     */
    public static void load(TaskList taskList, Path saveFile) {
        File file = saveFile.toFile();
        if (!file.exists()) {
            return; // no previous data
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Optional<Item> parsedItem = parseItem(line);
                if (parsedItem.isEmpty()) {
                    continue;
                }
                if (!taskList.restore(parsedItem.get())) {
                    Secret.error(true);
                    break;
                }
            }
        } catch (IOException exception) {
            Secret.error(false);
        }
    }

    /** Returns a task reconstructed from a valid saved record. */
    private static Optional<Item> parseItem(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            return Optional.empty();
        }

        try {
            Item item = createItem(parts);
            if (item == null) {
                return Optional.empty();
            }
            item.setDone(parts[1].trim().equals("y"));
            return Optional.of(item);
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /** Returns the task encoded by the split record, or {@code null} for an unsupported record. */
    private static Item createItem(String[] parts) {
        String type = parts[0].trim();
        String description = parts[2].trim();
        return switch (type) {
            case "T" -> new Todo(description);
            case "D" -> parts.length < 4 ? null : new Deadline(description,
                    LocalDateTime.parse(parts[3].trim(), FILE_FORMATTER));
            case "E" -> parts.length < 5 ? null : new Event(description,
                    LocalDateTime.parse(parts[3].trim(), FILE_FORMATTER),
                    LocalDateTime.parse(parts[4].trim(), FILE_FORMATTER));
            default -> null;
        };
    }

    /**
     * Converts an item into the pipe-delimited representation used in the save file.
     *
     * @param item task to serialize
     * @return serialized task record
     */
    private static String itemToFileString(Item item) {
        String type;
        String done = item.isDone() ? "y" : "n";
        if (item instanceof Todo) {
            type = "T";
            return type + " | " + done + " | " + item.getName();
        } else if (item instanceof Deadline) {
            type = "D";
            Deadline deadline = (Deadline) item;
            return type + " | " + done + " | " + deadline.getName() + " | "
                    + deadline.getBy().format(FILE_FORMATTER);
        } else if (item instanceof Event) {
            type = "E";
            Event event = (Event) item;
            return type + " | " + done + " | " + event.getName() + " | "
                    + event.getFrom().format(FILE_FORMATTER) + " | "
                    + event.getTo().format(FILE_FORMATTER);
        } else {
            return "? | " + done + " | " + item.getName();
        }
    }

    /**
     * Writes all tasks to the application's default save file.
     *
     * @param taskList task list to save
     */
    public static void save(TaskList taskList) {
        save(taskList, SAVE_FILE);
    }

    /**
     * Writes all tasks to a specified file, creating parent directories when needed.
     *
     * @param taskList task list to save
     * @param saveFile destination for serialized task records
     */
    public static void save(TaskList taskList, Path saveFile) {
        List<Item> items = taskList.getList();

        try {
            File file = saveFile.toFile();
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs(); // ensure directory exists
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                items.stream()
                        .map(TaskStorage::itemToFileString)
                        .forEach(writer::println);
            }
        } catch (IOException exception) {
            Secret.error(false);
        }
    }
}
