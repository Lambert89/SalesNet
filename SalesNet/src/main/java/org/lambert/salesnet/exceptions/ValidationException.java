package org.lambert.salesnet.exceptions;

/**
 * <p>
 * <b> Validation Exception. </b>
 * </p>
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorFieldName;

    public ValidationException(final String field) {
        super("Validation on field {" + field + "} fail!");
        this.errorFieldName = field;
    }

    /**
     * @return the errorFieldName
     */
    public String getErrorFieldName() {
        return this.errorFieldName;
    }

}
