package markup;

import java.util.List;

public class Header extends ExternalAbstractElement implements ExternalElement {
    private int headerLevel = 1;

    public Header(List<InternalElement> markups) {
        super(markups);
    }

    public void setHeaderLevel(int headerLevel) {
        this.headerLevel = headerLevel;
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
        stringBuilder.append("<h").append(headerLevel).append(">");
        for (InternalElement e : markupList) {
            e.toHtml(stringBuilder);
        }
        stringBuilder.append("</h").append(headerLevel).append(">");
    }

}
