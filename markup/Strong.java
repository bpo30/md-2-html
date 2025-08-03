package markup;

import java.util.List;

public class Strong extends InternalAbstractElement implements InternalElement {

    public Strong(List<InternalElement> markupElements) {
        super(markupElements);
        setBbCodeTag("b");
        setMarkdownTag("__");
        setHtmlTag("strong");
    }

}
