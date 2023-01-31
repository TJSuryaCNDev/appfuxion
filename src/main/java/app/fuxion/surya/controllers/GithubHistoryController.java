package app.fuxion.surya.controllers;
import app.fuxion.surya.entities.GithubHistory;
import app.fuxion.surya.repositories.GithubHistoryRepository;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@RestController
@RequestMapping("/api")
public class GithubHistoryController {
  @Autowired
  private GithubHistoryRepository githubHistoryRepository;
  private String baseUri = "http://localhost:8080/api/";

  @GetMapping("/githubHistory")
  public ResponseEntity<List<GithubHistory>> findAll() {
    try {
      // Get History From MySQL
      List<GithubHistory> githubHistory = githubHistoryRepository.findAll();

      // Return Github History
      return new ResponseEntity<>(githubHistory, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/export")
  public ResponseEntity<?> export(@RequestParam(required=false) Map<String,String> qparams) {
    try {
      // Base Github URI
      String githubUri = "https://api.github.com/search/users";

      // If Query Params name exist, use the Query Params name (default mike)
      String name = (qparams.containsKey("name")) ? qparams.get("name") : "mike";

      // If Query Params type exist, use the Query Params type (default user)
      String type = (qparams.containsKey("type")) ? qparams.get("type") : "user";

      // If Query Params per_page exist, use the Query Params per_page (default 100)
      int per_page = (qparams.containsKey("per_page") && isNumeric(qparams.get("per_page"))) ? Integer.parseInt(qparams.get("per_page")) : 100;

      // Create New Github URI
      githubUri = githubUri + "?q=type:" + type + " " + name + " in:name&per_page=" + per_page;
      
      // Call Github API and Save Response into JSON Object result
      RestTemplate restTemplate = new RestTemplate();
      JSONObject result = new JSONObject(restTemplate.getForObject(githubUri, String.class));

      // Getting items From Result
      JSONArray items = result.getJSONArray(("items"));

      // Create Date
      DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD-HH-MM");
      String currentDateTime = dateFormat.format(new Date());

      // Create Document
      Document doc = new Document();

      // String File Name
      String nameFile = name + "_" + currentDateTime + ".pdf";

      // String Save Location
      String fileLocation = "./src/main/resources/document/" + nameFile;

      // Create PDF File
      PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileLocation));
      doc.open();

      // Adding JSON to PDF File
      Paragraph para = new Paragraph(items.toString(2));
      doc.add(para);

      // Close After Finished
      doc.close();
      writer.close();

      // Save History to MySQL DB
      GithubHistory newGithub = new GithubHistory();
      newGithub.setName(name);
      newGithub.setFile(nameFile);
      newGithub.setDownload(baseUri + "document/" + nameFile);
      newGithub.setAt(new Date());
      githubHistoryRepository.save(newGithub);

      // Custom Response Success
      Map<String, String> response = new HashMap<>();
      response.put("code", "201");
      response.put("message", "Pdf Created");
      response.put("data", baseUri + "document/" + nameFile);

      // Response With JSON
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      // Custom Error JSON
      Map<String, String> error = new HashMap<>();
      error.put("code", "400");
      error.put("message", "Bad Request");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/document/{file}")
  public ResponseEntity<?> getImage(@PathVariable(value = "file") String file) {
    try {
      // Get File From Resource
      var docFile = new ClassPathResource("document/" + file);
      byte[] bytes = StreamUtils.copyToByteArray(docFile.getInputStream());

      // Response With PDF
      return ResponseEntity
              .ok()
              .contentType(MediaType.APPLICATION_PDF)
              .body(bytes);
    } catch(Exception e) {
      // Custom Error JSON
      Map<String, String> error = new HashMap<>();
      error.put("code", "404");
      error.put("message", "File Not Found");
      return ((BodyBuilder) ResponseEntity
              .notFound())
              .body(error);
    }
  }
  
  // Function to check Is Numeric
  public static boolean isNumeric(String string) {
    int intValue;
		
    if(string == null || string.equals("")) {
      return false;
    }
    
    try {
      intValue = Integer.parseInt(string);
      return true;
    } catch (NumberFormatException e) {
    }
    return false;
  }
}