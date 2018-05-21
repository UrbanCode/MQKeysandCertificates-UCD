/*
 * Licensed Materials - Property of IBM Corp.
 * IBM UrbanCode Deploy
 * (c) Copyright IBM Corporation 2011, 2014. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
 * GSA ADP Schedule Contract with IBM Corp.
 */
package com.urbancode.air.MQKeysandCertificates

import java.util.UUID
import java.util.List
import java.util.Map
import java.util.regex.Pattern

import groovy.json.JsonOutput

import org.codehaus.jettison.json.JSONObject
import org.codehaus.jettison.json.JSONArray
import groovy.json.JsonSlurper

import com.urbancode.air.AirPluginTool

public class MQKeysandCertificatesHelper {
  def apTool
  def props = []
  def udUser
  def udPass
  def weburl
  def commandOutput = ""

  public MQKeysandCertificatesHelper(def apToolIn) {
      apTool = apToolIn
      props = apTool.getStepProperties()
      udUser = apTool.getAuthTokenUsername()
      udPass = apTool.getAuthToken()
      weburl = System.getenv("AH_WEB_URL")
      com.urbancode.air.XTrustProvider.install()

  }

  private static List<Pattern> getGlobPatternsFromMultiline(String multiline) {
      return multiline.split("\n")
              .findAll({ it.trim().length() > 0 })
              .collect({ FileFilterToRegex.convert(it) })
  }

  def createKeyDB() {
    def mqHome = props['mqHome'].trim()
    def keyDBLocation = props['keyDBLocation'].trim()
    def keyDBPassword = props['keyDBPassword'].trim()
    def DBType = props['DBType'].trim()

    // DB Type options : cms | kdb | pkcs12 | p12

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("~~ Create Key DB             ~~")
    println("~~ Parameters                ~~")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

    println("MQ home           : " + mqHome)
    println("Key DB location   : " + keyDBLocation)
    println("DB type           : " + DBType)

    println("--------------------------------------------------------------------")

    def options = " -keydb -create"
    options += " -db " + keyDBLocation
    options += " -pw " + keyDBPassword
    options += " -type " + DBType
    options += " -stash"

    def commandResult = runCommand(apTool.isWindows,  "runmqakm", mqHome, options, "Creating a key DB")

    println("--------------------------------------------------------------------")
    println(commandResult)
    println(commandOutput)

  }

  def createCSRFromKeyDB() {
    def mqHome = props['mqHome'].trim()
    def keyDBLocation = props['keyDBLocation'].trim()
    def keyDBPassword = props['keyDBPassword'].trim()
    def csrLabel = props['csrLabel'].trim()
    def policyDetails = props['policyDetails'].trim()
    def certificateName = props['certificateName'].trim()
    def csrFilename = props['csrFilename'].trim()

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("~~ Create CSR from Key DB    ~~")
    println("~~ Parameters                ~~")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

    println("MQ home           : " + mqHome)
    println("Key DB location   : " + keyDBLocation)
    println("CSR label         : " + csrLabel)
    println("Policy details    : " + policyDetails)
    println("Certiicate name   : " + certificateName)
    println("CSR filename      : " + csrFilename)

    String city = ""
    String state = ""
    String country = ""
    String organization = ""
    String organizationalUnit = ""
    String keyBitStrength = ""
    Integer keyBitStrengthInt = 0
    String managementType = ""
    String manualCSR = ""
    String certificateAuthority = ""
    String keyAlgorithm = ""
    String policyDN

    def slurper = new JsonSlurper()
    def policyDetailsJSON = slurper.parseText(policyDetails)

    city = policyDetailsJSON.city
    state = policyDetailsJSON.state
    country = policyDetailsJSON.country
    organization = policyDetailsJSON.organization
    organizationalUnit = policyDetailsJSON.organizationalUnit
    keyAlgorithm = policyDetailsJSON.keyAlgorithm
    keyBitStrength = policyDetailsJSON.keyBitStrength
    if (keyBitStrength.length() > 0) {
      keyBitStrengthInt = keyBitStrength.toInteger()
    }
    managementType = policyDetailsJSON.managementType
    manualCSR = policyDetailsJSON.manualCSR
    certificateAuthority = policyDetailsJSON.certificateAuthority
    policyDN = policyDetailsJSON.policyDN

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("~~ Policy values           ~~")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("City                  : " + city)
    println("State                 : " + state)
    println("Country               : " + country)
    println("Organization          : " + organization)
    println("Organization unit     : " + organizationalUnit)
    println("Key algorithm         : " + keyAlgorithm)
    println("Key bit strength      : " + keyBitStrength)
    println("Management type       : " + managementType)
    println("Manual CSR            : " + manualCSR)
    println("Certificate authority : " + certificateAuthority)
    println("Policy DN             : " + policyDN)

    println("--------------------------------------------------------------------")

    def options = " -certreq -create"
    options += " -db " + keyDBLocation
    options += " -pw " + keyDBPassword
    options += " -label " + csrLabel
    options += " -dn \"CN=" + certificateName + ",O=" + organization + ",OU=" + organizationalUnit + ",L=" + city + ",ST=" + state + ",C=" + country + "\""
    options += " -size " + keyBitStrength
    options += " -file " + csrFilename

    def commandResult = runCommand(apTool.isWindows,  "runmqakm", mqHome, options, "Creating a CSR from the key DB")

    println("--------------------------------------------------------------------")
    println(commandResult)
    println(commandOutput)

    apTool.setOutputProperty("csrFilename", csrFilename)
    apTool.setOutputProperty("certificateName", certificateName)
    apTool.setOutputProperties()

  }

  def backupKeyDBFiles() {
    def queueManagerLocation = props['queueManagerLocation'].trim()

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("~~ Backup Key DB Files       ~~")
    println("~~ Parameters                ~~")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

    println("Queue Manager location : " + queueManagerLocation)

    Date dateNow = new Date()
    def dateString = dateNow.format('dd-MMM-yyyy-HH-mm')

    def options = " -R " + queueManagerLocation + "/ssl " + queueManagerLocation + "/ssl." + dateString

    def commandResult = runCommand(apTool.isWindows,  "cp", "/bin", options, "Backup Key DB files")

    println("--------------------------------------------------------------------")
    println(commandResult)
    println(commandOutput)
}

  def addCertsToKeyDB() {
    def mqHome = props['mqHome'].trim()
    def keyDBLocation = props['keyDBLocation'].trim()
    def keyDBPassword = props['keyDBPassword'].trim()
    def certificateFile = props['certificateFile'].trim()
    def label = props['label'].trim()

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("~~ Add certs to Key DB       ~~")
    println("~~ Parameters                ~~")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

    println("MQ home           : " + mqHome)
    println("Key DB location   : " + keyDBLocation)
    println("Certificate file  : " + certificateFile)
    println("Label             : " + label)

    println("--------------------------------------------------------------------")

    def options = " -cert -add"
    options += " -db " + keyDBLocation
    options += " -pw " + keyDBPassword
    options += " -file " + certificateFile
    options += " -label " + " '"  + label + "'"

    def commandResult = runCommand(apTool.isWindows,  "runmqakm", mqHome, options, "Add root and intermediate certificates to the key DB")

    println("--------------------------------------------------------------------")
    println(commandResult)
    println(commandOutput)
  }

  def receiveCertificate() {
    def mqHome = props['mqHome'].trim()
    def keyDBLocation = props['keyDBLocation'].trim()
    def keyDBPassword = props['keyDBPassword'].trim()
    def certificateFile = props['certificateFile'].trim()

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("~~ Receive certificates      ~~")
    println("~~ Parameters                ~~")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

    println("MQ home           : " + mqHome)
    println("Key DB location   : " + keyDBLocation)
    println("Certificate file  : " + certificateFile)

    println("--------------------------------------------------------------------")

    def options = " -cert -receive"
    options += " -db " + keyDBLocation
    options += " -pw " + keyDBPassword
    options += " -file " + certificateFile

    def commandResult = runCommand(apTool.isWindows,  "runmqakm", mqHome, options, "List certificates in key DB")

    println("--------------------------------------------------------------------")
    println(commandResult)
    println(commandOutput)

  }

  def listCertificates() {
    def mqHome = props['mqHome'].trim()
    def keyDBLocation = props['keyDBLocation'].trim()
    def keyDBPassword = props['keyDBPassword'].trim()
    def type = props['type'].trim()
    def details = props['details'].trim()

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("~~ List certificates         ~~")
    println("~~ Parameters                ~~")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

    if (details == "") {
      details = "false"
    }

    println("MQ home                    : " + mqHome)
    println("Key DB location            : " + keyDBLocation)
    println("Type of certs to be listed : " + type)

    println("--------------------------------------------------------------------")

    def options = " -cert -list"
    options += " " + type
    options += " -db " + keyDBLocation
    options += " -pw " + keyDBPassword

    def commandResult = runCommand(apTool.isWindows,  "runmqakm", mqHome, options, "List certificates in key DB")

    println("--------------------------------------------------------------------")
    println(commandResult)
    println(commandOutput)

    if (commandOutput.contains("No certificates were found.")) {
      println("Certificate database is empty")
    }
    def splitCommandOutput = commandOutput.split("\n")

    if (splitCommandOutput.size() > 2) {
      for (def counter = 2; counter < splitCommandOutput.size(); counter++) {
        def certName = splitCommandOutput[counter]
        if (certName.startsWith("-") || certName.startsWith("!") || certName.startsWith("*") || certName.startsWith("#")) {
          certName = certName.substring(1, certName.length())
          certName = certName.trim()
        }
        certName = certName.replaceAll('\"','\'')
        println(certName)
        if (details == "true") {
          options = " -cert -details"
          options += " -db " + keyDBLocation
          options += " -pw " + keyDBPassword
          options += " -label " + certName
          def commandResultDetails = runCommand(apTool.isWindows,  "runmqakm", mqHome, options, "List certificates in key DB")
          println("--------------------------------------------------------------------")
          println(commandResult)
          println(commandOutput)
        }
      }
    }
  }

  private Integer runCommand(def isWindows, String executable, String home, def options, String message) {

    Integer returnCode = 0
    def command = home + "/" + executable + options

    println(command)
    commandOutput = command.execute().text
    return(returnCode)
  }
}
