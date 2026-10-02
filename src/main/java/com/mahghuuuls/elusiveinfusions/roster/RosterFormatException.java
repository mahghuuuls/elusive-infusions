package com.mahghuuuls.elusiveinfusions.roster;

/** The roster file is malformed. The message names the line and the reason. */
public final class RosterFormatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RosterFormatException(String message) {
        super(message);
    }
}
