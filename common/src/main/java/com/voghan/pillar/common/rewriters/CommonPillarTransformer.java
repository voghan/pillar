package com.voghan.pillar.common.rewriters;

import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public abstract class CommonPillarTransformer implements Transformer, TransformerFactory {

  private ContentHandler contentHandler;
  private SlingHttpServletRequest servletRequest;

  @Override
  public void init(ProcessingContext processingContext,
      ProcessingComponentConfiguration processingComponentConfiguration) throws IOException {
    if (processingContext != null) {
      this.servletRequest = processingContext.getRequest();
    }
  }

  public ContentHandler getContentHandler() {
    return contentHandler;
  }

  @Override
  public void setContentHandler(ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  public SlingHttpServletRequest getServletRequest() {
    return servletRequest;
  }

  @Override
  public void dispose() {

  }

  @Override
  public void setDocumentLocator(Locator locator) {
    if (contentHandler != null) {
      contentHandler.setDocumentLocator(locator);
    }
  }

  @Override
  public void startDocument() throws SAXException {
    if (contentHandler != null) {
      contentHandler.startDocument();
    }
  }

  @Override
  public void endDocument() throws SAXException {
    if (contentHandler != null) {
      contentHandler.endDocument();
    }
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    if (contentHandler != null) {
      contentHandler.startPrefixMapping(prefix, uri);
    }
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    if (contentHandler != null) {
      contentHandler.endPrefixMapping(prefix);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (contentHandler != null) {
      contentHandler.endElement(uri, localName, qName);
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (contentHandler != null) {
      contentHandler.characters(ch, start, length);
    }
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    if (contentHandler != null) {
      contentHandler.ignorableWhitespace(ch, start, length);
    }
  }

  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    if (contentHandler != null) {
      contentHandler.processingInstruction(target, data);
    }
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
    if (contentHandler != null) {
      contentHandler.skippedEntity(name);
    }
  }
}
