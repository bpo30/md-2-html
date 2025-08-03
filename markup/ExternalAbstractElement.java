package markup;

import java.util.List;

public abstract class ExternalAbstractElement implements ExternalElement {
    private String bbCodeTag = "";
    private String markdownTag = "";
    private String htmlTag = "";
    protected List<InternalElement> markupList;

    protected ExternalAbstractElement(List<InternalElement> markups) {
        markupList = markups;
    }

    public abstract void toMarkdown(StringBuilder stringBuilder);

    public abstract void toBBCode(StringBuilder stringBuilder);

}
