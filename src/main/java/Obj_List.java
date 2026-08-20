/**
 * Stores the application's tasks in a fixed-size array.
 */
public class Obj_List {
    private int itemCount = 0;
    private final Item[] items;
    private final int capacity;

    public Obj_List(int capacity) {
        this.capacity = capacity;
        this.items = new Item[capacity];
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
            items[itemCount] = item;
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
            System.out.println(String.format("%d.%s", i + 1, items[i].toString()));
        }
    }

    /** Marks or unmarks the task at the supplied zero-based index. */
    public void mark(int index, boolean reverse) {
        if (index < 0 || index >= capacity) {
            System.out.println("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        } else {
            if (reverse) {
                items[index].undo();
            } else {
                items[index].mark();
            }
        }
    }
}
