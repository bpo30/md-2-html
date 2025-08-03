package markup;

public interface MarkupElement {
    void toMarkdown(StringBuilder stringBuilder);

    void toBBCode(StringBuilder stringBuilder);

    void toHtml(StringBuilder stringBuilder);
}
