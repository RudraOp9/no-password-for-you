<h1 align="center"<br /><a href='https://github.com/RudraOp9/no-password-for-you/releases/tag/alpha-0.0.02'> check out alpha-0.0.02 release !!!</h1>
<p align = "center"><kbd> <img src="https://i.ibb.co/3fm3kt1/a694367e-4127-4a0b-82ec-b5ccfa336a7d.png" height = "200"  ></p>
</kbd>
<h1 align="center">No Password For You</h1>
<p align="center">No password for you is a lightweight, fast and open-source custom secure password generator written in java</p>


<h1 align="center"<br /><a href='https://github.com/RudraOp9/no-password-for-you/blob/master/Samples.md'>Samples</h1>

---

<h1>Database Rules </h1>

    service cloud.firestore {
 	 match /databases/{database}/documents {


- Restrict access to all top-level collections
- Allow access only to documents where the ID matches the user's UID
- Allow access to subcollections and documents within the user's document
	
	   match /{collection}/{document}/{subcollection}/{subdocument} {
   		 allow read : if request.auth != null && request.auth.uid  == document; // document = UID
	 
     	 allow update, delete: if request.auth != null && request.auth.uid == document;
    	 allow create: if request.auth != null && request.auth.uid == document;
	   }
	  }
	  }
	   

   
---

<h1>🔐 Overview</h1>


- Lightweight  4 Mb
- No major permissions needed !
- Very fast and secured !
- Store to cloud
- Secured by RSA algorithm
- Generate custom highly configured passwords
- interactive ui

---

<h1>🛣️ Roadmap for No password for you</h1>

- Allowing users to customize Password.✅✅✅
- Add feature to secure password on cloud.✅✅❌ (Testing needed)
- Material Design ✅❌❌
- Add themes. ❌❌❌
- Migrate to kotlin.❌❌❌
- Adding security to app.✅✅❌
- Upload it to Google Play Store. ❌❌❌
- And most importantly, learn more about it ! ✅

---

# Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

**Check out Roadmap to see this repository's future goals.**

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


---

<h1>💬 Contact Me</h1>
<p>Email = rudrapratapsinhchauhan1@gmail.com </p>

<p>Telegram = t.me/LeoOnRide </p>

---

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
