all: architecture.pdf

%.pdf: %.tex
	pdflatex $<

architecture.pdf: overview.pdf xml.pdf presentation.pdf activitytask.pdf

%.eps: %.dia
	dia -e $@ -t eps $<

%.pdf: %.eps
	ps2pdf -dEPSCrop $< $@
