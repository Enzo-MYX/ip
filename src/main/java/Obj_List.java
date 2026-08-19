public class Obj_List {
    private int items = 0;
    private final String[] l;
    private final int len;

    public Obj_List(int i) {
        len = i;
        l = new String[i];
    }

    public void add(String s) {
        if (items >= len) {
            System.out.println("BUT, THE STORAGE WAS FULL.");
        } else {
            l[items] = s;
            items++;
            System.out.println("ORDER PROCESSED: " + s.toUpperCase());
        }
    }

    public void read() {
        if (items == 0) System.out.println("BUT, THERE WAS NOTHING TO READ");
        else {
            for (int i = 0; i < items; i++) {
                System.out.println(String.format("%d. %s", i+1, l[i]));
            }
        }
    }
}