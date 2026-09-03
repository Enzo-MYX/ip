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
    public String add(Item item) {
        if (itemCount >= capacity) {
            return "BUT, THERE IS NO MORE MEMORY TO ALLOCATE.";
        }
        if (item.getName().trim().isEmpty()) {
            return "IT IS BARREN AND CANNOT BE CREATED.";
        }

        items.add(item);
        itemCount++;
        save(); // persist after addition
        return "ORDER PROCESSED: " + item.toString().toUpperCase();
    }

    /**
     * Returns all stored tasks in their insertion order.
     *
     * @return formatted task list, or a message when the list is empty
     */
    public String read() {
        if (itemCount == 0) {
            return "BUT, THERE WAS NOTHING TO READ.";
        }
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < itemCount; i++) {
            response.append(String.format("%d.%s%n", i + 1, items.get(i)));
        }
        return response.toString().stripTrailing();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to find in task descriptions
     * @return matching tasks or a message explaining why none can be shown
     */
    public String find(String keyword) {
        if (keyword.isBlank()) {
            return "BUT, THERE WAS NOTHING TO LOCATE.";
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Item> matchingItems = items.stream()
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
        if (matchingItems.isEmpty()) {
            return "BUT, THERE WAS NOTHING THAT CONFORMS TO THE TERM.";
        }
        StringBuilder response = new StringBuilder("VERY WELL. HERE IS YOUR MATCHING LIST:\n");
        for (int i = 0; i < matchingItems.size(); i++) {
            response.append(String.format("%d.%s%n", i + 1, matchingItems.get(i)));
        }
        return response.toString().stripTrailing();
    }

    /**
     * Marks or unmarks the task at the supplied zero-based index.
     *
     * @param index zero-based index of the task
     * @param isReverse {@code true} to unmark the task, or {@code false} to mark it
     */
    public String mark(int index, boolean isReverse) {
        if (index < 0 || index >= itemCount) {
            return "BUT, IT WAS NEVER THERE IN THE FIRST PLACE.";
        }

        String response = isReverse ? items.get(index).undo() : items.get(index).mark();
        save(); // persist status change
        return response;
    }

    /**
     * Removes and saves the task at a zero-based index.
     *
     * @param index zero-based index of the task to remove
     */
    public String delete(int index) {
        if (index < 0 || index >= itemCount) {
            return "BUT, IT WAS NEVER THERE IN THE FIRST PLACE.";
        }

        String deletedItem = items.get(index).toString();
        items.remove(index);
        itemCount--;
        save(); // persist after deletion
        return deletedItem + "\nIT WAS AS IF IT WAS NEVER THERE\nAT ALL.";
    }

    /**
     * Prints deadlines and events that occur on a specified date.
     *
     * @param date date used to filter tasks
     */
    public String listByDate(LocalDate date) {
        List<Item> matchingItems = items.stream().filter(item -> item.inRange(date)).toList();
        if (matchingItems.isEmpty()) {
            return "WELL, THERE IS NOTHING OF CONCERN ON THIS SPECIFIC DATE.";
        }

        StringBuilder response = new StringBuilder("WE SIT ON THE PRECIPICE OF THESE EVENTS:\n\n");
        for (Item item : matchingItems) {
            response.append(item).append(System.lineSeparator());
        }
        return response.toString().stripTrailing();
    }
}
