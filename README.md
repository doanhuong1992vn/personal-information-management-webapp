# liquibase-demo
# First, to run the program, make sure you have Docker Desktop installed and running it
# You can download Docker Desktop here: https://www.docker.com/products/docker-desktop/
# Then perform the following steps:
- Step 1: Standing at the root directory of the project, move to the environment/liquibase-demo directory by executing the command "cd user-service/src/main/resources/environment/liquibase-demo" in command-line
- Step 2: Executing the command "docker compose up -d" in command-line
- Step 3: Access to "http://localhost:5050" in your browser and login to Pgadmin4 by email "huong.d@webglsoft.com" and password "123"
- Step 4: After successfully logging in, click on "Add New Server"
- Step 5: 
      + In General tab, enter your name in Name field
      + In Connection tab, enter the following information:
        Host name/address: db
        Port: 5432
        Maintenance database: postgres
        Username: root
        Password: 123
- Step 6: After successfully creating server with your name, let create database "liquibase_demo"
- ![step6a.png](docs/img/step6a.png)![step6b.png](docs/img/step6b.png)

