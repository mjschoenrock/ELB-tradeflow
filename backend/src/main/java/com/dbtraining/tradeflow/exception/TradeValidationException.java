package com.dbtraining.tradeflow.exception;

/**
 * ============================================================================
 * TradeValidationException — TICKET-I032
 * ============================================================================
 * WHAT:    Checked exception thrown when a Trade fails validation.
 * HOW:     `extends Exception` (checked — callers must declare or catch).
 * WHY:     Validation errors are RECOVERABLE — callers will likely want to
 *          surface them to the user. Checked exceptions force that handling.
 *          (Compare: InsufficientDataException, which is UNRECOVERABLE and
 *           therefore unchecked.)
 * OBSERVE: TradeController catches this on Day 6 and returns 400 Bad Request.
 * ============================================================================
 */
public class TradeValidationException extends Exception {

    public enum Code {
        MISSING_FIELD,
        INVALID_VALUE,
        REFERENCE_NOT_FOUND
    }

    private final Code code;

    /**
     * Constructs a validation exception with a specific error code and detail message.
    */
    public TradeValidationException(Code code, String message) {
        this(code, message, null);
    }

    /**
     * Constructs a validation exception with a specific error code, detail message, and cause.
     */
    public TradeValidationException(Code code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Convenience constructor defaulting the error code to {@link Code#INVALID_VALUE}.
     */
    public TradeValidationException(String message) {
        this(Code.INVALID_VALUE, message);
    }

    /**
     * Retrieves the structured validation error code.
     */
    public Code getCode() {
        return this.code;
    }
}