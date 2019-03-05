package mx.com.amx.unotv.workflow.dto;

import java.sql.Timestamp;

public class InfoNotaDTO {
	
	private static final long serialVersionUID = 1L;
	
	private String fcIdContenido;
	private String fcIdCategoria;
	private String fcNombre;
	private String fcTitulo;	
	private String fcIdTipoNota;
	private String fcImgPrincipal;
	private Timestamp fdFechaPublicacion;
	
	public String getFcIdContenido() {
		return fcIdContenido;
	}
	public void setFcIdContenido(String fcIdContenido) {
		this.fcIdContenido = fcIdContenido;
	}
	public String getFcIdCategoria() {
		return fcIdCategoria;
	}
	public void setFcIdCategoria(String fcIdCategoria) {
		this.fcIdCategoria = fcIdCategoria;
	}
	public String getFcNombre() {
		return fcNombre;
	}
	public void setFcNombre(String fcNombre) {
		this.fcNombre = fcNombre;
	}
	public String getFcTitulo() {
		return fcTitulo;
	}
	public void setFcTitulo(String fcTitulo) {
		this.fcTitulo = fcTitulo;
	}
	public String getFcIdTipoNota() {
		return fcIdTipoNota;
	}
	public void setFcIdTipoNota(String fcIdTipoNota) {
		this.fcIdTipoNota = fcIdTipoNota;
	}
	public String getFcImgPrincipal() {
		return fcImgPrincipal;
	}
	public void setFcImgPrincipal(String fcImgPrincipal) {
		this.fcImgPrincipal = fcImgPrincipal;
	}
	public Timestamp getFdFechaPublicacion() {
		return fdFechaPublicacion;
	}
	public void setFdFechaPublicacion(Timestamp fdFechaPublicacion) {
		this.fdFechaPublicacion = fdFechaPublicacion;
	}

	
}
