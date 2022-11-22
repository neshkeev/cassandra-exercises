# Abstract

The goal of this exercise is to get familiar with monitoring tools for Apache Cassandra

# Metrics

1. Download the current mcac version: `curl -LO https://github.com/datastax/metric-collector-for-apache-cassandra/releases/download/v0.3.1/datastax-mcac-agent-0.3.1.tar.gz`
1. Extract the archive: `tar -xf datastax-mcac-agent-0.3.1.tar.gz --transform "s,datastax-mcac-agent-0.3.1,mcac,"`
1. Start the containers: `docker compose up`
1. Wait for the containers to start
1. Navigate to grafana in webbrowser (replace `hostip` with a real value): `http://hostip:3000/`

# Stargate

Update your helm repo:
```bash
helm repo add k8ssandra https://helm.k8ssandra.io/stable
helm repo add jetstack https://charts.jetstack.io
helm repo update
```

Clone the `k8ssandra-operator` repo:
```bash
git clone https://github.com/k8ssandra/k8ssandra-operator.git
cd k8ssandra-operator
```

Apply the patch to fix the setup script:
```bash
git am ../patches/0001-Fix-setup-script.patch
```

Setup the single node cluster:
```bash
make NUM_CLUSTERS=1 create-kind-multicluster
```

Check podes:
```bash
kubectl get nodes
```

Now install the jetstack/cert-manager:
```
helm install cert-manager jetstack/cert-manager --namespace cert-manager --create-namespace --set installCRDs=true
```

Deploy namespace-scoped cassandra and create a `demo` cluster:

```bash
helm install k8ssandra-operator k8ssandra/k8ssandra-operator -n k8ssandra-operator --create-namespace --version 0.38.2
kubectl get pods -n k8ssandra-operator
kubectl apply -n k8ssandra-operator -f ../k8c1.yaml
```

Wait until the command will contain: `Cassandra Operator Progress:  Ready`
```bash
while true; do kubectl describe k8cs/demo -n k8ssandra-operator | grep 'Cassandra Operator Progress' ; sleep 5 ; clear ; done
```

Extract credentials:
```bash
CASS_USERNAME=$(kubectl get secret demo-superuser -n k8ssandra-operator -o=jsonpath='{.data.username}' | base64 --decode)
CASS_PASSWORD=$(kubectl get secret demo-superuser -n k8ssandra-operator -o=jsonpath='{.data.password}' | base64 --decode)
echo "$CASS_USERNAME:$CASS_PASSWORD"
```

Check the cluster's status:
```
kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- nodetool -u $CASS_USERNAME -pw $CASS_PASSWORD status
```

Setup the schema:
```bash
kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- cqlsh -u $CASS_USERNAME -p $CASS_PASSWORD -e "CREATE KEYSPACE test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};"

kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- cqlsh -u $CASS_USERNAME -p $CASS_PASSWORD  -e "CREATE TABLE test.users (email text primary key, name text, state text);"

kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- cqlsh -u $CASS_USERNAME -p $CASS_PASSWORD -e "insert into test.users (email, name, state) values ('john@gamil.com', 'John Smith', 'NC');"

kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- cqlsh -u $CASS_USERNAME -p $CASS_PASSWORD -e "insert into test.users (email, name, state) values ('sue@help.com', 'Sue Sas', 'CA');"

kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- cqlsh -u $CASS_USERNAME -p $CASS_PASSWORD -e "insert into test.users (email, name, state) values ('tom@yes.com', 'Tom and Jerry', 'NV');"

kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- cqlsh -u $CASS_USERNAME -p $CASS_PASSWORD -e "select * from test.users;"
```

Start a `cqlsh` session in `stargate`:
```bash
kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- /bin/bash
cqlsh -u demo-superuser -p GRJq8z3CmjQrBRndbqT3 demo-dc1-stargate-service
```

Enable the http interface of `stargate`
```bash
kubectl -n k8ssandra-operator port-forward svc/demo-dc1-stargate-service 18081:8081 18082:8082
```

In a new terminal:
```bash
mkdir caddy
cd caddy
curl -LO https://github.com/caddyserver/caddy/releases/download/v2.6.2/caddy_2.6.2_linux_amd64.tar.gz
tar -xf caddy_2.6.2_linux_amd64.tar.gz
./caddy reverse-proxy --from :8081 --to :18081
./caddy reverse-proxy --from :8082 --to :18082
```

Configure hosts:
```bash
ADMIN_HOST_IP_PORT=... ADMIN HOST PORT ....
HOST_IP_PORT=STARGATE HOST PORT
```

Setup `AUTH_TOKEN`:
```bash
curl -v -L -X POST "http://${ADMIN_HOST_IP_PORT}/v1/auth" -H 'Content-Type: application/json' --data-raw '{"username": "demo-superuser", "password": CASS_PASSWORD}'
AUTH_TOKEN=<VALUE FROM THE CURL ABOVE>
```

Create a new keyspace:
```bash
curl -s --location --request POST "http://${HOST_IP_PORT}/v2/schemas/keyspaces" \
--header "X-Cassandra-Token: $AUTH_TOKEN" \
--header 'Content-Type: application/json' \
--data '{
    "name": "my_keyspace_from_stargate"
}'
```

Get the created keyspace:
```bash
curl -s -L -X GET "http://${HOST_IP_PORT}/v2/schemas/keyspaces/my_keyspace_from_stargate" \
-H "X-Cassandra-Token: $AUTH_TOKEN" \
-H "Content-Type: application/json" \
-H "Accept: application/json" | json_pp
```

Create a table:
```bash
curl -s --location \
--request POST "http://${HOST_IP_PORT}/v2/schemas/keyspaces/my_keyspace_from_stargate/tables" \
--header "X-Cassandra-Token: $AUTH_TOKEN" \
--header "Content-Type: application/json" \
--header "Accept: application/json" \
--data '{
	"name": "users",
	"columnDefinitions":
	  [
        {
	      "name": "firstname",
	      "typeDefinition": "text"
	    },
        {
	      "name": "email",
	      "typeDefinition": "text"
	    },
        {
	      "name": "lastname",
	      "typeDefinition": "text"
	    },
        {
	      "name": "favorite color",
	      "typeDefinition": "text"
	    }
	  ],
	"primaryKey":
	  {
	    "partitionKey": ["firstname"],
	    "clusteringKey": ["lastname"]
	  },
	"tableOptions":
	  {
	    "defaultTimeToLive": 0,
	    "clusteringExpression":
	      [{ "column": "lastname", "order": "ASC" }]
	  }
}'
```

Get the created table:
```bash
curl -s -L -X GET "http://${HOST_IP_PORT}/v2/schemas/keyspaces/my_keyspace_from_stargate/tables" \
-H "X-Cassandra-Token: $AUTH_TOKEN" \
-H "Content-Type: application/json" \
-H "Accept: application/json" | json_pp
```

Create a row in the `user` table:
```bash
curl -s --location --request POST "http://${HOST_IP_PORT}/v2/keyspaces/my_keyspace_from_stargate/users" \
--header "X-Cassandra-Token: $AUTH_TOKEN" \
--header 'Content-Type: application/json' \
--data '{
    "firstname": "Mookie",
    "lastname": "Betts",
    "email": "mookie.betts@gmail.com",
    "favorite color": "blue"
}'
```

Create a row in the `user` table:
```bash
curl -s --location --request POST "http://${HOST_IP_PORT}/v2/keyspaces/my_keyspace_from_stargate/users" \
--header "X-Cassandra-Token: $AUTH_TOKEN" \
--header 'Content-Type: application/json' \
--data '{
    "firstname": "Janesha",
    "lastname": "Doesha",
    "email": "janesha.doesha@gmail.com",
    "favorite color": "grey"
}'
```

List rows from the `user` table:
```bash
curl -v -s -L -G "http://${HOST_IP_PORT}/v2/keyspaces/my_keyspace_from_stargate/users" \
   -H "X-Cassandra-Token: $AUTH_TOKEN" \
   -H "Content-Type: application/json" \
   -H "Accept: application/json" \
   --data-urlencode 'where={"firstname": {"$in": ["Janesha","Mookie"]}}' | json_pp
```
