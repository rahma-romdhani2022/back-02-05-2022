package com.projet.BackendPfe.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projet.BackendPfe.Entity.Generaliste;
import com.projet.BackendPfe.Entity.Patient;
import com.projet.BackendPfe.repository.GeneralisteRepository;
import com.projet.BackendPfe.repository.PatientRepository;
import com.projet.BackendPfe.request.PatientRequest;
import com.projet.BackendPfe.request.RegisterRequestExpert;
import com.projet.BackendPfe.services.IGestionPatient;
import com.projet.BackendPfe.services.PatientService;


@CrossOrigin("*")
@RestController
@RequestMapping("/patient")
public class PatientController {
	
	@Autowired GeneralisteRepository medecinRepository;

	@Autowired
	IGestionPatient gestionPatient;
	@Autowired
	PatientRepository pr;
	@Autowired
	GeneralisteRepository pre;
	@Autowired 
	PatientService ps;


	@GetMapping("/patiente/{id}")
	public List<Patient> getAll(@PathVariable("id") @ModelAttribute("id") long id){
         //pr.findById(id);
		 List<Patient> Utilisateur = new ArrayList<>();
		  
		 
		    return   pr.findByGeneraliste_id(id);
	} 
	

	
	@PostMapping("/addpatient/{id}")
	public void AddProduct(@RequestBody PatientRequest patient , @PathVariable("id") @ModelAttribute("id") long id ){
		Generaliste  generaliste = medecinRepository.findById(id).get(); 
		Patient p = new Patient(patient.getCin(),patient.getUsername(),patient.getEmail(),patient.getTelephone(),patient.getGender(),patient.getDateNaiss(),patient.getAntecedant(),generaliste , LocalDate.now());
		pr.save(p);
	}
	
	
	@GetMapping("/patient/{id}/{cin}")
	public Patient productById(@PathVariable("cin") long cin, @PathVariable("id") @ModelAttribute("id") long id ){
		return pr.findById(cin).get();
	}
	@DeleteMapping("/deletepatient/{id}/{cin}")
	public void deleteProduct(@PathVariable("cin") long cin, @PathVariable("id") @ModelAttribute("id") long id){
		 pr.findByGeneraliste_id(id);
	
		pr.deleteById(cin);
	} 
	@GetMapping("/patient/chercher/{username}")
	public List<Patient> patientByName(@PathVariable("username") String username ){
		return pr.findByUsernameContains(username);
	}
	 @PutMapping("/updatePatient/{id}/{cin}")  // check it 
	  public ResponseEntity<Patient> updateGeneraliste(@PathVariable("cin") long cin, @RequestBody Patient Utilisateur) {
	    System.out.println("Update Utilisateur with ID = " + cin + "...");
	 
	    Optional<Patient> UtilisateurInfo = pr.findById(cin);

	    	Patient utilisateur = UtilisateurInfo.get();
	       	utilisateur.setCin(Utilisateur.getCin());
	       	utilisateur.setUsername(Utilisateur.getUsername());
	       	utilisateur.setEmail(Utilisateur.getEmail());
	       	utilisateur.setDateNaiss(Utilisateur.getDateNaiss());
			
	      	utilisateur.setAntecedant(Utilisateur.getAntecedant());
	    	utilisateur.setTelephone(Utilisateur.getTelephone());
	    	utilisateur.setGender(Utilisateur.getGender());
	    	//utilisateur.setImage(Utilisateur.getImage());       //  utilisateur.getEmail();
	        // utilisateur.getUsername();
	    	
	      return new ResponseEntity<>(pr.save(utilisateur), HttpStatus.OK);
	    } 	
	 @GetMapping("/get/{id}")
		public Patient  getPatientById(@PathVariable("id") long id){
		Patient patient = 	pr.findById(id).get();
			    return   patient;
		} 
	 @GetMapping("/all")
		public List<Patient>  getAllPatients(){
			    return   pr.findAll();
		} 
	 @GetMapping("/allParDate")
		public int  getAllPatientsByDateInscription(){
			      int total = pr.findByDateInscription(LocalDate.now()).size();
			       return total ; 
		} 
	}