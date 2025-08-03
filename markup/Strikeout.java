package markup;

import java.util.List;

public  class Strikeout extends InternalAbstractElement implements InternalElement{
    public Strikeout(List<InternalElement> markupElements) {
        super(markupElements);
        setBbCodeTag("s");
        setMarkdownTag("~");
        setHtmlTag("s");
    }
}
