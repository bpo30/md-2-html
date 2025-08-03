package markup;

import java.util.List;

public class Code extends InternalAbstractElement implements InternalElement{
    public Code(List<InternalElement> markups) {
        super(markups);
        setMarkdownTag("`");
        setHtmlTag("code");
    }
}
