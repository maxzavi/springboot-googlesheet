# Java Spring Boot Sample write Google Sheet using gradle

Create Spring Boot project with gradle, without dependencies

Implements **CommandLineRunner** and add **run** method

```java
public class DemogradlegooglesheetApplication implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
```

Add logging using **slf4j**

```java
public class DemogradlegooglesheetApplication implements CommandLineRunner {
    //Add Logger
	private static Logger LOG = LoggerFactory.getLogger(DemogradlegooglesheetApplication.class);
```

## Google Sheet and Authorization

Create credentials and Authorization view link https://developers.google.com/sheets/api/quickstart/java#step_3_set_up_the_sample

Copy file credential type json in folder src/main/resources/credentials.json (exclude from version control for security)

Add dependecies in **build.gradle** file

```groovy
dependencies {

    .....
    implementation 'com.google.api-client:google-api-client:2.0.0'
    implementation 'com.google.oauth-client:google-oauth-client-jetty:1.34.1'
    implementation 'com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0'	
}
```

Scopes by create spreadsheet: **SheetsScopes.SPREADSHEETS**: 

```java
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
```

