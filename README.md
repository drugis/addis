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
1.14.1: Bugfix release. 

1.14: Network meta-analysis results can now be saved in the ADDIS data
   file. In addition, the computations can be reset and there is a new
   visual progress indicator.

1.12.5: Bugfix release.

1.12.4: The network meta-analysis simulation can be continued if it has
   not converged yet, and there is a new GUI for checking convergence.
   The benefit-risk analysis now uses the full covariance information
   from network meta-analysis. Reduced download from 30MB to 15MB.
   Bugfixes.

1.12.3: Bugfix release.

1.12.2: Bugfix release.

1.12.1: Input name of analyses in the first step of the wizard, not in
  a pop-up dialog at the very end.

1.12: Add [BRAT table][10] and its value tree to benefit-risk view, allow
  selection of baseline in benefit-risk analysis, enable BRAT description of
  benefit-risk analysis, improve performance of network meta-analysis wizard,
  clarify error dialog with bug reporting instructions, fix many bugs.

1.10.1: Bugfix release

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

Developing ADDIS using Eclipse
------------------------------

First, let Maven configure your workspace for use with Maven:

	$ mvn eclipse:configure-workspace -Declipse.workspace=WORKSPACE_DIR

Use Maven to generate the eclipse project:

	$ cd SOURCE_DIR
	$ mvn eclipse:eclipse

Then import the project into Eclipse (File -> Import -> Existing projects).

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
