# Checkout for Eclipse demo plugin

## About this repository

This repository contains a sample plugin for the Eclipse IDE (https://eclipseide.org/) that allows you to test the integration of Yatta Checkout for Eclipse in a secure demo environment.
The sample plugin is a mini game you can demo-purchase as a demo user. Use the Checkout to access the game.
No real payments or purchases are made in the demo.
You can use this demo plugin to prototype the integration of your own Eclipse solution, tool or any other digital product.

## Open project in Eclipse IDE

Import this repository's projects into an Eclipse IDE with the Plug-in Development Environment installed. For example, the "Eclipse IDE for Eclipse Committers": https://www.eclipse.org/downloads/packages/release/2022-12/r/eclipse-ide-eclipse-committers

Set the IDE's active target platform to [de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/softwarevendor-target.target](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/softwarevendor-target.target). The projects should then compile without errors.

## Run the application

Start the launch configuration [SoftwareVendorTool.launch](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/SoftwareVendorTool.launch) either in run or debug mode.
More information on using the plugin can be found in its [README](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.product/README.txt).

## Build update site and Rich Client Platform (RCP)

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
The folder can be copied to an FTP server or you can use it locally and specify it as update site inside an Eclipse installation.

### Eclipse RCP
```
de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.product/target/products/
```
The folder includes applications for Windows, MacOS and Linux. A Java Runtime Environment (JRE) is bundled with each application.

## Integrate Yatta Checkout to sell your product in Eclipse

Yatta Checkout for Eclipse is designed and built to help you monetize and sell digital products (plugins, frameworks, libraries, SaaS and more) to millions of Eclipse users—and provide developers access to better and more tooling.

To customize the demo to use it for your own solution, create a demo solution [here](https://www.yatta.de/portal). Replace `SOLUTION_ID` in [`de.yatta.softwarevendor.demo.VendorDemoPlugin`](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/src/de/yatta/softwarevendor/demo/VendorDemoPlugin.java) with the API Key of your newly created solution. Launch the application and use your new solution to demo-purchase the mini-game.

For more info about integrating the Checkout for Eclipse, read our [docs](https://www.yatta.de/docs) or visit our [website](https://www.yatta.de/checkout-for-eclipse).

---
Read our success story: https://www.yatta.de/thoughts/yatta-checkout-case-study

Any questions? Get in touch with us for answers:
  - Call +49 69 2475666-0
  - Email checkout@yatta.de
