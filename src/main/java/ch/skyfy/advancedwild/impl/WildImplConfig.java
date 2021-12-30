package ch.skyfy.advancedwild.impl;


public interface WildImplConfig {
    /**
     * We will check if the user has entered data in the wrong order, or negative numbers that should not be there
     */
    boolean isValid();
}
