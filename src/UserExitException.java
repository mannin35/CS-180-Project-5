/**
 * Project 5 - UserExitException
 * <p>
 * Exception to be thrown when user tries to exit the program
 *
 * @author Nick Andry, Lab Sec L15
 * @version December 12, 2022
 */

public class UserExitException extends Exception {
    public UserExitException(String message) {
        super(message);
    }
}
