public class NoArgsException extends Exception {
    public NoArgsException() { super(); }
    public NoArgsException(String message) { super(message); }
    public NoArgsException(String message, Throwable cause) { super(message, cause); }
    public NoArgsException(Throwable cause) { super(cause); }
}