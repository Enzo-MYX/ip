package surveyprogram.object;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import surveyprogram.persistence.TaskStorage;

/**
 * Stores the application's tasks in a fixed-size array.
 */
public class TaskList {
    private int itemCount = 0;
    private final ArrayList<Item> items = new ArrayList<>();
    private final int capacity;
    private final Path saveFile;

    /**
     * Creates a task list that uses the default save file.
     *
     * @param capacity maximum number of tasks the list can contain
     */
    public TaskList(int capacity) {
        this(capacity, Path.of("data", "PERSIST.txt"));
    }

    /**
     * Creates a task list backed by a specific save file.
     *
     * @param capacity maximum number of tasks the list can contain
     * @param saveFile file used to load and save tasks
     */
    TaskList(int capacity, Path saveFile) {
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
        TaskStorage.load(this, capacity, saveFile);
        itemCount = items.size();
    }

    /** Saves all current tasks and completion states to the configured file. */
    private void save() {
        TaskStorage.save(this, saveFile);
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

    /** Prints tasks whose descriptions contain the keyword, ignoring letter case. */
    public void find(String keyword) {
        if (keyword.isBlank()) {
            System.out.println("BUT, THERE WAS NOTHING TO LOCATE.");
            return;
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Item> matchingItems = items.stream()
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
        if (matchingItems.isEmpty()) {
            System.out.println("BUT, THERE WAS NOTHING THAT CONFORMS TO THE TERM.");
            return;
        }
        System.out.println("VERY WELL. HERE IS YOUR MATCHING LIST:");
        for (int i = 0; i < matchingItems.size(); i++) {
            System.out.printf("%d.%s%n", i + 1, matchingItems.get(i));
        }
    }

    /**
     * Marks or unmarks the task at the supplied zero-based index.
     *
     * @param index zero-based index of the task
     * @param isReverse {@code true} to unmark the task, or {@code false} to mark it
     */
    public void mark(int index, boolean isReverse) {
        if (index < 0 || index >= itemCount) {
            System.out.println("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        } else {
            if (isReverse) {
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
        List<Item> matchingItems = items.stream().filter(item -> item.inRange(date)).toList();
        if (matchingItems.isEmpty()) {
            System.out.println("WELL, THERE IS NOTHING OF CONCERN ON THIS SPECIFIC DATE.");
        } else {
            System.out.println("WE SIT ON THE PRECIPICE OF THESE EVENTS:\n");
            for (Item item : matchingItems) {
                System.out.println(item.toString());
            }
        }
    }
}
