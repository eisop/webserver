
all:
	if [ ! -d ./bin ] ; then mkdir bin ; fi
	find ./src -name "*.java" -print | xargs javac -Xlint:unchecked -cp javax.json-1.0.jar:snakeyaml-1.18.jar:${JAVA_HOME}/lib/tools.jar -d ./bin

clean:
	rm -rf ./bin/*
