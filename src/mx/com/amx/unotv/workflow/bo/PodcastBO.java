package mx.com.amx.unotv.workflow.bo;

import mx.com.amx.unotv.workflow.bo.exception.LlamadasWSBOException;
import mx.com.amx.unotv.workflow.bo.exception.LlamadasWSDAOException;
import mx.com.amx.unotv.workflow.bo.exception.PodcastBOException;
import mx.com.amx.unotv.workflow.dto.AudioOoyalaDTO;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;
import mx.com.amx.unotv.workflow.dto.PodcastDTO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class PodcastBO {
	//LOG
	private static Logger LOG = Logger.getLogger(PodcastBO.class);
	@Autowired
	private LlamadasWSDAO llamadasWSDAO; 
	@Autowired
	private LlamadasWSBO llamadasWSBO;
	
	/**
	 * 
	 * 
	 * */
	public void procesoPodcast(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws PodcastBOException
	{	
		LOG.debug("Inicia procesoPodcast");
		try {	
		
			AudioOoyalaDTO audioOoyalaDTO = new AudioOoyalaDTO();
			
			//validamos si la nota tiene podacast relacionado
			int existeNota = llamadasWSDAO._getPodcast(contentDTO.getFcIdContenido(), parametrosDTO);
	
			
			if((existeNota > 0) && (!contentDTO.getFcIdAudioOoyala().equals("")) )
			{
				//Obtebenos la informacion de ooyala del auudio
				audioOoyalaDTO = llamadasWSBO.getInfoAudio(contentDTO.getFcIdAudioOoyala(), parametrosDTO);
				LOG.debug("Ooyala source"+audioOoyalaDTO.getUrl());
				LOG.debug("Ooyala Alternate_text"+audioOoyalaDTO.getAlternate_text());				
				PodcastDTO podcastDTO = new PodcastDTO();
				podcastDTO.setFcIdContenido(contentDTO.getFcIdContenido());
				podcastDTO.setFcIdContentOooyala(contentDTO.getFcIdAudioOoyala());
				podcastDTO.setFcTitulo(audioOoyalaDTO.getAlternate_text());
				podcastDTO.setFcDescripcion(contentDTO.getFcDescripcion());
				podcastDTO.setFcDuracion(audioOoyalaDTO.getDuration());
				podcastDTO.setFcUrlAudio(audioOoyalaDTO.getUrl());
				podcastDTO.setFdFechaPublicacion(contentDTO.getFdFechaPublicacion());
				podcastDTO.setFcSize(audioOoyalaDTO.getFile_size());
				
				//Borramos nota existente 
				llamadasWSDAO._deletePodcast(contentDTO.getFcIdContenido(), parametrosDTO);
				
				//Insertamos podcast
				llamadasWSDAO._insertPodcast(podcastDTO, parametrosDTO);
				
				
				LOG.debug("Se actualizo audio");
			}
			else if((existeNota == 0) && (!contentDTO.getFcIdAudioOoyala().equals(""))) 
			{				
				//Obtebenos la informacion de ooyala del auudio
				audioOoyalaDTO = llamadasWSBO.getInfoAudio(contentDTO.getFcIdAudioOoyala(), parametrosDTO);
				LOG.debug("Ooyala source"+audioOoyalaDTO.getUrl());
				LOG.debug("Ooyala Alternate_text"+audioOoyalaDTO.getAlternate_text());				
				PodcastDTO podcastDTO = new PodcastDTO();
				podcastDTO.setFcIdContenido(contentDTO.getFcIdContenido());
				podcastDTO.setFcIdContentOooyala(contentDTO.getFcIdAudioOoyala());
				podcastDTO.setFcTitulo(audioOoyalaDTO.getAlternate_text());
				podcastDTO.setFcDescripcion(contentDTO.getFcDescripcion());
				podcastDTO.setFcDuracion(audioOoyalaDTO.getDuration());
				podcastDTO.setFcSize(audioOoyalaDTO.getFile_size());
				podcastDTO.setFcUrlAudio(audioOoyalaDTO.getUrl());
				podcastDTO.setFdFechaPublicacion(contentDTO.getFdFechaPublicacion());
				//Insertamos podcast
				llamadasWSDAO._insertPodcast(podcastDTO, parametrosDTO);				
				LOG.debug("Se inserto audio");
			}
			else if((existeNota > 0) && (contentDTO.getFcIdAudioOoyala().equals(""))) 
			{
				//Borramos nota existente 
				llamadasWSDAO._deletePodcast(contentDTO.getFcIdContenido(), parametrosDTO);				
				LOG.debug("Se borro audio");
			}
			
		} catch (LlamadasWSBOException le) {
			LOG.error("Exception en procesoPodcasts"+le.getMessage());
			throw new PodcastBOException(le.getMessage());
		} catch (LlamadasWSDAOException de) {
			LOG.error("Exception en procesoPodcasts"+de.getMessage());
			throw new PodcastBOException(de.getMessage());
		} catch (Exception e) {
			LOG.error("Exception en procesoPodcasts",e);
			throw new PodcastBOException(e.getMessage());
		}		
	}
	
	
	
}//FIN CLASE
