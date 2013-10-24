package com.proximus.util;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

/**
 * Simple wrapper class to ensure XPathExpression.evaluate is called in a
 * thread-safe way. The JavaDoc for XPathExpression indicates that this is
 * necessary.
 */
public final class SynchronizedXPathExpression {

    private XPathExpression m_xpathExpression;

    public SynchronizedXPathExpression(XPathExpression xpathExpression) {
        setXpathExpression(xpathExpression);
    }

    public synchronized void setXpathExpression(XPathExpression xpathExpression) {
        m_xpathExpression = xpathExpression;
    }

    public synchronized Object evaluate(Object item, QName returnType)
            throws XPathExpressionException {
        return m_xpathExpression.evaluate(item, returnType);
    }
}