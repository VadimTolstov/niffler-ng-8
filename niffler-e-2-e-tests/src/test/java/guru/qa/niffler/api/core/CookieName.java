package guru.qa.niffler.api.core;

public enum CookieName {
    XSRF_TOKEN("XSRF-TOKEN");

    private final String value;

    CookieName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}