# QR Code Distance Calculator App

## Description of the Solution
This Android project consists of two Java classes: `MainActivity.java` and `ScanActivity.java`. The application enables users to scan QR codes and calculate the distance between their current location and a geo-tagged location encoded within the QR code.

### Key Functionalities

1. **Scanning QR Codes**:
   - The `ScanActivity` class utilizes the Google Mobile Vision library to capture and decode QR codes using the device's camera.

2. **Location Services**:
   - The app requests and uses the device's location data to calculate the distance between the user's location and the geo-location encoded in the QR code.

3. **User Interface**:
   - Both activities include user-friendly interfaces with buttons, text views, and a camera view for scanning QR codes.

---


### 2.1 Main Activity
- This is the initial screen displayed when the app is launched.
- Contains a button labeled **"Scan QR Code"**.

![alt text](<screen 1.png>)
---

### 2.2 Camera Permission Request
- After tapping the **"Scan QR Code"** button on the main screen, the app navigates to the **Scan Activity**.
- A dialog appears, requesting permission to access the device's camera.
- The user is prompted to grant or deny camera access.
![alt text](<screen 2.png>)
---

### 2.3 Location Permission Request
- After granting camera access, the user points the camera at a QR code.
- If the QR code contains a geo-location, the app detects it and requests access to the device's location.
- A second permission dialog appears, asking for location access.
![alt text](<screen 3.png>)
---

### 2.4 Distance Calculation
- Upon granting both camera and location permissions, the app scans the QR code containing the geo-location.
- It calculates the distance between the user's current location and the encoded geo-location.
- The calculated distance is displayed on the screen.
![alt text](<screen 4.png>)
---

### 2.5 Example QR Code
Below is an example QR code to test the app functionality:

![alt text](test_qr_code.png)
