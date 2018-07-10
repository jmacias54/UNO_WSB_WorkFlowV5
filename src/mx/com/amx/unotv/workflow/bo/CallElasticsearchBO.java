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

import mx.com.amx.unotv.workflow.bo.exception.CallElasticsearchBOException;
import mx.com.amx.unotv.workflow.dto.NotaElasticDTO;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;

public class CallElasticsearchBO {

	//LOG
	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	private RestTemplate restTemplate;
	private HttpHeaders headers = new HttpHeaders();
	
	public CallElasticsearchBO() 
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
	 * 
	 * */
	public String callElastic(NotaElasticDTO notaElasticDTO, ParametrosDTO parametrosDTO, String method) throws CallElasticsearchBOException
	{
		LOG.debug("Inicia deleteNotaElastic");
		try {			
			String URL_WS_BASE = parametrosDTO.getUrl_dominio_app()+"/MX_UNO_WSB_Buscador/rest/notaController/"+method+"/";								
			HttpEntity<NotaElasticDTO> entity = new HttpEntity<NotaElasticDTO>( notaElasticDTO );
			LOG.debug("URL_WS_BASE: "+URL_WS_BASE);
			return restTemplate.postForObject(URL_WS_BASE, entity, String.class);			
		}catch(RestClientResponseException rre){
			LOG.error("RestClientResponseException deleteNotaElastic [WS Elastic]: " + rre.getResponseBodyAsString());
			LOG.error("RestClientResponseException deleteNotaElastic [WS Elastic]: ", rre);
			throw new CallElasticsearchBOException(rre.getResponseBodyAsString());
		}catch(Exception e) {																
			LOG.error("Exception deleteNotaElastic [WS Elastic]: ",e);
			throw new CallElasticsearchBOException(e.getMessage());
		}			
	}
	
}//FIN CLASE

