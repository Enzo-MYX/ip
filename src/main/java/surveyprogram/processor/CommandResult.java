package surveyprogram.processor;

/**
 * Contains the dialogue and continuation state produced by one command.
 *
 * @param response dialogue to show to the user
 * @param shouldContinue whether the application should accept another command
 */
public record CommandResult(String response, boolean shouldContinue) {
}
