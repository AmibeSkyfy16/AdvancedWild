package ch.skyfy.advancedwild;

public record AdvancedWildConfig(String typeImpl) {
    private static final String defaultTypeImpl = "classicPerPlayerImpl";
    public AdvancedWildConfig() { // Return the defaultConfiguration
        this(defaultTypeImpl);
    }
}
