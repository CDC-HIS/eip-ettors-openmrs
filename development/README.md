# Development Environment Setup

This guide will walk you through the steps to configure your local environment and start the `eip-ettors-openmrs` service using Docker Compose.

## Prerequisites

Before you begin, ensure you have the following installed on your system:  
* Java JDK 17
* Maven
* Docker and Docker Compose
* IntelliJ IDEA (or your preferred IDE)

## Assumptions
This setup assumes you have the following services running:
* OpenMRS server (with the required modules)
* ETTORS API (via OpenHIM)
* MySQL database (for OpenMRS and EIP Management Database) - For EIP Management Database, you can use the `create-eip-ettors-openmrs.sql` file in the `development` directory to create database and user.

## Environment Configuration

1. **Clone the Repository:** Start by cloning the project repository to your local machine.  
   
   ```bash
   git clone https://github.com/CDC-HIS/eip-ettors-openmrs.git && cd eip-ettors-openmrs
   ```
2. **Configure Environment Variables:**  The project uses environment variables for configuration. Copy the `.env.example` file to a new file named `.env` in the development directory and update it with your local settings. 
   ```bash
   cp development/.env.example development/.env 
   ```
   Edit the `development/.env` file to match your local development environment settings, such as database credentials, OpenMRS server URL, and ETTORS API (OpenHIM) details.
3. **Build the Project:**  Use Maven to build the project. This step compiles the Java code and packages it into a JAR file.
    ```bash
    mvn clean install
    ```
4. **Start `eip-ettors-openmrs` service with Docker Compose:**  To navigate to development directory and start the service, run the following command.

    ```bash
    cd development && docker-compose up -d
    ```

   This command starts the `eip-ettors-openmrs` container, mounting the generated JAR file and applying the environment variables defined in the `.env` file.

5. **Verify the Setup:**  After starting the services, verify that the `eip-ettors-openmrs` is running correctly and able to connect to both OpenMRS and ETTORS (via OpenHIM). Check the Docker container logs for any errors.
    ```bash
    docker logs -f --tail=1000 ozone-eip-ettors-openmrs
    ```

## Development Workflow

1. **Code Changes:** Make changes to the codebase using IntelliJ IDEA or your preferred IDE.
2. **Testing:** Run tests locally using Maven to ensure your changes do not break existing functionality. `mvn test`
3. **Rebuild & Restart service:** 
   After making changes, rebuild the project and restart the Docker service to apply the changes. 
   ```bash 
   mvn clean install && docker-compose restart eip-ettors-openmrs
    ```
4. **Debugging:** Use the IDE's debugging tools to troubleshoot issues and step through the code.
