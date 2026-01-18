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
        "pipeline.type=pillar-img-rewriter"
    }
)
public class SimpleImgTransformer extends CommonPillarTransformer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleImgTransformer.class);

    @Override
    public Transformer createTransformer() {
        return new SimpleImgTransformer();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        // Do the actual work
        ContentHandler contentHandler = getContentHandler();
        if (contentHandler != null) {
            if (atts.getIndex("src") > -1 && qName.equalsIgnoreCase("img")) {
                AttributesImpl modifiedAttributes = new AttributesImpl(atts);

                String updatedSrc = atts.getValue("src");
                logger.info("Transforming href {}", updatedSrc);

                modifiedAttributes.setValue(atts.getIndex("src"), updatedSrc);
                contentHandler.startElement(uri, localName, qName, modifiedAttributes);
            } else {
                contentHandler.startElement(uri, localName, qName, atts);
            }
        }
    }

}
