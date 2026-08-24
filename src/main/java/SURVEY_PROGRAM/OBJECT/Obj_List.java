package SURVEY_PROGRAM.OBJECT;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import SURVEY_PROGRAM.PERSISTENCE.Obj_Storage;

/**
 * Stores the application's tasks in a fixed-size array.
 */
public class Obj_List {
    private int itemCount = 0;
    private final ArrayList<Item> items = new ArrayList<>();
    private final int capacity;
    private final Path saveFile;

    /**
     * Creates a task list that uses the default save file.
     *
     * @param capacity maximum number of tasks the list can contain
     */
    public Obj_List(int capacity) {
        this(capacity, Path.of("data", "PERSIST.txt"));
    }

    /**
     * Creates a task list backed by a specific save file.
     *
     * @param capacity maximum number of tasks the list can contain
     * @param saveFile file used to load and save tasks
     */
    Obj_List(int capacity, Path saveFile) {
        this.capacity = capacity;
        this.saveFile = saveFile;
    }
    
    /**
     * Returns the mutable collection used to store tasks.
     *
     * @return stored tasks
     */
    public ArrayList<Item> getList() {
        return items;
    }

    /** Loads tasks from the save file, if it exists. */
    public void load() {
        Obj_Storage.load(this, capacity, saveFile);
        itemCount = items.size();
    }

    /** Saves all current tasks and completion states to the configured file. */
    private void save() {
        Obj_Storage.save(this, saveFile);
    }

    /**
     * Adds and saves a non-empty task when storage remains available.
     *
     * @param item task to add
     */
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

    /**
     * Marks or unmarks the task at the supplied zero-based index.
     *
     * @param index zero-based index of the task
     * @param reverse {@code true} to unmark the task, or {@code false} to mark it
     */
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

    /**
     * Removes and saves the task at a zero-based index.
     *
     * @param index zero-based index of the task to remove
     */
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

    /**
     * Prints deadlines and events that occur on a specified date.
     *
     * @param date date used to filter tasks
     */
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
