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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.termmed.owl.util.FileHelper;
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

	private String prefix;

	private Boolean useConcreteDomains;

	private Boolean generateOwlRefset;

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
	 * @param useConcreteDomains whether to use concrete domains
	 * @param generateOwlRefset whether to generate Owl Refset
	 */
	public RF2Parser(String conceptFile, String relationshipFile, String descriptionFile,
			String textDefinitionFile,String languageFile,
			String outputFile, String iri, Boolean useConcreteDomains, boolean generateOwlRefset) {
		super();
		this.conceptFile = conceptFile;
		this.relationshipFile = relationshipFile;
		this.descriptionFile=descriptionFile;
		this.textDefinitionFile=textDefinitionFile;
		this.languageFile=languageFile;
		this.useConcreteDomains=useConcreteDomains;
		this.generateOwlRefset = generateOwlRefset;

		this.outputFile = outputFile;
		this.prefix=iri;
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
	public RF2Parser(String inputFolder,
			String outputFile, String iri, Boolean useConcreteDomains, boolean generateOwlRefset) {
		super();
		try {
			this.conceptFile = FileHelper.getFile( new File(inputFolder), "rf2-concepts",null,null,null);
			this.descriptionFile = FileHelper.getFile( new File(inputFolder), "rf2-descriptions",null,null,null);
			this.languageFile = FileHelper.getFile( new File(inputFolder), "rf2-language",null,null,null);
			this.relationshipFile = FileHelper.getFile( new File(inputFolder), "rf2-relationships",null,"stated",null);
			this.textDefinitionFile=FileHelper.getFile( new File(inputFolder), "rf2-textDefinition",null,null,null);
			this.useConcreteDomains=useConcreteDomains;
			this.generateOwlRefset = generateOwlRefset;

			this.outputFile = outputFile;
			this.prefix=iri;
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
		} catch (Exception e) {
			e.printStackTrace();
		}

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

		manager = OWLManager.createOWLOntologyManager();
		ontologyIRI = IRI.create(iri);
		ont = manager.createOntology(ontologyIRI);
		factory = manager.getOWLDataFactory();

		addRoles();
		loadDescriptionsFile(descriptionFile,textDefinitionFile, languageFile);
		System.out.println("Attribute count:" + attributes.size());
		loadRelationshipsFile(new File(relationshipFile));

		roleGroupProp=factory.getOWLObjectProperty(IRI
				.create(prefix + ROLEGROUPSCTID));	
		HashMap <Integer,List<LightRelationship>> listLR = new HashMap<Integer,List<LightRelationship>>();
		List<LightRelationship> listIsas = new ArrayList<LightRelationship>();
		System.out.println("Starting Owl File Generation");
		int count = 0;
		for (Long cptId : concepts.keySet()) {
			count++;
			if (count % 15000 == 0) {
				System.out.print(".");
			}
			if (hashRoles.containsKey(cptId)){
				continue;
			}
			boolean isPrim=(concepts.get(cptId).getDefinitionStatus().equals(PRIMITIVE));
			OWLClass conceptClass = factory.getOWLClass(IRI.create(prefix + concepts.get(cptId).getConceptId()));
			listIsas = isarelationships.get(cptId);

			OWLDeclarationAxiom declarationAxiom = factory
					.getOWLDeclarationAxiom(conceptClass);
			manager.addAxiom(ont, declarationAxiom);

			listLR = relationships.get(cptId);
			if (listIsas != null) {
				if (listIsas.size()==0){

				}else if (listIsas.size()==1 && (listLR==null || (listLR!=null && listLR.size()==0))){
					OWLClass targetClass = factory.getOWLClass(IRI.create(prefix + listIsas.get(0).getTarget()));
					manager.addAxiom(ont, factory.getOWLSubClassOfAxiom(conceptClass, targetClass));
				}else{
					Set<OWLClassExpression> set=new HashSet<OWLClassExpression>();
					for (LightRelationship lrel : listIsas) {

						OWLClass targetClass = factory.getOWLClass(IRI.create(prefix + lrel.getTarget()));
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


		//		Set<OWLAxiom> axioms = ont.getAxioms();
		//		HashSet<String> axClasses = new HashSet<String>();
		//		for (OWLAxiom axiom:axioms){
		////			parseAxiom(axiom);
		//			axClasses.add(axiom.getClass().getName());
		//			
		//		}
		//
		//		for (String cls:axClasses){
		//			System.out.println(cls);
		//		}

		// Manchester sintax process*********************************		
		//		OWLDocumentFormat format = manager.getOntologyFormat(ont);
		//		System.out.println("    format: " + format);
		//
		//		ManchesterSyntaxDocumentFormat manSyntDocFormat=new ManchesterSyntaxDocumentFormat();
		//		format.isPrefixOWLDocumentFormat();
		//		if(format.isPrefixOWLDocumentFormat()) {
		//			manSyntDocFormat.copyPrefixesFrom(format.asPrefixOWLDocumentFormat());
		//		}
		//		manager.setOntologyFormat(ont, manSyntDocFormat);
		//		manager.saveOntology(ont, manSyntDocFormat,documentIRI);
		// Manchester sintax process*********************************


		manager.saveOntology(ont, new OWLXMLDocumentFormat(), documentIRI);
		if (this.generateOwlRefset) {
			RF2OwlRefsetRenderer refsetRenderer = new RF2OwlRefsetRenderer();
			PrintWriter refsetWriter = new PrintWriter("owlRefset.txt", "UTF-8");
			refsetRenderer.render(ont, refsetWriter);
			refsetWriter.close();
		}
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
				System.out.println("Starting descriptions from " + descFile.getName());
				int count = 0;
				while ((line=br.readLine())!=null){
					count++;
					spl=line.split("\t",-1);
					if (spl[2].equals("1") && spl[4].equals(EN_LANGUAGE_REFSET) ){

						lang.put(Long.parseLong(spl[5]),(spl[6].equals(PREFERRED_ACCEPTABILITY)?shortPreferred:shortAcceptable));
					}
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
				System.out.println(".");
				System.out.println("Descriptions and Acceptablities loaded = " + count);
				br.close();

				br = new BufferedReader(new InputStreamReader(new FileInputStream(descriptionFile), "UTF8"));
				br.readLine();
				Long cid;
				Long did;
				while ((line=br.readLine())!=null){
					spl=line.split("\t",-1);
					if (spl[6].equals("900000000000003001") && spl[2].equals("1")) {
						if (concepts.containsKey(Long.parseLong(spl[4]))) {
							concepts.get(Long.parseLong(spl[4])).setDefaultTerm(spl[7]);
						}
					}
					did=Long.parseLong(spl[0]);
					//					if (spl[2].equals("1") 
					//							&& lang.containsKey(did) ){
					if (spl[2].equals("1") 
							&& lang.containsKey(did) 
							&&  spl[6].equals(FSN_TYPE)){

						cid=Long.parseLong(spl[4]);
						if ( concepts.containsKey(cid) || hashRoles.containsKey(cid)){

							IRI cptIri = IRI.create(prefix + cid);

							OWLDatatypeImpl dtt=new OWLDatatypeImpl(OWL2Datatype.RDF_PLAIN_LITERAL.getIRI());
							OWLAnnotationProperty propA ;
							if ( spl[6].equals(FSN_TYPE)){
								propA = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

							}else{
								// just to add fsn
								continue;
								// ***************
								//								Short acceptability=lang.get(did);
								//								if (acceptability.equals(shortPreferred)){
								//									propA = factory.getOWLAnnotationProperty("sctp:","en-us.preferred");
								//								}else if (!hashRoles.containsKey(cid)){
								//									propA = factory.getOWLAnnotationProperty("sctp:","en-us.synonym");
								//								}else{
								//									continue;
								//								}
								// ***************
							}
							OWLAnnotation annotation = factory.getOWLAnnotation(propA,new OWLLiteralImpl(spl[7],"en",dtt));
							OWLAnnotationAssertionAxiom axiom = factory.getOWLAnnotationAssertionAxiom(cptIri, annotation);
							manager.addAxiom(ont, axiom);

						}

					}
				}
				br.close();
				if (textDefinition!=null ){
					DefaultPrefixManager pm=new DefaultPrefixManager();
					pm.setDefaultPrefix("TextDefinition.term");
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
									IRI cptIri = IRI.create(prefix + cid);
									OWLDatatypeImpl dtt=new OWLDatatypeImpl(OWL2Datatype.RDF_PLAIN_LITERAL.getIRI());
									OWLAnnotationProperty propA ;
									if ( spl[6].equals(TEXT_DEFINITION_TYPE)){
										propA = factory.getOWLAnnotationProperty("sctf:",pm);
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
					OWLClassExpression role;
					if (useConcreteDomains &&
							(lrel.getType().equals(732946004l) || lrel.getType().equals(732944001l) )) {
						String value = concepts.get(lrel.getTarget()).getDefaultTerm().replaceAll("[^0-9?!\\.]","");
						OWLDataProperty property= factory.getOWLDataProperty(IRI
								.create("id/" + lrel.getType()));
						role=factory.getOWLDataHasValue(property.asDataPropertyExpression(), factory.getOWLLiteral(Float.parseFloat(value)));
					} else {
						OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
						OWLObjectProperty property= hashRoles.get(lrel.getType());
						role=factory.getOWLObjectSomeValuesFrom(property, targetClass);
					}
					//					OWLClass targetClass = factory.getOWLClass(IRI.create(prefix + lrel.getTarget()));
					//					OWLObjectProperty property= hashRoles.get(lrel.getType());
					//					OWLClassExpression role=factory.getOWLObjectSomeValuesFrom(property, targetClass);

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
					OWLClassExpression role;
					if (useConcreteDomains &&
							(lrel.getType().equals(732946004l) || lrel.getType().equals(732944001l) )) {
						String value = concepts.get(lrel.getTarget()).getDefaultTerm().replaceAll("[^0-9?!\\.]","");
						OWLDataProperty property= factory.getOWLDataProperty(IRI
								.create("id/" + lrel.getType()));
						role=factory.getOWLDataHasValue(property.asDataPropertyExpression(), factory.getOWLLiteral(Float.parseFloat(value)));
					} else {
						OWLClass targetClass = factory.getOWLClass(IRI.create("id/" + lrel.getTarget()));
						OWLObjectProperty property= hashRoles.get(lrel.getType());
						role=factory.getOWLObjectSomeValuesFrom(property, targetClass);
					}
					setRoles.add(role);
				}
				try{
					OWLObjectIntersectionOf intersection=factory.getOWLObjectIntersectionOf(setRoles);
					OWLClassExpression roleGroup=factory.getOWLObjectSomeValuesFrom(roleGroupProp, intersection);
					set.add(roleGroup);
				}catch (Exception e){
					e.printStackTrace();

				}
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
			if (useConcreteDomains && (concept.equals(732946004l) || concept.equals(732944001l))) {
				OWLDataProperty property = factory.getOWLDataProperty(IRI
						.create(prefix + concept));
				OWLDeclarationAxiom declarationAxiom = factory
						.getOWLDeclarationAxiom(property);
				manager.addAxiom(ont, declarationAxiom);
			} else {
				OWLObjectProperty property = factory.getOWLObjectProperty(IRI
						.create(prefix + concept));
				OWLDeclarationAxiom declarationAxiom = factory
						.getOWLDeclarationAxiom(property);
				manager.addAxiom(ont, declarationAxiom);
				if(rightIdent.containsKey(concept)){
					long parentProp=rightIdent.get(concept);
					OWLObjectProperty superProperty = factory.getOWLObjectProperty(IRI
							.create(prefix + parentProp));
					List<OWLObjectProperty> lProp=new ArrayList<OWLObjectProperty>();
					lProp.add(property);
					lProp.add(superProperty);
					OWLSubPropertyChainOfAxiom chainAxiom = factory.getOWLSubPropertyChainOfAxiom(lProp, property);
					manager.addAxiom(ont, chainAxiom);
				}
				hashRoles.put(concept, property);
			}
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
							.create(prefix + rel.getSourceId()));

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
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				//				if ( columns[2].equals("1") && !columns[3].equals(METADATA_MODULE)){
				if ( columns[2].equals("1")){
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
			while ((line = br.readLine())!= null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				//				if (Long.parseLong(columns[7])!=ISARELATIONSHIPTYPEID 
				//						&& columns[2].equals("1")
				//						&& !columns[3].equals(METADATA_MODULE)){
				if (Long.parseLong(columns[7])!=ISARELATIONSHIPTYPEID 
						&& columns[2].equals("1")){
					Long sourceId = Long.parseLong(columns[4]);
					ConceptDescriptor concept = concepts.get(sourceId);
					if (concept!=null && !concept.getModule().equals(Long.parseLong(columns[3]))){
						continue;
					}
					LightRelationship loopRelationship = new LightRelationship();

					loopRelationship.setActive(columns[2].equals("1"));
					loopRelationship.setEffectiveTime(columns[1]);
					loopRelationship.setModule(Long.parseLong(columns[3]));

					loopRelationship.setTarget(Long.parseLong(columns[5]));
					loopRelationship.setType(Long.parseLong(columns[7]));
					loopRelationship.setModifier(Long.parseLong(columns[9]));
					Integer groupId=Integer.parseInt(columns[6]);
					loopRelationship.setGroupId(groupId);
					loopRelationship.setSourceId(sourceId);
					loopRelationship.setCharType(Long.parseLong(columns[8]));

					HashMap<Integer, List<LightRelationship>> relGrList = relationships.get(sourceId);
					if (relGrList == null) {
						relGrList = new HashMap<Integer,List<LightRelationship>>();
					}
					List<LightRelationship> relList=relGrList.get(groupId);
					if (relList == null) {
						relList = new ArrayList<LightRelationship>();
					}else if (relExists(relList,Long.parseLong(columns[5]),Long.parseLong(columns[7]))){
						continue;
					}
					relList.add(loopRelationship);

					relGrList.put(groupId, relList);
					relationships.put(sourceId, relGrList);
					count++;
					if (count % 100000 == 0) {
						System.out.print(".");
					}
				}
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
			while ((line = br.readLine())!= null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\t");
				if (Long.parseLong(columns[7])==ISARELATIONSHIPTYPEID 
						&& columns[2].equals("1")){
					Long sourceId = Long.parseLong(columns[4]);
					ConceptDescriptor concept = concepts.get(sourceId);
					//					if (concept!=null && !concept.getModule().equals(Long.parseLong(columns[3]))){
					if (concept==null){
						continue;
					}
					LightRelationship loopRelationship = new LightRelationship();

					loopRelationship.setActive(columns[2].equals("1"));
					loopRelationship.setEffectiveTime(columns[1]);
					loopRelationship.setModule(Long.parseLong(columns[3]));

					loopRelationship.setTarget(Long.parseLong(columns[5]));
					loopRelationship.setType(Long.parseLong(columns[7]));
					loopRelationship.setModifier(Long.parseLong(columns[9]));
					loopRelationship.setGroupId(Integer.parseInt(columns[6]));
					loopRelationship.setSourceId(sourceId);
					loopRelationship.setCharType(Long.parseLong(columns[8]));

					List<LightRelationship> relList = isarelationships.get(sourceId);
					if (relList == null) {
						relList = new ArrayList<LightRelationship>();
					}else if (relExists(relList,Long.parseLong(columns[5]),Long.parseLong(columns[7]))){
						continue;
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
			}
			System.out.println(".");
			System.out.println("Isas Relationships loaded = " + isarelationships.size());
		} finally {
			br.close();
		}
	}

	private boolean relExists(List<LightRelationship> relList, long newTarget, long newType) {
		for (LightRelationship rel:relList){
			if (rel.getTarget().equals(Long.valueOf(newTarget))
					&& rel.getType().equals(Long.valueOf(newType))){
				return true;
			}
		}
		return false;
	}
}
