package it.unimore.dipi.iot.consumer.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * A simple CoAP Synchronous Client implemented using Californium Java Library
 * The simple client send a POST request to a target CoAP Resource with some custom request parameters
 * and Payload (if necessary)
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-demo-smartobject
 * @created 20/10/2020 - 09:19
 */
public class CoapPostClientProcess {

	private final static Logger logger = LoggerFactory.getLogger(CoapPostClientProcess.class);

	private static final String COAP_ENDPOINT = "coap://127.0.0.1:5783/switch";

	public static void main(String[] args) {
		
		//Initialize coapClient
		CoapClient coapClient = new CoapClient(COAP_ENDPOINT);

		//Request Class is a generic CoAP message: in this case we want a GET.
		//"Message ID", "Token" and other header's fields can be set 
		Request request = new Request(Code.POST);

		//Set Request as Confirmable
		request.setConfirmable(true);

		logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(request));

		//Synchronously send the POST request (blocking call)
		CoapResponse coapResp = null;

		try {

			coapResp = coapClient.advanced(request);

			//Pretty print for the received response
			logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

			//The "CoapResponse" message contains the response.
			String text = coapResp.getResponseText();
			logger.info("Payload: {}", text);
			logger.info("Message ID: " + coapResp.advanced().getMID());
			logger.info("Token: " + coapResp.advanced().getTokenString());

		} catch (ConnectorException | IOException e) {
			e.printStackTrace();
		}
	}
}