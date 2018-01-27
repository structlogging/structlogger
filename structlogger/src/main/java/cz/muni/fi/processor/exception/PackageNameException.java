package cz.muni.fi.processor.exception;

/**
 * Exception thrown when package name is not valid
 */
public class PackageNameException extends Exception {
    public PackageNameException(final String message) {
        super(message);
    }
}
