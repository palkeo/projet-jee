pack:
	cp rapport/rapport.pdf .
	tar cf ahlouche-arthaud-auguste-carton.tgz \
	    rapport.pdf \
	    build.gradle README.md settings.gradle \
	    src/backend/*.java src/backend/games/*.java \
	    src/frontend/java/ \
	    src/frontend/build.gradle \
	    src/frontend/resources/application.properties \
	    src/frontend/resources/messages.properties \
	    src/frontend/resources/templates \
	    src/frontend/webapp/css/* \
	    src/frontend/webapp/js/*
	rm rapport.pdf
