package transcription.hub.model;

import org.springframework.stereotype.Component;

@Component
public class HybridTransScript {

	private byte[] fileByte;
	private String fileName;
	private PayLoad payLoad;

	public byte[] getFileByte() {
		return fileByte;
	}

	public void setFileByte(byte[] fileByte) {
		this.fileByte = fileByte;
	}

	public PayLoad getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(PayLoad payLoad) {
		this.payLoad = payLoad;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}