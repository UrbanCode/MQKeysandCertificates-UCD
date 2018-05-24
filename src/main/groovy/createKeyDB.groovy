/**
 * (c) Copyright IBM Corporation 2017.
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import com.urbancode.air.AirPluginTool
import com.urbancode.air.MQKeysandCertificates.MQKeysandCertificatesHelper

MQKeysandCertificatesHelper helper = new MQKeysandCertificatesHelper(new AirPluginTool(this.args[0], this.args[1]))

helper.createKeyDB()
