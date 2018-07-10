package mx.com.amx.unotv.workflow.bo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import mx.com.amx.unotv.workflow.bo.exception.ProcesoWorkflowException;
import mx.com.amx.unotv.workflow.bo.exception.RemplazaHTMLBOException;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.Nota;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;
import mx.com.amx.unotv.workflow.dto.RedSocialEmbedPostDTO;
import mx.com.amx.unotv.workflow.utileria.UtilsHTML;

public class RemplazaHTMLBO {
	
	//LOG
	private static Logger LOG = Logger.getLogger(RemplazaHTMLBO.class);	
	
	/**
	 * 
	 * 
	 * */
	public void creaHTML(ParametrosDTO parametrosDTO, ContentDTO contentDTO)
	{
		LOG.debug("Inicio creaHTML");
		try {			
			
			Document doc = Jsoup.connect(parametrosDTO.getBasePaginaPlantilla()).timeout(120000).get();			 
			
			LOG.debug(parametrosDTO.getBasePaginaPlantilla());
			String HTML = doc.html();			
									
			HTML = remplazaPantilla(HTML, contentDTO, parametrosDTO);			
			//Remplazamos thema o otras cosas
			HTML = HTML.replace(parametrosDTO.getBaseTheme(),"/"+parametrosDTO.getCarpetaResources()+"/");			
			
			String rutaConteido = getRutaContenido(contentDTO);									
			String rutaHTML =parametrosDTO.getPathFiles() + rutaConteido+"/"+parametrosDTO.getNameHTML();
			LOG.info("Ruta HTML: "+rutaHTML);
			UtilsHTML.writeHTML(rutaHTML, HTML);						
			LOG.info("Genero HTML: : "+true);				
		} catch (Exception e) {
			LOG.error("Exception en creaHTML: ",e);
		}		
	}
	
	/*
	 * 
	 * **/
	public void creaHTMLTest(ParametrosDTO parametrosDTO, ContentDTO contentDTO)
	{
		LOG.debug("Inicio creaHTML");
		try {						
			Document doc = Jsoup.connect(parametrosDTO.getBasePaginaPlantilla()).timeout(120000).get();			 
			String HTML = doc.html();	
			
			//
			HTML = remplazaPantilla(HTML, contentDTO, parametrosDTO);						
			//Remplazamos thema o otras cosas
			HTML = HTML.replace(parametrosDTO.getBaseTheme(),"http://pruebas-unotv.tmx-internacional.net/"+ parametrosDTO.getCarpetaResources() + "/");			
			//
			String rutaConteido = getRutaContenido(contentDTO);									
			String rutaHTML =parametrosDTO.getPathFilesTest() + rutaConteido+"/"+parametrosDTO.getNameHTML();
			LOG.info("Ruta HTML: "+rutaHTML);
			UtilsHTML.writeHTML(rutaHTML, HTML);						
			LOG.info("Genero HTML: : "+true);				
		} catch (Exception e) {
			LOG.error("Exception en creaHTML: ",e);
		}		
	}
		
		
	/*
	 * Remplaza valores de la platilla
	 * */
	private String remplazaPantilla(String HTML, ContentDTO contentDTO, ParametrosDTO parametrosDTO)
	{
		
		
		
		LOG.debug("Inicia remplazaPantilla");
		
		//$WCM_TITULO_COMENTARIO$
		try {
			String titulo_comentario=contentDTO.getFcTituloComentario() == null || contentDTO.getFcTituloComentario().equals("")?"¿Qué opinas?":contentDTO.getFcTituloComentario();
			HTML = HTML.replace("$WCM_TITULO_COMENTARIO$", StringEscapeUtils.escapeHtml(titulo_comentario));
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_TITULO_COMENTARIO$", "");
			LOG.error("Error al sustituir $WCM_TITULO_COMENTARIO$");
		}
		
		// Remplaza comscore
		try {		
			HTML = HTML.replace("$WCM_NAVEGACION_COMSCORE$", contentDTO.getFcTipoSeccion() + "." + contentDTO.getFcSeccion()+"."+ contentDTO.getFcIdCategoria()+ "." + parametrosDTO.getPathDetalle() + "." + contentDTO.getFcNombre());
		} catch (Exception e) {
			LOG.error("Error al sustituir navegacion  comscore");
		}
		
		//$WCM_DESCRIPCION_CONTENIDO$
		try {
			HTML = HTML.replace("$WCM_DESCRIPCION_CONTENIDO$", UtilsHTML.htmlEncode(contentDTO.getFcDescripcion().trim()));
		} catch(Exception e) {
			HTML = HTML.replace("$WCM_DESCRIPCION_CONTENIDO$", "");
			LOG.error("Error al remplazar $WCM_DESCRIPCION_CONTENIDO$");
		}
		
		//$WCM_ID_CATEGORIA$
		try {
			HTML = HTML.replace("$WCM_ID_CATEGORIA$", contentDTO.getFcIdCategoria().trim());
		} catch(Exception e) {
			HTML = HTML.replace("$WCM_ID_CATEGORIA$", "");
			LOG.error("Error al remplazar $WCM_ID_CATEGORIA$");
		}
		
		//$WCM_TITLE_CONTENIDO$
		try {
			HTML = HTML.replace("$WCM_TITLE_CONTENIDO$",StringEscapeUtils.escapeHtml(contentDTO.getFcTitulo().trim()));
		} catch(Exception e) {
			HTML = HTML.replace("$WCM_TITLE_CONTENIDO$", "");
			LOG.error("Error al remplazar $WCM_TITLE_CONTENIDO$");
		}
		
		//$WCM_FECHA$
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			HTML = HTML.replace("$WCM_FECHA$", format.format(contentDTO.getFdFechaPublicacion()));
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_FECHA$", "");
			LOG.error("Error al remplazar $WCM_FECHA$");
		}
		
		//$WCM_HORA$
		try {
			HTML = HTML.replace("$WCM_HORA$", contentDTO.getFcHora());
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_HORA$", "");
			LOG.error("Error al remplazar $WCM_HORA$");
		}
		
		//$WCM_AUTOR$
		try {
			String autor = contentDTO.getFcEscribio() == null? "": contentDTO.getFcEscribio();
			HTML = HTML.replace("$WCM_AUTOR$", StringEscapeUtils.escapeHtml(autor).trim());
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_AUTOR$", "");
			LOG.error("Error al remplazar $WCM_AUTOR$");
		}
		
		//$WCM_LUGAR$
		try {
			String lugar = contentDTO.getFcLugar() == null? "": contentDTO.getFcLugar();
			HTML = HTML.replace("$WCM_LUGAR$", StringEscapeUtils.escapeHtml(lugar));
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_LUGAR$", "");
			LOG.error("Error al remplazar $WCM_LUGAR$");
		}
		
		//$WCM_CATEGORIA$
		try {
			String nombreCategoria = contentDTO.getFcNombreCategoria() == null? "": contentDTO.getFcNombreCategoria();
			HTML = HTML.replace("$WCM_CATEGORIA$", StringEscapeUtils.escapeHtml(nombreCategoria));
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_CATEGORIA$", "");
			LOG.error("Error al remplazar $WCM_CATEGORIA$");
		}
		
		//$WCM_FUENTE$
		try {
			String fuente = contentDTO.getFcFuente() == null? "": contentDTO.getFcFuente();
			HTML = HTML.replace("$WCM_FUENTE$", StringEscapeUtils.escapeHtml(fuente));
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_FUENTE$", "");
			LOG.error("Error al remplazar $WCM_FUENTE$");
		}
				
		//Video o Imagen principal			
		try {			
			HTML = HTML.replace("$WCM_MEDIA_CONTENT$", getMediaContent(contentDTO, parametrosDTO));		
		} catch(Exception e) {
			HTML = HTML.replace("$WCM_MEDIA_CONTENT$", "");
			LOG.error("Error al remplazar $WCM_MEDIA_CONTENT$");
		}
						
		//$WCM_RTF_CONTENIDO$
		try {
			HTML = HTML.replace("$WCM_RTF_CONTENIDO$", UtilsHTML.cambiaCaracteres(getEmbedPost(contentDTO.getClRtfContenido())));
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_RTF_CONTENIDO$", "");
			LOG.error("Error al remplazar $WCM_RTF_CONTENIDO$");
		}	
				
		//Remplazamos la galeria
		try {
			HTML = HTML.replace("[=GALERIA=]",UtilsHTML.cambiaCaracteres(contentDTO.getClGaleriaImagenes()));
		} catch (Exception e) {
			HTML = HTML.replace("[=GALERIA=]", "");
			LOG.error("Error al remplazar galeria",e);
		}		
		//Remplazamos la infografia
		try {
			HTML = HTML.replace("[=INFOGRAFIA=]","<div class=\"infografia\"><img src=\""+contentDTO.getFcImgInfografia()+"\"></div>");			
		} catch (Exception e) {
			HTML = HTML.replace("[=INFOGRAFIA=]", "");
			LOG.error("Error al remplazar la infografia",e);
		}	
		
		//$WCM_URL_PAGE$
		try {
			String url=getRutaContenido(contentDTO);
			HTML = HTML.replace("$WCM_URL_PAGE$", parametrosDTO.getDominio()+"/"+url+"/");
			HTML = HTML.replace("$URL_PAGE$", parametrosDTO.getDominio()+"/"+url+"/");
		} catch (Exception e) {
			HTML = HTML.replace("$WCM_URL_PAGE$", "");
			LOG.error("Error al remplazar $WCM_URL_PAGE$");
		}
		
		
		HTML = HTML.replace("$CLASS_MENU$", contentDTO.getFcIdCategoria().trim());
		HTML = HTML.replace("$CLASS_SUBMENU$", contentDTO.getFcIdCategoria().trim());
		HTML = HTML.replace("$CLASS_BODY$", contentDTO.getFcIdCategoria().trim());
				
		try {
			// Remplaza notas relacionadas  (Se quitaron las notas relacionadas por taboola)
			//List<Nota> listaRelacionadas = llamadasWSDAO._obtieneRelacionadas(contentDTO.getFcNombre(), contentDTO.getFcIdCategoria(), parametrosDTO).getLista();		
			//HTML = HTML.replace("$TE_RECOMENDAMOS$", remplazaRecomendados(listaRelacionadas , parametrosDTO));
		} catch (Exception e) {
			LOG.error("Error al remplazar notas relacionadas",e);
			HTML = HTML.replace("$TE_RECOMENDAMOS$", "");
		}

		//Remplaza archivo Ad-server
		try {			
			HTML = remplazaAdserverHTML(HTML, parametrosDTO, contentDTO);									
		} catch (Exception e) {
			LOG.error("Error al remplazar class y adserver",e);
		}
		
		
		//numberUnoCross Ad-server
		try {			
			String[] pala=  contentDTO.getFcNombre().split("-");
			String id="";
			if(pala.length > 1){
				id=pala[pala.length - 1];
			}
			HTML = HTML.replace("numberUnoCross", id.trim());
		} catch (Exception e) {
			HTML = HTML.replace("numberUnoCross", "");
			LOG.error("Error al remplazar numberUnoCross");			
		}
		
		//Remplaza etiquetas Ad-server
		try {
			String etiquetas = Arrays.toString(contentDTO.getFcTagsApp());
			etiquetas = etiquetas.replace("[","");
			etiquetas = etiquetas.replace("]","");
			HTML = HTML.replace("$ETIQUETAS$" , etiquetas);
		} catch(Exception e) {
			HTML = HTML.replace("$ETIQUETAS$", "");
			LOG.error("Error al remplazar $ETIQUETAS$");
		}
		
		//Remplaza etiquetas data-layer
		try {
			String etiquetas_dataleyer = "";
			for (String etiqueta : contentDTO.getFcTagsApp()) {				
				etiquetas_dataleyer = etiquetas_dataleyer+"'"+etiqueta+"',"; 			
			}			
			etiquetas_dataleyer = etiquetas_dataleyer.substring(0, etiquetas_dataleyer.length()-1);			
			HTML = HTML.replace("$ETIQUETAS_DATALAYER$" , etiquetas_dataleyer);			
		} catch (Exception e) {
			HTML = HTML.replace("$ETIQUETAS_DATALAYER$", "");
			LOG.error("Error al remplazar $ETIQUETAS_DATALAYER$");
		}		
		
		//Remplaza metas
		HTML = remplazaMetas(HTML, contentDTO, parametrosDTO);
		
		//Base URL
		try 
		{
			String valorBase [] = HTML.split("<base");
			valorBase[0] = valorBase[1].substring(0, valorBase[1].indexOf("/>"));
			String tmp [] = valorBase[0].split("href=\"");
			String base = tmp[1].substring(0, tmp[1].indexOf("\""));
			HTML = HTML.replace(base, parametrosDTO.getBaseURL());
		} catch (Exception e) {
			LOG.debug("No tiene base URL");
		}			
		
		//Remplaza parametro js y styles
		try {			
			/*String cad="version_styles,$VERSION_STYLES$|version_scripts,$VERSION_SCRIPT$|version_libs,$VERSION_LIBS$|version_gas_json,$VERSION_GAS_JSON$|version_banner_json,$VERSION_BANNER_JSON$";
			String [] params=parametrosDTO.getCatalogoParametros().split("\\|");
			String valor="";
			String cad_a_reemplazar="";
			for (int i = 0; i < params.length; i++) {
				valor=llamadasWSBO.getParameter(params[i].split("\\,")[0], parametrosDTO);
				cad_a_reemplazar=params[i].split("\\,")[1];
				if(valor!=null && !valor.equals("")){
					HTML = HTML.replace(cad_a_reemplazar ,valor);
				}else{
					HTML = HTML.replace(cad_a_reemplazar ,"");
				}
			}*/
		} catch(Exception e) {
			LOG.error("Error al remplazar version de estilos"+e.getLocalizedMessage());
		}
		
		
		HTML = HTML.replace(parametrosDTO.getBasePagesPortal(), "");		
		return HTML;		
	}
	
	/*
	 * Metodo que remplaza los metas de una nota
	 * @param  HTML
     * @param  ContentDTO
     * @param  ParametrosDTO      
	 * @return boolean
	 * @author jesus
	 * */
	private static String remplazaMetas(String HTML, ContentDTO contentDTO, ParametrosDTO parametrosDTO){
		TimeZone tz = TimeZone.getTimeZone("America/Mexico_City");
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		df.setTimeZone(tz);
		Date date = new Date();
				
		// Remplaza og:image
		try {
			String imageFija="/utils/img/default-noticias.png";
			HTML = HTML.replace(imageFija, contentDTO.getFcImgPrincipal().trim());			
		} catch (Exception e) {
			LOG.error("Error al reemplazar meta og:image");
		}
		
		// Remplaza twitter:image
		try {
			HTML = HTML.replace("twitter:image", "twitter:image:src");			
		} catch (Exception e) {
			LOG.error("Error al reemplazar meta twitter:image");
		}
		
		try {
			HTML = HTML.replace("$WCM_KEYWORDS$", UtilsHTML.cambiaCaracteres(contentDTO.getFcKeywords().trim()));
		} catch(Exception e) {
			HTML = HTML.replace("$WCM_KEYWORDS$", "");
			LOG.error("Error al remplazar $WCM_KEYWORDS$");
		}
		
		//Remplaza nota:published_time [$META_PUBLISHED_TIME$]
		try {
			String fechaS=df.format(contentDTO.getFdFechaPublicacion());
			fechaS=fechaS.substring(0, fechaS.length()-2)+":00";
			HTML = HTML.replace("$META_PUBLISHED_TIME$" ,fechaS);
		} catch(Exception e) {
			HTML = HTML.replace("$META_PUBLISHED_TIME$", "");
			LOG.error("Error al remplazar $META_PUBLISHED_TIME$");
		}

		//Remplaza nota:modified_time [$META_MODIFIED_TIME$]
		try {
			String fechaS=df.format(date);
			fechaS=fechaS.substring(0, fechaS.length()-2)+":00";
			HTML = HTML.replace("$META_MODIFIED_TIME$" ,fechaS);
		} catch(Exception e) {
			HTML = HTML.replace("$META_MODIFIED_TIME$", "");
			LOG.error("Error al remplazar $META_MODIFIED_TIME$");
		}
		//Remplaza nota:tipo [$META_CONTENT_ID$]
		try {
			HTML = HTML.replace("$META_CONTENT_ID$" ,contentDTO.getFcIdContenido());
		} catch(Exception e) {
			HTML = HTML.replace("$META_CONTENT_ID$", "");
			LOG.error("Error al remplazar $META_CONTENT_ID$");
		}
		//Remplaza nota:tipo [$META_FRIENDLY_URL$]
		try {
			HTML = HTML.replace("$META_FRIENDLY_URL$" ,contentDTO.getFcNombre());
		} catch(Exception e) {
			HTML = HTML.replace("$META_FRIENDLY_URL$", "");
			LOG.error("Error al remplazar $META_FRIENDLY_URL$");
		}
		//Remplaza nota:tipo [$META_TIPO$]
		try {
			HTML = HTML.replace("$META_TIPO$" ,contentDTO.getFcIdTipoNota());
		} catch(Exception e) {
			HTML = HTML.replace("$META_TIPO$", "");
			LOG.error("Error al remplazar $META_TIPO$");
		}
		
		//Remplaza nota:tipo_seccion [$META_TIPO_SECCION$]
		try {
			HTML = HTML.replace("$META_TIPO_SECCION$" ,contentDTO.getFcTipoSeccion());
		} catch(Exception e) {
			HTML = HTML.replace("$META_TIPO_SECCION$", "");
			LOG.error("Error al remplazar $META_TIPO_SECCION$");
		}
		
		//Remplaza nota:seccion [$META_SECCION$]
		try {
			HTML = HTML.replace("$META_SECCION$" ,contentDTO.getFcSeccion());
		} catch(Exception e) {
			HTML = HTML.replace("$META_SECCION$", "");
			LOG.error("Error al remplazar $META_SECCION$");
		}
		
		//Remplaza nota:categoria [$META_CATEGORIA$]
		try {
			HTML = HTML.replace("$META_CATEGORIA$" ,contentDTO.getFcIdCategoria());
		} catch(Exception e) {
			HTML = HTML.replace("$META_CATEGORIA$", "");
			LOG.error("Error al remplazar $META_CATEGORIA$");
		}
		
		//Remplaza nota:tags [$META_TAGS$]
		try {
			HTML = HTML.replace("$META_TAGS$" , UtilsHTML.cambiaCaracteres(contentDTO.getFcTags()));
		} catch(Exception e) {
			HTML = HTML.replace("$META_TAGS$", "");
			LOG.error("Error al remplazar $META_TAGS$");
		}
		
		//Remplaza nota:tags [$META_IMG$]
		try {
			HTML = HTML.replace("$META_IMG$" ,contentDTO.getFcImgPrincipal());
		} catch(Exception e) {
			HTML = HTML.replace("$META_IMG$", "");
			LOG.error("Error al remplazar $META_IMG$");
		}
		
		//Remplaza nota:tags [$META_TITULO$]
		try {
			HTML = HTML.replace("$META_TITULO$" , UtilsHTML.htmlEncode(contentDTO.getFcTitulo().trim()));
		} catch(Exception e) {
			HTML = HTML.replace("$META_TITULO$", "");
			LOG.error("Error al remplazar $META_TITULO$");
		}
		
		// Remplaza los metas de video ooyala
		if(!contentDTO.getFcIdVideoOoyala().equals("") && !contentDTO.getFcIdPlayerOoyala().equals("")){
			try {							
				HTML = HTML.replace("$OG_VIDEO$",parametrosDTO.getMetaVideo().replace("$ID_VIDEO$", contentDTO.getFcIdVideoOoyala()).replace("$ID_VIDEO_PLAYER$", contentDTO.getFcIdPlayerOoyala()));
				HTML = HTML.replace("$OG_VIDEO_SECURE$",parametrosDTO.getMetaVideoSecureUrl().replace("$ID_VIDEO$", contentDTO.getFcIdVideoOoyala()).replace("$ID_VIDEO_PLAYER$", contentDTO.getFcIdPlayerOoyala()));
			} catch (Exception e) {
				HTML = HTML.replace("$OG_VIDEO_SECURE$", "");
				HTML = HTML.replace("$OG_VIDEO$", "");
				LOG.error("Error al sustituir metas de Video $OG_VIDEO$ y $OG_VIDEO_SECURE$");
			}
		}else{
			HTML = HTML.replace("<meta property=\"og:video\" content=\"$OG_VIDEO$\" />", "");
			HTML = HTML.replace("<meta property=\"og:video:secure_url\" content=\"$OG_VIDEO_SECURE$\" />", "");
			HTML = HTML.replace("<meta property=\"og:video:type\" content=\"application/x-shockwave-flash\" />", "");
			HTML = HTML.replace("<meta property=\"og:type\" content=\"video.other\" />", "");
			HTML = HTML.replace("<meta property=\"og:video:height\" content=\"480\" />", "");
			HTML = HTML.replace("<meta property=\"og:video:width\" content=\"640\" />", "");
		}					
		return HTML;
	}
	
	
	/*
	 * Se lleva a cabo el reemplazo del Media Content de la nota,
	 * puede ser solo reemplazo de la imagen principal o del vide.
	 * @param  ContentDTOInstancia con la información necesaria para reemplazar
	 * @return String Se devuelve una cadena con el Media Content
	 * @author 
	 * */	
	private static String getMediaContent(ContentDTO dto, ParametrosDTO parametrosDTO)
	{		
		String media="";
		if(!dto.getFcIdVideoOoyala().trim().equals("") || !dto.getFcIdVideoYouTube().trim().equals("") || !dto.getFcIdPlayerOoyala().trim().equals("")){
			media=getVideo(dto);
		}else{
			media=getImagen(dto);
		}
		return media;
	}
	
	
	

	/*
	 * Obtiene el html de la imagen principal
	 * @param  ContentDTO, Instancia con la información necesaria para reemplazar
	 * @author 
	 * */
	private static String getImagen(ContentDTO dto) 
	{
		StringBuffer mediaImage = new StringBuffer("");
		String imgPrincipal = dto.getFcImgPrincipal() == null?"":dto.getFcImgPrincipal();
		String pieFoto = dto.getFcPieFoto() == null?"":dto.getFcPieFoto().trim();
		mediaImage.append("<div class=\"panel-principal-media\"><img src=\""+imgPrincipal+"\" alt=\""+StringEscapeUtils.escapeHtml(pieFoto)+"\"></div>\n");
		mediaImage.append("<div class=\"panel-image-meta\">\n");
		mediaImage.append("   <p><small><i class=\"far fa-camera\"></i></small>"+StringEscapeUtils.escapeHtml(pieFoto)+"</p>\n");		
		mediaImage.append("</div>\n");
		return mediaImage.toString();
	}
	
	
	

	
	/*
	 * Metodo encargado de regresar una cadena con la definicion de la ruta de la nota
	 * en el webServer, por ejemplo: "noticias/estados/estado-de-mexico/detalle/finaliza-conteo-distrital-en-edomex-se-confirma-triunfo-de-del-mazo-202664"
	 * @param  ContentDTO, Objeto que contiene la información necesaria para el path
     * @param  ParametrosDTO, Objeto con informacion adicional para el path
	 * @return String, Se regresa el path de guardado de un contenido en el WebServer
	 * @throws ProcesoWorkflowException
	 * @author 
	 * */
	public static String getRutaContenido(ContentDTO contentDTO) throws ProcesoWorkflowException
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
			
			rutaContenido = tipoSeccion + "/" + id_seccion +"/"+ id_categoria+"/detalle/" +contentDTO.getFcNombre();			
			LOG.debug("rutaContenido: "+rutaContenido);			
		} catch (Exception e) {
			LOG.error("Error getRutaContenido: ",e);
			throw new ProcesoWorkflowException(e.getMessage());
		}
		return rutaContenido;
	}
	
	
	/**
	 * Se regresa una cadena con todo el Texto de una nota, reemplazando los valores de sus respectivas redes sociales 
	 * que fueron inssertadas por los redactores.
	 * @param  String Toto el Rich Text Conten de la nota
	 * @return String Se regresa una nueva cadena con los replace de los widgets de las redes sociales.
	 * @author jesus
	 * */
	private static String getEmbedPost(String RTFContenido)
	{
		try {
			String rtfContenido = RTFContenido;
			
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
					embedCode.append(" <div class=\"instagram-post\"> \n");
					embedCode.append(" <blockquote data-instgrm-captioned data-instgrm-version=\"6\" class=\"instagram-media\"> \n");
					embedCode.append(" <div> \n");
					embedCode.append(" 	<p><a href=\""+url+"\"></a></p> \n");
					embedCode.append(" </div> \n");
					embedCode.append(" </blockquote> \n");
					embedCode.append(" <script async defer src=\"//platform.instagram.com/en_US/embeds.js\"></script> \n");
					embedCode.append(" </div> \n");
					
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
					embedCode.append(" <div class=\"tweeet-post\"> \n");
					embedCode.append(" 		<blockquote data-width=\"500\" lang=\"es\" class=\"twitter-tweet\"><a href=\""+url+"\"></a></blockquote> \n");
					embedCode.append(" 		<script type=\"text/javascript\" async defer src=\"//platform.twitter.com/widgets.js\" id=\"twitter-wjs\"></script> \n");
					embedCode.append(" </div> \n");
					
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
					embedCode.append(" <div class=\"facebook-post\"> \n");
					embedCode.append(" 		<div data-href=\""+url+"\" data-width=\"500\" class=\"fb-post\"></div> \n");
					embedCode.append(" </div> \n");
					
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
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode=new StringBuffer();
					embedCode.append(" <img src=\""+url.split("\\,")[1]+"\" class=\"giphy\"> \n");
					embedCode.append(" <span>V&iacute;a  \n");
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
			return RTFContenido;
		} catch (Exception e) {
			LOG.error("Error getEmbedPost: ",e);
			return RTFContenido;
		}
	}
	
	/**
	 * Se regresa una cadena separada por | en donde se especifica la cadena a ser reemplazada y la url de la red social
	 * @param  String, id_red_social es el identificador de la red social a buscar, para poder reemplazar sus urls
	 * @param  rtfContenido, Toto el Rich Text Conten de la nota
	 * @return String, La cadena separada por |
	 * @author jesus
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
	 * Se lleva a cabo el reemplazo del Media Content de la nota,
	 * de tipo video, se valida si es youtube u ooyala
	 * @param  ContentDTO, Instancia con la información necesaria para reemplazar
	 * @return String, Se devuelve una cadena con el Media Content de tipo Video
	 * @author Fernando
	 * */		
	private static String getVideo(ContentDTO dto) 
	{
		
		StringBuffer mediaContent = new StringBuffer();
		
		String IdVideoYouTube = dto.getFcIdVideoYouTube() == null? "":dto.getFcIdVideoYouTube().trim();  
		String IdVideoOoyala = dto.getFcIdVideoOoyala() == null? "" : dto.getFcIdVideoOoyala().trim();
		String IdPlayerVideoOoyala = dto.getFcIdPlayerOoyala() == null? "" : dto.getFcIdPlayerOoyala().trim();
		
		if(!IdVideoYouTube.trim().equals(""))
		{
			mediaContent.append(" <div class=\"panel-principal-media\"> \n");
			mediaContent.append("<iframe id=\"ytplayer\" type=\"text/html\" width=\"640\" height=\"360\" src=\"https://www.youtube.com/embed/"+IdVideoYouTube+"\" frameborder=\"0\" allowfullscreen=\"allowfullscreen\"></iframe>\n");
			mediaContent.append(" </div> \n");
		}
		else if(!IdVideoOoyala.trim().equals("") && !IdPlayerVideoOoyala.trim().equals(""))
		{			
			//VERSION 4
			mediaContent.append(" <!-- Ooyala V4--> \n");
			mediaContent.append("<div class=\"panel-principal-media\"> \n");
			mediaContent.append(" <div id=\"ooyalaplayer\"></div> \n");
			mediaContent.append(" <link rel=\"stylesheet\" href=\"//player.ooyala.com/static/v4/stable/4.13.5/skin-plugin/html5-skin.min.css\"> \n");
			mediaContent.append(" <script src=\"//player.ooyala.com/static/v4/stable/4.13.5/core.min.js\"></script> \n");
			mediaContent.append(" <script src=\"//player.ooyala.com/static/v4/stable/4.13.5/video-plugin/bit_wrapper.min.js\"></script> \n");
			mediaContent.append(" <script src=\"//player.ooyala.com/static/v4/stable/4.13.5/video-plugin/main_html5.min.js\"></script> \n");
			mediaContent.append(" <script src=\"//player.ooyala.com/static/v4/stable/4.13.5/skin-plugin/html5-skin.min.js\"></script> \n");
			mediaContent.append(" <script src=\"//player.ooyala.com/static/v4/stable/4.13.5/ad-plugin/google_ima.min.js\"></script> \n");
			mediaContent.append(" <script src=\"//player.ooyala.com/static/v4/stable/4.13.5/analytics-plugin/googleAnalytics.min.js\"></script> \n");
			mediaContent.append(" <script> \n");
			mediaContent.append("   var playerParam = { \n");
			mediaContent.append("     'pcode': '"+dto.getFcPCode()+"', \n");
			mediaContent.append("     'playerBrandingId': \""+IdPlayerVideoOoyala+"\", \n");
			mediaContent.append("     'skin': { \n");
			mediaContent.append("       'config': '/ooyala/4.13.5/skin.json' \n");
			mediaContent.append("     } \n");
			mediaContent.append("   }; \n");
			mediaContent.append("   OO.ready(function() { \n");
			mediaContent.append("     window.pp = OO.Player.create('ooyalaplayer', \""+IdVideoOoyala+"\", playerParam); \n");
			mediaContent.append("   }); \n");
			mediaContent.append(" </script> \n");
			mediaContent.append(" </div> ");
		}
		return mediaContent.toString();
	}	
	
	/**
	 * Metodo que remplaza el adserver de una pagina
	 * @param String con el HTML
	 * @param PaginasDTO, DTO con los datos de la pagina.
	 * @param ParametrosDTO, DTO con los parametros.
	 * @return HTML que se esta tratando
	 * @throws RemplazaHTMLBOException
	 * */
	public String remplazaAdserverHTML(String HTML, ParametrosDTO parametrosDTO, ContentDTO contentDTO) 
	{
		try {
			LOG.debug("Inicia remplazaAdserverHTML");
			LOG.debug("url_adserver: "+parametrosDTO.getUrl_adserver());
			LOG.debug("Seccion     : "+contentDTO.getFcSeccion());
			
			String adserver= "";
			if(contentDTO.getFcSeccion().equals("estados"))
			{
				adserver = parametrosDTO.getUrl_adserver().replace("$ID_ADSERVER$", contentDTO.getFcSeccion());
			}
			else
			{
				adserver = parametrosDTO.getUrl_adserver().replace("$ID_ADSERVER$", contentDTO.getFcIdCategoria());
			}		
			
			LOG.debug("url_adserver: "+adserver);
			StringBuffer sbHTML = new StringBuffer();			
			URL urlJava = new URL(adserver);
			BufferedReader in = new BufferedReader(new InputStreamReader(urlJava.openStream()));
			 String inputLine;
		        while ((inputLine = in.readLine()) != null)
		        	sbHTML.append(inputLine+"\n");
		        in.close();					        
		    
		    HTML = HTML.replace("<script>$AD_SERVER$</script>",sbHTML.toString());
			
		} catch (Exception e) {
			LOG.error("Exception en remplazaAdserverHTML: "+e.getMessage());
			HTML = HTML.replace("<script>$AD_SERVER$</script>","");
		}
		return HTML;
	}
	
	/*
	 * Rempalza el html de notas relacionadas
	 * @param Nota, Lista de notas relacionadas
	 * @param ParametrosDTO
	 * @return String
	 * */
	private  String remplazaRecomendados(List<Nota> listaNota, ParametrosDTO parametrosDTO)
	{
		LOG.debug("Inicia remplazaRecomendados");
		try {
			
			String htmlRelacionadas = "";
			
			if(!listaNota.isEmpty())
			{
				StringBuffer itemsRelacionadas = new StringBuffer();
				LOG.debug("<<<<< listaNota"+listaNota.size());					
				htmlRelacionadas = parametrosDTO.getHtmlNotasRelacionadas();				
				for (Nota nota : listaNota) {
					String item = parametrosDTO.getItemNotasRelacionadas();					
					item = item.replace("$ID_CATEGORIA$", nota.getIdCategoria());
					item = item.replace("$URL_NOTA$", nota.getFriendlyUrl());
					item = item.replace("$URL_IMAGEN$", nota.getImagenPrincipal());
					item = item.replace("$DESCRIPCION_CATEGORIA$", UtilsHTML.htmlEncode(nota.getCategoriaDescripcion()));
					item = item.replace("$FECHA$", nota.getFechaPublicacion());
					item = item.replace("$TITULO$", UtilsHTML.htmlEncode(nota.getTitulo()));					
					item = item.replace("$TIPO_NOTA$", getHTMLTipoNota(nota.getTipoNota()));
					LOG.debug("item: "+nota.getTitulo());
					itemsRelacionadas.append(item);
				}
				LOG.debug("item: "+itemsRelacionadas.toString());
				htmlRelacionadas = htmlRelacionadas.replace("$ITEMS$", itemsRelacionadas.toString());			
			}			
			return htmlRelacionadas;
		} catch (Exception e) {
			LOG.error("Exception en remplazaRecomendados: ",e);
			return "";
		}
		
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
	
	
}