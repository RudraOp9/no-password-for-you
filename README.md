
<p align = "center"> 
	<img alt="No Password for you logo"src="https://github.com/RudraOp9/no-password-for-you/blob/main-compose/app/src/main/ic_launcher-playstore.png" height = "200"> 
</p>

<p align="center">
	<img src="https://img.shields.io/badge/Kotlin-1.9.24-6750a3" alt="Kotlin">
	<a href="https://github.com/RudraOp9/no-password-for-you/releases"><img src="https://img.shields.io/badge/Download-Github_release-6750a3" alt="Download from github release"></a>
	<img src="https://img.shields.io/badge/Telegram-Community-279bd5?logo=telegram" alt="Compose for android">
</p>


## No Password For You
A secure, easy-to-use **password manager** for Android. Built with **Jetpack Compose** for a modern UI and Firebase Firestore for reliable data storage. Leverages **RSA** encryption for strong security.

## Build
### Build Locally 
- Download & setup Android studio
- Clone repo
- Go to firebase create new project and download `google-services.json` file & paste in `root/app` folder
- Run
#### or
[<img src="https://img.shields.io/badge/Github_release-6750a3" alt="Download from github release">](https://github.com/RudraOp9/no-password-for-you/releases)


## Database Rules

- Allow access only to documents where the ID matches the user's UID
- Allow access to subcollections and documents within the user's document
- Only allow Access to verified email users
``` 
service cloud.firestore {
  match /databases/{database}/documents {

    match /{collection}/{document}/{subcollection}/{subdocument} {

      allow read : if request.auth != null && request.auth.uid  == document && request.auth.token.email_verified; // document = UID
      allow update, delete: if request.auth != null && request.auth.uid == document && request.auth.token.email_verified;
      allow create: if request.auth != null && request.auth.uid == document && request.auth.token.email_verified;

    }
  }
}
```
## Features
- Generate passwords
- Save to cloud or device
- Manage Vault
- Import and Export Vault
- Manage Keys
- Dark & Light theme

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.
1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request

Or open an issue !!



## Contact Me
Email : work.ieo@outlook.com

Telegram : [LeoOnRide](https://tx.me/LeoOnRide)

# License


    Copyright 2024 RudraOp9
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
