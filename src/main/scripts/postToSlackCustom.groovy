/**
 * © Copyright IBM Corporation 2014, 2016.
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * @author cooperc
 */
import com.urbancode.air.AirPluginTool;

import groovy.json.JsonBuilder

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.StringRequestEntity

final airTool = new AirPluginTool(args[0], args[1])

/* Here we call getStepProperties() to get a Properties object that contains the step properties
* provided by the user.
*/
final def props = airTool.getStepProperties()

// properties
final def webhook = props['webhook'];
final def slackChannel = props['channel'];
final def slackUsername = props['username'];
final def emoji = props['emoji'];
final def slackAttachment = props['attachment'];

//Create Attachment JSON object
def attachmentJson = new JsonBuilder();
try {
	attachmentJson slackAttachment
	println "DEBUG:: " attachmentJson;
	assert attachmentJson instanceof List
} catch (Exception exception) {
	println "ERROR:: Attachment value not JSON Array"
	System.exit(1)
}

attachmentsJson[0].ts = System.currentTimeMillis()/1000;

// JSON message composition
def json = new JsonBuilder();
try {
	json {
		channel slackChannel
		username slackUsername
		icon_emoji emoji
		attachments attachmentJson
	}
	println json.toPrettyString();
} catch (Exception exception) {
	println "ERROR setting path: ${e.message}"
	System.exit(1)
}

// HTTP POST to Slack
try{
	def requestEntity = new StringRequestEntity(
			json.toString(),
			"application/json",
			"UTF-8"
	);
	def http = new HttpClient();
	def post = new PostMethod(webhook);
	post.setRequestEntity(requestEntity);

	def status = http.executeMethod(post);

	if (status == 200){
		println "Success: ${status}";
		System.exit(0);;
	} else {
		println "Failure: ${status}"
		System.exit(3);
	}
} catch (Exception e) {
	println "[Error] Unable to set path: ${e.message}"
    println "[Possible Solution] Confirm the properties by running the Webhook with its associated JSON body in a REST Client."
	System.exit(2)
}