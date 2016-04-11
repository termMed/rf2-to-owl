# RF2 to OWL

This project uses the OWLApi to create an OWL ontology, in RDF/XML format, based on the content of standard SNOMED CT RF2 files. As a starting point, the conversion only covers the minimal content required to load the SNOMED CT stated form into an OWL ontology (e.g. synonyms, language refsets, etc. are not represented) to then save it or classify it. The heuristics are based on the Perl transformation script originally developed by Kent Spackman and currently maintained by IHTSDO at the [SNOMED to OWL GitHub repository] (https://github.com/IHTSDO/snomed2owl)

The output of both PERL and Java processing is identical when axioms are compared (excluding annotations), and the classification results are also identical between them and when compared with the official SNOMED International Edition release. Output was classified using [ELK reasoner] (https://github.com/liveontologies/elk-reasoner).

In future iterations, we expect revisions will be made based on ongoing IHTSDO efforts towards standardizing the transformation process, the current status is just trying to reach semantic equivalency between the PERL and the Java scripts.

In particular, significant changes are expected on the following areas:
* Annotations (Preferred terms, alternative descriptions, etc.)
* Revise header/declarations
* Revise concept identifier usage (IRIs)
* Remove special handling of attributes inherited from PERL script
* Parameters to handle different serialization formats

Current state has only been tested with SNOMED CT International Edition core distribution (July 2015/January 2016). Extension management, ontology imports, etc. would require consensus building.

## Build instructions

Clone and build with:

`mvn clean compile assembly:single`

This will generate the executable JAR file: owl-test-x.x.x-SNAPSHOT-jar-with-dependencies.jar

Binaries are also available in the releases section of the [github repository](https://github.com/termMed/rf2-to-owl/releases).

## Executing the conversion

Run the executable from the command line, for example:

`java -jar owl-test-x.x.x-SNAPSHOT-jar-with-dependencies.jar /folder/sct2_Concept_Snapshot_INT_20150131.txt /folder/sct2_StatedRelationship_Snapshot_INT_20150131.txt /folder/outputFile.owl http://snomed.org/`

Parameters (in this order):
 * Path to RF2 Concepts Snapshot file
 * Path to RF2 Stated Relationships Snapshot file
 * Path to output file (it will be created or replaced by this process)
 * IRI for the resulting ontology

## About the conversion

 The process generates the following OWL artifacts in the ontology:

 * Class declarations for all concepts
 * SubclassOf or EquivalentClass Axioms for the concept definitions
 * ObjectProperty declarations, including SubPropertyOf for Role hierarchies
 * Non groupable attributes are managed in a special way to ensure they are never in a Relationship Group
   * *Part of*
   * *Has active ingredient*
   * *Laterality*
   * *Has dose form*
 * A sub-property chain is created between *Direct substance* and *Has active ingredient*
