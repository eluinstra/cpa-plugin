/**
 * Copyright 2016 Ordina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.plugin.cpa.common;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import io.vavr.control.Option;
import lombok.val;
import nl.clockwork.ebms.util.DOMUtils;

public class Utils
{
	public static XPath createXPath()
	{
		val result = XPathFactory.newInstance().newXPath();
		result.setNamespaceContext(new NamespaceContext()
		{
			public String getNamespaceURI(String prefix)
			{
				return Match(prefix).of(
						Case($("cpa"),"http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd"),
						Case($("xmldsig"),"http://www.w3.org/2000/09/xmldsig#"),
						Case($(),"") // XPathConstants.NULL_NS_URI;
				);
			}

			@Override
			public String getPrefix(String namespaceURI)
			{
				return Match(namespaceURI).of(
					Case($("http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd"),"cpa"),
					Case($("http://www.w3.org/2000/09/xmldsig#"),"xmldsig"),
					Case($(),(String)null)
				);
			}

			@Override
			public Iterator<String> getPrefixes(String namespaceURI)
			{
				Option<String> prefix = Match(namespaceURI).option(
					Case($("http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd"),"cpa"),
					Case($("http://www.w3.org/2000/09/xmldsig#"),"xmldsig")
				);
				return prefix.iterator();
			}
		});
		return result;
	}

	public static String toXSDDate(LocalDateTime date)
	{
		val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00");
		return formatter.format(date);
	}

	public static void generateKeyInfo(Node node, InputStream certificate) throws CertificateException, KeyException, MarshalException
	{
    val kif = XMLSignatureFactory.getInstance("DOM").getKeyInfoFactory();
    val x509Certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(certificate);
    //val keyInfo = kif.newKeyInfo(Arrays.asList(kif.newKeyValue(certificate.getPublicKey()),kif.newX509Data(Collections.singletonList(certificate))));
    val keyInfo = kif.newKeyInfo(Arrays.asList(kif.newKeyValue(x509Certificate.getPublicKey()),kif.newX509Data(Arrays.asList(x509Certificate.getSubjectX500Principal().toString(),kif.newX509IssuerSerial(x509Certificate.getIssuerX500Principal().toString(),x509Certificate.getSerialNumber()),x509Certificate))));
    val dom = new DOMStructure(node);
    keyInfo.marshal(dom,null);
	}
	
	public static void write(Node node, OutputStream out) throws TransformerException
	{
		val transformer = DOMUtils.getTransformer();
		transformer.transform(new DOMSource(node),new StreamResult(out));
	}

	public static void main(String[] args) throws ParserConfigurationException, MarshalException, TransformerException, GeneralSecurityException, IOException
	{
		val document = createDocument();
    document.appendChild(document.createElement("root"));
    val kif = XMLSignatureFactory.getInstance("DOM").getKeyInfoFactory();
    val fis = new FileInputStream("classpath:/nl/clockwork/ebms/localhost.crt");
    val certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(fis);
    val keyInfo = kif.newKeyInfo(Arrays.asList(kif.newKeyValue(certificate.getPublicKey()),kif.newX509Data(Arrays.asList(certificate.getSubjectX500Principal().toString(),kif.newX509IssuerSerial(certificate.getIssuerX500Principal().toString(),certificate.getSerialNumber()),certificate))));
    val dom = new DOMStructure(document.getFirstChild());
    keyInfo.marshal(dom,null);
    write(dom.getNode().getFirstChild(),System.out);
	}

	private static Document createDocument() throws ParserConfigurationException
	{
		val dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    val db = dbf.newDocumentBuilder();
    return db.newDocument();
	}
}
