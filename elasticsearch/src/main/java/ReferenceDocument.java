import com.google.gson.JsonObject;

public class ReferenceDocument {

    public JsonObject document;

    public ReferenceDocument(JsonObject document) {
        this.document = document;
    }

    public JsonObject getDocument() {
        return document;
    }
}
