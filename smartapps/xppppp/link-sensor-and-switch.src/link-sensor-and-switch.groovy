/**
 *  Copyright 2015 SmartThings
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
 *  Virtual Thermostat
 *
 *  Author: SmartThings
 */
definition(
    name: "Link sensor and switch",
    namespace: "xppppp",
    author: "Sxppppp",
    description: "Turn a switch on or off depending on the state of a sensor.",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch@2x.png"
)

preferences {
	section("Choose a temperature sensor... "){
		input "sensor", "capability.temperatureMeasurement", title: "Sensor"
	}
	section("Select the switch(es)... "){
		input "outlets", "capability.switch", title: "Switches", multiple: true
	}
	section("Threshold..."){
		input "setpoint", "decimal", title: "Set Temp"
	}
	section("Select when the switch goes on: 'low' or 'high'..."){
		input "mode", "enum", title: "On low or high?", options: ["low","high"]
	}
}

def installed()
{
	subscribe(sensor, "temperature", temperatureHandler)
}

def updated()
{
	unsubscribe()
	subscribe(sensor, "temperature", temperatureHandler)
}

def temperatureHandler(evt)
{
    if (mode == "low") {
       if (evt.doubleValue > setpoint) {
           outlets.off();
       } else {
           outlets.on();
       }
    } else {
        if (evt.doubleValue > setpoint) {
            outlets.on();
        } else {
            outlets.off();
        }
    }
}
