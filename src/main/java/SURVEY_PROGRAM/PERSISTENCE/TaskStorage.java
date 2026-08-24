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

/** Reads and writes task lists using the application's text-file format. */
public class TaskStorage {
    private static final Path SAVE_FILE = Path.of("data", "PERSIST.txt");
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /** Loads tasks from the default save file. */
    public static void load(TaskList taskList, int capacity) {
        load(taskList, capacity, SAVE_FILE);
    }

    /** Loads tasks from the supplied file path, primarily to support isolated tests. */
    public static void load(TaskList taskList, int capacity, Path saveFile) {
        ArrayList<Item> items = taskList.getList();
        int itemCount = 0;
        File file = saveFile.toFile();
        if (!file.exists()) {
            return;
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

    /** Converts a single item to its file representation. */
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

    /** Saves tasks to the default save file. */
    public static void save(TaskList taskList) {
        save(taskList, SAVE_FILE);
    }

    /** Saves tasks to the supplied file path, primarily to support isolated tests. */
    public static void save(TaskList taskList, Path saveFile) {
        ArrayList<Item> items = taskList.getList();
        int itemCount = items.size();
        try {
            File file = saveFile.toFile();
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
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
