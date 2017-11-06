package transcription.hub.configuration;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import transcription.hub.model.PayLoad;
import transcription.hub.service.TranscriptionService;
import transcription.hub.service.TranscriptionServiceImpl;

@SpringBootApplication
//@EnableWebMvc
@ComponentScan("transcription")
public class TranscriptionConfiguration {

	@Autowired
	TranscriptionService service;
	
	public static void main(String[] args) {
		TranscriptionService service =new TranscriptionServiceImpl();
		PayLoad payload = new PayLoad();
		service.getMediaAssert(payload);
	}
	
}
