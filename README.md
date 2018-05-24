# IBM UrbanCode Deploy - MQ Keys and Certificates Plug-in
---

### License
This plugin is protected under the [Eclipse Public 1.0 License](http://www.eclipse.org/legal/epl-v10.html)

### Documentation
See the associated pdf file for detailed information on how to use the plugin steps and how to assemble the steps into useful UrbanCode processes.

### Compatibility
	This plug-in requires version 6.1.1 or later of IBM UrbanCode Deploy.

### Installation
	The packaged zip is located in the releases folder. No special steps are required for installation. See Installing plug-ins in UrbanCode Deploy. Download this zip file if you wish to skip the manual build step. Otherwise, download the entire MQKeysandCertificates-UCD project and run the `gradle` command in the top level folder. This should compile the code and create 	a new distributable zip within the `build/distributions` folder. Use this command if you wish to make your own changes to the plugin.

### History
    Version 3
        - Community GitHub Release

### Available steps
- **Add certs to Key DB**: Add certificates from a certificate file to the key database. Used for the certificate chain.
- **Backup key DB**: Backup a key database.
- **Create CSR from Key DB**: Create a certificate signing request from the key database. Note that this assumes an integration with Venafi to get policy details.
- **Create key DB**: Create a key database.
- **List certs in Key DB**: List certificates in the key database. Option to include all details too.
- **Receive certs to Key DB**: Receive certificates from a certificate file to the key database. Must be certificates previously requested with the step 'Create CSR from Key DB'.

### How to build the plugin from command line:

1. Navigate to the base folder of the project through command line.
2. Make sure that there is a build.gradle file in the root directory and execute the `gradle` command.
3. The built plugin is located at `build/distributions/MQKeysandCertificates-UCD-vdev.zip`
