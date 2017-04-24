# SNOMED CT RF2 and  OWL Utilities

This project uses the OWLApi perform different transformations of SNOMED CT representations.

* Create an OWL Ontology file based on the RF2 release of SNOMED CT (Snapshot)
* Create an RF2 OWL Reference Set from an OWL Ontology file
* Create an OWL Ontology file from an RF2 OWL Reference Set file

## Build instructions

Clone and build with:

`mvn clean compile assembly:single`

This will generate the executable JAR file: owl-test-x.x.x-SNAPSHOT-jar-with-dependencies.jar

Binaries are also available in the releases section of the [github repository](https://github.com/termMed/rf2-to-owl/releases).

## Executing the conversion

Run the executable from the command line, for example:

`java -jar rf2-to-owl-x.x.x-SNAPSHOT-jar-with-dependencies.jar -help`

Arguments help:
```
usage: rf2-to-owl
 -cd                Convert concepts to concrete domains
 -cf <arg>          Concepts file
 -df <arg>          Descriptions file
 -help              Prints help
 -iri <arg>         IRI for Owl Generation
 -lf <arg>          language refset file
 -mode <arg>        conversion mode, expected values: rf2-to-owl,
                    owl-to-refset, refset-to-owl
 -of <arg>          Owl file
 -orf <arg>         Owl Refset file
 -output <arg>      Output file
 -rf <arg>          Relationships file
 -rf2Folder <arg>   RF2 Folder
 -syntax <arg>      OWL Syntax, expected values: owlxml, functional,
                    manchester
 -tf <arg>          Text definition file
```

## RF2 Snapshot to OWL file conversion

Arguments required for this mode:

```
java -jar rf2-to-owl-x.x.x-SNAPSHOT-jar-with-dependencies.jar -mode rf2-to-owl -rf2Folder /x/y/z/snapshot -output ontology.owl
```

* *-mode*: sets the mode to "rf2-to-owl"
* *-rf2Folder*: sets the folder of a valid SNOMED CT Snpashot release, the 
process will automatically find the necessary files inside that folder
* *-output*: the output file where the ontology will be generated

Optional arguments:

* -syntax: by default the process generates the ontology in OWL-XML syntax, but 
this can be changed to Manchester or Functional syntax by passing this argument, 
acceptable values are: owlxml, functional, manchester
* -cd: if this argument is present, the process will convert selected attributes that
are represented using Object Properties in RF2 to Data Properties (concrete domains), 
parsing the values from the Fully specified names of the target concepts. The details 
of this conversion are described later on this document.
    
If the RF2 files are not part of a standard release package, paths for the individual files 
can be passed using the arguments described in the arguments help.

### Output 

 The process generates the following OWL artifacts in the ontology:

 * Class declarations for all concepts
 * SubclassOf or EquivalentClass Axioms for the concept definitions
 * ObjectProperty declarations, including SubPropertyOf for Role hierarchies
 * Non groupable attributes are managed in a special way to nerver add them in a Relationship Group
   * *Part of*
   * *Has active ingredient*
   * *Laterality*
   * *Has dose form*
 * A sub-property chain is created between *Direct substance* and *Has active ingredient*
 
### Concrete domains conversion
 
 Since the July 2017 release of the international edition, some relationships that specify numerical values are 
 represented using concepts for the example:

 * Source: 318420003 | Product containing atenolol 50 mg/1 each oral tablet (virtual clinical drug) |
 * Attribute: 732947008 | Has presentation strength denominator unit (attribute) |
 * Destination: 732774003 | 50 (qualifier value) |

 If the "Concrete domains conversion" parameter is sent as "TRUE", those relationships are converted into OWL Data 
 Properties, instead of the normal OWL Object Properties. Classification results should be identical, but having 
 actual values instead of concept references may allow for more detailed queries and computations.
 
 Attributes that are currently converted as data properties:
 
 * 732944001 | Has presentation strength numerator value (attribute) | - DataType: float
 * 732946004 | Has presentation strength denominator value (attribute) | - DataType: float
 
 
## OWL Ontology file to RF2 OWL Refset file conversion
 
 Arguments required for this mode:
 
 ```
 java -jar rf2-to-owl-x.x.x-SNAPSHOT-jar-with-dependencies.jar -mode owl-to-refset -of ontology.owl -output owlRefset.txt
 ```
 
 * *-mode*: sets the mode to "owl-to-refset"
 * *-of*: path to the ontology file
 * *-output*: the output file where the refset will be generated
 
## RF2 OWL Refset file ot OWL Ontology file conversion
  
  Arguments required for this mode:
  
  ```
  java -jar rf2-to-owl-x.x.x-SNAPSHOT-jar-with-dependencies.jar -mode refset-to-owl -orf owlRefset.txt -output ontology.owl
  ```
  
  * *-mode*: sets the mode to "refset-to-owl"
  * *-orf*: path to the OWL Refset file
  * *-output*: the output file where the ontology will be generated

