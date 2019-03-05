package mx.com.amx.unotv.workflow.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.amx.unotv.workflow.bo.exception.CallElasticsearchBOException;
import mx.com.amx.unotv.workflow.bo.exception.JsonBOException;
import mx.com.amx.unotv.workflow.bo.exception.LlamadasWSBOException;
import mx.com.amx.unotv.workflow.bo.exception.LlamadasWSDAOException;
import mx.com.amx.unotv.workflow.bo.exception.PodcastBOException;
import mx.com.amx.unotv.workflow.bo.exception.ProcesoWorkflowException;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.ErrorNotaDTO;
import mx.com.amx.unotv.workflow.dto.NotaElasticDTO;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;
import mx.com.amx.unotv.workflow.dto.PushAmpDTO;
import mx.com.amx.unotv.workflow.dto.RequestArrayEntityPurgue;
import mx.com.amx.unotv.workflow.dto.VideoOoyalaDTO;
import mx.com.amx.unotv.workflow.utileria.PropertiesUtils;
import mx.com.amx.unotv.workflow.utileria.UtilWorkFlow;
import mx.com.amx.unotv.workflow.utileria.UtilsHTML;

public class ProcesoWorkflowBO {

	//LOG
	private static Logger LOG = Logger.getLogger(ProcesoWorkflowBO.class);
	
	@Autowired
	private LlamadasWSBO llamadasWSBO;
	@Autowired
	private LlamadasWSDAO llamadasWSDAO;
	@Autowired
	private PodcastBO podcastBO;
	@Autowired
	private JsonBO jsonBO;
	@Autowired
	private CorreoBO correoBO;	
	@Autowired 
	private RemplazaHTMLBO remplazaHTMLBO;
	@Autowired
	private CallElasticsearchBO callElasticsearchBO;
	@Autowired
	private AmpBO ampBO;
		
	public void procesoWorkflow() throws ProcesoWorkflowException
	{		
		LOG.debug("Inicia procesoWorkflow");
		try {			
		} catch (Exception e) {
			LOG.error("Exception en procesoWorkflow");
			throw new ProcesoWorkflowException(e.getMessage());
		}
		
	}
		
	/**
	 * Metodo que es utilizado para el proceso de publicación de una nota del portal de UNOTV,
	 * se inserta nota en la base de datos, se hace el llamado para el Push AMP y el insert a Facebook Instant Articles
	 * @param ContentDTO
	 * @return String
	 * @throws ProcesoWorkflowException
	 * * */
	public String publicarNota(ContentDTO contentDTO) throws ProcesoWorkflowException	
	{
		LOG.debug("*** Inicia publicarNota[BO] ***");			
		
		ParametrosDTO parametrosDTO = null;		
		List<String> listError = new ArrayList<String>();
		
		String id_facebook="";
		String carpetaContenido="";
		String urlNota = "";
	
		try {
			
			//Obtenemos archivo de propiedades
			parametrosDTO = PropertiesUtils.obtenerPropiedades();
			
			LOG.info("Frendy URL: "+contentDTO.getFcNombre());
			urlNota = parametrosDTO.getDominio()+"/"+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO);
			LOG.info("URL: "+urlNota);

			//Ruta donde se va guardar el html
			carpetaContenido = parametrosDTO.getPathFiles()+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO)+"/";
			LOG.info("carpetaContenido: "+carpetaContenido);
			LOG.info("cn= "+contentDTO.getPortal_cn());
			LOG.info("uid= "+contentDTO.getPortal_uid());
										
			//Validamos si la nota contiene video de ooyala
			LOG.debug("**TIPO DE NOTA: "+contentDTO.getFcIdTipoNota());							
			if(contentDTO.getFcIdTipoNota().equals("video") || contentDTO.getFcIdTipoNota().equals("multimedia")&& !contentDTO.getFcIdVideoOoyala().equals(""))
			{						
				//Obtebenos la informacion de ooyala				
				VideoOoyalaDTO ooyalaDTO = new VideoOoyalaDTO();				
				try {						
					if(!contentDTO.getFcIdVideoOoyala().trim().equals(""))
					{					
						ooyalaDTO = llamadasWSBO.getInfoVideo(contentDTO.getFcIdVideoOoyala(), parametrosDTO);
						LOG.debug("Ooyala source"+ooyalaDTO.getSource());
						LOG.debug("Ooyala Alternate_text"+ooyalaDTO.getAlternate_text());
					}
				} catch (LlamadasWSBOException le) {
					LOG.error("LlamadasWSBOException en publicarNota[BO]: "+le.getMessage());
					listError.add("Content id: " +contentDTO.getFcIdVideoOoyala()+"<br>Player id: "+contentDTO.getFcIdPlayerOoyala()+"<br>Pcode: "+contentDTO.getFcPCode()+"<br>"+le.getMessage());										
				}				
				contentDTO.setFcSourceVideo(ooyalaDTO.getSource() ==  null?"":ooyalaDTO.getSource());
				contentDTO.setFcAlternateTextVideo(ooyalaDTO.getAlternate_text()==null?"":ooyalaDTO.getAlternate_text());
				contentDTO.setFcDurationVideo(ooyalaDTO.getDuration()==null?"":ooyalaDTO.getDuration());
				contentDTO.setFcFileSizeVideo(ooyalaDTO.getFileSize()==null?"":ooyalaDTO.getFileSize());
			}		

			
			//Guardamos o actualizamos la nota en la base de datos.
			String nota_db = llamadasWSDAO.setNotaBD(contentDTO, parametrosDTO);

			
			//Procesamos podcast			
			try {
				podcastBO.procesoPodcast(contentDTO, parametrosDTO);
			} catch (PodcastBOException pe) {
				LOG.error("PodcastBOException"+pe.getMessage());
				listError.add("Content Audio id: " +contentDTO.getFcIdAudioOoyala()+"<br>"+pe.getMessage());
			}
			
			//Guardamos o actualizamos los Tags de la nota			
			ArrayList<String> arrayTag = obtieneTags(contentDTO.getFcTagsApp());			
			if(arrayTag!= null && arrayTag.size() > 0)
			{			
				LOG.debug("arrayTag: "+arrayTag.size());
				//Obtenemos el id del contenido que se guardo
				String id_contenido = llamadasWSDAO.getIdNotaByName(contentDTO.getFcNombre(), parametrosDTO);
				LOG.debug("id_contenido DB: "+id_contenido);
				LOG.debug("id_contenido Plantilla: "+contentDTO.getFcIdContenido());				
				if(!id_contenido.trim().equals(""))
				{
					try {
						llamadasWSDAO.insertTagsApp(id_contenido, contentDTO.getFcTagsApp(), parametrosDTO);
					} catch (LlamadasWSDAOException le) {
						LOG.error("LlamadasWSDAOException en insertTagsApp[BO]: "+le.getMessage());
						listError.add("insertTagsApp: "+le.getMessage());
					}			
				}
			}
			
			//Enviamos nota al elasticsearch
			try {				
				callElastic(contentDTO, parametrosDTO, "save", urlNota);				
			} catch (Exception e) {
				LOG.error("Exception  json: "+e.getMessage());
				listError.add("Exception elasticsearch: "+e.getMessage());
			}
			
			//Creamos estrcutura de directorios
			UtilWorkFlow.createFolders(carpetaContenido);
					
			//Creamos html de la nota
			remplazaHTMLBO.creaHTML(parametrosDTO, contentDTO);
									
			//Generamos el json del detalle para la app.			
			try {
				jsonBO.generaDetalleJson(contentDTO, parametrosDTO, carpetaContenido);				
			} catch (JsonBOException je) {
				LOG.error("Exception  json: "+je.getMessage());
				listError.add("generaDetalleJson: "+je.getMessage());
			}
			
			//Creamos html de AMP			
			try {				
				String html_amp=ampBO.generaAMP(parametrosDTO, contentDTO);				
				
				//Enviamos push de AMP
				if(!html_amp.equals("")){ 
					LOG.info("Enviamos PUSH al AMP");
					PushAmpDTO pushAMP = new PushAmpDTO();
					pushAMP.setFcIdCategoria(contentDTO.getFcIdCategoria());
					pushAMP.setFcIdContenido(contentDTO.getFcIdContenido());
					pushAMP.setFcNombre(contentDTO.getFcNombre());
					pushAMP.setFcSeccion(contentDTO.getFcSeccion());
					pushAMP.setFcTipoSeccion(contentDTO.getFcTipoSeccion());
					pushAMP.setFcTitulo(contentDTO.getFcTitulo());
					pushAMP.setFdFechaPublicacion(contentDTO.getFdFechaPublicacion());
					pushAMP.setHtmlAMP(html_amp);				
					//RespuestaWSAmpDTO  respuestaWSAMP=llamadasWSBO.sendPushAMP(pushAMP, parametrosDTO);
					//LOG.info("Respuesta AMP: "+respuestaWSAMP.getRespuesta());
				}				
			} catch (Exception ampe) {
				LOG.error("Exception  json: "+ampe.getMessage());
				listError.add("AMP Exception: "+ampe.getMessage());
			}
			
			
			//Enviamos Push de Instant Article
			try {							
				if(parametrosDTO.getAmbiente().equals("desarrollo"))
					id_facebook = "DEV";
				else
					id_facebook = llamadasWSBO.insertUpdateArticleFB(contentDTO, parametrosDTO);								
				LOG.debug("id_facebook: "+id_facebook);
			} catch (LlamadasWSBOException boe) {
				LOG.error("Exception  json: "+boe.getMessage());
				listError.add("Instant Article Exception: "+boe.getMessage());
			}			
			
			
			//Purgamos en akami si es una actualziación.
			if(nota_db.equals("UPDATE"))
			{
				try {											
					RequestArrayEntityPurgue requestArrayPurgue = new RequestArrayEntityPurgue();
					String[] urls = {urlNota+"/", urlNota+"/amp.html", urlNota+"/detalle.json"};
					requestArrayPurgue.setUrls(urls);													
					String responsePurga = "";
					if(parametrosDTO.getAmbiente().equals("desarrollo"))
						responsePurga = "DESARROLLO";
					else
						responsePurga = llamadasWSBO._purgaAkamai(parametrosDTO, requestArrayPurgue);					
					LOG.debug("responsePurga: "+responsePurga);
				} catch (Exception e) {
					LOG.error("AKAMI ERROR"+e.getMessage());
					listError.add("Akamai exception, purgue las url manual: "+e.getMessage());
				}
				
			}
						
			//Vemos si se generaron errores en la pubicacion de la nota.
			if(listError.size() > 0)
			{
				LOG.debug("Errores al generar la nota: "+listError.size());
				ErrorNotaDTO errorNotaDTO = new ErrorNotaDTO();				
				errorNotaDTO.setFrendyURL(contentDTO.getFcNombre());
				errorNotaDTO.setUrlnota(urlNota);
				errorNotaDTO.setAutor(contentDTO.getPortal_uid()+","+contentDTO.getPortal_cn());
				errorNotaDTO.setDescripcionError("Se genero la nota pero con errores.");				
				try {
					correoBO.enviaCorreoError(parametrosDTO, errorNotaDTO, listError);
				} catch (Exception e) {
					LOG.error("Exception al enviar el correo: "+e.getMessage());
				}				
			}
			
		//catch de errores y envio de correo	
		}catch (LlamadasWSDAOException de){
			LOG.error("LlamadasWSDAOException en publicarNota[BO]: "+de.getMessage());			
			enviaCorreoError(parametrosDTO, contentDTO, de.getMessage(),"Error al insertar/actualizar nota[DAO].");				
			throw new ProcesoWorkflowException(de.getMessage());			
		}catch (Exception e) {
			LOG.error("Exception publicarNota: ",e);
			enviaCorreoError(parametrosDTO, contentDTO, e.getMessage(),"Exception al insertar/actualizar nota.");
			throw new ProcesoWorkflowException(e.getMessage());
		}
		return id_facebook;
	}
		
	/**
	 * Metodo que es utilizado para el proceso de revisión de una nota del portal de UNOTV,
	 * se genera html para poder visualizarlo antes de publicar la nota.
	 * @param ContentDTO contentDTO
	 * @return String
	 * @throws ProcesoWorkflowException
	 * */
	public String revisarNota(ContentDTO contentDTO) throws ProcesoWorkflowException
	{
		LOG.debug("Inicia revisarNota en ProcesoWorkflowBO");
		LOG.debug("contentDTO: "+contentDTO);
		LOG.info("cn= "+contentDTO.getPortal_cn());
		LOG.info("uid= "+contentDTO.getPortal_uid());
		String url_revision_nota="";
		
		try {
			//Obtenemos properties
			ParametrosDTO parametrosDTO=PropertiesUtils.obtenerPropiedades();
						
			//Carpeta donde se va guardar el contenido
			String carpetaContenido=parametrosDTO.getPathFilesTest()+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO);
			LOG.debug("carpetaContenido: "+carpetaContenido);
						
			//Crea estrcutura de directorios
			UtilWorkFlow.createFolders(carpetaContenido);			
			
			parametrosDTO.setBaseURL(parametrosDTO.getBaseURLTest());
						
			//Crea HTML de nota			
			remplazaHTMLBO.creaHTMLTest(parametrosDTO, contentDTO);
			
			url_revision_nota=parametrosDTO.getAmbiente().equalsIgnoreCase("desarrollo")?parametrosDTO.getDominio()+"/portal/test-unotv/"+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO):
 				"http://pruebas-unotv.tmx-internacional.net"+"/"+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO);
			LOG.debug("url_revision_nota: "+url_revision_nota);
		} catch (Exception e) {
			LOG.error("Error en revisarNota: ",e);
			throw new ProcesoWorkflowException(e.getMessage());
		}
		return url_revision_nota;
	}
	
		
	/**
	 * Metodo que es utilizado para el proceso de caducación de una nota del portal de UNOTV,
	 * se elimina nota de la base de datos y de Facebook Instant Articles
	 * @param ContentDTO
	 * @return Boolean
	 * @throws LlamadasWSDAOException, LlamadasWSBOException, ProcesoWorkflowException
	 * */
	public Boolean caducarNota(ContentDTO contentDTO) throws ProcesoWorkflowException
	{
		LOG.debug("Inicia caducarNota en ProcesoWorkflowBO");
		LOG.debug("contentDTO: "+contentDTO);
		LOG.info("cn= "+contentDTO.getPortal_cn());
		LOG.info("uid= "+contentDTO.getPortal_uid());
		
		boolean success=false;
		ParametrosDTO parametrosDTO = null;
		try {
			
			//Obtenemos archivo de propiedades
			parametrosDTO = PropertiesUtils.obtenerPropiedades();
			
			//Url Nota
			String urlNota = parametrosDTO.getDominio()+"/"+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO);
			
			//Borramos los taga realacionados a la nota
			if(contentDTO.getFcTagsApp() != null && contentDTO.getFcTagsApp().length>0){
				success = llamadasWSDAO.deleteTagsApp(contentDTO, parametrosDTO);
			}
			
			//Borramos de la tabla de negocio
			success = llamadasWSDAO.deleteNotaBD(contentDTO, parametrosDTO);
			
			//Borramos de la tabla de historico
			success = llamadasWSDAO.deleteNotaHistoricoBD(contentDTO, parametrosDTO);			
			
			//Borramos elastic
			try {				
				callElastic(contentDTO, parametrosDTO, "delete", urlNota);				
			} catch (Exception e) {
				LOG.error("Exception  json: "+e.getMessage());				
			}
			
			//Borramos el Instant Article
			if(contentDTO.getFcFBArticleId() != null && !contentDTO.getFcFBArticleId().equals(""))			
		 		LOG.info(llamadasWSBO.deleteArticleFB(contentDTO.getFcFBArticleId(),parametrosDTO));
			else
		 		LOG.info("No se contaba con el articleFBId");		
			
			//Ruta para borrar html
			String carpetaContenido=parametrosDTO.getPathFiles()+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO);			
			LOG.debug("carpetaContenido: "+carpetaContenido);			
			//Borramos html
			boolean delteHTML = UtilsHTML.deleteHTML(carpetaContenido);
			LOG.debug("Se borro direcorio: "+delteHTML);
			
			//Purgamos en akami el borrado de nota.
			try {	
				
				RequestArrayEntityPurgue requestArrayPurgue = new RequestArrayEntityPurgue();
				String[] urls = {urlNota+"/", urlNota+"/amp.html", urlNota+"/detalle.json"};
				requestArrayPurgue.setUrls(urls);													
				String responsePurga = "";
				if(parametrosDTO.getAmbiente().equals("desarrollo"))
					responsePurga = "DESARROLLO";
				else
					responsePurga = llamadasWSBO._purgaAkamai(parametrosDTO, requestArrayPurgue);					
				LOG.debug("responsePurga: "+responsePurga);
			} catch (Exception e) {
				LOG.error("AKAMI ERROR"+e.getMessage());			
			}

		//catch errores en proceso de borrado	
		}catch (LlamadasWSDAOException daoException){
			LOG.error("LlamadasWSDAOException caducarNota[BO]: "+daoException.getMessage());
			enviaCorreoError(parametrosDTO, contentDTO, daoException.getMessage(),"Error al borrar la nota[DAO].");
			throw new ProcesoWorkflowException(daoException.getMessage());
		}catch (LlamadasWSBOException boException){
			LOG.error("Exception LlamadasWSBOException: "+boException.getMessage());
			enviaCorreoError(parametrosDTO, contentDTO, boException.getMessage(),"Error al borrar la nota[BO].");
			throw new ProcesoWorkflowException(boException.getMessage());
		}catch (Exception e) {
			LOG.error("Exception CaducarNota: ",e);
			enviaCorreoError(parametrosDTO, contentDTO, e.getMessage(),"Exception en el proceso de borrado.");
			throw new ProcesoWorkflowException(e.getMessage());
		}
		return success;
	}
	
	
	/*
	 * 
	 * */
	private ArrayList<String> obtieneTags(String[] arrayTag)
	{
		try {						
			ArrayList<String> arrayList = new ArrayList<String>();   									
			for (String tag : arrayTag) {
				if(!tag.trim().equals(""))
					arrayList.add(tag);				
			}			
			return arrayList;
		} catch (Exception e) {
			LOG.error("Exception en obtieneTags",e);
			return null;
		}		
	}
		
	/*
	 * Metodo para envio de correo de error
	 * 
	 * */
	private void enviaCorreoError(ParametrosDTO parametrosDTO, ContentDTO contentDTO, String errroMsg, String descError)
	{
		try {			
			String urlNota = parametrosDTO.getDominio()+"/"+UtilWorkFlow.getRutaContenido(contentDTO, parametrosDTO);			
			ErrorNotaDTO errorNotaDTO = new ErrorNotaDTO();				
			errorNotaDTO.setFrendyURL(contentDTO.getFcNombre());
			errorNotaDTO.setUrlnota(urlNota);
			errorNotaDTO.setAutor(contentDTO.getPortal_uid()+","+contentDTO.getPortal_cn());
			errorNotaDTO.setDescripcionError(descError);
			errorNotaDTO.setError(errroMsg);			
			correoBO.enviaCorreoError(parametrosDTO, errorNotaDTO);				
		} catch (Exception e) {
			LOG.error("Exception en enviaCorreoError: ",e);
		}		
	}
	
	
	/*
	 * 
	 * */
	private void callElastic(ContentDTO contentDTO, ParametrosDTO parametrosDTO, String method, String urlNota) throws Exception
	{		
		LOG.debug("Inicia callElastic");		
		try {			
			NotaElasticDTO notaElasticDTO = new NotaElasticDTO();			
			notaElasticDTO.setContenido(contentDTO.getClRtfContenido());
			notaElasticDTO.setFriendly_url(contentDTO.getFcNombre());
			notaElasticDTO.setTipo_seccion(contentDTO.getFcTipoSeccion());
			notaElasticDTO.setSeccion(contentDTO.getFcSeccion());
			notaElasticDTO.setCategoria(contentDTO.getFcIdCategoria());
			notaElasticDTO.setTitulo(contentDTO.getFcTitulo());
			notaElasticDTO.setDescripcion(contentDTO.getFcDescripcion());
			notaElasticDTO.setUrl_imagen(contentDTO.getFcImgPrincipal());
			notaElasticDTO.setUrl_nota(urlNota+"/");
			notaElasticDTO.setTipo(contentDTO.getFcIdTipoNota());
			notaElasticDTO.setKeywords(contentDTO.getFcKeywords());			
			
			//
			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			Date date = inputFormat.parse(contentDTO.getFdFechaPublicacion().toString());
			//
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
			String strFecha = dateFormat.format(date);			
			
			notaElasticDTO.setFecha_publicacion(strFecha);
			String respuesta = callElasticsearchBO.callElastic(notaElasticDTO, parametrosDTO, method);
			LOG.debug("respuesta elastic: "+respuesta);
		} catch (CallElasticsearchBOException ce) {
			LOG.error("CallElasticsearchBOException en callElastic: "+ce.getMessage());
			throw new Exception(ce.getMessage());
		} catch (Exception e) {
			LOG.error("Exception en callElastic: ",e);
			throw new Exception(e.getMessage());
		}
		
	}
	
	
}// FIN CLASE

