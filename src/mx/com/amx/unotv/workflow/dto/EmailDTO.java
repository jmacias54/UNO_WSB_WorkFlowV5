package mx.com.amx.unotv.workflow.dto;

public class EmailDTO {

	private String subject;
	private String bodyMSG;
	private String[] recipients;
	private String[] recipientsCC;
	private String smtpsender;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBodyMSG() {
		return bodyMSG;
	}
	public void setBodyMSG(String bodyMSG) {
		this.bodyMSG = bodyMSG;
	}
	public String[] getRecipients() {
		return recipients;
	}
	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}
	public String[] getRecipientsCC() {
		return recipientsCC;
	}
	public void setRecipientsCC(String[] recipientsCC) {
		this.recipientsCC = recipientsCC;
	}
	public String getSmtpsender() {
		return smtpsender;
	}
	public void setSmtpsender(String smtpsender) {
		this.smtpsender = smtpsender;
	}	
}
