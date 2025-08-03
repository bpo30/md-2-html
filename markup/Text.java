package markup;

public class Text implements InternalElement {
    private final String string;

    public Text(String string) {
        this.string = string;
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        stringBuilder.append(string);
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        stringBuilder.append(string);
    }

    @Override
    public void toHtml(StringBuilder stringBuilder) {
        stringBuilder.append(string);
    }
}
