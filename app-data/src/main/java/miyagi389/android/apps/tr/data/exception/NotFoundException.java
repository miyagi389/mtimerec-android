package miyagi389.android.apps.tr.data.exception;

public class NotFoundException extends Exception {

    @SuppressWarnings("unused")
    public NotFoundException() {
        super();
    }

    public NotFoundException(final String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public NotFoundException(
        final String message,
        final Throwable cause
    ) {
        super(message, cause);
    }

    @SuppressWarnings("unused")
    public NotFoundException(final Throwable cause) {
        super(cause);
    }
}
