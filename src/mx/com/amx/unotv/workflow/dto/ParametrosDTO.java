package mx.com.amx.unotv.workflow.dto;

import java.io.Serializable;

public class ParametrosDTO implements Serializable {
	
private static final long serialVersionUID = 1L;
		
	private String carpetaResources;
	private String pathFiles;
	private String nameHTML;
	private String basePaginaPlantilla;
	private String baseTheme;
	private String baseURL;
	private String basePagesPortal;
	private String pathDetalle;
	private String dominio;
	private String ambiente;
	private String metaVideo;
	private String metaVideoSecureUrl;
	private String pathFilesTest;
	private String pathRemoteTest;
	private String baseURLTest;
	private String catalogoParametros;
	private String url_adserver;
	
	private String URL_WS_DATOS;
	private String URL_WS_VIDEO;
	private String URL_WS_PARAMETROS;
	private String URL_WS_AMP;
	private String URL_WS_FB;
	private String URL_WEBSERVER_AMP;
	private String URL_WEBSERVER_CSS_AMP;
		
	private String htmlNotasRelacionadas;
	private String itemNotasRelacionadas;
	
	private String url_dominio_dat;
	private String url_dominio_app;
	
	private String url_wsd_WorkFlow;
	private String url_wsb_WorkFlowUtils;
	private String url_wsb_Utils;
	private String url_wsb_BackOffice;
	private String url_wsb_FB;

	private String met_wsb_FB_insertUpdateArticle2;
	private String met_wsb_FB_deleteArticle;
	private String met_wsb_BackOffice_sendPushAMP;
	private String met_wsb_WorkFlowUtils_getInfoVideo;
	private String met_wsb_WorkFlowUtils_getInfoAudio;
	
	private String met_wsb_Utils_getParameter;
	
	private String met_wsd_WorkFlow_existeNotaRegistrada;
	private String met_wsd_WorkFlow_getIdNotaByName;
	private String met_wsd_WorkFlow_insertNotaTag;
	private String met_wsd_WorkFlow_deleteNotaTag;
	private String met_wsd_WorkFlow_deleteNotaBD;
	private String met_wsd_WorkFlow_deleteNotaHistoricoBD;
	
	private String met_wsd_WorkFlow_getRelacionadasbyIdCategoria;		
	private String met_wsd_WorkFlow_insertNotaBD;
	private String met_wsd_WorkFlow_insertNotaHistoricoBD;
	private String met_wsd_WorkFlow_updateNotaBD;
	private String met_wsd_WorkFlow_updateNotaHistoricoBD;
	private String met_wsd_WorkFlow_getExtraInfoContent;	
	private String correo_error_para;
	private String correo_error_asunto;
	private String correo_error_cuerpo;
	private String correo_error_smtpsender;
	
	private String js_amp_ooyala_player;
	private String js_amp_youtube;
	private String js_amp_instagram;
	private String js_amp_facebook;
	private String js_amp_twitter;
	
	/**
	 * @return the carpetaResources
	 */
	public String getCarpetaResources() {
		return carpetaResources;
	}
	/**
	 * @param carpetaResources the carpetaResources to set
	 */
	public void setCarpetaResources(String carpetaResources) {
		this.carpetaResources = carpetaResources;
	}
	/**
	 * @return the pathFiles
	 */
	public String getPathFiles() {
		return pathFiles;
	}
	/**
	 * @param pathFiles the pathFiles to set
	 */
	public void setPathFiles(String pathFiles) {
		this.pathFiles = pathFiles;
	}
	/**
	 * @return the nameHTML
	 */
	public String getNameHTML() {
		return nameHTML;
	}
	/**
	 * @param nameHTML the nameHTML to set
	 */
	public void setNameHTML(String nameHTML) {
		this.nameHTML = nameHTML;
	}
	/**
	 * @return the basePaginaPlantilla
	 */
	public String getBasePaginaPlantilla() {
		return basePaginaPlantilla;
	}
	/**
	 * @param basePaginaPlantilla the basePaginaPlantilla to set
	 */
	public void setBasePaginaPlantilla(String basePaginaPlantilla) {
		this.basePaginaPlantilla = basePaginaPlantilla;
	}
	/**
	 * @return the baseTheme
	 */
	public String getBaseTheme() {
		return baseTheme;
	}
	/**
	 * @param baseTheme the baseTheme to set
	 */
	public void setBaseTheme(String baseTheme) {
		this.baseTheme = baseTheme;
	}
	/**
	 * @return the baseURL
	 */
	public String getBaseURL() {
		return baseURL;
	}
	/**
	 * @param baseURL the baseURL to set
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	/**
	 * @return the basePagesPortal
	 */
	public String getBasePagesPortal() {
		return basePagesPortal;
	}
	/**
	 * @param basePagesPortal the basePagesPortal to set
	 */
	public void setBasePagesPortal(String basePagesPortal) {
		this.basePagesPortal = basePagesPortal;
	}
	/**
	 * @return the pathDetalle
	 */
	public String getPathDetalle() {
		return pathDetalle;
	}
	/**
	 * @param pathDetalle the pathDetalle to set
	 */
	public void setPathDetalle(String pathDetalle) {
		this.pathDetalle = pathDetalle;
	}
	/**
	 * @return the dominio
	 */
	public String getDominio() {
		return dominio;
	}
	/**
	 * @param dominio the dominio to set
	 */
	public void setDominio(String dominio) {
		this.dominio = dominio;
	}
	/**
	 * @return the ambiente
	 */
	public String getAmbiente() {
		return ambiente;
	}
	/**
	 * @param ambiente the ambiente to set
	 */
	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}
	/**
	 * @return the metaVideo
	 */
	public String getMetaVideo() {
		return metaVideo;
	}
	/**
	 * @param metaVideo the metaVideo to set
	 */
	public void setMetaVideo(String metaVideo) {
		this.metaVideo = metaVideo;
	}
	/**
	 * @return the metaVideoSecureUrl
	 */
	public String getMetaVideoSecureUrl() {
		return metaVideoSecureUrl;
	}
	/**
	 * @param metaVideoSecureUrl the metaVideoSecureUrl to set
	 */
	public void setMetaVideoSecureUrl(String metaVideoSecureUrl) {
		this.metaVideoSecureUrl = metaVideoSecureUrl;
	}
	/**
	 * @return the pathFilesTest
	 */
	public String getPathFilesTest() {
		return pathFilesTest;
	}
	/**
	 * @param pathFilesTest the pathFilesTest to set
	 */
	public void setPathFilesTest(String pathFilesTest) {
		this.pathFilesTest = pathFilesTest;
	}
	/**
	 * @return the pathRemoteTest
	 */
	public String getPathRemoteTest() {
		return pathRemoteTest;
	}
	/**
	 * @param pathRemoteTest the pathRemoteTest to set
	 */
	public void setPathRemoteTest(String pathRemoteTest) {
		this.pathRemoteTest = pathRemoteTest;
	}
	/**
	 * @return the baseURLTest
	 */
	public String getBaseURLTest() {
		return baseURLTest;
	}
	/**
	 * @param baseURLTest the baseURLTest to set
	 */
	public void setBaseURLTest(String baseURLTest) {
		this.baseURLTest = baseURLTest;
	}
	/**
	 * @return the uRL_WS_DATOS
	 */
	public String getURL_WS_DATOS() {
		return URL_WS_DATOS;
	}
	/**
	 * @param uRL_WS_DATOS the uRL_WS_DATOS to set
	 */
	public void setURL_WS_DATOS(String uRL_WS_DATOS) {
		URL_WS_DATOS = uRL_WS_DATOS;
	}
	/**
	 * @return the uRL_WS_VIDEO
	 */
	public String getURL_WS_VIDEO() {
		return URL_WS_VIDEO;
	}
	/**
	 * @param uRL_WS_VIDEO the uRL_WS_VIDEO to set
	 */
	public void setURL_WS_VIDEO(String uRL_WS_VIDEO) {
		URL_WS_VIDEO = uRL_WS_VIDEO;
	}
	/**
	 * @return the uRL_WS_PARAMETROS
	 */
	public String getURL_WS_PARAMETROS() {
		return URL_WS_PARAMETROS;
	}
	/**
	 * @param uRL_WS_PARAMETROS the uRL_WS_PARAMETROS to set
	 */
	public void setURL_WS_PARAMETROS(String uRL_WS_PARAMETROS) {
		URL_WS_PARAMETROS = uRL_WS_PARAMETROS;
	}
	/**
	 * @return the uRL_WS_AMP
	 */
	public String getURL_WS_AMP() {
		return URL_WS_AMP;
	}
	/**
	 * @param uRL_WS_AMP the uRL_WS_AMP to set
	 */
	public void setURL_WS_AMP(String uRL_WS_AMP) {
		URL_WS_AMP = uRL_WS_AMP;
	}
	/**
	 * @return the uRL_WS_FB
	 */
	public String getURL_WS_FB() {
		return URL_WS_FB;
	}
	/**
	 * @param uRL_WS_FB the uRL_WS_FB to set
	 */
	public void setURL_WS_FB(String uRL_WS_FB) {
		URL_WS_FB = uRL_WS_FB;
	}
	/**
	 * @return the uRL_WEBSERVER_AMP
	 */
	public String getURL_WEBSERVER_AMP() {
		return URL_WEBSERVER_AMP;
	}
	/**
	 * @param uRL_WEBSERVER_AMP the uRL_WEBSERVER_AMP to set
	 */
	public void setURL_WEBSERVER_AMP(String uRL_WEBSERVER_AMP) {
		URL_WEBSERVER_AMP = uRL_WEBSERVER_AMP;
	}
	/**
	 * @return the uRL_WEBSERVER_CSS_AMP
	 */
	public String getURL_WEBSERVER_CSS_AMP() {
		return URL_WEBSERVER_CSS_AMP;
	}
	/**
	 * @param uRL_WEBSERVER_CSS_AMP the uRL_WEBSERVER_CSS_AMP to set
	 */
	public void setURL_WEBSERVER_CSS_AMP(String uRL_WEBSERVER_CSS_AMP) {
		URL_WEBSERVER_CSS_AMP = uRL_WEBSERVER_CSS_AMP;
	}
	/**
	 * @return the catalogoParametros
	 */
	public String getCatalogoParametros() {
		return catalogoParametros;
	}
	/**
	 * @param catalogoParametros the catalogoParametros to set
	 */
	public void setCatalogoParametros(String catalogoParametros) {
		this.catalogoParametros = catalogoParametros;
	}
	public String getUrl_wsb_WorkFlowUtils() {
		return url_wsb_WorkFlowUtils;
	}
	public void setUrl_wsb_WorkFlowUtils(String url_wsb_WorkFlowUtils) {
		this.url_wsb_WorkFlowUtils = url_wsb_WorkFlowUtils;
	}
	public String getUrl_wsb_Utils() {
		return url_wsb_Utils;
	}
	public void setUrl_wsb_Utils(String url_wsb_Utils) {
		this.url_wsb_Utils = url_wsb_Utils;
	}
	public String getUrl_wsb_BackOffice() {
		return url_wsb_BackOffice;
	}
	public void setUrl_wsb_BackOffice(String url_wsb_BackOffice) {
		this.url_wsb_BackOffice = url_wsb_BackOffice;
	}
	public String getUrl_wsb_FB() {
		return url_wsb_FB;
	}
	public void setUrl_wsb_FB(String url_wsb_FB) {
		this.url_wsb_FB = url_wsb_FB;
	}
	public String getMet_wsb_FB_insertUpdateArticle2() {
		return met_wsb_FB_insertUpdateArticle2;
	}
	public void setMet_wsb_FB_insertUpdateArticle2(
			String met_wsb_FB_insertUpdateArticle2) {
		this.met_wsb_FB_insertUpdateArticle2 = met_wsb_FB_insertUpdateArticle2;
	}
	public String getMet_wsb_FB_deleteArticle() {
		return met_wsb_FB_deleteArticle;
	}
	public void setMet_wsb_FB_deleteArticle(String met_wsb_FB_deleteArticle) {
		this.met_wsb_FB_deleteArticle = met_wsb_FB_deleteArticle;
	}
	public String getMet_wsb_BackOffice_sendPushAMP() {
		return met_wsb_BackOffice_sendPushAMP;
	}
	public void setMet_wsb_BackOffice_sendPushAMP(
			String met_wsb_BackOffice_sendPushAMP) {
		this.met_wsb_BackOffice_sendPushAMP = met_wsb_BackOffice_sendPushAMP;
	}
	public String getMet_wsb_WorkFlowUtils_getInfoVideo() {
		return met_wsb_WorkFlowUtils_getInfoVideo;
	}
	public void setMet_wsb_WorkFlowUtils_getInfoVideo(
			String met_wsb_WorkFlowUtils_getInfoVideo) {
		this.met_wsb_WorkFlowUtils_getInfoVideo = met_wsb_WorkFlowUtils_getInfoVideo;
	}
	public String getUrl_wsd_WorkFlow() {
		return url_wsd_WorkFlow;
	}
	public void setUrl_wsd_WorkFlow(String url_wsd_WorkFlow) {
		this.url_wsd_WorkFlow = url_wsd_WorkFlow;
	}
	public String getMet_wsd_WorkFlow_existeNotaRegistrada() {
		return met_wsd_WorkFlow_existeNotaRegistrada;
	}
	public void setMet_wsd_WorkFlow_existeNotaRegistrada(
			String met_wsd_WorkFlow_existeNotaRegistrada) {
		this.met_wsd_WorkFlow_existeNotaRegistrada = met_wsd_WorkFlow_existeNotaRegistrada;
	}
	public String getMet_wsd_WorkFlow_getIdNotaByName() {
		return met_wsd_WorkFlow_getIdNotaByName;
	}
	public void setMet_wsd_WorkFlow_getIdNotaByName(
			String met_wsd_WorkFlow_getIdNotaByName) {
		this.met_wsd_WorkFlow_getIdNotaByName = met_wsd_WorkFlow_getIdNotaByName;
	}
	public String getMet_wsd_WorkFlow_insertNotaTag() {
		return met_wsd_WorkFlow_insertNotaTag;
	}
	public void setMet_wsd_WorkFlow_insertNotaTag(
			String met_wsd_WorkFlow_insertNotaTag) {
		this.met_wsd_WorkFlow_insertNotaTag = met_wsd_WorkFlow_insertNotaTag;
	}
	public String getMet_wsd_WorkFlow_deleteNotaTag() {
		return met_wsd_WorkFlow_deleteNotaTag;
	}
	public void setMet_wsd_WorkFlow_deleteNotaTag(
			String met_wsd_WorkFlow_deleteNotaTag) {
		this.met_wsd_WorkFlow_deleteNotaTag = met_wsd_WorkFlow_deleteNotaTag;
	}
	public String getMet_wsd_WorkFlow_deleteNotaBD() {
		return met_wsd_WorkFlow_deleteNotaBD;
	}
	public void setMet_wsd_WorkFlow_deleteNotaBD(
			String met_wsd_WorkFlow_deleteNotaBD) {
		this.met_wsd_WorkFlow_deleteNotaBD = met_wsd_WorkFlow_deleteNotaBD;
	}
	public String getMet_wsd_WorkFlow_deleteNotaHistoricoBD() {
		return met_wsd_WorkFlow_deleteNotaHistoricoBD;
	}
	public void setMet_wsd_WorkFlow_deleteNotaHistoricoBD(
			String met_wsd_WorkFlow_deleteNotaHistoricoBD) {
		this.met_wsd_WorkFlow_deleteNotaHistoricoBD = met_wsd_WorkFlow_deleteNotaHistoricoBD;
	}
	public String getMet_wsd_WorkFlow_getRelacionadasbyIdCategoria() {
		return met_wsd_WorkFlow_getRelacionadasbyIdCategoria;
	}
	public void setMet_wsd_WorkFlow_getRelacionadasbyIdCategoria(
			String met_wsd_WorkFlow_getRelacionadasbyIdCategoria) {
		this.met_wsd_WorkFlow_getRelacionadasbyIdCategoria = met_wsd_WorkFlow_getRelacionadasbyIdCategoria;
	}
	public String getMet_wsd_WorkFlow_insertNotaBD() {
		return met_wsd_WorkFlow_insertNotaBD;
	}
	public void setMet_wsd_WorkFlow_insertNotaBD(
			String met_wsd_WorkFlow_insertNotaBD) {
		this.met_wsd_WorkFlow_insertNotaBD = met_wsd_WorkFlow_insertNotaBD;
	}
	public String getMet_wsd_WorkFlow_insertNotaHistoricoBD() {
		return met_wsd_WorkFlow_insertNotaHistoricoBD;
	}
	public void setMet_wsd_WorkFlow_insertNotaHistoricoBD(
			String met_wsd_WorkFlow_insertNotaHistoricoBD) {
		this.met_wsd_WorkFlow_insertNotaHistoricoBD = met_wsd_WorkFlow_insertNotaHistoricoBD;
	}
	public String getMet_wsd_WorkFlow_updateNotaBD() {
		return met_wsd_WorkFlow_updateNotaBD;
	}
	public void setMet_wsd_WorkFlow_updateNotaBD(
			String met_wsd_WorkFlow_updateNotaBD) {
		this.met_wsd_WorkFlow_updateNotaBD = met_wsd_WorkFlow_updateNotaBD;
	}
	public String getMet_wsd_WorkFlow_updateNotaHistoricoBD() {
		return met_wsd_WorkFlow_updateNotaHistoricoBD;
	}
	public void setMet_wsd_WorkFlow_updateNotaHistoricoBD(
			String met_wsd_WorkFlow_updateNotaHistoricoBD) {
		this.met_wsd_WorkFlow_updateNotaHistoricoBD = met_wsd_WorkFlow_updateNotaHistoricoBD;
	}
	public String getUrl_dominio_dat() {
		return url_dominio_dat;
	}
	public void setUrl_dominio_dat(String url_dominio_dat) {
		this.url_dominio_dat = url_dominio_dat;
	}
	public String getUrl_dominio_app() {
		return url_dominio_app;
	}
	public void setUrl_dominio_app(String url_dominio_app) {
		this.url_dominio_app = url_dominio_app;
	}
	public String getMet_wsd_WorkFlow_getExtraInfoContent() {
		return met_wsd_WorkFlow_getExtraInfoContent;
	}
	public void setMet_wsd_WorkFlow_getExtraInfoContent(
			String met_wsd_WorkFlow_getExtraInfoContent) {
		this.met_wsd_WorkFlow_getExtraInfoContent = met_wsd_WorkFlow_getExtraInfoContent;
	}
	public String getMet_wsb_Utils_getParameter() {
		return met_wsb_Utils_getParameter;
	}
	public void setMet_wsb_Utils_getParameter(String met_wsb_Utils_getParameter) {
		this.met_wsb_Utils_getParameter = met_wsb_Utils_getParameter;
	}
	public String getCorreo_error_para() {
		return correo_error_para;
	}
	public void setCorreo_error_para(String correo_error_para) {
		this.correo_error_para = correo_error_para;
	}
	public String getCorreo_error_asunto() {
		return correo_error_asunto;
	}
	public void setCorreo_error_asunto(String correo_error_asunto) {
		this.correo_error_asunto = correo_error_asunto;
	}
	public String getCorreo_error_cuerpo() {
		return correo_error_cuerpo;
	}
	public void setCorreo_error_cuerpo(String correo_error_cuerpo) {
		this.correo_error_cuerpo = correo_error_cuerpo;
	}
	public String getCorreo_error_smtpsender() {
		return correo_error_smtpsender;
	}
	public void setCorreo_error_smtpsender(String correo_error_smtpsender) {
		this.correo_error_smtpsender = correo_error_smtpsender;
	}
	public String getMet_wsb_WorkFlowUtils_getInfoAudio() {
		return met_wsb_WorkFlowUtils_getInfoAudio;
	}
	public void setMet_wsb_WorkFlowUtils_getInfoAudio(
			String met_wsb_WorkFlowUtils_getInfoAudio) {
		this.met_wsb_WorkFlowUtils_getInfoAudio = met_wsb_WorkFlowUtils_getInfoAudio;
	}
	public String getUrl_adserver() {
		return url_adserver;
	}
	public void setUrl_adserver(String url_adserver) {
		this.url_adserver = url_adserver;
	}
	public String getHtmlNotasRelacionadas() {
		return htmlNotasRelacionadas;
	}
	public void setHtmlNotasRelacionadas(String htmlNotasRelacionadas) {
		this.htmlNotasRelacionadas = htmlNotasRelacionadas;
	}
	public String getItemNotasRelacionadas() {
		return itemNotasRelacionadas;
	}
	public void setItemNotasRelacionadas(String itemNotasRelacionadas) {
		this.itemNotasRelacionadas = itemNotasRelacionadas;
	}
	public String getJs_amp_ooyala_player() {
		return js_amp_ooyala_player;
	}
	public void setJs_amp_ooyala_player(String js_amp_ooyala_player) {
		this.js_amp_ooyala_player = js_amp_ooyala_player;
	}
	public String getJs_amp_youtube() {
		return js_amp_youtube;
	}
	public void setJs_amp_youtube(String js_amp_youtube) {
		this.js_amp_youtube = js_amp_youtube;
	}
	public String getJs_amp_instagram() {
		return js_amp_instagram;
	}
	public void setJs_amp_instagram(String js_amp_instagram) {
		this.js_amp_instagram = js_amp_instagram;
	}
	public String getJs_amp_facebook() {
		return js_amp_facebook;
	}
	public void setJs_amp_facebook(String js_amp_facebook) {
		this.js_amp_facebook = js_amp_facebook;
	}
	public String getJs_amp_twitter() {
		return js_amp_twitter;
	}
	public void setJs_amp_twitter(String js_amp_twitter) {
		this.js_amp_twitter = js_amp_twitter;
	}
	
	
	
}
