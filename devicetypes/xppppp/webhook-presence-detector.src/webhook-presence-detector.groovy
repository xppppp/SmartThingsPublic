/**
 *  Webhook Presence Detector
 *
 *  Copyright 2017 P Park
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Webhook Presence Detector", namespace: "xppppp", author: "P Park") {
		capability "Presence Sensor"
		capability "Sensor"
	}
	simulator {
		// TODO: define status and reply messages here
	}
    preferences {
		section {
        	input("targetHost", "text", title: "Target Host", description: "Target host name [:port optional]", displayDuringSetup: true, required: true)
            input("targetPath", "text", title: "Target URI Path", description: "Target path", displayDuringSetup: true, required: true)
			input("id", "text", title: "Device ID", description: "Device Identifier", displayDuringSetup: true, required: true)
		}
	}
	tiles {
        standardTile("presence", "device.presence", width: 2, height: 2, canChangeBackground: true) {
            state "present", labelIcon:"st.presence.tile.present", backgroundColor:"#00a0dc"
            state "not present", labelIcon:"st.presence.tile.not-present", backgroundColor:"#ffffff"
        }
        main "presence"
        details(["presence"])
	}
}
def parse(String description) {
	log.debug "Parsing '${description}'"
}
def presenceResponse(physicalgraph.device.HubResponse hubResponse) {
	if (hubResponse.status == 200) {
    	// log.debug "hubRequest success"
    	updateState(hubResponse.json)
    } else {
    	log.debug "hubRequest failed with " + hubResponse.status
    }
}
def installed() {
	state.xcurrentState = "unknown"
	configure()
}
def updated() {
	configure()
}
def configure() {
	checkPresence()
    runEvery1Minute(checkPresence)
}
def updateState(data){
	try {
		// log.debug "Recieved \"$data\" from server"
        if (data.success) {
        	setPresence(data.present)
        } else {
            log.debug "Update error"
        }
    } catch (e) {
        log.error "Error setting presence: $e"
   	}
}
def checkPresence(){
	// log.debug "Checking presence"
	def hubAction = new physicalgraph.device.HubAction(
    	[
			path: "/${targetPath}/${id}",
        	method: "GET",
        	HOST: "${targetHost}",
        	headers: [
	            "Host":"${targetHost}",
    	        "Accept":"application/json"
        	]
        ],
        null,
        [
        	callback: presenceResponse
        ]
	);
	sendHubCommand(hubAction)
}

def setPresence(boolean present){
	// log.debug "setPresence(" + present + ")"
    def lastState = state.xcurrentState == "here"
    if ((present != lastState) || (state.xcurrentState == "unknown")) {
		if (present) {
	   		sendEvent(displayed: true,  isStateChange: true, name: "presence", value: "present", descriptionText: "$device.displayName is in the house")
            state.xcurrentState = "here"
        } else {
	    	sendEvent(displayed: true,  isStateChange: true, name: "presence", value: "not present", descriptionText: "$device.displayName has left the building")
            state.xcurrentState = "absent"
        }
		// log.debug "Presence set"
	}
}