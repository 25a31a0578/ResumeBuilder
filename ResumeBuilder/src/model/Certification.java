package model;

public class Certification {
    private final String name;
    private final String issuer;

    public Certification(String name, String issuer) {
        this.name = Utils.clean(name);
        this.issuer = Utils.clean(issuer);
    }

    public String getName() { return name; }
    public String getIssuer() { return issuer; }
}
