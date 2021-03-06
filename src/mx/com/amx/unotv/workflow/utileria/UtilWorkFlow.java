package mx.com.amx.unotv.workflow.utileria;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import mx.com.amx.unotv.workflow.bo.LlamadasWSDAO;
import mx.com.amx.unotv.workflow.bo.exception.ProcesoWorkflowException;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.ParametrosDTO;
import mx.com.amx.unotv.workflow.dto.RedSocialEmbedPostDTO;

public class UtilWorkFlow {
	
	//LOG
	private final static Logger LOG = Logger.getLogger(UtilWorkFlow.class);	
		
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
	public static String getRutaContenido(ContentDTO contentDTO, ParametrosDTO parametrosDTO) throws ProcesoWorkflowException
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
	/**
	 * Regresa un boolean true si el directorio se pudo crear, o un false en caso contrario
	 * @param  String
     *         Path del directorio a ser creado
	 * @return boolean
	 * @throws ProcesoWorkflowException
	 * @author jesus
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
	 * Se regresa una cadena separada por | en donde se especifica la cadena a ser reemplazada y la url de la red social
	 * @param  String
     *         id_red_social es el identificador de la red social a buscar, para poder reemplazar sus urls
	 * @param  rtfContenido
     *		   Toto el Rich Text Conten de la nota
	 * @return String
	 * 		   La cadena separada por |
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
	 * Se encarga de generar todo el html del detalle de una nota mediante los siguientes pasos
	 * 1.- Se conecta hacia una plantilla prerender en el portal
	 * 2.- Se hace un replace de los elementos par valor que se especificaron en la plantilla prerender por ejemplo :$WCM_TITULO$
	 * 3.- Se escribe el html fisicamente en el WebServer
	 * @param  ParametrosDTO
     *         Objeto con la información necesaria para generar el html del detalle de la nota
     * @param  ContentDTO
     *         Objeto con toda la información de la nota
	 * @return String
	 * 		   Se regresa el html de la nota en formato AMP
	 * @author jesus
	 * */	
		public static String createPlantillaAMP(ParametrosDTO parametrosDTO, ContentDTO contentDTO) 
		{
			LOG.debug("*** Inicia createPlantillaAMP");
			
			boolean success = false;
			String HTML="";
			try {					
					ReadHTMLWebServer readHTMLWebServer=new ReadHTMLWebServer();
					LOG.info("Plantilla AMP: "+parametrosDTO.getURL_WEBSERVER_AMP());
					HTML = readHTMLWebServer.getResourceWebServer(parametrosDTO.getURL_WEBSERVER_AMP());
					HTML = reemplazaPlantillaAMP(HTML, contentDTO, parametrosDTO);
					String rutaHTML = getRutaContenido(contentDTO, parametrosDTO)+"/amp.html";
					LOG.info("Ruta HTML AMP: "+parametrosDTO.getPathFiles()+rutaHTML);
					success = writeHTML(parametrosDTO.getPathFiles()+rutaHTML, HTML);
					LOG.info("Genero HTML Local AMP: "+success);
			} catch(Exception e) {
				LOG.error("Error al obtener HTML de Plantilla: ", e);
				return "";
			}
			return HTML;
		}
		/**
		 * Se lleva a cabo un encode para los caracteres especiales
		 * @param  String
	     *         Cadena a ser encodeada
		 * @return String
		 * 		   Se regresa la cadena encodeada
		 * @author jesus
		 * */	
		private static String htmlEncode(final String string) {
			  final StringBuffer stringBuffer = new StringBuffer();
			  for (int i = 0; i < string.length(); i++) {
			    final Character character = string.charAt(i);
			    if (CharUtils.isAscii(character)) {
			      // Encode common HTML equivalent characters
			      stringBuffer.append(
			    		  org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(character.toString()));
			    } else {
			      // Why isn't this done in escapeHtml4()?
			      stringBuffer.append(
			          String.format("&#x%x;",
			              Character.codePointAt(string, i)));
			    }
			  }
			  return stringBuffer.toString();
			}
		
		
		/**
		 * Se realiza el reemplazo de de los campos fijos que se tenian en el html y se cambian
		 * por la información real del Contenido
		 * por ejemplo $WCM_TITULO_COMENTARIO$
		 * @param  String
	     *         Es la cadena de HTML
	     * @param  ContentDTO
	     * 		   Objeto que trae la informaición del contenido 
	     * @param  ParametrosDTO
	     * 		   Objeto que trae información adiconal, que es de suma importancia para generar el HTML     
		 * @return String
		 * 		   Se regresa el HTML con los campos reemplazados
		 * @author jesus
		 * */	
		private static String reemplazaPlantillaAMP(String HTML, ContentDTO contentDTO, ParametrosDTO parametrosDTO){
			
			try {
				LlamadasWSDAO llamadasWSDAO=new LlamadasWSDAO();
				List<ContentDTO> listRelacionadas=llamadasWSDAO.getNotasMagazine("magazine-home-2",contentDTO.getFcIdContenido(), parametrosDTO);
				StringBuffer relacionadas=new StringBuffer();
				if(listRelacionadas!=null && listRelacionadas.size()>0){
					for (ContentDTO relacionada : listRelacionadas) {
						relacionadas.append("<a class=\"card\" href=\""+relacionada.getFcUrl()+"\">\n");
						relacionadas.append("	<amp-img width=\"100\" height=\"70\" src=\""+relacionada.getFcImgPrincipal()+"\"></amp-img>\n");
						relacionadas.append("	<div>\n");
						relacionadas.append("	<span>"+StringEscapeUtils.escapeHtml(relacionada.getFcTitulo())+"</span>\n");
						relacionadas.append("	<small>"+relacionada.getFcIdCategoria()+"</small>\n");
						relacionadas.append("	</div>\n");
						relacionadas.append("</a>\n");
					}
					HTML = HTML.replace("$WCM_LIST_RELACIONADAS$",relacionadas.toString().trim());
				}
			} catch (Exception e) {
				LOG.error("Error al sustituir relacionadas");
				HTML = HTML.replace("$WCM_LIST_RELACIONADAS$","");
			}
			
			try {		
				HTML = HTML.replace("$WCM_NAVEGACION_COMSCORE$", contentDTO.getFcTipoSeccion() + "." + contentDTO.getFcSeccion()+"."+ contentDTO.getFcIdCategoria()+ "." + parametrosDTO.getPathDetalle() + "." + contentDTO.getFcNombre());
			} catch (Exception e) {
				LOG.error("Error al sustituir navegacion  comscore");
			}
			
			try {
				ReadHTMLWebServer readHTMLWebServer=new ReadHTMLWebServer();
				HTML = HTML.replace("$WCM_STYLES$",readHTMLWebServer.getResourceWebServer(parametrosDTO.getURL_WEBSERVER_CSS_AMP()).trim());
			} catch(Exception e) {
				HTML = HTML.replace("$WCM_STYLES$", "");
				LOG.error("Error al remplazar $WCM_STYLES$");
			}
			
			try {
				HTML = HTML.replace("$WCM_TITLE_CONTENIDO$",StringEscapeUtils.escapeHtml(contentDTO.getFcTitulo().trim()));
			} catch(Exception e) {
				HTML = HTML.replace("$WCM_TITLE_CONTENIDO$", "");
				LOG.error("Error al remplazar $WCM_TITLE_CONTENIDO$");
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
				String fuente = contentDTO.getFcFuente() == null? "": contentDTO.getFcFuente();
				HTML = HTML.replace("$WCM_FUENTE$", StringEscapeUtils.escapeHtml(fuente));
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_FUENTE$", "");
				LOG.error("Error al remplazar $WCM_FUENTE$");
			}
			
			//Video o Imagen principal			
			try {	
				
				HTML = HTML.replace("$WCM_MEDIA_CONTENT$", getMediaContentAMP(contentDTO));		
			} catch(Exception e) {
				HTML = HTML.replace("$WCM_MEDIA_CONTENT$", "");
				LOG.error("Error al remplazar $WCM_MEDIA_CONTENT$");
			}
			
			//Remplaza contenido
			try {		
				
				String aux_rtf_contenido = cambiaCaracteres(getEmbedPostAMP(contentDTO.getClRtfContenido()));
				aux_rtf_contenido = aux_rtf_contenido.replace("<iframe", "<amp-iframe resizable");
				aux_rtf_contenido = aux_rtf_contenido.replace("</iframe>", "</amp-iframe>");				
				HTML = HTML.replace("$WCM_RTF_CONTENIDO$", aux_rtf_contenido);
			} catch (Exception e) {
				HTML = HTML.replace("$WCM_RTF_CONTENIDO$", "");
				LOG.error("Error al remplazar $WCM_RTF_CONTENIDO$");
			}	
					
			//Remplazamos la galeria
			try {
				HTML = HTML.replace("[=GALERIA=]",getGaleriaAMP(contentDTO));
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
				HTML = HTML.replace("$URL_WHATSAPP$", "https://www.unotv.com/"+contentDTO.getFcTipoSeccion() + "/" + contentDTO.getFcSeccion()+"/"+ contentDTO.getFcIdCategoria()+"/"+ parametrosDTO.getPathDetalle() + "/" +contentDTO.getFcNombre()+"/");
			} catch (Exception e) {
				HTML = HTML.replace("$URL_WHATSAPP$", "");
				LOG.error("Error al remplazar $URL_WHATSAPP$");
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
			return HTML;
		}
		/**
		 * Se realiza a cabo la lógica para poner el código adecuado de las redes sociales
		 * que va embebido en el Rich Text Format de la nota
		 * @param  String
	     *         RTFContenido de la nota
		 * @return String
		 * 		   Se regresa una cadena con el RTF adecuado para las redes sociales
		 * @author jesus
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
		/**
		 * Se lleva a cabo el reemplazo del Media Content de la nota,
		 * puede ser solo reemplazo de la imagen principal o del vide.
		 * @param  ContentDTO
	     *         Instancia con la información necesaria para reemplazar
		 * @return String
		 * 		   Se devuelve una cadena con el Media Content
		 * @author jesus
		 * */	
		private static String getMediaContentAMP(ContentDTO dto){
			String media="";
			if(!dto.getFcIdVideoOoyala().trim().equals("") || !dto.getFcIdVideoYouTube().trim().equals("") || !dto.getFcIdPlayerOoyala().trim().equals("")){
				media=getVideoAMP(dto);
			}else{
				media=getImagenAMP(dto);
			}
			return media;
		}
		
		/**
		 * Se lleva a cabo el reemplazo del Media Content de la nota,
		 * de tipo video, se valida si es youtube u ooyala
		 * @param  ContentDTO
	     *         Instancia con la información necesaria para reemplazar
		 * @return String
		 * 		   Se devuelve una cadena con el Media Content de tipo Video
		 * @author jesus
		 * */	
		private static String getVideoAMP(ContentDTO dto) {
			
			StringBuffer mediaContent = new StringBuffer();
			String IdVideoYouTube = dto.getFcIdVideoYouTube() == null? "":dto.getFcIdVideoYouTube().trim();  
			String IdVideoOoyala = dto.getFcIdVideoOoyala() == null? "" : dto.getFcIdVideoOoyala().trim();
			String IdPlayerVideoOoyala = dto.getFcIdPlayerOoyala() == null? "" : dto.getFcIdPlayerOoyala().trim();
			
			if(!IdVideoYouTube.trim().equals("")){
				mediaContent.append("<amp-iframe class=\"video\" width=\"16\" height=\"9\" layout=\"responsive\" sandbox=\"allow-scripts allow-same-origin\" src=\"https://www.youtube.com/embed/"+IdVideoYouTube+"\"></amp-iframe>");
			}else if(!IdVideoOoyala.trim().equals("") && !IdPlayerVideoOoyala.trim().equals("")){
				mediaContent.append("<amp-iframe class=\"video\" width=\"16\" height=\"9\" layout=\"responsive\" sandbox=\"allow-scripts allow-same-origin\" src=\"https://player.ooyala.com/iframe.html?ec="+IdVideoOoyala+"&amp;pbid="+IdPlayerVideoOoyala+"&amp;platform=html5\">\n");
				mediaContent.append("<amp-img layout=\"fill\" src=\""+dto.getFcImgPrincipal()+"\" placeholder></amp-img>\n");
				mediaContent.append("</amp-iframe>\n");
				
			}
			return mediaContent.toString();
		}
		/**
		 * Se genera el HTML de la galeria
		 * @param  ContentDTO
	     *         Instancia con la información necesaria para reemplazar
		 * @return String
		 * 		   Se devuelve el HTML dela galeria
		 * @author jesus
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
		/**
		 * Se lleva a cabo el reemplazo del Media Content de la nota,
		 * de tipo imagen
		 * @param  ContentDTO
	     *         Instancia con la información necesaria para reemplazar
		 * @return String
		 * 		   Se devuelve una cadena con el Media Content de tipo imagen
		 * @author jesus
		 * */
		private static String getImagenAMP(ContentDTO dto) {
			StringBuffer mediaImage = new StringBuffer("");
			String imgPrincipal = dto.getFcImgPrincipal() == null?"":dto.getFcImgPrincipal();
			String imgInfografia = dto.getFcImgInfografia() == null?"":dto.getFcImgInfografia();
			
			if(!imgInfografia.trim().equals("")){
				mediaImage.append("<amp-img width=\"287\" height=\"775\" layout=\"responsive\" src=\""+imgPrincipal+"\"></amp-img>");
			}else{
				mediaImage.append("<amp-img width=\"545\" height=\"360\" layout=\"responsive\" src=\""+imgPrincipal+"\"></amp-img>");
			}
			  return mediaImage.toString();
		}
		
		/**
		 * Se escribe fisicamente un archivo de tipo HTML
		 * @param  String
	     *         Path del directorio donde se va a guardar el HTML
		 * @param String
		 * 		   Código HTML
		 * @return boolean
		 * @author jesus
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
}
