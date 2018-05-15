# swagger-java-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-java-client</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/swagger-java-client-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;

import java.io.File;
import java.util.*;

public class DefaultApiExample {

    public static void main(String[] args) {
        
        DefaultApi apiInstance = new DefaultApi();
        String subjectClass = "subjectClass_example"; // String | The subject class
        String subjectName = "subjectName_example"; // String | The name of the subject
        List<String> center = Arrays.asList("center_example"); // List<String> | Restrict returned observations by a comma-separated list of center ids
        List<String> role = Arrays.asList("role_example"); // List<String> | Restrict returned observations by a comma-separated list of roles
        List<Integer> tier = Arrays.asList(56); // List<Integer> | Restrict returned observations by tier(s)
        Integer maximum = 56; // Integer | The maximum number of observations returned by the query (if not specified, all observations are returned)
        try {
            Subject result = apiInstance.browse(subjectClass, subjectName, center, role, tier, maximum);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#browse");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://ctd2-dashboard.nci.nih.gov/dashboard-api*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**browse**](docs/DefaultApi.md#browse) | **GET** /browse/{subjectClass}/{subjectName} | returns observations for a subject
*DefaultApi* | [**centers**](docs/DefaultApi.md#centers) | **GET** /centers | returns a list of centers
*DefaultApi* | [**searchTermGet**](docs/DefaultApi.md#searchTermGet) | **GET** /search/{term} | Search Dashboard
*DefaultApi* | [**submission**](docs/DefaultApi.md#submission) | **GET** /submission/{submissionId} | returns content of a submission


## Documentation for Models

 - [CentersSubmissions](docs/CentersSubmissions.md)
 - [InlineResponse200](docs/InlineResponse200.md)
 - [InlineResponse2001](docs/InlineResponse2001.md)
 - [Observation](docs/Observation.md)
 - [ObservationEvidenceList](docs/ObservationEvidenceList.md)
 - [ObservationSubjectList](docs/ObservationSubjectList.md)
 - [Subject](docs/Subject.md)
 - [SubjectObservationCount](docs/SubjectObservationCount.md)
 - [SubjectXref](docs/SubjectXref.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author

ocg@mail.nih.gov

