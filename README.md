# Authenticator Framework Project



- ## How to install?

  **1.** Copy webapps, .keystore and conf folders to Tomcat main folder. **(In case you want to keep your conf/server.xml file intact do a backup before overwritting).**
  
  **2.** Edit the port from scripts/script.json to your current mongoDB port. Do the same on webapps/authenticator/config.json.

  - Example script.json port change:

    - ```db = connect("localhost:MYMONGODBPORT/demo");```

  **3.** Open command prompt and type mongo to open mongo. **(In case environment variable isnt set follow up on F.A.Q bellow).**

  **4.** After opening mongo type the following command to load the script to make the initial DB with the root account:
  
  - ```load("scriptsFolderPath/script.js")```
  
  **5.** Run tomcat and **access the webapp via localhost:8181(or your defined port)/authenticator** to be redirected to the login page.

- ## Login credentials

  - The root account credentials are the following:

    - Username: root
    - Password: lcaires1

- ## F.A.Q
