1. Dependencies : 
	1.1 : Jackson Library   (http://www.java2s.com/Code/Jar/j/Downloadjacksonall190jar.htm)
	1.2	: Json.simple library  (http://www.java2s.com/Code/Jar/j/Downloadjsonsimple111jar.htm)
2. Instructions for building the code : 
	2.1 Create a Java project
	2.2 Add the dependent external libraries from step 1.
	2.3 Copy the .java files to "/src/" folder of the project.
	2.4 Build the project. (NOT built using Maven/Gradle)
3. Instructions for running the code : 
	3.1 Code takes upto 2 command line arguments:
		3.1.1 "c" or "C" in first argument runs the code to create and use Compressed Inverted List. Any other option uses Uncompressed version.
		3.2.2 Number of Results in Query Retrieval(k) : Default is 5. User can specify how many results to retrieve for a query. Numeric Input Required.

Note : 
1. For convenience, the 7 one term queries and 7 two-term queries are saved along with their retrieved results in "Query Results.txt" once the program is run. 
2. All files/maps will be created in the parent folder of the project. ("..//")