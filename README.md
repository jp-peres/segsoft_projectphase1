# Authenticator Framework Project



## How to install?

  **1.** Copy [webapps](./webapps), [.keystore](./.keystore) and [conf](./conf) folders to Tomcat main folder. **(In case you want to keep your [conf/server.xml](./conf/server.xml) file intact do a backup before overwritting).**
  
  **2.** Edit the port from [scripts/script.json](./scripts/script.js) to your current mongoDB port. Do the same on [webapps/authenticator/config.json](./webapps/authenticator/config.json).

  - Example [script.json](./scripts/script.js) port change:

    - `db = connect("localhost:MYMONGODBPORT/demo");`

  **3.** Open command prompt and type mongo to open mongo. **(In case environment variable isnt set check troubleshooting section bellow).**

  **4.** After opening mongo type the following command to load the script to make the initial DB with the root account:
  
  - `load("scriptsFolderPath/`[`script.js`](./scripts/script.js)`")`
  
  **5.** Run tomcat and **access the webapp via localhost:8181(or your defined port)/authenticator** to be redirected to the login page.

## Login credentials

  - The root account credentials are the following:

    - Username: root
    - Password: lcaires1

## Troubleshooting

Prob: I can't run mongo on my command line/shell and I have MongoDB installed.

|Platform|Solution|
|:--|:--|
|Windows|Make sure that the bin folder of MongoDB is in the Path of Environment Variables|
|Linux|If you have installed it but the execution of mongo halts, make sure that [these steps](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/) were followed correctly and if the problem persists try getting help from [here](https://docs.mongodb.com/manual/reference/installation-ubuntu-community-troubleshooting/).|


