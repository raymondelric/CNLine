all: calls ui packet cli

calls: 
	javac client/calls/*.java

ui:
	javac client/ui/*.java

packet:
	javac client/packet/*.java

cli:
	javac client/*.java

run:
	java -cp . client/Client

runserver:
	./server/server 5566

runjsserver:
	node server/server.js

clean:
	rm -f .*~ *.class
	rm -f client/.*~ client/*.class
	rm -f client/ui/.*~ client/ui/*.class
	rm -f client/calls/.*~ client/calls/*.class
	rm -f client/packet/.*~ client/calls/*.class
	rm -f server/.*~
