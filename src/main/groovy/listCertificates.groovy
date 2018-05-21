import com.urbancode.air.AirPluginTool
import com.urbancode.air.MQKeysandCertificates.MQKeysandCertificatesHelper

MQKeysandCertificatesHelper helper = new MQKeysandCertificatesHelper(new AirPluginTool(this.args[0], this.args[1]))

helper.listCertificates()
