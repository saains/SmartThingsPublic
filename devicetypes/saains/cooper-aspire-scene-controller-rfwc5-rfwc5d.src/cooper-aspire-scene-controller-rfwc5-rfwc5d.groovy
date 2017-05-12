/**
 *  Copyright 2015 SmartThings
 *https://graph-na02-useast1.api.smartthings.com/ide/device/editor/93342121-e596-4896-ac33-49d9553cfee6#
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
	definition (name: "Cooper Aspire Scene Controller RFWC5 RFWC5D", namespace: "saains", author: "Scott Ainsworth") {
		//capability "Actuator"
        capability "Button"
        capability "Configuration"
        capability "Sensor"
        //capability "Switch"
        //capability "switchLevel"
        
		//command "on"
		//command "off"
        //command "clearassoc"
        command "CheckIndicators"
        //command "initialize"

        
        attribute "currentButton", "STRING"
        attribute "numberOfButtons", "number"
        attribute "Indicator1", "enum",  ["On", "Off"]
        attribute "Indicator2", "enum",  ["On", "Off"]
        attribute "Indicator3","enum",  ["On", "Off"]
        attribute "Indicator4","enum",  ["On", "Off"]
        attribute "Indicator5","enum",  ["On", "Off"]
        attribute "IndDisplay", "STRING"
        
		// zw:L type:0202 mfr:001A prod:574D model:0000 ver:1.13 zwv:2.78 lib:01 cc:87,77,86,22,2D,85,72,21,70
		//Controller Replication-21;
        //Application Status-22;
        //Switch Multilevel	0x26
        //Scene Controller Conf-2D;
        //Scene Activation-2B
        //Configuration-70;
        //Manufacturer Specific-72;
        //Node Naming-77; 
        //Association-85;
        //Version-86;
		//indicator-87;
        // Hail-82????????
       
        
		fingerprint type: "0202", mfr: "001A", prod: "574D", model: "0000",  cc:"87,77,86,22,2D,85,72,21,70" 
	}
    
    preferences {
                input (
                type: "paragraph",
                element: "paragraph",
                title: "Configure Scenes",
                description: "The Cooper controller can control devices via scenes and via association.  Scene capable devices are those which report 2B in the Raw Description. Scene capable devices must have scenes locally configured. Scenes 251-255 are reserved to configure buttons not assigned another scene#. Entries for associated devices must be followed by a level setting. On off devices use 0 or 255, dimable devices use 0 to 100."
            )
    section {
    	input "sceneNum1", "number", title: "Button 1 scene ID (1-250)", required: false
        input "dimdur1", "number", title: "Button 1 scene dimming duration (0-60) seconds", required: false
		input "sceneCap1", "text", title: "Button 1 Scene Capable Devices -example (A3, 2C, 25)", required: false
        input "assocCap1", "text", title: "Button 1 Devices via association, levels(0,1-100,255) -example(03, 100, 0E, 255)", required: false}
    section {
    	input "sceneNum2", "number", title: "Button 2 scene ID (1-250)", required: false
        input "dimdur2", "number", title: "Button 2 scene dimming duration (0-60) seconds", required: false
		input "sceneCap2", "text", title: "Button 2 Scene Capable Devices -example (A3, 2C, 25)", required: false
        input "assocCap2", "text", title: "Button 2 Devices via association, levels(0,1-100,255) -example(03, 100, 0E, 255)", required: false}
    section {
    	input "sceneNum3", "number", title: "Button 3 scene ID (1-250)", required: false
        input "dimdur3", "number", title: "Button 3 scene dimming duration (0-60) seconds", required: false
		input "sceneCap3", "text", title: "Button 3 Scene Capable Devices -example (A3, 2C, 25)", required: false
        input "assocCap3", "text", title: "Button 3 Devices via association, levels(0,1-100,255) -example(03, 100, 0E, 255)", required: false}
    section {
    	input "sceneNum4", "number", title: "Button 4 scene ID (1-250)", required: false
        input "dimdur4", "number", title: "Button 4 scene dimming duration (0-60) seconds", required: false
		input "sceneCap4", "text", title: "Button 4 Scene Capable Devices -example (A3, 2C, 25)", required: false
        input "assocCap4", "text", title: "Button 4 Devices via association, levels(0,1-100,255) -example(03, 100, 0E, 255)", required: false}
    section {
    	input "sceneNum5", "number", title: "Button 5 scene ID (1-250)", required: false
        input "dimdur5", "number", title: "Button 5 scene dimming duration (0-60) seconds", required: false
		input "sceneCap5", "text", title: "Button 5 Scene Capable Devices -example (A3, 2C, 25)", required: false
        input "assocCap5", "text", title: "Button 5 Devices via association, levels(0,1-100,255) -example(03, 100, 0E, 255)", required: false}
     }

	simulator {

	}

	tiles (scale: 2){
		standardTile("Indicators", "device.IndDisplay", width: 6, height: 4) {
			state '${currentValue}',label:'${currentValue}', icon: "st.unknown.zwave.static-controller", backgroundColor:"#ffffff"
		} 
        standardTile("configure", "device.configure",width: 6, height:2, inactiveLabel: false, decoration:"flat") {
			state "off", label:"Configure Scenes", action:"configure"
		}

		main "Indicators"
		details(["Indicators", "configure"])
	}
}





def parse(String description) {
        def result = null
        def cmd = zwave.parse(description)
        if (cmd) {
                result = zwaveEvent(cmd)
                log.debug "Parsed ${cmd} to ${result.inspect()}"
        } else {
                log.debug "Non-parsed event: ${description}"
        }
        result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
    def result = []
    def cmds = []
    //result << createEvent(descriptionText: "${device.displayName} Button Action")
    cmds << response(zwave.indicatorV1.indicatorGet())
    sendHubCommand(cmds)  
    //log.debug "$result"
    result
    }
    
    
def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
    def result = []
    def cmds = []
    //result << createEvent(descriptionText: "${device.displayName} Button Action")
    cmds << response(zwave.indicatorV1.indicatorGet())
    sendHubCommand(cmds)  
    //log.debug "$result"
    result
    }

 
 def zwaveEvent(physicalgraph.zwave.commands.indicatorv1.IndicatorReport cmd) {
	def events = []
    def event = []
    def event2 =[]
    def indval = 0
    def onoff = 0
    def priorOnoff = 0
    def ino = 0
    def ibit = 0
    def istring = ""
    indval = cmd.value
    if(state.lastindval  == indval &&(now() -state.repeatStart <2000 )){  // test to see if it is actually a change.  The controller sends double commands by design. 
      //log.debug "skipping and repeat"
     createEvent([:])
    }
    else{

    istring = "IND " + Integer.toString(indval+128,2).reverse().take(5) // create a string to display for user
    event = createEvent(name: "IndDisplay", value: "$istring", descriptionText: "Indicators: $istring", linkText: "device.label Indicators: $istring")
    events << event
    
	for (i in 0..4) {
    	ibit = 2**i
    	ino = i + 1
    	onoff = indval & ibit
        priorOnoff = state.lastindval & ibit
        //log.debug "$ino is $onoff , piorOnoff is:$priorOnoff ibit is $ibit"
        if (onoff != priorOnoff){
        //log.debug "$ino first if true"
            if (onoff) { log.debug "$ino second if true"
               event = createEvent(name: "Indicator$ino", value: "On", descriptionText: "$device.label Indicator:$ino On", linkText: "$device.label Indicator:$ino on")
            } else { log.debug "$ino second if false"
                event = createEvent(name: "Indicator$ino", value: "Off", descriptionText: "$device.label Indicator:$ino Off", linkText: "$device.label Indicator:$ino off")
            }
        event2 = createEvent(name:"button",value:"pushed",data:[buttonNumber: ino],descriptionText:"$device.displayName button $ino pushed",linkText:"$device.label Button:$ino pushed",isStateChange: true)
        events << event
     	events << event2
        } //else { log.debug "$ino first if false"}
    }
    state.lastindval = indval
    state.repeatStart = now()
    
   events
  }
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	def event = [isStateChange: true]
	event.linkText = device.label ?: device.name
	event.descriptionText = "Cooper $event.linkText: $cmd"
	event
}



def configure() {
  log.debug("executing configure hub id is: $zwaveHubNodeId")
    def cmds = []
    
    //the buttons on the controller will not work with out a scene load in.  Use 251-255 if no scene number is specified in the preferences
    def s1 = 251
    def s2 = 252
    def s3 = 253
    def s4 = 254
    def s5 = 255
  log.debug "s2 is $s1"
    
    if (sceneNum1) s1 = sceneNum1
    if (sceneNum2) s2 = sceneNum2
    if (sceneNum3) s3 = sceneNum3
    if (sceneNum4) s4 = sceneNum4
    if (sceneNum5) s5 = sceneNum5
    log.debug "s2 is now $s1"
    //will use 0 for dimming durations unless a value is entered
    def d1 = 0x00
    def d2 = 0x00
    def d3 = 0x00
    def d4 = 0x00
    def d5 = 0x00
   

    if (dimdur1) d1=dimdur1 else d1 = 0x00
    if (dimdur2) d2=dimdur2 else d2 = 0x00
    if (dimdur3) d3=dimdur3 else d3 = 0x00
    if (dimdur4) d4=dimdur4 else d4 = 0x00
    if (dimdur5) d5=dimdur5 else d5 = 0x00
    
//for each button group create a sub to run for each button
cmds += buttoncmds(1, s1, sceneCap1, assocCap1, d1)
cmds << zwave.associationV1.associationGet(groupingIdentifier:1).format()
cmds << zwave.sceneControllerConfV1.sceneControllerConfGet(groupId:1).format()
cmds += buttoncmds(2, s2, sceneCap2, assocCap2, d2)
cmds << zwave.associationV1.associationGet(groupingIdentifier:2).format()
cmds << zwave.sceneControllerConfV1.sceneControllerConfGet(groupId:2).format()
cmds += buttoncmds(3, s3, sceneCap3, assocCap3, d3)
cmds << zwave.associationV1.associationGet(groupingIdentifier:3).format()
cmds << zwave.sceneControllerConfV1.sceneControllerConfGet(groupId:3).format()
cmds += buttoncmds(4, s4, sceneCap4, assocCap4, d4)
cmds << zwave.associationV1.associationGet(groupingIdentifier:4).format()
cmds << zwave.sceneControllerConfV1.sceneControllerConfGet(groupId:4).format()
cmds += buttoncmds(5, s5, sceneCap5, assocCap5, d5)
cmds << zwave.associationV1.associationGet(groupingIdentifier:5).format()
cmds << zwave.sceneControllerConfV1.sceneControllerConfGet(groupId:5).format()
// send commands

log.debug "$cmds"
delayBetween(cmds,3000)
}


// Parse the user input and create commands to set up the controller -- called from config
def buttoncmds(btn, scene, scenelist, assoclist, dimdur)
{ 
def cmds = []
def lList = 0
def alist = []
def alists = []
def atlist = []
def amap = [level:0, anodes: []]

log.debug "assoclist is:$assoclist"

//add clear associaton cmd to the list
cmds << zwave.associationV1.associationRemove(groupingIdentifier: btn, nodeId:[]).format()

// add the levels to the data structure.
if (assoclist) {
	atlist = assoclist.tokenize(', ')
     log.debug "atlist is: $atlist"
	lList = atlist.size()
	for (int i = 1; i <= lList; i+=2) {
		if(alist.every { it != atlist[i]}) {	alist << atlist[i] } // if the value is not in alist then add it.
	}
	alist.each{amap = [level: it, anodes: []]      //add the levels to the data structure
		alists << amap  //build the matrix
    }
 
// fill the matrix with nodes ordered with levels            log.debug "afterif alists:$alists  i is:$i x is:$x"
    for (int i = 1; i <= lList; i+=2) {
    	for (int x = 0; x < alist.size(); x++){
            def bob = alists[x]
            if (bob.level == atlist[i]) {bob.anodes << atlist[i-1]}
            }
       }
      log.debug "alists is now: $alists"
   }
// for each list of ids
// <<create association set commands
// <<create configuration set commands
	for (int i = 0; i <=alists.size(); i++){
   
    def bob = alists[i]
    def nodestring = ""
    def thislevel = 0xFF
     log.debug "alists i is $bob"
    if (bob){
    nodestring = bob.anodes.join(", ")
    thislevel = [bob.level.value]}
    

    cmds << AssocNodes(nodestring,btn,1)
    cmds << zwave.configurationV1.configurationSet(parameterNumber:btn, size:1, configurationValue: [thislevel]).format()
	}
// <<scene set commands
// <<create association set commands for scenes

log.debug "passing to scene command $btn  and  $scene  and  $dimdur"
cmds << zwave.sceneControllerConfV1.sceneControllerConfSet(groupId:btn, sceneId:scene, dimmingDuration:dimdur).format()
cmds << AssocNodes(scenelist, btn, 1)

return (cmds)
}



def CheckIndicators(){
	delayBetween([
	zwave.indicatorV1.indicatorGet().format(),
    ],1000)
    
}
def initialize() {
	sendEvent(name: "numberOfButtons", value: 5)
}

def installed() {
initialize()
configure()
}

def updated() {
	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 5000) {
		state.updatedLastRanAt = now()
		log.debug "Executing 'updated'"
        
        initialize()
 
	}
	else {
		log.trace "updated(): Ran within last 5 seconds so skipping."
	}
}


def AssocNodes(txtlist,group,hub) {

def List1
def List3 = []
def cmd = ""
if (txtlist){
	List1 = txtlist.tokenize(', ')
	List3 = List1.collect{Integer.parseInt(it,16)}
	if (hub){
		List3 << zwaveHubNodeId
		}
	} 
else if (hub){
	List3 = zwaveHubNodeId
	}

log.debug "associating group $group: $List3"

cmd = zwave.associationV1.associationSet(groupingIdentifier:group, nodeId:List3).format()

return (cmd)
}





// convert a hex string to integer
def integerhex(String v) {
	if (v == null) {
    	return 0
    }
    
	return v.split(',').collect { Integer.parseInt(it, 16) }
}