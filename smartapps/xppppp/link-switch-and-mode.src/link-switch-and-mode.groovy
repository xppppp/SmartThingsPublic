/**
 *  Link Switch and Mode
 *
 *  To use this SmartApp, first create a virtual device using the On/Off Button Tile device type.
 *  Install this SmartApp, select your virtual On/Off Button Tile, then your lock.  
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

definition(
	name: "Link Switch and Mode",
	namespace: "xppppp",
	author: "xppppp",
	description: "Link state between a switch and a mode. Switch On <=> mode, Switch Off <=> away",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Solution/doors-locks.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Solution/doors-locks@2x.png"
)

preferences {
	section("When this switch is turned on") {
		input "switch1", "capability.switch", multiple: false, required: true
	}
	section("Set this mode") {
		input "mode1", "mode", title: "Mode?"
	}
}    

def installed()
{   
	subscribe(switch1, "switch.on", onHandler)
	subscribe(switch1, "switch.off", offHandler)
	subscribe(location, "mode", modeChangeHandler)
}

def updated()
{
	unsubscribe()
	subscribe(switch1, "switch.on", onHandler)
	subscribe(switch1, "switch.off", offHandler)
	subscribe(location, "mode", modeChangeHandler)
}

def onHandler(evt) {
setLocationMode(mode1)
}

def offHandler(evt) {
setLocationMode("Away")
}

def modeChangeHandler(evt) {
if (evt.value == mode1) {
	log.debug "Turning on switch: $switch1"
   	switch1.on()
} else {
log.debug "Turning off switch: $switch1"
switch1.off()
}
}