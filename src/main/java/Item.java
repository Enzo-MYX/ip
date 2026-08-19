public class Item {
    private final String name;
    private boolean done = false;

    public Item(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", done ? "X" : " ", name);
    }

    public void mark() {
        if (done) {
            System.out.println("BUT, IT WAS ALREADY DONE.");
        } else {
            done = true;
            System.out.println("THEN, IT IS DONE.");
            System.out.println(this);
        }
    }

    public void undo() {
        if (done) {
            done = false;
            System.out.println("THEN, IT WAS AS IF IT WAS NEVER DONE.");
            System.out.println(this);
        } else {
            System.out.println("BUT, YOU HAVEN'T DONE IT IN THE FIRST PLACE.");
        }
    }
}
