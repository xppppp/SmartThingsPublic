/**
 *  Unlock Events
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
    name: "Unlock Events",
    namespace: "xppppp",
    author: "P Park",
    description: "Do Some Stuff when Unlocking",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When this lock is unlocked...") {
		input "lock1", "capability.lock", multiple: false, required: true
	}
    section("And it's dark...") {
		input "luminance1", "capability.illuminanceMeasurement", title: "Where?"
	}
  	section("Turn on these lights..."){
		input "switches", "capability.switch", multiple: true
	}
	section("Choose thermostat... ") {
		input "thermostat", "capability.thermostat"
	}
	section("Heat setting..." ) {
		input "heatingSetpoint", "number", title: "Degrees"
	}   
	section("Cooling setting...") {
		input "coolingSetpoint", "number", title: "Degrees"
	}
    section("and change mode to...") {
		input "afterUnlockMode", "mode", title: "Mode?"
	}

}

def installed() {
	subscribe(lock1, "lock.unlocked", unlockedHandler)
}

def updated() {
	unsubscribe()
	subscribe(lock1, "lock.unlocked", unlockedHandler)
}

private def executeRoutine(name) {
    log.trace "Executing Routine \'${name}\'"
    location.helloHome.execute(name)
}

private def setMode(name) {
    log.trace "Setting location mode to \'${name}\'"
    setLocationMode(name)
}

private def setAlarmMode(name) {
    log.trace "Setting alarm system mode to \'${name}\'"

    def event = [
        name:           "alarmSystemStatus",
        value:          name,
        isStateChange:  true,
        displayed:      true,
        description:    "alarm system status is ${name}",
    ]

    sendLocationEvent(event)
}

def unlockedHandler(evt) {
   	setAlarmMode("off")
    setMode(afterUnlockMode)
    def lightSensorState = luminance1.currentIlluminance
	log.debug "SENSOR = $lightSensorState"
	if (lightSensorState != null && lightSensorState < 10) {
		log.trace "light.on() ... [luminance: ${lightSensorState}]"
		switches.on()
	}
	thermostat.quickSetHeat(heatingSetpoint)
	thermostat.quickSetCool(coolingSetpoint)
}
