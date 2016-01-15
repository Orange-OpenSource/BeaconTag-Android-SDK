# BeaconTag-Android-SDK project for Android
-----------------------------

Welcome to BeaconTag-Android-SDK project for Android phones, designed to help you test the BeaconTag SDK.
BeaconTag-Android-SDK is an Android library with a sample app to set, configure and interact with the connected devices 'Orange Beacon Tag' by Orange.

Please start with the sample associated and edit the variables according to your use cases. The sample allows you to display messages on your phone in beacon's vicinity. Other use cases might be to trigger different phone actions, such as sending an SMS or automatically opening a mobile application. To discover more possible beacon's interactions with your phone, do not hesitate to test our mobile app: "Beacon Connect".

At this time, the downloadable projects are designed for use with Gradle and Android Studio.

We'd love to hear your feedback/comments.
Thank you in advance for your time.

Beacon Tag SDK:
---------------
Document revision 4 (SDK v 1.1.2)

-----------------------------------------------------------------------------------------
Legal notice:

ORANGE OFFERS NO WARRANTY EITHER EXPRESS OR IMPLIED INCLUDING THOSE OF MERCHANTABILITY,
NONINFRINGEMENT OF THIRD-PARTY INTELLECTUAL PROPERTY OR FITNESS FOR A PARTICULAR PURPOSE.
ORANGE SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER (INCLUDING, WITHOUT LIMITATION,
DAMAGES FOR LOSS OF BUSINESS PROFITS, BUSINESS INTERRUPTION, LOSS OF BUSINESS INFORMATION,
OR OTHER LOSS) ARISING OUT OF THE USE OF OR INABILITY TO USE THE SAMPLE CODE, 
EVEN IF ORANGE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

-----------------------------------------------------------------------------------------

Sample app: beaconTag_sdk_example
--------------------
This sample app offers you to configure your Orange Beacon Tag. 
To quickly be back on track you can just edit the code of 'BeaconTagSdkExampleApplication' 
with the UUID, major and minor of your Orange Beacon Tag (OBT). 
You can also configure the service you want to test (entering or leaving the Orange Beacon Tag area for example)
and edit its parameters (typically, the transmission power or the accelerometer value).

To have the configuration of your Orange Beacon Tag updated, you need to build and open the app
on a compatible smartphone. Then, press down the small button located at the back 
of your Orange Beacon Tag, putting it into configuration mode. During this configuration mode, 
a blue light will flash during a few moments and you should see the UUID of your OBT displayed on your phone screen. 
Then, you can test the service by moving it (if you chose the accelerometer mode), leaving or entering 
the OBT interaction zone or even changing the OBT triggered temperature. 


Library integration
--------------------
BeaconTag framework requires SDK level 18 or higher, and was created under Compile SDK Version 22. For proper functioning of this library you must follow the following steps:

1. Add library as a dependency.
2. Add `<service android:name="com.orange.beaconTag_sdk.ble.control.BLEDeviceScanner" android:enabled="true"/>` to your AndroidManifest.xml file.
3. In Application class initialize `BeaconMonitor` and pass it in Application Context.
4. To add a device for detection, pass `BeaconSettings` object to `registerForBeaconDetection` method of `BeaconMonitor`.
5. To change device settings, or event to occur, provide `registerForBeaconDetection` with new `BeaconSettings` object.

Add Beacon For Detection
--------------------
To trigger actions as you wish, the Orange Beacon Tag must be properly configured. After the device is configured, you can use it until the configuration is updated. In case you want to configure a device, you should proceed as follows:

To start detection first you need to create `BeaconSettings` object.
To create `BeaconSettings` object you need to provide constructor information following parameters:

- **UUID** 32-character hexadecimal UUID
- **Major** 0..65536 integer
- **Minor** 0..65536 integer
- **AreaSettings** Enum which describes different behaviors of tag detection handlers. Possible values:
*ENTER*, *EXIT*, *APPROACHING*, *LEAVING*. For more see Area Settings Section.

Most of these parameters can be found behind your Orange Beacon Tag. Do be careful of converting Major and Minor to decimal values.

You can also set different parameters with appropriate setters:

- **Sleep Delay** Sleep delay in seconds (ranges from 1 to 65535) A value of 0 disables sleeping.
After Orange Beacon Tag activation because of acceleration or temperature events, the device will 'advertise' for a given period of time, then it will be deactivated. If sleep delay is not within the provided range it will be set to 0.
By default is 0.

- **Acceleration** The Orange Beacon Tag is activated when its acceleration reaches a given value. The value range is between 0,1569064 m/s² and 156,9064 m/s², if it is not within range it will be ignored.
If no value is provided, the accelerometer will be disabled.

- **Tx Power** Orange Beacon Tag's transmitting power in dBm. If it is not set, current device value will be kept.
Allowed values: -62, -52, -48, -44, -40, -36, -32, -30, -20, -16, -12, -8, -4, 0, 4. 
If an invalid value is provided, it will be ignored.

- **Advertising Interval** Represents the Orange Beacon Tag's advertising interval in units of 625μs (ranging from 160 to 16000μs).
If invalid value is provided, it will be ignored.

- **Temperature** Set Orange Beacon Tag activation when the temperature is within the given range.
Upper boundary must be greater than or equal to lower boundary, otherwise boundaries will be ignored. Boundaries are inclusive. Both boundaries must lie between -20 and +50 to ensure a smooth behavior of BLE chip. If lower boundary is greater than upper, then both values will be ignored.

Area Settings
--------------------
Description of different behaviors of tag detection handlers.

* **ENTER** - event will occur within a few seconds after phone enters beacon area,
* **EXIT** - event will occur 30 seconds after phone leaves the Orange Beacon Tag area,
* **APPROACHING** - event will occur within a few seconds after the phone enters Immidiate and/or Near Orange Beacon Tag area,
* **LEAVING** - event will occur after the phone leaves Immidiate and/or Near the Orange Beacon Tag area within a few seconds if phone in Far area, or after 30 seconds, if the OBT is no longer visible for the phone.

Device Reconfiguration
--------------------
If an Orange Beacon Tag appears in configuration mode, it will be reconfigurated according to settings.

Event Propagation
--------------------
Once an event defined as an Area Settings occurs, LocalBroadcast with
`BeaconMonitor.TRIGGER_EVENT_ACTION` action will be sent, with a `DeviceFootprint` in extras under `BeaconMonitor.DEVICE_FOOTPRINT_TAG` key.

