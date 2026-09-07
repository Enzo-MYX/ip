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
     * Returns an immutable snapshot of the stored tasks.
     *
     * @return stored tasks
     */
    public List<Item> getList() {
        return List.copyOf(items);
    }

    /**
     * Restores a task without saving it again.
     *
     * @param item task reconstructed from storage
     * @return {@code true} if the task was restored, or {@code false} if the list is full
     */
    public boolean restore(Item item) {
        if (itemCount >= capacity) {
            return false;
        }

        items.add(item);
        itemCount++;
        assert itemCount == items.size();
        return true;
    }

    /** Loads tasks from the save file, if it exists. */
    public void load() {
        TaskStorage.load(this, saveFile);
        assert itemCount == items.size();
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
    public TaskListResult add(Item item) {
        if (itemCount >= capacity) {
            return error("BUT, THERE IS NO MORE MEMORY TO ALLOCATE.");
        }
        if (item.getName().trim().isEmpty()) {
            return error("IT IS BARREN AND CANNOT BE CREATED.");
        }

        items.add(item);
        itemCount++;
        assert itemCount == items.size();
        save(); // persist after addition
        return success("ORDER PROCESSED: " + item.toString().toUpperCase());
    }

    /**
     * Returns all stored tasks in their insertion order.
     *
     * @return formatted task list, or a message when the list is empty
     */
    public TaskListResult read() {
        if (itemCount == 0) {
            return empty("BUT, THERE WAS NOTHING TO READ.");
        }
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < itemCount; i++) {
            response.append(String.format("%d.%s%n", i + 1, items.get(i)));
        }
        return success(response.toString().stripTrailing());
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to find in task descriptions
     * @return matching tasks or a message explaining why none can be shown
     */
    public TaskListResult find(String keyword) {
        if (keyword.isBlank()) {
            return empty("BUT, THERE WAS NOTHING TO LOCATE.");
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Item> matchingItems = items.stream()
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
        if (matchingItems.isEmpty()) {
            return empty("BUT, THERE WAS NOTHING THAT CONFORMS TO THE TERM.");
        }
        StringBuilder response = new StringBuilder("VERY WELL. HERE IS YOUR MATCHING LIST:\n");
        for (int i = 0; i < matchingItems.size(); i++) {
            response.append(String.format("%d.%s%n", i + 1, matchingItems.get(i)));
        }
        return success(response.toString().stripTrailing());
    }

    /**
     * Marks or unmarks the task at the supplied zero-based index.
     *
     * @param index zero-based index of the task
     * @param isReverse {@code true} to unmark the task, or {@code false} to mark it
     */
    public TaskListResult mark(int index, boolean isReverse) {
        if (index < 0 || index >= itemCount) {
            return error("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        }

        String response = isReverse ? items.get(index).undo() : items.get(index).mark();
        save(); // persist status change
        return success(response);
    }

    /**
     * Removes and saves the task at a zero-based index.
     *
     * @param index zero-based index of the task to remove
     */
    public TaskListResult delete(int index) {
        if (index < 0 || index >= itemCount) {
            return error("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        }

        String deletedItem = items.get(index).toString();
        items.remove(index);
        itemCount--;
        assert itemCount == items.size();
        save(); // persist after deletion
        return success(deletedItem + "\nIT WAS AS IF IT WAS NEVER THERE\nAT ALL.");
    }

    /**
     * Prints deadlines and events that occur on a specified date.
     *
     * @param date date used to filter tasks
     */
    public TaskListResult listByDate(LocalDate date) {
        List<Item> matchingItems = items.stream().filter(item -> item.inRange(date)).toList();
        if (matchingItems.isEmpty()) {
            return empty("WELL, THERE IS NOTHING OF CONCERN ON THIS SPECIFIC DATE.");
        }

        StringBuilder response = new StringBuilder("WE SIT ON THE PRECIPICE OF THESE EVENTS:\n\n");
        for (Item item : matchingItems) {
            response.append(item).append(System.lineSeparator());
        }
        return success(response.toString().stripTrailing());
    }

    private TaskListResult success(String response) {
        return new TaskListResult(response, TaskListResult.Status.SUCCESS);
    }

    private TaskListResult empty(String response) {
        return new TaskListResult(response, TaskListResult.Status.EMPTY);
    }

    private TaskListResult error(String response) {
        return new TaskListResult(response, TaskListResult.Status.ERROR);
    }
}
