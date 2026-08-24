package survey_program.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import survey_program.object.Deadline;
import survey_program.object.Event;
import survey_program.object.Item;
import survey_program.object.TaskList;
import survey_program.object.Todo;
import survey_program.ui.Secret;

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
     * @param capacity maximum number of items to load
     */
    public static void load(TaskList taskList, int capacity) {
        load(taskList, capacity, SAVE_FILE);
    }

    /**
     * Loads tasks from a specified file, skipping malformed or unknown records.
     *
     * @param taskList task list that receives loaded items
     * @param capacity maximum number of items to load
     * @param saveFile file containing serialized task records
     */
    public static void load(TaskList taskList, int capacity, Path saveFile) {
        ArrayList<Item> items = taskList.getList();
        int itemCount = 0;
        File file = saveFile.toFile();
        if (!file.exists()) {
            return; // no previous data
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (itemCount >= capacity) {
                    Secret.error(true);
                    taskList.read();
                    break;
                }
                String[] parts = line.split("\\|");
                if (parts.length < 3) {
                    continue;
                }
                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("y");
                String description = parts[2].trim();
                Item item;
                switch (type) {
                    case "T":
                        item = new Todo(description);
                        break;
                    case "D":
                        if (parts.length < 4) {
                            continue;
                        }
                        LocalDateTime deadline = LocalDateTime.parse(parts[3].trim(), FILE_FORMATTER);
                        item = new Deadline(description, deadline);
                        break;
                    case "E":
                        if (parts.length < 5) {
                            continue;
                        }
                        LocalDateTime from = LocalDateTime.parse(parts[3].trim(), FILE_FORMATTER);
                        LocalDateTime to = LocalDateTime.parse(parts[4].trim(), FILE_FORMATTER);
                        item = new Event(description, from, to);
                        break;
                    default:
                        continue;
                }
                item.setDone(isDone);
                items.add(item);
                itemCount++;
            }
        } catch (IOException exception) {
            Secret.error(false);
        }
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
        ArrayList<Item> items = taskList.getList();
        int itemCount = items.size();
        try {
            File file = saveFile.toFile();
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs(); // ensure directory exists
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (int i = 0; i < itemCount; i++) {
                    writer.println(itemToFileString(items.get(i)));
                }
            }
        } catch (IOException exception) {
            Secret.error(false);
        }
    }
}
