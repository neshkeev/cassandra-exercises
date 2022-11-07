# Abstract

The goal of this exercise is to get familiar with monitoring tools for Apache Cassandra

# Preparation

1. Download the current mcac version: `curl -LO https://github.com/datastax/metric-collector-for-apache-cassandra/releases/download/v0.3.1/datastax-mcac-agent-0.3.1.tar.gz`
1. Extract the archive: `tar -xf datastax-mcac-agent-0.3.1.tar.gz --transform "s,datastax-mcac-agent-0.3.1,mcac,"`
1. Start the containers: `docker compose up`
1. Wait for the containers to start
1. Navigate to grafana in webbrowser: `http://localhost:3000/`