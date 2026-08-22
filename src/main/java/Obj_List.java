import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores the application's tasks in a fixed-size array.
 */
public class Obj_List {
    private static final String SAVE_FILE = "./data/PERSIST.txt";
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;   // e.g., 2026-08-21T18:00

    private int itemCount = 0;
    private final ArrayList<Item> items = new ArrayList<>();
    private final int capacity;

    public Obj_List(int capacity) {
        this.capacity = capacity;
    }

    /** Loads tasks from the save file, if it exists. */
    public void load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return; // no previous data
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (itemCount >= capacity) {
                    Secret.error(true);
                    read();
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

    /** Saves all tasks to the save file. */
    private void save() {
        try {
            File file = new File(SAVE_FILE);
            file.getParentFile().mkdirs(); // ensure directory exists
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (int i = 0; i < itemCount; i++) {
                    writer.println(itemToFileString(items.get(i)));
                }
            }
        } catch (IOException e) {
            Secret.error(false);
        }
    }

    /** Converts a single item to its file representation. */
    private String itemToFileString(Item item) {
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

    /** Adds a non-empty task when storage remains available. */
    public void add(Item item) {
        if (itemCount >= capacity) {
            System.out.println("BUT, THERE IS NO MORE MEMORY TO ALLOCATE.");
        } else {
            if (item.getName().trim().isEmpty()) {
                System.out.println("IT IS BARREN AND CANNOT BE CREATED.");
                return;
            }
            items.add(item);
            itemCount++;
            System.out.println("ORDER PROCESSED: " + item.toString().toUpperCase());
            save(); // persist after addition
        }
    }

    /** Prints all stored tasks in their insertion order. */
    public void read() {
        if (itemCount == 0) {
            System.out.println("BUT, THERE WAS NOTHING TO READ.");
            return;
        }
        for (int i = 0; i < itemCount; i++) {
            System.out.printf("%d.%s%n", i + 1, items.get(i).toString());
        }
    }

    /** Marks or unmarks the task at the supplied zero-based index. */
    public void mark(int index, boolean reverse) {
        if (index < 0 || index >= itemCount) {
            System.out.println("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        } else {
            if (reverse) {
                items.get(index).undo();
            } else {
                items.get(index).mark();
            }
            save(); // persist status change
        }
    }

    public void delete(int index) {
        if (index < 0 || index >= itemCount) {
            System.out.println("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        } else {
            System.out.println(items.get(index));
            items.remove(index);
            itemCount--;
            System.out.println("IT WAS AS IF IT WAS NEVER THERE\nAT ALL.");
            save(); // persist after deletion
        }
    }

    // ---------- New: list tasks on a specific date ----------
    public void listByDate(LocalDate date) {
        List<Item> filt = items.stream().filter(item -> item.inRange(date)).toList();
        if (filt.isEmpty()) {
            System.out.println("WELL, THERE IS NOTHING OF CONCERN ON THIS SPECIFIC DATE.");
        } else {
            System.out.println("WE SIT ON THE PRECIPICE OF THESE EVENTS:\n");
            for (Item item : filt) {
                System.out.println(item.toString());
            }
        }
    }
}