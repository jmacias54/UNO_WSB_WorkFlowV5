package mx.com.amx.unotv.workflow.bo.exception;
public class PodcastBOException extends Exception {
	
	private static final long serialVersionUID = 1L;
	public PodcastBOException(String mensaje) {
        super(mensaje);
    }
	public PodcastBOException(Throwable exception) {
        super(exception);
    }	
    public PodcastBOException(String mensaje, Throwable exception) {
        super(mensaje, exception);
    }
}