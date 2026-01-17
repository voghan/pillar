package com.voghan.pillar.core.rewriters;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.rewriter.Transformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SimpleImgTransformerTest {

    private SimpleImgTransformer simpleImgTransformer;

    @Mock
    private ContentHandler contentHandler;

    @BeforeEach
    void setup() {
        simpleImgTransformer = new SimpleImgTransformer();
        simpleImgTransformer.setContentHandler(contentHandler);
    }

    @Test
    void createTransformer() {
        Transformer expected = simpleImgTransformer.createTransformer();

        assertNotNull(expected);
    }

    @Test
    void startElement() throws SAXException {
        String uri = "uri";
        String localName = "localname";
        String qName = "qName";
        Attributes atts = mock(Attributes.class);
        simpleImgTransformer.startElement(uri, localName, qName, atts);

        verify(contentHandler, times(0)).startElement(uri, localName, qName, atts);

    }

    @Test
    void startElement_imgUri() throws SAXException {
        String uri = "uri";
        String localName = "localname";
        String qName = "img";
        String srcValue = "srcValue";
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(uri, localName, "src", "src", srcValue);
        ArgumentCaptor<Attributes> atts = ArgumentCaptor.forClass(Attributes.class);
        simpleImgTransformer.startElement(uri, localName, qName, attributes);

        verify(contentHandler, times(1)).startElement(eq(uri), eq(localName), eq(qName), atts.capture());

        String actual = atts.getValue().getValue("src");
        assertEquals(srcValue, actual);
    }

}