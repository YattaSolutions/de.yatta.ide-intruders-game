# Integrating the Yatta Checkout for Eclipse

## Running the application

Start the launch configuration [SoftwareVendorTool.launch](de.yatta.softwarevendor.demo\de.yatta.softwarevendor.demo.client\SoftwareVendorTool.launch) either in run or debug mode.

## Building update site and the Rich Client Platform (RCP)

Build the [de.yatta.softwarevendor.demo.releng](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.releng) project:
```bash
pushd de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.releng
mvn clean package
popd
```
This will build the following artifacts:

### Eclipse Update Site
```
de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.site/target/repository/
```
The folder can be copied to an FTP server, or you can use it locally and specify it as update site inside an Eclipse installation.

### Eclipse RCP
```
de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.product/target/products/
```
The folder will include applications for Windows, MacOS and Linux. A Java Runtime Environment (JRE) is bundled with each application.

## Start selling your solution
[Become a vendor](https://www.yatta.de/portal) to integrate the Checkout into your own Eclipse product, tool or any other solution. For more information, read the [vendor documentation](https://www.yatta.de/docs).

---
If you have any questions, please [contact us](mailto:contact@yatta.de). We are happy to help.
