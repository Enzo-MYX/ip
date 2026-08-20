import java.util.ArrayList;

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
        }
    }
}
