public class Obj_List {
    private int items = 0;
    private final Item[] l;
    private final int len;

    public Obj_List(int i) {
        len = i;
        l = new Item[i];
    }

    public void add(String s) {
        if (items >= len) {
            System.out.println("BUT, THE STORAGE WAS FULL.");
        } else {
            l[items] = new Item(s);
            items++;
            System.out.println("ORDER PROCESSED: " + s.toUpperCase());
        }
    }

    public void read() {
        if (items == 0) System.out.println("BUT, THERE WAS NOTHING TO READ");
        else {
            for (int i = 0; i < items; i++) {
                System.out.println(String.format("%d.%s", i+1, l[i].toString()));
            }
        }
    }

    public void mark(int index, boolean reverse) {
        if (index < 0 || index >= len) {
            System.out.println("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        } else {
            if (reverse) {
                l[index].undo();
            } else {
                l[index].mark();
            }
        }
    }
}
