package SURVEY_PROGRAM.OBJECT;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public Obj_List(int capacity) {
        this.capacity = capacity;
    }
    
    public ArrayList<Item> getList() {
        return items;
    }

    /** Loads tasks from the save file, if it exists. */
    public void load() {
        Obj_Storage.load(this, capacity);
        itemCount = items.size();
    }

    /** Saves all tasks to the save file. */
    private void save() {
        Obj_Storage.save(this);
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