package exceptions;

import java.io.IOException;

/**
 * Created by huangzhengyue on 2016/10/1.
 */
public class TCPServicesException extends IOException {
    public TCPServicesException() {
    }

    public TCPServicesException(String message) {
        super(message);
    }

    public TCPServicesException(String message, Throwable cause) {
        super(message, cause);
    }

    public TCPServicesException(Throwable cause) {
        super(cause);
    }
}
