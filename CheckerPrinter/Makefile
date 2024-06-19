
all:
	if [ ! -d ./bin ] ; then mkdir bin ; fi
	find ./src -name "*.java" -print | xargs javac -Xlint:unchecked -cp .:javax.json-1.0.jar -d ./bin

clean:
	rm -rf ./bin/*
