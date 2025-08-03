package markup;

import java.util.List;

public class Paragraph extends ExternalAbstractElement implements MarkupElement {
    public Paragraph(List<InternalElement> markupElements) {
        super(markupElements);
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        for (InternalElement e : markupList) {
            e.toMarkdown(stringBuilder);
        }
    }

    public void toBBCode(StringBuilder stringBuilder) {
        for (InternalElement e : markupList) {
            e.toBBCode(stringBuilder);
        }
    }

    @Override
    public void toHtml(StringBuilder stringBuilder) {
        stringBuilder.append("<p>");
        for (InternalElement e : markupList) {
            e.toBBCode(stringBuilder);
        }
        stringBuilder.append("</p>");
    }
}
