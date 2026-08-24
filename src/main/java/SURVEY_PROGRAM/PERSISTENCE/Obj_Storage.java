package SURVEY_PROGRAM.PERSISTENCE;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.util.ArrayList;
import SURVEY_PROGRAM.INTERFACE.Secret;
import SURVEY_PROGRAM.OBJECT.*;

public class Obj_Storage {
    private static final Path SAVE_FILE = Path.of("data", "PERSIST.txt");
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;   // e.g., 2026-08-21T18:00

    public static void load(Obj_List lst, int capacity) {
        load(lst, capacity, SAVE_FILE);
    }

    /** Loads tasks from the supplied file path, primarily to support isolated tests. */
    public static void load(Obj_List lst, int capacity, Path saveFile) {
        ArrayList<Item> items = lst.getList();
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
                    lst.read();
                    break;
                }
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue; // malformed line
                String type = parts[0].trim();
                boolean done = parts[1].trim().equals("y");
                String desc = parts[2].trim();
                Item item;
                switch (type) {
                    case "T":
                        item = new Todo(desc);
                        break;
                    case "D":
                        if (parts.length < 4) continue;
                        LocalDateTime by = LocalDateTime.parse(parts[3].trim(), FILE_FORMATTER);
                        item = new Deadline(desc, by);
                        break;
                    case "E":
                        if (parts.length < 5) continue;
                        LocalDateTime from = LocalDateTime.parse(parts[3].trim(), FILE_FORMATTER);
                        LocalDateTime to = LocalDateTime.parse(parts[4].trim(), FILE_FORMATTER);
                        item = new Event(desc, from, to);
                        break;
                    default:
                        continue; // unknown type
                }
                item.setDone(done);
                items.add(item);
                itemCount++;
            }
        } catch (IOException e) {
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
            Deadline d = (Deadline) item;
            return type + " | " + done + " | " + d.getName() + " | "
                    + d.getBy().format(FILE_FORMATTER);
        } else if (item instanceof Event) {
            type = "E";
            Event e = (Event) item;
            return type + " | " + done + " | " + e.getName() + " | "
                    + e.getFrom().format(FILE_FORMATTER) + " | "
                    + e.getTo().format(FILE_FORMATTER);
        } else {
            // fallback (should not happen)
            return "? | " + done + " | " + item.getName();
        }
    }

    public static void save(Obj_List lst) {
        save(lst, SAVE_FILE);
    }

    /** Saves tasks to the supplied file path, primarily to support isolated tests. */
    public static void save(Obj_List lst, Path saveFile) {
        ArrayList<Item> items = lst.getList();
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
        } catch (IOException e) {
            Secret.error(false);
        }
    }
}
