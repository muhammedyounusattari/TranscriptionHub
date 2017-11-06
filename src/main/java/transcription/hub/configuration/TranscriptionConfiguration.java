package transcription.hub.configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import transcription.hub.model.PayLoad;
import transcription.hub.service.TranscriptionService;
import transcription.hub.service.TranscriptionServiceImpl;

@SpringBootApplication
//@EnableWebMvc
@ComponentScan("transcription")
public class TranscriptionConfiguration {

	public static void main(String[] args) {
		//SpringApplication.run(TranscriptionConfiguration.class, args);

		
		ClassLoader classLoader = new TranscriptionConfiguration().getClass().getClassLoader();
		BufferedReader br = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("payload.json")));

		Gson gson = new Gson();
		PayLoad payLoad = gson.fromJson(br, PayLoad.class);
		String recordId = payLoad.getRecordingId();
		System.out.println("recordId....." + recordId);
		
		TranscriptionService service = new TranscriptionServiceImpl();
		service.processTranscription(payLoad);
	
	}
}