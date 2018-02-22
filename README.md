# holiday

RESTFul API for get next same holiday for two given country codes which come after the given date.
Using https://holidayapi.com API.

**Architecture Setup**
----

* **Prerequisite**

  * Java (support Java 7 and later)
  * Maven
  * Git
  
* **Run project**

  1) Clone project from repository: https://github.com/turytury/holiday.git
  2) Build project: mvn clean install
  3) Run project: mvn spring-boot:run

**API**
----
```
GET: http://localhost:8088/holiday/api/upcoming
```
  
* **Params Required:**
 
   * `date` with YYYY-MM-DD format
   * `countryCode1` support only country codes that are available in Holiday API.
   * `countryCode2` support only country codes that are available in Holiday API.

* **Success Response:**

    ```
    {
      "date": “2016-03-20”,
      "country1": ”Palm Sunday”,
      "country2": “Palmesøndag”
    }
    ```
    
    country1 and country2 will be replace by the given country codes
    
* **Error Response:**

    ```
    {
      "status": "400",
      "error": ”{Error message}”
    }
    ```
