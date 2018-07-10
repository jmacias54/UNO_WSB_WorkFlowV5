package mx.com.amx.unotv.workflow.dto;

public class ErrorNotaDTO {
	
	private String urlnota;
	private String frendyURL;
	private String autor;
	private String descripcionError;
	private String error;
	
	public String getUrlnota() {
		return urlnota;
	}
	public void setUrlnota(String urlnota) {
		this.urlnota = urlnota;
	}
	public String getFrendyURL() {
		return frendyURL;
	}
	public void setFrendyURL(String frendyURL) {
		this.frendyURL = frendyURL;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getDescripcionError() {
		return descripcionError;
	}
	public void setDescripcionError(String descripcionError) {
		this.descripcionError = descripcionError;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
