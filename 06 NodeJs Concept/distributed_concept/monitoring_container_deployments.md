Observability & Monitoring

This provides a way to log beside traditional micro console.log to debug data. We have logging best practices for distributed system. Best practices:
	use structure JSON over plaintext for rich information
	use UDP for log to impose low latency. Trade of UDP with guarantee delivery and back pressure control
	Mark severity level for log

Mechanics: When having trouble or crash, nodejs automatically write to stdout via console.log() on native functions. Docker of host machine (need explicitly specify file log or use external pm lib to do this). Then we can use another service like Filebeat/Fluentd to send to log server over network 

ELK - ElasticSearch, Logstash, Kibana: keep central and process logs -> Indexed and text log
	L: Ingest and transform log from many services
	E: process full-text search by indexed text of all logs. Help query and see log text faster
	K: a dashboard UI for query indexes

StatsD, Graphite, and Grafana: get numeric metrics aggregation for performance, capacity, system health -> Numeric logs
	StatsD: Listen for incoming metrics over UDP (or TCP), receive log as string (adhere statsD format) and act. There are 3 types of 	act:
		counter: |c e.g “web-api.inbound.response_code.200:1|c”
		timer: |ms
		gauges: |g
	Graphite: play data source storage for dashboard, receive aggregate metrics from StatsD.
	Grafana: Dashboard that query time-series data from Graphite. Also have alert feature to notify engineers

Distributed request tracing - Zipkin and Open Telemetry

Health check & Probe
	Lagging (CPU):
	Socket/file/Memory leak

Containers

Deployments
