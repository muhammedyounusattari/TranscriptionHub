package transcription.hub.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import transcription.hub.controller.TranscriptionController;
import transcription.hub.model.PayLoad;
import transcription.hub.service.TranscriptionService;

@RestController
public class TranscriptionControllerImpl implements TranscriptionController {

	@Autowired
	TranscriptionService service;
	
	public String getData(@RequestBody PayLoad payload) {
		service.processTranscription(payload);
		return null;
	}
}
