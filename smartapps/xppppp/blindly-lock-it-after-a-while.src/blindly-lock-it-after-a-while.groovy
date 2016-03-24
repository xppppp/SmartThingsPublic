/**
 *  Lock It After A While
 *
 *  Original Author: 	Chris LeBlanc (LeBlaaanc)
 *  Email: 		chris@leblaaanc.com
 *  Date: 		05/22/2014
 */

definition(
    name: "Blindly Lock It After A While",
    namespace: "xppppp",
    author: "xppppp",
    description: "Locks a lock after a given period of time of being unlocked.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true
)

preferences {
	section("Choose lock(s)") {
		input "lock1","capability.lock", multiple: true
	}
	section("After this many minutes") {
		input "after", "number", title: "Minutes", description: "10 minutes",  required: false
	}
    section("Notification method") {
    		input "push", "bool", title: "Push notification", metadata: [values: ["Yes","No"]]
    }
}

def installed()
{
	subscribe(lock1, "lock.unlocked", eventHandler)
}

def updated()
{
	unsubscribe()
	installed()
}

def eventHandler(evt)
{
	def delay = (after != null && after != "") ? after * 60 : 600
	runIn(delay, lockTheLocks)
    log.debug("runIn(${delay}, lockTheLocks)")
}

def lockTheLocks ()
{
	def xsDelta = (after != null && after != "") ? after : 10;
	sendMessage("Doors locked after ${xsDelta} minutes.")
	lock1.lock()
}

def sendMessage(msg) 
{
	log.debug("sendMessage(${msg})")
	if (push) {
		sendPush msg
	}
}