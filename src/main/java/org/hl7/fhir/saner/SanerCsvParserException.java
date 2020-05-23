package org.hl7.fhir.saner;

public class SanerCsvParserException extends RuntimeException {
	   private static final long serialVersionUID = 1L;
	   public SanerCsvParserException(Exception e)
	   {
		   super(e);
	   }
}