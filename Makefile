pack:
	cp rapport/rapport.pdf .
	tar cf ahlouche-arthaud-auguste-carton.tgz \
	    rapport.pdf \
	    build.gradle LICENSE README.md settings.gradle \
	    src/backend/*.java src/backend/games/*.java \
		src/backend/build.gradle \
	    src/frontend/java/ \
	    src/frontend/build.gradle \
	    src/frontend/resources/ \
	    src/frontend/webapp/css/* \
	    src/frontend/webapp/js/*
	rm rapport.pdf
