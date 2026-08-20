/**
 * Starts the application and owns its presentation-only introduction.
 */
public class Device_G {
    private static final String DIVIDER = "____________________________________________________________________________________";

    /**
     * Displays the introduction before handing control to the command interface.
     *
     * @param args unused command-line arguments
     * @throws InterruptedException if the introductory pauses are interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        String banner = """
                ░▒▓███████▓▒░░▒▓████████▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓██████▓▒░░▒▓████████▓▒░▒▓██████▓▒░ \s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░       ░▒▓█▓▒▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░     ░▒▓█▓▒░       \s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓██████▓▒░  ░▒▓█▓▒▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓██████▓▒░░▒▓█▓▒▒▓███▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░        ░▒▓█▓▓█▓▒░ ░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░        ░▒▓█▓▓█▓▒░ ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓███████▓▒░░▒▓████████▓▒░  ░▒▓██▓▒░  ░▒▓█▓▒░░▒▓██████▓▒░░▒▓████████▓▒░▒▓██████▓▒░ \s
                
                
                """;
        System.out.println(DIVIDER);
        System.out.println(banner.stripTrailing());
        Secret.run(DIVIDER);
    }

    /** Starts the functional console interface after the introduction. */
    public static void boot() {
        new CommandLoop(System.in, new TaskCommandProcessor(new Obj_List(100), DIVIDER)).run();
    }
}
