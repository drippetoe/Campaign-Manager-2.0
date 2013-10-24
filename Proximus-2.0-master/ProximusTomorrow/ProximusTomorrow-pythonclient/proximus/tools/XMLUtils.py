import xml.etree.ElementTree as xml
from elementtree import SimpleXMLTreeBuilder
from proximus.tools.SystemWriter import SystemWriter
xml.XMLTreeBuilder = SimpleXMLTreeBuilder.TreeBuilder
import xmlpp #@UnresolvedImport

class XMLUtils:
    """Generic XML Tools not tied to any domain specific classes
    """
    
    """
    Convert ElementTree Element to a String
    """
    @staticmethod
    def elementToString(element):
        return XMLUtils.prettyPrintXML(xml.tostring(element))
    
    """
    Convert String to ElementTree Element
    """
    @staticmethod
    def stringToElement(xmlContent):
        return xml.fromstring(xmlContent)
    
    """
    Format an XML block nicely with indentation
    """
    @staticmethod
    def prettyPrintXML(xmlString):
        return xmlpp.get_pprint(xmlString)
    
    """
    takes two ElementTree Elements as arguments
    returns True if the XML they represent is the same
    """
    @staticmethod
    def areElementsEqual(node1, node2):
        n1s = XMLUtils.elementToString(node1)
        n2s = XMLUtils.elementToString(node2)
        return n1s == n2s
    
    """
    Takes a filename (path) and an ElementTree Element and persists
    """
    @staticmethod
    def writeNodeToFile(filepath, node):
        SystemWriter.writeFile(filepath, XMLUtils.elementToString(node))
        
    @staticmethod
    def readNodeFromFile(filepath):
        data = SystemWriter.readFileToString(filepath)
        node = XMLUtils.stringToElement(data)
        return node
    
    """
    takes an Element node and a String attribute
    if the attribute is found, it is returned
    if not, returns None
    """
    @staticmethod
    def getAttributeSafe(node, attribute, default=None):
        attribDict = node.attrib
        if attribDict.has_key(attribute):
            return attribDict[attribute]
        else:
            return default