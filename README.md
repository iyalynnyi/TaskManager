# Task Manager API

## Overview

The Task Manager API is a RESTful web service designed to manage tasks effectively. It provides endpoints for creating, retrieving, updating, and deleting tasks, making it easy for users to organize their work. Built with Spring Boot, the API utilizes a Kafka message broker for task notifications, ensuring efficient communication and processing.

## Features

- **Create Tasks**: Add new tasks with details like title, description, priority, assignee, and due date.
- **Retrieve Tasks**: Fetch a list of all tasks or specific tasks by ID.
- **Update Tasks**: Modify existing tasks, including changing their status and updating their details.
- **Delete Tasks**: Remove tasks from the system.
- **Kafka Integration**: Send notifications to a Kafka topic when tasks are created, allowing for real-time processing.
- **Failover database**: The application has H2 as main database and PostgreSql as failover database.

### Failover Database Strategy

In this project, a custom approach is utilized for the failover database through a **Repository Management Layer**. This layer first attempts to interact with the primary database. If any issues occur, it seamlessly switches to the failover database. For reading data, the system queries both databases to ensure consistency.

#### Alternative Approaches

- **AbstractRoutingDataSource**: This Spring technology automatically switches between the primary and failover databases. The advantage of this approach is that it eliminates the need to create multiple repositories or add extra logic. However, a significant drawback is that if some data is saved in the failover database while the primary database is down, that data may become inaccessible once the primary database is back online. This is because `AbstractRoutingDataSource` will default to reading from the primary database.

- **SymmetricDS**: This is a technology that automatically manages synchronization between two different databases. Unfortunately, I did not have sufficient time to implement this solution. 😅


## Installation

To run the Task Manager API locally, follow these steps:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/iyalynnyi/TaskManager.git
   cd TaskManager
   ```
2. **Make sure you have Docker installed and running**

3. **Build and run using `start.sh` script**
   ```bash
   sh start.sh
   ```

4. **Access the application:**
   The application will be running at `http://localhost:8080`.

## API Usage

### Base URL

The base URL for the API is:
http://localhost:8080/api/v1/tasks

### Endpoints

#### 1. Get All Tasks

- **URL**: `/api/v1/tasks`
- **Method**: `GET`
- **Response**: Returns a list of tasks in JSON format.

**Example Request**:

```bash
curl -X GET http://localhost:8080/api/v1/tasks
```

#### 2. Create New Task

- **URL**: `/api/v1/tasks`
- **Method**: `POST`
- **Request Body**: A JSON object representing the task details.
- **Response**: Returns an id of created task or an error.


**Example Request**:

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
-H "Content-Type: application/json" \
-d '{
  "title": "Sample Task",
  "description": "This is a sample task.",
  "assignee": "John Doe",
  "reporter": "Jane Doe",
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "dueDate": "2024-12-31T12:00:00"
}'
```

#### 3. Update A Task

- **URL**: `/api/v1/tasks/{id}`
- **Method**: `PATCH`
- **Request Body**: A JSON object representing the task details.
- **Response**: Returns a confirmation message or error.


**Example Request**:

```bash
curl -X POST http://localhost:8080/api/v1/tasks/1 \
-H "Content-Type: application/json" \
-d '{
  "description": "New description.",
  "status": "DONE",
}'
```

#### 4. Delete A Task

- **URL**: `/api/v1/tasks/{id}`
- **Method**: `DELETE`
- **Response**: Returns a confirmation message or error.

**Example Request**:

```bash
curl -X DELETE http://localhost:8080/api/v1/tasks/1
```

#### 5. Update Task Status

- **URL**: `/api/v1/tasks/{id}`
- **Method**: `PUT`
- **Response**: Returns a confirmation message or error.
- **Query Parameter**: *status* - The new status for the task.

**Example Request**:

```bash
curl -X DELETE http://localhost:8080/api/v1/tasks/1?status=DONE
```