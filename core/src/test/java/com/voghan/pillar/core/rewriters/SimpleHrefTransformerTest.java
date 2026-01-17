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

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SimpleHrefTransformerTest {

    private SimpleHrefTransformer simpleHrefTransformer;

    @Mock
    private ContentHandler contentHandler;

    @BeforeEach
    void setup() {
        simpleHrefTransformer = new SimpleHrefTransformer();
        simpleHrefTransformer.setContentHandler(contentHandler);
    }

    @Test
    void createTransformer() {
        Transformer expected = simpleHrefTransformer.createTransformer();

        assertNotNull(expected);
    }

    @Test
    void startElement() throws SAXException {
        String uri = "uri";
        String localName = "localname";
        String qName = "qName";
        Attributes atts = mock(Attributes.class);
        simpleHrefTransformer.startElement(uri, localName, qName, atts);

        verify(contentHandler, times(0)).startElement(uri, localName, qName, atts);

    }

    @Test
    void startElement_hrefUri() throws SAXException {
        String uri = "uri";
        String localName = "localname";
        String qName = "a";
        String srcValue = "srcValue";
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(uri, localName, "href", "href", srcValue);
        ArgumentCaptor<Attributes> atts = ArgumentCaptor.forClass(Attributes.class);
        simpleHrefTransformer.startElement(uri, localName, qName, attributes);

        verify(contentHandler, times(1)).startElement(eq(uri), eq(localName), eq(qName), atts.capture());

        String actual = atts.getValue().getValue("href");
        assertEquals(srcValue, actual);
    }
}