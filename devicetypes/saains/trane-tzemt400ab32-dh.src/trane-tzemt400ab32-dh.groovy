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
 *  This device handler is based on the smartthings zwave thermostat template.  It has been modified for the TRANE thermostat
 *  Model: TZEMT400AB32 version: 01.00.10.  It should work with similar RCS Technology thermostats such as the TZ43.
 *  Fan circulate mode is activated with a value of 2 rather than 6 as specified by the zwave standard.
 *  Run and hold schedule is controlled via zwave configuration command class parameter 132.
 *  Energy save mode is controlled via the zwave basic command class.
 
 */
metadata {
	definition (name: "TRANE TZEMT400AB32 DH", namespace: "saains", author: "Scott Ainsworth") {
		capability "Actuator"
		capability "Temperature Measurement"
        capability "Thermostat Operating State"
		capability "Relative Humidity Measurement"
		capability "Thermostat"
		capability "Configuration"
		capability "Polling"
		capability "Sensor"
		
		attribute "thermostatFanState", "string"
        attribute "EsaveModeState", "enum", ["esave-on", "esave-off"]
        attribute "ScheduleModeState", "enum", ["run", "hold"]


        command "quickSetCool"
        command "quickSetHeat"
        command "Esavemode_on"
        command "Esavemode_off"
        command "runSch"
        command "holdSch"

// Note the fingerprint below is very specific an may need to be modifed for your device.
//zw:L type:0806 mfr:008B prod:5452 model:5431 ver:20.09 zwv:2.64 lib:06 cc:40,42,43,44,45,70,31,86,81,72,76,85
        fingerprint type: "0806", mfr: "008B", prod: "5452", model: "5431", cc: "40,42,43,44,45,70,31,86,81,72,76,85"


	}

	// simulator metadata
	simulator {
		status "off"			: "command: 4003, payload: 00"
		status "heat"			: "command: 4003, payload: 01"
		status "cool"			: "command: 4003, payload: 02"
		status "auto"			: "command: 4003, payload: 03"
		status "emergencyHeat"	: "command: 4003, payload: 04"

		status "fanAuto"		: "command: 4403, payload: 00"
		status "fanOn"			: "command: 4403, payload: 01"
		status "fanCirculate"	: "command: 4403, payload: 02"

		status "heat 60"        : "command: 4303, payload: 01 09 3C"
		status "heat 68"        : "command: 4303, payload: 01 09 44"
		status "heat 72"        : "command: 4303, payload: 01 09 48"

		status "cool 72"        : "command: 4303, payload: 02 09 48"
		status "cool 76"        : "command: 4303, payload: 02 09 4C"
		status "cool 80"        : "command: 4303, payload: 02 09 50"

		status "temp 58"        : "command: 3105, payload: 01 2A 02 44"
		status "temp 62"        : "command: 3105, payload: 01 2A 02 6C"
		status "temp 70"        : "command: 3105, payload: 01 2A 02 BC"
		status "temp 74"        : "command: 3105, payload: 01 2A 02 E4"
		status "temp 78"        : "command: 3105, payload: 01 2A 03 0C"
		status "temp 82"        : "command: 3105, payload: 01 2A 03 34"

		status "idle"			: "command: 4203, payload: 00"
		status "heating"		: "command: 4203, payload: 01"
		status "cooling"		: "command: 4203, payload: 02"
		status "fan only"		: "command: 4203, payload: 03"
		status "pending heat"	: "command: 4203, payload: 04"
		status "pending cool"	: "command: 4203, payload: 05"
		status "vent economizer": "command: 4203, payload: 06"

		// reply messages
		reply "2502": "command: 2503, payload: FF"
	}

	tiles (scale: 2) {
		valueTile("temperature", "device.temperature", width: 4, height: 4) {
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}

		standardTile("mode", "device.thermostatMode",width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "off", label:'${name}', action:"heat", nextState:"heat"
			state "heat", label:'${name}', action:"cool", nextState:"cool"
			state "cool", label:'${name}', action:"auto", nextState:"auto"
			state "auto", label:'${name}', action:"off", nextState:"off"
			//state "emergency heat", label:'${name}', action:"off", nextState:"off"

		}

        standardTile("fanMode", "device.thermostatFanMode",width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "fanAuto", label:'${name}', action:"fanOn", nextState:"fanOn"
			state "fanOn", label:'${name}', action:"fanCirculate", nextState:"fanCirculate"
			state "fanCirculate", label:'${name}', action:"fanAuto", nextState:"fanAuto"
       	}
       	
        
		controlTile("heatSliderControl", "device.heatingSetpoint", "slider", height: 1, width: 4, inactiveLabel: false) {
			state "setHeatingSetpoint", action:"quickSetHeat", backgroundColor:"#d04e00"
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint",height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
			state "heat", label:'${currentValue}° heat', backgroundColor:"#ffffff"
		}
		controlTile("coolSliderControl", "device.coolingSetpoint", "slider", height: 1, width: 4, inactiveLabel: false) {
			state "setCoolingSetpoint", action:"quickSetCool", backgroundColor: "#1e9cbb"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint",height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
			state "cool", label:'${currentValue}° cool', backgroundColor:"#ffffff"
		}
        standardTile("thermostatOperatingState", "device.thermostatOperatingState", width: 2, height:2, decoration: "flat") {
			state "thermostatOperatingState", label:'${currentValue}', backgroundColor:"#ffffff"
		}
        
		standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Refresh", action:"polling.poll", icon:"st.secondary.refresh"
		}
		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat") {
			state "configure", label:"Configure", action:"configuration.configure", icon:"st.secondary.configure"
		}
        
        standardTile("Esave", "device.EsaveModeState",height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
			state "esave-on", label:'${name}', action:"Esavemode_off", nextState:"esave-off"
			state "esave-off", label:'${name}', action:"Esavemode_on", nextState:"esave-on"
		}
        
        standardTile("Schedule", "device.ScheduleModeState",height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
			state "run", label:'${name}', action:"holdSch", nextState:"hold"
			state "hold", label:'${name}', action:"runSch", nextState:"run"
       	}
        
		main "temperature"
		details(["temperature", "mode", "fanMode", "heatSliderControl", "heatingSetpoint", "coolSliderControl", "coolingSetpoint", "thermostatOperatingState", "refresh", "configure","Esave","Schedule"])
	}
}

def parse(String description)
{  log.debug "TZEM parse description : $description"
//, [0x42:1, 0x43:2, 0x31: 3, 0x20: 1, 0x70: 1]
	def map = createEvent(zwaveEvent(zwave.parse(description))) 
	//log.debug "map is: $map"
    if (!map) {
     
		return null
	}

	def result = [map]
    
	if (map.isStateChange && map.name in ["heatingSetpoint","coolingSetpoint","thermostatMode"]) {
		def map2 = [
			name: "thermostatSetpoint",
			unit: getTemperatureScale()
		]
		if (map.name == "thermostatMode") {
			state.lastTriedMode = map.value
			if (map.value == "cool") {
				map2.value = device.latestValue("coolingSetpoint")
				//log.info "L150 THERMOSTAT, latest cooling setpoint = ${map2.value}"
			}
			else {
				map2.value = device.latestValue("heatingSetpoint")
				//log.info "L154 THERMOSTAT, latest heating setpoint = ${map2.value}"
			}
		}
		else {
			def mode = device.latestValue("thermostatMode")
			//log.info "L159 THERMOSTAT, latest mode = ${mode}"
			if ((map.name == "heatingSetpoint" && mode == "heat") || (map.name == "coolingSetpoint" && mode == "cool")) {
				map2.value = map.value
				map2.unit = map.unit
			}
		}
		if (map2.value != null) {
			//log.debug "L167 THERMOSTAT, adding setpoint event: $map"
			result << createEvent(map2)
		}
	} else if (map.name == "thermostatFanMode" && map.isStateChange) {
        //log.debug "L170 map name is: $map.name  map value is: $map.value"
		state.lastTriedFanMode = map.value
	}
	//log.debug "L198 Parse result is: $result"
	result
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceCmdEncap cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand([0x31: 3])
    log.debug ("Command from endpoint ${cmd.sourceEndPoint}: ${encapsulatedCommand}")
	if (encapsulatedCommand) {
		return zwaveEvent(encapsulatedCommand)
	}
}

// Event Generation
def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd)
{
	def cmdScale = cmd.scale == 1 ? "F" : "C"
	def map = [:]
	map.value = convertTemperatureIfNeeded(cmd.scaledValue, cmdScale, cmd.precision)
	map.unit = getTemperatureScale()
	map.displayed = false
	switch (cmd.setpointType) {
		case 1:
			map.name = "heatingSetpoint"
			break;
		case 2:
			map.name = "coolingSetpoint"
			break;
		default:
			return [:]
	}
	// So we can respond with same format
	state.size = cmd.size
	state.scale = cmd.scale
	state.precision = cmd.precision
	map
}

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv2.SensorMultilevelReport cmd) {
	def map = [:]
	if (cmd.sensorType == 1) {
		map.name = "temperature"
		map.unit = getTemperatureScale()
		map.value = getTempInLocalScale(cmd.scaledSensorValue, (cmd.scale == 1 ? "F" : "C"))
		updateThermostatSetpoint(null, null)
	} else if (cmd.sensorType == 5) {
		map.name = "humidity"
		map.unit = "%"
		map.value = cmd.scaledSensorValue
	}
	sendEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv3.SensorMultilevelReport cmd) {
	def map = [:]
	if (cmd.sensorType == 1) {
		map.name = "temperature"
		map.unit = getTemperatureScale()
		map.value = getTempInLocalScale(cmd.scaledSensorValue, (cmd.scale == 1 ? "F" : "C"))
		updateThermostatSetpoint(null, null)
	} else if (cmd.sensorType == 5) {
		map.value = cmd.scaledSensorValue
		map.unit = "%"
		map.name = "humidity"
	}
	sendEvent(map)
}


def zwaveEvent(physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport cmd) {
	def map = [name: "thermostatOperatingState"]
	switch (cmd.operatingState) {
		case physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport.OPERATING_STATE_IDLE:
			map.value = "idle"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport.OPERATING_STATE_HEATING:
			map.value = "heating"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport.OPERATING_STATE_COOLING:
			map.value = "cooling"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport.OPERATING_STATE_FAN_ONLY:
			map.value = "fan only"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport.OPERATING_STATE_PENDING_HEAT:
			map.value = "pending heat"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport.OPERATING_STATE_PENDING_COOL:
			map.value = "pending cool"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev2.ThermostatOperatingStateReport.OPERATING_STATE_VENT_ECONOMIZER:
			map.value = "vent economizer"
			break
	}
	sendEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatfanstatev1.ThermostatFanStateReport cmd) {
	def map = [name: "thermostatFanState", unit: ""]
	switch (cmd.fanOperatingState) {
		case 0:
			map.value = "idle"
			break
		case 1:
			map.value = "running"
			break
		case 2:
			map.value = "running high"
			break
	}
	map
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd) {
	def map = [:]
	switch (cmd.mode) {
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_OFF:
			map.value = "off"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_HEAT:
			map.value = "heat"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_AUXILIARY_HEAT:
			map.value = "emergency heat"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_COOL:
			map.value = "cool"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_AUTO:
			map.value = "auto"
			break
	}
	map.name = "thermostatMode"
	map
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport cmd) {
	def map = [:]
	switch (cmd.fanMode) {
		case physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport.FAN_MODE_AUTO_LOW:
			map.value = "fanAuto"
			break
		case physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport.FAN_MODE_LOW:
			map.value = "fanOn"
			break
		case physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport.FAN_MODE_AUTO_HIGH:
			map.value = "fanCirculate"
			break
	}
    log.debug "L323 thermostate fanMode is: $map.value"
	map.name = "thermostatFanMode"
	map.displayed = false
	map
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeSupportedReport cmd) {
	def supportedModes = ""
	if(cmd.off) { supportedModes += "off " }
	if(cmd.heat) { supportedModes += "heat " }
	if(cmd.auxiliaryemergencyHeat) { supportedModes += "emergency heat " }
	if(cmd.cool) { supportedModes += "cool " }
	if(cmd.auto) { supportedModes += "auto " }
	state.supportedModes = supportedModes
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeSupportedReport cmd) {
	def supportedFanModes = "fanAuto fanOn fanCirculate "
	state.supportedFanModes = supportedFanModes
    log.debug "L331 supportedFanModes are: $state.supportedFanModes , cmd is $cmd"
}


def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
    //log.debug "L350 Zwave event received: $cmd"
    
	def map = [name: "", unit: ""]
    //log.debug "L353 configuratonValue: $cmd.scaledConfigurationValue"
    //log.debug "L353 parameterNumber: $cmd.parameterNumber"

	if (cmd.parameterNumber == 132){
		map.name = "ScheduleModeState"
		switch (cmd.scaledConfigurationValue) {
			case 0:
				map.value = "hold"
				break
			case 1:
				map.value = "run"
				break
		}
	}
	else if (cmd.parameterNumber == 25) {
		map.name = "EsaveModeState"
		switch (cmd.scaledConfigurationValue) {
			case 0:
				map.value = "esave-off"
				break
			case 2:
				map.value = "esave-on"
				break
		}
	}
    else {map = null}
    
map
    
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	log.debug "Zwave BasicReport: $cmd"
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.warn "Unexpected zwave command TZEM: $cmd"
}

// Command Implementations
//def poll() {
//	delayBetween([
		//zwave.sensorMultilevelV3.sensorMultilevelGet().format(), // current temperature
//        new physicalgraph.device.HubAction(zwave.multiChannelV3.multiInstanceCmdEncap(instance: 1).encapsulate(zwave.sensorMultilevelV3.sensorMultilevelGet()).format()), // temperature
//		zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1).format(),
//		zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 2).format(),
//		zwave.thermostatModeV2.thermostatModeGet().format(),
//		zwave.thermostatFanModeV3.thermostatFanModeGet().format(),
//		zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format(),
//        zwave.basicV1.basicGet().format()
//	], 2300)
//}

def poll() {
	def cmds = []
	cmds << new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeSupportedGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.thermostatFanModeV3.thermostatFanModeSupportedGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.thermostatFanModeV3.thermostatFanModeGet().format())
//	cmds << new physicalgraph.device.HubAction(zwave.batteryV1.batteryGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.multiChannelV3.multiInstanceCmdEncap(instance: 2).encapsulate(zwave.sensorMultilevelV3.sensorMultilevelGet()).format()) // humidity
	cmds << new physicalgraph.device.HubAction(zwave.multiChannelV3.multiInstanceCmdEncap(instance: 1).encapsulate(zwave.sensorMultilevelV3.sensorMultilevelGet()).format()) // temperature
	cmds << new physicalgraph.device.HubAction(zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format())
	//def time = getTimeAndDay()
//	if (time) {
//		cmds << new physicalgraph.device.HubAction(zwave.clockV1.clockSet(time).format())
//	}
	// ThermostatModeReport will spawn request for operating state and setpoints so request this last
	// this as temperature and mode is needed to determine which setpoints should be requested first
	cmds << new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeGet().format())
	// Add 2 seconds delay between each command to avoid flooding the Z-Wave network choking the hub 
	sendHubCommand(cmds, 2000)
}






def quickSetHeat(degrees) {
	setHeatingSetpoint(degrees, 1000)
}

def setHeatingSetpoint(degrees, delay = 30000) {
	setHeatingSetpoint(degrees.toDouble(), delay)
}

def setHeatingSetpoint(Double degrees, Integer delay = 30000) {
	//log.trace "setHeatingSetpoint($degrees, $delay)"
	def deviceScale = state.scale ?: 1
	def deviceScaleString = deviceScale == 2 ? "C" : "F"
    def locationScale = getTemperatureScale()
	def p = (state.precision == null) ? 1 : state.precision

    def convertedDegrees
    if (locationScale == "C" && deviceScaleString == "F") {
    	convertedDegrees = celsiusToFahrenheit(degrees)
    } else if (locationScale == "F" && deviceScaleString == "C") {
    	convertedDegrees = fahrenheitToCelsius(degrees)
    } else {
    	convertedDegrees = degrees
    }

	delayBetween([
		zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 1, scale: deviceScale, precision: p, scaledValue: convertedDegrees).format(),
		zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1).format()
	], delay)
}

def quickSetCool(degrees) {
	setCoolingSetpoint(degrees, 1000)
}

def setCoolingSetpoint(degrees, delay = 30000) {
	setCoolingSetpoint(degrees.toDouble(), delay)
}

def setCoolingSetpoint(Double degrees, Integer delay = 30000) {
    //log.trace "setCoolingSetpoint($degrees, $delay)"
	def deviceScale = state.scale ?: 1
	def deviceScaleString = deviceScale == 2 ? "C" : "F"
    def locationScale = getTemperatureScale()
	def p = (state.precision == null) ? 1 : state.precision

    def convertedDegrees
    if (locationScale == "C" && deviceScaleString == "F") {
    	convertedDegrees = celsiusToFahrenheit(degrees)
    } else if (locationScale == "F" && deviceScaleString == "C") {
    	convertedDegrees = fahrenheitToCelsius(degrees)
    } else {
    	convertedDegrees = degrees
    }

	delayBetween([
		zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 2, scale: deviceScale, precision: p,  scaledValue: convertedDegrees).format(),
		zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 2).format()
	], delay)
}

def configure() {
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSupportedGet().format(),
		zwave.thermostatFanModeV3.thermostatFanModeSupportedGet().format(),
		zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format()
	], 2300)
}

def modes() {
	["off", "heat", "cool", "auto", "emergency heat"]
}


def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def getModeMap() { [
	"off": 0,
	"heat": 1,
	"cool": 2,
	"auto": 3,
	"emergency heat": 4
]}

def setThermostatMode(String value) {
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: modeMap[value]).format(),
		zwave.thermostatModeV2.thermostatModeGet().format()
	], standardDelay)
}

def getFanModeMap() { [
	"auto": 0,
	"on": 1,
	"circulate": 2
]}

def setThermostatFanMode(String value) {
	delayBetween([
		zwave.thermostatFanModeV3.thermostatFanModeSet(fanMode: fanModeMap[value]).format(),
		zwave.thermostatFanModeV3.thermostatFanModeGet().format()
	], standardDelay)
}

def off() {
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 0).format(),
		zwave.thermostatModeV2.thermostatModeGet().format()
	], standardDelay)
}

def heat() {
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 1).format(),
		zwave.thermostatModeV2.thermostatModeGet().format()
	], standardDelay)
}

def emergencyHeat() {
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 4).format(),
		zwave.thermostatModeV2.thermostatModeGet().format()
	], standardDelay)
}

def cool() {
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 2).format(),
		zwave.thermostatModeV2.thermostatModeGet().format()
	], standardDelay)
}

def auto() {
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 3).format(),
		zwave.thermostatModeV2.thermostatModeGet().format()
	], standardDelay)
}

def fanAuto() {
	delayBetween([
		zwave.thermostatFanModeV3.thermostatFanModeSet(fanMode: 0).format(),
        zwave.thermostatFanModeV3.thermostatFanModeGet().format()
	], standardDelay)

}

def fanOn() {
	delayBetween([
		zwave.thermostatFanModeV3.thermostatFanModeSet(fanMode: 1).format(),
        zwave.thermostatFanModeV3.thermostatFanModeGet().format()
	], standardDelay)
}

def fanCirculate() {
	delayBetween([
		zwave.thermostatFanModeV3.thermostatFanModeSet(fanMode: 2).format(),
		zwave.thermostatFanModeV3.thermostatFanModeGet().format()       
	], standardDelay)
}

-

private getStandardDelay() {
	1000
}