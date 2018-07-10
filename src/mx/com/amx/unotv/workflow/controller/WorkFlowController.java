package mx.com.amx.unotv.workflow.controller;

import mx.com.amx.unotv.workflow.bo.ProcesoWorkflowBO;
import mx.com.amx.unotv.workflow.dto.ContentDTO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"workflow-controller"})
public class WorkFlowController {
	
	//LOG
	private static Logger LOG=Logger.getLogger(WorkFlowController.class);
	
	@Autowired
	private ProcesoWorkflowBO procesoWorkflowBO;
		
	/**
	 * Metodo que es utilizado para llamar al BO encargado de realizar la logica de 
	 * la publicacion de una nota en el portal de UNOTV
	 * @param ContentDTO
	 * @return String
	 * */
	@RequestMapping(value={"publicarNota"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public String publicarNota(@RequestBody ContentDTO contentDTO)
	{
		LOG.info("==================  INICIO Publica Nota =====================");
		LOG.debug("*** publicarNota [Controller] ***");
		LOG.info("contentDTO: "+contentDTO);
		String resultado="";
		try{	
			resultado = procesoWorkflowBO.publicarNota(contentDTO);
			LOG.info("================== FIN Publica Nota=====================");
		}
		catch (Exception e){
			LOG.error(" Error WorkFlowController [publicarNota]:", e);
		}
		return resultado;
	}
	
	
	/**
	 * 
	 * Metodo que es utilizado para llamar al BO encargado de realizar la lógica del
	 * proceso de revisión de una nota en el portal de UNOTV
	 * @param ContentDTO
	 * @return String
	 * */
	@RequestMapping(value={"revisarNota"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public String revisarNota(@RequestBody ContentDTO contentDTO){
				
		String resultado="";
		try{
			LOG.info("========== INICIO Revisa Nota ============");
			LOG.debug("*** revisarNota [Controller] ***");
			resultado = procesoWorkflowBO.revisarNota(contentDTO);
			LOG.info("========== FIN Revisa Nota ============");

		}
		catch (Exception e){
			LOG.error(" Error WorkFlowController [revisarNota]:"+e.getMessage());
		}
		return resultado;
	}
	
	/**
	 * Metodo que es utilizado para llamar al BO encargado de realizar la lógica del
	 * proceso de caducación de de una nota en el portal de UNOTV
	 * @param ContentDTO
	 * @return Boolean
	 * */
	@RequestMapping(value={"caducarNota"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean caducarNota(@RequestBody ContentDTO contentDTO){
				
		Boolean resultado=false;
		try{		
			LOG.info("========== INICIO CaducarNota Nota ============");
			LOG.debug("*** caducarNota [Controller] ***");
			resultado = procesoWorkflowBO.caducarNota(contentDTO);
			LOG.info("graylog-nota-caducada");
			LOG.info("========== CADUCA CaducarNota Nota ============");
		}
		catch (Exception e){
			LOG.error(" Error WorkFlowController [caducarNota]:"+e.getMessage());
		}
		return resultado;
	}
}//FIN CLASE
