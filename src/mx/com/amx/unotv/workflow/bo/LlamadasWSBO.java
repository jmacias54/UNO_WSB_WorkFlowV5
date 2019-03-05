package mx.com.amx.unotv.workflow.bo;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import mx.com.amx.unotv.workflow.bo.exception.LlamadasWSBOException;
import mx.com.amx.unotv.workflow.dto.AudioOoyalaDTO;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.EmailDTO;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;
import mx.com.amx.unotv.workflow.dto.PushAmpDTO;
import mx.com.amx.unotv.workflow.dto.RequestArrayEntityPurgue;
import mx.com.amx.unotv.workflow.dto.RespuestaWSAmpDTO;
import mx.com.amx.unotv.workflow.dto.VideoOoyalaDTO;

public class LlamadasWSBO {
	
	//LOG
	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	private RestTemplate restTemplate;
	private HttpHeaders headers = new HttpHeaders();
	
	public LlamadasWSBO() 
	{
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
	 * Metodo que inserta o actualiza el Instant Article
	 * @param contentDTO
	 * @param ParametrosDTO
	 * @return String
	 * @throws LlamadasWSBOException
	 * */
	public String insertUpdateArticleFB (ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws LlamadasWSBOException
	{
		LOG.debug("Inicia insertUpdateArticleFB en LlamadasWSBOTest");
		try {
			String URL_WS=parametrosDTO.getUrl_dominio_app()+parametrosDTO.getUrl_wsb_FB()+parametrosDTO.getMet_wsb_FB_insertUpdateArticle2();
			LOG.info("LLamado a "+URL_WS);
			HttpEntity<ContentDTO> entity = new HttpEntity<ContentDTO>( contentDTO );
			return restTemplate.postForObject(URL_WS, entity, String.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException insertUpdateArticleFB [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException insertUpdateArticleFB [WS BO]: ", rre);
			throw new LlamadasWSBOException(rre.getResponseBodyAsString());			
		} catch(Exception e) {
			LOG.error("Error insertUpdateArticle - FB [WS BO]: ",e);
			throw new LlamadasWSBOException(e.getMessage());
		}
	}
	
	/**
	 * Metodo que borra un Instant Article
	 * @param articleId
	 * @param ParametrosDTO
	 * @return String
	 * @throws LlamadasWSBOException
	 * */
	public String deleteArticleFB (String articleId, ParametrosDTO parametrosDTO) throws LlamadasWSBOException
	{
		LOG.debug("Inicia deleteArticleFB en LlamadasWSBOTest");
		LOG.debug("articleId: "+articleId);

		try {
			String URL_WS=parametrosDTO.getUrl_dominio_app()+parametrosDTO.getUrl_wsb_FB()+parametrosDTO.getMet_wsb_FB_deleteArticle();
			LOG.debug("LLamado a "+URL_WS);
			HttpEntity<String> entity = new HttpEntity<String>( articleId );
			return restTemplate.postForObject(URL_WS, entity, String.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException deleteArticleFB [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException deleteArticleFB [WS BO]: ", rre);
			throw new LlamadasWSBOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception deleteArticle - FB [WS BO]: ",e);
			throw new LlamadasWSBOException(e.getMessage());
		}
	}
	
	/**
	 * Llama el api para AMP
	 * @param pushAmpDTO
	 * @param ParametrosDTO
	 * @return RespuestaWSAmpDTO
	 * @throws LlamadasWSBOException
	 * */
	public RespuestaWSAmpDTO sendPushAMP(PushAmpDTO pushAmpDTO, ParametrosDTO parametrosDTO) throws LlamadasWSBOException 
	{
		LOG.debug("Inicia sendPushAMP en LlamadasWSBOTest");
		LOG.debug("pushAmpDTO: "+pushAmpDTO);					
		try {		
			String URL_WS=parametrosDTO.getUrl_dominio_app()+parametrosDTO.getUrl_wsb_BackOffice()+parametrosDTO.getMet_wsb_BackOffice_sendPushAMP();
			LOG.debug("LLamado a "+URL_WS);
			HttpEntity<PushAmpDTO> entity = new HttpEntity<PushAmpDTO>( pushAmpDTO );
			return restTemplate.postForObject(URL_WS, entity, RespuestaWSAmpDTO.class);	
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException sendPushAMP [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException sendPushAMP [WS BO]: ", rre);
			throw new LlamadasWSBOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception sendPushAMP [WS BO]: ",e);
			throw new LlamadasWSBOException(e.getMessage());
		}			
	}
	
	/**
	 * Metodo que obtiene la informacion de ooyala
	 * @param content_id
	 * @param ParametrosDTO
	 * @return VideoOoyalaDTO
	 * @throws LlamadasWSBOException
	 * */
	public VideoOoyalaDTO getInfoVideo(String content_id, ParametrosDTO parametrosDTO) throws LlamadasWSBOException 
	{
		LOG.debug("Inicia getInfoVideo [WS BO]]");
		LOG.debug("content_id: "+content_id);
		try {
			String URL_WS=parametrosDTO.getUrl_dominio_app()+parametrosDTO.getUrl_wsb_WorkFlowUtils()+parametrosDTO.getMet_wsb_WorkFlowUtils_getInfoVideo();			
			LOG.debug("LLamado a "+URL_WS);
			HttpEntity<String> entity = new HttpEntity<String>( content_id );
			return restTemplate.postForObject(URL_WS, entity, VideoOoyalaDTO.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException getInfoVideo [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException getInfoVideo [WS BO]: ", rre);
			throw new LlamadasWSBOException("getInfoVideo: "+rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception getInfoVideo [WS BO]: ",e);
			throw new LlamadasWSBOException(e.getMessage());
		}			
	}
	
	/**
	 * 
	 * */
	public String getParameter(String idParameter, ParametrosDTO parametrosDTO) throws LlamadasWSBOException 
	{
		LOG.debug("Inicia getParameter [WS BO]");
		LOG.debug("idParameter : "+idParameter);		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_app()+parametrosDTO.getUrl_wsb_Utils()+parametrosDTO.getMet_wsb_Utils_getParameter();
			LOG.debug("URL_WS: "+URL_WS);			
			HttpEntity<String> entity = new HttpEntity<String>( idParameter );
			return restTemplate.postForObject(URL_WS, entity, String.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException getParameter [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException getParameter [WS BO]: ", rre);
			throw new LlamadasWSBOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Error getParameter [BO]: "+e.getLocalizedMessage());
			throw new LlamadasWSBOException(e.getMessage());
		}			
	}
	
	
	/**
	 * Metodo para enviar correo electronico
	 * @param EmailDTO
	 * @param ParametrosDTO
	 * @return boolean
	 * @throws LlamadasWSBOException
	 * */
	public Boolean _sendEmail(EmailDTO emailDTO, ParametrosDTO parametrosDTO) throws LlamadasWSBOException 
	{
		LOG.debug("Inicia sendEmail [WS BO]");
		LOG.debug("EmailDTO : "+emailDTO);		
		try {
			String URL_WS =parametrosDTO.getUrl_dominio_app()+parametrosDTO.getUrl_wsb_Utils()+"/rest/CorreoController/sendEmail";
			LOG.debug("URL_WS: "+URL_WS);			
			HttpEntity<EmailDTO> entity = new HttpEntity<EmailDTO>(emailDTO);
			return restTemplate.postForObject(URL_WS, entity, Boolean.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _sendEmail [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _sendEmail [WS BO]: ", rre);
			throw new LlamadasWSBOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Error getParameter [BO]: "+e.getLocalizedMessage());
			throw new LlamadasWSBOException(e.getMessage());
		}			
	}
	
	/**
	 * 
	 * @param EmailDTO
	 * @param ParametrosDTO
	 * @return boolean
	 * @throws LlamadasWSBOException
	 * */
	public String _purgaAkamai(ParametrosDTO parametrosDTO, RequestArrayEntityPurgue url) throws LlamadasWSBOException 
	{
		LOG.debug("Inicia _purgaAkamai[BO]");
		LOG.debug("url : "+url);		
		try {			
			String URL_WS =parametrosDTO.getUrl_dominio_app()+"/MX_WSB_Utils_Akamai/rest/purgeController/purga_urls";
			LOG.info("URL_WS: "+URL_WS);									
			HttpEntity<RequestArrayEntityPurgue> entity = new HttpEntity<RequestArrayEntityPurgue>( url );
			return restTemplate.postForObject(URL_WS, entity, String.class);					
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException _purgaAkamai [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException _purgaAkamai [WS BO]: ", rre);
			throw new LlamadasWSBOException(rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Error getParameter [BO]: "+e.getLocalizedMessage());
			throw new LlamadasWSBOException(e.getMessage());
		}			
	}
	
	/**
	 * Metodo que obtiene la informacion de ooyala de un audio
	 * @param content_id
	 * @param ParametrosDTO
	 * @return AudioOoyalaDTO
	 * @throws LlamadasWSBOException
	 * */
	public AudioOoyalaDTO getInfoAudio(String content_id, ParametrosDTO parametrosDTO) throws LlamadasWSBOException 
	{
		LOG.debug("Inicia getInfoAudio [WS BO]]");
		LOG.debug("content_id: "+content_id);
		try {
			String URL_WS=parametrosDTO.getUrl_dominio_app()+parametrosDTO.getUrl_wsb_WorkFlowUtils()+"/rest/getInfoAudioController/getInfoAudio";			
			LOG.debug("LLamado a "+URL_WS);
			HttpEntity<String> entity = new HttpEntity<String>( content_id );
			return restTemplate.postForObject(URL_WS, entity, AudioOoyalaDTO.class);
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException getInfoAudio [WS BO]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException getInfoAudio [WS BO]: ", rre);
			throw new LlamadasWSBOException("getInfoAudio: "+rre.getResponseBodyAsString());
		} catch(Exception e) {
			LOG.error("Exception getInfoVideo [WS BO]: ",e);
			throw new LlamadasWSBOException(e.getMessage());
		}			
	}
	
	
}//FIN CLASE
