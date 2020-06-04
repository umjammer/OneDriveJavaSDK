package de.tuberlin.onedrivesdk.networking;

import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Wrapper for OkHTTP Responses  received by the ConcreteOneDriveSDK
 */
public class ConcreteOneResponse implements OneResponse {
	private Response response;
	private byte[] body;

	public ConcreteOneResponse(Response response) throws IOException {
		this.response = response;
    }

	private byte[] getBody() {
	    if (body == null) {
            try {
                this.body = response.body().bytes();
            } catch (Exception e) {
                this.body = new byte[0];
            }
	    }
	    return body;
	}

	@Override
	public String getBodyAsString() {
		return new String(getBody()); // TODO encoding
	}

    @Override
    public byte[] getBodyAsBytes() {
        return getBody();
    }

    @Override
    public InputStream getBodyAsStream() {
        return this.response.body().byteStream();
    }

    @Override
	public int getStatusCode() {
		return this.response.code();
	}
	
	@Override
	public List<String> getHeaders(String key){
		return this.response.headers(key);
	}
	
	@Override
	public boolean wasSuccess() {
		return this.response.isSuccessful();
	}
	
	@Override
	public String getHeader(String key) {
		return this.response.header(key);
	}

	@Override
	public String toString() {
		return "Response{" +
				"response=" + response +
				", body='" + getBodyAsString() + '\'' +
				'}';
	}
}
