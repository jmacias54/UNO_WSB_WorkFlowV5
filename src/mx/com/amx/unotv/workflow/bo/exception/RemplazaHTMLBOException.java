package mx.com.amx.unotv.workflow.bo.exception;

public class RemplazaHTMLBOException  extends Exception{

private static final long serialVersionUID = 1L;
	
	public RemplazaHTMLBOException(String mensaje) {
        super(mensaje);
    }

	public RemplazaHTMLBOException(Throwable exception) {
        super(exception);
    }
	
    public RemplazaHTMLBOException(String mensaje, Throwable exception) {
        super(mensaje, exception);
    }	
}
