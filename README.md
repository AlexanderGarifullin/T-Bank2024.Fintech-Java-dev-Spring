# KudaGo CRUD API with Logging

This project implements a simple Spring Boot application that provides RESTful APIs for managing categories and locations, leveraging the data from the KudaGo API. It includes logging for method execution time and initialization processes.

## Features

- CRUD operations for two entities:
  - **Categories**
    - GET all categories: `/api/v1/places/categories`
    - GET a category by ID: `/api/v1/places/categories/{id}`
    - POST a new category: `/api/v1/places/categories`
    - PUT to update a category by ID: `/api/v1/places/categories/{id}`
    - DELETE a category by ID: `/api/v1/places/categories/{id}`
  
  - **Locations**
    - GET all locations: `/api/v1/locations`
    - GET a location by slug: `/api/v1/locations/{slug}`
    - POST a new location: `/api/v1/locations`
    - PUT to update a location by slug: `/api/v1/locations/{slug}`
    - DELETE a location by slug: `/api/v1/locations/{slug}`

- Logging of method execution times using a custom annotation.
- Automatic initialization of data sources by querying the [KudaGo API](https://docs.kudago.com/api/#) on application startup.

## Requirements

- Java 21
- Gradle

## Setup

To run the application, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/your-repo-name.git
   cd your-repo-name
   ```
2. Build the application:
   ```bash
   ./gradlew build
   ```
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
