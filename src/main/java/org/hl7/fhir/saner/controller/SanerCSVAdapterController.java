package org.hl7.fhir.saner.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.saner.SanerCsvParserException;
import org.hl7.fhir.saner.TransformerErrorListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.s9api.XsltTransformer;
import net.sf.saxon.serialize.MessageWarner;
import net.sf.saxon.trace.XSLTTraceListener;

@RestController
@RequestMapping("/saner")
public class SanerCSVAdapterController {

	@Autowired
	private ResourceLoader resourceLoader;

	@GetMapping("/csv/status")
	public String status() {
		return "Working..";
	}

	@PostMapping("/csv/transform")
	public ResponseEntity<StreamingResponseBody> handleFileUpload(
			@RequestParam("mappingfile") MultipartFile mappingFile, @RequestParam("csvfile") MultipartFile csvFile,
			@RequestParam("format") String format,
			RedirectAttributes redirectAttributes, final HttpServletResponse response) {

        System.setProperty("javax.xml.transform.TransformerFactory",
                  "net.sf.saxon.TransformerFactoryImpl");
		Resource xsltResource = resourceLoader.getResource("classpath:stateLabReportingExamples2ToFsh.xslt");
        
		StreamingResponseBody stream = out -> {
			try {
		        SAXTransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
	            javax.xml.transform.sax.TemplatesHandler templatesHandler =
	            		tFactory.newTemplatesHandler();

			    String xsltText = IOUtils.toString(xsltResource.getInputStream(), StandardCharsets.UTF_8);

		        StreamSource stylesource = new StreamSource(xsltResource.getInputStream());
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				transformerFactory.setURIResolver(new ClasspathResourceURIResolver());

				Transformer transformer = transformerFactory.newTransformer(stylesource);
		        Source src = new StreamSource(new StringReader("<test/>"));
		
		        transformer.setErrorListener(new ErrorListener() {
		            public void error(TransformerException exception) throws TransformerException
		            {
		                System.err.println("error: " + exception.getMessage());
		            }
		
		            public void fatalError(TransformerException exception) throws TransformerException
		            {
		                System.err.println("fatal error: " + exception.getMessage());
		            }
		
		            public void warning(TransformerException exception) throws TransformerException
		            {
		                System.err.println("warning: " + exception.getMessage());
		            }
		        });

			    String mappingText = IOUtils.toString(mappingFile.getInputStream(), StandardCharsets.UTF_8);
			    String csvText = IOUtils.toString(csvFile.getInputStream(), StandardCharsets.UTF_8);

		        MessageWarner mw = new MessageWarner();
		        mw.setWriter(new StringWriter());
		        ((TransformerImpl) transformer).getUnderlyingXsltTransformer().getUnderlyingController().setMessageEmitter(mw);
		        transformer.setParameter("mapping", mappingText);
		        transformer.setParameter("csvInputData", csvText);
		        transformer.setParameter("format", format);
		        transformer.transform(src, new StreamResult(out));
			} catch (TransformerException e) {
				throw new SanerCsvParserException(e);
			} catch (NullPointerException e) {
				throw new SanerCsvParserException(e);
			}
		};
		return new ResponseEntity(stream, HttpStatus.OK);
	}

    private static XMLReader makeXMLReader() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newSAXParser().getXMLReader();
    }
	
	class ClasspathResourceURIResolver implements URIResolver {
		@Override
		public Source resolve(String href, String base) throws TransformerException {
			return new StreamSource(getClass().getClassLoader().getResourceAsStream(href));
		}
	}
}
