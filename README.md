# Integrating the Checkout for Eclipse

## About this repository

This repository contains a sample plugin for the Eclipse IDE (https://eclipseide.org/) that allows you to test the integration of the Yatta Checkout for Eclipse in a secure demo environment.
The sample plugin includes a mini game you can demo-purchase as a demo user. Use the Checkout to access the game.
No real payments or purchases are made in the demo.
You can use this demo plugin to prototype the integration of your own Eclipse product, tool or any other solution.

## Running the application

Start the launch configuration [SoftwareVendorTool.launch](de.yatta.softwarevendor.demo\de.yatta.softwarevendor.demo.client\SoftwareVendorTool.launch) either in run or debug mode. More details about using the plugin could be found in the plugin's [Readme file](de.yatta.softwarevendor.demo-product/README.txt).

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
If you have any questions, please [contact us](mailto:checkout@yatta.de). We are happy to help.
