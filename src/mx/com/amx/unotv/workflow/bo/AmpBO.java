package mx.com.amx.unotv.workflow.bo;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.amx.unotv.workflow.bo.exception.AmpBOException;
import mx.com.amx.unotv.workflow.bo.exception.ProcesoWorkflowException;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;
import mx.com.amx.unotv.workflow.dto.RedSocialEmbedPostDTO;
import mx.com.amx.unotv.workflow.utileria.ReadHTMLWebServer;

public class AmpBO {

	//LOG
	private static Logger LOG  = Logger.getLogger(AmpBO.class);
	@Autowired
	private LlamadasWSDAO llamadasWSDAO;
	
	
	/**
	 * 
	 * */
	public String generaAMP(ParametrosDTO parametrosDTO, ContentDTO contentDTO) throws AmpBOException 
	{
		LOG.debug("Inicia generaAMP");
		String HTML="";
		try {
		
			String url_plantilla = parametrosDTO.getURL_WEBSERVER_AMP();
			
			ReadHTMLWebServer readHTMLWebServer=new ReadHTMLWebServer();
			LOG.info("Plantilla AMP: "+url_plantilla);
			
			//Obtenemos plantilla
			HTML = readHTMLWebServer.getResourceWebServer(url_plantilla);									
			//Remplazamos HTML
			HTML = remplazaHTMLAMP(HTML, contentDTO, parametrosDTO);
									
			String rutaHTML = getRutaContenido(contentDTO, parametrosDTO)+"/amp.html";
			LOG.info("Ruta HTML AMP: "+parametrosDTO.getPathFiles()+rutaHTML);
			
			boolean success = writeHTML(parametrosDTO.getPathFiles()+rutaHTML, HTML);
			LOG.info("Genero HTML Local AMP: "+success);
			
		} catch (Exception e) {
			LOG.error("Exception en generaAMP: ",e);
		}
		return HTML;
		
	}
	
	
	/**
	 * Metodo encargado de regresar una cadena con la definición de la ruta de la nota
	 * en el webServer, por ejemplo: "noticias/estados/estado-de-mexico/detalle/finaliza-conteo-distrital-en-edomex-se-confirma-triunfo-de-del-mazo-202664"
	 * @param  ContentDTO
	 *         Objeto que contiene la información necesaria para el path
	 * @param  ParametrosDTO
	 *         Objeto con informacion adicional para el path
	 * @return String
	 * 		   Se regresa el path de guardado de un contenido en el WebServer
	 * @throws ProcesoWorkflowException
	 * @author jesus
	 * */
	private static String getRutaContenido(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws ProcesoWorkflowException
	{		
		LOG.debug("**** Inicia getRutaContenido[Utils]");	
		String rutaContenido="";
	
		try {			
			String tipoSeccion="";
			
			if(contentDTO.getFcTipoSeccion().equalsIgnoreCase("noticia") || contentDTO.getFcTipoSeccion().equalsIgnoreCase("noticias"))
				tipoSeccion="noticias";
			else if(contentDTO.getFcTipoSeccion().equalsIgnoreCase("videoblog") || contentDTO.getFcTipoSeccion().equalsIgnoreCase("videoblogs"))
				tipoSeccion="videoblogs";
			else
				tipoSeccion=contentDTO.getFcTipoSeccion();
			
			String id_categoria=contentDTO.getFcFriendlyURLCategoria() !=null && !contentDTO.getFcFriendlyURLCategoria().equals("")?contentDTO.getFcFriendlyURLCategoria():contentDTO.getFcIdCategoria();
			
			String id_seccion=contentDTO.getFcFriendlyURLSeccion() !=null && !contentDTO.getFcFriendlyURLSeccion().equals("")?contentDTO.getFcFriendlyURLSeccion():contentDTO.getFcSeccion();
			
			rutaContenido = tipoSeccion + "/" + id_seccion +"/"+ id_categoria+"/"+ parametrosDTO.getPathDetalle() + "/" +contentDTO.getFcNombre();			
			LOG.debug("rutaContenido: "+rutaContenido);			
		} catch (Exception e) {
			LOG.error("Error getRutaContenido: ",e);
			throw new ProcesoWorkflowException(e.getMessage());
		}
		return rutaContenido;
	}
	
	/*
	 * 
	 * */
	private String remplazaHTMLAMP(String HTML, ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws Exception 
	{
		LOG.debug("Inicia remplazaHTMLAMP");
		try {
			
			//Remplaza styles
			try {
				ReadHTMLWebServer readHTMLWebServer=new ReadHTMLWebServer();
				HTML = HTML.replace("$WCM_STYLES$",readHTMLWebServer.getResourceWebServer(parametrosDTO.getURL_WEBSERVER_CSS_AMP()).trim());
			} catch(Exception e) {
				HTML = HTML.replace("$WCM_STYLES$", "");
				LOG.error("Error al remplazar $WCM_STYLES$",e);
			}
			
			//"$WCM_NAVEGACION_COMSCORE$ 
			try {		
				HTML = HTML.replace("$WCM_NAVEGACION_COMSCORE$", contentDTO.getFcTipoSeccion() + "." + contentDTO.getFcSeccion()+"."+ contentDTO.getFcIdCategoria()+ "." + parametrosDTO.getPathDetalle() + "." + contentDTO.getFcNombre());
			} catch (Exception e) {
				LOG.error("Error al sustituir navegacion  comscore",e);
			}
			
			try {
				HTML = HTML.replace("$WCM_TITLE_CONTENIDO$",StringEscapeUtils.escapeHtml(contentDTO.getFcTitulo().trim()));
			} catch(Exception e) {
				HTML = HTML.replace("$WCM_TITLE_CONTENIDO$", "");
				LOG.error("Error al remplazar $WCM_TITLE_CONTENIDO$",e);
			}
			try {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				HTML = HTML.replace("$WCM_FECHA$", format.format(contentDTO.getFdFechaPublicacion()));
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_FECHA$", "");
				LOG.error("Error al remplazar $WCM_FECHA$");
			}
			try {
				HTML = HTML.replace("$WCM_HORA$", contentDTO.getFcHora());
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_HORA$", "");
				LOG.error("Error al remplazar $WCM_HORA$");
			}
			
			try {
				String autor = contentDTO.getFcEscribio() == null? "": contentDTO.getFcEscribio();
				HTML = HTML.replace("$WCM_AUTOR$", StringEscapeUtils.escapeHtml(autor).trim());
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_AUTOR$", "");
				LOG.error("Error al remplazar $WCM_AUTOR$");
			}			
			
			try {
				String lugar = contentDTO.getFcLugar() == null? "": contentDTO.getFcLugar();
				HTML = HTML.replace("$WCM_LUGAR$", StringEscapeUtils.escapeHtml(lugar));
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_LUGAR$", "");
				LOG.error("Error al remplazar $WCM_LUGAR$");
			}
						
			try {
				String nombreCategoria = contentDTO.getFcNombreCategoria() == null? "": contentDTO.getFcNombreCategoria();
				HTML = HTML.replace("$WCM_CATEGORIA$", StringEscapeUtils.escapeHtml(nombreCategoria));
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_CATEGORIA$", "");
				LOG.error("Error al remplazar $WCM_CATEGORIA$");
			}
			
			
			try {				
				HTML = HTML.replace("$WCM_DESCRIPCION$",contentDTO.getFcDescripcion());
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_DESCRIPCION$", "");
				LOG.error("Error al remplazar $WCM_DESCRIPCION$");
			}
			
			try {				
				HTML = HTML.replace("$WCM_KEYWORDS$",contentDTO.getFcKeywords());
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_KEYWORDS$", "");
				LOG.error("Error al remplazar $WCM_KEYWORDS$");
			}
			
			try {				
				HTML = HTML.replace("$WCM_IMAGEN$",parametrosDTO.getDominio()+contentDTO.getFcImgPrincipal());
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_IMAGEN$", "");
				LOG.error("Error al remplazar $WCM_IMAGEN$");
			}
			
			
			try {
				String fuente = contentDTO.getFcFuente() == null? "": contentDTO.getFcFuente();
				HTML = HTML.replace("$WCM_FUENTE$", StringEscapeUtils.escapeHtml(fuente));
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_FUENTE$", "");
				LOG.error("Error al remplazar $WCM_FUENTE$",e);
			}
						
			//Video o Imagen principal			
			try {					
				HTML = HTML.replace("$WCM_MEDIA_CONTENT$", getMediaContentAMP(contentDTO));								
			} catch(Exception e) {
				HTML = HTML.replace("$WCM_MEDIA_CONTENT$", "");
				LOG.error("Error al remplazar $WCM_MEDIA_CONTENT$",e);
			}
			
			//Remplaza contenido
			try {						
				
				int inicio = contentDTO.getClRtfContenido().indexOf("<p");
				int fin  = contentDTO.getClRtfContenido().indexOf("</p>", inicio+1);
				
				String aux_substring = "<"+contentDTO.getClRtfContenido().substring(inicio+1, fin)+"</p>";				
				String content_banner = aux_substring+"<div class=\"banner\"><amp-ad width=\"300\" height=\"250\" layout=\"fixed\" type=\"doubleclick\" data-slot=\""+getUrlAdServer(contentDTO)+"MB08\"></amp-ad></div>";				
				String rtfContentBanner = contentDTO.getClRtfContenido().replace(aux_substring, content_banner);
				
				String aux_rtf_contenido = cambiaCaracteres(getEmbedPostAMP(rtfContentBanner));
				aux_rtf_contenido = aux_rtf_contenido.replace("<iframe", "<amp-iframe resizable");
				aux_rtf_contenido = aux_rtf_contenido.replace("</iframe>", "</amp-iframe>");				
								
				HTML = HTML.replace("$WCM_RTF_CONTENIDO$", aux_rtf_contenido);
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_RTF_CONTENIDO$", "");
				LOG.error("Error al remplazar $WCM_RTF_CONTENIDO$",e);
			}	
					
			//Remplazamos la galeria
			try {
				HTML = HTML.replace("[=GALERIA=]",getGaleriaAMP(contentDTO));
				
				//Repleza galeria por separado.
				if(!contentDTO.getClGaleriaImagenes().equals(""))
				{
					HTML = remplazaGaleriaItem(contentDTO.getJsonGaleria(), HTML);
				}
				
			} catch (Exception e) {
				HTML = HTML.replace("[=GALERIA=]", "");
				LOG.error("Error al remplazar galeria",e);
			}		
			//Remplazamos la infografia
			try {
				HTML = HTML.replace("[=INFOGRAFIA=]","<amp-img width=\"287\" height=\"775\" layout=\"responsive\" src=\""+contentDTO.getFcImgInfografia()+"\"></amp-img>");			
			} catch (Exception e) {
				HTML = HTML.replace("[=INFOGRAFIA=]", "");
				LOG.error("Error al remplazar la infografia",e);
			}	
						
			try {			
				HTML = HTML.replace("$URL_PAGE$", "https://www.unotv.com/"+contentDTO.getFcTipoSeccion() + "/" + contentDTO.getFcSeccion()+"/"+ contentDTO.getFcIdCategoria()+"/"+ parametrosDTO.getPathDetalle() + "/" +contentDTO.getFcNombre()+"/");
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_URL_PAGE$", "");
				LOG.error("Error al remplazar $WCM_URL_PAGE$");
			}
			
			try {			
				HTML = HTML.replace("target=\"\"", "target=\"_blank\"");
			} catch (Exception e) {			
				LOG.error("Error al remplazar $WCM_URL_PAGE$");
			}	

			
			//Fecha Json
			try {						
				DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
				
				Date date = inputFormat.parse(contentDTO.getFdFechaPublicacion().toString());				
				String strFecha = dateFormat.format(date);				
				HTML = HTML.replace("$WCM_FECHA_PUBLISHED$",strFecha);						
				String strFechaMod = dateFormat.format(new Date());				
				HTML = HTML.replace("$WCM_FECHA_MODIFIED$",strFechaMod);				
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_FECHA_PUBLISHED$", "");
				HTML = HTML.replace("$WCM_FECHA_MODIFIED$", "");
				LOG.error("Error al remplazar $WCM_FECHA_PUBLISHED$",e);
			}
						
			//Notas Relacionadas
			try {											
				HTML = HTML.replace("$WCM_LIST_RELACIONADAS$",getRelacionadas(contentDTO, parametrosDTO));
				
			} catch (Exception e) {
				LOG.error("Error al sustituir relacionadas");
				HTML = HTML.replace("$WCM_LIST_RELACIONADAS$","");
			}
			
			//URL AD-Server
			try {											
				HTML = HTML.replace("$WCM_URL_BANNER$",getUrlAdServer(contentDTO));				
			} catch (Exception e) {
				LOG.error("Error al sustituir WCM_URL_BANNER");
				HTML = HTML.replace("$WCM_URL_BANNER$","/121173452/UnoTV/noticias/inicio/");
			}
			
			
			//Script
			try {				
				if(HTML.contains("<amp-ooyala-player"))
					HTML = HTML.replace("$WCM_JS_OOYALA$", parametrosDTO.getJs_amp_ooyala_player());
				
				if(HTML.contains("<amp-youtube"))
					HTML = HTML.replace("$WCM_JS_YOUTUBE$", parametrosDTO.getJs_amp_youtube());
				
				if(HTML.contains("<amp-twitter"))
					HTML = HTML.replace("$WCM_JS_TWITTER$", parametrosDTO.getJs_amp_twitter());
				
				if(HTML.contains("<amp-facebook "))
					HTML = HTML.replace("$WCM_JS_FACEBOOK$", parametrosDTO.getJs_amp_facebook());
				
				if(HTML.contains("<amp-instagram"))
					HTML = HTML.replace("$WCM_JS_INSTAGRAM$", parametrosDTO.getJs_amp_instagram());
								
				HTML = HTML.replace("$WCM_JS_OOYALA$", "");
				HTML = HTML.replace("$WCM_JS_YOUTUBE$", "");
				HTML = HTML.replace("$WCM_JS_INSTAGRAM$", "");
				HTML = HTML.replace("$WCM_JS_TWITTER$", "");
				HTML = HTML.replace("$WCM_JS_FACEBOOK$", "");				
			} catch (Exception e) {
				LOG.error("Error al sustituir js amp",e);
				HTML = HTML.replace("$WCM_JS_OOYALA$", "");
				HTML = HTML.replace("$WCM_JS_YOUTUBE$", "");
				HTML = HTML.replace("$WCM_JS_INSTAGRAM$", "");
				HTML = HTML.replace("$WCM_JS_TWITTER$", "");
				HTML = HTML.replace("$WCM_JS_FACEBOOK$", "");
			}
						
		} catch (Exception e) {
			LOG.error("Exception en generaAMP: ",e);
			return HTML; 
		}
		return HTML;				
	}
	
	
	
	/*
	 * 
	 * */
	private String getRelacionadas(ContentDTO contentDTO, ParametrosDTO parametrosDTO)
	{
		LOG.debug("Inicia getRelacionadas");
		try {				
			
			StringBuffer relacionadas=new StringBuffer();
			
			//Obtenemos la lista de las notas relacionadas 
			List<ContentDTO> listRelacionadas= llamadasWSDAO.getNotasMagazine("magazine-home-2",contentDTO.getFcIdContenido(), parametrosDTO);
			
			//Recorremos la lista
			if(listRelacionadas!=null && listRelacionadas.size()>0)
			{					
				int conBanner = 1;
				for (ContentDTO relacionada : listRelacionadas) 
				{
					relacionadas.append("<a class=\""+relacionada.getFcIdCategoria()+" item\" href=\""+relacionada.getFcUrl()+"\">\n");
					relacionadas.append("	<div class=\"thumb\">"+getHTMLTipoNota(relacionada.getFcIdTipoNota())+"\n");
					relacionadas.append("	   <amp-img src=\""+relacionada.getFcImgPrincipal()+"\" layout=\"responsive\" width=\"750\" height=\"450\"></amp-img>\n");
					relacionadas.append("	</div>\n");						
					relacionadas.append("	<div class=\"item-content\"><h2>"+relacionada.getFcTitulo()+"</h2></div>\n");						
					relacionadas.append("</a>\n");
					switch (conBanner) {
					case 2:
						{
							relacionadas.append("<div class=\"banner\">");		
							relacionadas.append("<amp-ad width=\"300\" height=\"250\" layout=\"fixed\" type=\"doubleclick\" data-slot=\""+getUrlAdServer(contentDTO)+"MB01\"></amp-ad>");
							relacionadas.append("</div>");								
						}
						break;
					case 4:
					{
							relacionadas.append("<div class=\"banner\">");		
							relacionadas.append("<amp-ad width=\"300\" height=\"250\" layout=\"fixed\" type=\"doubleclick\" data-slot=\""+getUrlAdServer(contentDTO)+"MB02\"></amp-ad>");
							relacionadas.append("</div>");								
					}
						break;
					case 6:
					{
							relacionadas.append("<div class=\"banner\">");		
							relacionadas.append("<amp-ad width=\"300\" height=\"250\" layout=\"fixed\" type=\"doubleclick\" data-slot=\""+getUrlAdServer(contentDTO)+"MB06\"></amp-ad>");
							relacionadas.append("</div>");								
					}
						break;
					}
					
				conBanner++;	
				}
				
				return "<h3>Te recomendamos</h3><div class=\"panel-related\">"+relacionadas.toString().trim()+"</div>";
			}
			else
			{
				return "";
			}
		} catch (Exception e) {
			LOG.error("Error al sustituir relacionadas",e);
			return "";
		}
		
	}
	
	
	/*
	 * Se lleva a cabo el reemplazo del Media Content de la nota,
	 * puede ser solo reemplazo de la imagen principal o del vide.
	 * @param  ContentDTO Instancia con la información necesaria para reemplazar
	 * @return String Se devuelve una cadena con el Media Content
	 * */	
	private String getMediaContentAMP(ContentDTO dto)
	{
		
		LOG.debug("AMP - Inicia getMediaContentAMP");
		LOG.debug("AMP - getFcIdVideoOoyala: " +dto.getFcIdVideoOoyala().trim());
		LOG.debug("AMP - getFcIdVideoYouTube: " +dto.getFcIdVideoYouTube().trim());
		
		String media="";
		
		if(!dto.getFcIdVideoOoyala().trim().equals("") || !dto.getFcIdVideoYouTube().trim().equals("")){
			media=getVideoAMP(dto);
		}
		else
		{		
			StringBuffer mediaImage = new StringBuffer("");
			mediaImage.append("<div class=\"panel-principal-media\">");
			mediaImage.append("<amp-img width=\"750\" height=\"450\" layout=\"responsive\" src=\""+dto.getFcImgPrincipal()+"\"></amp-img>");
			mediaImage.append(" </div>");
			mediaImage.append("<div class=\"panel-image-meta\">");
			mediaImage.append("<p><small><i class=\"far fa-camera\"></i></small>"+dto.getFcPieFoto()+"</p>");
			mediaImage.append("</div>");			
			media = mediaImage.toString();
		}
		
		LOG.debug("media: "+media);
		return media;
	}
	
	
	/*
	 * Se lleva a cabo el reemplazo del Media Content de la nota,
	 * de tipo video, se valida si es youtube u ooyala
	 * @param  ContentDTO Instancia con la informacion necesaria para reemplazar
	 * @return String Se devuelve una cadena con el Media Content de tipo Video
	 * */	
	private static String getVideoAMP(ContentDTO dto) 
	{	
		StringBuffer mediaContent = new StringBuffer();
		String IdVideoYouTube = dto.getFcIdVideoYouTube() == null? "":dto.getFcIdVideoYouTube().trim();  
		String IdVideoOoyala = dto.getFcIdVideoOoyala() == null? "" : dto.getFcIdVideoOoyala().trim();
		String IdPlayerVideoOoyala = dto.getFcIdPlayerOoyala() == null? "" : dto.getFcIdPlayerOoyala().trim();
		
		if(!IdVideoYouTube.trim().equals(""))
		{			
           mediaContent.append("<div class=\"panel-principal-media\">");           
           mediaContent.append("<amp-youtube data-videoid=\""+IdVideoYouTube+"\" layout=\"responsive\" width=\"480\" height=\"270\"></amp-youtube>");
           mediaContent.append(" </div>");		
		}
		else if(!IdVideoOoyala.trim().equals("") && !IdPlayerVideoOoyala.trim().equals(""))
		{					
           mediaContent.append("<div class=\"panel-principal-media\">");           
           mediaContent.append("<amp-ooyala-player data-embedcode=\""+IdVideoOoyala+"\" data-pcode=\""+dto.getFcPCode()+"\" data-playerid=\""+IdPlayerVideoOoyala+"\" data-playerversion=\"v4\" layout=\"responsive\" width=\"640\" height=\"360\"></amp-ooyala-player>");
           mediaContent.append(" </div>");
			
		}
		return mediaContent.toString();
	}
	
	/*
	 * Se genera el HTML de la galeria
	 * @param  ContentDTO Instancia con la información necesaria para reemplazar
	 * @return String Se devuelve el HTML dela galeria
	 * */	
	private static String getGaleriaAMP(ContentDTO dto) {
		StringBuffer mediaImage = new StringBuffer("");
		String galeria = dto.getClGaleriaImagenes() == null?"":dto.getClGaleriaImagenes();
		if(!galeria.trim().equals("")){
		String listSRC[]=StringUtils.substringsBetween(galeria,"src=\"", "\">");
		String listDesc[]=StringUtils.substringsBetween(galeria,"<p>","<u>");
		String listPie[]=StringUtils.substringsBetween(galeria,"<u>","</u>");
			if(listSRC.length == listDesc.length && listSRC.length == listPie.length){
				mediaImage.append("<div class=\"gallery\">");
				for (int i = 0; i < listSRC.length; i++) {
					mediaImage.append("<div class=\"item-gallery\">");
					mediaImage.append("<amp-img width=\"545\" height=\"360\" layout=\"responsive\" src=\""+listSRC[i]+"\"></amp-img>");
					mediaImage.append("<p>"+cambiaCaracteres(listDesc[i])+" ");
					mediaImage.append("<u>"+cambiaCaracteres(listPie[i])+"</u>");
					mediaImage.append("</p>");
					mediaImage.append("</div>");
				}
				mediaImage.append("</div>");
				
			}
		}
		return mediaImage.toString();
	}
	
	/*
	 * Se realiza a cabo la lógica para poner el codigo adecuado de las redes sociales
	 * que va embebido en el Rich Text Format de la nota
	 * @param  String RTFContenido de la nota
	 * @return String Se regresa una cadena con el RTF adecuado para las redes sociales
	 * */	
	private static String getEmbedPostAMP(String RTFContenido){
		try {
			String rtfContenido=RTFContenido;
			String url, cadenaAReemplazar;
			StringBuffer embedCode;
			HashMap<String,ArrayList<RedSocialEmbedPostDTO>> MapAReemplazar = new HashMap<String,ArrayList<RedSocialEmbedPostDTO>>();
			int num_post_embebidos;
			int contador;
			if(rtfContenido.contains("[instagram")){
				//LOG.info("Embed Code instagram");
				ArrayList<RedSocialEmbedPostDTO> listRedSocialEmbedInstagram=new ArrayList<RedSocialEmbedPostDTO>();
				num_post_embebidos=rtfContenido.split("\\[instagram=").length-1;
				contador=1;
				do{
					RedSocialEmbedPostDTO embebedPost=new RedSocialEmbedPostDTO();
					String cadenas=devuelveCadenasPost("instagram", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode.append("<amp-instagram data-shortcode=\""+StringUtils.substringBetween(url, "https://www.instagram.com/p/", "/")+"\" width=\"300\" height=\"300\" layout=\"responsive\"></amp-instagram>\n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("instagram");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedInstagram.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("instagram", listRedSocialEmbedInstagram);
			}
			if(rtfContenido.contains("[twitter")){
				//LOG.info("Embed Code twitter");
				ArrayList<RedSocialEmbedPostDTO> listRedSocialEmbedTwitter=new ArrayList<RedSocialEmbedPostDTO>();
				num_post_embebidos=rtfContenido.split("\\[twitter=").length-1;
				contador=1;
				do{
					RedSocialEmbedPostDTO embebedPost=new RedSocialEmbedPostDTO();
					String cadenas=devuelveCadenasPost("twitter", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
							
					embedCode.append(" <amp-twitter class=\"twitter\" width=\"400\" height=\"300\" layout=\"responsive\" data-tweetid=\""+url.split("/status/")[1]+"\" data-cards=\"hidden\"></amp-twitter> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("twitter");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedTwitter.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("twitter", listRedSocialEmbedTwitter);
			
			}
			if(rtfContenido.contains("[facebook")){
				//LOG.info("Embed Code facebook");
				ArrayList<RedSocialEmbedPostDTO> listRedSocialEmbedFacebook=new ArrayList<RedSocialEmbedPostDTO>();
				num_post_embebidos=rtfContenido.split("\\[facebook=").length-1;
				contador=1;
				do{
					RedSocialEmbedPostDTO embebedPost=new RedSocialEmbedPostDTO();
					String cadenas=devuelveCadenasPost("facebook", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode=new StringBuffer();
					if(url.contains("/videos/")){
						embedCode.append(" <amp-facebook width=\"300\" height=\"175\" layout=\"responsive\" data-embed-as=\"video\" data-href=\""+url+"\"></amp-facebook> \n");
					}else{
						embedCode.append(" <amp-facebook width=\"600\" height=\"300\" layout=\"responsive\" data-href=\""+url+"\"></amp-facebook>  \n");
					}
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("facebook");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedFacebook.add(embebedPost);
					contador++;;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("facebook", listRedSocialEmbedFacebook);
			}
			if(rtfContenido.contains("[giphy")){
				//LOG.info("Embed Code giphy");
				ArrayList<RedSocialEmbedPostDTO> listRedSocialEmbedGiphy=new ArrayList<RedSocialEmbedPostDTO>();
				num_post_embebidos=rtfContenido.split("\\[giphy=").length-1;
				contador=1;
				do{
					RedSocialEmbedPostDTO embebedPost=new RedSocialEmbedPostDTO();
					String cadenas=devuelveCadenasPost("giphy", rtfContenido);
					//cadenas giphy: [giphy=http://giphy.com/gifs/sassy-batman-ZuM7gif8TCvqU,http://i.giphy.com/rgg2PJ6VJTyPC.gif=giphy]|http://giphy.com/gifs/sassy-batman-ZuM7gif8TCvqU,http://i.giphy.com/rgg2PJ6VJTyPC.gif
					//cadenas giphy: [giphy=http://giphy.com/gifs/superman-funny-wdh1SvEn0E06I,http://i.giphy.com/wdh1SvEn0E06I.gif=giphy]|http://giphy.com/gifs/superman-funny-wdh1SvEn0E06I,http://i.giphy.com/wdh1SvEn0E06I.gif

					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode=new StringBuffer();
					embedCode.append(" <amp-img class=\"giphy\" src=\""+url.split("\\,")[1]+"\" width=\"300\" height=\"125\" layout=\"responsive\"></amp-img> \n");
					embedCode.append(" <span> V&iacute;a  \n");
					embedCode.append(" 	<a href=\""+url.split("\\,")[0]+"\" target=\"_blank\">Giphy</a> \n");
					embedCode.append("  </span> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("giphy");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedGiphy.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("giphy", listRedSocialEmbedGiphy);
			}
			
			
			if(!MapAReemplazar.isEmpty()){
				Iterator<String> iterator_red_social = MapAReemplazar.keySet().iterator();
				String red_social="", codigo_embebido="", cadena_que_sera_reemplazada="";
				while(iterator_red_social.hasNext()){
					red_social = iterator_red_social.next();
			        if(red_social.equalsIgnoreCase("twitter") || red_social.equalsIgnoreCase("facebook") || red_social.equalsIgnoreCase("instagram") 
			        		|| red_social.equalsIgnoreCase("giphy")){
			        	ArrayList<RedSocialEmbedPostDTO> listEmbebidos=MapAReemplazar.get(red_social);
			        	for (RedSocialEmbedPostDTO redSocialEmbedPost : listEmbebidos) {
				        	cadena_que_sera_reemplazada=redSocialEmbedPost.getCadena_que_sera_reemplazada();
				        	codigo_embebido=redSocialEmbedPost.getCodigo_embebido();
				        	RTFContenido=RTFContenido.replace(cadena_que_sera_reemplazada, codigo_embebido);
						}
			        	
			        }
			    } 
			}
			try {
				String listStyles[]=StringUtils.substringsBetween(RTFContenido,"style=\"","\"");
				for (String style : listStyles) {
					RTFContenido = RTFContenido.replace(style,"");
				}
				RTFContenido = RTFContenido.replace("style=\"\"","");
				
			} catch (Exception e) {
				LOG.debug("Error al sustituir styles");
			}
			
			try {
				StringBuffer widget=new StringBuffer();
				widget.append("<amp-iframe class=\"video\" width=\"300px\" height=\"150px\" layout=\"responsive\" sandbox=\"allow-scripts allow-same-origin allow-popups allow-forms\" src=\"https://www.showt.com/widgets/US-Election?showtee_id=&amp;language_filter=ES&amp;theme=light&amp;event_id=&amp;partner_id=b982ecc6-af9d-4b94-a073-fc40d15ce9e0&amp;fullscreen_link=https%3A%2F%2Fwww.showt.com%2Fus-election%2FES&amp;widget_country=MX&amp;widget_lang=ES&amp;stream_id=&amp;window_type=showtbox&amp;intent=show-showtees\">\n");
				widget.append("        <amp-img layout=\"fill\" src=\"/recursos_mobile_first/css/img/usa_flag.jpg\" placeholder></amp-img>\n");
				widget.append("</amp-iframe>\n");
				RTFContenido = RTFContenido.replace("[widget-elecciones-eeuu]", widget.toString());
			} catch (Exception e) {
				RTFContenido = RTFContenido.replace("[widget-elecciones-eeuu]", "");
				LOG.error("Error al sustituir [widget-elecciones-eeuu]");
			}
			
			try {
				StringBuffer widget=new StringBuffer();
				widget.append(" <amp-iframe class=\"video\" width=\"300px\" height=\"175px\" frameborder=\"0\" layout=\"responsive\" sandbox=\"allow-scripts allow-same-origin allow-popups allow-forms\" src=\"https://widgets.unotv.com/mapa-eleciones/\">\n");
				widget.append(" <amp-img layout=\"fill\" src=\"/recursos_mobile_first/css/img/usa_flag.jpg\" placeholder></amp-img>\n");
				widget.append(" </amp-iframe>\n");
				
				RTFContenido = RTFContenido.replace("[mapa-elecciones-eeuu]", widget.toString());
			} catch (Exception e) {
				RTFContenido = RTFContenido.replace("[mapa-elecciones-eeuu]", "");
				LOG.error("Error al sustituir [mapa-elecciones-eeuu]");
			}
			return RTFContenido;
		} catch (Exception e) {
			LOG.error("Error getEmbedPost: ",e);
			return RTFContenido;
		}
	}
	
	
	/*
	 * Se regresa una cadena separada por | en donde se especifica la cadena a ser reemplazada y la url de la red social
	 * @param  String id_red_social es el identificador de la red social a buscar, para poder reemplazar sus urls
	 * @param  rtfContenido Toto el Rich Text Conten de la nota
	 * @return String La cadena separada por |
	 * */
	private static String devuelveCadenasPost(String id_red_social, String rtfContenido){
		String url="", cadenaAReemplazar="", salida="";
		try {
			cadenaAReemplazar=rtfContenido.substring(rtfContenido.indexOf("["+id_red_social+"="), rtfContenido.indexOf("="+id_red_social+"]"))+"="+id_red_social+"]";
			url=cadenaAReemplazar.replace("["+id_red_social+"=", "").replace("="+id_red_social+"]", "");
			salida=cadenaAReemplazar+"|"+url;
		} catch (Exception e) {
			LOG.error("Error devuelveCadenasPost: ",e);
			return "|";
		}
		return salida;
	}
	
	
	/**
	 * Metodo que cambia caracteres especiales por caracteres en código HTML
	 * @param  String     
	 * @return String
	 * */
	private static String cambiaCaracteres(String texto) {			
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
		
		//texto = texto.replaceAll("\"", "&#34;"); 
        
		return texto;
	}
	
	
	private String getHTMLTipoNota(String tipoNota)
	{
		String html = "";		
		if(tipoNota.equals("video"))
			html = "<i class=\"far fa-play\"></i>";
		else if(tipoNota.equals("galeria"))
			html = "<i class=\"far fa-images\"></i>";
		else if(tipoNota.equals("infografia"))
			html = "<i class=\"far fa-images\"></i>";
        else if(tipoNota.equals("image"))
        	html = "<i class=\"far fa-play\"></i>";
        else if(tipoNota.equals("imagen"))
        	html = "";
        else
        	html = "";        	
		return html;
	}
	
	/*
	 * 
	 * */
	private String getUrlAdServer(ContentDTO contentDTO)
	{			
		String url_adserver = "";
		try {						
			if(contentDTO.getFcTipoSeccion().equals("noticias"))
			{
				if(contentDTO.getFcSeccion().equals("estados"))				
					url_adserver ="/121173452/UnoTV/"+contentDTO.getFcTipoSeccion()+"/"+contentDTO.getFcSeccion()+"/";				
				else
					url_adserver ="/121173452/UnoTV/"+contentDTO.getFcTipoSeccion()+"/"+contentDTO.getFcIdCategoria()+"/";
			}				
			else if(contentDTO.getFcTipoSeccion().equals("videoblog"))
				url_adserver ="/121173452/UnoTV/"+contentDTO.getFcTipoSeccion()+"s/"+contentDTO.getFcSeccion()+"/";
			else
				url_adserver ="/121173452/UnoTV/noticias/inicio/";
			
		} catch (Exception e) {
			url_adserver ="/121173452/UnoTV/noticias/inicio/";
			LOG.error("Exception: en getUrlAdServer",e);
		}
		return url_adserver; 
	}
	
	
	/*
	 * Se escribe fisicamente un archivo de tipo HTML
	 * @param  String Path del directorio donde se va a guardar el HTML
	 * @param String Codigo HTML
	 * @return boolean
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
	
	
	
	/*
	 * 
	 * */
	private String remplazaGaleriaItem(String strJsonGaeria, String HTML)
	{
		LOG.debug("strJsonGaeria: "+strJsonGaeria);
		try {
			
			JSONObject jsonGaleria = new JSONObject(strJsonGaeria);		
			LOG.debug("Total Img: "+jsonGaleria.getString("contadorArchivos0"));
			int totalImg = Integer.valueOf(jsonGaleria.getString("contadorArchivos0"));			
			LOG.debug("Total de imagesn: "+totalImg);
			
			//LOG
			for (int i = 0; i <= totalImg; i++) {				
				String itemGallery = "<div class=\"gallery\"><div class=\"item-gallery\"><amp-img width=\"545\" height=\"360\" layout= \"responsive\" src=\"$GALLERY_URL_IMG$\"></amp-img><p>$GALLERY_DESCRIPCION_IMG$<u>$GALLERY_PIE_IMG$</u></p></div></div>";				
				itemGallery = itemGallery.replace("$GALLERY_URL_IMG$",jsonGaleria.getString("name[0]["+i+"]"));
				itemGallery = itemGallery.replace("$GALLERY_DESCRIPCION_IMG$",jsonGaleria.getString("descripcion[0]["+i+"]"));
				itemGallery = itemGallery.replace("$GALLERY_PIE_IMG$",jsonGaleria.getString("pie[0]["+i+"]"));				
				LOG.debug("[=foto"+i+"=]");
				LOG.debug("name:        "+jsonGaleria.getString("name[0]["+i+"]"));
				LOG.debug("descripcion: "+jsonGaleria.getString("descripcion[0]["+i+"]"));
				LOG.debug("pie:         "+jsonGaleria.getString("pie[0]["+i+"]"));				
				HTML = HTML.replace("[=foto"+i+"=]", itemGallery);				
			}
					
		} catch (Exception e) {
			LOG.error("Exception en remplazaGaleriaItem: ",e);
		}		
		return HTML;
		
	}
}//FIN CLASS


