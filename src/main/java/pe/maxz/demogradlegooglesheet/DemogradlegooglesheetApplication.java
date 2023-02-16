package pe.maxz.demogradlegooglesheet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

@SpringBootApplication
public class DemogradlegooglesheetApplication implements CommandLineRunner {
	private static Logger LOG = LoggerFactory.getLogger(DemogradlegooglesheetApplication.class);

	private static final String APPLICATION_NAME = "Google Sheet - java gradle sample";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	public static void main(String[] args) {
		SpringApplication.run(DemogradlegooglesheetApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("Start..");
		//readSheet();
		createSheet();
		LOG.info("End!");
	}

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
			throws IOException {
		// Load client secrets.
		InputStream in = DemogradlegooglesheetApplication.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public static void readSheet() throws GeneralSecurityException, IOException{
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		final String spreadsheetId = "1RzM-9pG8FdXlGh6S4YRDpISp5pEJ2aS10Cczjblq_nQ";//"1AVLD8Sf_g0ywLNZNepS_y1PeC4Lii2L8tNPxmqdUwUc";
		final String range = "Hoja 1!A2:E5";
		Sheets service =
			new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME)
				.build();
		ValueRange response = service.spreadsheets().values()
			.get(spreadsheetId, range)
			.execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
		  System.out.println("No data found.");
		} else {
		  System.out.println("Name, Lastname");
		  for (List<Object> row : values) {
			// Print columns A and E, which correspond to indices 0 and 4.
			System.out.printf("%s, %s\n", row.get(0), row.get(1));
		  }
		}
	}

	public static void createSheet() throws GeneralSecurityException, IOException{
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		Credential credential = getCredentials(HTTP_TRANSPORT);
	
		Sheets service=  new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
			.setApplicationName(APPLICATION_NAME)
			.build();

		Spreadsheet spreadSheet = new Spreadsheet().setProperties(
			new SpreadsheetProperties().setTitle("My Spreadsheet"));
		Spreadsheet result = service
			.spreadsheets()
			.create(spreadSheet).execute();
		LOG.info("SpreadSheet Id :" + result.getSpreadsheetId());

		List<ValueRange> data = new ArrayList<>();
		data.add(new ValueRange()
			.setRange("A1")
			.setValues(Arrays.asList(
				Arrays.asList("OC XXXXX"))));
		data.add(new ValueRange()
			.setRange("A2")
			.setValues(Arrays.asList(
				Arrays.asList("SKU1", 1,"UN"),
				Arrays.asList("SKU2", 3,"UN"),
				Arrays.asList("SKU3", 2,"UN"),
				Arrays.asList("SKU4", 1,"UN")

				)));

		BatchUpdateValuesRequest batchBody = new BatchUpdateValuesRequest()
			.setValueInputOption("USER_ENTERED")
			.setData(data);

		service.spreadsheets().values()
			.batchUpdate(result.getSpreadsheetId(), batchBody)
			.execute();
	}
}
