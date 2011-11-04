ADDIS - Aggregate Data Drug Information System
==============================================

[ADDIS][1] is a software developed within the Dutch [Escher-project][2]
for managing and analyzing clinical trial information.

ADDIS comes with a built-in example modeled on the paper: [Hansen et
al., Annals of internal medicine, 143(6): 415-426, 2005][3]. All data
was extracted by us from the original papers.

Requirements
------------

ADDIS is made in [Java][4], and requires Java 6 (JRE 1.6) or newer. Most
modern operating systems ship with a suitable JRE.

Versions
--------

1.11: Add [BRAT table][10] to benefit/risk view.

1.10: Enable combination treatment. Add "free text" ('Other') activity.
   Allow multiple moments of measurement for each outcome. Add installer
   and double-click loading of .addis data files on Windows.

1.8: Input study design (time structure), allow missing measurements,
   generate summary of efficacy table according to EMA D80 template,
   support of primary/secondary endpoints.

1.6.2: Bugfix release

1.6.1: Performance improvements for network meta-analysis, rank
   probability table.

1.6: User interface improvements, risk-based sampling in MetaBR, new
   schema-based XML format, added note taking in study wizard.

1.4: Lynd & O'Brien benefit-risk analysis, corrected meta-analysis,
   network meta-analysis enhancements (assessment of convergence,
   variance parameters, node-splitting analysis, R-dump of results,
   memory management), tabbed views.

1.2.1: Bugfix release

1.2: Benefit-risk models based on a single study.

1.0: Welcome screen, limited editing, links to various information
   systems (PubMed, medicines.org.uk, whocc.no) and many minor
   improvements.

0.10: Benefit-risk analysis, baseline effect (heuristic) estimation

0.8.1: Faster network meta-analysis (updated library), minor fixes

0.8: Network meta-analysis, XML import/export

0.6: Added add-study-wizard, import from ClinicalTrials.gov

0.4.1: Fixed example data (studies Org 022 and 023), changed domain
   to be version-specific.

0.4: Added meta-analysis, removed combined studies and MCDA-hookup.
   Lots of minor changes.

0.2: Initial release.

Building ADDIS from source
--------------------------

All releases as well as a nightly build (see [the DrugIS website][5])
are distributed in a compiled format. However, we also provide source
for external development / verification. The sources can be checked out
of the [ADDIS Git repository][6].

In order to compile ADDIS, you need:

 - Java >= 1.6
 - [Maven 2][7]
 - Other dependencies should be downloaded automatically by Maven2

Building is automatic with "mvn package".

License
-------

ADDIS is open source, and licensed under [GPLv3][8]. See
[LICENSE.txt][9] for more information.

Contact
-------

For contact information, see [the ADDIS website][1].

ADDIS Development Team

 [1]: http://www.drugis.org/addis
 [2]: http://www.tipharma.com/projects/efficiency-analysis-drug-discovery-process/the-escher-project.html
 [3]: http://pubmed.com/16172440
 [4]: http://www.java.com/getjava/
 [5]: http://www.drugis.org/
 [6]: https://github.com/gertvv/addis
 [7]: http://maven.apache.org/download.html
 [8]: http://gplv3.fsf.org/
 [9]: https://github.com/gertvv/addis/blob/master/LICENSE.txt
[10]: http://dx.doi.org/10.1038/clpt.2010.291
