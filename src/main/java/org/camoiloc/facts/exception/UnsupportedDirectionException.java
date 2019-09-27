package org.camoiloc.facts.exception;

/**
 * Created by Dmitri Samoilov on 2019-09-26.
 */
public class UnsupportedDirectionException extends Exception {

    public UnsupportedDirectionException(String language) {
        super("Unsupported direction [" + language + "]");
    }

}
