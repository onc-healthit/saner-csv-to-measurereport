package org.hl7.fhir.saner;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class TransformerErrorListener implements ErrorListener {

	int count = 0;

	@Override
	public void warning(TransformerException e) throws TransformerException {

		++count;

		System.out.println("TransformerErrorListener received warning");

	}

	@Override
	public void error(TransformerException e) throws TransformerException {

		++count;

		System.out.println("TransformerErrorListener received error");

	}

	@Override
	public void fatalError(TransformerException e) throws TransformerException {

		++count;

		System.out.println("TransformerErrorListener received fatal error");

	}

}