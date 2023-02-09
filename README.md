# Integrating the Checkout for Eclipse

## About this repository

This repository contains a sample plug-in for the Eclipse IDE (https://eclipseide.org/) that allows you to test the integration of the Yatta Checkout for Eclipse in a secure demo environment.
The sample plug-in includes a mini game you can demo-purchase as a demo user. Use the Checkout to access the game.
No real payments or purchases are made in the demo.
You can use this demo plug-in to prototype the integration of your own Eclipse product, tool or any other solution.

## Open project in IDE

Checkout this repository and import the contained projects in an Eclipse IDE that has the Eclipse Plug-in Development Environment installed. You could e.g. use the "Eclipse IDE for Eclipse Committers": https://www.eclipse.org/downloads/packages/release/2022-12/r/eclipse-ide-eclipse-committers

You should then set the active target platform in Eclipse to [de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/softwarevendor-target.target](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/softwarevendor-target.target). The contained projects should afterwards compile without errors.

## Running the application

Start the launch configuration [SoftwareVendorTool.launch](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/SoftwareVendorTool.launch) either in run or debug mode.
Details about using the plug-in could be found in the plug-in's [Readme file](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.product/README.txt).

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

## Integrate Yatta Checkout to sell your product in Eclipse

Yatta Checkout for Eclipse is designed and built to help you monetize and sell products (e.g., plug-ins, frameworks, libraries, SaaS and more) to millions of Eclipse users—and provide developers access to better and more tooling.

To customize the demo to use your own solution, create a demo solution [here](https://www.yatta.de/portal). Copy the API Key of your newly created solution there and replace `SOLUTION_ID` in [`de.yatta.softwarevendor.demo.VendorDemoPlugin`](de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.client/src/de/yatta/softwarevendor/demo/VendorDemoPlugin.java) with that key. Launch the application and user your new solution to subscripe for the mini-game.

For more info about integration the Checkout for Eclipse read our [docs](https://www.yatta.de/docs) or visit our [website](https://www.yatta.de/checkout-for-eclipse).

---
Read our success story: https://www.yatta.de/thoughts/yatta-checkout-case-study

Any questions? Get in touch with us for answers,
  - Call +49 69 2475666-0
  - Email checkout@yatta.de
