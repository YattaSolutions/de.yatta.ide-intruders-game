# SoftwareVendor Test Application

Demo applications for https://stage.platform.yatta.de/

Profile: https://www.yatta.de/profiles/hub/YkxZ

Update Site: https://cdn.yatta.de/stage/update/softwarevendor

Installer: https://cdn.yatta.de/update/softwarevendor/0.0.1/softwarevendor.exe

## Build

For stage we can use the default target definition in `de.yatta.softwarevendor.demo\de.yatta.softwarevendor.demo.client\softwarevendor-target.target`

```bash
pushd de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.releng
mvn package
popd

pushd de.yatta.softwarevendor.demo/de.yatta.softwarevendor.demo.site/target/repository
rsync -n --delete -v -a ./* push-33.cdn77.com:/www/stage/update/softwarevendor/
popd
```

To build against a local LicenseClient you have to change the repository tag in the `*.target`
```xml
			<!-- for stage -->
			<!-- <repository location="http://cdn.yatta.de/stage/update/marketplace" /> -->
			<!-- for local -->
			<repository location="file:/C:/.../platform-server/marketplace/com.yattasolutions.platform.marketplace.client.site/target/repository" />
```

The local update site of the licensing client has to built like this beforehand:
```bash
cd ${workspace}/platform-server/marketplace
mvn package -DskipTests=true -Dbuild.stage=dev -Dinclude.client=nosandbox -Dexclude.server=true -Denvironment=dev -Dhostname.backend="http://localhost:8013" -Dhostname.frontend="http://localhost:4300" -Dhostname.account="http://localhost:5000" --batch-mode
 ```
