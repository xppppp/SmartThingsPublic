/**
 *  Vacation Mode
 *
 *  Copyright 2016 P Park
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
    name: "Vacation Mode Thermostat",
    namespace: "xppppp",
    author: "P Park",
    description: "Do stuff when in vacation mode",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Vacation Mode") {
		input "mode1", "mode", title: "Mode?"
	}
   	section("Controls These Thermostats") {
   		input "thermostats", "capability.thermostat", multiple: true, required: true
	}
	section("Vacation Mode Heat setting..." ) {
		input "vHeatingSetpoint", "number", title: "Degrees"
	}   
	section("Vacation Mode Cooling setting...") {
		input "vCoolingSetpoint", "number", title: "Degrees"
	}
	section("Heat setting..." ) {
		input "nHeatingSetpoint", "number", title: "Degrees"
	}   
	section("Cooling setting...") {
		input "nCoolingSetpoint", "number", title: "Degrees"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	appNewMode(location.currentMode)
	subscribe(location, "mode", modeChangeHandler)
    log.debug "Initialized: currently in ${state.lastMode}"
}

def appNewMode(nMode) {
    if (nMode == mode1) {
        log.debug "Bon Voyage: mode is $nMode, heat: $vHeatingSetpoint, cool: $vCoolingSetpoint"
        thermostats.quickSetHeat(vHeatingSetpoint)
        thermostats.quickSetCool(vCoolingSetpoint)
    } else {
        if (state.lastMode == mode1) {
            log.debug "You are back: mode is $nMode, heat: $nHeatingSetpoint, cool: $nCoolingSetpoint"
            thermostats.quickSetHeat(nHeatingSetpoint)
            thermostats.quickSetCool(nCoolingSetpoint)
        } else {
            log.debug "Ho-Hum: mode is $nMode"
        }
    }
    state.lastMode = nMode
}

def modeChangeHandler(evt) {
    appNewMode(evt.value)
}