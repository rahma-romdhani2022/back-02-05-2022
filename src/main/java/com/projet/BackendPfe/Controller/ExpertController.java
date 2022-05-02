package com.projet.BackendPfe.Controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projet.BackendPfe.Entity.AdminMedicalManager;
import com.projet.BackendPfe.Entity.Consultation;
import com.projet.BackendPfe.Entity.Expert;
import com.projet.BackendPfe.Entity.Generaliste;
import com.projet.BackendPfe.Entity.IAModel;
import com.projet.BackendPfe.Entity.Patient;
import com.projet.BackendPfe.config.JwtTokenUtil;
import com.projet.BackendPfe.domaine.JwtResponse;
import com.projet.BackendPfe.domaine.Message;
import com.projet.BackendPfe.exception.ResourceNotFoundException;
import com.projet.BackendPfe.repository.AdminMedicalManagerRepository;
import com.projet.BackendPfe.repository.ConsultationRepository;
import com.projet.BackendPfe.repository.ExpertRepository;
import com.projet.BackendPfe.repository.UserRepository;
import com.projet.BackendPfe.request.LoginRequest;
import com.projet.BackendPfe.request.RegisterRequestExpert;
import com.projet.BackendPfe.request.RegisterRequestGeneraliste;
import com.projet.BackendPfe.services.ExpertService;
import com.projet.BackendPfe.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/expert")
public class ExpertController {
	
	@Autowired 	AuthenticationManager authenticationManager;
	@Autowired	ExpertRepository expertRepository;
	@Autowired	UserRepository userRepository;
	@Autowired  ConsultationRepository repository ;
	@Autowired AdminMedicalManagerRepository  repositoryAdminMedical ;
	@Autowired	private ExpertService expertService ;
	@Autowired	PasswordEncoder encoder;
	@Autowired	JwtTokenUtil jwtUtils;

	
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest data) {
		System.out.println(data.getPassword());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						data.getUsername(),
						data.getPassword()));	
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail() , 
												 userDetails.getRole()
											));
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerExpert(@Valid @RequestBody RegisterRequestExpert signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Email is already in use!"));
		}
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd ");
		Date date = new Date();
		System.out.println(format.format(date));
		Expert expert = new Expert(signUpRequest.getUsername(), 
				 signUpRequest.getEmail(),
				 encoder.encode(signUpRequest.getPassword()),
						 signUpRequest.getGender(),
						 signUpRequest.getTelephone(),
						 signUpRequest.getImage(),
						 LocalDate.now(), "expert");
		expertRepository.save(expert);

		return ResponseEntity.ok(new Message("User registered successfully!"));
	}	  
	@PostMapping("/addExpertParAdminSansImage/{id}")
	/*@RequestParam("imageFile") MultipartFile imageFile ,@RequestParam("username") String  nom,
    @RequestParam("email") String email , @RequestParam("gender") String gender , @RequestParam("telephone") long telephone,
    @RequestParam("password")  String password  , @PathVariable("id") long  idAdmin*/
	public long addExpertParAdmin( @PathVariable("id") long  idAdmin , @RequestBody Expert expert ) throws IOException {
		
		Expert expert1 = new Expert(  expert.getUsername(), 
				expert.getEmail(), encoder.encode(expert.getPassword()),
					expert.getGender(),expert.getTelephone(),expert.getImage(),
					LocalDate.now() , "expert" ,repositoryAdminMedical.findById(idAdmin).get());
expertRepository.save(expert1);
		return   expertRepository.findByUsername(expert.getUsername()).getId() ;
	}
	@PostMapping("/image/{id}")
	public String ajoutImageExpert(@PathVariable("id") long id , @RequestParam("image") MultipartFile imageFile) throws IOException {
		Expert expert = expertRepository.findById(id).get();
		 expert.setImage(expertService.compressZLib(imageFile.getBytes()));
		 return "Done !!!!" ; 
		 }
	  @PutMapping("/update/{id}")
	  public ResponseEntity<?> updateExpert(@PathVariable("id") long id, @RequestBody Expert Utilisateur) {
	    System.out.println("Update Utilisateur with ID = " + id + "...");
	    Optional<Expert> UtilisateurInfo = expertRepository.findById(id);
	    Expert utilisateur = UtilisateurInfo.get();
	    if (expertRepository.existsByUsername(Utilisateur.getUsername())) {
	    	if(expertRepository.findById(id).get().getId() != (expertRepository.findByUsername(Utilisateur.getUsername()).getId())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Username is already taken!"));
		}
	    }
		if (userRepository.existsByEmail(Utilisateur.getEmail())) {
			if(expertRepository.findById(id).get().getId() != (expertRepository.findByEmail(Utilisateur.getEmail()).getId())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Email is already in use!"));
		}
		}

	    
	    	utilisateur.setTelephone(Utilisateur.getTelephone());
	    	utilisateur.setGender(Utilisateur.getGender());
	    	utilisateur.setUsername(Utilisateur.getUsername());
	    	 utilisateur.setEmail(Utilisateur.getEmail());
	
	      return new ResponseEntity<>(expertRepository.save(utilisateur), HttpStatus.OK);
	    } 
	  
	  @PutMapping("/updateImage/{id}")
	  public Expert updateExpert(@PathVariable("id") long id ,  @RequestParam("imageFile") MultipartFile imageFile ) throws IOException		    		  {
		  
		    	return expertService.updateImage(id,imageFile);
		    }

	  @GetMapping( "/getExpert/{id}" )
		public Expert getExpert(@PathVariable("id") long id) throws IOException {

		  Expert expert = expertRepository.findById(id).get();
		  if(expert.getImage()== null) {
			  return expert;
		  }
		  else {
			    expert.setImage(expertService.decompressZLib(expert.getImage()));	
			    return expert;
		  }
		
		
		}
	 // @GetMapping ("/getByUsername")
	  @GetMapping( "/getImage/{id}" )
		public Expert getImage(@PathVariable("id") long id) throws IOException {

			Expert expert = expertRepository.findById(id).get();
			  if(expert.getImage()== null) {
				  return expert;
			  }
			  else {
				    expert.setImage(expertService.decompressZLib(expert.getImage()));	
				    return expert;
			  }
		
		}
	
	  @DeleteMapping("/{id}")
	  public void deleteExpert(@PathVariable("id") long id )
	  {
		  expertRepository.deleteById(id);
	  }
		@GetMapping("/all/{role}")
		public List<Expert> getAll(@PathVariable ("role") String role){
			return expertRepository.findByRole(role);
		}

		
		@GetMapping("/patientsExpert/{idExpert}")
     public List<Patient> getAllPatientsDr(@PathVariable("idExpert") long idExpert){
		
		   	Expert expert = expertRepository.findById(idExpert).get();	
	    	List<Consultation> liste = repository.findAll();
	    	List<Patient> res= new ArrayList<>() ; 
	    	
	    	for(Consultation consult :liste) {
	    		System.out.println(expert);
	    		if((consult.getAutoDetection().getAvisExpert().getExpert().getId())==(expert.getId())) {
	    			res.add(consult.getPatient());
	    			return res ; 
	    		}
	    	}
	    
	    	return res ; 
	    	
		}
		
@GetMapping("/demandes")
public List<Consultation> getAllDemandes(){
	    	List<Consultation> liste = repository.findAll();
	    	List<Consultation> resultat= new ArrayList<>() ; 
	    	for(Consultation consult :liste) {
	    		if((consult.getDemandeAvisD()==1 || consult.getDemandeAvisG()==1)){
	    			if((consult.getAutoDetection().getAvisExpert()==null)) {
	    				resultat.add(consult);
	    				
	    			
	    			}
	    		}
	    	}
	    	return resultat ; 
}

@GetMapping("test")
public List<Consultation> getAllDemandesss (){
	List<Consultation> liste = repository.findAll();
	List<Consultation> resultat= new ArrayList<>() ; 
	for(Consultation consult :liste) {
		if((consult.getAutoDetection().getAvisExpert()==null)) {
			if((consult.getDemandeAvisD()==1 && consult.getDemandeAvisG()==0)) {
			     resultat.add(consult);}
			if((consult.getDemandeAvisD()==0 && consult.getDemandeAvisG()==1)) {
			     resultat.add(consult);}
		  
			if((consult.getDemandeAvisD()==1 || consult.getDemandeAvisG()==1)) {
				if(resultat.contains(consult)) {
					 resultat.add(consult);
			    }
				}
		
	}
	
	}
	return resultat ; 
}
 }

