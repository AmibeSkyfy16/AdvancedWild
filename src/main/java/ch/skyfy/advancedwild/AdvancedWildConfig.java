package ch.skyfy.advancedwild;

public final class AdvancedWildConfig {
    private static final String defaultTypeImpl = "MySpecificImpl";
    public final String typeImpl;

    public AdvancedWildConfig(String typeImpl) {
        this.typeImpl = typeImpl;
    }

    @SuppressWarnings("unused")
    public AdvancedWildConfig() {
        this(defaultTypeImpl);
    }
}
