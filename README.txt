ADDIS - Aggregate Data Drug Information System
==============================================

ADDIS is a software developed within the Dutch Escher-project for
managing and analyzing clinical trial information.

ADDIS comes with a built-in example modeled on the paper: Hansen et al.,
Annals of internal medicine, 143(6): 415-426, 2005. All data was
extracted by us from the original papers.

Requirements
------------

ADDIS is made in Java, and requires Java 5 (JRE 1.5) or newer. Most
modern operating systems ship with a suitable JRE.

Versions
--------

1.5: User interface improvements, risk-based sampling in MetaBR, new
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

All releases as well as a nightly build (see http://www.drugis.org) are
distributed in a compiled format. However, we also provide source for
external development / verification.

In order to compile ADDIS, you need:

 - Java >= 1.5
 - Maven 2
 - Other dependencies should be downloaded automatically by Maven2

Building is automatic with "mvn package".

License
-------

ADDIS is open source, and licensed under GPLv3. See LICENSE.txt for more
information.

Contact
-------

For contact information, see the ADDIS website: http://www.drugis.org

ADDIS Development Team
