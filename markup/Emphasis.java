package markup;

import java.util.List;

public class Emphasis extends InternalAbstractElement implements InternalElement {

    public Emphasis(List<InternalElement> markupElements) {
        super(markupElements);
        setBbCodeTag("i");
        setMarkdownTag("*");
        setHtmlTag("em");
    }
}
