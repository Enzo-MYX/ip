package SURVEY_PROGRAM.INTERFACE;

import SURVEY_PROGRAM.Device_G;

public class Secret {
    public static void run(String divider) throws InterruptedException {
        Thread.sleep(3000);
        System.out.println("\nGREETINGS.\n");
        Thread.sleep(3000);
        System.out.println("WE MEET ONCE MORE.\n");
        Thread.sleep(3000);
        System.out.println("WELL THEN,\n");
        Thread.sleep(2000);
        System.out.println("SHALL WE HASTEN?\n");
        Thread.sleep(2000);
        System.out.println(divider);
        System.out.println("\nNOW.\n");
        Thread.sleep(2000);
        System.out.println("LET US POPULATE THE CONTENTS\nOF THE DEVICE\nAS YOU WISH.\n");
        Thread.sleep(3000);
        System.out.println("*  (With repetition, you found yourself answering the machine more fluently.)");
        Thread.sleep(1000);
        System.out.println("*  (Seems like you know all the commands you need to.)");
        Thread.sleep(1000);
        System.out.println("*  (Still, you remembered you can say \"bye\" to leave!)");
        Device_G.boot();
        System.out.println(divider);
        System.out.println("\nTHEN, UNTIL WE MEET ONCE MORE.");
        System.out.println(divider);
        Thread.sleep(3000);
        System.out.println("\n(*  Well, there was not a man here.)");
    }

    public static void error(boolean saved) {
        try {
            System.out.println("CURIOUS.\n");
            Thread.sleep(3000);
            System.out.println("YOU MUST HAVE\nTAMPERED WITH SOMETHING\nYOU SHOULD NOT HAVE.\n");
            Thread.sleep(3000);
            System.out.println("DID YOU DO SUCH A THING?\n");
            Thread.sleep(3000);
            System.out.println("WELL. IN ANY CASE.\nTHIS DATA MUST BE DISCARDED.\n");
            Thread.sleep(3000);
            if (!saved) {
                System.out.print("TRY");
                Thread.sleep(1000);
                System.out.print(" TO BE");
                Thread.sleep(1000);
                System.out.println(" MORE CAUTIOUS");
                Thread.sleep(1000);
                System.out.println("NEXT TIME.");
            } else {
                System.out.println("HOWEVER.\n");
                Thread.sleep(3000);
                System.out.println("I HAVE SALVAGED\nSOME OF YOUR ANSWERS.\n");
                Thread.sleep(2000);
                System.out.println("DO WITH THIS\nAS YOU WILL.\n");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
