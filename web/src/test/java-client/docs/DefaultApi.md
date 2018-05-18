# DefaultApi

All URIs are relative to *https://ctd2-dashboard.nci.nih.gov/dashboard-api*

Method | HTTP request | Description
------------- | ------------- | -------------
[**browse**](DefaultApi.md#browse) | **GET** /browse/{subjectClass}/{subjectName} | returns observations for a subject
[**centers**](DefaultApi.md#centers) | **GET** /centers | returns a list of centers
[**searchTermGet**](DefaultApi.md#searchTermGet) | **GET** /search/{term} | Search Dashboard
[**submission**](DefaultApi.md#submission) | **GET** /submission/{submissionId} | returns content of a submission


<a name="browse"></a>
# **browse**
> Subject browse(subjectClass, subjectName, center, role, tier, maximum)

returns observations for a subject

Returns a list observations associated with a subject

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


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
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **subjectClass** | **String**| The subject class | [enum: AnimalModel, CellSample, Compound, Gene, ShRna, TissueSample]
 **subjectName** | **String**| The name of the subject |
 **center** | [**List&lt;String&gt;**](String.md)| Restrict returned observations by a comma-separated list of center ids | [optional] [enum: Broad, CSHL, Columbia, DFCI, Emory, FHCR1, FHCR2, Stanford, TGRI, UCSD, UCSF1, UCSF2, UTMDA, UTSW]
 **role** | [**List&lt;String&gt;**](String.md)| Restrict returned observations by a comma-separated list of roles | [optional]
 **tier** | [**List&lt;Integer&gt;**](Integer.md)| Restrict returned observations by tier(s) | [optional]
 **maximum** | **Integer**| The maximum number of observations returned by the query (if not specified, all observations are returned) | [optional]

### Return type

[**Subject**](Subject.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="centers"></a>
# **centers**
> List&lt;InlineResponse200&gt; centers()

returns a list of centers

Returns a list of centers and their submissions

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
try {
    List<InlineResponse200> result = apiInstance.centers();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#centers");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;InlineResponse200&gt;**](InlineResponse200.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="searchTermGet"></a>
# **searchTermGet**
> List&lt;Subject&gt; searchTermGet(term, center, role, tier, maximum)

Search Dashboard

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String term = "term_example"; // String | The search term
List<String> center = Arrays.asList("center_example"); // List<String> | Restrict returned observations by a comma-separated list of center ids
List<String> role = Arrays.asList("role_example"); // List<String> | Restrict returned observations by a comma-separated list of roles
List<Integer> tier = Arrays.asList(56); // List<Integer> | Restrict returned observations by tier(s)
Integer maximum = 56; // Integer | The maximum number of observations returned by the query (if not specified, all observations are returned)
try {
    List<Subject> result = apiInstance.searchTermGet(term, center, role, tier, maximum);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#searchTermGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **term** | **String**| The search term |
 **center** | [**List&lt;String&gt;**](String.md)| Restrict returned observations by a comma-separated list of center ids | [optional] [enum: Broad, CSHL, Columbia, DFCI, Emory, FHCR1, FHCR2, Stanford, TGRI, UCSD, UCSF1, UCSF2, UTMDA, UTSW]
 **role** | [**List&lt;String&gt;**](String.md)| Restrict returned observations by a comma-separated list of roles | [optional]
 **tier** | [**List&lt;Integer&gt;**](Integer.md)| Restrict returned observations by tier(s) | [optional]
 **maximum** | **Integer**| The maximum number of observations returned by the query (if not specified, all observations are returned) | [optional]

### Return type

[**List&lt;Subject&gt;**](Subject.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="submission"></a>
# **submission**
> InlineResponse2001 submission(submissionId, maximum)

returns content of a submission

Returns a submission description and a list observations in a submission

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String submissionId = "submissionId_example"; // String | The name of the requested submission
Integer maximum = 56; // Integer | The maximum number of observations returned by the query (if not specified, all observations are returned)
try {
    InlineResponse2001 result = apiInstance.submission(submissionId, maximum);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#submission");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **submissionId** | **String**| The name of the requested submission |
 **maximum** | **Integer**| The maximum number of observations returned by the query (if not specified, all observations are returned) | [optional]

### Return type

[**InlineResponse2001**](InlineResponse2001.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

