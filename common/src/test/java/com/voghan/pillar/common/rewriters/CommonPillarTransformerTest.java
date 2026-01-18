package com.voghan.pillar.common.rewriters;

import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CommonPillarTransformerTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private CommonPillarTransformer commonPillarTransformer;

    @Mock
    private ContentHandler contentHandler;

    @BeforeEach
    void setup() {
        commonPillarTransformer = getComponent();
    }

    @Test
    void init() throws IOException {
        ProcessingContext processingContext = mock(ProcessingContext.class);
        ProcessingComponentConfiguration processingComponentConfiguration = mock(ProcessingComponentConfiguration.class);

        commonPillarTransformer.init(processingContext, processingComponentConfiguration);

        verify(processingContext, times(1)).getRequest();
    }

    @Test
    void getContentHandler() {
        assertNotNull(commonPillarTransformer.getContentHandler());
    }

    @Test
    void getServletRequest() throws IOException {
        ProcessingContext processingContext = mock(ProcessingContext.class);
        ProcessingComponentConfiguration processingComponentConfiguration = mock(ProcessingComponentConfiguration.class);
        when(processingContext.getRequest()).thenReturn(context.request());
        commonPillarTransformer.init(processingContext, processingComponentConfiguration);

        SlingHttpServletRequest request = commonPillarTransformer.getServletRequest();

        assertNotNull(request);
    }

    @Test
    void dispose() {
        commonPillarTransformer.dispose();
    }

    @Test
    void setDocumentLocator() {
        Locator locator = mock(Locator.class);
        commonPillarTransformer.setDocumentLocator(locator);

        verify(contentHandler, times(1)).setDocumentLocator(locator);
    }

    @Test
    void startDocument() throws SAXException {
        commonPillarTransformer.startDocument();

        verify(contentHandler, times(1)).startDocument();
    }

    @Test
    void endDocument() throws SAXException {
        commonPillarTransformer.endDocument();

        verify(contentHandler, times(1)).endDocument();
    }

    @Test
    void startPrefixMapping() throws SAXException {
        String prefix = "prefix";
        String url = "uri";
        commonPillarTransformer.startPrefixMapping(prefix, url);

        verify(contentHandler, times(1)).startPrefixMapping(prefix, url);
    }

    @Test
    void endPrefixMapping() throws SAXException {
        String prefix = "prefix";
        commonPillarTransformer.endPrefixMapping(prefix);

        verify(contentHandler, times(1)).endPrefixMapping(prefix);
    }

    @Test
    void endElement() throws SAXException {
        String uri = "uri";
        String localName = "localName";
        String qName = "qName";
        commonPillarTransformer.endElement(uri, localName, qName);

        verify(contentHandler, times(1)).endElement(uri, localName, qName);
    }

    @Test
    void characters() throws SAXException {
        char[] ch = "abc".toCharArray();
        int start = 0;
        int length = 1;
        commonPillarTransformer.characters(ch, start, length);

        verify(contentHandler, times(1)).characters(ch, start, length);
    }

    @Test
    void ignorableWhitespace() throws SAXException {
        char[] ch = "abc".toCharArray();
        int start = 0;
        int length = 1;
        commonPillarTransformer.ignorableWhitespace(ch, start, length);

        verify(contentHandler, times(1)).ignorableWhitespace(ch, start, length);
    }

    @Test
    void processingInstruction() throws SAXException {
        String target = "target";
        String data = "data";
        commonPillarTransformer.processingInstruction(target, data);

        verify(contentHandler, times(1)).processingInstruction(target, data);
    }

    @Test
    void skippedEntity() throws SAXException {
        String name = "name";
        commonPillarTransformer.skippedEntity(name);

        verify(contentHandler, times(1)).skippedEntity(name);
    }

    private CommonPillarTransformer getComponent() {
        commonPillarTransformer = new TestableCommonPillarTransformer();
        commonPillarTransformer.setContentHandler(contentHandler);
        return commonPillarTransformer;
    }

    static class  TestableCommonPillarTransformer extends CommonPillarTransformer {
        @Override
        public Transformer createTransformer() {
            return this;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        }
    };
}
