package mx.com.amx.unotv.workflow.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.amx.unotv.workflow.bo.exception.CorreoBOException;
import mx.com.amx.unotv.workflow.bo.exception.LlamadasWSBOException;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.EmailDTO;
import mx.com.amx.unotv.workflow.dto.ErrorNotaDTO;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;

public class CorreoBO {
	//LOG
	private static Logger LOG = Logger.getLogger(CorreoBO.class);
	@Autowired
	private LlamadasWSBO llamadasWSBO;
	private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Metodo que envia correo con error
	 * @param ErrorNotaDTO
	 * @param ParametrosDTO
	 * @throws CorreoBOException
	 * */
	public void enviaCorreoError(ParametrosDTO parametrosDTO, ErrorNotaDTO errorNotaDTO) 
	{
		LOG.debug("Inicia enviaCorreoError[BO]");
		LOG.debug("parametrosDTO: "+parametrosDTO);
		LOG.debug("errorNotaDTO: "+errorNotaDTO);
		try {		
			
			EmailDTO emailDTO = new EmailDTO();			
			String[] arrayPara = parametrosDTO.getCorreo_error_para().split(",");		
			emailDTO.setSubject(parametrosDTO.getCorreo_error_asunto()+" - "+errorNotaDTO.getFrendyURL() +" - "+formatDate.format(new Date()));			
			emailDTO.setRecipients(arrayPara);
			emailDTO.setSmtpsender(parametrosDTO.getCorreo_error_smtpsender());
			
			String cuerpo = parametrosDTO.getCorreo_error_cuerpo();
			LOG.debug("cuerpo: "+cuerpo);
			cuerpo = cuerpo.replace("$URL_NOTA$", errorNotaDTO.getUrlnota());
			cuerpo = cuerpo.replace("$ERROR_DESCRIPCION$", errorNotaDTO.getDescripcionError());
			cuerpo = cuerpo.replace("$AUTOR_NOTA$", errorNotaDTO.getAutor());			
			cuerpo = cuerpo.replace("$ERROR$", errorNotaDTO.getError());			
			emailDTO.setBodyMSG(cuerpo);						
			Boolean res = llamadasWSBO._sendEmail(emailDTO, parametrosDTO);
			LOG.debug("Envio de correo: "+res);
		} catch (LlamadasWSBOException le){
			LOG.debug("LlamadasWSBOException en enviaCorreoError[BO]: "+le.getMessage());
		} catch (Exception e) {
			LOG.debug("Exception en enviaCorreoError[BO]: ",e);
		}		
	}
	
	/**
	 * Metodo que envia correo con error
	 * @param ErrorNotaDTO
	 * @param ParametrosDTO
	 * @throws CorreoBOException
	 * */
	public void enviaCorreoError(ParametrosDTO parametrosDTO, ErrorNotaDTO errorNotaDTO, List<String> listError) 
	{
		LOG.debug("Inicia enviaCorreoError[BO]");
		LOG.debug("parametrosDTO: "+parametrosDTO);
		LOG.debug("listError: "+listError);
		try {		
			
			EmailDTO emailDTO = new EmailDTO();			
			String[] arrayPara = parametrosDTO.getCorreo_error_para().split(",");		
			emailDTO.setSubject(parametrosDTO.getCorreo_error_asunto()+" - "+errorNotaDTO.getFrendyURL() +" - "+formatDate.format(new Date()));			
			emailDTO.setRecipients(arrayPara);
			emailDTO.setSmtpsender(parametrosDTO.getCorreo_error_smtpsender());
			
			String cuerpo = parametrosDTO.getCorreo_error_cuerpo();
			LOG.debug("cuerpo: "+cuerpo);
			cuerpo = cuerpo.replace("$URL_NOTA$", errorNotaDTO.getUrlnota());
			cuerpo = cuerpo.replace("$ERROR_DESCRIPCION$", errorNotaDTO.getDescripcionError());
			cuerpo = cuerpo.replace("$AUTOR_NOTA$", errorNotaDTO.getAutor());						
			
			StringBuffer sbErrro = new StringBuffer();
			for (String string : listError){
				sbErrro.append(string);
				sbErrro.append("<br><br>");				
			}			
			cuerpo = cuerpo.replace("$ERROR$", sbErrro.toString());						
			emailDTO.setBodyMSG(cuerpo);						
			Boolean res = llamadasWSBO._sendEmail(emailDTO, parametrosDTO);
			LOG.debug("Envio de correo: "+res);
		} catch (LlamadasWSBOException le){
			LOG.debug("LlamadasWSBOException en enviaCorreoError[BO]: "+le.getMessage());
		} catch (Exception e) {
			LOG.debug("Exception en enviaCorreoError[BO]: ",e);
		}		
	}
	
	
	/**
	 * Metodo que envia correo con error
	 * @param ErrorNotaDTO
	 * @param ParametrosDTO
	 * @throws CorreoBOException
	 * */
	public void enviaCorreoCaducar(ParametrosDTO parametrosDTO, ContentDTO contentDTO, String url) 
	{
		LOG.debug("Inicia enviaCorreoCaducar[BO]");
		LOG.debug("parametrosDTO: "+parametrosDTO);
		LOG.debug("url: "+url);

		try {		
			
			
			
			EmailDTO emailDTO = new EmailDTO();			
			String[] arrayPara = parametrosDTO.getCorreo_caduca_para().split(",");		
			emailDTO.setSubject(parametrosDTO.getCorreo_caduca_asunto()+" - "+contentDTO.getFcNombre()+" - "+formatDate.format(new Date()));			
			emailDTO.setRecipients(arrayPara);
			emailDTO.setSmtpsender(parametrosDTO.getCorreo_caduca_smtpsender());
			
			String cuerpo = parametrosDTO.getCorreo_caduca_cuerpo();
			LOG.debug("cuerpo: "+cuerpo);
			cuerpo = cuerpo.replace("$URL_NOTA$", url);
			cuerpo = cuerpo.replace("$TITUTLO_NOTA$", contentDTO.getFcTitulo());
			cuerpo = cuerpo.replace("$AUTOR_NOTA$", contentDTO.getFcEscribio());			
			emailDTO.setBodyMSG(cuerpo);						
			Boolean res = llamadasWSBO._sendEmail(emailDTO, parametrosDTO);
			LOG.debug("Envio de correo: "+res);
		} catch (LlamadasWSBOException le){
			LOG.debug("LlamadasWSBOException en enviaCorreoError[BO]: "+le.getMessage());
		} catch (Exception e) {
			LOG.debug("Exception en enviaCorreoError[BO]: ",e);
		}		
	}
}//FIN CLASE


