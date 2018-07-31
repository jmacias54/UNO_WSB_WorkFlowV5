package mx.com.amx.unotv.workflow.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import mx.com.amx.unotv.workflow.bo.exception.LlamadasWSDAOException;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.ExtraInfoContentDTO;
import mx.com.amx.unotv.workflow.dto.NotaListResponse;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;
import mx.com.amx.unotv.workflow.dto.PodcastDTO;

public class LlamadasWSDAO {
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	private RestTemplate restTemplate;
	private HttpHeaders headers = new HttpHeaders();
	
	/**
	 * Constructor de la clase LlamadasWSDAO, seteamos el ContentType para que sea del tipo
	 * APPLICATION_JSON
	 * */
	public LlamadasWSDAO() {
		super();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();

	        if ( factory instanceof SimpleClientHttpRequestFactory)
	        {
	            ((SimpleClientHttpRequestFactory) factory).setConnectTimeout( 50 * 1000 );
	            ((SimpleClientHttpRequestFactory) factory).setReadTimeout( 50 * 1000 );
	        }
	        else if ( factory instanceof HttpComponentsClientHttpRequestFactory)
	        {
	            ((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout( 50 * 1000);
	            ((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout( 50 * 1000);
	            
	        }
	        restTemplate.setRequestFactory( factory );
	        headers.setContentType(MediaType.APPLICATION_JSON);	        
	}
	
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para verificar la existencia del contenido, si este no existe es tratado como
	 * un insert, por otro lado si el contenido ya existe, se hace un actualización del mismo.
	 * @param ContentDTO
	 * @param parametrosDTO
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	public String setNotaBD(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("*** Inicia setNotaBD [WS DAO] ***");				
		int respuesta = 0;
		String nota_db = "";
		try {	
			
			String URL_WS_BASE = parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow();						
			
			//Validamos si la nota existe en la BD									
			String urlNotaRegistrada = URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_existeNotaRegistrada();
			
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			LOG.debug("urlNotaRegistrada: "+urlNotaRegistrada);
			
			respuesta = restTemplate.postForObject(urlNotaRegistrada, entity, Integer.class);
			LOG.debug("La nota esta resgitrada: "+respuesta);
			
			//Procesamos la nota en la base de datos
			if(respuesta > 0)
			{			
				LOG.info("Se actualiza la nota");
				LOG.debug(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_updateNotaBD());
				respuesta=restTemplate.postForObject(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_updateNotaBD(), entity, Integer.class);
				LOG.debug("updateNotaBD: "+respuesta);
				LOG.debug(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_updateNotaHistoricoBD());
				respuesta=restTemplate.postForObject(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_updateNotaHistoricoBD(), entity, Integer.class);
				LOG.debug("updateNotaHistoricoBD: "+respuesta);											
				LOG.info("graylog-nota-actualizada");
				LOG.info("graylog-actualiza-"+contentDTO.getFcIdTipoNota());				
				LOG.info("graylog-actualiza-"+contentDTO.getFcSeccion());
				LOG.info("graylog-actualiza-"+contentDTO.getFcTipoSeccion());
				LOG.info("graylog-actualiza-"+contentDTO.getFcIdCategoria());
				LOG.info("graylog-actualiza-"+contentDTO.getPortal_uid() );
				nota_db="UPDATE";
			}
			else
			{		
				LOG.info("Se inserta nueva nota");
				LOG.debug(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_insertNotaBD());
				respuesta = restTemplate.postForObject(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_insertNotaBD(), entity, Integer.class);				
				LOG.debug("insertNotaBD: "+respuesta);				
				respuesta=restTemplate.postForObject(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_insertNotaHistoricoBD(), entity, Integer.class);
				LOG.debug(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_insertNotaHistoricoBD());
				LOG.debug("insertNotaHistoricoBD: "+respuesta);								
				LOG.info("graylog-nota-insertada");
				LOG.info("graylog-inserta-"+contentDTO.getFcIdTipoNota());				
				LOG.info("graylog-inserta-"+contentDTO.getFcSeccion());
				LOG.info("graylog-inserta-"+contentDTO.getFcTipoSeccion());
				LOG.info("graylog-inserta-"+contentDTO.getFcIdCategoria());
				LOG.info("graylog-inserta-"+contentDTO.getPortal_uid() );				
				nota_db="INSERT";
			}				
			
			return nota_db;
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException setNotaBD [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException setNotaBD [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		}catch(Exception e) {																
			LOG.error("Exception setNotaBD [WS DAO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
	}	
	
	
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para obtener el id de una nota previamente guardada en la base 
	 * de datos mediante el campo fc_nombre
	 * @param String nameContent
	 * @return String
	 * @throws LlamadasWSDAOException
	 * */
	public String getIdNotaByName(String nombreContenido, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia getIdNotaByName en LlamadasWSDAO");
		LOG.debug("nombreContenido : "+nombreContenido);		
		try {					
			String URL_WS = parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+parametrosDTO.getMet_wsd_WorkFlow_getIdNotaByName();
			LOG.debug("URL_WS: "+URL_WS);			
			HttpEntity<String> entity = new HttpEntity<String>( nombreContenido );
			return restTemplate.postForObject(URL_WS, entity, String.class);
			
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException getIdNotaByName [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException getIdNotaByName [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception getIdNotaByName [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}			
	}
	
	/**
	 * Método que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para hacer un insert en una tabla intermedia que lleva el registro 
	 * del id_contenido y su relación con con el id_tag_app 
	 * @param String id_contenido
	 * @param String[] tags_app_id
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	public boolean insertTagsApp(String idContenido, String[] TagsApp, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia insertTagsApp[WS DAO]");
		LOG.debug("idContenido : "+idContenido);
		LOG.debug("TagsApp : "+TagsApp);
		
		boolean success = false;
		
		try {	
			ContentDTO contentDTO=new ContentDTO();
			contentDTO.setFcIdContenido(idContenido);						
			
			deleteTagsApp(contentDTO, parametrosDTO);
				for (String idTag:TagsApp) 
				{
					if(!idTag.trim().equals(""))
						success=insertNotaTag(idContenido, idTag, parametrosDTO);					
					LOG.debug("Inserto Tag "+idTag+": "+success);
				}
				
		} catch(Exception e) {
			LOG.error("Error insertTagsApp [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
		return success;
	}
	
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para hacer un insert en una tabla intermedia que lleva el registro 
	 * del id_contenido y su relación con con el id_tag_app 
	 * @param  id_contenido, Id del contenido para insertar en la tabla intermedia
   	 * @param  id_tag,Id del Tag para insertar en la tabla intermedia
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	private boolean insertNotaTag(String id_contenido, String id_tag, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia insertNotaTag en LlamadasWSDAO");
		LOG.debug("id_contenido : "+id_contenido);
		LOG.debug("id_tag : "+id_tag);		
		try {	
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+parametrosDTO.getMet_wsd_WorkFlow_insertNotaTag();			
			LOG.debug("URL_WS:" +URL_WS);
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("idContenido", id_contenido);
			parts.add("idTag", id_tag);
			return restTemplate.postForObject(URL_WS, parts, Boolean.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException insertNotaTag [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException insertNotaTag [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());	
		} catch(Exception e) {
			LOG.error("Error insertNotaTag [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		

	}
	
	
	
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para llevar a cabo un delete en la tabla intermedia que lleva a
	 * cabo el control de la relación entre el id del contenido y el id tag de la app V1.0
	 * @param  ContentDTO
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	public boolean deleteTagsApp(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia deleteTagsApp en LlamadasWSDAO");		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+parametrosDTO.getMet_wsd_WorkFlow_deleteNotaTag();
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			return restTemplate.postForObject(URL_WS, entity, Boolean.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException deleteTagsApp [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException deleteTagsApp [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());		
		} catch(Exception e) {
			LOG.error("Error deleteTagsApp [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		

	}
		
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para llevar a cabo un delete en la tabla de negocio
	 * @param  ContentDTO
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	public boolean deleteNotaBD(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia deleteNotaBD en LlamadasWSDAO");		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+parametrosDTO.getMet_wsd_WorkFlow_deleteNotaBD();
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			return restTemplate.postForObject(URL_WS, entity, Boolean.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException deleteNotaBD [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException deleteNotaBD [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception deleteNotaBD [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
		
	}

	/**
	 * Método que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para llevar a cabo un delete en la tabla de historicos
	 * @param  ContentDTO
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	public boolean deleteNotaHistoricoBD(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia deleteNotaHistoricoBD en LlamadasWSDAO");
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+parametrosDTO.getMet_wsd_WorkFlow_deleteNotaHistoricoBD();
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			return restTemplate.postForObject(URL_WS, entity, Boolean.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException deleteNotaHistoricoBD [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException deleteNotaHistoricoBD [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());		
		} catch(Exception e) {
			LOG.error("Error deleteNotaHistoricoBD [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
	
	}
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para obtener las notas del magazine
	 * @param  String, idMagazine es el identificador del magazine para obtener sus notas
	 * @param  String, idContenido es el parametro que se utiliza para no repetir notas
	 * @return List<ContentDTO>, lista de notas del magazine
	 * @throws LlamadasWSDAOException
	 * */
	public List<ContentDTO> getNotasMagazine(String idMagazine, String idContenido, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia getNotasMagazine en LlamadasWSDAO");
		LOG.debug("idMagazine : "+idMagazine);
		LOG.debug("idContenido : "+idContenido);
		ContentDTO[] arrayContentsRecibidos=null;
		ArrayList<ContentDTO> listRelacionadas=null;
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+"/MX_UNO_WSD_WorkFlow/rest/workflow-controller/getNotasMagazine";
			LOG.debug("URL_WS: "+URL_WS);
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("idMagazine", idMagazine);
			parts.add("idContenido", idContenido);
			arrayContentsRecibidos=restTemplate.postForObject(URL_WS, parts, ContentDTO[].class);
			listRelacionadas=new ArrayList<ContentDTO>(Arrays.asList(arrayContentsRecibidos));			
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException getNotasMagazine [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException getNotasMagazine [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());	
		} catch(Exception e) {
			LOG.error("Error getNotasMagazine[WS DAO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
		return listRelacionadas;	
	}
	
	
	/**
	 * Método que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para obtener una lista de notas relacionadas por id_categoria
	 * @param  ContentDTO
	 * @return List<ContentDTO>
	 * 		   lista de notas de una misma categoria
	 * @throws LlamadasWSDAOException
	 * @author jesus
	 * */
	public List<ContentDTO> getRelacionadasbyIdCategoria(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia getRelacionadasbyIdCategoria en LlamadasWSDAO");		
		ContentDTO[] arrayContentsRecibidos=null;
		ArrayList<ContentDTO> listRelacionadas=null;
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+parametrosDTO.getMet_wsd_WorkFlow_getRelacionadasbyIdCategoria();
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			arrayContentsRecibidos=restTemplate.postForObject(URL_WS, entity, ContentDTO[].class);
			listRelacionadas=new ArrayList<ContentDTO>(Arrays.asList(arrayContentsRecibidos));			
		} catch(Exception e) {
			LOG.error("Error getRelacionadasbyIdCategoria[WS DAO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
		return listRelacionadas;	
	}
	
		
	/**
	 * 
	 * */
	public ExtraInfoContentDTO _getExtraInfoContent(String friendlyURL, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia _getExtraInfoContent [WS DAO]");
		LOG.debug("friendlyURL : "+friendlyURL);		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+parametrosDTO.getMet_wsd_WorkFlow_getExtraInfoContent();
			LOG.debug("URL_WS: "+URL_WS);			
			HttpEntity<String> entity = new HttpEntity<String>(friendlyURL);
			return restTemplate.postForObject(URL_WS, entity, ExtraInfoContentDTO.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _getExtraInfoContent [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _getExtraInfoContent [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());	
		} catch(Exception e) {
			LOG.error("Error _getExtraInfoContent[WS DAO]:",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}			
	}
	
	
	/**
	 * Metodo para hacer el llamado al WS que inserta un podcast 
	 * @param  PodcastDTO
	 * @return int
	 * @throws LlamadasWSDAOException
	 * */
	public int _insertPodcast(PodcastDTO podcastDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia _insertPodcast en LlamadasWSDAO");		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+"/rest/podcast-dao-controller/insert-podcast";
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<PodcastDTO> entity = new HttpEntity<PodcastDTO>( podcastDTO );
			return restTemplate.postForObject(URL_WS, entity, Integer.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _insertPodcast [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _insertPodcast [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception _insertPodcast [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}				
	}
	
	
	/**
	 * Metodo para hacer el llamado al WS que inserta un podcast 
	 * @param  PodcastDTO
	 * @return int
	 * @throws LlamadasWSDAOException
	 * */
	public int _updatePodcast(PodcastDTO podcastDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia _updatePodcast en LlamadasWSDAO");		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+"/rest/podcast-dao-controller/update-podcast";
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<PodcastDTO> entity = new HttpEntity<PodcastDTO>( podcastDTO );
			return restTemplate.postForObject(URL_WS, entity, Integer.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _updatePodcast [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _updatePodcast [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception _updatePodcast [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}				
	}
	
	/**
	 * Metodo para hacer el llamado al WS que inserta un podcast 
	 * @param  PodcastDTO
	 * @return int
	 * @throws LlamadasWSDAOException
	 * */
	public int _getPodcast(String idContenido, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia _getPodcast en LlamadasWSDAO");		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+"/rest/podcast-dao-controller/get-podcast";
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<String> entity = new HttpEntity<String>( idContenido );
			return restTemplate.postForObject(URL_WS, entity, Integer.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _getPodcast [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _getPodcast [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception _getPodcast [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}				
	}
	
	/**
	 * 
	 * @param  idContenido
	 * @return int
	 * @throws LlamadasWSDAOException
	 * */
	public int _deletePodcast(String idContenido, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia _deletePodcast en LlamadasWSDAO");		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+"/rest/podcast-dao-controller/delete-podcast";
			LOG.debug("URL_WS: "+URL_WS);
			HttpEntity<String> entity = new HttpEntity<String>(idContenido);
			return restTemplate.postForObject(URL_WS, entity, Integer.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _deletePodcast [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _deletePodcast [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception _deletePodcast [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
		
	}
	
	
	/**
	 * @param  friendlyURL
	 * @return url_ws
	 * @throws LlamadasWSDAOException
	 * */
	public NotaListResponse _obtieneRelacionadas(String friendlyURL, String idCategoria, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException 
	{
		LOG.debug("Inicia _obtieneRelacionadas en LlamadasWSDAO");		
		try {
			String url_ws = parametrosDTO.getUrl_dominio_dat()+"/UNO_WSD_ComponentesV5/rest/nNota/lastNotesFindByIdCategoriaLimit/"+idCategoria+"/6/"+friendlyURL+"/";
			LOG.debug("URL_WS: "+url_ws);			
			HttpEntity<String> entity = new HttpEntity<String>("Accept=application/json; charset=utf-8", headers);
			return restTemplate.postForObject(url_ws, entity, NotaListResponse.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _obtieneRelacionadas [WS DAO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _obtieneRelacionadas [WS DAO]: ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception _deletePodcast [BO]: ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}
		
	}
	
	
	/* =========================================================================================================== */
	/* ========================== Operation's with uno_mx_n_nota_tmp_revision  ========================== */
	/* =========================================================================================================== */

	
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para llevar a cabo un delete en la tabla de negocio
	 * @param  ContentDTO
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	public boolean _deleteRevNotaBD(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException {
		LOG.debug(" ---  deleteRevNotaBD en LlamadasWSDAO --- ");		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow()+"/rest/workflow-controller/deleteNotaBD";
			LOG.debug(" *** URL_WS: "+URL_WS);
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			return restTemplate.postForObject(URL_WS, entity, Boolean.class);
		}catch(RestClientResponseException rre){
			LOG.error("--- RestClientResponseException deleteRevNotaBD [WS DAO]: --- " + rre.getResponseBodyAsString());
			LOG.error("--- RestClientResponseException deleteRevNotaBD [WS DAO]: --- ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("--- Exception deleteRevNotaBD [BO]: --- ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
		
	}
	
	
	
	/**
	 * Metodo que es utilizado para hacer una llamada al servicio que se conecta a la
	 * base de datos para verificar la existencia del contenido, si este no existe es tratado como
	 * un insert, por otro lado si el contenido ya existe, se hace un actualización del mismo.
	 * @param ContentDTO
	 * @param parametrosDTO
	 * @return boolean
	 * @throws LlamadasWSDAOException
	 * */
	public String _setRevNotaBD(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSDAOException {
		LOG.debug(" --- setRevNotaBD [WS DAO] --- ");				
		int respuesta = 0;
		String nota_db = "";
		try {	
			
			String URL_WS_BASE = parametrosDTO.getUrl_dominio_dat()+parametrosDTO.getUrl_wsd_WorkFlow();						
			
			//Validamos si la nota existe en la BD									
			String urlNotaRegistrada = URL_WS_BASE+"/rest/workflow-controller/existeRevNota";
			
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			LOG.debug("--- urlNotaRegistrada: --- "+urlNotaRegistrada);
			
			respuesta = restTemplate.postForObject(urlNotaRegistrada, entity, Integer.class);
			LOG.debug("--- La nota esta resgitrada: "+respuesta);
			
			//Procesamos la nota en la base de datos
			if(respuesta > 0)
			{			
				
				String urlUpdate = URL_WS_BASE+"/rest/workflow-controller/updateRevNotaBD";
				LOG.info(" --- Se actualiza la nota --- ");
				LOG.debug(urlUpdate);
				respuesta=restTemplate.postForObject(urlUpdate, entity, Integer.class);
				LOG.debug(" --- updateNotaBD: "+respuesta);
				// LOG.debug(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_updateNotaHistoricoBD());
				// respuesta=restTemplate.postForObject(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_updateNotaHistoricoBD(), entity, Integer.class);
				// LOG.debug("updateNotaHistoricoBD: "+respuesta);											
				LOG.info("graylog-nota-actualizada");
				LOG.info("graylog-nota-insertada");
				LOG.info("graylog-actualiza-"+contentDTO.getFcIdTipoNota());				
				LOG.info("graylog-actualiza-"+contentDTO.getFcSeccion());
				LOG.info("graylog-actualiza-"+contentDTO.getFcTipoSeccion());
				LOG.info("graylog-actualiza-"+contentDTO.getFcIdCategoria());
				LOG.info("graylog-actualiza-"+contentDTO.getPortal_uid() );
				nota_db="UPDATE";
			}
			else
			{		 
				LOG.info("--- Se inserta nueva nota --- ");
				String urlInsert = URL_WS_BASE+"/rest/workflow-controller/insertRevNotaBD";
				LOG.debug(urlInsert);
				respuesta = restTemplate.postForObject(urlInsert, entity, Integer.class);				
				LOG.debug(" --- insertNotaBD: "+respuesta);				
				// respuesta=restTemplate.postForObject(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_insertNotaHistoricoBD(), entity, Integer.class);
				// LOG.debug(URL_WS_BASE+parametrosDTO.getMet_wsd_WorkFlow_insertNotaHistoricoBD());
				// LOG.debug("insertNotaHistoricoBD: "+respuesta);								
				LOG.info("graylog-nota-insertada");
				LOG.info("graylog-inserta-"+contentDTO.getFcIdTipoNota());				
				LOG.info("graylog-inserta-"+contentDTO.getFcSeccion());
				LOG.info("graylog-inserta-"+contentDTO.getFcTipoSeccion());
				LOG.info("graylog-inserta-"+contentDTO.getFcIdCategoria());
				LOG.info("graylog-inserta-"+contentDTO.getPortal_uid() );				
				nota_db="INSERT";
			}				
			
			return nota_db;
		}catch(RestClientResponseException rre){
			LOG.error(" --- RestClientResponseException setRevNotaBD [WS DAO]: --- " + rre.getResponseBodyAsString());
			LOG.error(" --- RestClientResponseException setRevNotaBD [WS DAO]: --- ", rre);
			throw new LlamadasWSDAOException(rre.getResponseBodyAsString());
		}catch(Exception e) {																
			LOG.error(" --- Exception setRevNotaBD [WS DAO]: --- ",e);
			throw new LlamadasWSDAOException(e.getMessage());
		}		
	}	
	
	
	
	
	/* =========================================================================================================== */
	/* ========================== End Operation's with uno_mx_n_nota_tmp_revision ========================== */
	/* =========================================================================================================== */

	
	
}//FIN CLASE

