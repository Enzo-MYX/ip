import java.util.Scanner;

public class Secret {
    public static void run(String divider) throws InterruptedException {
        Thread.sleep(3000);
//        System.out.println("\nWELCOME.\n");
//        Thread.sleep(3000);
//        System.out.println("HAVE YOU BEEN\nLOOKING FOR ME?\n");
//        Thread.sleep(3000);
//        System.out.println("HOW WONDERFUL.\n");
//        Thread.sleep(3000);
//        System.out.println("I\nHAVE BEEN LOOKING FOR YOU\nAS WELL.\n");
        System.out.println("\nGREETINGS.\n");
        Thread.sleep(3000);
        System.out.println("WE MEET ONCE MORE.\n");
        Thread.sleep(3000);
        System.out.println("WELL THEN,\n");
        Thread.sleep(2000);
        System.out.println("SHALL WE HASTEN?\n");
        Thread.sleep(2000);
        System.out.println(divider);
        System.out.println("NOW.\n");
        Thread.sleep(2000);
        System.out.println("LET US SHAPE ITS MIND\nAS YOUR OWN.\n");
        Thread.sleep(3000);
        System.out.println("*  (You felt a strange feeling, it was as if...)");
        Thread.sleep(1000);
        System.out.println("*  (You found a voice to call out to the strange device with.)");
        Thread.sleep(1000);
        System.out.println("*  (Type ANYTHING to the device, use \"list\" to revisit what you typed.) \n*  (Or, say \"bye\" to leave!)");
//        Thread.sleep(3000);
//        System.out.println("STILL.\n");
//        Thread.sleep(1000);
//        System.out.println("I HAVE A FEELING THAT WE WILL MEET AGAIN");
//        Thread.sleep(800);
//        System.out.println("VERY,");
//        Thread.sleep(800);
//        System.out.println("VERY,");
//        Thread.sleep(800);
//        System.out.println("SOON.");
        Obj_List list = new Obj_List(5);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                if (input.toLowerCase().equals("bye")) {;
                    break;
                } else if (input.toLowerCase().equals("list")) {
                    System.out.println(divider);
                    System.out.println("VERY WELL. HERE IS YOUR LIST:");
                    list.read();
                } else {
                    System.out.println(divider);
                    list.add(input);
                }
                System.out.println(divider);
            }
        }
        System.out.println(divider);
        System.out.println("\nGOOD BY!");
        System.out.println(divider);
        Thread.sleep(3000);
        System.out.println("\n(*  Well, there was not a man here.)");
    }
}
