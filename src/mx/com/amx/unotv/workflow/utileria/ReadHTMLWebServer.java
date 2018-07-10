package mx.com.amx.unotv.workflow.utileria;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import mx.com.amx.unotv.workflow.bo.exception.ProcesoWorkflowException;

import org.apache.log4j.Logger;

public class ReadHTMLWebServer {
	
	static Logger LOG = Logger.getLogger(ReadHTMLWebServer.class);
	
	/**
	 * Metodo que te permite descargar un archivo drectamente del WebServer
	 * @param  String url_a_conectar es la dirección a la cual se va a conectar para descargar el archivo.
	 * @return String Se regresa todo el archivo en formato cadena, por ejemplo todo el contenido de  un html, o todo el contenido de un css	
	 * @throws ProcesoWorkflowException
	 * */
	public String getResourceWebServer(String url_a_conectar) throws ProcesoWorkflowException
	{
		LOG.debug("Inicia getResourceWebServer");		
		URL url;
		StringBuffer HTML = new StringBuffer();
		try {
			url = new URL(url_a_conectar);
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));			
			String inputLine;			
			while ((inputLine = br.readLine()) != null) {
				HTML.append(inputLine+"\n");
			}
			br.close();
		} catch (MalformedURLException e) {
			LOG.error("Error getResourceWebServer MalformedURLException: ",e);
		} catch (IOException e) {
			LOG.error("Error getResourceWebServer IOException: ",e);
		} catch (Exception e) {
			LOG.error("Error getResourceWebServer: ",e);
			throw new ProcesoWorkflowException(e.getMessage());
		}
		return HTML.toString();
	}
}