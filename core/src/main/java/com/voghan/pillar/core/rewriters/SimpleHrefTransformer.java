package com.voghan.pillar.core.rewriters;

import com.voghan.pillar.common.rewriters.CommonPillarTransformer;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@Component(
    immediate = true,
    service = TransformerFactory.class,
    property = {
        "pipeline.type=pillar-href-rewriter"
    }
)
public class SimpleHrefTransformer extends CommonPillarTransformer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleHrefTransformer.class);

    @Override
    public Transformer createTransformer() {
        return new SimpleHrefTransformer();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        // Do the actual work
        ContentHandler contentHandler = getContentHandler();
        if (contentHandler != null) {
            if (atts.getIndex("href") > -1 && qName.equalsIgnoreCase("a")) {
                AttributesImpl modifiedAttributes = new AttributesImpl(atts);

                String updatedHref = atts.getValue("href");
                logger.info("Transforming href {}", updatedHref);

                modifiedAttributes.setValue(atts.getIndex("href"), updatedHref);
                contentHandler.startElement(uri, localName, qName, modifiedAttributes);
            } else {
                contentHandler.startElement(uri, localName, qName, atts);
            }
        }
    }

}
