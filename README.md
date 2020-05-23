# Cope: CSV to MeasureReport
Convert a CSV file to a FHIR Measure Report.

As a user with a measure collection process using a spreadsheet reporting on a specific measure, I want to convert the spreadsheet to an appropriate FHIR MeasureReport resource so that I can automate the transmission of measures to a FHIR MeasureReport endpoint.

## Acceptance Criteria:
Given a {spreadsheet} 

And a {Measure} resource

And other control or configuration parameters such as {DateOfMeasurement}

And {Institution Identifier}

When I process the {spreadsheet} using the service 

Then I have a {MeasureReport Resource} which contains the data in the {spreadsheet}

And it has all {required components}

And the MeasureReport Resource is valid according to FHIR Validation rules

And the MeasureReport Resource is valid according to the Saner Implementation Guide profile for a MeasureReport

And the MeasureReport Resource is valid according to the any profile required of it by the {Measure} resource.
