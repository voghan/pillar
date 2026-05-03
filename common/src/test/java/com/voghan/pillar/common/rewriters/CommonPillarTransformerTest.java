package com.voghan.pillar.common.rewriters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CommonPillarTransformerTest {

  private static final AemContext context = AppAemContext.newAemContext();

  @Mock private ContentHandler contentHandler;

  // Transformer with contentHandler wired
  private CommonPillarTransformer transformer;
  // Transformer with no contentHandler (null guard paths)
  private CommonPillarTransformer transformerNoHandler;

  @BeforeEach
  void setup() {
    transformer = newTransformer();
    transformer.setContentHandler(contentHandler);

    transformerNoHandler = newTransformer();
    // contentHandler intentionally not set — remains null
  }

  // -------------------------------------------------------------------------
  // init
  // -------------------------------------------------------------------------

  @Test
  void init_storesRequestFromProcessingContext() throws IOException {
    ProcessingContext processingContext = mock(ProcessingContext.class);
    when(processingContext.getRequest()).thenReturn(context.request());

    transformer.init(processingContext, mock(ProcessingComponentConfiguration.class));

    assertNotNull(transformer.getServletRequest());
    verify(processingContext, times(1)).getRequest();
  }

  @Test
  void init_doesNotThrow_whenProcessingContextIsNull() throws IOException {
    // Should not throw — null guard in init()
    transformer.init(null, mock(ProcessingComponentConfiguration.class));

    assertNull(transformer.getServletRequest());
  }

  // -------------------------------------------------------------------------
  // getContentHandler / getServletRequest
  // -------------------------------------------------------------------------

  @Test
  void getContentHandler_returnsSetHandler() {
    assertNotNull(transformer.getContentHandler());
  }

  @Test
  void getServletRequest_returnsRequest_afterInit() throws IOException {
    ProcessingContext processingContext = mock(ProcessingContext.class);
    when(processingContext.getRequest()).thenReturn(context.request());
    transformer.init(processingContext, mock(ProcessingComponentConfiguration.class));

    SlingHttpServletRequest request = transformer.getServletRequest();

    assertNotNull(request);
  }

  // -------------------------------------------------------------------------
  // dispose
  // -------------------------------------------------------------------------

  @Test
  void dispose_doesNotThrow() {
    transformer.dispose();
  }

  // -------------------------------------------------------------------------
  // setDocumentLocator
  // -------------------------------------------------------------------------

  @Test
  void setDocumentLocator_delegatesToContentHandler() {
    Locator locator = mock(Locator.class);
    transformer.setDocumentLocator(locator);
    verify(contentHandler, times(1)).setDocumentLocator(locator);
  }

  @Test
  void setDocumentLocator_doesNotThrow_whenContentHandlerIsNull() {
    Locator locator = mock(Locator.class);
    transformerNoHandler.setDocumentLocator(locator);
    // contentHandler is a different instance — must have zero interactions
    verify(contentHandler, never()).setDocumentLocator(locator);
  }

  // -------------------------------------------------------------------------
  // startDocument / endDocument
  // -------------------------------------------------------------------------

  @Test
  void startDocument_delegatesToContentHandler() throws SAXException {
    transformer.startDocument();
    verify(contentHandler, times(1)).startDocument();
  }

  @Test
  void startDocument_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.startDocument();
    verify(contentHandler, never()).startDocument();
  }

  @Test
  void endDocument_delegatesToContentHandler() throws SAXException {
    transformer.endDocument();
    verify(contentHandler, times(1)).endDocument();
  }

  @Test
  void endDocument_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.endDocument();
    verify(contentHandler, never()).endDocument();
  }

  // -------------------------------------------------------------------------
  // startPrefixMapping / endPrefixMapping
  // -------------------------------------------------------------------------

  @Test
  void startPrefixMapping_delegatesToContentHandler() throws SAXException {
    transformer.startPrefixMapping("prefix", "uri");
    verify(contentHandler, times(1)).startPrefixMapping("prefix", "uri");
  }

  @Test
  void startPrefixMapping_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.startPrefixMapping("prefix", "uri");
    verify(contentHandler, never()).startPrefixMapping("prefix", "uri");
  }

  @Test
  void endPrefixMapping_delegatesToContentHandler() throws SAXException {
    transformer.endPrefixMapping("prefix");
    verify(contentHandler, times(1)).endPrefixMapping("prefix");
  }

  @Test
  void endPrefixMapping_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.endPrefixMapping("prefix");
    verify(contentHandler, never()).endPrefixMapping("prefix");
  }

  // -------------------------------------------------------------------------
  // endElement
  // -------------------------------------------------------------------------

  @Test
  void endElement_delegatesToContentHandler() throws SAXException {
    transformer.endElement("uri", "localName", "qName");
    verify(contentHandler, times(1)).endElement("uri", "localName", "qName");
  }

  @Test
  void endElement_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.endElement("uri", "localName", "qName");
    verify(contentHandler, never()).endElement("uri", "localName", "qName");
  }

  // -------------------------------------------------------------------------
  // characters
  // -------------------------------------------------------------------------

  @Test
  void characters_delegatesToContentHandler() throws SAXException {
    char[] ch = "abc".toCharArray();
    transformer.characters(ch, 0, 1);
    verify(contentHandler, times(1)).characters(ch, 0, 1);
  }

  @Test
  void characters_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.characters("abc".toCharArray(), 0, 1);
    verify(contentHandler, never()).characters(new char[0], 0, 0);
  }

  // -------------------------------------------------------------------------
  // ignorableWhitespace
  // -------------------------------------------------------------------------

  @Test
  void ignorableWhitespace_delegatesToContentHandler() throws SAXException {
    char[] ch = "   ".toCharArray();
    transformer.ignorableWhitespace(ch, 0, 1);
    verify(contentHandler, times(1)).ignorableWhitespace(ch, 0, 1);
  }

  @Test
  void ignorableWhitespace_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.ignorableWhitespace("   ".toCharArray(), 0, 1);
    verify(contentHandler, never()).ignorableWhitespace(new char[0], 0, 0);
  }

  // -------------------------------------------------------------------------
  // processingInstruction
  // -------------------------------------------------------------------------

  @Test
  void processingInstruction_delegatesToContentHandler() throws SAXException {
    transformer.processingInstruction("target", "data");
    verify(contentHandler, times(1)).processingInstruction("target", "data");
  }

  @Test
  void processingInstruction_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.processingInstruction("target", "data");
    verify(contentHandler, never()).processingInstruction("target", "data");
  }

  // -------------------------------------------------------------------------
  // skippedEntity
  // -------------------------------------------------------------------------

  @Test
  void skippedEntity_delegatesToContentHandler() throws SAXException {
    transformer.skippedEntity("name");
    verify(contentHandler, times(1)).skippedEntity("name");
  }

  @Test
  void skippedEntity_doesNotThrow_whenContentHandlerIsNull() throws SAXException {
    transformerNoHandler.skippedEntity("name");
    verify(contentHandler, never()).skippedEntity("name");
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private static CommonPillarTransformer newTransformer() {
    return new CommonPillarTransformer() {
      @Override
      public Transformer createTransformer() {
        return this;
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes atts)
          throws SAXException {
      }
    };
  }
}
