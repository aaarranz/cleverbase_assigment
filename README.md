# CLEVERBASE ASSIGMENT


### PRE REQUISITES

- Docker installed
- If no sbt installed, the jar is pre-compiled and uploaded in the github repository

### INSTALLATION 

From root project directory run:

$ sbt assembly

$ sudo docker build -t cleverbase-assigment:0.1 .

$ sudo docker run -it -e POSTGRES_HOST_AUTH_METHOD=trust cleverbase-assigment:0.1

### ACCESS

- All endpoint are GET methods for easy test from a browser and implementation
- Credentials like basicAuth, tokens or session cookies are send by query params for the easy test and implementation

- When the docker is launched and initial script is run for the example data input:

  init-data-script.sh

- When the docker is launched the root token is printed in the stdout:

...

 --- Admin Token: admin1234 --- 

...

Use example: 

- The first user needs to know the root token which implies to have deployment permissions and access to in this case "the docker"

http://172.17.0.2:9090/authenticate?userid=user1&password=pass1&token=admin1234
LOGGED!! Session id: 8d54d570-7ede-44c9-89fe-b10a00df4745

http://172.17.0.2:9090/authenticate?userid=user2&password=pass2&token=notyet
Authentication error

http://172.17.0.2:9090/grantAuthentication?userid=user1&sessionid=8d54d570-7ede-44c9-89fe-b10a00df4745&mate=user2
Generated token: 9540a004-5654-4237-9f07-e7a2459eaba2

http://172.17.0.2:9090/authenticate?userid=user2&password=pass2&token=9540a004-5654-4237-9f07-e7a2459eaba2
LOGGED!! Session id: 22e3d5e6-c167-448a-8e3c-8ad9fb3f702b


