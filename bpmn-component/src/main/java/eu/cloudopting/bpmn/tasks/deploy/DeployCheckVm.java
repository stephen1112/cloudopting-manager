package eu.cloudopting.bpmn.tasks.deploy;

import java.util.concurrent.TimeUnit;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.cloudopting.cloud.CloudService;
import eu.cloudopting.tosca.ToscaService;

@Service
public class DeployCheckVm implements JavaDelegate {
	private final Logger log = LoggerFactory.getLogger(DeployCheckVm.class);
	@Autowired
	ToscaService toscaService;
	
	@Autowired
	CloudService cloudService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		log.debug("in DeployCheckVm");
		String customizationId = (String) execution.getVariable("customizationId");
		String cloudtask = (String) execution.getVariable("cloudtask");
		String cloudId = (String) execution.getVariable("cloudId");
		Long cloudAccountId = (Long) execution.getVariable("cloudAccountId");
		TimeUnit.SECONDS.sleep(4);
//		toscaService.getNodeType(customizationId,"");
		boolean check = cloudService.checkVM(cloudAccountId, cloudtask);
		execution.setVariable("vmInstalled", check);
		
	}

}
