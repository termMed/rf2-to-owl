/*
 *
 *  * Copyright (C) 2014 termMed IT
 *  * www.termmed.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.termmed.owl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.termmed.rf2.model.ConceptDescriptor;
import com.termmed.rf2.model.LightRelationship;

import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;

/**
 * The Class RF2Parser.
 *
 * @author Alejandro Rodriguez
 */
public class RF2Parser {

	/** The concepts. */
	private Map<Long, ConceptDescriptor> concepts;
	
	/** The relationships. */
	private Map<Long, HashMap<Integer, List<LightRelationship>>> relationships;

	/** The hash roles. */
	private HashMap<Long, OWLObjectProperty> hashRoles;
	
	/** The isarelationships. */
	private Map<Long, List<LightRelationship>> isarelationships;
	
	/** The isarelationshiptypeid. */
	private long ISARELATIONSHIPTYPEID=116680003l;
	
	/** The primitive. */
	private String PRIMITIVE="900000000000074008";
	
	/** The Constant FSN_TYPE. */
	private static final Object FSN_TYPE = "900000000000003001";
	
	/** The Constant PREFERRED_ACCEPTABILITY. */
	private static final Object PREFERRED_ACCEPTABILITY = "900000000000548007";
	
	/** The Constant TEXT_DEFINITION_TYPE. */
	private static final Object TEXT_DEFINITION_TYPE = "900000000000550004";
	
	/** The role group prop. */
	private OWLObjectProperty roleGroupProp;
	
	/** The rolegroupsctid. */
	private long ROLEGROUPSCTID=609096000l;
	
	/** The conceptmodelattribute. */
	private long CONCEPTMODELATTRIBUTE=410662002l;
	
	/** The attributes. */
	private HashSet<Long> attributes;
	
	/** The manager. */
	private OWLOntologyManager manager = null;
	
	/** The ontology iri. */
	private IRI ontologyIRI = null;
	
	/** The ont. */
	private OWLOntology ont = null;
	
	/** The factory. */
	private OWLDataFactory factory ;
	
	/** The wogroup. */
	private HashSet<Long> wogroup;
	
	/** The right ident. */
	private HashMap<Long, Long> rightIdent;
	
	/** The coremodule. */
	private String COREMODULE="900000000000207008";
	
	/** The en language refset. */
	private String EN_LANGUAGE_REFSET="900000000000509007";
	
	/** The concept file. */
	private String conceptFile;
	
	/** The relationship file. */
	private String relationshipFile;
	
	/** The output file. */
	private String outputFile;
	
	/** The iri. */
	private String iri;
	
	/** The description file. */
	private String descriptionFile;
	
	/** The text definition file. */
	private String textDefinitionFile;
	
	/** The language file. */
	private String languageFile;
	
	/** The short preferred. */
	private Short shortPreferred=2;
	
	/** The short acceptable. */
	private Short shortAcceptable=3;

	/**
	 * Instantiates a new r f2 parser.
	 *
	 * @param conceptFile the concept file
	 * @param relationshipFile the relationship file
	 * @param descriptionFile the description file
	 * @param textDefinitionFile the text definition file
	 * @param languageFile the language file
	 * @param outputFile the output file
	 * @param iri the iri
	 */
	public RF2Parser(String conceptFile, String relationshipFile, String descriptionFile,
			String textDefinitionFile,String languageFile,
			String outputFile, String iri) {
		super();
		this.conceptFile = conceptFile;
		this.relationshipFile = relationshipFile;
		this.descriptionFile=descriptionFile;
		this.textDefinitionFile=textDefinitionFile;
		this.languageFile=languageFile;

		this.outputFile = outputFile;
		this.iri = iri;
		concepts = new HashMap<Long, ConceptDescriptor>();
		relationships = new HashMap<Long, HashMap<Integer,List<LightRelationship>>>();
		wogroup=new HashSet<Long>();
		wogroup.add(123005000l);
		wogroup.add(127489000l);
		wogroup.add(272741003l);
		wogroup.add(411116001l);

		rightIdent=new HashMap<Long,Long>();
		rightIdent.put(363701004l, 127489000l);

	}

	/**
	 * Parses the.
	 *
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 * @throws OWLOntologyStorageException the OWL ontology storage exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void parse()throws OWLOntologyCreationException,
	OWLOntologyStorageException, FileNotFoundException, IOException{
		loadConceptsFile(new File(conceptFile));
		loadIsaRelationshipsFile(new File(relationshipFile));
		loadRelationshipsFile(new File(relationshipFile));

		manager = OWLManager.createOWLOntologyManager();
		ontologyIRI = IRI.create(iri);
		ont = manager.createOntology(ontologyIRI);
		factory = manager.getOWLDataFactory();

		addRoles();
		loadDescriptionsFile(descriptionFile,textDefinitionFile, languageFile);
		System.out.println("Attribute count:" + attributes.size());

		roleGroupProp=factory.getOWLObjectProperty(IRI
				.create("id/" + ROLEGROUPSCTID));	
		HashMap <Integer,List<LightRelationship>> listLR = new HashMap<Integer,List<LightRelationship>>();
		List<LightRelationship> listIsas = new ArrayList<LightRelationship>();

		for (Long cptId : concepts.keySet()) {
			if (hashRoles.containsKey(cptId)){
				continue;
			}
			boolean isPrim=(concepts.get(cptId).getDefinitionStatus().equals(PRIMITIVE));
			OWLClass conceptClass = factory.getOWLClass(IRI.create("id/" + concepts.get(cptId).getConceptId()));
			listIsas = isarelationships.get(cptId);

			OWLDeclarationAxiom declarationAxiom = factory
					.getOWLDeclarationAxiom(conceptClass);
			manager.addAxiom(ont, declarationAxiom);

			listLR = relationships.get(cptId);
			if (listIsas != null) {
				if (listIsas.size()==0){

				}else if (listIsas.size()==1 && (listLR==null || (listLR!=null && listLR.size()==0))){
					OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + listIsas.get(0).getTarget()));
					manager.addAxiom(ont, factory.getOWLSubClassOfAxiom(conceptClass, targetClass));
				}else{
					Set<OWLClassExpression> set=new HashSet<OWLClassExpression>();
					for (LightRelationship lrel : listIsas) {

						OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
						set.add(targetClass);

					}

					if (listLR!=null && listLR.size()>0){
						set=addtoSet(set,listLR);
					}
					OWLObjectIntersectionOf intersection=factory.getOWLObjectIntersectionOf(set);

					if (isPrim){
						manager.addAxiom(ont,factory.getOWLSubClassOfAxiom(conceptClass, intersection));
					}else{
						manager.addAxiom(ont,factory.getOWLEquivalentClassesAxiom(conceptClass,intersection));
					}
				}
			}
		}

		File f = new File(outputFile);
		IRI documentIRI = IRI.create(f);
		manager.saveOntology(ont, new RDFXMLOntologyFormat(), documentIRI);
		manager.removeOntology(ont);
		System.gc();

	}
	
	/**
	 * Load descriptions file.
	 *
	 * @param description the description
	 * @param textDefinition the text definition
	 * @param language the language
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void loadDescriptionsFile(String description, String textDefinition, String language) throws IOException {
		if (description!=null && !description.equals("") &&
				language!=null && !language.equals("")){

			HashMap<Long,Short> lang=new HashMap<Long,Short>();
			File descFile = new File(description);
			File langFile = new File(language);
			if (descFile.exists() && langFile.exists()){
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(langFile), "UTF8"));

				br.readLine();
				String line;
				String[] spl;
				while ((line=br.readLine())!=null){
					spl=line.split("\t",-1);
					if (spl[2].equals("1") && spl[4].equals(EN_LANGUAGE_REFSET) ){

						lang.put(Long.parseLong(spl[5]),(spl[6].equals(PREFERRED_ACCEPTABILITY)?shortPreferred:shortAcceptable));
					}
				}
				br.close();

				br = new BufferedReader(new InputStreamReader(new FileInputStream(descriptionFile), "UTF8"));
				br.readLine();
				Long cid;
				Long did;
				while ((line=br.readLine())!=null){
					spl=line.split("\t",-1);
					did=Long.parseLong(spl[0]);
					if (spl[2].equals("1") 
							&& lang.containsKey(did)){

						cid=Long.parseLong(spl[4]);
						if ( concepts.containsKey(cid) || hashRoles.containsKey(cid)){

							IRI cptIri = IRI.create("id/" + cid);

							OWLDatatypeImpl dtt=new OWLDatatypeImpl(OWL2Datatype.RDF_PLAIN_LITERAL.getIRI());
							OWLAnnotationProperty propA ;
							if ( spl[6].equals(FSN_TYPE)){
								propA = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

							}else{
								Short acceptability=lang.get(did);
								if (acceptability.equals(shortPreferred)){
									propA = factory.getOWLAnnotationProperty("sctp:","en-us.preferred");
								}else if (!hashRoles.containsKey(cid)){
									propA = factory.getOWLAnnotationProperty("sctp:","en-us.synonym");
								}else{
									continue;
								}
							}
							OWLAnnotation annotation = factory.getOWLAnnotation(propA,new OWLLiteralImpl(spl[7],"en",dtt));
							OWLAnnotationAssertionAxiom axiom = factory.getOWLAnnotationAssertionAxiom(cptIri, annotation);
							manager.addAxiom(ont, axiom);

						}

					}
				}
				br.close();
				File txtDFile = new File(textDefinition);
				if (txtDFile.exists() && txtDFile.exists()){
					br = new BufferedReader(new InputStreamReader(new FileInputStream(txtDFile), "UTF8"));
					br.readLine();
					while ((line=br.readLine())!=null){
						spl=line.split("\t",-1);
						did=Long.parseLong(spl[0]);
						if (spl[2].equals("1") 
								&& lang.containsKey(did)){

							cid=Long.parseLong(spl[4]);
							if ( concepts.containsKey(cid)
									&& !hashRoles.containsKey(cid)){

								IRI cptIri = IRI.create("id/" + cid);
								OWLDatatypeImpl dtt=new OWLDatatypeImpl(OWL2Datatype.RDF_PLAIN_LITERAL.getIRI());
								OWLAnnotationProperty propA ;
								if ( spl[6].equals(TEXT_DEFINITION_TYPE)){
									propA = factory.getOWLAnnotationProperty("sctf:","TextDefinition.term");
								}else{
									continue;
								}
								OWLAnnotation annotation = factory.getOWLAnnotation(propA,new OWLLiteralImpl(spl[7],"en",dtt));
								OWLAnnotationAssertionAxiom axiom = factory.getOWLAnnotationAssertionAxiom(cptIri, annotation);
								manager.addAxiom(ont, axiom);

							}

						}
					}
					lang=null;
					br.close();
				}
			}
		}

	}

	/**
	 * Addto set.
	 *
	 * @param set the set
	 * @param listLR the list lr
	 * @return the sets the
	 */
	private Set<OWLClassExpression> addtoSet(
			Set<OWLClassExpression> set, HashMap<Integer, List<LightRelationship>> listLR) {
		List<LightRelationship> listR = new ArrayList<LightRelationship>();
		for (Integer grp:listLR.keySet()){
			if (grp==0){
				listR=listLR.get(0);
				for (LightRelationship lrel:listR){

					OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
					OWLObjectProperty property= hashRoles.get(lrel.getType());
					OWLClassExpression role=factory.getOWLObjectSomeValuesFrom(property, targetClass);

					if (wogroup.contains(lrel.getType())){
						set.add(role);
					}else{
						OWLClassExpression singlerolegroup=factory.getOWLObjectSomeValuesFrom(roleGroupProp, role);
						set.add(singlerolegroup);
					}

				}
			}else{
				HashSet<OWLClassExpression> setRoles = new HashSet<OWLClassExpression>();
				listR=listLR.get(grp);
				for (LightRelationship lrel:listR){

					OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
					OWLObjectProperty property= hashRoles.get(lrel.getType());
					OWLClassExpression role=factory.getOWLObjectSomeValuesFrom(property, targetClass);
					setRoles.add(role);
				}
				OWLObjectIntersectionOf intersection=factory.getOWLObjectIntersectionOf(setRoles);
				OWLClassExpression roleGroup=factory.getOWLObjectSomeValuesFrom(roleGroupProp, intersection);
				set.add(roleGroup);
			}

		}

		return set;
	}

	/**
	 * Adds the roles.
	 */
	private void addRoles() {

		hashRoles=new HashMap<Long,OWLObjectProperty>();
		for(Long concept:attributes){
			OWLObjectProperty property = factory.getOWLObjectProperty(IRI
					.create("id/" + concept));	
			OWLDeclarationAxiom declarationAxiom = factory
					.getOWLDeclarationAxiom(property);
			manager.addAxiom(ont, declarationAxiom);
			if(rightIdent.containsKey(concept)){

				long parentProp=rightIdent.get(concept);
				OWLObjectProperty superProperty = factory.getOWLObjectProperty(IRI
						.create("id/" + parentProp));
				List<OWLObjectProperty> lProp=new ArrayList<OWLObjectProperty>();
				lProp.add(property);
				lProp.add(superProperty);
				OWLSubPropertyChainOfAxiom chainAxiom = factory.getOWLSubPropertyChainOfAxiom(lProp, property);
				manager.addAxiom(ont, chainAxiom);
			}
			hashRoles.put(concept, property);
		}
		getDescendantsRoles(attributes);
	}
	
	/**
	 * Gets the descendants roles.
	 *
	 * @param attribute the attribute
	 * @return the descendants roles
	 */
	private void getDescendantsRoles(HashSet<Long> attribute) {

		HashSet<Long> tmpSet=new HashSet<Long>();
		for (Long cpt:isarelationships.keySet()){
			List<LightRelationship> rels=isarelationships.get(cpt);
			for (LightRelationship rel:rels){
				if (attribute.contains(rel.getTarget())){
					attributes.add(rel.getSourceId());

					OWLObjectProperty subProperty = factory.getOWLObjectProperty(IRI
							.create("id/" + rel.getSourceId()));

					OWLDeclarationAxiom declarationAxiom = factory
							.getOWLDeclarationAxiom(subProperty);
					manager.addAxiom(ont, declarationAxiom);

					OWLObjectProperty superProperty= hashRoles.get(rel.getTarget());
					hashRoles.put(rel.getSourceId(), subProperty);
					OWLSubObjectPropertyOfAxiom SupPropAxim = factory.getOWLSubObjectPropertyOfAxiom(subProperty, superProperty);
					manager.addAxiom(ont, SupPropAxim);

					tmpSet.add(rel.getSourceId());
				}
			}
		}
		if (tmpSet.size()>0){
			getDescendantsRoles(tmpSet);
		}
	}
	
	/**
	 * Load concepts file.
	 *
	 * @param conceptsFile the concepts file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void loadConceptsFile(File conceptsFile) throws FileNotFoundException, IOException {
		System.out.println("Starting Concepts: " + conceptsFile.getName());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(conceptsFile), "UTF8"));
		try {
			String line = br.readLine();
			line = br.readLine(); // Skip header
			int count = 0;
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				if ( columns[2].equals("1") && columns[3].equals(COREMODULE)){
					ConceptDescriptor loopConcept = new ConceptDescriptor();
					Long conceptId = Long.parseLong(columns[0]);
					loopConcept.setConceptId(conceptId);
					loopConcept.setActive(columns[2].equals("1"));
					loopConcept.setEffectiveTime(columns[1]);
					loopConcept.setModule(Long.parseLong(columns[3]));
					loopConcept.setDefinitionStatus(columns[4]);
					concepts.put(conceptId, loopConcept);
					count++;
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
				line = br.readLine();
			}
			System.out.println(".");
			System.out.println("Concepts loaded = " + concepts.size());
		} finally {
			br.close();
		}

	}


	/**
	 * Load relationships file.
	 *
	 * @param relationshipsFile the relationships file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void loadRelationshipsFile(File relationshipsFile) throws FileNotFoundException, IOException {
		System.out.println("Starting Relationships: " + relationshipsFile.getName());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(relationshipsFile), "UTF8"));
		try {
			String line = br.readLine();
			line = br.readLine(); // Skip header
			int count = 0;
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				if (Long.parseLong(columns[7])!=ISARELATIONSHIPTYPEID 
						&& columns[2].equals("1")
						&& columns[3].equals(COREMODULE)){
					LightRelationship loopRelationship = new LightRelationship();

					loopRelationship.setActive(columns[2].equals("1"));
					loopRelationship.setEffectiveTime(columns[1]);
					loopRelationship.setModule(Long.parseLong(columns[3]));

					loopRelationship.setTarget(Long.parseLong(columns[5]));
					loopRelationship.setType(Long.parseLong(columns[7]));
					loopRelationship.setModifier(Long.parseLong(columns[9]));
					Integer groupId=Integer.parseInt(columns[6]);
					loopRelationship.setGroupId(groupId);
					Long sourceId = Long.parseLong(columns[4]);
					loopRelationship.setSourceId(sourceId);
					loopRelationship.setCharType(Long.parseLong(columns[8]));

					HashMap<Integer, List<LightRelationship>> relGrList = relationships.get(sourceId);
					if (relGrList == null) {
						relGrList = new HashMap<Integer,List<LightRelationship>>();
					}
					List<LightRelationship> relList=relGrList.get(groupId);
					if (relList == null) {
						relList = new ArrayList<LightRelationship>();
					}
					relList.add(loopRelationship);

					relGrList.put(groupId, relList);
					relationships.put(sourceId, relGrList);
					count++;
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
				line = br.readLine();
			}
			System.out.println(".");
			System.out.println("Relationships loaded = " + relationships.size());
		} finally {
			br.close();
		}
	}

	/**
	 * Load isa relationships file.
	 *
	 * @param relationshipsFile the relationships file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void loadIsaRelationshipsFile(File relationshipsFile) throws FileNotFoundException, IOException {
		System.out.println("Starting Isas Relationships from: " + relationshipsFile.getName());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(relationshipsFile), "UTF8"));
		try {
			String line = br.readLine();
			attributes=new HashSet<Long>();
			isarelationships=new HashMap<Long,List<LightRelationship>>();
			line = br.readLine(); // Skip header
			int count = 0;
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				if (Long.parseLong(columns[7])==ISARELATIONSHIPTYPEID 
						&& columns[2].equals("1")){
					LightRelationship loopRelationship = new LightRelationship();

					loopRelationship.setActive(columns[2].equals("1"));
					loopRelationship.setEffectiveTime(columns[1]);
					loopRelationship.setModule(Long.parseLong(columns[3]));

					loopRelationship.setTarget(Long.parseLong(columns[5]));
					loopRelationship.setType(Long.parseLong(columns[7]));
					loopRelationship.setModifier(Long.parseLong(columns[9]));
					loopRelationship.setGroupId(Integer.parseInt(columns[6]));
					Long sourceId = Long.parseLong(columns[4]);
					loopRelationship.setSourceId(sourceId);
					loopRelationship.setCharType(Long.parseLong(columns[8]));

					List<LightRelationship> relList = isarelationships.get(sourceId);
					if (relList == null) {
						relList = new ArrayList<LightRelationship>();
					}
					relList.add(loopRelationship);
					isarelationships.put(sourceId, relList);
					if (Long.parseLong(columns[5])==CONCEPTMODELATTRIBUTE){
						attributes.add(sourceId);
					}
					count++;
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
				line = br.readLine();
			}
			System.out.println(".");
			System.out.println("Isas Relationships loaded = " + isarelationships.size());
		} finally {
			br.close();
		}
	}
}
