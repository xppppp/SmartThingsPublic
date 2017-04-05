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
 *  Good Night
 *
 *  Author: SmartThings
 *  Date: 2013-03-07
 */
definition(
    name: "Good Night Switch Fork",
    namespace: "xppppp",
    author: "xppppp",
    description: "Runs one routine if switch on; other if off.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/ModeMagic/good-night.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/ModeMagic/good-night@2x.png"
)

preferences {
	page(name: "firstPage", title: "Time and Switch", nextPage: "selectActions", uninstall: true) {
        section("At this time of day") {
                input "timeOfDay", "time", title: "Time?"
        }
        section("Based on this switch") {
                input "tswitch", "capability.switch", multiple: false, required: false
        }
    }
    page(name: "selectActions", title: "Select Actions", install: true, uninstall: true)
}


def selectActions() {
    dynamicPage(name: "selectActions", title: "Select Routines to Execute", install: true, uninstall: true) {

        // get the available actions
            def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
                // sort them alphabetically
                actions.sort()
                section("Switch On Routine") {
                    // use the actions as the options for an enum input
                        input "onRoutine", "enum", title: "Select an routine to execute", options: actions
                }
                 section("Switch Off Routine") {
                    // use the actions as the options for an enum input
                        input "offRoutine", "enum", title: "Select an routine to execute", options: actions
                }
            }
    }
}

def installed() {
    subscribe(tswitch, "switch.on", onHandler)
    subscribe(tswitch, "switch.off", offHandler)
    schedule(timeOfDay, doit)
}

def onHandler() {
    state.tswitchState = "on"
}

def offHandler() {
    state.tswitchState = "off"
}

def updated() {
    unsubscribe()
    installed()
}

def doit() {
    if (state.tswitchState== "on") {
        log.trace "switch on - executing on routine"
        onRoutine
    } else {
        log.trace "switch off - executing off routine"
        offRoutine
    }
}
