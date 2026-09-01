package surveyprogram.ui;

import surveyprogram.DeviceG;

/**
 * Displays the application's timed introductory sequence and persistence warnings.
 */
public class Secret {
    private static final String DIVIDER =
            "____________________________________________________________________________________";

    private Secret() {
        // This class contains only presentation utilities.
    }

    /**
     * Plays the introduction, starts the command interface, and displays the farewell.
     *
     * @throws InterruptedException if a timed pause is interrupted
     */
    public static void run() throws InterruptedException {
        Thread.sleep(3000);
        System.out.println("\nGREETINGS.\n");
        Thread.sleep(3000);
        System.out.println("WE MEET ONCE MORE.\n");
        Thread.sleep(3000);
        System.out.println("WELL THEN,\n");
        Thread.sleep(2000);
        System.out.println("SHALL WE HASTEN?\n");
        Thread.sleep(2000);
        System.out.println(DIVIDER);
        System.out.println("\nNOW.\n");
        Thread.sleep(2000);
        System.out.println("LET US POPULATE THE CONTENTS\nOF THE DEVICE\nAS YOU WISH.\n");
        Thread.sleep(3000);
        System.out.println("*  (With repetition, you found yourself answering the machine more fluently.)");
        Thread.sleep(1000);
        System.out.println("*  (Seems like you know all the commands you need to.)");
        Thread.sleep(1000);
        System.out.println("*  (Still, you remembered you can say \"bye\" to leave!)");
        DeviceG.boot();
        System.out.println(DIVIDER);
        System.out.println("\nTHEN, UNTIL WE MEET ONCE MORE.");
        System.out.println(DIVIDER);
        Thread.sleep(3000);
        System.out.println("\n(*  Well, there was not a man here.)");
    }

    /**
     * Displays a warning after stored data cannot be fully read or written.
     *
     * @param hasSavedRecords whether part of the stored data was successfully recovered
     */
    public static void error(boolean hasSavedRecords) {
        try {
            System.out.println("CURIOUS.\n");
            Thread.sleep(3000);
            System.out.println("YOU MUST HAVE\nTAMPERED WITH SOMETHING\nYOU SHOULD NOT HAVE.\n");
            Thread.sleep(3000);
            System.out.println("DID YOU DO SUCH A THING?\n");
            Thread.sleep(3000);
            System.out.println("WELL. IN ANY CASE.\nTHIS DATA MUST BE DISCARDED.\n");
            Thread.sleep(3000);
            if (!hasSavedRecords) {
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
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Returns the introductory dialogue without timed pauses.
     *
     * @return introductory dialogue for the GUI
     */
    public static String getOpeningDialogue() {
        return """
            GREETINGS.

            WE MEET ONCE MORE.

            WELL THEN,

            SHALL WE HASTEN?

            NOW.

            LET US POPULATE THE CONTENTS
            OF THE DEVICE
            AS YOU WISH.

            *  (With repetition, you found yourself answering the machine more fluently.)
            *  (Seems like you know all the commands you need to.)
            *  (Still, you remembered you can say "bye" to leave!)
            """.strip();
    }

    /**
     * Returns the farewell dialogue without timed pauses.
     *
     * @return farewell dialogue for the GUI
     */
    public static String getClosingDialogue() {
        return """
            THEN, UNTIL WE MEET ONCE MORE.

            (*  Well, there was not a man here.)
            """.strip();
    }
}
