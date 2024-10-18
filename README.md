On-Demand Logistics Platform: Capacity Estimation and Technical Architecture

Capacity Estimation

System User Capacity

	•	Registered Users: 50 million
	•	Average User Requests: 2 requests per week
	•	Weekly User Requests: 100,000,000
	•	Daily User Requests: ~14,285,714 (100,000,000 / 7)

Driver Capacity

	•	Registered Drivers: 100,000
	•	Average Driver Handling Capacity: 5 requests per day
	•	Monthly Driver Capacity: 15,000,000 requests
	•	Daily Driver Capacity: 500,000 requests

System Throughput

	•	Required System Throughput: 10,000 requests per second
	•	Requests per Day: 864,000,000
	•	Requests per Hour: 36,000,000
	•	Requests per Minute: 600,000

Infrastructure Requirements

Servers

	•	Requests per Server: 500 requests per second
	•	Number of Application Servers Required: 20 servers to handle 10,000 requests per second.

Database Capacity

	1.	Storage Capacity:
	•	User Data Size: 500,000 MB (50 million users * 10 KB per user)
	•	Driver Data Size: 1,000 MB (100,000 drivers * 10 KB per driver)
	•	Total Estimated Data Size: ~489.26 GB
	2.	Transaction Processing:
	•	Updates per Second: 250,000 (5% of 50 million users updating 5 times per second)
	•	Number of Database Servers Needed: 25,000 servers

CPU Size Calculation

	•	Total Requests per Second: 10,000
	•	Average Request Handling Time: 0.1 seconds
	•	Workload per Second: 1,000 request-seconds (10,000 * 0.1)
	•	Requests per CPU Core: 40 requests/second
	•	Total Required CPU Cores: 25 cores
	•	Cores per CPU: 8 cores
	•	Total CPUs Needed: 4 CPUs (rounded up)

Technical Decision: Apache Kafka and Redis

Real-Time Data Streaming

	•	Apache Kafka: Handles real-time data streaming, crucial for tracking GPS-equipped vehicle locations.
	•	Redis: Provides sub-millisecond response times for location data, enabling real-time access.

High Throughput and Low Latency

	•	Kafka: Fault-tolerant and scalable, designed to handle 100,000 drivers and 10,000 requests/second.
	•	Redis: Complements Kafka by caching real-time data for instant availability.

Event-Driven Architecture

Kafka

	•	Event-Driven Architecture: Enables handling real-time events like location updates, order status changes, and delivery confirmations.
	•	Scalability: Kafka and Redis can be distributed across multiple servers to manage high volumes of data and requests.

Redis

	•	Geospatial Capabilities: Supports efficient search for nearby vehicles, aiding in route optimization and finding the nearest available driver.
	•	Caching and Fast Data Access: Redis caches frequently accessed data such as vehicle locations, reducing primary database load.

Communication Protocol: Server-Sent Events (SSE)

	•	Unidirectional Communication: SSE provides server-to-client communication for real-time updates on vehicle locations and order statuses.
	•	Automatic Reconnection: SSE supports automatic reconnection, ideal for mobile environments with unstable networks.
	•	HTTP Compatibility: SSE operates over standard HTTP/HTTPS, making it easier to integrate with corporate environments.

High-Level Design

Assumptions

	1.	Drivers cannot cancel requests once received.
	2.	The system relies heavily on geolocation data for bookings, tracking, and driver locations.

Implementation Details

	1.	Java & Spring Boot: Backend built using Java and Spring Boot for a scalable and robust application.
	2.	Database Design: Comprehensive schema for users, drivers, vehicles, bookings, tracking, and payments.
	3.	Redis: Integrated for real-time caching and fast access to location data.
	4.	Kafka: Real-time data streaming for handling high-volume events like location updates.
	5.	Server-Sent Events (SSE): Real-time updates pushed to users, especially for vehicle locations.
	6.	API Design: RESTful API for user and driver management, bookings, real-time tracking, and pricing.
	7.	Real-Time Tracking: Combination of Redis, Kafka, and SSE for real-time vehicle tracking.
	8.	Booking Management: Comprehensive system handling bookings, updates, and cancellations.
	9.	Pricing System: Dynamic pricing system based on various factors like time, demand, and location.
	10.	Admin Dashboard: Features for driver management, system statistics, and pricing updates.
	11.	Vehicle Management: Vehicles are managed separately from drivers.
	12.	Scalability: Optimized to handle 10,000 requests per second using the designed architecture.
	13.	Error Handling & Logging: (Assumed to be implemented) Robust error handling and logging.

Can Be Implemented (Future Work)

These features are planned but not implemented due to time constraints (college project):

	1.	Payment Gateway: Integration for handling payments.
	2.	WebSockets: For real-time bi-directional communication.
	3.	Authentication: Secure authentication system.
	4.	Spring Security: For implementing security features.
	5.	Frontend Integration: Frontend development is done but not yet merged with the backend.
