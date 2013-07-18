all: addis1-architecture.pdf addis2-architecture.pdf

%.pdf: %.tex
	pdflatex $<
	pdflatex $<

addis1-architecture.pdf: overview.pdf xml.pdf presentation.pdf activitytask.pdf

addis2-architecture.pdf: future.pdf

%.eps: %.dia
	dia -e $@ -t eps $<

%.pdf: %.eps
	ps2pdf -dEPSCrop $< $@
