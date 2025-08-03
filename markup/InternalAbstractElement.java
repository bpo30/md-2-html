package markup;

import java.util.List;

public abstract class InternalAbstractElement implements InternalElement {

    protected List<InternalElement> markupList;

    private String bbCodeTag = "";
    private String markdownTag = "";
    private String htmlTag = "";

    public InternalAbstractElement(List<InternalElement> markups) {
        markupList = markups;
        ;
    }

    public void setBbCodeTag(String index) {
        this.bbCodeTag = index;
    }

    public void setMarkdownTag(String index) {
        this.markdownTag = index;
    }

    public void setHtmlTag(String htmlTag) {
        this.htmlTag = htmlTag;
    }

    public void toMarkdown(StringBuilder stringBuilder) {
        stringBuilder.append(markdownTag);
        for (InternalElement e : markupList) {
            e.toMarkdown(stringBuilder);
        }
        stringBuilder.append(markdownTag);
    }

    private void toTagged(StringBuilder stringBuilder,
                          String openTag,
                          String closeTag,
                          java.util.function.BiConsumer<InternalElement, StringBuilder> childRenderer) {
        stringBuilder.append(openTag);
        for (InternalElement e : markupList) {
            childRenderer.accept(e, stringBuilder);
        }
        stringBuilder.append(closeTag);
    }

    public void toBBCode(StringBuilder stringBuilder) {
        toTagged(stringBuilder,
                "[" + bbCodeTag + "]",
                "[/" + bbCodeTag + "]",
                InternalElement::toBBCode);
    }

    public void toHtml(StringBuilder stringBuilder) {
        toTagged(stringBuilder,
                "<" + markdownTag + ">",
                "</" + markdownTag + ">",
                InternalElement::toBBCode);
    }
}
