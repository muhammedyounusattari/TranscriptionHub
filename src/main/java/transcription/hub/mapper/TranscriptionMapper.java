package transcription.hub.mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import transcription.hub.model.PayLoad;

public class TranscriptionMapper implements InitializingBean {

	@Value("$media.assert")
	public String mediaAsrt;
	
	@Value("$post.hybrid.transcription")
	public String postTranscription;
	
	public String mediaAssert,postAssert;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.mediaAssert = mediaAsrt;
		this.postAssert = postTranscription;
	}
	
	private static ClientHttpRequestFactory getClientHttpRequestFactory() {
	    int timeout = 5000;
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
	    clientHttpRequestFactory.setConnectTimeout(timeout);
	    clientHttpRequestFactory.setReadTimeout(timeout);
	    return clientHttpRequestFactory;
	}
	
	public static RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	public static ResponseEntity<byte[]> getMedia(PayLoad payload) {

		HttpEntity<String> entity = new HttpEntity<String>("parameters", createHttpHeaders());
	//	String url = mediaAssert.replace("recordingId",  payload.getRecordingId());
		String url="https://api.veritone.com/api/recording/"+payload.getRecordingId()+"/asset";
	//	payload.setRecordingId("40392735");
		ResponseEntity<byte[]> response = getRestTemplate().exchange(url, HttpMethod.GET, entity, byte[].class);
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
		header.set("Authorization", "Bearer 3386b2:6d88a3f1152d4df7b14d93b22bf26c574ca753e3ac6641559de7fdf36be844c7");
		header.set("Cache-Control", "no-cache");
		header.set("Content-Type", "application/json");
		return header;
	}

	public static ResponseEntity<byte[]> postHybridTranscription(ResponseEntity<byte[]> response,String recordingId) {

		HttpEntity<Resource> requestEntity = createHttpEntity(recordingId);
		//String url = postAssert;
		String url="https://api.veritone.com/api/recording/"+recordingId+"/asset";
		ResponseEntity<byte[]> result = null;
		try {
			result = getRestTemplate().exchange(url, HttpMethod.POST, requestEntity,byte[].class);
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return result;
	}
	
	public static void createTranscriptionJob(PayLoad payload) {
		String url="https://api.veritone.com/api/job";
		
		String parameters = "{\"recordingId\": \""+payload.getRecordingId()+"\", \"tasks\": [{ \"engineId\": \"transcode-ffmpeg\", \"taskPayload\": { \"setAsPrimary\": true } }, { \"engineId\": \"transcribe-voicebase\" },{\"engineId\": \"insert-into-index\" } ] }";
		System.out.println(parameters);
		
		HttpEntity<String> entity = new HttpEntity<String>(parameters, createHttpHeaders());
		ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.POST, entity, String.class);
	//	String response = getRestTemplate().postForObject(url, entity, String.class);
		String str[] = response.getBody().split(",")[0].split("jobId");
		String jobId = str[1].substring(3,str[1].length()-1);
		System.out.println("jobId created...."+jobId);
		
		String status = createTranscriptionJobStatus(jobId);
		String s_i[]= status.split("status");
		
		String status_i = s_i[1].substring(3, s_i[1].length()-2);
		System.out.println("status_i: "+status_i);
		String ttml_script = "";
		//if("completed".equalsIgnoreCase(status_i))
			ttml_script = getTranscriptedScript(payload.getRecordingId());
		System.out.println(ttml_script);
	}

	private static String getTranscriptedScript(String string) {
		String url="https://api.veritone.com/api/recording/"+string+"/transcript";
		HttpEntity<String> entity = new HttpEntity<String>("parameters",createHttpHeaders());
		ResponseEntity<String> response = null;
		String result = "";
		try {
			response = getRestTemplate().exchange(url, HttpMethod.GET,entity,String.class);
			System.out.println("response: "+response.getBody());
			return response.getBody();
		} catch(Exception e) {
			result = e.getLocalizedMessage().split(" ")[0];
		}
		if(Integer.parseInt(result) == HttpStatus.NOT_FOUND.value()) {
			return "Could not find matching recording.";
		}
		return null;
	}

	private static String createTranscriptionJobStatus(String jobId) {
		String url="https://api.veritone.com/api/job/"+jobId;
		HttpEntity<String> entity = new HttpEntity<String>("parameters",createHttpHeaders());
		
		ResponseEntity<String> response = getRestTemplate().exchange(url,HttpMethod.GET,entity,String.class);
		return response.getBody();
	}

	private static HttpEntity<Resource> createHttpEntity(String recordingId) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Content-Type", "audio/wav");
		requestHeaders.add("Authorization", "Bearer 3386b2:6d88a3f1152d4df7b14d93b22bf26c574ca753e3ac6641559de7fdf36be844c7");
		requestHeaders.add("Accept", "application/json");
		requestHeaders.add("x-veritone-asset-type", "media");
		requestHeaders.add("Cache-Control", "no-cache");	
		return new HttpEntity<Resource>(new FileSystemResource(new File("transcription.engine/"+recordingId+".wav")), requestHeaders);
	}
}