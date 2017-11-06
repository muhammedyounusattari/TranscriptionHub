package transcription.hub.mapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import transcription.hub.model.HybridTransScript;
import transcription.hub.model.PayLoad;

public class TranscriptionMapper implements InitializingBean {

	@Value("$media.assert")
	public String mediaAsrt;
	
	@Value("$post.hybrid.transcription")
	public String postTranscription;
	
	public static String mediaAssert,postAssert;
	@Override
	public void afterPropertiesSet() throws Exception {
		this.mediaAssert = mediaAsrt;
		this.postAssert = postTranscription;
	}
	
	public static ResponseEntity<byte[]> getMedia(PayLoad payload) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = createHttpHeaders();
		
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
	//	String url = mediaAssert.replace("recordingId",  payload.getRecordingId());
		String url="https://api.veritone.com/api/recording/40392735/asset";
		payload.setRecordingId("40392735");
		ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
		System.out.println(response);
		if (response.getStatusCode() == HttpStatus.OK) {
			try {
				Files.write(Paths.get(payload.getRecordingId() + ".wav"), response.getBody());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("File got successfully downloaded from media assert.....");
		}
		return response;

	}

	private static HttpHeaders createHttpHeaders() {
		HttpHeaders header = new HttpHeaders();
		header.set("authorization", "Bearer 3386b2:6d88a3f1152d4df7b14d93b22bf26c574ca753e3ac6641559de7fdf36be844c7");
		header.set("cache-control", "no-cache");
		header.set("Content-type", "application/json");
		return header;
	}

	public static String postHybridTranscription(ResponseEntity<byte[]> response) {

		final HttpEntity<byte[]> requestEntity = createHttpEntity(response.getBody());
		//String url = postAssert;
		String url="https://api.veritone.com/api/recording/40392735/asset";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity,String.class);
		System.out.println("File sent successfully for transcription" + result.getBody());
		
	
		
		
		
		HybridTransScript hybridData = new HybridTransScript();
		hybridData.setFileByte(response.getBody());
		//hybridData.setFileName("fileName...");
		//hybridData.setPayLoad(payload);

		// System.out.println("params are =>" + hybridData);
		ObjectMapper mapper = new ObjectMapper();
		String postData = null;
		try {
			postData = mapper.writeValueAsString(hybridData);
//			System.out.println("data sent..." + postData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// System.out.println("request is=>" + request);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				//String url = postAssert;
				String url="https://api.veritone.com/api/recording/40392735/asset";
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
						String.class);
				System.out.println("File sent successfully for transcription" + result.getBody());

			}
		}, 10000);
		// ResponseEntity<String> result = restTemplate.exchange(url,
		// HttpMethod.POST, requestEntity, String.class);
		return "Some text encripted....";
	}
	
	private static HttpEntity<byte[]> createHttpEntity(byte[] bs) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Content-Type", "audio/wav");
		requestHeaders.add("authorization", "Bearer 3386b2:6d88a3f1152d4df7b14d93b22bf26c574ca753e3ac6641559de7fdf36be844c7");
		requestHeaders.add("Accept", "application/json");
		requestHeaders.add("x-veritone-asset-type", "media");
		requestHeaders.add("cache-control", "no-cache");		
		return new HttpEntity<byte[]>(bs, requestHeaders);
	}
	
}
