What: data lake is a storage with large collection of file with any format (unstructured, structured, semi-structured). It is often used in big data analytics.

Why:

E.g:
    Amazon S3 - storage
    Amazon Glue - ETL (Extract Transform Load) service to help create schema, transform and load data to S3
    Amazon Athena - serverless interactive query service for data
    Amazon Redshift - distributed data warehouse

    Data pipeline in AWS:
        S3 -> Glue -> Athena
                   -> Redshift



1. Traditional Data Warehouse
	- Schema-on-write
	- Expensive
	- Not suitable for big data
	- Data only structured

2. Data Lake
	- Schema-on-read
	- Cheaper
	- Suitable for big data
	- Data can be unstructured, semi-structured, and structured

3. Data Lakehouse
	- Hybrid of data warehouse and data lake
	- Schema-on-read
	- Cheaper
	- Suitable for big data
	- Data can be unstructured, semi-structured, and structured
