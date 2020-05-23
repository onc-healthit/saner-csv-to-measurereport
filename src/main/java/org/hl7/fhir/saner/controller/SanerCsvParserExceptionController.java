package org.hl7.fhir.saner.controller;

import org.hl7.fhir.saner.SanerCsvParserException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SanerCsvParserExceptionController {
   @ExceptionHandler(value = SanerCsvParserException.class)
   public ResponseEntity<Object> exception(SanerCsvParserException exception) {
      return new ResponseEntity<>("Error parsing csv file to SANER Measure report: "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
   }
}
