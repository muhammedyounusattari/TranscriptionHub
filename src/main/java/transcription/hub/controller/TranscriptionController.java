package transcription.hub.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import transcription.hub.model.PayLoad;

@RequestMapping("/transcription-engine")
public interface TranscriptionController {

	@RequestMapping(value="/getData", method = RequestMethod.POST, consumes = "application/json")
	public String getData(@RequestBody PayLoad payload);
}
