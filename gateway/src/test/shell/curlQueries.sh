curl -X POST -H 'Content-Type: application/json' http://localhost:8080/v1.0/scoring/create -d "$(< src/test/json/loanContract.json)"
curl -X GET -H 'Content-Type: application/json' http://localhost:8080/v1.0/scoring/findById/gerogi

docker run -it --rm --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 quay.io/debezium/zookeeper:1.9
docker run -it --rm --name kafka -p 9092:9092 --link zookeeper:zookeeper quay.io/debezium/kafka:1.9