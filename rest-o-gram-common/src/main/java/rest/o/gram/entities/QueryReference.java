package rest.o.gram.entities;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/3/13
 */
public class QueryReference {
    public QueryReference(String property, String kind) {
        this.property = property;
        this.kind = kind;
    }

    public String getProperty() {
        return property;
    }

    public String getKind() {
        return kind;
    }

    private String property;
    private  String kind;
}
