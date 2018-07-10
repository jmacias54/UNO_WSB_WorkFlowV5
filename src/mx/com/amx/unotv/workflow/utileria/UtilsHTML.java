package mx.com.amx.unotv.workflow.utileria;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import mx.com.amx.unotv.workflow.bo.exception.ProcesoWorkflowException;

import org.apache.commons.lang.CharUtils;
import org.apache.log4j.Logger;

public class UtilsHTML {

	//LOG
	private final static Logger LOG = Logger.getLogger(UtilWorkFlow.class);	
	
	/**
	 * 
	 * */
	public static boolean deleteHTML(String pathHTML) 
	{
		LOG.debug("Inicia deleteHTML");
		LOG.debug("pathHTML: "+pathHTML);
		try {
			File fileHTML = new File(pathHTML+"/index.html") ;
			File fileAMP = new File(pathHTML+"/amp.html") ;
			File fileJSON = new File(pathHTML+"/detalle.json") ;

			//Borramos archivos
			fileHTML.delete();
			fileAMP.delete();
			fileJSON.delete();
			
			//Borramos directorio
			File directorio = new File(pathHTML) ;
			return directorio.delete();
			
		} catch (Exception e) {
			LOG.error("Exception en deleteHTML: ",e);
			return false;
		} 		
	}
	
	
	
	/**
	 * Regresa un boolean true si el directorio se pudo crear, o un false en caso contrario
	 * @param  String Path del directorio a ser creado
	 * @return boolean
	 * @throws ProcesoWorkflowException
	 * */
	public static boolean createFolders(String carpetaContenido) throws ProcesoWorkflowException {
		boolean success = false;
		try {						
			File carpetas = new File(carpetaContenido) ;
			if(!carpetas.exists()) {   
				success = carpetas.mkdirs();					
			} else 
				success = true;							
		} catch (Exception e) {
			success = false;
			LOG.error("Ocurrio error al crear las carpetas: ", e);
			throw new ProcesoWorkflowException(e.getMessage());
		} 
		return success;
	}
	
	/**
	 * Se escribe fisicamente un archivo de tipo HTML
	 * @param String, path del directorio donde se va a guardar el HTML
	 * @param String, codigo HTML
	 * @return boolean
	 * @author Fernando
	 * */
	public static boolean writeHTML(String rutaHMTL, String HTML) {
		boolean success = false;
		try {
			FileWriter fichero = null;
	        PrintWriter pw = null;
	        try {
				fichero = new FileWriter(rutaHMTL);				
				pw = new PrintWriter(fichero);							
				pw.println(HTML);
				pw.close();
				success = true;
			} catch(Exception e){			
				LOG.error("Error al obtener la plantilla " + rutaHMTL + ": ", e);
				success = false;
			}finally{
				try{                    			              
					if(null!= fichero)
						fichero.close();
				}catch (Exception e2){
					success = false;
					LOG.error("Error al cerrar el file: ", e2);
				}
			}	
		} catch(Exception e) {
			success = false;
			LOG.error("Fallo al crear la plantilla: ", e);
		}		
		return success;
	}	
	
	/**
	* Metodo que cambia caracteres especiales por caracteres en código HTML
	* @param  String     
	* @return String
	* */
	public static String cambiaCaracteres(String texto) 
	{
		
		texto = texto.replaceAll("á", "&#225;");
		texto = texto.replaceAll("é", "&#233;");
		texto = texto.replaceAll("í", "&#237;");
		texto = texto.replaceAll("ó", "&#243;");
		texto = texto.replaceAll("ú", "&#250;");  
		texto = texto.replaceAll("Á", "&#193;");
		texto = texto.replaceAll("É", "&#201;");
		texto = texto.replaceAll("Í", "&#205;");
		texto = texto.replaceAll("Ó", "&#211;");
		texto = texto.replaceAll("Ú", "&#218;");
		texto = texto.replaceAll("Ñ", "&#209;");
		texto = texto.replaceAll("ñ", "&#241;");        
		texto = texto.replaceAll("ª", "&#170;");          
		texto = texto.replaceAll("ä", "&#228;");
		texto = texto.replaceAll("ë", "&#235;");
		texto = texto.replaceAll("ï", "&#239;");
		texto = texto.replaceAll("ö", "&#246;");
		texto = texto.replaceAll("ü", "&#252;");    
		texto = texto.replaceAll("Ä", "&#196;");
		texto = texto.replaceAll("Ë", "&#203;");
		texto = texto.replaceAll("Ï", "&#207;");
		texto = texto.replaceAll("Ö", "&#214;");
		texto = texto.replaceAll("Ü", "&#220;");
		texto = texto.replaceAll("¿", "&#191;");
		texto = texto.replaceAll("“", "&#8220;");        
		texto = texto.replaceAll("”", "&#8221;");
		texto = texto.replaceAll("‘", "&#8216;");
		texto = texto.replaceAll("’", "&#8217;");
		texto = texto.replaceAll("…", "...");
		texto = texto.replaceAll("¡", "&#161;");
		texto = texto.replaceAll("¿", "&#191;");
		texto = texto.replaceAll("°", "&#176;");		
		texto = texto.replaceAll("–", "&#8211;");
		texto = texto.replaceAll("—", "&#8212;");		
		texto = texto.replaceAll("ç", "&#231;");
		texto = texto.replaceAll("Ç", "&#199;");
		texto = texto.replaceAll("'", "&#39;");		
		return texto;
	}
	
	/*
	 * Se lleva a cabo un encode para los caracteres especiales
	 * @param  String, Cadena a ser encodeada
	 * @return String, Se regresa la cadena encodeada
	 * @author Fernando
	 * */	
	public static String htmlEncode(final String string) 
	{
	  final StringBuffer stringBuffer = new StringBuffer();
	  for (int i = 0; i < string.length(); i++) {
	    final Character character = string.charAt(i);
	    if (CharUtils.isAscii(character)) {
	      // Encode common HTML equivalent characters
	      stringBuffer.append(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(character.toString()));
	    } else {
	      // Why isn't this done in escapeHtml4()?
	      stringBuffer.append(
	          String.format("&#x%x;",Character.codePointAt(string, i)));
	    }
	  }
	  return stringBuffer.toString();
	}
	
}
