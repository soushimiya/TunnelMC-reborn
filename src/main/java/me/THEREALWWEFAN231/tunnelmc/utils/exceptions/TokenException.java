package me.THEREALWWEFAN231.tunnelmc.utils.exceptions;

public class TokenException extends RuntimeException{

    public TokenException() {
        super("Your token may be expired, please log in again.");
    }
}
