# <center>Magic Video Games</center>

## <center>Overview</center>
<br>

* This project contains a Java Spring Boot RESTful backend API, which provides
<br> authenticated access to categories, products, and user-specific shopping carts.
<br> All data persists within a MySQL database hosted locally.
  * The focus of this project is on backend logic, interacting with the database, and API design. 
  <br> The frontend serves to help visualize REST endpoints, and how we use them.
<br>
<br>
## <center>How to run the backend</center>
* In the backend-api directory, navigate to src/main/java/org/yearup
<br> From the EasyShopApplication class, you can start the backend API.
![backendpathway.png](backend-api/src/main/resources/backendpathway.png)
<br>
<br>
## <center>How to run the frontend</center>
* In the frontend-ui directory, you will find index.html
<br> From the top right corner of that file, 
<br> you can open the frontend UI in a browser of your choice.
<br> Note: The backend API must be running.
![openwithbrowser.png](backend-api/src/main/resources/openwithbrowser.png)
<br>
<br>
## Key API features:
  * User login and registration
  * JSON web tokens are required for protected endpoints
  * Retrieve available categories and products straight from the database
  * Filter results based on various values, such as pricing, category, and subcategory
  * Retrieve the current user's shopping cart, and add or remove products
  * Returns appropriate HTTP response status codes for supported requests
  * Database information is stored in the application.properties file
<br>
<br>
* API endpoints were tested using Insomnia
![insomniatests.png](backend-api/src/main/resources/insomniatests.png)
<br>
<br>
## How it works:
* Controllers listen for requests at specific endpoints
![controllerexample.png](backend-api/src/main/resources/controllerexample.png)
<br>
* Once those specific endpoints receive a request, the controller will call a DAO to query the database
![responsemethodexample.png](backend-api/src/main/resources/responsemethodexample.png)
<br>
* The DAO will then use secure PreparedStatements to obtain the results
![daomethodexample.png](backend-api/src/main/resources/daomethodexample.png)