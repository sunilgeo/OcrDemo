# OCRDemo

This is a simple solution to the bank OCR coding dojo challenge found here https://code.joejag.com/coding-dojo/bank-ocr/. This solution
uses Finite State Machine for scanning seven segment characters from input lines. 

Very limited file error checking and correction is handled by the application. Input file is expected to be well formatted and in line with the requirements mentioned in user stories as -<br>

_"Each entry is 4 lines long, and each line has 27 characters. The first 3 lines of each entry contain an account number written using pipes and underscores, and the fourth line is blank."_ <br>

Any variance or errors in input format will result in errors and application will not produce expected results.

The application is developed in Java 8. Gradle is used for building, packaging and running tests.   

## Prerequisites

* Java 8 or above installed and configured.

## Setup

Clone the application -

`git clone https://github.com/sunilgeo/OcrDemo.git`

## Building

On unix environment - <br>

`./gradlew build`

On windows environment - <br>

`gradlew.bat build`

This will build and jar the application. The jar file _ocr-demo.jar_ is created at `build/libs/` folder. This command will also run unit tests. The test results can be found by browsing `build/reports/tests/test/index.html`

## Run the application

`java -jar <path to ocr-demo.jar> <path to input file> [options]`

where options can be - 
* `-verify` - to verify scanned account numbers with checksum
* `-detailed` - to print details, account numbers along with any error status
* `-fix` - will try to fix scan errors

## Example usage
 * For User Story 1 <br>
 
 `java -jar build/libs/ocr-demo.jar src/test/resources/user_story1.txt`
 
 * For User Story 2 <br>
 
 `java -jar build/libs/ocr-demo.jar src/test/resources/user_story2.txt -verify`
 
 * For User Story 3 <br>
 
 `java -jar build/libs/ocr-demo.jar src/test/resources/user_story3.txt -verify -detailed`
 
 * For User Story 4 <br>
 
 `java -jar build/libs/ocr-demo.jar src/test/resources/user_story4.txt -verify -detailed -fix`
 
 
 



 

