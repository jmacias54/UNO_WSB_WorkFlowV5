package mx.com.amx.unotv.workflow.dto;

import java.sql.Timestamp;

public class PodcastDTO {

	private String fcIdContenido;
	private String fcIdContentOooyala;
	private String fcTitulo;
	private String fcDescripcion;
	private String fcUrlAudio;
	private String fcDuracion;
	private String fcSize;
	private Timestamp fdFechaPublicacion;
	
	public String getFcIdContenido() {
		return fcIdContenido;
	}
	public void setFcIdContenido(String fcIdContenido) {
		this.fcIdContenido = fcIdContenido;
	}
	public String getFcTitulo() {
		return fcTitulo;
	}
	public void setFcTitulo(String fcTitulo) {
		this.fcTitulo = fcTitulo;
	}
	public String getFcDescripcion() {
		return fcDescripcion;
	}
	public void setFcDescripcion(String fcDescripcion) {
		this.fcDescripcion = fcDescripcion;
	}
	public String getFcUrlAudio() {
		return fcUrlAudio;
	}
	public void setFcUrlAudio(String fcUrlAudio) {
		this.fcUrlAudio = fcUrlAudio;
	}
	public Timestamp getFdFechaPublicacion() {
		return fdFechaPublicacion;
	}
	public void setFdFechaPublicacion(Timestamp fdFechaPublicacion) {
		this.fdFechaPublicacion = fdFechaPublicacion;
	}
	public String getFcIdContentOooyala() {
		return fcIdContentOooyala;
	}
	public void setFcIdContentOooyala(String fcIdContentOooyala) {
		this.fcIdContentOooyala = fcIdContentOooyala;
	}
	public String getFcDuracion() {
		return fcDuracion;
	}
	public void setFcDuracion(String fcDuracion) {
		this.fcDuracion = fcDuracion;
	}
	public String getFcSize() {
		return fcSize;
	}
	public void setFcSize(String fcSize) {
		this.fcSize = fcSize;
	}
	
}
