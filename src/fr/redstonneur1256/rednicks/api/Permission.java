package fr.redstonneur1256.rednicks.api;

public enum Permission {

    RANDOM_NICK("nick.random"),
    CUSTOM_NICK("nick.choose");

    private String code;

    Permission(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
