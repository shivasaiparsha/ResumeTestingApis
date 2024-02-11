package com.pdf.FileManager;

import com.pdf.FileManager.entity.ResumeEntity;
import com.pdf.FileManager.repository.StorageRepository;
import com.pdf.FileManager.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.Document;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.pdf.FileManager.util.ImageUtils.getMediaType;

@SpringBootApplication
@RestController
@Slf4j
@CrossOrigin(origins = "https://blue-arda-82.tiiny.site/")
public class FileManagerApplication {

	@Autowired
	private StorageService service;

	@Autowired
	private StorageRepository storageRepository;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
		Integer userid=1;
		String uploadImage = service.uploadImage(file, userid);
		return ResponseEntity.ok().body("{\"message\": \"" + uploadImage + "\"}");
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<?> downloadImage(@PathVariable Long id)throws Exception{

        try {
			ResponseEntity<?> s=service.downloadImage(id);

			return s;

		}
		catch(Exception e){
			log.error(" resume not  found");
			  throw new Exception(e.getMessage());
			}
	}



	@GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> getAllImages(@RequestParam Integer userid) throws Exception {


		List<InputStream> pdfStreams = service.findAllResumes(userid); // Your method to retrieve PDF streams
		InputStream combinedPdfStream = combinePdfStreams(pdfStreams);
		InputStreamResource resource = new InputStreamResource(combinedPdfStream);

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.body(resource);
 	}

	private InputStream combinePdfStreams(List<InputStream> pdfStreams) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			for (InputStream pdfStream : pdfStreams) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = pdfStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			InputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());

			return inputStream;
		} catch (IOException e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}


//	@PostMapping("/upload-pdf")
//	public ResponseEntity<String> uploadPDF(@RequestBody String base64String) {
//		// Process Base64 data and convert to PDF
//		String text = "hi hello world.iam testing bas64 string conversion to pdf";
//		String resource = Base64.getEncoder().encodeToString(text.getBytes());
//
//		return ResponseEntity.ok().body(resource);
//	}

        @DeleteMapping("/deletebyResumeId")
		public ResponseEntity<String> deleteResumeById(@RequestParam("resumeId") Long resumeId) throws Exception {
			         String message= service.deletedResumeByIdInDb(resumeId);
					 return new ResponseEntity<>(message, HttpStatus.OK);
		}

	@DeleteMapping("/deletebyUserId/{userId}")
	public ResponseEntity<String> deleteUserById(@PathVariable("userId") Integer userId) throws Exception {
		String message= service.deletedUserByIdInDb(userId);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}


	public static void main(String[] args) {
		SpringApplication.run(FileManagerApplication.class, args);
	}

}
