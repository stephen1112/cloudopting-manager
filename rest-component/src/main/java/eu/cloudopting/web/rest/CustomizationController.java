package eu.cloudopting.web.rest;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.cloudopting.domain.User;
import eu.cloudopting.service.UserService;
import eu.cloudopting.tosca.ToscaService;

@RestController
@RequestMapping("/api")
public class CustomizationController {
	private final Logger log = LoggerFactory.getLogger(CustomizationController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ToscaService toscaService;
	
	@RequestMapping(value = "/application/{idApp}/getSizings",
            method = RequestMethod.GET)
	public void getSizing(@PathVariable("appId") final Long id){
		
	}
	
	@RequestMapping(value = "/application/{idApp}/getCustomizationForm",
            method = RequestMethod.GET)
	@ResponseBody
	public String getCustomizationForm(@PathVariable("idApp") final Long idApp){
		log.debug("in getCustomizationForm");
		log.debug(idApp.toString());
//		JSONObject jret = new JSONObject("{\"type\": \"object\",\"title\": \"Compute\",\"properties\": {\"node_id\":  {\"title\": \"Node ID\",\"type\": \"string\"},\"node_label\":  {\"title\": \"Node Label\",\"type\": \"string\",\"description\": \"Email will be used for evil.\"},\"memory\":  {\"title\": \"Memory\",\"type\": \"string\",\"enum\": [\"512\",\"1024\",\"2048\"]},\"cpu\": {\"title\": \"CPU\",\"type\": \"integer\",\"maxLength\": 20,\"validationMessage\": \"Dont be greedy!\"}},\"required\": [\"node_id\",\"node_label\",\"memory\", \"cpu\"]}");
		JSONObject jret = toscaService.getCustomizationFormData(idApp);
		return jret.toString();
  		
	}

	@RequestMapping(value = "/application/sendCustomizationForm",
            method = RequestMethod.POST)
	public void postCustomizationForm(@RequestParam(value = "formData") String formData,HttpServletRequest request){
		log.debug("in postCustomizationForm");
		log.debug(formData);
		User user = userService.loadUserByLogin(request.getUserPrincipal().getName());
		
		
	}
}